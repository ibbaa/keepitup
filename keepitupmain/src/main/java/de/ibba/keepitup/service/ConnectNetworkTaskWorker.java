package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.network.ConnectCommand;
import de.ibba.keepitup.service.network.ConnectCommandResult;
import de.ibba.keepitup.util.StringUtil;

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
        return getResources().getString(R.string.text_connect_worker_max_instances_error, activeInstances);
    }

    @Override
    public ExecutionResult execute(NetworkTask networkTask) {
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Executing ConnectNetworkTaskWorker for " + networkTask);
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
            ExecutionResult connectExecutionResult = executeConnectCommand(address, networkTask.getPort(), ip6);
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

    private ExecutionResult executeConnectCommand(InetAddress address, int port, boolean ip6) {
        Log.d(ConnectNetworkTaskWorker.class.getName(), "executeConnectCommand, address is " + address + ", port is " + port);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        int count = preferenceManager.getPreferenceConnectCount();
        Callable<ConnectCommandResult> connectCommand = getConnectCommand(address, port, count);
        int connectTimeout = getResources().getInteger(R.integer.connect_timeout) * count * 2;
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
            if (connectResult.isSuccess()) {
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
        Throwable exc = connectResult.getException();
        if (exc != null) {
            successMessage = successMessage + " " + getResources().getString(R.string.text_connect_last_error);
            return getMessageFromException(successMessage, exc, connectTimeout);
        }
        return successMessage;
    }

    private String getConnectFailedMessage(ConnectCommandResult connectResult, String address, int port, boolean ip6, int connectTimeout) {
        String failedMessage = getResources().getString(R.string.text_connect_failure, getAddressWithPort(address, port, ip6));
        failedMessage = failedMessage + " " + getConnectStatsMessage(connectResult);
        Throwable exc = connectResult.getException();
        if (exc != null) {
            failedMessage = failedMessage + " " + getResources().getString(R.string.text_connect_last_error);
            return getMessageFromException(failedMessage, exc, connectTimeout);
        }
        return failedMessage;
    }

    private String getConnectStatsMessage(ConnectCommandResult connectResult) {
        int attempts = connectResult.getAttempts();
        int successfulAttempts = connectResult.getSuccessfulAttempts();
        int timeouts = connectResult.getTimeoutAttempts();
        int errors = connectResult.getErrorAttempts();
        String attemptsMessage = attempts == 1 ? getResources().getString(R.string.text_connect_attempt, attempts) : getResources().getString(R.string.text_connect_attempts, attempts);
        String successfulAttemptsMessage = successfulAttempts == 1 ? getResources().getString(R.string.text_connect_attempt_successful, successfulAttempts) : getResources().getString(R.string.text_connect_attempts_successful, successfulAttempts);
        String timeoutsMessage = timeouts == 1 ? getResources().getString(R.string.text_connect_timeout, timeouts) : getResources().getString(R.string.text_connect_timeouts, timeouts);
        String errorMessage = errors == 1 ? getResources().getString(R.string.text_connect_error, errors) : getResources().getString(R.string.text_connect_errors, errors);
        String message = attemptsMessage + " " + successfulAttemptsMessage + " " + timeoutsMessage + " " + errorMessage;
        if (successfulAttempts > 0) {
            String averageTime = StringUtil.formatTimeRange(connectResult.getAverageTime(), getContext());
            String averageTimeMessage = getResources().getString(R.string.text_connect_time, averageTime);
            message += " " + averageTimeMessage;
        }
        return message;
    }

    private String getAddressWithPort(String address, int port, boolean ip6) {
        String addressPort = ip6 ? "[" + address + "]" : address;
        return addressPort + ":" + port;
    }

    protected Callable<ConnectCommandResult> getConnectCommand(InetAddress address, int port, int connectCount) {
        return new ConnectCommand(getContext(), address, port, connectCount);
    }
}
