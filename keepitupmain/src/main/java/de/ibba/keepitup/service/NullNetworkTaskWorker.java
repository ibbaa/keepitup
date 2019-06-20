package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;

public class NullNetworkTaskWorker extends NetworkTaskWorker {

    public NullNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(NullNetworkTaskWorker.class.getName(), "Executing NullNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(false);
        logEntry.setTimestamp(System.currentTimeMillis());
        logEntry.setMessage(getResources().getString(R.string.text_access_type_null));
        return logEntry;
    }
}
