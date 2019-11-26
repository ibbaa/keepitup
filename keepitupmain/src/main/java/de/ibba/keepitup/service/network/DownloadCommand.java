package de.ibba.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.util.concurrent.Callable;

public class DownloadCommand implements Callable<DownloadCommandResult> {

    private final Context context;
    private final String baseURL;
    private final InetAddress address;
    private final String folder;
    private final boolean delete;

    public DownloadCommand(Context context, String baseURL, InetAddress address, String folder, boolean delete) {
        this.context = context;
        this.baseURL = baseURL;
        this.address = address;
        this.folder = folder;
        this.delete = delete;
    }

    @Override
    public DownloadCommandResult call() {
        Log.d(DownloadCommand.class.getName(), "call");
        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        boolean connectSuccess = false;
        boolean downloadSuccess = false;
        boolean deleteSuccess = false;
        int httpCode = Integer.MAX_VALUE;
        String httpMessage = null;
        String fileName = null;
        try {
            return new DownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, null);
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error executing download command", exc);
            return new DownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, exc);
        } finally {
            closeResources(httpConnection, inputStream, outputStream);
        }
    }

    private void closeResources(HttpURLConnection httpConnection, InputStream inputStream, FileOutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing output stream", exc);
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing input stream", exc);
        }
        try {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing http connection", exc);
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
