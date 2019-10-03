package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import de.ibba.keepitup.notification.INotificatioManager;
import de.ibba.keepitup.permission.IPermissionManager;
import de.ibba.keepitup.resources.ServiceFactory;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.service.INetworkManager;

public class TestServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createAlarmManager");
        return new MockAlarmManager();
    }

    @Override
    public INotificatioManager createNotificationManager(Context context) {
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
    public IPermissionManager createPermissionManager(FragmentActivity activity) {
        Log.d(TestServiceFactory.class.getName(), "createPermissionManager");
        return new MockPermissionManager();
    }
}
