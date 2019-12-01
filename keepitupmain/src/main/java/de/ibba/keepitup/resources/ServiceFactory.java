package de.ibba.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import de.ibba.keepitup.notification.INotificatioManager;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.service.INetworkManager;
import de.ibba.keepitup.service.ITimeService;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    INotificatioManager createNotificationManager(Context context);

    NotificationCompat.Builder createNotificationBuilder(Context context, String channelId);

    INetworkManager createNetworkManager(Context context);

    ITimeService createTimeService();
}
