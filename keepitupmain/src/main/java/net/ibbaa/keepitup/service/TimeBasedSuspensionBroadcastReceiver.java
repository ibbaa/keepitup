/*
 * Copyright (c) 2023. Alwin Ibba
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
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;

import java.util.Objects;

public class TimeBasedSuspensionBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "onReceive");
        if (intent == null) {
            Log.e(TimeBasedSuspensionBroadcastReceiver.class.getName(), "intent is null");
            return;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;
        int wakeLockTimeout = context.getResources().getInteger(R.integer.scheduler_receiver_wakelock_timeout) * 1000;
        try {
            synchronized (TimeBasedSuspensionScheduler.class) {
                Log.d(NetworkTaskProcessBroadcastReceiver.class.getName(), "Acquiring partial wake lock with a timeout of " + wakeLockTimeout + " msec");
                wakeLock = Objects.requireNonNull(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "KeepItUp:TimeBasedSuspensionBroadcastReceiver");
                wakeLock.acquire(wakeLockTimeout);
                TimeBasedSuspensionScheduler scheduler = createTimeBasedSuspensionScheduler(context);
                ITimeService timeService = createTimeService(context);
                boolean restarted = intent.getBooleanExtra(context.getResources().getString(R.string.scheduler_restart_key), false);
                if (scheduler.getWasRestartedFlag() && !restarted) {
                    Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Received intent without restarted marker, but scheduler was restarted. Ignoring this call.");
                    return;
                } else {
                    scheduler.resetWasRestartedFlag();
                }
                String actionValue = intent.getStringExtra(context.getResources().getString(R.string.scheduler_action_key));
                if (actionValue == null) {
                    Log.e(TimeBasedSuspensionBroadcastReceiver.class.getName(), "No action received. Intent contains no scheduler_action_key");
                    return;
                }
                TimeBasedSuspensionScheduler.Action action;
                try {
                    action = TimeBasedSuspensionScheduler.Action.valueOf(actionValue);
                } catch (IllegalArgumentException exc) {
                    Log.e(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Received action is invalid: " + actionValue);
                    return;
                }
                long now = timeService.getCurrentTimestamp();
                long thresholdNow = addThreshold(context, now);
                Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Current timestamp is " + now);
                Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Current timestamp with configured threshold is " + thresholdNow);
                if (TimeBasedSuspensionScheduler.Action.UP.equals(action)) {
                    Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Received action is UP. Current state is suspended. Ending suspension now.");
                    doStart(scheduler, now, thresholdNow);
                } else {
                    Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Received action is DOWN. Current state is not suspended. Suspend now.");
                    doSuspend(scheduler, now, thresholdNow);
                }
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskProcessBroadcastReceiver.class.getName(), "Error executing TimeBasedSuspensionBroadcastReceiver", exc);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Releasing partial wake lock");
                wakeLock.release();
            }
        }
    }

    private static void doStart(TimeBasedSuspensionScheduler scheduler, long now, long thresholdNow) {
        Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "doStart");
        Interval interval = scheduler.findNextSuspendInterval(thresholdNow);
        if (interval == null) {
            Log.e(TimeBasedSuspensionBroadcastReceiver.class.getName(), "No interval found for next suspension period.");
        } else {
            Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Found next suspend interval: " + interval);
            scheduler.scheduleStart(interval, thresholdNow, false);
        }
        scheduler.startup(now);
    }

    private static void doSuspend(TimeBasedSuspensionScheduler scheduler, long now, long thresholdNow) {
        Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "doSuspend");
        Interval interval = scheduler.findCurrentSuspendInterval(thresholdNow);
        if (interval == null) {
            Log.e(TimeBasedSuspensionBroadcastReceiver.class.getName(), "No interval found for current suspension period.");
        } else {
            Log.d(TimeBasedSuspensionBroadcastReceiver.class.getName(), "Found current suspend interval: " + interval);
            scheduler.scheduleSuspend(interval, thresholdNow, false);
            scheduler.suspend(now);
        }
    }

    private long addThreshold(Context context, long timestamp) {
        return timestamp + context.getResources().getInteger(R.integer.scheduler_receiver_time_threshold) * 1000;
    }

    protected ITimeService createTimeService(Context context) {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(context);
        return factoryContributor.createServiceFactory().createTimeService();
    }

    protected TimeBasedSuspensionScheduler createTimeBasedSuspensionScheduler(Context context) {
        return new TimeBasedSuspensionScheduler(context);
    }
}
