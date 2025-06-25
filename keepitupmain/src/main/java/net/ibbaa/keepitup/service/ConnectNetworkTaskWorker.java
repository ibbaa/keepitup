/*
 * Copyright (c) 2025 Alwin Ibba
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
import net.ibbaa.keepitup.service.network.ConnectCommand;
import net.ibbaa.keepitup.service.network.ConnectCommandResult;
import net.ibbaa.keepitup.util.StringUtil;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConnectNetworkTaskWorker extends NetworkTaskWorker {

    public ConnectNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public int getMaxInstances() {
        return getResources().getInteger(R.integer.connect_worker_max_instances);
    }

    @Override
    public String getMaxInstancesErrorMessage(int activeInstances) {
        return getResources().getQuantityString(R.plurals.text_connect_worker_max_instances_error, activeInstances, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask, AccessTypeData data) {
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Executing ConnectNetworkTaskWorker for network task " + networkTask + " and access type data" + data);
        DNSExecutionResult dnsExecutionResult = executeDNSLookup(networkTask.getAddress(), getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (dnsExecutionResult.getAddress() != null) {
            InetAddress address = dnsExecutionResult.getAddress();
            Log.d(ConnectNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
            boolean ip6 = address instanceof Inet6Address;
            if (ip6) {
                Log.d(ConnectNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
            } else {
                Log.d(ConnectNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
            }
            ExecutionResult connectExecutionResult = executeConnectCommand(address, networkTask.getPort(), data.getConnectCount(), data.isStopOnSuccess(), ip6);
            LogEntry logEntry = connectExecutionResult.getLogEntry();
            completeLogEntry(networkTask, logEntry);
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Returning " + connectExecutionResult);
            return connectExecutionResult;
        } else {
            Log.e(ConnectNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
        }
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        completeLogEntry(networkTask, logEntry);
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Returning " + dnsExecutionResult);
        return dnsExecutionResult;
    }

    private void completeLogEntry(NetworkTask networkTask, LogEntry logEntry) {
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
    }

    @SuppressWarnings("resource")
    private ExecutionResult executeConnectCommand(InetAddress address, int port, int connectCount, boolean stopOnSuccess, boolean ip6) {
        Log.d(ConnectNetworkTaskWorker.class.getName(), "executeConnectCommand, address is " + address + ", port is " + port + ", connectCount is " + connectCount + ", ip6 is " + ip6);
        Callable<ConnectCommandResult> connectCommand = getConnectCommand(address, port, connectCount, stopOnSuccess);
        int connectTimeout = getResources().getInteger(R.integer.connect_timeout) * connectCount * 2;
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<ConnectCommandResult> connectResultFuture = null;
        LogEntry logEntry = new LogEntry();
        boolean interrupted = false;
        try {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Executing " + connectCommand.getClass().getSimpleName() + " with a timeout of " + connectTimeout);
            connectResultFuture = executorService.submit(connectCommand);
            ConnectCommandResult connectResult = connectResultFuture.get(connectTimeout, TimeUnit.SECONDS);
            Log.d(ConnectNetworkTaskWorker.class.getName(), connectCommand.getClass().getSimpleName() + " returned " + connectResult);
            if (connectResult.success()) {
                Log.d(ConnectNetworkTaskWorker.class.getName(), "Connect was successful.");
                logEntry.setSuccess(true);
                logEntry.setMessage(getConnectSuccessMessage(connectResult, address.getHostAddress(), port, ip6, connectTimeout));
            } else {
                Log.d(ConnectNetworkTaskWorker.class.getName(), "Connect was not successful.");
                logEntry.setSuccess(false);
                logEntry.setMessage(getConnectFailedMessage(connectResult, address.getHostAddress(), port, ip6, connectTimeout));
            }
        } catch (Throwable exc) {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Error executing " + connectCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_connect_failure, getAddressWithPort(address.getHostAddress(), port, ip6)), exc, connectTimeout));
            if (connectResultFuture != null && isInterrupted(exc)) {
                Log.d(ConnectNetworkTaskWorker.class.getName(), "Cancelling " + connectCommand.getClass().getSimpleName());
                connectResultFuture.cancel(true);
                interrupted = true;
            }
        } finally {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        return new ExecutionResult(interrupted, logEntry);
    }

    private String getConnectSuccessMessage(ConnectCommandResult connectResult, String address, int port, boolean ip6, int connectTimeout) {
        String successMessage = getResources().getString(R.string.text_connect_success, getAddressWithPort(address, port, ip6));
        successMessage = successMessage + " " + getConnectStatsMessage(connectResult);
        Throwable exc = connectResult.exception();
        if (exc != null) {
            successMessage = successMessage + " " + getResources().getString(R.string.text_connect_last_error);
            return getMessageFromException(successMessage, exc, connectTimeout);
        }
        return successMessage;
    }

    private String getConnectFailedMessage(ConnectCommandResult connectResult, String address, int port, boolean ip6, int connectTimeout) {
        String failedMessage = getResources().getString(R.string.text_connect_failure, getAddressWithPort(address, port, ip6));
        failedMessage = failedMessage + " " + getConnectStatsMessage(connectResult);
        Throwable exc = connectResult.exception();
        if (exc != null) {
            failedMessage = failedMessage + " " + getResources().getString(R.string.text_connect_last_error);
            return getMessageFromException(failedMessage, exc, connectTimeout);
        }
        return failedMessage;
    }

    private String getConnectStatsMessage(ConnectCommandResult connectResult) {
        int attempts = connectResult.attempts();
        int successfulAttempts = connectResult.successfulAttempts();
        int timeouts = connectResult.timeoutAttempts();
        int errors = connectResult.errorAttempts();
        String attemptsMessage = getResources().getQuantityString(R.plurals.text_connect_attempt, attempts, attempts);
        String successfulAttemptsMessage = getResources().getQuantityString(R.plurals.text_connect_attempt_successful, successfulAttempts, successfulAttempts);
        String timeoutsMessage = getResources().getQuantityString(R.plurals.text_connect_timeout, timeouts, timeouts);
        String errorMessage = getResources().getQuantityString(R.plurals.text_connect_error, errors, errors);
        String message = attemptsMessage + " " + successfulAttemptsMessage + " " + timeoutsMessage + " " + errorMessage;
        if (successfulAttempts > 0) {
            String averageTime = StringUtil.formatTimeRange(connectResult.averageTime(), getContext());
            String averageTimeMessage = successfulAttempts > 1 ? getResources().getString(R.string.text_connect_average_time, averageTime) : getResources().getString(R.string.text_connect_time, averageTime);
            message += " " + averageTimeMessage;
        }
        return message;
    }

    private String getAddressWithPort(String address, int port, boolean ip6) {
        String addressPort = ip6 ? "[" + address + "]" : address;
        return addressPort + ":" + port;
    }

    protected Callable<ConnectCommandResult> getConnectCommand(InetAddress address, int port, int connectCount, boolean stopOnSuccess) {
        return new ConnectCommand(getContext(), address, port, connectCount, stopOnSuccess);
    }
}
