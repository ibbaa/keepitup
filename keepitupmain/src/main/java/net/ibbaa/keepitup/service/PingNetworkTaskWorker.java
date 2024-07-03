/*
 * Copyright (c) 2024. Alwin Ibba
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

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.network.PingCommand;
import net.ibbaa.keepitup.service.network.PingCommandResult;
import net.ibbaa.keepitup.service.network.PingOutputParser;
import net.ibbaa.keepitup.util.StringUtil;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PingNetworkTaskWorker extends NetworkTaskWorker {

    public PingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public int getMaxInstances() {
        return getResources().getInteger(R.integer.ping_worker_max_instances);
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return getResources().getQuantityString(R.plurals.text_ping_worker_max_instances_error, activeInstances, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask, AccessTypeData data) {
        Log.d(PingNetworkTaskWorker.class.getName(), "Executing PingNetworkTaskWorker for network task " + networkTask + " and access type data" + data);
        DNSExecutionResult dnsExecutionResult = executeDNSLookup(networkTask.getAddress(), getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (dnsExecutionResult.getAddress() != null) {
            InetAddress address = dnsExecutionResult.getAddress();
            Log.d(PingNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
            boolean ip6 = address instanceof Inet6Address;
            if (ip6) {
                Log.d(PingNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
            }
            PreferenceManager preferenceManager = new PreferenceManager(getContext());
            boolean enforceDefaultPackageSize = preferenceManager.getPreferenceEnforceDefaultPingPackageSize();
            Log.d(PingNetworkTaskWorker.class.getName(), "enforceDefaultPackageSize is " + enforceDefaultPackageSize);
            ExecutionResult pingExecutionResult = executePingCommand(address.getHostAddress(), data.getPingCount(), enforceDefaultPackageSize, data.getPingPackageSize(), data.isStopOnSuccess(), ip6);
            LogEntry logEntry = pingExecutionResult.getLogEntry();
            completeLogEntry(networkTask, logEntry);
            Log.d(PingNetworkTaskWorker.class.getName(), "Returning " + pingExecutionResult);
            return pingExecutionResult;
        } else {
            Log.e(PingNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
        }
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        completeLogEntry(networkTask, logEntry);
        Log.d(PingNetworkTaskWorker.class.getName(), "Returning " + dnsExecutionResult);
        return dnsExecutionResult;
    }

    private void completeLogEntry(NetworkTask networkTask, LogEntry logEntry) {
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
    }

    private ExecutionResult executePingCommand(String address, int pingCount, boolean defaultPackageSize, int packageSize, boolean stopOnSuccess, boolean ip6) {
        Log.d(PingNetworkTaskWorker.class.getName(), "executePingCommand, address is " + address + ", pingCount is " + pingCount + ", defaultPackageSize is " + defaultPackageSize + ", packageSize is " + packageSize + ", ip6 is " + ip6);
        Callable<PingCommandResult> pingCommand = getPingCommand(address, pingCount, defaultPackageSize, packageSize, stopOnSuccess, ip6);
        int timeout = getResources().getInteger(R.integer.ping_timeout) * pingCount * 2;
        Log.d(PingNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<PingCommandResult> pingResultFuture = null;
        LogEntry logEntry = new LogEntry();
        boolean interrupted = false;
        try {
            Log.d(PingNetworkTaskWorker.class.getName(), "Executing " + pingCommand.getClass().getSimpleName() + " with a timeout of " + timeout);
            pingResultFuture = executorService.submit(pingCommand);
            PingCommandResult pingResult = pingResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(PingNetworkTaskWorker.class.getName(), pingCommand.getClass().getSimpleName() + " returned " + pingResult);
            if (pingResult.exception() == null && pingResult.processReturnCode() == 0) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was successful");
                logEntry.setSuccess(true);
                logEntry.setMessage(getPingSuccessMessage(address, getPingOutputMessage(pingResult, stopOnSuccess)));
            } else if (pingResult.exception() != null) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because of an exception", pingResult.exception());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_ping_error, address), pingResult.exception(), timeout));
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because the ping command returned " + pingResult.processReturnCode());
                logEntry.setSuccess(false);
                logEntry.setMessage(getPingFailureMessage(pingResult.processReturnCode(), address, getPingOutputMessage(pingResult, stopOnSuccess)));
            }
        } catch (Throwable exc) {
            Log.d(PingNetworkTaskWorker.class.getName(), "Error executing " + pingCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_ping_error, address), exc, timeout));
            if (pingResultFuture != null && isInterrupted(exc)) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Cancelling " + pingCommand.getClass().getSimpleName());
                pingResultFuture.cancel(true);
                interrupted = true;
            }
        } finally {
            Log.d(PingNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        return new ExecutionResult(interrupted, logEntry);
    }

    private String getPingSuccessMessage(String address, String output) {
        return getResources().getString(R.string.text_ping_success, address) + " " + output;
    }

    private String getPingFailureMessage(int returnCode, String address, String output) {
        if (StringUtil.isEmpty(output)) {
            return getResources().getString(R.string.text_ping_error, address) + " " + getResources().getString(R.string.text_ping_return_code_error, returnCode);
        }
        return getResources().getString(R.string.text_ping_error, address) + " " + output;
    }

    private String getPingOutputMessage(PingCommandResult result, boolean stopOnSuccess) {
        Log.d(PingNetworkTaskWorker.class.getName(), "getPingOutputMessage, output is " + result.output());
        PingOutputParser parser = new PingOutputParser();
        try {
            parser.parse(result.output());
        } catch (Exception exc) {
            Log.e(PingNetworkTaskWorker.class.getName(), "Error parsing ping output: " + result.output());
            return result.output();
        }
        if (!parser.isValidInput()) {
            return result.output();
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        int bytesReceived = parser.getBytesReceived();
        int packetsTransmitted = stopOnSuccess ? result.pingCalls() : parser.getPacketsTransmitted();
        int packetsReceived = parser.getPacketsReceived();
        String packetLoss;
        if (stopOnSuccess) {
            packetLoss = numberFormat.format(packetsTransmitted > packetsReceived ? ((double) (packetsTransmitted - packetsReceived) / packetsTransmitted) * 100 : 0.0f) + "%";
        } else {
            packetLoss = numberFormat.format(parser.getPacketLoss()) + "%";
        }
        String packetsTransmittedMessage = getResources().getQuantityString(R.plurals.text_ping_packet_transmitted, packetsTransmitted, packetsTransmitted);
        String packetsReceivedMessage = getResources().getQuantityString(R.plurals.text_ping_packet_received, packetsReceived, packetsReceived);
        String packetLossMessage = getResources().getString(R.string.text_ping_packet_loss, packetLoss);
        String message = packetsTransmittedMessage + " " + packetsReceivedMessage + " " + packetLossMessage;
        if (parser.getValidTimes() > 0) {
            String averageTime = StringUtil.formatTimeRange(parser.getAverageTime(), getContext());
            if (packetsReceived == 1) {
                message += " " + getResources().getString(R.string.text_ping_time, averageTime);
            } else {
                message += " " + getResources().getString(R.string.text_ping_average_time, averageTime);
            }

        }
        if (bytesReceived > 0 && packetsReceived > 0) {
            String bytesReceivedMessage = getResources().getQuantityString(R.plurals.text_ping_bytes_received, bytesReceived, bytesReceived);
            message = bytesReceivedMessage + " " + message;
        }
        return message;
    }

    protected Callable<PingCommandResult> getPingCommand(String address, int pingCount, boolean defaultPackageSize, int packageSize, boolean stopOnSuccess, boolean ip6) {
        return new PingCommand(getContext(), address, pingCount, defaultPackageSize, packageSize, stopOnSuccess, ip6);
    }
}
