/*
 * Copyright (c) 2022. Alwin Ibba
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

import java.net.InetAddress;
import java.util.concurrent.Callable;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.ConnectNetworkTaskWorker;
import net.ibbaa.keepitup.service.network.ConnectCommandResult;
import net.ibbaa.keepitup.service.network.DNSLookupResult;

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
