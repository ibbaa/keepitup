package de.ibba.keepitup.service;

import android.content.Context;
import android.content.res.Resources;
import android.os.PowerManager;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskWorker implements Runnable {

    private final Context context;
    private final NetworkTask networkTask;
    private final PowerManager.WakeLock wakeLock;

    public NetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        this.context = context;
        this.networkTask = networkTask;
        this.wakeLock = wakeLock;
    }

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        LogDAO logDAO = new LogDAO(context);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(true);
        logEntry.setTimestamp(System.currentTimeMillis());
        logEntry.setMessage(getResources().getString(R.string.string_successful));
        Log.d(NetworkTaskWorker.class.getName(), "Writing log entry to database " + logEntry);
        logDAO.insertAndDeleteLog(logEntry);
        if (wakeLock != null) {
            Log.d(NetworkTaskWorker.class.getName(), "Releasing partial wake lock");
            wakeLock.release();
        } else {
            Log.e(NetworkTaskWorker.class.getName(), "Wake lock is null");
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
