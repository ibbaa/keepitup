package net.ibbaa.keepitup.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import net.ibbaa.keepitup.logging.Log;

public class SystemNotificationManager implements INotificationManager {

    private final NotificationManager notificationManager;

    public SystemNotificationManager(Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void notify(int id, Notification notification) {
        Log.d(SystemNotificationManager.class.getName(), "Sending notification with id " + id);
        notificationManager.notify(id, notification);
    }
}