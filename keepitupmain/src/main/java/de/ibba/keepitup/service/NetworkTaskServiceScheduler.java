package de.ibba.keepitup.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;

public class NetworkTaskServiceScheduler {

    private final Context context;
    private final NetworkTaskDAO networkTaskDAO;
    private final IAlarmManager alarmManager;

    public NetworkTaskServiceScheduler(Context context) {
        this.context = context;
        this.networkTaskDAO = new NetworkTaskDAO(context);
        this.alarmManager = createAlarmManager();
    }

    public NetworkTask schedule(NetworkTask networkTask) {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Schedule network task " + networkTask);
        networkTask.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), true);
        reschedule(networkTask, true);
        return networkTask;
    }

    public NetworkTask reschedule(NetworkTask networkTask, boolean immediate) {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Reschedule network task " + networkTask + ", immediate is " + immediate);
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(networkTask.getId());
        if (databaseTask == null) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "Network task does no longer exist. Skipping reschedule.");
            terminate(networkTask);
            return networkTask;
        }
        if (!databaseTask.isRunning()) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "Network task is no longer running. Skipping reschedule.");
            terminate(networkTask);
            return networkTask;
        }
        PendingIntent pendingIntent = getPendingIntent(networkTask);
        long delay = immediate ? 0 : getIntervalMilliseconds(networkTask);
        if (immediate) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "scheduling alarm immediately");
        } else {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "scheduling alarm with delay of " + delay + " msec");
        }
        alarmManager.setAlarm(delay, pendingIntent);
        return networkTask;
    }

    public NetworkTask cancel(NetworkTask networkTask) {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancelling network task " + networkTask);
        networkTask.setRunning(false);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
        return terminate(networkTask);
    }

    public NetworkTask terminate(NetworkTask networkTask) {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Terminating network task " + networkTask);
        PendingIntent pendingIntent = getPendingIntent(networkTask);
        alarmManager.cancelAlarm(pendingIntent);
        pendingIntent.cancel();
        return networkTask;
    }

    public void startup() {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Starting all network tasks marked as running.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Database returned the following network tasks: " + (networkTasks.isEmpty() ? "no network tasks" : ""));
        for (NetworkTask currentTask : networkTasks) {
            if (currentTask.isRunning()) {
                Log.d(NetworkTaskServiceScheduler.class.getName(), "Network task " + currentTask + " is marked as running.");
                if (!hasPendingAlarm(currentTask)) {
                    Log.d(NetworkTaskServiceScheduler.class.getName(), "No pending alarm. Scheduling...");
                    reschedule(currentTask, true);
                } else {
                    Log.d(NetworkTaskServiceScheduler.class.getName(), "Pending alarm already present.");
                }
            } else {
                Log.d(NetworkTaskServiceScheduler.class.getName(), "Network task " + currentTask + " is not marked as running.");
            }
        }
    }

    public void cancelAll() {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancelling all network tasks.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Database returned the following network tasks: " + (networkTasks.isEmpty() ? "no network tasks" : ""));
        for (NetworkTask currentTask : networkTasks) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancelling network task " + currentTask);
            cancel(currentTask);
        }
    }

    public void terminateAll() {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Terminating all network tasks.");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        for (NetworkTask currentTask : networkTasks) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "Terminating network task " + currentTask);
            terminate(currentTask);
        }
    }

    public IAlarmManager getAlarmManager() {
        return alarmManager;
    }

    private boolean hasPendingAlarm(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private PendingIntent getPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(getContext(), NetworkTaskBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(getContext(), networkTask.getSchedulerId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60 * 1000 * networkTask.getInterval();
    }

    private IAlarmManager createAlarmManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createAlarmManager(getContext());
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
