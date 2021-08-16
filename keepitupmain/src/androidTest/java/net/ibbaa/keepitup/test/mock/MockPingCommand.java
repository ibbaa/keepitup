package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import net.ibbaa.keepitup.service.network.PingCommand;
import net.ibbaa.keepitup.service.network.PingCommandResult;

public class MockPingCommand extends PingCommand {

    private final PingCommandResult pingCommandResult;

    public MockPingCommand(Context context, String address, int pingCount, boolean ip6, PingCommandResult pingCommandResult) {
        super(context, address, pingCount, ip6);
        this.pingCommandResult = pingCommandResult;
    }

    @Override
    public PingCommandResult call() {
        return pingCommandResult;
    }
}
