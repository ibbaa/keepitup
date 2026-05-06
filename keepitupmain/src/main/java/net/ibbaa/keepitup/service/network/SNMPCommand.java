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

package net.ibbaa.keepitup.service.network;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.ITimeService;
import net.ibbaa.keepitup.util.NumberUtil;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

public class SNMPCommand implements Callable<SNMPCommandResult> {

    private final Context context;
    private final InetAddress address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final String community;
    private final long lastSysUpTime;
    private final boolean ip6;
    private final ITimeService timeService;

    public SNMPCommand(Context context, InetAddress address, int port, SNMPVersion snmpVersion, String community, long lastSysUpTime, boolean ip6) {
        this.context = context;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion;
        this.community = community;
        this.lastSysUpTime = lastSysUpTime;
        this.ip6 = ip6;
        this.timeService = createTimeService();
    }

    @Override
    public SNMPCommandResult call() {
        Log.d(SNMPCommand.class.getName(), "call");
        long start = timeService.getCurrentTimestamp();
        SNMPAccess snmpAccess = getSNMPAccess();
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        SNMPAccess.WalkResult walkResult = snmpAccess.walk(snmpMapping.getSystemOID());
        Map<String, String> result = walkResult.result() != null ? walkResult.result() : Collections.emptyMap();
        long currentSysUpTime = snmpMapping.getSysUpTime(result);
        boolean rebooted = wasRebooted(currentSysUpTime);
        long end = timeService.getCurrentTimestamp();
        return new SNMPCommandResult(currentSysUpTime >= 0, result, rebooted, walkResult.exception(), walkResult.errorMessages(), NumberUtil.ensurePositive(end - start));
    }

    private boolean wasRebooted(long currentSysUpTime) {
        Log.d(SNMPCommand.class.getName(), "wasRebooted, currentSysUpTime is " + currentSysUpTime + "lastSysUpTime is " + currentSysUpTime);
        if (currentSysUpTime < 0 || lastSysUpTime < 0) {
            return false;
        }
        if (currentSysUpTime >= lastSysUpTime) {
            return false;
        }
        long maxTimeTicks = NumberUtil.getLongValue(getResources().getString(R.string.sys_uptime_max_time_ticks), -1);
        long overflowThreshold = (long) (maxTimeTicks * 0.95);
        return lastSysUpTime < overflowThreshold;
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    protected SNMPAccess getSNMPAccess() {
        return new SNMPAccess(getContext(), address, port, snmpVersion, community, ip6);
    }
}
