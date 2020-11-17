package de.ibba.keepitup.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.notification.NotificationHandler;

class NetworkTaskRunningNotificationService extends Service {

    public final static int NOTIFICATION_SERVICE_ID = 111;

    @Override
    public void onCreate() {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onCreate");
        NotificationHandler notificationHandler = new NotificationHandler(this);
        Notification notification = notificationHandler.buildForegroundNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_SERVICE_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_SERVICE_ID, notification);
        }
    }

    public static String getRescheduleDelayKey() {
        return NetworkTaskRunningNotificationService.class.getSimpleName() + "RescheduleDelay";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onDestroy");
        NetworkTaskProcessServiceScheduler scheduler = new NetworkTaskProcessServiceScheduler(this);
        scheduler.cancelAll();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onBind");
        return null;
    }
}
