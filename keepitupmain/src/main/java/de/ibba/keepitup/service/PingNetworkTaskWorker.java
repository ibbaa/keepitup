package de.ibba.keepitup.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.network.DNSLookup;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.service.network.PingCommand;
import de.ibba.keepitup.service.network.PingCommandResult;
import de.ibba.keepitup.service.network.PingOutputParser;
import de.ibba.keepitup.util.ExceptionUtil;
import de.ibba.keepitup.util.StringUtil;

public class PingNetworkTaskWorker extends NetworkTaskWorker {

    public PingNetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        super(context, networkTask, wakeLock);
    }

    @Override
    public LogEntry execute(NetworkTask networkTask) {
        Log.d(PingNetworkTaskWorker.class.getName(), "Executing PingNetworkTaskWorker for " + networkTask);
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            InetAddress address = executeDNSLookup(executorService, networkTask.getAddress(), logEntry);
            if (address != null) {
                Log.d(PingNetworkTaskWorker.class.getName(), "executeDNSLookup returned " + address);
                boolean ip6 = address instanceof Inet6Address;
                if (ip6) {
                    Log.d(PingNetworkTaskWorker.class.getName(), address + " is an IPv6 address");
                } else {
                    Log.d(PingNetworkTaskWorker.class.getName(), address + " is an IPv4 address");
                }
                executePingCommand(executorService, address.getHostAddress(), ip6, logEntry);
            } else {
                Log.e(PingNetworkTaskWorker.class.getName(), "executeDNSLookup returned null. DNSLookup failed.");
            }
        } finally {
            Log.d(PingNetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        Log.d(PingNetworkTaskWorker.class.getName(), "Returning " + logEntry);
        logEntry.setTimestamp(System.currentTimeMillis());
        return logEntry;
    }

    private InetAddress executeDNSLookup(ExecutorService executorService, String host, LogEntry logEntry) {
        Log.d(PingNetworkTaskWorker.class.getName(), "executeDNSLookup, host is " + host);
        Callable<DNSLookupResult> dnsLookup = getDNSLookup(host);
        int timeout = getResources().getInteger(R.integer.dns_lookup_timeout);
        try {
            Log.d(PingNetworkTaskWorker.class.getName(), "Executing " + dnsLookup.getClass().getSimpleName() + " with a timeout of " + timeout);
            Future<DNSLookupResult> dnsLookupResultFuture = executorService.submit(dnsLookup);
            DNSLookupResult dnsLookupResult = dnsLookupResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(PingNetworkTaskWorker.class.getName(), dnsLookup.getClass().getSimpleName() + " returned " + dnsLookupResult);
            if (dnsLookupResult.getException() == null) {
                Log.d(PingNetworkTaskWorker.class.getName(), "DNS lookup was successful");
                return dnsLookupResult.getAddress();
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), "DNS lookup was not successful because of an exception", dnsLookupResult.getException());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_dns_lookup_error, host), dnsLookupResult.getException(), timeout));
            }
        } catch (Throwable exc) {
            Log.d(PingNetworkTaskWorker.class.getName(), "Error executing " + dnsLookup.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_dns_lookup_error, host), exc, timeout));
        }
        return null;
    }

    private void executePingCommand(ExecutorService executorService, String address, boolean ip6, LogEntry logEntry) {
        Log.d(PingNetworkTaskWorker.class.getName(), "executePingCommand, address is " + address + ", ip6 is " + ip6);
        Callable<PingCommandResult> pingCommand = getPingCommand(address, ip6);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        int count = preferenceManager.getPreferencePingCount();
        int timeout = getResources().getInteger(R.integer.ping_timeout) * count * 2;
        try {
            Log.d(PingNetworkTaskWorker.class.getName(), "Executing " + pingCommand.getClass().getSimpleName() + " with a timeout of " + timeout);
            Future<PingCommandResult> pingResultFuture = executorService.submit(pingCommand);
            PingCommandResult pingResult = pingResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(PingNetworkTaskWorker.class.getName(), pingCommand.getClass().getSimpleName() + " returned " + pingResult);
            if (pingResult.getException() == null && pingResult.getProcessReturnCode() == 0) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was successful");
                logEntry.setSuccess(true);
                logEntry.setMessage(getPingSuccessMessage(getAddress(address, ip6), getPingOutputMessage(pingResult.getOutput())));
            } else if (pingResult.getException() != null) {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because of an exception", pingResult.getException());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_ping_error, getAddress(address, ip6)), pingResult.getException(), timeout));
            } else {
                Log.d(PingNetworkTaskWorker.class.getName(), "Ping was not successful because the ping command returned " + pingResult.getProcessReturnCode());
                logEntry.setSuccess(false);
                logEntry.setMessage(getPingFailureMessage(pingResult.getProcessReturnCode(), getAddress(address, ip6), getPingOutputMessage(pingResult.getOutput())));
            }
        } catch (Throwable exc) {
            Log.d(PingNetworkTaskWorker.class.getName(), "Error executing " + pingCommand.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_ping_error, getAddress(address, ip6)), exc, timeout));
        }
    }

    private String getMessageFromException(String prefixMessage, Throwable exc, int timeout) {
        if (exc instanceof TimeoutException) {
            String unit = timeout == 1 ? getResources().getString(R.string.string_second) : getResources().getString(R.string.string_seconds);
            return prefixMessage + " " + getResources().getString(R.string.text_timeout, timeout) + " " + unit;
        }
        return prefixMessage + " " + ExceptionUtil.getLogableMessage(ExceptionUtil.getRootCause(exc));
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

    private String getAddress(String address, boolean ip6) {
        String ipVersion = ip6 ? getResources().getString(R.string.string_IPv6) : getResources().getString(R.string.string_IPv4);
        return address + " (" + ipVersion + ")";
    }

    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return new DNSLookup(host);
    }

    protected Callable<PingCommandResult> getPingCommand(String address, boolean ip6) {
        return new PingCommand(getContext(), address, ip6);
    }
}
