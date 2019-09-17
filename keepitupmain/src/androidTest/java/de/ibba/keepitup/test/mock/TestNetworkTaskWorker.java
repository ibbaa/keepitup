package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.Callable;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkTaskWorker;
import de.ibba.keepitup.service.network.DNSLookupResult;

public class TestNetworkTaskWorker extends NetworkTaskWorker {

    private final boolean success;
    private MockDNSLookup mockDNSLookup;

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success) {
        super(context, networkTask, wakeLock);
        ((MockNetworkManager) getNetworkManager()).setConnected(true);
        ((MockNetworkManager) getNetworkManager()).setConnectedWithWiFi(true);
        this.success = success;
    }

    @Override
    public int getMaxInstances() {
        return 10;
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return "TestMaxInstancesError";
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

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }
}
