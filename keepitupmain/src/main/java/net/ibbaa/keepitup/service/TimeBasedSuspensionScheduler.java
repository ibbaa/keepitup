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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.db.SchedulerIdGenerator;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.util.TimeUtil;

import java.util.List;

public class TimeBasedSuspensionScheduler {

    public enum Action {
        UP,
        DOWN
    }

    private final Context context;
    private final IntervalDAO intervalDAO;
    private final ITimeService timeService;
    private final IAlarmManager alarmManager;

    private static List<Interval> intervals;
    private static boolean wasRestarted = false;

    public TimeBasedSuspensionScheduler(Context context) {
        this.context = context;
        this.intervalDAO = new IntervalDAO(context);
        this.timeService = createTimeService();
        this.alarmManager = createAlarmManager();
    }

    public List<Interval> getIntervals() {
        synchronized (TimeBasedSuspensionScheduler.class) {
            if (intervals == null) {
                intervals = intervalDAO.readAllIntervals();
            }
            return intervals;
        }
    }

    public void reset() {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "reset");
        synchronized (TimeBasedSuspensionScheduler.class) {
            intervals = null;
        }
    }

    public boolean getWasRestartedFlag() {
        synchronized (TimeBasedSuspensionScheduler.class) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "getWasRestartedFlag, wasRestarted is " + wasRestarted);
            return wasRestarted;
        }
    }

    public void resetWasRestartedFlag() {
        synchronized (TimeBasedSuspensionScheduler.class) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "resetWasRestartedFlag, wasRestarted is " + wasRestarted);
            wasRestarted = false;
        }
    }

    public void restart() {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "reconfigure");
        synchronized (TimeBasedSuspensionScheduler.class) {
            cancelCurrent();
            reset();
            resetWasRestartedFlag();
            getIntervals();
            if (!isSuspensionActiveAndEnabled()) {
                Log.d(TimeBasedSuspensionScheduler.class.getName(), "Suspension feature is not active.");
                startup();
                return;
            }
            start();
        }
    }

    public void start() {
        start(null);
    }

    public void start(NetworkTask task) {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "start");
        synchronized (TimeBasedSuspensionScheduler.class) {
            long now = timeService.getCurrentTimestamp();
            long thresholdNow = addThreshold(now);
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Current timestamp is " + now);
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Current timestamp configured threshold is " + thresholdNow);
            Interval currentSuspendInterval = findCurrentSuspendInterval(thresholdNow);
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Found current suspend interval is " + currentSuspendInterval);
            if (currentSuspendInterval != null) {
                scheduleSuspend(currentSuspendInterval, thresholdNow, true);
                suspend();
            } else {
                Interval nextSuspendInterval = findNextSuspendInterval(thresholdNow);
                Log.d(TimeBasedSuspensionScheduler.class.getName(), "Found next suspend interval is " + nextSuspendInterval);
                scheduleStart(nextSuspendInterval, thresholdNow, true);
                if (task == null) {
                    startup();
                } else {
                    startSingle(task);
                }
            }
        }
    }

    public void stop() {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "stop");
        cancelCurrent();
        resetWasRestartedFlag();
    }

    public boolean isRunning() {
        return getPendingIntent() != null;
    }

    public Interval findCurrentSuspendInterval(long now) {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "findCurrentSuspendInterval for " + now);
        List<Interval> intervalList = getIntervals();
        for (Interval interval : intervalList) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Current interval is " + interval);
            long start = TimeUtil.getTimestampToday(interval.getStart(), now);
            long end = TimeUtil.getTimestampToday(interval.getEnd(), now);
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Start timestamp is " + start);
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "End timestamp is " + end);
            if (!interval.doesOverlapDays()) {
                if (now >= start && now < end) {
                    return interval;
                }
            } else {
                if (now >= start || now < end) {
                    return interval;
                }
            }
        }
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "No suspend interval found. Returning null.");
        return null;
    }

    public Interval findNextSuspendInterval(long now) {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "findStartupTime with timestamp " + now);
        List<Interval> intervalList = getIntervals();
        for (Interval interval : intervalList) {
            long start = TimeUtil.getTimestampToday(interval.getStart(), now);
            if (now < start) {
                Log.d(TimeBasedSuspensionScheduler.class.getName(), "Returning interval " + interval);
                return interval;
            }
        }
        if (!intervalList.isEmpty()) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Returning first interval of list: " + intervalList.get(0));
            return intervalList.get(0);
        }
        Log.e(TimeBasedSuspensionScheduler.class.getName(), "Interval list is empty. Returning null");
        return null;
    }

    public boolean isSuspended() {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "isSuspended");
        return isSuspended(timeService.getCurrentTimestamp());
    }

    private boolean isSuspended(long now) {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "isSuspended  for " + now);
        if (!isSuspensionActiveAndEnabled()) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Suspension feature is not active. Returning false.");
            return false;
        }
        return findCurrentSuspendInterval(now) != null;
    }

    public void scheduleSuspend(Interval currentSuspendInterval, long now, boolean restart) {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "suspend with current suspend interval " + currentSuspendInterval + " and timestamp of " + now);
        long end = TimeUtil.getTimestampToday(currentSuspendInterval.getEnd(), now);
        if (end <= now) {
            end = TimeUtil.getTimestampTomorrow(currentSuspendInterval.getEnd(), now);
        }
        wasRestarted = restart;
    }

    public void scheduleStart(Interval nextSuspendInterval, long now, boolean restart) {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "startup with next suspend interval " + nextSuspendInterval + " and timestamp of " + now);
        long start = TimeUtil.getTimestampToday(nextSuspendInterval.getStart(), now);
        if (start <= now) {
            start = TimeUtil.getTimestampTomorrow(nextSuspendInterval.getStart(), now);
        }
        wasRestarted = restart;
    }

    public void suspend() {

    }

    public void startup() {

    }

    public void startSingle(NetworkTask task) {

    }

    private boolean isSuspensionActiveAndEnabled() {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "isSuspensionActiveAndEnabled");
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (!preferenceManager.getPreferenceSuspensionEnabled()) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Suspension feature is disabled in the settings. Returning false.");
            return false;
        }
        if (getIntervals().isEmpty()) {
            Log.d(TimeBasedSuspensionScheduler.class.getName(), "Interval list is empty. Returning false.");
            return false;
        }
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "Suspension is enabled with intervals present. Returning true.");
        return true;
    }

    private void cancelCurrent() {
        Log.d(TimeBasedSuspensionScheduler.class.getName(), "cancelCurrent");
        PendingIntent intent = getPendingIntent();
        if (intent != null) {
            alarmManager.cancelAlarm(intent);
            intent.cancel();
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getContext(), TimeBasedSuspensionBroadcastReceiver.class);
        intent.setPackage(getContext().getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), SchedulerIdGenerator.TIME_BASED_SCHEDULER_ID, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        } else {
            return PendingIntent.getBroadcast(getContext(), SchedulerIdGenerator.TIME_BASED_SCHEDULER_ID, intent, PendingIntent.FLAG_NO_CREATE);
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent createPendingIntent(Action action, boolean restart) {
        Intent intent = new Intent(getContext(), TimeBasedSuspensionBroadcastReceiver.class);
        intent.setPackage(getContext().getPackageName());
        intent.putExtra(getContext().getResources().getString(R.string.scheduler_action_key), action.name());
        if (restart) {
            intent.putExtra(getContext().getResources().getString(R.string.scheduler_restart_key), true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), SchedulerIdGenerator.TIME_BASED_SCHEDULER_ID, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            return PendingIntent.getBroadcast(getContext(), SchedulerIdGenerator.TIME_BASED_SCHEDULER_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
    }

    private long addThreshold(long timestamp) {
        return timestamp + getContext().getResources().getInteger(R.integer.scheduler_time_threshold) * 1000;
    }

    private Context getContext() {
        return context;
    }

    public IAlarmManager getAlarmManager() {
        return alarmManager;
    }

    public ITimeService getTimeService() {
        return timeService;
    }

    private IAlarmManager createAlarmManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createAlarmManager(getContext());
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }
}
