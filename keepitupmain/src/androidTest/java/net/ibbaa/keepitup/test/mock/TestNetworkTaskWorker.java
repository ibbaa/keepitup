package net.ibbaa.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.Callable;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.NetworkTaskWorker;
import net.ibbaa.keepitup.service.network.DNSLookupResult;

public class TestNetworkTaskWorker extends NetworkTaskWorker {

    private MockDNSLookup mockDNSLookup;
    private final boolean success;
    private final int maxInstances;
    private int instancesOnExecute;
    private final boolean interrupted;

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success) {
        this(context, networkTask, wakeLock, success, 10);
    }

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success, int maxInstances) {
        this(context, networkTask, wakeLock, success, maxInstances, false);
    }

    public TestNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock, boolean success, int maxInstances, boolean interrupted) {
        super(context, networkTask, wakeLock);
        ((MockNetworkManager) getNetworkManager()).setConnected(true);
        ((MockNetworkManager) getNetworkManager()).setConnectedWithWiFi(true);
        this.success = success;
        this.maxInstances = maxInstances;
        this.instancesOnExecute = -1;
        this.interrupted = interrupted;
    }

    @Override
    public int getMaxInstances() {
        return maxInstances;
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return "TestMaxInstancesError " + activeInstances;
    }

    public int getInstancesOnExecute() {
        return instancesOnExecute;
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask) {
        Log.d(TestNetworkTaskWorker.class.getName(), "Executing TestNetworkTaskWorker for " + networkTask);
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        instancesOnExecute = networkTaskDAO.readNetworkTaskInstances(networkTask.getId());
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setSuccess(success);
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        logEntry.setMessage(success ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful));
        return new ExecutionResult(interrupted, logEntry);
    }

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }
}
