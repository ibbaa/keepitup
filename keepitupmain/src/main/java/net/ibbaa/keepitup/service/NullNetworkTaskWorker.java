package net.ibbaa.keepitup.service;

import android.content.Context;
import android.os.PowerManager;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;

public class NullNetworkTaskWorker extends NetworkTaskWorker {

    public NullNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public int getMaxInstances() {
        return getResources().getInteger(R.integer.null_worker_max_instances);
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return getResources().getString(R.string.text_null_worker_max_instances_error, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask) {
        Log.d(NullNetworkTaskWorker.class.getName(), "Executing NullNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(false);
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        logEntry.setMessage(getResources().getString(R.string.text_access_type_null));
        return new ExecutionResult(false, logEntry);
    }
}
