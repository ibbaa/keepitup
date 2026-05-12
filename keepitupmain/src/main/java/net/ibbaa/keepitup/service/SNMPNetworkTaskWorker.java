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
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.SNMPCommand;
import net.ibbaa.keepitup.service.network.SNMPCommandResult;
import net.ibbaa.keepitup.service.network.SNMPMapping;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
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
            ExecutionResult snmpExecutionResult = executeSNMPCommand(address, networkTask.getPort(), data.getSnmpVersion(), data.getSnmpCommunity(), networkTask.getLastSysUpTime(), ip6);
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

    private void completeLogEntry(NetworkTask networkTask, LogEntry logEntry) {
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
    }

    @SuppressWarnings("resource")
    private ExecutionResult executeSNMPCommand(InetAddress address, int port, SNMPVersion snmpVersion, String snmpCommunity, long lastSysUpTime, boolean ip6) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "executeSNMPCommand, address is " + address + ", port is " + port + ", snmpVersion is " + snmpVersion + ", lastSysUpTime is " + lastSysUpTime + ", ip6 is " + ip6);
        Callable<SNMPCommandResult> snmpCommand = getSNMPCommand(address, port, snmpVersion, snmpCommunity, lastSysUpTime, ip6);
        int snmpTimeout = getResources().getInteger(R.integer.snmp_request_timeout) * 8;
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
            SNMPMapping snmpMapping = new SNMPMapping(getContext());
            long currentSysUpTime = snmpMapping.getSysUpTime(snmpResult.result());
            if (currentSysUpTime >= 0) {
                updateNetworkTaskLastSysUpTime(currentSysUpTime);
            }
            if (snmpResult.success()) {
                Log.d(SNMPNetworkTaskWorker.class.getName(), "SNMP request was successful.");
                logEntry.setSuccess(true);
                logEntry.setMessage(getSNMPSuccessMessage(snmpResult, URLUtil.getHostAddress(address), port, ip6, snmpTimeout));
            } else {
                Log.d(SNMPNetworkTaskWorker.class.getName(), "Connect was not successful.");
                logEntry.setSuccess(false);
                logEntry.setMessage(getSNMPFailedMessage(snmpResult, URLUtil.getHostAddress(address), port, ip6, snmpTimeout));
            }
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

    private String getSNMPSuccessMessage(SNMPCommandResult snmpResult, String address, int port, boolean ip6, int snmpTimeout) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getSNMPSuccessMessage, snmpResult is " + snmpResult + ", address is " + address + ", port is " + port + ", ip6 is " + ip6 + ", snmpTimeout is " + snmpTimeout);
        String successMessage = getResources().getString(R.string.text_snmp_success, getAddressWithPort(address, port, ip6));
        if (snmpResult.reboot()) {
            successMessage += ". " + getResources().getString(R.string.text_snmp_reboot);
        }
        Map<String, String> result = snmpResult.result();
        String systemValues = getSystemValues(result);
        String sysUpTimeMessage = getSysUpTime(result);
        if (!StringUtil.isEmpty(systemValues) && !StringUtil.isEmpty(sysUpTimeMessage)) {
            successMessage += ". " + systemValues + ", " + sysUpTimeMessage;
        } else if (!StringUtil.isEmpty(systemValues)) {
            successMessage += ". " + systemValues;
        } else if (!StringUtil.isEmpty(sysUpTimeMessage)) {
            successMessage += ". " + sysUpTimeMessage;
        }
        String durationMessage = getResources().getString(R.string.text_snmp_time, StringUtil.formatTimeRange(snmpResult.duration(), getContext()));
        successMessage += ". " + durationMessage + ".";
        Throwable exc = snmpResult.exception();
        if (exc != null) {
            successMessage = successMessage + " " + getResources().getString(R.string.text_connect_last_error);
            return getMessageFromException(successMessage, exc, snmpTimeout);
        }
        return successMessage;
    }

    @SuppressWarnings("SizeReplaceableByIsEmpty")
    private String getSNMPFailedMessage(SNMPCommandResult snmpResult, String address, int port, boolean ip6, int snmpTimeout) {
        Log.d(SNMPNetworkTaskWorker.class.getName(), "getSNMPFailedMessage, snmpResult is " + snmpResult + ", address is " + address + ", port is " + port + ", ip6 is " + ip6 + ", snmpTimeout is " + snmpTimeout);
        String failedMessage = getResources().getString(R.string.text_snmp_failure, getAddressWithPort(address, port, ip6));
        if (snmpResult.reboot()) {
            failedMessage += ". " + getResources().getString(R.string.text_snmp_reboot);
        }
        List<String> errorMessages = snmpResult.errorMessages();
        Map<String, String> result = snmpResult.result();
        String errorMessage = getErrorMessages(errorMessages);
        String systemValues = getSystemValues(result);
        String sysUpTimeMessage = getSysUpTime(result);
        StringBuilder builder = new StringBuilder();
        if (!StringUtil.isEmpty(errorMessage)) {
            builder.append(errorMessage);
        }
        if (!StringUtil.isEmpty(systemValues)) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(systemValues);
        }
        if (!StringUtil.isEmpty(sysUpTimeMessage)) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(sysUpTimeMessage);
        }
        if (builder.length() > 0) {
            failedMessage += ". " + builder;
        }
        String durationMessage = getResources().getString(R.string.text_snmp_time, StringUtil.formatTimeRange(snmpResult.duration(), getContext()));
        failedMessage += ". " + durationMessage + ".";
        Throwable exc = snmpResult.exception();
        if (exc != null) {
            return getMessageFromException(failedMessage, exc, snmpTimeout);
        }
        return failedMessage;
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
        long sysUpTime = snmpMapping.getSysUpTime(result);
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

    protected Callable<SNMPCommandResult> getSNMPCommand(InetAddress address, int port, SNMPVersion snmpVersion, String snmpCommunity, long lastSysUpTime, boolean ip6) {
        return new SNMPCommand(getContext(), address, port, snmpVersion, snmpCommunity, lastSysUpTime, ip6);
    }
}
