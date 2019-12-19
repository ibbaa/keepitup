package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.Locale;
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
import de.ibba.keepitup.service.network.PingCommand;
import de.ibba.keepitup.service.network.PingCommandResult;
import de.ibba.keepitup.service.network.PingOutputParser;
import de.ibba.keepitup.util.StringUtil;

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
        return getResources().getString(R.string.text_ping_worker_max_instances_error, activeInstances);
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(PingNetworkTaskWorker.class.getName(), "Executing PingNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        InetAddress address = executeDNSLookup(networkTask.getAddress(), logEntry, getResources().getBoolean(R.bool.network_prefer_ipv4));
        if (address != null) {
            Log.d(PingNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
            boolean ip6 = address instanceof Inet6Address;
            if (ip6) {
                Log.d(PingNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
            }
            executePingCommand(address.getHostAddress(), ip6, logEntry);
        } else {
            Log.e(PingNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
        }
        Log.d(PingNetworkTaskWorker.class.getName(), "Returning " + logEntry);
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        return logEntry;
    }

    private void executePingCommand(String address, boolean ip6, LogEntry logEntry) {
        Log.d(PingNetworkTaskWorker.class.getName(), "executePingCommand, address is " + address + ", ip6 is " + ip6);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        int count = preferenceManager.getPreferencePingCount();
        Callable<PingCommandResult> pingCommand = getPingCommand(address, count, ip6);
        int timeout = getResources().getInteger(R.integer.ping_timeout) * count * 2;
        Log.d(PingNetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Log.d(PingNetworkTaskWorker.class.getName(), "Executing " + pingCommand.getClass().getSimpleName() + " with a timeout of " + timeout);
            Future<PingCommandResult> pingResultFuture = executorService.submit(pingCommand);
            PingCommandResult pingResult = pingResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(PingNetworkTaskWorker.class.getName(), pingCommand.getClass().getSimpleName() + " returned " + pingResult);
            if (pingResult.getException() == null && pingResult.getProcessReturnCode() == 0) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was successful");
                logEntry.setSuccess(true);
                logEntry.setMessage(getPingSuccessMessage(address, getPingOutputMessage(pingResult.getOutput())));
            } else if (pingResult.getException() != null) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because of an exception", pingResult.getException());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_ping_error, address), pingResult.getException(), timeout));
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because the ping command returned " + pingResult.getProcessReturnCode());
                logEntry.setSuccess(false);
                logEntry.setMessage(getPingFailureMessage(pingResult.getProcessReturnCode(), address, getPingOutputMessage(pingResult.getOutput())));
            }
        } catch (Throwable exc) {
            Log.d(PingNetworkTaskWorker.class.getName(), "Error executing " + pingCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_ping_error, address), exc, timeout));
        } finally {
            Log.d(PingNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
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

    private String getPingOutputMessage(String output) {
        Log.d(PingNetworkTaskWorker.class.getName(), "getPingOutputMessage, output is " + output);
        PingOutputParser parser = new PingOutputParser();
        try {
            parser.parse(output);
        } catch (Exception exc) {
            Log.e(PingNetworkTaskWorker.class.getName(), "Error parsing ping output: " + output);
            return output;
        }
        if (!parser.isValidInput()) {
            return output;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String packetLoss = numberFormat.format(parser.getPacketLoss()) + "%";
        String averageTime = numberFormat.format(parser.getAverageTime());
        return getResources().getString(R.string.text_ping_message, parser.getPacketsTransmitted(), parser.getPacketsReceived(), packetLoss, averageTime);
    }

    protected Callable<PingCommandResult> getPingCommand(String address, int pingCount, boolean ip6) {
        return new PingCommand(getContext(), address, pingCount, ip6);
    }
}
