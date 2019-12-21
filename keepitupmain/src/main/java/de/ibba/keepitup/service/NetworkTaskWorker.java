package de.ibba.keepitup.service;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Log;
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
    private final ITimeService timeService;

    public NetworkTaskWorker(Context context, NetworkTask networkTask, PowerManager.WakeLock wakeLock) {
        this.context = context;
        this.networkTask = networkTask;
        this.wakeLock = wakeLock;
        this.networkManager = createNetworkManager();
        this.notificationHandler = createNotificationHandler();
        this.timeService = createTimeService();
    }

    public abstract int getMaxInstances();

    public abstract String getMaxInstancesErrorMessage(int activeInstances);

    public abstract LogEntry execute(NetworkTask networkTask);

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        try {
            LogEntry logEntry = checkInstances();
            if (logEntry != null) {
                Log.d(NetworkTaskWorker.class.getName(), "Skipping execution. Too many active instances.");
                writeLogEntry(logEntry, false);
                return;
            }
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
            Log.d(NetworkTaskWorker.class.getName(), "Increasing instances count.");
            networkTaskDAO.increaseNetworkTaskInstances(networkTask.getSchedulerId());
            try {
                boolean isConnectedWithWifi = networkManager.isConnectedWithWiFi();
                boolean isConnected = networkManager.isConnected();
                Log.d(NetworkTaskWorker.class.getName(), "isConnectedWithWifi: " + isConnectedWithWifi);
                Log.d(NetworkTaskWorker.class.getName(), "isConnectedWithWifi: " + isConnected);
                logEntry = checkNetwork(isConnectedWithWifi, isConnected);
                if (logEntry != null) {
                    Log.d(NetworkTaskWorker.class.getName(), "Skipping execution because of the network state.");
                    writeLogEntry(logEntry, shouldSendSystemNotification(isConnectedWithWifi, isConnected));
                    return;
                }
                Log.d(NetworkTaskWorker.class.getName(), "Executing task...");
                logEntry = execute(networkTask);
                Log.d(NetworkTaskWorker.class.getName(), "Th executed task returned " + logEntry);
                if (isNetworkTaskValid()) {
                    writeLogEntry(logEntry, shouldSendSystemNotification(logEntry));
                } else {
                    Log.d(NetworkTaskWorker.class.getName(), "Network task does no longer exist. Not writing log.");
                }
            } finally {
                Log.d(NetworkTaskWorker.class.getName(), "Decreasing instances count.");
                networkTaskDAO.decreaseNetworkTaskInstances(networkTask.getSchedulerId());
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

    private void writeLogEntry(LogEntry logEntry, boolean sendSystemNotifiaction) {
        Log.d(NetworkTaskWorker.class.getName(), "Writing log entry " + logEntry + " to database, sendSystemNotifiaction is " + sendSystemNotifiaction);
        LogDAO logDAO = new LogDAO(getContext());
        logDAO.insertAndDeleteLog(logEntry);
        Log.d(NetworkTaskWorker.class.getName(), "Notify UI");
        sendUINotificationBroadcast();
        if (sendSystemNotifiaction) {
            sendSystemNotifications(logEntry);
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

    private boolean shouldSendSystemNotification(LogEntry logEntry) {
        Log.d(NetworkTaskWorker.class.getName(), "shouldSendSystemNotification");
        if (logEntry.isSuccess()) {
            Log.d(NetworkTaskWorker.class.getName(), "Execution was successful. Returning false.");
            return false;
        }
        if (!networkTask.isNotification()) {
            Log.d(NetworkTaskWorker.class.getName(), "Notifications for this network task are disabled. Not sending notifications. Returning false.");
            return false;
        }
        Log.d(NetworkTaskWorker.class.getName(), "Returning true.");
        return true;
    }

    private boolean shouldSendSystemNotification(boolean isConnectedWithWifi, boolean isConnected) {
        Log.d(NetworkTaskWorker.class.getName(), "shouldSendSystemNotifiaction");
        if (!networkTask.isNotification()) {
            Log.d(NetworkTaskWorker.class.getName(), "Notifications for this network task are disabled. Returning false.");
            return false;
        }
        if (!isConnectedWithWifi && networkTask.isOnlyWifi()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active wifi connection and network task should only be executed if wifi is active. Returning false.");
            return false;
        }
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (!isConnected && !preferenceManager.getPreferenceNotificationInactiveNetwork()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active network connection and notifications for inactive networks are disabled. Returning false.");
            return false;
        }
        Log.d(NetworkTaskWorker.class.getName(), "Returning true.");
        return true;
    }

    private void sendSystemNotifications(LogEntry logEntry) {
        Log.d(NetworkTaskWorker.class.getName(), "sendSystemNotifications for log entry " + logEntry);
        notificationHandler.sendNotification(networkTask, logEntry);
    }

    private LogEntry checkInstances() {
        Log.d(NetworkTaskWorker.class.getName(), "checkInstances");
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(networkTask.getSchedulerId());
        int maxInstances = getMaxInstances();
        Log.d(NetworkTaskWorker.class.getName(), "Currenty active instances: " + activeInstances);
        Log.d(NetworkTaskWorker.class.getName(), "Max active instances: " + maxInstances);
        if (activeInstances >= maxInstances) {
            Log.d(NetworkTaskWorker.class.getName(), "Too many active instances.");
            logEntry.setSuccess(false);
            logEntry.setMessage(getMaxInstancesErrorMessage(activeInstances));
            return logEntry;
        }
        Log.d(NetworkTaskWorker.class.getName(), "Active instances do not exceed the maximum.");
        return null;
    }

    private LogEntry checkNetwork(boolean isConnectedWithWifi, boolean isConnected) {
        Log.d(NetworkTaskWorker.class.getName(), "checkNetwork");
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
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

    public InetAddress executeDNSLookup(String host, LogEntry logEntry, boolean preferIp4) {
        Log.d(NetworkTaskWorker.class.getName(), "executeDNSLookup, host is " + host + ", preferIp4 is " + preferIp4);
        Callable<DNSLookupResult> dnsLookup = getDNSLookup(host);
        int timeout = getResources().getInteger(R.integer.dns_lookup_timeout);
        Log.d(NetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
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
        } finally {
            Log.d(NetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
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

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private boolean isNetworkTaskValid() {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
        return databaseTask != null && networkTask.getSchedulerId() == databaseTask.getSchedulerId();
    }

    public INetworkManager getNetworkManager() {
        return networkManager;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public ITimeService getTimeService() {
        return timeService;
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
