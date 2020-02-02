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
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Executing ConnectNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        InetAddress address = executeDNSLookup(networkTask.getAddress(), logEntry, getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (address != null) {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
            boolean ip6 = address instanceof Inet6Address;
            if (ip6) {
                Log.d(ConnectNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
            } else {
                Log.d(ConnectNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
            }
            executeConnectCommand(address, networkTask.getPort(), ip6, logEntry);
        } else {
            Log.e(ConnectNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
        }
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Returning " + logEntry);
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        return logEntry;
    }

    private void executeConnectCommand(InetAddress address, int port, boolean ip6, LogEntry logEntry) {
        Log.d(ConnectNetworkTaskWorker.class.getName(), "executeConnectCommand, address is " + address + ", port is " + port);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        int count = preferenceManager.getPreferenceConnectCount();
        Callable<ConnectCommandResult> connectCommand = getConnectCommand(address, port, count);
        int connectTimeout = getResources().getInteger(R.integer.connect_timeout) * count * 2;
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Executing " + connectCommand.getClass().getSimpleName() + " with a timeout of " + connectTimeout);
            Future<ConnectCommandResult> connectResultFuture = executorService.submit(connectCommand);
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
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_connect_error, getAddressWithPort(address.getHostAddress(), port, ip6)), exc, connectTimeout));
        } finally {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
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
        String failedMessage = getResources().getString(R.string.text_connect_error, getAddressWithPort(address, port, ip6));
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
        String attemptsMessage = attempts == 1 ? getResources().getString(R.string.text_connect_attempt, attempts) : getResources().getString(R.string.text_connect_attempts, attempts);
        String successfulAttemptsMessage = successfulAttempts == 1 ? getResources().getString(R.string.text_connect_attempt_successful, successfulAttempts) : getResources().getString(R.string.text_connect_attempts_successful, successfulAttempts);
        String timeoutsMessage = timeouts == 1 ? getResources().getString(R.string.text_connect_timeout, timeouts) : getResources().getString(R.string.text_connect_timeouts, timeouts);
        if (successfulAttempts > 0) {
            String averageTime = StringUtil.formatTimeRange(connectResult.getAverageTime(), getContext());
            String averageTimeMessage = getResources().getString(R.string.text_connect_time, averageTime);
            return attemptsMessage + " " + successfulAttemptsMessage + " " + timeoutsMessage + " " + averageTimeMessage;
        }
        return attemptsMessage + " " + successfulAttemptsMessage + " " + timeoutsMessage;
    }

    private String getAddressWithPort(String address, int port, boolean ip6) {
        String addressPort = ip6 ? "[" + address + "]" : address;
        return addressPort + ":" + port;
    }

    protected Callable<ConnectCommandResult> getConnectCommand(InetAddress address, int port, int connectCount) {
        return new ConnectCommand(getContext(), address, port, connectCount);
    }
}
