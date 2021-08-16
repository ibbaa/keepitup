package net.ibbaa.keepitup.test.mock;

import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import net.ibbaa.keepitup.notification.INotificationManager;
import net.ibbaa.keepitup.resources.ISystemSetup;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.ServiceFactory;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.INetworkManager;
import net.ibbaa.keepitup.service.ITimeService;

public class TestServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createAlarmManager");
        return new MockAlarmManager();
    }

    @Override
    public INotificationManager createNotificationManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createNotificatioManager");
        return new MockNotificationManager();
    }

    @Override
    public NotificationCompat.Builder createNotificationBuilder(Context context, String channelId) {
        Log.d(TestServiceFactory.class.getName(), "createNotificationBuilder");
        return new MockNotificationBuilder(context, channelId);
    }

    @Override
    public INetworkManager createNetworkManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createNetworkManager");
        return new MockNetworkManager();
    }

    @Override
    public ITimeService createTimeService() {
        Log.d(TestServiceFactory.class.getName(), "createTimeService");
        return new MockTimeService();
    }

    @Override
    public ISystemSetup createSystemSetup(Context context, String implementation) {
        return new JSONSystemSetup(context);
    }
}
