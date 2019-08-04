package de.ibba.keepitup.service;

import android.content.Context;
import android.content.res.Resources;
import android.os.PowerManager;
import android.util.Log;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public abstract class NetworkTaskWorker implements Runnable {

    private final Context context;
    private final NetworkTask networkTask;
    private final PowerManager.WakeLock wakeLock;

    public NetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        this.context = context;
        this.networkTask = networkTask;
        this.wakeLock = wakeLock;
    }

    public abstract LogEntry execute(NetworkTask networkTask);

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        try {
            LogEntry logEntry = execute(networkTask);
            Log.d(NetworkTaskWorker.class.getName(), "Writing log entry to database " + logEntry);
            LogDAO logDAO = new LogDAO(getContext());
            logDAO.insertAndDeleteLog(logEntry);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                Log.d(NetworkTaskWorker.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return getContext().getResources();
    }
}
