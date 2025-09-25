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
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.AccessTypeDataDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.logging.NetworkTaskLog;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.alarm.AlarmService;
import net.ibbaa.keepitup.service.network.DNSLookup;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.IStoragePermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.ui.permission.StoragePermissionManager;
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

@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
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

    public abstract ExecutionResult execute(NetworkTask networkTask, AccessTypeData data);

    @Override
    public void run() {
        Log.d(NetworkTaskWorker.class.getName(), "Executing worker thread for " + networkTask);
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
            AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(getContext());
            NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
            if (isNetworkTaskInvalid(databaseTask)) {
                Log.d(NetworkTaskWorker.class.getName(), "NetworkTask is invalid. Skipping.");
                return;
            }
            AccessTypeData databaseAccessTypeData = accessTypeDataDAO.readAccessTypeDataForNetworkTask(networkTask.getId());
            if (databaseAccessTypeData == null) {
                Log.d(NetworkTaskWorker.class.getName(), "AccessTypeData for network task " + databaseTask + " not found in database.");
                databaseAccessTypeData = new AccessTypeData(getContext());
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
                sendNetworkTaskUINotificationBroadcast(databaseTask);
                return;
            }
            Log.d(NetworkTaskWorker.class.getName(), "Increasing instances count.");
            networkTaskDAO.increaseNetworkTaskInstances(networkTask.getId());
            sendNetworkTaskUINotificationBroadcast(databaseTask);
            try {
                boolean isConnectedWithWifi = networkManager.isConnectedWithWiFi();
                boolean isConnected = networkManager.isConnected();
                Log.d(NetworkTaskWorker.class.getName(), "isConnectedWithWifi: " + isConnectedWithWifi);
                Log.d(NetworkTaskWorker.class.getName(), "isConnectedWithWifi: " + isConnected);
                logEntry = checkNetwork(isConnectedWithWifi, isConnected);
                if (logEntry != null) {
                    Log.d(NetworkTaskWorker.class.getName(), "Skipping execution because of the network state.");
                    int oldFailureCount = networkTaskDAO.readNetworkTaskFailureCount(networkTask.getId());
                    int newFailureCount = adaptFailureCount(networkTask, oldFailureCount, logEntry, false, networkTaskDAO, isConnectedWithWifi, isConnected);
                    writeLogEntry(databaseTask, logEntry, shouldSendNotification(oldFailureCount, newFailureCount));
                    return;
                }
                Log.d(NetworkTaskWorker.class.getName(), "Executing task...");
                ExecutionResult executionResult = execute(networkTask, databaseAccessTypeData);
                Log.d(NetworkTaskWorker.class.getName(), "The executed task returned " + executionResult);
                logEntry = executionResult.getLogEntry();
                int oldFailureCount = networkTaskDAO.readNetworkTaskFailureCount(networkTask.getId());
                int newFailureCount = adaptFailureCount(networkTask, oldFailureCount, logEntry, executionResult.isInterrupted(), networkTaskDAO, true, true);
                writeLogEntry(databaseTask, logEntry, shouldSendNotification(oldFailureCount, newFailureCount));
            } finally {
                Log.d(NetworkTaskWorker.class.getName(), "Decreasing instances count.");
                networkTaskDAO.decreaseNetworkTaskInstances(networkTask.getId());
                sendNetworkTaskUINotificationBroadcast(databaseTask);
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskWorker.class.getName(), "Fatal error while executing worker and writing log", exc);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                Log.d(NetworkTaskWorker.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
        }
    }

    private void writeLogEntry(NetworkTask task, LogEntry logEntry, boolean sendNotification) {
        Log.d(NetworkTaskWorker.class.getName(), "Writing log entry " + logEntry + " to database, sendNotification is " + sendNotification);
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(task.getId());
        if (isNetworkTaskInvalid(databaseTask)) {
            Log.d(NetworkTaskWorker.class.getName(), "NetworkTask is invalid. Not writing log entry.");
            return;
        }
        LogDAO logDAO = new LogDAO(getContext());
        logDAO.insertAndDeleteLog(logEntry);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (preferenceManager.getPreferenceLogFile()) {
            Log.d(NetworkTaskWorker.class.getName(), "Writing log entry " + logEntry + " to file");
            if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
                if (checkArbitraryLogFolderPermission(preferenceManager)) {
                    NetworkTaskLog.log(getContext(), task, logEntry);
                } else {
                    getNotificationHandler().sendMessageNotificationMissingLogFolderPermission();
                }
            } else {
                NetworkTaskLog.log(getContext(), task, logEntry);
            }
        }
        Log.d(NetworkTaskWorker.class.getName(), "Notify UI");
        sendLogEntryUINotificationBroadcast(databaseTask);
        if (sendNotification) {
            sendNotification(databaseTask, logEntry);
        }
    }

    private boolean checkArbitraryLogFolderPermission(PreferenceManager preferenceManager) {
        Log.d(NetworkTaskWorker.class.getName(), "checkArbitraryLogFolderPermission");
        String arbitraryLogFolder = preferenceManager.getPreferenceArbitraryLogFolder();
        if (getDocumentManager().getArbitraryDirectory(arbitraryLogFolder) == null) {
            Log.e(NetworkTaskWorker.class.getName(), "Error accessing folder " + arbitraryLogFolder);
            return false;
        }
        return getStoragePermissionManager().hasPersistentPermission(getContext(), arbitraryLogFolder);
    }

    private void sendNetworkTaskUINotificationBroadcast(NetworkTask task) {
        Log.d(NetworkTaskWorker.class.getName(), "sendNetworkTaskUINotificationBroadcast for task " + task);
        Intent mainUIintent = new Intent(NetworkTaskMainUIBroadcastReceiver.class.getName());
        mainUIintent.setPackage(getContext().getPackageName());
        mainUIintent.putExtras(task.toBundle());
        getContext().sendBroadcast(mainUIintent);
    }

    private void sendLogEntryUINotificationBroadcast(NetworkTask task) {
        Log.d(NetworkTaskWorker.class.getName(), "sendLogEntryUINotificationBroadcast for task " + task);
        Intent logUIintent = new Intent(LogEntryUIBroadcastReceiver.class.getName());
        logUIintent.setPackage(getContext().getPackageName());
        logUIintent.putExtras(task.toBundle());
        getContext().sendBroadcast(logUIintent);
    }

    private int adaptFailureCount(NetworkTask databaseTask, int oldFailureCount, LogEntry logEntry, boolean interrupted, NetworkTaskDAO networkTaskDAO, boolean isConnectedWithWifi, boolean isConnected) {
        Log.d(NetworkTaskWorker.class.getName(), "adaptFailureCount");
        if (logEntry.isSuccess()) {
            Log.d(NetworkTaskWorker.class.getName(), "Execution was successful. Resetting failure count.");
            networkTaskDAO.resetNetworkTaskFailureCount(databaseTask.getId());
            return 0;
        }
        if (!isConnectedWithWifi && databaseTask.isOnlyWifi()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active wifi connection and network task should only be executed if wifi is active. Not increasing failure count.");
            return oldFailureCount;
        }
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (!isConnected && !preferenceManager.getPreferenceNotificationInactiveNetwork()) {
            Log.d(NetworkTaskWorker.class.getName(), "No active network connection and notifications for inactive networks are disabled. Not increasing failure count.");
            return oldFailureCount;
        }
        if (interrupted) {
            Log.d(NetworkTaskWorker.class.getName(), "Execution was interrupted. Not increasing failure count.");
            return oldFailureCount;
        }
        Log.d(NetworkTaskWorker.class.getName(), "Execution was not successful. Increasing failure count.");
        networkTaskDAO.increaseNetworkTaskFailureCount(databaseTask.getId());
        return oldFailureCount + 1;
    }

    private boolean shouldSendNotification(int oldFailureCount, int newFailureCount) {
        Log.d(NetworkTaskWorker.class.getName(), "shouldSendNotification, oldFailureCount is " + oldFailureCount + ", newFailureCount is " + newFailureCount);
        if (!networkTask.isNotification()) {
            Log.d(NetworkTaskWorker.class.getName(), "Notifications for this network task are disabled. Not sending notifications. Returning false.");
            return false;
        }
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (NotificationType.CHANGE.equals(preferenceManager.getPreferenceNotificationType())) {
            Log.d(NetworkTaskWorker.class.getName(), "NotificationType is CHANGE.");
            if (newFailureCount == 0) {
                return oldFailureCount > 0;
            }
            return oldFailureCount == 0;
        }
        Log.d(NetworkTaskWorker.class.getName(), "NotificationType is FAILURE.");
        int notificationAfterFailure = preferenceManager.getPreferenceNotificationAfterFailures();
        return (newFailureCount > oldFailureCount) && (newFailureCount % notificationAfterFailure == 0);
    }

    private void sendNotification(NetworkTask task, LogEntry logEntry) {
        Log.d(NetworkTaskWorker.class.getName(), "sendNotification for network task " + task + " and log entry " + logEntry);
        if (!getPermissionManager().hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot send notification because of missing permission.");
            return;
        }
        notificationHandler.sendMessageNotificationForNetworkTask(task, logEntry);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (task.isHighPrio() && preferenceManager.getPreferenceAlarmOnHighPrio()) {
            Log.d(NetworkTaskWorker.class.getName(), "Starting alarm service...");
            Intent intent = new Intent(getContext(), AlarmService.class);
            intent.putExtra(AlarmService.getNetworkTaskBundleKey(), task.toBundle());
            intent.setPackage(getContext().getPackageName());
            getContext().startService(intent);
        }
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

    @SuppressWarnings("resource")
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
            String unit = getResources().getQuantityString(R.plurals.string_second, timeout);
            return prefixMessage + " " + getResources().getString(R.string.text_timeout, timeout) + " " + unit + ".";
        }
        if (isInterrupted(exc)) {
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

    private boolean isNetworkTaskInvalid(NetworkTask databaseTask) {
        return databaseTask == null || networkTask.getSchedulerId() != databaseTask.getSchedulerId();
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

    public IDocumentManager getDocumentManager() {
        return new SystemDocumentManager(getContext());
    }

    public IStoragePermissionManager getStoragePermissionManager() {
        return new StoragePermissionManager();
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
