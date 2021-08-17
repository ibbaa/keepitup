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

import java.util.concurrent.Callable;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.PingNetworkTaskWorker;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.PingCommandResult;

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
