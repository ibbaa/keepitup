package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import java.util.concurrent.Callable;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.PingNetworkTaskWorker;
import de.ibba.keepitup.service.network.PingCommandResult;

public class TestPingNetworkTaskWorker extends PingNetworkTaskWorker {

    private MockPingCommand mockPingCommandExecutionCallable;

    public TestPingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    public void setMockPingCommandExecutionCallable(MockPingCommand mockPingCommandExecutionCallable) {
        this.mockPingCommandExecutionCallable = mockPingCommandExecutionCallable;
    }

    @Override
    protected Callable<PingCommandResult> getPingCommand(NetworkTask networkTask) {
        return mockPingCommandExecutionCallable;
    }
}
