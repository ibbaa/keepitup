package net.ibbaa.keepitup.service;

import android.app.PendingIntent;

public interface IAlarmManager {

    void setAlarm(long delay, PendingIntent pendingIntent);

    void cancelAlarm(PendingIntent pendingIntent);
}
