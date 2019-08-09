package de.ibba.keepitup.test.mock;

import android.content.Context;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.PingCommandExecutionCallable;
import de.ibba.keepitup.service.PingCommandResult;

public class MockPingCommandExecutionCallable extends PingCommandExecutionCallable {

    private final PingCommandResult pingCommandResult;

    public MockPingCommandExecutionCallable(Context context, NetworkTask networkTask, PingCommandResult pingCommandResult) {
        super(context, networkTask);
        this.pingCommandResult = pingCommandResult;
    }

    @Override
    public PingCommandResult call() {
        return pingCommandResult;
    }
}
