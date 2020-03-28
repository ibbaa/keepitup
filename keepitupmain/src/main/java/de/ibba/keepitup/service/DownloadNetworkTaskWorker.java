package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.network.DownloadCommand;
import de.ibba.keepitup.service.network.DownloadCommandResult;
import de.ibba.keepitup.util.HTTPUtil;
import de.ibba.keepitup.util.StringUtil;
import de.ibba.keepitup.util.URLUtil;

public class DownloadNetworkTaskWorker extends NetworkTaskWorker {

    public DownloadNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public int getMaxInstances() {
        return getResources().getInteger(R.integer.download_worker_max_instances);
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return getResources().getString(R.string.text_download_worker_max_instances_error, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Executing DownloadNetworkTaskWorker for " + networkTask);
        ExecutionResult downloadExecutionResult = executeDownloadCommand(networkTask);
        LogEntry logEntry = downloadExecutionResult.getLogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Returning " + downloadExecutionResult);
        return downloadExecutionResult;
    }

    private ExecutionResult executeDownloadCommand(NetworkTask networkTask) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "executeDownloadCommand, networkTask is " + networkTask);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        IFileManager fileManager = getFileManager();
        String baseURL = networkTask.getAddress();
        URL url = determineURL(baseURL);
        LogEntry logEntry = new LogEntry();
        boolean interrupted = false;
        if (url == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determined url is null");
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_download_url_error, baseURL));
            return new ExecutionResult(interrupted, logEntry);
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "URL is " + url.toExternalForm());
        }
        File folder = determineDownloadFolder();
        if (folder == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determined folder is null");
            String folderMessage = preferenceManager.getPreferenceDownloadExternalStorage() ? preferenceManager.getPreferenceDownloadFolder() : fileManager.getDefaultDownloadDirectoryName();
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_download_folder_error, folderMessage));
            return new ExecutionResult(interrupted, logEntry);
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Download folder is " + folder.getAbsolutePath());
        }
        boolean delete = determineDeleteDownloadedFile();
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Delete downloaded file: " + delete);
        Callable<DownloadCommandResult> downloadCommand = getDownloadCommand(networkTask, url, folder, delete);
        int timeout = getResources().getInteger(R.integer.download_timeout);
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DownloadCommandResult> downloadResultFuture = null;
        try {
            downloadResultFuture = executorService.submit(downloadCommand);
            DownloadCommandResult downloadResult = downloadResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(DownloadNetworkTaskWorker.class.getName(), downloadCommand.getClass().getSimpleName() + " returned " + downloadResult);
            if (!downloadResult.isConnectSuccess()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Connection failed. Preparing error message.");
                prepareConnectError(downloadResult, url, timeout, folder, delete, logEntry);
                return new ExecutionResult(interrupted, logEntry);
            }
            if (downloadResult.getHttpResponseCode() >= 0 && !HTTPUtil.isHTTPReturnCodeOk(downloadResult.getHttpResponseCode())) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "HTTP download and HTTP return code is not HTTP_OK. Preparing error message.");
                prepareHTTPReturnCodeError(downloadResult, url, timeout, folder, delete, logEntry);
                return new ExecutionResult(interrupted, logEntry);
            }
            if (downloadResult.isDownloadSuccess()) {
                if (!downloadResult.fileExists()) {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful but the downloaded file does not exist. Preparing error message.");
                    prepareUnknownError(downloadResult, url, timeout, folder, delete, logEntry);
                    return new ExecutionResult(interrupted, logEntry);
                }
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful. Preparing message.");
                prepareSuccess(downloadResult, url, timeout, folder, delete, logEntry);
                return new ExecutionResult(interrupted, logEntry);
            }
            if (downloadResult.isStopped()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was stopped. Preparing error message.");
                prepareStoppedError(downloadResult, url, timeout, folder, delete, logEntry);
                return new ExecutionResult(interrupted, logEntry);
            }
            if (!downloadResult.isValid()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The network task is invalid. Preparing error message.");
                prepareInvalidError(downloadResult, url, timeout, folder, delete, logEntry);
                return new ExecutionResult(interrupted, logEntry);
            }
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The download failed for an unknown reason.");
            prepareUnknownError(downloadResult, url, timeout, folder, delete, logEntry);
        } catch (Throwable exc) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Error executing " + downloadCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_download_error, url.toExternalForm()), exc, timeout));
            if (downloadResultFuture != null && isInterrupted(exc)) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Cancelling " + downloadCommand.getClass().getSimpleName());
                downloadResultFuture.cancel(true);
            }
        } finally {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        return new ExecutionResult(interrupted, logEntry);
    }

    private void prepareConnectError(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareConnectErrorMessage");
        String connectMessage = getResources().getString(R.string.text_download_connect_error, URLUtil.getHostAndPort(url));
        prepareError(downloadResult, url, timeout, folder, delete, logEntry, connectMessage);
    }

    private void prepareHTTPReturnCodeError(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareHTTPReturnCodeErrorMessage");
        String downloadError = getResources().getString(R.string.text_download_error, url.toExternalForm());
        String httpMessage = getResources().getString(R.string.text_download_http_error, downloadResult.getHttpResponseCode(), downloadResult.getHttpResponseMessage());
        String message = downloadError + " " + httpMessage;
        prepareError(downloadResult, url, timeout, folder, delete, logEntry, message);
    }

    private void prepareStoppedError(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareStoppedError");
        String stoppedMessage = getResources().getString(R.string.text_download_stopped_error);
        prepareError(downloadResult, url, timeout, folder, delete, logEntry, stoppedMessage);
    }

    private void prepareInvalidError(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareInvalidError");
        String invalidMessage = getResources().getString(R.string.text_download_invalid_error);
        prepareError(downloadResult, url, timeout, folder, delete, logEntry, invalidMessage);
    }

    private void prepareUnknownError(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareUnknownErrorMessage");
        logEntry.setSuccess(false);
        Throwable exc = downloadResult.getException();
        String message;
        if (exc == null) {
            message = getResources().getString(R.string.text_download_unknown_error, url.toExternalForm());
        } else {
            message = getResources().getString(R.string.text_download_error, url.toExternalForm());
        }
        prepareError(downloadResult, url, timeout, folder, delete, logEntry, message);
    }

    private void prepareError(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry, String message) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareError");
        logEntry.setSuccess(false);
        if (downloadResult.fileExists()) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The file was partially downloaded.");
            message += " " + getResources().getString(R.string.text_download_partial);
            if (!delete) {
                message += " " + getResources().getString(R.string.text_download_file, new File(folder, downloadResult.getFileName()).getAbsolutePath());
            } else {
                if (downloadResult.isDeleteSuccess()) {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The partially downloaded file was deleted.");
                    message += " " + getResources().getString(R.string.text_download_partial_delete);
                } else {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The deletion of the partially downloaded file failed.");
                    message += " " + getResources().getString(R.string.text_download_partial_delete_error);
                }
            }
            String durationMessage = getResources().getString(R.string.text_download_time, StringUtil.formatTimeRange(downloadResult.getDuration(), getContext()));
            message += " " + durationMessage;
        }
        Throwable exc = downloadResult.getException();
        if (exc == null) {
            logEntry.setMessage(message);
        } else {
            logEntry.setMessage(getMessageFromException(message, exc, timeout));
        }
    }

    private void prepareSuccess(DownloadCommandResult downloadResult, URL url, int timeout, File folder, boolean delete, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareSuccess");
        logEntry.setSuccess(true);
        String successMessage = getResources().getString(R.string.text_download_success, url.toExternalForm());
        String durationMessage = getResources().getString(R.string.text_download_time, StringUtil.formatTimeRange(downloadResult.getDuration(), getContext()));
        if (!delete) {
            String fileMessage = getResources().getString(R.string.text_download_file, new File(folder, downloadResult.getFileName()).getAbsolutePath());
            logEntry.setMessage(successMessage + " " + fileMessage + " " + durationMessage);
            return;
        }
        if (downloadResult.isDeleteSuccess()) {
            String deleteMessage = getResources().getString(R.string.text_download_delete);
            logEntry.setMessage(successMessage + " " + deleteMessage + " " + durationMessage);
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful but the file could not be deleted.");
            String deleteErrorMessage = getResources().getString(R.string.text_download_delete_error);
            String message = successMessage + " " + deleteErrorMessage + " " + durationMessage;
            Throwable exc = downloadResult.getException();
            if (exc == null) {
                logEntry.setMessage(message);
            } else {
                logEntry.setMessage(getMessageFromException(message, exc, timeout));
            }
        }
    }

    private URL determineURL(String baseURL) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineURL, baseURL is " + baseURL);
        return URLUtil.getURL(baseURL, null);
    }

    private File determineDownloadFolder() {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineDownloadFolder");
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        IFileManager fileManager = getFileManager();
        if (preferenceManager.getPreferenceDownloadExternalStorage()) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determining external download folder");
            String preferenceDownloadFolder = preferenceManager.getPreferenceDownloadFolder();
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Specified folder is " + preferenceDownloadFolder);
            return fileManager.getExternalDirectory(preferenceDownloadFolder);
        }
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Determining internal download folder");
        return fileManager.getInternalDownloadDirectory();
    }

    private boolean determineDeleteDownloadedFile() {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineDeleteDownloadedFile");
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (preferenceManager.getPreferenceDownloadExternalStorage()) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Downloading to external folder. Using keep downloaded files switch.");
            return !preferenceManager.getPreferenceDownloadKeep();
        }
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Downloading to internal folder. Always delete.");
        return true;
    }

    protected IFileManager getFileManager() {
        return new SystemFileManager(getContext());
    }

    protected Callable<DownloadCommandResult> getDownloadCommand(NetworkTask networkTask, URL url, File folder, boolean delete) {
        return new DownloadCommand(getContext(), networkTask, url, folder, delete);
    }
}
