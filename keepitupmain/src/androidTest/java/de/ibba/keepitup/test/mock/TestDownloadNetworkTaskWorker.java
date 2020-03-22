package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.DownloadNetworkTaskWorker;
import de.ibba.keepitup.service.IFileManager;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.service.network.DownloadCommandResult;

public class TestDownloadNetworkTaskWorker extends DownloadNetworkTaskWorker {

    private MockDNSLookup mockDNSLookup;
    private MockDownloadCommand mockDownloadCommand;
    private MockFileManager mockFileManager;

    public TestDownloadNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
        ((MockNetworkManager) getNetworkManager()).setConnected(true);
        ((MockNetworkManager) getNetworkManager()).setConnectedWithWiFi(true);
    }

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    public void setMockDownloadCommand(MockDownloadCommand mockDownloadCommand) {
        this.mockDownloadCommand = mockDownloadCommand;
    }

    public void setMockFileManager(MockFileManager mockFileManager) {
        this.mockFileManager = mockFileManager;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }

    @Override
    public Callable<DownloadCommandResult> getDownloadCommand(NetworkTask networkTask, URL url, File folder, boolean delete) {
        return mockDownloadCommand;
    }

    @Override
    protected IFileManager getFileManager() {
        return mockFileManager;
    }
}
