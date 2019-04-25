package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskBroadcastReceiver extends BroadcastReceiver {

    private boolean synchronous = false;

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Received request for " + task);
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Acquiring partial wake lock");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KeepItUp:DataReadBroadcastReceiver");
        wakeLock.acquire();
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Rescheduling " + task);
        NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
        scheduler.reschedule(task, false);
        doWork(context, task, wakeLock);
    }

    private void doWork(Context context, NetworkTask task, PowerManager.WakeLock wakeLock) {
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Doing work for " + task);
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Synchronous is " + synchronous);
        NetworkTaskWorker networkTaskWorker = new NetworkTaskWorker(context, task, wakeLock);
        if (synchronous) {
            networkTaskWorker.run();
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(networkTaskWorker);
            executorService.shutdown();
        }
    }
}
