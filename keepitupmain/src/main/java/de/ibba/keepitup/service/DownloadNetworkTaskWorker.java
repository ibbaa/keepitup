package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
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
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Executing DownloadNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        InetAddress address = executeDNSLookup(networkTask.getAddress(), logEntry, getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (address != null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
            boolean ip6 = address instanceof Inet6Address;
            if (ip6) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
            } else {
                Log.d(DownloadNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
            }
            executeDownloadCommand(address.getHostAddress(), networkTask, logEntry);
        } else {
            Log.e(DownloadNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
        }
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Returning " + logEntry);
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        return logEntry;
    }

    private void executeDownloadCommand(String address, NetworkTask networkTask, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "executeDownloadCommand, address is " + address + ", networkTask is " + networkTask);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        IFileManager fileManager = getFileManager();
        String baseURL = networkTask.getAddress();
        URL url = determineURL(baseURL, address);
        if (url == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determined url is null");
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_download_url_error, baseURL));
            return;
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "URL is " + url.toExternalForm());
        }
        File folder = determineDownloadFolder();
        if (folder == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Determined folder is null");
            String folderMessage = preferenceManager.getPreferenceDownloadExternalStorage() ? preferenceManager.getPreferenceDownloadFolder() : fileManager.getDefaultDownloadDirectoryName();
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_download_folder_error, folderMessage));
            return;
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Download folder is " + folder.getAbsolutePath());
        }
        boolean delete = determineDeleteDownloadedFile();
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Delete downloaded file: " + delete);
        Callable<DownloadCommandResult> downloadCommand = getDownloadCommand(networkTask, url, folder, delete);
        int timeout = getResources().getInteger(R.integer.download_timeout);
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<DownloadCommandResult> downloadResultFuture = executorService.submit(downloadCommand);
            DownloadCommandResult downloadResult = downloadResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(PingNetworkTaskWorker.class.getName(), downloadCommand.getClass().getSimpleName() + " returned " + downloadResult);
            if (!downloadResult.isConnectSuccess()) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Connection failed. Preparing error message.");
                prepareConnectError(downloadResult, url, timeout, logEntry);
                return;
            }
            if (!HTTPUtil.isHTTPReturnCodeOk(downloadResult.getHttpResponseCode())) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "HTTP return code is not HTTP_OK. Preparing error message.");
                prepareHTTPReturnCodeError(downloadResult, url, timeout, logEntry);
                return;
            }
            if (downloadResult.isDownloadSuccess()) {
                if (!downloadResult.fileExists()) {
                    Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful but the downloaded file does not exist. Preparing error message.");
                    prepareUnknownError(downloadResult, url, timeout, logEntry);
                    return;
                }
                Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful. Preparing message.");
                prepareSuccess(downloadResult, url, timeout, folder, delete, logEntry);
                return;
            }
        } catch (Throwable exc) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Error executing " + downloadCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_download_error, url.toExternalForm()), exc, timeout));
        } finally {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
    }

    private void prepareConnectError(DownloadCommandResult downloadResult, URL url, int timeout, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareConnectErrorMessage");
        String connectMessage = getResources().getString(R.string.text_download_connect_error, URLUtil.getHostAndPort(url));
        prepareError(downloadResult, url, timeout, logEntry, connectMessage);
    }

    private void prepareHTTPReturnCodeError(DownloadCommandResult downloadResult, URL url, int timeout, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareHTTPReturnCodeErrorMessage");
        String downloadError = getResources().getString(R.string.text_download_error, url.toExternalForm());
        String httpMessage = getResources().getString(R.string.text_download_http_error, downloadResult.getHttpResponseCode(), downloadResult.getHttpResponseMessage());
        String message = downloadError + " " + httpMessage;
        prepareError(downloadResult, url, timeout, logEntry, message);
    }

    private void prepareUnknownError(DownloadCommandResult downloadResult, URL url, int timeout, LogEntry logEntry) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareUnknownErrorMessage");
        logEntry.setSuccess(false);
        Throwable exc = downloadResult.getException();
        String message;
        if (exc == null) {
            message = getResources().getString(R.string.text_download_unknown_error, url.toExternalForm());
        } else {
            message = getResources().getString(R.string.text_download_error, url.toExternalForm());
        }
        prepareError(downloadResult, url, timeout, logEntry, message);
    }

    private void prepareError(DownloadCommandResult downloadResult, URL url, int timeout, LogEntry logEntry, String message) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "prepareError");
        logEntry.setSuccess(false);
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
        if (!delete) {
            String fileMessage = getResources().getString(R.string.text_download_file, new File(folder, downloadResult.getFileName()).getAbsolutePath());
            logEntry.setMessage(successMessage + " " + fileMessage);
            return;
        }
        if (downloadResult.isDeleteSuccess()) {
            String deleteMessage = getResources().getString(R.string.text_download_delete);
            logEntry.setMessage(successMessage + " " + deleteMessage);
        } else {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "The download was successful but the file could not be deleted.");
            String deleteErrorMessage = getResources().getString(R.string.text_download_delete_error);
            String message = successMessage + " " + deleteErrorMessage;
            Throwable exc = downloadResult.getException();
            if (exc == null) {
                logEntry.setMessage(message);
            } else {
                logEntry.setMessage(getMessageFromException(message, exc, timeout));
            }
        }
    }

    private URL determineURL(String baseURL, String address) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "determineURL, baseURL is " + baseURL + ", address is " + address);
        URL url = URLUtil.getURL(baseURL, address);
        Log.d(DownloadNetworkTaskWorker.class.getName(), "URL with address modification is " + url);
        if (url == null) {
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Error creating valid url from the specified url " + baseURL + " and the address " + address);
            Log.d(DownloadNetworkTaskWorker.class.getName(), "Trying specified url without address modification.");
            url = URLUtil.getURL(baseURL, null);
            Log.d(DownloadNetworkTaskWorker.class.getName(), "URL without address modification is " + url);
            if (url == null) {
                Log.d(DownloadNetworkTaskWorker.class.getName(), "Error creating valid url from the specified url " + baseURL);
                return null;
            }
        }
        return url;
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
