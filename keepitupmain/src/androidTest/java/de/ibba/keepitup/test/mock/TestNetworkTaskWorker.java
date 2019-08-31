package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkTaskWorker;

public class TestNetworkTaskWorker extends NetworkTaskWorker {

    private final boolean success;

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success) {
        super(context, networkTask, wakeLock);
        ((MockNetworkManager) getNetworkManager()).setConnected(true);
        ((MockNetworkManager) getNetworkManager()).setConnectedWithWiFi(true);
        this.success = success;
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(TestNetworkTaskWorker.class.getName(), "Executing TestNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(success);
        logEntry.setTimestamp(System.currentTimeMillis());
        logEntry.setMessage(success ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful));
        return logEntry;
    }
}
