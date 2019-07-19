package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.ibba.keepitup.notification.INotificatioManager;
import de.ibba.keepitup.resources.ServiceFactory;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.ui.sync.IHandler;
import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

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
    public IHandler createHandler() {
        Log.d(TestServiceFactory.class.getName(), "createHandler");
        return new MockHandler();
    }

    @Override
    public UISyncAsyncTask createUISyncAsyncTask() {
        Log.d(TestServiceFactory.class.getName(), "createUISyncAsyncTask");
        return new MockUISyncAsyncTask();
    }
}
