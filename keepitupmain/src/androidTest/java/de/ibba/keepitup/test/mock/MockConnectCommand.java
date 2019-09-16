package de.ibba.keepitup.test.mock;

import android.content.Context;

import java.net.InetAddress;

import de.ibba.keepitup.service.network.ConnectCommand;
import de.ibba.keepitup.service.network.ConnectCommandResult;

public class MockConnectCommand extends ConnectCommand {

    private final ConnectCommandResult connectCommandResult;

    public MockConnectCommand(Context context, InetAddress address, int port, ConnectCommandResult connectCommandResult) {
        super(context, address, port);
        this.connectCommandResult = connectCommandResult;
    }

    @Override
    public ConnectCommandResult call() {
        return connectCommandResult;
    }
}
