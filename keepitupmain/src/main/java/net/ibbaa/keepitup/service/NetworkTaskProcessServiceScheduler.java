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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.util.NumberUtil;

import java.util.List;

public class NetworkTaskProcessServiceScheduler {

    private final Context context;
    private final NetworkTaskDAO networkTaskDAO;
    private final IAlarmManager alarmManager;
    private final ITimeService timeService;

    private static NetworkTaskProcessPool processPool;

    public enum Delay {
        IMMEDIATE,
        INTERVAL,
        LASTSCHEDULED
    }

    public NetworkTaskProcessServiceScheduler(Context context) {
        this.context = context;
        this.networkTaskDAO = new NetworkTaskDAO(context);
        this.alarmManager = createAlarmManager();
        this.timeService = createTimeService();
    }

    public synchronized static NetworkTaskProcessPool getNetworkTaskProcessPool() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "getNetworkTaskProcessPool");
        if (processPool == null) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "processPool is null. Creating...");
            processPool = new NetworkTaskProcessPool();
        }
        return processPool;
    }

    public NetworkTask start(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Start network task " + networkTask);
        networkTask.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), true);
        networkTask.setFailureCount(0);
        getTimeBasedSuspensionScheduler().start(networkTask);
        return networkTask;
    }

    public NetworkTask schedule(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Schedule network task " + networkTask);
        if (shouldStartForegroundService()) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Starting foreground service...");
            startService(networkTask, Delay.IMMEDIATE);
        } else {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Foreground service will not be started.");
            reschedule(networkTask, Delay.IMMEDIATE);
        }
        return networkTask;
    }

    public NetworkTask reschedule(NetworkTask networkTask, Delay delay) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Reschedule network task " + networkTask + ", delay is " + delay);
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
        if (databaseTask == null) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task does no longer exist. Skipping reschedule.");
            terminate(networkTask);
            return networkTask;
        }
        if (!databaseTask.isRunning()) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task is no longer running. Skipping reschedule.");
            terminate(networkTask);
            return networkTask;
        }
        if (databaseTask.getSchedulerId() != networkTask.getSchedulerId()) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task was updated. Skipping reschedule.");
            terminate(networkTask);
            return networkTask;
        }
        long delayMillis;
        PendingIntent pendingIntent = createPendingIntent(databaseTask);
        if (Delay.IMMEDIATE.equals(delay)) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Delay is IMMEDIATE. Scheduling alarm immediately.");
            delayMillis = 0;
        } else if (Delay.INTERVAL.equals(delay)) {
            delayMillis = getIntervalMilliseconds(networkTask);
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Delay is INTERVAL. Scheduling alarm with delay of " + delayMillis + " msec");
        } else if (Delay.LASTSCHEDULED.equals(delay)) {
            delayMillis = getLastScheduledMilliseconds(networkTask);
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Delay is LASTSCHEDULED. Scheduling alarm with delay of " + delayMillis + " msec");
        } else {
            Log.e(NetworkTaskProcessServiceScheduler.class.getName(), "Delay is undefined. Scheduling alarm immediately.");
            delayMillis = 0;
        }
        alarmManager.setAlarm(delayMillis, pendingIntent);
        return networkTask;
    }

    public NetworkTask cancel(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling network task " + networkTask);
        synchronized (TimeBasedSuspensionScheduler.LOCK) {
            networkTask.setRunning(false);
            networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
            networkTask.setLastScheduled(-1);
            terminate(networkTask);
            if (!areNetworkTasksRunning()) {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "No running tasks. Stopping time bases scheduler.");
                getTimeBasedSuspensionScheduler().stop();
                if (shouldStartForegroundService()) {
                    Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "No running tasks. Stopping service.");
                    Intent intent = new Intent(getContext(), NetworkTaskRunningNotificationService.class);
                    intent.setPackage(getContext().getPackageName());
                    intent.putExtras(networkTask.toBundle());
                    getContext().stopService(intent);
                }
            }
            return networkTask;
        }
    }

    public boolean areNetworkTasksRunning() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "areNetworkTasksRunning");
        int running = networkTaskDAO.readNetworkTasksRunning();
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Running tasks: " + running);
        return running > 0;
    }

    public NetworkTask terminate(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Terminating network task " + networkTask);
        if (hasPendingIntent(networkTask)) {
            PendingIntent pendingIntent = getPendingIntent(networkTask);
            alarmManager.cancelAlarm(pendingIntent);
            pendingIntent.cancel();
        }
        getNetworkTaskProcessPool().cancel(networkTask.getSchedulerId());
        return networkTask;
    }

    public void startup() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Starting all network tasks marked as running.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Database returned the following network tasks: " + (networkTasks.isEmpty() ? "no network tasks" : networkTasks));
        for (NetworkTask currentTask : networkTasks) {
            if (currentTask.isRunning()) {
                if (shouldStartForegroundService()) {
                    Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Starting foreground service...");
                    startService(currentTask, Delay.LASTSCHEDULED);
                } else {
                    Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Foreground service will not be started.");
                    reschedule(currentTask, Delay.LASTSCHEDULED);
                }
            } else {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task " + currentTask + " is not marked as running.");
                networkTaskDAO.resetNetworkTaskInstances(currentTask.getId());
                networkTaskDAO.resetNetworkTaskLastScheduled(currentTask.getId());
            }
        }
    }

    public void cancelAll() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling all network tasks.");
        synchronized (TimeBasedSuspensionScheduler.LOCK) {
            List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
            for (NetworkTask currentTask : networkTasks) {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling network task " + currentTask);
                cancel(currentTask);
            }
        }
    }

    public void suspendAll() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Suspending all network tasks.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        for (NetworkTask currentTask : networkTasks) {
            terminate(currentTask);
        }
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Stopping foreground service...");
        Intent intent = new Intent(getContext(), NetworkTaskRunningNotificationService.class);
        intent.setPackage(getContext().getPackageName());
        getContext().stopService(intent);
    }

    public void terminateAll() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Terminating all network tasks.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        for (NetworkTask currentTask : networkTasks) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Terminating network task " + currentTask);
            terminate(currentTask);
        }
        networkTaskDAO.resetAllNetworkTaskInstances();
    }

    public IAlarmManager getAlarmManager() {
        return alarmManager;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private boolean hasPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskProcessBroadcastReceiver.class);
        intent.setPackage(getContext().getPackageName());
        intent.putExtras(networkTask.toBundle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE) != null;
        } else {
            return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_NO_CREATE) != null;
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskProcessBroadcastReceiver.class);
        intent.setPackage(getContext().getPackageName());
        intent.putExtras(networkTask.toBundle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        } else {
            return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_NO_CREATE);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent createPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskProcessBroadcastReceiver.class);
        intent.setPackage(getContext().getPackageName());
        intent.putExtras(networkTask.toBundle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60L * 1000 * networkTask.getInterval();
    }

    private long getLastScheduledMilliseconds(NetworkTask networkTask) {
        long lastScheduled = networkTask.getLastScheduled();
        if (lastScheduled < 0) {
            return 0;
        }
        long timestamp = timeService.getCurrentTimestamp();
        long interval = getIntervalMilliseconds(networkTask);
        long timeDifference = NumberUtil.ensurePositive(timestamp - lastScheduled);
        return NumberUtil.ensurePositive(interval - timeDifference);
    }

    public void restartForegroundService() {
        restartForegroundService(false);
    }

    public void restartForegroundService(boolean withAlarm) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "startServiceDelayed");
        if (shouldStartForegroundService()) {
            try {
                Intent intent = new Intent(getContext(), NetworkTaskRunningNotificationService.class);
                intent.putExtra(NetworkTaskRunningNotificationService.getWithAlarmKey(), withAlarm);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startForegroundService(intent);
                } else {
                    getContext().startService(intent);
                }
            } catch (Exception exc) {
                Log.e(NetworkTaskProcessServiceScheduler.class.getName(), "startServiceDelayed: Error starting the foreground service.", exc);
            }
        }
    }

    private void startService(NetworkTask task, Delay delay) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "startService for network task " + task + " with delay " + delay);
        try {
            Intent intent = new Intent(getContext(), NetworkTaskRunningNotificationService.class);
            intent.setPackage(getContext().getPackageName());
            intent.putExtras(task.toBundle());
            intent.putExtra(NetworkTaskRunningNotificationService.getRescheduleDelayKey(), delay.name());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(intent);
            } else {
                getContext().startService(intent);
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskProcessServiceScheduler.class.getName(), "startService: Error starting the foreground service.", exc);
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Scheduling without service");
            reschedule(task, delay);
            if (isForegroundServiceStartNotAllowedException(exc)) {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Sending notification to open app");
                createNotificationHandler().sendMessageNotificationForegroundStart();
            }
        }
    }

    private boolean isForegroundServiceStartNotAllowedException(Exception exc) {
        return exc.getClass().getName().equals("android.app.ForegroundServiceStartNotAllowedException");
    }

    private boolean shouldStartForegroundService() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "shouldStartForegroundService");
        boolean useForegroundService = getContext().getResources().getBoolean(R.bool.worker_use_foreground_service);
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "useForegroundService is " + useForegroundService);
        boolean hasPostNotificationsPermission = getPermissionManager().hasPostNotificationsPermission(context);
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "hasPostNotificationsPermission is " + hasPostNotificationsPermission);
        boolean shouldStartForegroundService = useForegroundService && hasPostNotificationsPermission;
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "shouldStartForegroundService is " + shouldStartForegroundService);
        return shouldStartForegroundService;
    }

    private IAlarmManager createAlarmManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createAlarmManager(getContext());
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private NotificationHandler createNotificationHandler() {
        return new NotificationHandler(getContext(), getPermissionManager());
    }

    public ITimeService getTimeService() {
        return timeService;
    }

    public IPermissionManager getPermissionManager() {
        return new PermissionManager();
    }

    public TimeBasedSuspensionScheduler getTimeBasedSuspensionScheduler() {
        return new TimeBasedSuspensionScheduler(getContext());
    }

    private Context getContext() {
        return context;
    }
}
