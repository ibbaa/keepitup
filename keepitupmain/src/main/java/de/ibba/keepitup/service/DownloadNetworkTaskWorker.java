package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public class DownloadNetworkTaskWorker extends NetworkTaskWorker {

    public DownloadNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(DownloadNetworkTaskWorker.class.getName(), "Executing DownloadNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(true);
        logEntry.setTimestamp(System.currentTimeMillis());
        logEntry.setMessage(getResources().getString(R.string.string_successful));
        return logEntry;
    }
}