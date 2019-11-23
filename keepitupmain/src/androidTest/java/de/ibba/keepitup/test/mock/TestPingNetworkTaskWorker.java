package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import java.util.concurrent.Callable;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.PingNetworkTaskWorker;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.service.network.PingCommandResult;

public class TestPingNetworkTaskWorker extends PingNetworkTaskWorker {

    private MockDNSLookup mockDNSLookup;
    private MockPingCommand mockPingCommand;

    public TestPingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    public void setMockPingCommand(MockPingCommand mockPingCommand) {
        this.mockPingCommand = mockPingCommand;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }

    @Override
    protected Callable<PingCommandResult> getPingCommand(String address, int pingCount, boolean ip6) {
        return mockPingCommand;
    }
}
