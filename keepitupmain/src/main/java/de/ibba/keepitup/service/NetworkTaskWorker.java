package de.ibba.keepitup.service;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.ui.sync.LogEntryUIBroadcastReceiver;
import de.ibba.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;

public abstract class NetworkTaskWorker implements Runnable {

    private final Context context;
    private final NetworkTask networkTask;
    private final PowerManager.WakeLock wakeLock;
    private final INetworkManager networkManager;

    public NetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        this.context = context;
        this.networkTask = networkTask;
        this.wakeLock = wakeLock;
        this.networkManager = createNetworkManager();
    }

    public abstract LogEntry execute(NetworkTask networkTask);

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        try {
            LogEntry logEntry = checkExecution();
            if (logEntry == null) {
                logEntry = execute(networkTask);
            }
            if (isNetworkTaskValid(networkTask)) {
                Log.d(NetworkTaskWorker.class.getName(), "Writing log entry to database " + logEntry);
                LogDAO logDAO = new LogDAO(getContext());
                logDAO.insertAndDeleteLog(logEntry);
                Log.d(NetworkTaskWorker.class.getName(), "Notify UI");
                sendUINotificationBroadcast();
            } else {
                Log.d(NetworkTaskWorker.class.getName(), "Network task does no longer exist. Not writing log.");
            }
        } catch (Exception exc) {
            Log.d(NetworkTaskWorker.class.getName(), "Fatal errror while executing worker and writing log", exc);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                Log.d(NetworkTaskWorker.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
        }
    }

    private void sendUINotificationBroadcast() {
        Log.d(NetworkTaskWorker.class.getName(), "sendUINotificationBroadcast");
        Intent mainUIintent = new Intent(NetworkTaskMainUIBroadcastReceiver.class.getName());
        mainUIintent.putExtras(networkTask.toBundle());
        getContext().sendBroadcast(mainUIintent);
        Intent logUIintent = new Intent(LogEntryUIBroadcastReceiver.class.getName());
        logUIintent.putExtras(networkTask.toBundle());
        getContext().sendBroadcast(logUIintent);
    }

    private LogEntry checkExecution() {
        Log.d(NetworkTaskWorker.class.getName(), "checkExecution");
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(System.currentTimeMillis());
        if (!networkManager.isConnected()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active network connection.");
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_no_network_connection));
            return logEntry;
        }
        if (!networkManager.isConnectedWithWiFi() && networkTask.isOnlyWifi()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active wifi connection.");
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_no_wifi_connection));
            return logEntry;
        }
        Log.d(NetworkTaskWorker.class.getName(), "Everything is ok with the network.");
        return null;
    }

    private INetworkManager createNetworkManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNetworkManager(getContext());
    }

    private boolean isNetworkTaskValid(NetworkTask task) {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(task.getId());
        return databaseTask != null && task.getSchedulerId() == databaseTask.getSchedulerId();
    }

    public INetworkManager getNetworkManager() {
        return networkManager;
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return getContext().getResources();
    }
}
