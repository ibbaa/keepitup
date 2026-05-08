/*
 * Copyright (c) 2026 Alwin Ibba
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

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.SNMPNetworkTaskWorker;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.SNMPCommandResult;

import java.net.InetAddress;
import java.util.concurrent.Callable;

public class TestSNMPNetworkTaskWorker extends SNMPNetworkTaskWorker {

    private MockDNSLookup mockDNSLookup;
    private final MockSNMPCommand mockSNMPCommand;

    public TestSNMPNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
        mockSNMPCommand = new MockSNMPCommand(context, InetAddress.getLoopbackAddress(), 161, SNMPVersion.V2C, "community", -1, false);
    }

    public void setMockDNSLookup(MockDNSLookup mockDNSLookup) {
        this.mockDNSLookup = mockDNSLookup;
    }

    public MockSNMPCommand getMockSNMPCommand() {
        return mockSNMPCommand;
    }

    @Override
    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return mockDNSLookup;
    }

    @Override
    protected Callable<SNMPCommandResult> getSNMPCommand(InetAddress address, int port, SNMPVersion snmpVersion, String snmpCommunity, long lastSysUpTime, boolean ip6) {
        return mockSNMPCommand;
    }
}
