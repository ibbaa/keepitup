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
import de.ibba.keepitup.resources.WorkerFactory;
import de.ibba.keepitup.resources.WorkerFactoryContributor;

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
        WorkerFactoryContributor workerFactoryContributor = new WorkerFactoryContributor(context);
        WorkerFactory workerFactory = workerFactoryContributor.createWorkerFactory();
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Worker factory is " + workerFactory.getClass().getName());
        NetworkTaskWorker networkTaskWorker = workerFactory.createWorker(context, task, wakeLock);
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Worker is " + networkTaskWorker.getClass().getName());
        if (synchronous) {
            networkTaskWorker.run();
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(networkTaskWorker);
            executorService.shutdown();
        }
    }
}
