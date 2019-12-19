package de.ibba.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.notification.INotificatioManager;
import de.ibba.keepitup.notification.SystemNotificationManager;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.service.INetworkManager;
import de.ibba.keepitup.service.ITimeService;
import de.ibba.keepitup.service.SystemAlarmManager;
import de.ibba.keepitup.service.SystemNetworkManager;
import de.ibba.keepitup.service.SystemTimeService;

public class SystemServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createAlarmManager");
        return new SystemAlarmManager(context);
    }

    @Override
    public INotificatioManager createNotificationManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createNotificationManager");
        return new SystemNotificationManager(context);
    }

    @Override
    public NotificationCompat.Builder createNotificationBuilder(Context context, String channelId) {
        Log.d(SystemServiceFactory.class.getName(), "createNotificationBuilder");
        return new NotificationCompat.Builder(context, channelId);
    }

    @Override
    public INetworkManager createNetworkManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createNetworkManager");
        return new SystemNetworkManager(context);
    }

    @Override
    public ITimeService createTimeService() {
        Log.d(SystemServiceFactory.class.getName(), "createTimeService");
        return new SystemTimeService();
    }
}
