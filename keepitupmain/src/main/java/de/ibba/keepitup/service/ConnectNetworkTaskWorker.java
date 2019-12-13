package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.network.ConnectCommand;
import de.ibba.keepitup.service.network.ConnectCommandResult;

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
        Callable<ConnectCommandResult> connectCommand = getConnectCommand(address, port);
        int connectTimeout = getResources().getInteger(R.integer.connect_timeout);
        Log.d(ConnectNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Executing " + connectCommand.getClass().getSimpleName() + " with a timeout of " + connectTimeout * 2);
            Future<ConnectCommandResult> connectResultFuture = executorService.submit(connectCommand);
            ConnectCommandResult connectResult = connectResultFuture.get(connectTimeout * 2, TimeUnit.SECONDS);
            Log.d(ConnectNetworkTaskWorker.class.getName(), connectCommand.getClass().getSimpleName() + " returned " + connectResult);
            if (connectResult.isSuccess()) {
                Log.d(ConnectNetworkTaskWorker.class.getName(), "Connect was successful");
                logEntry.setSuccess(true);
                logEntry.setMessage(getConnectSuccessMessage(address.getHostAddress(), port, ip6));
            } else if (connectResult.getException() != null) {
                Log.d(ConnectNetworkTaskWorker.class.getName(), "Connect was not successful because of an exception", connectResult.getException());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_connect_error, getAddressWithPort(address.getHostAddress(), port, ip6)), connectResult.getException(), connectTimeout));
            } else {
                Log.d(ConnectNetworkTaskWorker.class.getName(), "Connect was not successful for an unknown reason");
                logEntry.setSuccess(false);
                logEntry.setMessage(getResources().getString(R.string.text_connect_error, getAddressWithPort(address.getHostAddress(), port, ip6)));
            }
        } catch (Throwable exc) {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Error executing " + connectCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_connect_error, getAddressWithPort(address.getHostAddress(), port, ip6)), exc, connectTimeout * 2));
        } finally {
            Log.d(ConnectNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
    }

    private String getConnectSuccessMessage(String address, int port, boolean ip6) {
        return getResources().getString(R.string.text_connect_success, getAddressWithPort(address, port, ip6));
    }

    private String getAddressWithPort(String address, int port, boolean ip6) {
        String addressPort = ip6 ? "[" + address + "]" : address;
        return addressPort + ":" + port;
    }

    protected Callable<ConnectCommandResult> getConnectCommand(InetAddress address, int port) {
        return new ConnectCommand(getContext(), address, port);
    }
}
