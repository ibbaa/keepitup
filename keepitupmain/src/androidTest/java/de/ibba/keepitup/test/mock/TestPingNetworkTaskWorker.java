package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import java.util.concurrent.Callable;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.PingCommandResult;
import de.ibba.keepitup.service.PingNetworkTaskWorker;

public class TestPingNetworkTaskWorker extends PingNetworkTaskWorker {

    private MockPingCommandExecutionCallable mockPingCommandExecutionCallable;

    public TestPingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    public void setMockPingCommandExecutionCallable(MockPingCommandExecutionCallable mockPingCommandExecutionCallable) {
        this.mockPingCommandExecutionCallable = mockPingCommandExecutionCallable;
    }

    @Override
    protected Callable<PingCommandResult> getPingCommandExecutionCallable(NetworkTask networkTask) {
        return mockPingCommandExecutionCallable;
    }
}
