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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.WorkerFactory;
import net.ibbaa.keepitup.resources.WorkerFactoryContributor;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkTaskProcessBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Received request for " + task);
        boolean synchronous = context.getResources().getBoolean(R.bool.worker_synchronous_execution);
        boolean addToPool = context.getResources().getBoolean(R.bool.worker_add_to_pool);
        int wakeLockTimeout = context.getResources().getInteger(R.integer.worker_execution_wakelock_timeout) * 1000;
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Synchronous execution is " + synchronous);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Creating ExecutorService");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Acquiring partial wake lock with a timeout of " + wakeLockTimeout + " msec");
            wakeLock = Objects.requireNonNull(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KeepItUp:NetworkTaskProcessBroadcastReceiver");
            wakeLock.acquire(wakeLockTimeout);
            TimeBasedSuspensionScheduler timeBasedScheduler = createTimeBasedSuspensionScheduler(context);
            synchronized (TimeBasedSuspensionScheduler.LOCK) {
                if (timeBasedScheduler.isRunning()) {
                    Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Time based scheduler is running.");
                    if (!timeBasedScheduler.isSuspended()) {
                        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Time based scheduler is not suspended.");
                        executeAndReschedule(context, task, wakeLock, synchronous, addToPool, executorService);
                    } else {
                        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Time based scheduler is suspended. Skipping execution and rescheduling.");
                    }
                } else {
                    Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Time based scheduler is not running.");
                    if (!timeBasedScheduler.isSuspensionActiveAndEnabled()) {
                        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Time based scheduler is not active.");
                        executeAndReschedule(context, task, wakeLock, synchronous, addToPool, executorService);
                    } else {
                        Log.e(NetworkTaskProcessBroadcastReceiver.class.getName(), "Time based scheduler is not running but is active. Restarting...");
                        timeBasedScheduler.start(task);
                    }
                }
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

    private void executeAndReschedule(Context context, NetworkTask task, PowerManager.WakeLock wakeLock, boolean synchronous, boolean addToPool, ExecutorService executorService) {
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "executeAndReschedule for " + task);
        if (synchronous) {
            doWork(context, task, wakeLock, true, false, executorService);
            rescheduleTask(context, task);
        } else {
            rescheduleTask(context, task);
            doWork(context, task, wakeLock, false, addToPool, executorService);
        }
    }

    private void doWork(Context context, NetworkTask task, PowerManager.WakeLock wakeLock, boolean synchronous, boolean addToPool, ExecutorService executorService) {
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Doing work for " + task);
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Synchronous is " + synchronous);
        if (isNetworkTaskInvalid(context, task)) {
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
            Future<?> networkTaskWorkerFuture = executorService.submit(networkTaskWorker);
            if (addToPool) {
                NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().pool(task.getSchedulerId(), networkTaskWorkerFuture);
                Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Added worker to pool");
            } else {
                Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Worker not added to pool");
            }
        }
    }

    private void rescheduleTask(Context context, NetworkTask task) {
        Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "rescheduleTask for " + task);
        if (isNetworkTaskInvalid(context, task)) {
            Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Network task has been marked as not running. Skipping reschedule");
            return;
        }
        TimeBasedSuspensionScheduler timeBasedScheduler = createTimeBasedSuspensionScheduler(context);
        timeBasedScheduler.getNetworkTaskScheduler().reschedule(task, NetworkTaskProcessServiceScheduler.Delay.INTERVAL);
    }

    private boolean isNetworkTaskInvalid(Context context, NetworkTask task) {
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
        NetworkTask databaseTask = networkTaskDAO.readNetworkTask(task.getId());
        return databaseTask == null || !databaseTask.isRunning() || databaseTask.getSchedulerId() != task.getSchedulerId();
    }

    protected TimeBasedSuspensionScheduler createTimeBasedSuspensionScheduler(Context context) {
        return new TimeBasedSuspensionScheduler(context);
    }
}
