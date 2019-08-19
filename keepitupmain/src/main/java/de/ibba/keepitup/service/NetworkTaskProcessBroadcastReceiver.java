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
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.WorkerFactory;
import de.ibba.keepitup.resources.WorkerFactoryContributor;

public class NetworkTaskProcessBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Received request for " + task);
        boolean synchronous = context.getResources().getBoolean(R.bool.worker_synchronous_execution);
        int wakeLockTimeout = context.getResources().getInteger(R.integer.worker_execution_wakelock_timeout) * 1000;
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Synchronoues execution is " + synchronous);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Acquiring partial wake lock with a timeout of " + wakeLockTimeout + " msec");
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KeepItUp:DataReadBroadcastReceiver");
            wakeLock.acquire(wakeLockTimeout);
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Rescheduling " + task);
            NetworkTaskProcessServiceScheduler scheduler = new NetworkTaskProcessServiceScheduler(context);
            if (synchronous) {
                doWork(context, task, wakeLock, true, executorService);
                scheduler.reschedule(task, false);
            } else {
                scheduler.reschedule(task, false);
                doWork(context, task, wakeLock, false, executorService);
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskProcessBroadcastReceiver.class.getName(), "Error executing worker", exc);
        } finally {
            if (wakeLock != null && synchronous && wakeLock.isHeld()) {
                Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Shutting down ExecutorService");
            executorService.shutdown();
        }
    }

    private void doWork(Context context, NetworkTask task, PowerManager.WakeLock wakeLock, boolean synchronous, ExecutorService executorService) {
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Doing work for " + task);
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Synchronous is " + synchronous);
        if (!isNetworkTaskValid(context, task)) {
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Network task has been marked as not running. Skipping execution");
            return;
        }
        WorkerFactoryContributor workerFactoryContributor = new WorkerFactoryContributor(context);
        WorkerFactory workerFactory = workerFactoryContributor.createWorkerFactory();
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Worker factory is " + workerFactory.getClass().getName());
        if (synchronous) {
            NetworkTaskWorker networkTaskWorker = workerFactory.createWorker(context, task, null);
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Worker is " + networkTaskWorker.getClass().getName());
            networkTaskWorker.run();
        } else {
            NetworkTaskWorker networkTaskWorker = workerFactory.createWorker(context, task, wakeLock);
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Worker is " + networkTaskWorker.getClass().getName());
            executorService.execute(networkTaskWorker);
        }
    }

    private boolean isNetworkTaskValid(Context context, NetworkTask task) {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(task.getId());
        return databaseTask != null && databaseTask.isRunning() && databaseTask.getSchedulerId() == task.getSchedulerId();
    }
}
