package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import java.net.InetAddress;
import java.util.concurrent.Callable;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.ConnectNetworkTaskWorker;
import de.ibba.keepitup.service.network.ConnectCommandResult;
import de.ibba.keepitup.service.network.DNSLookupResult;

public class TestConnectNetworkTaskWorker extends ConnectNetworkTaskWorker {

    private MockDNSLookup mockDNSLookup;
    private MockConnectCommand mockConnectCommand;

    public TestConnectNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    public void setMockConnectCommand(MockConnectCommand mockConnectCommand) {
        this.mockConnectCommand = mockConnectCommand;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }

    @Override
    protected Callable<ConnectCommandResult> getConnectCommand(InetAddress address, int port, int connectCount) {
        return mockConnectCommand;
    }
}
