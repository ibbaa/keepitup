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

import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPAccess;
import net.ibbaa.keepitup.service.network.SNMPCommand;

import java.net.InetAddress;
import java.util.List;

public class TestSNMPCommand extends SNMPCommand {

    private final MockSNMPAccess mockSNMPAccess;

    public TestSNMPCommand(Context context, long networkTaskId, InetAddress address, int port, SNMPVersion snmpVersion, String community, List<SNMPItem> interfaces, long lastSysUpTime, boolean ip6) {
        super(context, networkTaskId, address, port, snmpVersion, community, interfaces, lastSysUpTime, ip6);
        mockSNMPAccess = new MockSNMPAccess(context);
    }

    public MockSNMPAccess getMockSNMPAccess() {
        return mockSNMPAccess;
    }

    @Override
    protected SNMPAccess getSNMPAccess() {
        return mockSNMPAccess;
    }
}
