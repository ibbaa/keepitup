package de.ibba.keepitup.service;

import android.app.PendingIntent;

public interface IAlarmManager {

    void setAlarm(long delay, PendingIntent pendingIntent);

    void cancelAlarm(PendingIntent pendingIntent);
}
