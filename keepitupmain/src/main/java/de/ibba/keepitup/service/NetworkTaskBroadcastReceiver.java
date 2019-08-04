package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.WorkerFactory;
import de.ibba.keepitup.resources.WorkerFactoryContributor;

public class NetworkTaskBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Received request for " + task);
        boolean synchronous = context.getResources().getBoolean(R.bool.worker_synchronous_execution);
        int wakeLockTimeout = context.getResources().getInteger(R.integer.worker_execution_wakelock_timeout) * 1000;
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Synchronoues execution is " + synchronous);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;
        try {
            Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Acquiring partial wake lock with a timeout of " + wakeLockTimeout + " msec");
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KeepItUp:DataReadBroadcastReceiver");
            wakeLock.acquire(wakeLockTimeout);
            Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Rescheduling " + task);
            NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
            if (synchronous) {
                doWork(context, task, wakeLock, true);
                scheduler.reschedule(task, false);
            } else {
                scheduler.reschedule(task, false);
                doWork(context, task, wakeLock, false);
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskBroadcastReceiver.class.getName(), "Error executing worker", exc);
        } finally {
            if (wakeLock != null && synchronous && wakeLock.isHeld()) {
                Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
        }
    }

    private void doWork(Context context, NetworkTask task, PowerManager.WakeLock wakeLock, boolean synchronous) {
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Doing work for " + task);
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Synchronous is " + synchronous);
        WorkerFactoryContributor workerFactoryContributor = new WorkerFactoryContributor(context);
        WorkerFactory workerFactory = workerFactoryContributor.createWorkerFactory();
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Worker factory is " + workerFactory.getClass().getName());
        if (synchronous) {
            NetworkTaskWorker networkTaskWorker = workerFactory.createWorker(context, task, null);
            Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Worker is " + networkTaskWorker.getClass().getName());
            networkTaskWorker.run();
        } else {
            NetworkTaskWorker networkTaskWorker = workerFactory.createWorker(context, task, wakeLock);
            Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Worker is " + networkTaskWorker.getClass().getName());
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(networkTaskWorker);
            executorService.shutdown();
        }
    }
}
