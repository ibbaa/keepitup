/*
 * Copyright (c) 2021. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.test.mock;

import android.content.Context;
import android.os.PowerManager;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.DownloadNetworkTaskWorker;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.DownloadCommandResult;

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
