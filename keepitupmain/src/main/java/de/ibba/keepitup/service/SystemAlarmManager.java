package de.ibba.keepitup.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class SystemAlarmManager implements IAlarmManager {

    private final AlarmManager alarmManager;

    public SystemAlarmManager(Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void setAlarm(long delay, PendingIntent pendingIntent) {
        Log.d(SystemAlarmManager.class.getName(), "Setting alarm with a delay of " + delay);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
        }
    }

    @Override
    public void cancelAlarm(PendingIntent pendingIntent) {
        Log.d(SystemAlarmManager.class.getName(), "Canceling alarm");
        alarmManager.cancel(pendingIntent);
    }
}
