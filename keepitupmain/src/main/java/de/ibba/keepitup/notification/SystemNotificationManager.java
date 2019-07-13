package de.ibba.keepitup.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

public class SystemNotificationManager implements INotificatioManager {

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
