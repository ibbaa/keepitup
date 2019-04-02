package de.ibba.keepitup.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskServiceScheduler {

    private final Context context;
    private final NetworkTaskDAO networkTaskDAO;

    public NetworkTaskServiceScheduler(Context context) {
        this.context = context;
        this.networkTaskDAO = new NetworkTaskDAO(context);
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
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Reschedule network task " + networkTask + ", immediate is " + true);
        PendingIntent pendingIntent = getPendingIntent(networkTask);
        long delay = immediate ? 0 : getIntervalMilliseconds(networkTask);
        if (immediate) {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "scheduling alarm immediately");
        } else {
            Log.d(NetworkTaskServiceScheduler.class.getName(), "scheduling alarm with delay of " + delay + " msec");
        }
        setAlarm(delay, pendingIntent);
        return networkTask;
    }

    public NetworkTask cancel(NetworkTask networkTask) {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancel network task " + networkTask);
        networkTask.setRunning(false);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
        PendingIntent pendingIntent = getPendingIntent(networkTask);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        return networkTask;
    }

    private PendingIntent getPendingIntent(NetworkTask networkTask) {
        Intent intent = new Intent(context, NetworkTaskBroadcastReceiver.class);
        intent.putExtras(networkTask.toBundle());
        return PendingIntent.getBroadcast(context, networkTask.getSchedulerId(), intent, 0);
    }

    public void cancelAll() {
        Log.d(NetworkTaskServiceScheduler.class.getName(), "Cancel all network tasks ");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        for (NetworkTask currentTask : networkTasks) {
            cancel(currentTask);
        }
    }

    private void setAlarm(long delay, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
        }
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60 * 1000 * networkTask.getInterval();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
