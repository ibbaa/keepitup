package de.ibba.keepitup.service;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.notification.NotificationHandler;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.service.network.DNSLookup;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.ui.sync.LogEntryUIBroadcastReceiver;
import de.ibba.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;
import de.ibba.keepitup.util.ExceptionUtil;

public abstract class NetworkTaskWorker implements Runnable {

    private final Context context;
    private final NetworkTask networkTask;
    private final PowerManager.WakeLock wakeLock;
    private final INetworkManager networkManager;
    private final NotificationHandler notificationHandler;

    public NetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        this.context = context;
        this.networkTask = networkTask;
        this.wakeLock = wakeLock;
        this.networkManager = createNetworkManager();
        this.notificationHandler = createNotificationHandler();
    }

    public abstract LogEntry execute(NetworkTask networkTask);

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        try {
            boolean isConnectedWithWifi = networkManager.isConnectedWithWiFi();
            boolean isConnected = networkManager.isConnected();
            LogEntry logEntry = checkExecution(isConnectedWithWifi, isConnected);
            if (logEntry == null) {
                logEntry = execute(networkTask);
            } else {
                Log.d(NetworkTaskWorker.class.getName(), "Skipping execution-");
            }
            if (isNetworkTaskValid(networkTask)) {
                Log.d(NetworkTaskWorker.class.getName(), "Writing log entry to database " + logEntry);
                LogDAO logDAO = new LogDAO(getContext());
                logDAO.insertAndDeleteLog(logEntry);
                Log.d(NetworkTaskWorker.class.getName(), "Notify UI");
                sendUINotificationBroadcast();
                sendSystemNotifications(networkTask, logEntry, isConnectedWithWifi, isConnected);
            } else {
                Log.d(NetworkTaskWorker.class.getName(), "Network task does no longer exist. Not writing log.");
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskWorker.class.getName(), "Fatal errror while executing worker and writing log", exc);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                Log.d(NetworkTaskWorker.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
        }
    }

    private void sendUINotificationBroadcast() {
        Log.d(NetworkTaskWorker.class.getName(), "sendUINotificationBroadcast");
        Intent mainUIintent = new Intent(NetworkTaskMainUIBroadcastReceiver.class.getName());
        mainUIintent.putExtras(networkTask.toBundle());
        getContext().sendBroadcast(mainUIintent);
        Intent logUIintent = new Intent(LogEntryUIBroadcastReceiver.class.getName());
        logUIintent.putExtras(networkTask.toBundle());
        getContext().sendBroadcast(logUIintent);
    }

    private void sendSystemNotifications(NetworkTask networkTask, LogEntry logEntry, boolean isConnectedWithWifi, boolean isConnected) {
        Log.d(NetworkTaskWorker.class.getName(), "sendSystemNotifications");
        if (logEntry.isSuccess()) {
            Log.d(NetworkTaskWorker.class.getName(), "Execution was successful. Not sending notifications.");
            return;
        }
        if (!networkTask.isNotification()) {
            Log.d(NetworkTaskWorker.class.getName(), "Notifications for this network task are disabled. Not sending notifications.");
            return;
        }
        if (!isConnectedWithWifi && networkTask.isOnlyWifi()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active wifi connection and network task should only be executed if wifi is active. Not sending notifications.");
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (!isConnected && !preferenceManager.getPreferenceNotificationInactiveNetwork()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active network connection and notifications for inactive networks are disabled. Not sending notifications.");
            return;
        }
        notificationHandler.sendNotification(networkTask, logEntry);
    }

    private LogEntry checkExecution(boolean isConnectedWithWifi, boolean isConnected) {
        Log.d(NetworkTaskWorker.class.getName(), "checkExecution");
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(System.currentTimeMillis());
        if (!isConnected) {
            Log.d(NetworkTaskWorker.class.getName(), "No active network connection.");
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_no_network_connection));
            return logEntry;
        }
        if (!isConnectedWithWifi && networkTask.isOnlyWifi()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active wifi connection and network task should only be executed if wifi is active.");
            logEntry.setSuccess(false);
            logEntry.setMessage(getResources().getString(R.string.text_no_wifi_connection));
            return logEntry;
        }
        Log.d(NetworkTaskWorker.class.getName(), "Everything is ok with the network.");
        return null;
    }

    public InetAddress executeDNSLookup(ExecutorService executorService, String host, LogEntry logEntry, boolean preferIp4) {
        Log.d(NetworkTaskWorker.class.getName(), "executeDNSLookup, host is " + host + ", preferIp4 is " + preferIp4);
        Callable<DNSLookupResult> dnsLookup = getDNSLookup(host);
        int timeout = getResources().getInteger(R.integer.dns_lookup_timeout);
        try {
            Log.d(NetworkTaskWorker.class.getName(), "Executing " + dnsLookup.getClass().getSimpleName() + " with a timeout of " + timeout);
            Future<DNSLookupResult> dnsLookupResultFuture = executorService.submit(dnsLookup);
            DNSLookupResult dnsLookupResult = dnsLookupResultFuture.get(timeout, TimeUnit.SECONDS);
            Log.d(NetworkTaskWorker.class.getName(), dnsLookup.getClass().getSimpleName() + " returned " + dnsLookupResult);
            if (dnsLookupResult.getException() == null) {
                Log.d(NetworkTaskWorker.class.getName(), "DNS lookup was successful");
                List<InetAddress> addresses = dnsLookupResult.getAddresses();
                if (addresses == null || addresses.isEmpty()) {
                    Log.e(NetworkTaskWorker.class.getName(), "DNS lookup returned no addresses");
                    logEntry.setSuccess(false);
                    logEntry.setMessage(getResources().getString(R.string.text_dns_lookup_error, host) + " " + getResources().getString(R.string.text_dns_lookup_no_address));
                } else {
                    Log.d(NetworkTaskWorker.class.getName(), "DNS lookup returned the following addresses " + addresses);
                    InetAddress address = findAddress(addresses, preferIp4);
                    Log.d(NetworkTaskWorker.class.getName(), "Resolved address is " + address);
                    logEntry.setSuccess(true);
                    logEntry.setMessage(getResources().getString(R.string.text_dns_lookup_successful, host, address.getHostAddress()));
                    return address;
                }
            } else {
                Log.d(NetworkTaskWorker.class.getName(), "DNS lookup was not successful because of an exception", dnsLookupResult.getException());
                logEntry.setSuccess(false);
                logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_dns_lookup_error, host), dnsLookupResult.getException(), timeout));
            }
        } catch (Throwable exc) {
            Log.e(NetworkTaskWorker.class.getName(), "Error executing " + dnsLookup.getClass().getName(), exc);
            logEntry.setSuccess(false);
            logEntry.setMessage(getMessageFromException(getResources().getString(R.string.text_dns_lookup_error, host), exc, timeout));
        }
        return null;
    }

    private InetAddress findAddress(List<InetAddress> addresses, boolean preferIp4) {
        Log.d(NetworkTaskWorker.class.getName(), "findAddress, preferIp4 is " + preferIp4);
        for (InetAddress currentAddress : addresses) {
            if (preferIp4 && currentAddress instanceof Inet4Address) {
                return currentAddress;
            } else if (!preferIp4 && currentAddress instanceof Inet6Address) {
                return currentAddress;
            }
        }
        return addresses.get(0);
    }

    protected String getMessageFromException(String prefixMessage, Throwable exc, int timeout) {
        if (isTimeout(exc)) {
            String unit = timeout == 1 ? getResources().getString(R.string.string_second) : getResources().getString(R.string.string_seconds);
            return prefixMessage + " " + getResources().getString(R.string.text_timeout, timeout) + " " + unit + ".";
        }
        return prefixMessage + " " + ExceptionUtil.getLogableMessage(ExceptionUtil.getRootCause(exc));
    }

    private boolean isTimeout(Throwable exc) {
        return exc instanceof TimeoutException || exc instanceof SocketTimeoutException;
    }

    private INetworkManager createNetworkManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNetworkManager(getContext());
    }

    private NotificationHandler createNotificationHandler() {
        return new NotificationHandler(getContext());
    }

    private boolean isNetworkTaskValid(NetworkTask task) {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(task.getId());
        return databaseTask != null && task.getSchedulerId() == databaseTask.getSchedulerId();
    }

    public INetworkManager getNetworkManager() {
        return networkManager;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    protected Callable<DNSLookupResult> getDNSLookup(String host) {
        return new DNSLookup(host);
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return getContext().getResources();
    }
}
