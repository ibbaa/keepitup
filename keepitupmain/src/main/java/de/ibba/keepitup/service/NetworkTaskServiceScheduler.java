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
        if (networkTask.isRunning()) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "Network task " + networkTask + " is already running. Stopping...");
            cancel(networkTask);
        }
        networkTask.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), true);
        reschedule(networkTask, true);
        return networkTask;
    }

    public NetworkTask reschedule(NetworkTask networkTask, boolean immediate) {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Reschedule network task " + networkTask + ", immediate is " + immediate);
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
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancel network task " + networkTask);
        networkTask.setRunning(false);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
        PendingIntent pendingIntent = getPendingIntent(networkTask);
        alarmManager.cancelAlarm(pendingIntent);
        pendingIntent.cancel();
        return networkTask;
    }

    public void cancelAll() {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancel all network tasks ");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        for (NetworkTask currentTask : networkTasks) {
            cancel(currentTask);
        }
    }

    public IAlarmManager getAlarmManager() {
        return alarmManager;
    }

    private PendingIntent getPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(context, NetworkTaskBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(context, networkTask.getSchedulerId(), intent, 0);
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60 * 1000 * networkTask.getInterval();
    }

    private IAlarmManager createAlarmManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(context);
        return factoryContributor.createServiceFactory().createAlarmManager(context);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
