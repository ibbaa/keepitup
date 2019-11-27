package de.ibba.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;

public class DownloadCommand implements Callable<DownloadCommandResult> {

    private final Context context;
    private final NetworkTask networkTask;
    private final String baseURL;
    private final InetAddress address;
    private final String folder;
    private final boolean delete;
    private ScheduledExecutorService executorService;
    private AtomicBoolean valid;

    public DownloadCommand(Context context, NetworkTask networkTask, String baseURL, InetAddress address, String folder, boolean delete) {
        this.context = context;
        this.networkTask = networkTask;
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
        executorService = Executors.newScheduledThreadPool(1);
        valid.set(true);
        try {
            int pollInterval = getResources().getInteger(R.integer.download_valid_poll_interval);
            Log.d(DownloadCommand.class.getName(), "Scheduling verify valid polling thread with an interval of " + pollInterval);
            executorService.scheduleWithFixedDelay(this::verifyValid, 0, pollInterval, TimeUnit.SECONDS);
            return new DownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, null);
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error executing download command", exc);
            return new DownloadCommandResult(connectSuccess, downloadSuccess, deleteSuccess, httpCode, httpMessage, fileName, exc);
        } finally {
            closeResources(httpConnection, inputStream, outputStream);
        }
    }

    private URL getAddressURL() {
        return null;
    }

    private void verifyValid() {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
        if (databaseTask == null || !databaseTask.isRunning()) {
            valid.set(false);
            return;
        }
        if (networkTask.getSchedulerId() != databaseTask.getSchedulerId()) {
            valid.set(false);
        }
    }

    private void closeResources(HttpURLConnection httpConnection, InputStream inputStream, FileOutputStream outputStream) {
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
            if (httpConnection != null) {
                Log.d(DownloadCommand.class.getName(), "Disconnecting http connection");
                httpConnection.disconnect();
            }
        } catch (Exception exc) {
            Log.e(DownloadCommand.class.getName(), "Error closing http connection", exc);
        }
        if (executorService != null) {
            Log.d(DownloadCommand.class.getName(), "Shutting down ScheduledExecutorService for polling thread");
            executorService.shutdownNow();
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
