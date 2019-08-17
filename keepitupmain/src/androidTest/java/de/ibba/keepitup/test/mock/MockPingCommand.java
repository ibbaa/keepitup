package de.ibba.keepitup.test.mock;

import android.content.Context;

import de.ibba.keepitup.service.network.PingCommand;
import de.ibba.keepitup.service.network.PingCommandResult;

public class MockPingCommand extends PingCommand {

    private final PingCommandResult pingCommandResult;

    public MockPingCommand(Context context, String address, boolean ip6, PingCommandResult pingCommandResult) {
        super(context, address, ip6);
        this.pingCommandResult = pingCommandResult;
    }

    @Override
    public PingCommandResult call() {
        return pingCommandResult;
    }
}