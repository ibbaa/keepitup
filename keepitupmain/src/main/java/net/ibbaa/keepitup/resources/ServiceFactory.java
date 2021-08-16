package net.ibbaa.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import net.ibbaa.keepitup.notification.INotificationManager;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.INetworkManager;
import net.ibbaa.keepitup.service.ITimeService;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    INotificationManager createNotificationManager(Context context);

    NotificationCompat.Builder createNotificationBuilder(Context context, String channelId);

    INetworkManager createNetworkManager(Context context);

    ITimeService createTimeService();

    ISystemSetup createSystemSetup(Context context, String implementation);
}