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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import net.ibbaa.keepitup.logging.Log;

public class SystemAlarmManager implements IAlarmManager {

    private final AlarmManager alarmManager;

    public SystemAlarmManager(Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public boolean canScheduleAlarms() {
        Log.d(SystemAlarmManager.class.getName(), "canScheduleAlarms");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean canScheduleExactAlarms = alarmManager.canScheduleExactAlarms();
            Log.d(SystemAlarmManager.class.getName(), "canScheduleExactAlarms is " + canScheduleExactAlarms);
            return canScheduleExactAlarms;
        }
        Log.d(SystemAlarmManager.class.getName(), "canScheduleAlarms returns true because of old Android version");
        return true;
    }

    @Override
    public void setAlarm(long delay, PendingIntent pendingIntent) {
        Log.d(SystemAlarmManager.class.getName(), "Setting alarm with a delay of " + delay);
        if (!canScheduleAlarms()) {
            Log.e(SystemAlarmManager.class.getName(), "Cannot set alarm because of missing permission.");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
        }
    }

    @Override
    public void setRTCAlarm(long timestamp, PendingIntent pendingIntent) {
        Log.d(SystemAlarmManager.class.getName(), "Setting alarm with a timestamp of " + timestamp);
        if (!canScheduleAlarms()) {
            Log.e(SystemAlarmManager.class.getName(), "Cannot set alarm because of missing permission.");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
        }
    }

    @Override
    public void cancelAlarm(PendingIntent pendingIntent) {
        Log.d(SystemAlarmManager.class.getName(), "Canceling alarm");
        alarmManager.cancel(pendingIntent);
    }
}
