package de.ibba.keepitup.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;

public class NetworkTaskProcessServiceScheduler {

    private final Context context;
    private final NetworkTaskDAO networkTaskDAO;
    private final IAlarmManager alarmManager;

    private static NetworkTaskProcessPool processPool;

    public NetworkTaskProcessServiceScheduler(Context context) {
        this.context = context;
        this.networkTaskDAO = new NetworkTaskDAO(context);
        this.alarmManager = createAlarmManager();
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
        reschedule(networkTask, true);
        return networkTask;
    }

    public NetworkTask reschedule(NetworkTask networkTask, boolean immediate) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Reschedule network task " + networkTask + ", immediate is " + immediate);
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
        PendingIntent pendingIntent = createPendingIntent(networkTask);
        long delay = immediate ? 0 : getIntervalMilliseconds(networkTask);
        if (immediate) {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "scheduling alarm immediately");
        } else {
            Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "scheduling alarm with delay of " + delay + " msec");
        }
        alarmManager.setAlarm(delay, pendingIntent);
        return networkTask;
    }

    public NetworkTask cancel(NetworkTask networkTask) {
        Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Cancelling network task " + networkTask);
        networkTask.setRunning(false);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
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
                reschedule(currentTask, false);
            } else {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Network task " + currentTask + " is not marked as running.");
                networkTaskDAO.resetNetworkTaskInstances(currentTask.getId());
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
