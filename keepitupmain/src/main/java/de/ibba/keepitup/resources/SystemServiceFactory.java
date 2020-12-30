package de.ibba.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import java.lang.reflect.Constructor;
import java.util.Objects;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.notification.INotificationManager;
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
    public INotificationManager createNotificationManager(Context context) {
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

    @Override
    public ISystemSetup createSystemSetup(Context context, String implementation) {
        Log.d(SystemServiceFactory.class.getName(), "createSystemSetup, implementation is " + implementation);
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            if (classloader == null) {
                classloader = this.getClass().getClassLoader();
            }
            Class<?> setupClass = Objects.requireNonNull(classloader).loadClass(implementation);
            Log.d(SystemServiceFactory.class.getName(), "Loaded setup class is " + setupClass.getName());
            Constructor<?> constructor = setupClass.getDeclaredConstructor(Context.class);
            return (ISystemSetup) constructor.newInstance(context);
        } catch (Exception exc) {
            Log.e(ServiceFactoryContributor.class.getName(), "Error creating system setup", exc);
            throw new RuntimeException(exc);
        }
    }
}
