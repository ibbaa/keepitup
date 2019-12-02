package de.ibba.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.util.HTTPUtil;

public class DownloadCommand implements Callable<DownloadCommandResult> {

    private final Context context;
    private final NetworkTask networkTask;
    private final URL url;
    private final String folder;
    private final boolean delete;
    private boolean valid;
    private int notRunningCount;

    public DownloadCommand(Context context, NetworkTask networkTask, URL url, String folder, boolean delete) {
        this.context = context;
        this.networkTask = networkTask;
        this.url = url;
        this.folder = folder;
        this.delete = delete;
    }

    @Override
    public DownloadCommandResult call() {
        Log.d(DownloadCommand.class.getName(), "call");
        URLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        boolean connectSuccess = false;
        boolean downloadSuccess = false;
        boolean deleteSuccess = false;
        int httpCode = Integer.MAX_VALUE;
        String httpMessage = null;
        String fileName = null;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        initializeValid();
        try {
            Log.d(DownloadCommand.class.getName(), "Establishing connection to " + url);
            connection = openConnection();
            if (connection == null) {
                Log.d(DownloadCommand.class.getName(), "Error establishing connection to " + url);
                return createDownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, null);
            }
            connectSuccess = true;
            if (HTTPUtil.isHTTPConnection(connection)) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpCode = httpConnection.getResponseCode();
                httpMessage = httpConnection.getResponseMessage();
                if (httpCode != HttpURLConnection.HTTP_OK) {
                    Log.d(DownloadCommand.class.getName(), "Connection successful but HTTP return code " + httpCode + " is not HTTP_OK");
                    return createDownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, null);
                }
            }
            fileName = getFileName(connection);
            if (fileName == null) {
                Log.d(DownloadCommand.class.getName(), "Connection successful but download file name could not be determined");
                return createDownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, null);
            }
            int pollInterval = getResources().getInteger(R.integer.download_valid_poll_interval);
            Log.d(DownloadCommand.class.getName(), "Scheduling verify valid polling thread with an interval of " + pollInterval);
            executorService.scheduleWithFixedDelay(this::verifyValid, 0, pollInterval, TimeUnit.SECONDS);
            return createDownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, null);
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error executing download command", exc);
            return createDownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, exc);
        } finally {
            closeResources(connection, inputStream, outputStream, executorService);
        }
    }

    private URLConnection openConnection() throws IOException {
        Log.d(DownloadCommand.class.getName(), "openConnection");
        if (url == null) {
            Log.e(DownloadCommand.class.getName(), "URL is null");
            return null;
        }
        URLConnection connection = url.openConnection();
        if (connection == null) {
            Log.e(DownloadCommand.class.getName(), "Connection is null");
            return null;
        }
        connection.setConnectTimeout(getResources().getInteger(R.integer.download_connect_timeout));
        connection.setReadTimeout(getResources().getInteger(R.integer.download_read_timeout));
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.connect();
        return connection;
    }

    private String getFileName(URLConnection connection) {
        Log.d(DownloadCommand.class.getName(), "getFileName");
        String contentDisposition = HTTPUtil.getContentDisposition(getContext(), connection);
        String contentType = connection.getContentType();
        Log.d(DownloadCommand.class.getName(), "Content-Disposition header is " + contentDisposition);
        Log.d(DownloadCommand.class.getName(), "Content-Type header is " + contentType);
        String contentDispositionFileName = HTTPUtil.getFileNameFromContentDisposition(contentDisposition);
        String mimeType = HTTPUtil.getMimeTypeFromContentType(contentType);
        Log.d(DownloadCommand.class.getName(), "Parsed file name from content disposition is " + contentDispositionFileName);
        Log.d(DownloadCommand.class.getName(), "Parsed mime type from content type is " + mimeType);
        IFileManager fileManager = getFileManager();
        String fileName = fileManager.getDownloadFileName(url, contentDispositionFileName, mimeType);
        Log.d(DownloadCommand.class.getName(), "Download file name is " + fileName);
        String validFileName = fileManager.getValidFileName(folder, fileName);
        Log.d(DownloadCommand.class.getName(), "Adjusted valid file name is " + validFileName);
        return validFileName;
    }

    private synchronized DownloadCommandResult createDownloadCommandResult(boolean connectSuccess, boolean downloadSuccess, boolean deleteSuccess, int httpCode, String httpMessage, String fileName, Exception exc) {
        return new DownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, valid, notRunningCount >= 2, httpCode, httpMessage, fileName, exc);
    }

    private synchronized void initializeValid() {
        valid = true;
        notRunningCount = 0;
    }

    private synchronized void verifyValid() {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
        if (databaseTask == null || networkTask.getSchedulerId() != databaseTask.getSchedulerId()) {
            valid = false;
            notRunningCount = 0;
            return;
        }
        if (databaseTask.isRunning()) {
            valid = true;
            notRunningCount = 0;
            return;
        }
        valid = true;
        notRunningCount++;
        if (notRunningCount >= 2) {
            valid = false;
        }
    }

    private void closeResources(URLConnection connection, InputStream inputStream, FileOutputStream outputStream, ScheduledExecutorService executorService) {
        Log.d(DownloadCommand.class.getName(), "closeResources");
        try {
            if (outputStream != null) {
                Log.d(DownloadCommand.class.getName(), "Closing output stream");
                outputStream.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing output stream", exc);
        }
        try {
            if (inputStream != null) {
                Log.d(DownloadCommand.class.getName(), "Closing input stream");
                inputStream.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing input stream", exc);
        }
        try {
            if (HTTPUtil.isHTTPConnection(connection)) {
                Log.d(DownloadCommand.class.getName(), "Disconnecting http connection");
                ((HttpURLConnection) connection).disconnect();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing http connection", exc);
        }
        if (executorService != null) {
            Log.d(DownloadCommand.class.getName(), "Shutting down ScheduledExecutorService for polling thread");
            executorService.shutdownNow();
        }
    }

    private IFileManager getFileManager() {
        return new SystemFileManager(getContext());
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
