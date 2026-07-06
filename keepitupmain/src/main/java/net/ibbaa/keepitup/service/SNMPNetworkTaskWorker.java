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

package net.ibbaa.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SNMPAuthInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPCommand;
import net.ibbaa.keepitup.service.network.SNMPCommandResult;
import net.ibbaa.keepitup.service.network.SNMPInterfaceResult;
import net.ibbaa.keepitup.service.network.SNMPMapping;
import net.ibbaa.keepitup.ui.sync.SNMPItemSyncHandler;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SNMPNetworkTaskWorker extends NetworkTaskWorker {

    public SNMPNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public int getMaxInstances() {
        return getResources().getInteger(R.integer.snmp_worker_max_instances);
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return getResources().getQuantityString(R.plurals.text_snmp_worker_max_instances_error, activeInstances, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask, AccessTypeData data) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "execute, network task is " + networkTask + " and access type data is " + data);
        DNSExecutionResult dnsExecutionResult = executeDNSLookup(networkTask.getAddress(), getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (dnsExecutionResult.getAddress() != null) {
            InetAddress address = dnsExecutionResult.getAddress();
            Log.d(SNMPNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
            boolean ip6 = address instanceof Inet6Address;
            if (ip6) {
                Log.d(SNMPNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
            } else {
                Log.d(SNMPNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
            }
            SNMPAuthInfo authInfo = buildSNMPAuthInfo(data);
            ExecutionResult snmpExecutionResult = executeSNMPCommand(networkTask.getId(), address, networkTask.getPort(), data.getSnmpVersion(), data.getSnmpTransport(), authInfo, networkTask.getLastSysUpTime(), ip6);
            LogEntry logEntry = snmpExecutionResult.getLogEntry();
            completeLogEntry(networkTask, logEntry);
            Log.d(SNMPNetworkTaskWorker.class.getName(), "Returning " + snmpExecutionResult);
            return snmpExecutionResult;
        }
        Log.e(SNMPNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        completeLogEntry(networkTask, logEntry);
        Log.d(SNMPNetworkTaskWorker.class.getName(), "Returning " + dnsExecutionResult);
        return dnsExecutionResult;
    }

    private SNMPAuthInfo buildSNMPAuthInfo(AccessTypeData data) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "buildSNMPAuthInfo");
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setCommunity(data.getSnmpCommunity());
        authInfo.setAuthAlgorithm(data.getSnmpAuthAlgorithm());
        authInfo.setUserName(data.getSnmpUserName());
        authInfo.setAuthPassphrase(data.getSnmpAuthPassphrase());
        authInfo.setPrivAlgorithm(data.getSnmpPrivAlgorithm());
        authInfo.setPrivPassphrase(data.getSnmpPrivPassphrase());
        return authInfo;
    }

    private void completeLogEntry(NetworkTask networkTask, LogEntry logEntry) {
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
    }

    @SuppressWarnings("resource")
    private ExecutionResult executeSNMPCommand(long networkTaskId, InetAddress address, int port, SNMPVersion snmpVersion, SNMPTransport snmpTransport, SNMPAuthInfo authInfo, long lastSysUpTime, boolean ip6) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "executeSNMPCommand, networktaskId is " + networkTaskId + ", address is " + address + ", port is " + port + ", snmpVersion is " + snmpVersion + ", snmpTransport is " + snmpTransport + ", lastSysUpTime is " + lastSysUpTime + ", ip6 is " + ip6);
        List<SNMPItem> snmpItems = readSNMPItems();
        boolean initiallyEmpty = snmpItems.isEmpty();
        Callable<SNMPCommandResult> snmpCommand = getSNMPCommand(networkTaskId, address, port, snmpVersion, snmpTransport, authInfo, snmpItems, lastSysUpTime, ip6);
        int snmpTimeout = getResources().getInteger(R.integer.snmp_request_timeout) * 9;
        Log.d(SNMPNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<SNMPCommandResult> snmpResultFuture = null;
        LogEntry logEntry = new LogEntry();
        boolean interrupted = false;
        try {
            Log.d(SNMPNetworkTaskWorker.class.getName(), "Executing " + snmpCommand.getClass().getSimpleName() + " with a timeout of " + snmpTimeout);
            snmpResultFuture = executorService.submit(snmpCommand);
            SNMPCommandResult snmpResult = snmpResultFuture.get(snmpTimeout, TimeUnit.SECONDS);
            Log.d(SNMPNetworkTaskWorker.class.getName(), snmpCommand.getClass().getSimpleName() + " returned " + snmpResult);
            if (snmpResult.interfaceResult().canSave()) {
                new SNMPItemSyncHandler(getContext()).synchronizeSNMPItems(snmpResult.interfaceResult().result(), snmpItems);
            }
            SNMPMapping snmpMapping = new SNMPMapping(getContext());
            long currentSysUpTime = snmpMapping.getOverallSysUpTime(snmpResult.systemResult());
            if (currentSysUpTime >= 0) {
                updateNetworkTaskLastSysUpTime(currentSysUpTime);
            }
            Log.d(SNMPNetworkTaskWorker.class.getName(), "SNMP request success is " + snmpResult.success());
            logEntry.setSuccess(snmpResult.success());
            logEntry.setMessage(getSNMPMessage(snmpResult, URLUtil.getHostAddress(address), port, ip6, snmpTimeout, initiallyEmpty));
        } catch (Throwable exc) {
            Log.d(SNMPNetworkTaskWorker.class.getName(), "Error executing " + snmpCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_snmp_failure, getAddressWithPort(URLUtil.getHostAddress(address), port, ip6)) + ".", exc, snmpTimeout));
            if (snmpResultFuture != null && isInterrupted(exc)) {
                Log.d(SNMPNetworkTaskWorker.class.getName(), "Cancelling " + snmpResultFuture.getClass().getSimpleName());
                snmpResultFuture.cancel(true);
                interrupted = true;
            }
        } finally {
            Log.d(SNMPNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        return new ExecutionResult(interrupted, logEntry);
    }

    private String getSNMPMessage(SNMPCommandResult snmpResult, String address, int port, boolean ip6, int snmpTimeout, boolean initiallyEmpty) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getSNMPMessage, address is " + address + ", port is " + port + ", ip6 is " + ip6 + ", snmpTimeout is " + snmpTimeout + ", initiallyEmpty is " + initiallyEmpty);
        boolean requestSuccessful = snmpResult.success() || snmpResult.interfaceResult().canSave();
        String message = requestSuccessful ? getResources().getString(R.string.text_snmp_success, getAddressWithPort(address, port, ip6)) : getResources().getString(R.string.text_snmp_failure, getAddressWithPort(address, port, ip6));
        if (snmpResult.reboot()) {
            message += ". " + getResources().getString(R.string.text_snmp_reboot);
        }
        String errorMessage = getErrorMessages(snmpResult.errorMessages());
        if (!StringUtil.isEmpty(errorMessage)) {
            message += ". " + errorMessage;
        }
        String interfaceMessages = getInterfaceMessages(snmpResult, initiallyEmpty);
        if (!StringUtil.isEmpty(interfaceMessages)) {
            message += ". " + interfaceMessages;
        }
        Map<String, String> result = snmpResult.systemResult();
        String systemValues = getSystemValues(result);
        String sysUpTimeMessage = getSysUpTime(result);
        if (!StringUtil.isEmpty(systemValues) && !StringUtil.isEmpty(sysUpTimeMessage)) {
            message += ". " + systemValues + ", " + sysUpTimeMessage;
        } else if (!StringUtil.isEmpty(systemValues)) {
            message += ". " + systemValues;
        } else if (!StringUtil.isEmpty(sysUpTimeMessage)) {
            message += ". " + sysUpTimeMessage;
        }
        message += ". " + getResources().getString(R.string.text_snmp_time, StringUtil.formatTimeRange(snmpResult.duration(), getContext())) + ".";
        Throwable exc = snmpResult.exception();
        if (exc != null) {
            return getMessageFromException(message, exc, snmpTimeout);
        }
        return message;
    }

    private String getInterfaceMessages(SNMPCommandResult snmpResult, boolean initiallyEmpty) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getInterfaceMessages, initiallyEmpty is " + initiallyEmpty);
        if (initiallyEmpty || !snmpResult.interfaceResult().canSave()) {
            return "";
        }
        SNMPInterfaceResult interfaceResult = snmpResult.interfaceResult();
        List<String> messageParts = new ArrayList<>();
        List<SNMPItem> descrItems = getIfDescrItems(interfaceResult.result());
        int foundCount = interfaceResult.foundCount();
        messageParts.add(getResources().getQuantityString(R.plurals.text_snmp_interfaces_found, foundCount, foundCount));
        boolean hasAnyMonitored = hasMonitoredItem(descrItems) || !interfaceResult.monitoredNotFound().isEmpty();
        if (hasAnyMonitored) {
            if (interfaceResult.monitoredDownStatus().isEmpty() && interfaceResult.monitoredNotFound().isEmpty()) {
                messageParts.add(getResources().getString(R.string.text_snmp_all_monitored_up));
            } else {
                Map<String, List<String>> interfaceByStatusMap = new LinkedHashMap<>();
                String downLabel = getResources().getString(R.string.interface_operstatus_down_label).toLowerCase(Locale.ROOT);
                for (Map.Entry<String, String> entry : interfaceResult.monitoredDownStatus().entrySet()) {
                    String statusLabel = entry.getValue().toLowerCase(Locale.ROOT);
                    String descr = entry.getKey();
                    List<String> descrWithStatusList = interfaceByStatusMap.get(statusLabel);
                    if (descrWithStatusList == null) {
                        descrWithStatusList = new ArrayList<>();
                        interfaceByStatusMap.put(statusLabel, descrWithStatusList);
                    }
                    descrWithStatusList.add(descr);
                }
                for (Map.Entry<String, List<String>> entry : interfaceByStatusMap.entrySet()) {
                    List<String> sortedNames = new ArrayList<>(entry.getValue());
                    Collections.sort(sortedNames);
                    String names = TextUtils.join(", ", sortedNames);
                    int count = sortedNames.size();
                    if (entry.getKey().equals(downLabel)) {
                        messageParts.add(getResources().getQuantityString(R.plurals.text_snmp_monitored_down, count, names));
                    } else {
                        messageParts.add(getResources().getQuantityString(R.plurals.text_snmp_monitored_in_status, count, names, entry.getKey()));
                    }
                }
                List<String> notFound = new ArrayList<>(interfaceResult.monitoredNotFound());
                Collections.sort(notFound);
                if (!notFound.isEmpty()) {
                    String names = TextUtils.join(", ", notFound);
                    messageParts.add(getResources().getQuantityString(R.plurals.text_snmp_monitored_not_found, notFound.size(), names));
                }
            }
        }
        List<String> duplicateNames = new ArrayList<>(interfaceResult.duplicateNames());
        Collections.sort(duplicateNames);
        if (!duplicateNames.isEmpty()) {
            String names = TextUtils.join(", ", duplicateNames);
            messageParts.add(getResources().getString(R.string.text_snmp_duplicate_interfaces, names));
        }
        return TextUtils.join(". ", messageParts);
    }

    private List<SNMPItem> getIfDescrItems(List<SNMPItem> items) {
        List<SNMPItem> descrItems = new ArrayList<>();
        for (SNMPItem item : items) {
            if (SNMPItemType.INTERFACEDESCR.equals(item.getSnmpItemType())) {
                descrItems.add(item);
            }
        }
        return descrItems;
    }

    private boolean hasMonitoredItem(List<SNMPItem> items) {
        for (SNMPItem item : items) {
            if (item.isMonitored()) {
                return true;
            }
        }
        return false;
    }

    private String getSystemValues(Map<String, String> result) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getSystemValues");
        StringBuilder systemValues = new StringBuilder();
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        if (result != null) {
            for (Map.Entry<String, String> entry : result.entrySet()) {
                String oid = entry.getKey();
                String value = entry.getValue();
                String label = snmpMapping.getLabelForSystemOID(oid);
                if (!snmpMapping.isSysUpTimeOID(oid) && !StringUtil.isEmpty(value) && !StringUtil.isEmpty(label)) {
                    if (!StringUtil.isEmpty(systemValues)) {
                        systemValues.append(", ");
                    }
                    systemValues.append(label).append(": ").append(value);
                }
            }
        }
        return systemValues.toString();
    }

    private String getErrorMessages(List<String> errorMessages) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getErrorMessages");
        List<String> validErrors = new ArrayList<>();
        if (errorMessages != null) {
            for (String message : errorMessages) {
                if (message != null && message.endsWith(".")) {
                    message = message.substring(0, message.length() - 1);
                }
                if (!StringUtil.isTrimmedEmpty(message)) {
                    validErrors.add(message);
                }
            }
        }
        if (validErrors.isEmpty()) {
            return "";
        }
        String errors = TextUtils.join(", ", validErrors);
        return getResources().getQuantityString(R.plurals.text_snmp_errors, validErrors.size(), errors);
    }

    private String getSysUpTime(Map<String, String> result) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getSysUpTime");
        SNMPMapping snmpMapping = new SNMPMapping(getContext());
        long sysUpTime = snmpMapping.getOverallSysUpTime(result);
        if (sysUpTime > 0) {
            String sysUpTimeFormatted = StringUtil.formatUpTime(sysUpTime);
            String oid = snmpMapping.getSysUpTimeOID();
            String label = snmpMapping.getLabelForSystemOID(oid);
            if (!StringUtil.isEmpty(label)) {
                return label + ": " + sysUpTimeFormatted;
            }
        }
        return "";
    }

    private String getAddressWithPort(String address, int port, boolean ip6) {
        String addressPort = ip6 ? "[" + address + "]" : address;
        return addressPort + ":" + port;
    }

    protected Callable<SNMPCommandResult> getSNMPCommand(long networkTaskId, InetAddress address, int port, SNMPVersion snmpVersion, SNMPTransport snmpTransport, SNMPAuthInfo authInfo, List<SNMPItem> snmpItems, long lastSysUpTime, boolean ip6) {
        return new SNMPCommand(getContext(), networkTaskId, address, port, snmpVersion, snmpTransport, authInfo, snmpItems, lastSysUpTime, ip6);
    }
}
