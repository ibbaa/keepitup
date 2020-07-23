package de.ibba.keepitup.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.util.NumberUtil;

public class NetworkTaskProcessServiceScheduler {

    private final static String LOG_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private final Context context;
    private final NetworkTaskDAO networkTaskDAO;
    private final IAlarmManager alarmManager;
    private final ITimeService timeService;

    private static NetworkTaskProcessPool processPool;

    public static enum Delay {
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
        if (processPool == null) {
            processPool = new NetworkTaskProcessPool();
        }
        return processPool;
    }

    public NetworkTask schedule(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Schedule network task " + networkTask);
        networkTask.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), true);
        reschedule(networkTask, Delay.IMMEDIATE);
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
        PendingIntent pendingIntent = createPendingIntent(networkTask);
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
        long timestamp = timeService.getCurrentTimestamp();
        SimpleDateFormat logTimestampDateFormat = new SimpleDateFormat(LOG_TIMESTAMP_PATTERN, Locale.US);
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Updated last scheduled timestamp to " + timestamp + " (" + logTimestampDateFormat.format(timestamp) + ")");
        return networkTask;
    }

    public NetworkTask cancel(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling network task " + networkTask);
        networkTask.setRunning(false);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
        networkTask.setLastScheduled(-1);
        return terminate(networkTask);
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
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Database returned the following network tasks: " + (networkTasks.isEmpty() ? "no network tasks" : ""));
        for (NetworkTask currentTask : networkTasks) {
            if (currentTask.isRunning()) {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task " + currentTask + " is marked as running. Rescheduling...");
                networkTaskDAO.resetNetworkTaskInstances(currentTask.getId());
                reschedule(currentTask, Delay.LASTSCHEDULED);
            } else {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task " + currentTask + " is not marked as running.");
                networkTaskDAO.resetNetworkTaskInstances(currentTask.getId());
                networkTaskDAO.resetNetworkTaskLastScheduled(currentTask.getId());
            }
        }
    }

    public void cancelAll() {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling all network tasks.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Database returned the following network tasks: " + (networkTasks.isEmpty() ? "no network tasks" : ""));
        for (NetworkTask currentTask : networkTasks) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling network task " + currentTask);
            cancel(currentTask);
        }
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

    private boolean hasPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskProcessBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private PendingIntent getPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskProcessBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_NO_CREATE);
    }

    private PendingIntent createPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskProcessBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60 * 1000 * networkTask.getInterval();
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

    private IAlarmManager createAlarmManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createAlarmManager(getContext());
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    public ITimeService getTimeService() {
        return timeService;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
