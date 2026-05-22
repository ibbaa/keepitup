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
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemMergeResult;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.ITimeService;
import net.ibbaa.keepitup.util.NumberUtil;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class SNMPCommand implements Callable<SNMPCommandResult> {

    private final Context context;
    private final long networkTaskId;
    private final InetAddress address;
    private final int port;
    private final SNMPVersion snmpVersion;
    private final String community;
    private final List<SNMPItem> interfaces;
    private final long lastSysUpTime;
    private final boolean ip6;
    private final ITimeService timeService;

    public SNMPCommand(Context context, long networkTaskId, InetAddress address, int port, SNMPVersion snmpVersion, String community, List<SNMPItem> interfaces, long lastSysUpTime, boolean ip6) {
        this.context = context;
        this.networkTaskId = networkTaskId;
        this.address = address;
        this.port = port;
        this.snmpVersion = snmpVersion;
        this.community = community;
        this.interfaces = interfaces;
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
        SNMPAccess.WalkResult systemWalkResult = snmpAccess.walkSystem();
        Map<String, String> systemResult = systemWalkResult.result() != null ? systemWalkResult.result() : Collections.emptyMap();
        long currentSysUpTime = snmpMapping.getSysUpTime(systemResult);
        boolean rebooted = wasRebooted(currentSysUpTime);
        if (!systemWalkResult.success()) {
            long end = timeService.getCurrentTimestamp();
            return new SNMPCommandResult(false, systemResult, prepareEmptyInterfaceResult(), rebooted, systemWalkResult.exception(), systemWalkResult.errorMessages(), NumberUtil.ensurePositive(end - start));
        }
        if (interfaces.isEmpty()) {
            long end = timeService.getCurrentTimestamp();
            return new SNMPCommandResult(true, systemResult, prepareEmptyInterfaceResult(), rebooted, systemWalkResult.exception(), systemWalkResult.errorMessages(), NumberUtil.ensurePositive(end - start));
        }
        List<SNMPItem> existingDescrItems = snmpMapping.filterDescrItems(interfaces);
        Map<String, SNMPInterfaceInfo> existingInterfaceInfos = snmpMapping.extractSNMPInterfaceInfos(interfaces);
        SNMPAccess.WalkResult ifDescrResult = snmpAccess.walkInterfacesDescr();
        if (!ifDescrResult.success()) {
            long end = timeService.getCurrentTimestamp();
            return new SNMPCommandResult(false, systemResult, prepareEmptyInterfaceResult(), rebooted, ifDescrResult.exception(), ifDescrResult.errorMessages(), NumberUtil.ensurePositive(end - start));
        }
        List<SNMPItem> scannedSnmpItems = snmpMapping.toSNMPItems(ifDescrResult.result(), networkTaskId);
        if (scannedSnmpItems.isEmpty()) {
            return prepareEmptyDescrResult(existingDescrItems, systemResult, rebooted, systemWalkResult, start);
        }
        Collections.sort(scannedSnmpItems, new SNMPMapping.SNMPItemNameComparator());
        SNMPAccess.WalkResult ifTypeResult = snmpAccess.walkInterfacesType();
        if (!ifTypeResult.success()) {
            long end = timeService.getCurrentTimestamp();
            return new SNMPCommandResult(false, systemResult, prepareEmptyInterfaceResult(), rebooted, ifTypeResult.exception(), ifTypeResult.errorMessages(), NumberUtil.ensurePositive(end - start));
        }
        SNMPAccess.WalkResult ifOperStatusResult = snmpAccess.walkInterfacesOperStatus();
        if (!ifOperStatusResult.success()) {
            long end = timeService.getCurrentTimestamp();
            return new SNMPCommandResult(false, systemResult, prepareEmptyInterfaceResult(), rebooted, ifOperStatusResult.exception(), ifOperStatusResult.errorMessages(), NumberUtil.ensurePositive(end - start));
        }
        SNMPAccess.WalkResult ifAliasResult = snmpAccess.walkInterfacesAlias();
        Map<String, String> combinedInfo = new HashMap<>(ifTypeResult.result().size() + ifOperStatusResult.result().size() + ifAliasResult.result().size());
        combinedInfo.putAll(ifTypeResult.result());
        combinedInfo.putAll(ifOperStatusResult.result());
        if (ifAliasResult.success()) {
            combinedInfo.putAll(ifAliasResult.result());
        }
        Map<String, SNMPInterfaceInfo> scannedInterfaceInfos = snmpMapping.toSNMPInterfaceInfo(scannedSnmpItems, combinedInfo);
        SNMPItemMergeResult mergeResult = snmpMapping.mergeDescrItems(existingDescrItems, scannedSnmpItems);
        Map<String, SNMPInterfaceInfo> mergedInterfaceInfos = snmpMapping.mergeSNMPInterfaceInfos(existingInterfaceInfos, scannedInterfaceInfos);
        Map<String, String> downStatusForMonitored = getDownStatusForMonitored(mergeResult.mergedItems(), mergedInterfaceInfos);
        List<SNMPItem> finalMerged = snmpMapping.mergeAllSNMPItems(interfaces, mergeResult.mergedItems(), mergedInterfaceInfos, networkTaskId);
        List<String> monitoredNotFound = getMonitoredNotFound(mergeResult.removedMonitoredItems());
        SNMPInterfaceResult interfaceResult = new SNMPInterfaceResult(true, finalMerged, monitoredNotFound, downStatusForMonitored);
        boolean finalSuccess = monitoredNotFound.isEmpty() && downStatusForMonitored.isEmpty();
        long end = timeService.getCurrentTimestamp();
        return new SNMPCommandResult(finalSuccess, systemResult, interfaceResult, rebooted, systemWalkResult.exception(), systemWalkResult.errorMessages(), NumberUtil.ensurePositive(end - start));
    }

    private List<String> getMonitoredNotFound(List<SNMPItem> removedMonitored) {
        List<String> monitoredNotFound = new ArrayList<>();
        for (SNMPItem removedItem : removedMonitored) {
            monitoredNotFound.add(removedItem.getName());
        }
        return monitoredNotFound;
    }

    private Map<String, String> getDownStatusForMonitored(List<SNMPItem> snmpItems, Map<String, SNMPInterfaceInfo> interfaceInfos) {
        Log.d(SNMPCommand.class.getName(), "getDownStatusForMonitored");
        Map<String, String> result = new HashMap<>();
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        int upStatus = getResources().getInteger(R.integer.interface_operstatus_up);
        for (SNMPItem currentItem : snmpItems) {
            if (currentItem.isMonitored()) {
                SNMPInterfaceInfo interfaceInfo = interfaceInfos.get(currentItem.getOid());
                if (interfaceInfo != null) {
                    if (interfaceInfo.getStatus() != upStatus) {
                        result.put(interfaceInfo.getDescr(), snmpMapping.getLabelForInterfaceOperStatus(interfaceInfo.getStatus()));
                    }
                }
            }
        }
        return result;
    }

    private SNMPCommandResult prepareEmptyDescrResult(List<SNMPItem> existingDescrItems, Map<String, String> systemResult, boolean rebooted, SNMPAccess.WalkResult systemWalkResult, long start) {
        Log.d(SNMPCommand.class.getName(), "prepareEmptyDescrResult");
        List<String> monitoredNotFound = new ArrayList<>();
        for (SNMPItem item : existingDescrItems) {
            if (item.isMonitored()) {
                monitoredNotFound.add(item.getName());
            }
        }
        boolean success = monitoredNotFound.isEmpty();
        SNMPInterfaceResult interfaceResult = new SNMPInterfaceResult(true, Collections.emptyList(), monitoredNotFound, Collections.emptyMap());
        long end = timeService.getCurrentTimestamp();
        return new SNMPCommandResult(success, systemResult, interfaceResult, rebooted, systemWalkResult.exception(), systemWalkResult.errorMessages(), NumberUtil.ensurePositive(end - start));
    }

    private SNMPInterfaceResult prepareEmptyInterfaceResult() {
        return new SNMPInterfaceResult(false, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
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
