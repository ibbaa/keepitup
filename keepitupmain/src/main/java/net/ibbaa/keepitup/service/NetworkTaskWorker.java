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
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.logging.NetworkTaskLog;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.network.DNSLookup;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.ui.sync.LogEntryUIBroadcastReceiver;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;
import net.ibbaa.keepitup.util.ExceptionUtil;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class NetworkTaskWorker implements Runnable {

    private final static String LOG_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

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

    public abstract ExecutionResult execute(NetworkTask networkTask);

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
            LogDAO logDAO = new LogDAO(getContext());
            NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
            if (databaseTask == null) {
                Log.d(NetworkTaskWorker.class.getName(), "NetworkTask not found in database.");
                return;
            }
            Log.d(NetworkTaskWorker.class.getName(), "Updating last scheduled time.");
            long timestamp = timeService.getCurrentTimestamp();
            networkTaskDAO.updateNetworkTaskLastScheduled(networkTask.getId(), timestamp);
            SimpleDateFormat logTimestampDateFormat = new SimpleDateFormat(LOG_TIMESTAMP_PATTERN, Locale.US);
            Log.d(NetworkTaskWorker.class.getName(), "Updated last scheduled timestamp to " + timestamp + " (" + logTimestampDateFormat.format(timestamp) + ")");
            LogEntry logEntry = checkInstances();
            if (logEntry != null) {
                Log.d(NetworkTaskWorker.class.getName(), "Skipping execution. Too many active instances.");
                writeLogEntry(databaseTask, logEntry, false);
                return;
            }
            Log.d(NetworkTaskWorker.class.getName(), "Increasing instances count.");
            networkTaskDAO.increaseNetworkTaskInstances(networkTask.getId());
            sendNetworkTaskUINotificationBroadcast();
            LogEntry lastLogEntry = logDAO.readMostRecentLogForNetworkTask(networkTask.getId());
            boolean lastSuccessful = lastLogEntry == null || lastLogEntry.isSuccess();
            try {
                boolean isConnectedWithWifi = networkManager.isConnectedWithWiFi();
                boolean isConnected = networkManager.isConnected();
                Log.d(NetworkTaskWorker.class.getName(), "isConnectedWithWifi: " + isConnectedWithWifi);
                Log.d(NetworkTaskWorker.class.getName(), "isConnectedWithWifi: " + isConnected);
                logEntry = checkNetwork(isConnectedWithWifi, isConnected);
                if (logEntry != null) {
                    Log.d(NetworkTaskWorker.class.getName(), "Skipping execution because of the network state.");
                    writeLogEntry(databaseTask, logEntry, shouldSendErrorNotification(isConnectedWithWifi, isConnected, lastSuccessful));
                    return;
                }
                Log.d(NetworkTaskWorker.class.getName(), "Executing task...");
                ExecutionResult executionResult = execute(networkTask);
                Log.d(NetworkTaskWorker.class.getName(), "The executed task returned " + executionResult);
                if (isNetworkTaskValid(databaseTask)) {
                    logEntry = executionResult.getLogEntry();
                    writeLogEntry(databaseTask, logEntry, shouldSendNotification(executionResult, lastSuccessful));
                } else {
                    Log.d(NetworkTaskWorker.class.getName(), "Network task does no longer exist. Not writing log.");
                }
            } finally {
                Log.d(NetworkTaskWorker.class.getName(), "Decreasing instances count.");
                networkTaskDAO.decreaseNetworkTaskInstances(networkTask.getId());
                sendNetworkTaskUINotificationBroadcast();
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

    private void writeLogEntry(NetworkTask databaseTask, LogEntry logEntry, boolean sendNotification) {
        Log.d(NetworkTaskWorker.class.getName(), "Writing log entry " + logEntry + " to database, sendNotification is " + sendNotification);
        LogDAO logDAO = new LogDAO(getContext());
        logDAO.insertAndDeleteLog(logEntry);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (preferenceManager.getPreferenceLogFile()) {
            Log.d(NetworkTaskWorker.class.getName(), "Writing log entry " + logEntry + " to file, sendNotification is " + sendNotification);
            NetworkTaskLog.log(getContext(), databaseTask, logEntry);
        }
        Log.d(NetworkTaskWorker.class.getName(), "Notify UI");
        sendNetworkTaskUINotificationBroadcast();
        sendLogEntryUINotificationBroadcast();
        if (sendNotification) {
            sendNotification(logEntry);
        }
    }

    private void sendNetworkTaskUINotificationBroadcast() {
        Log.d(NetworkTaskWorker.class.getName(), "sendNetworkTaskUINotificationBroadcast");
        Intent mainUIintent = new Intent(NetworkTaskMainUIBroadcastReceiver.class.getName());
        mainUIintent.setPackage(getContext().getPackageName());
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask readNetworkTask = networkTaskDAO.readNetworkTask(networkTask.getId());
        mainUIintent.putExtras(readNetworkTask.toBundle());
        getContext().sendBroadcast(mainUIintent);
    }

    private void sendLogEntryUINotificationBroadcast() {
        Log.d(NetworkTaskWorker.class.getName(), "sendLogEntryUINotificationBroadcast");
        Intent logUIintent = new Intent(LogEntryUIBroadcastReceiver.class.getName());
        logUIintent.setPackage(getContext().getPackageName());
        logUIintent.putExtras(networkTask.toBundle());
        getContext().sendBroadcast(logUIintent);
    }

    private boolean shouldSendNotification(ExecutionResult executionResult, boolean lastSuccessful) {
        Log.d(NetworkTaskWorker.class.getName(), "shouldSendNotification");
        if (!networkTask.isNotification()) {
            Log.d(NetworkTaskWorker.class.getName(), "Notifications for this network task are disabled. Not sending notifications. Returning false.");
            return false;
        }
        if (executionResult.isInterrupted()) {
            Log.d(NetworkTaskWorker.class.getName(), "Execution was interrupted. Returning false.");
            return false;
        }
        boolean successful = executionResult.getLogEntry().isSuccess();
        Log.d(NetworkTaskWorker.class.getName(), "lastSuccessful is " + lastSuccessful);
        Log.d(NetworkTaskWorker.class.getName(), "successful is " + successful);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (NotificationType.CHANGE.equals(preferenceManager.getPreferenceNotificationType())) {
            Log.d(NetworkTaskWorker.class.getName(), "NotificationType is CHANGE. Returning " + (lastSuccessful != successful) + ".");
            return lastSuccessful != successful;
        }
        Log.d(NetworkTaskWorker.class.getName(), "NotificationType is FAILURE. Returning " + !successful + ".");
        return !successful;
    }

    private boolean shouldSendErrorNotification(boolean isConnectedWithWifi, boolean isConnected, boolean lastSuccessful) {
        Log.d(NetworkTaskWorker.class.getName(), "shouldSendErrorNotification");
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
        if (NotificationType.CHANGE.equals(preferenceManager.getPreferenceNotificationType())) {
            Log.d(NetworkTaskWorker.class.getName(), "NotificationType is CHANGE. Returning " + lastSuccessful + ".");
            return lastSuccessful;
        }
        Log.d(NetworkTaskWorker.class.getName(), "NotificationType is FAILURE. Returning true.");
        return true;
    }

    private void sendNotification(LogEntry logEntry) {
        Log.d(NetworkTaskWorker.class.getName(), "sendErrorNotification for log entry " + logEntry);
        notificationHandler.sendMessageNotification(networkTask, logEntry);
    }

    private LogEntry checkInstances() {
        Log.d(NetworkTaskWorker.class.getName(), "checkInstances");
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(networkTask.getId());
        logEntry.setTimestamp(getTimeService().getCurrentTimestamp());
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(networkTask.getId());
        int maxInstances = getMaxInstances();
        Log.d(NetworkTaskWorker.class.getName(), "Currently active instances: " + activeInstances);
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

    public DNSExecutionResult executeDNSLookup(String host, boolean preferIp4) {
        Log.d(NetworkTaskWorker.class.getName(), "executeDNSLookup, host is " + host + ", preferIp4 is " + preferIp4);
        Callable<DNSLookupResult> dnsLookup = getDNSLookup(host);
        int timeout = getResources().getInteger(R.integer.dns_lookup_timeout);
        Log.d(NetworkTaskWorker.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<DNSLookupResult> dnsLookupResultFuture = null;
        LogEntry logEntry = new LogEntry();
        boolean interrupted = false;
        try {
            Log.d(NetworkTaskWorker.class.getName(), "Executing " + dnsLookup.getClass().getSimpleName() + " with a timeout of " + timeout);
            dnsLookupResultFuture = executorService.submit(dnsLookup);
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
                    return new DNSExecutionResult(false, logEntry, address);
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
            if (dnsLookupResultFuture != null && isInterrupted(exc)) {
                Log.d(NetworkTaskWorker.class.getName(), "Cancelling " + dnsLookup.getClass().getSimpleName());
                dnsLookupResultFuture.cancel(true);
                interrupted = true;
            }
        } finally {
            Log.d(NetworkTaskWorker.class.getName(), "Shutting down ExecutorService");
            executorService.shutdownNow();
        }
        return new DNSExecutionResult(interrupted, logEntry, null);
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
        if (isInterrupted(exc)) {
            String unit = timeout == 1 ? getResources().getString(R.string.string_second) : getResources().getString(R.string.string_seconds);
            return prefixMessage + " " + getResources().getString(R.string.text_interrupted);
        }
        return prefixMessage + " " + ExceptionUtil.getLogableMessage(ExceptionUtil.getRootCause(exc));
    }

    private boolean isTimeout(Throwable exc) {
        return exc instanceof TimeoutException;
    }

    protected boolean isInterrupted(Throwable exc) {
        return exc instanceof InterruptedException;
    }

    private INetworkManager createNetworkManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNetworkManager(getContext());
    }

    private NotificationHandler createNotificationHandler() {
        return new NotificationHandler(getContext(), getPermissionManager());
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private boolean isNetworkTaskValid(NetworkTask databaseTask) {
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

    public IPermissionManager getPermissionManager() {
        return new PermissionManager();
    }

    public static class DNSExecutionResult extends ExecutionResult {

        private final InetAddress address;

        public DNSExecutionResult(boolean interrupted, LogEntry logEntry, InetAddress address) {
            super(interrupted, logEntry);
            this.address = address;
        }

        public InetAddress getAddress() {
            return address;
        }

        @NonNull
        @Override
        public String toString() {
            return "DNSExecutionResult{" +
                    "address=" + address +
                    ", interrupted=" + isInterrupted() +
                    ", logEntry=" + getLogEntry() +
                    '}';
        }
    }

    public static class ExecutionResult {

        private final boolean interrupted;
        private final LogEntry logEntry;

        public ExecutionResult(boolean interrupted, LogEntry logEntry) {
            this.interrupted = interrupted;
            this.logEntry = logEntry;
        }

        public boolean isInterrupted() {
            return interrupted;
        }

        public LogEntry getLogEntry() {
            return logEntry;
        }

        @NonNull
        @Override
        public String toString() {
            return "ExecutionResult{" +
                    "interrupted=" + interrupted +
                    ", logEntry=" + logEntry +
                    '}';
        }
    }
}
