package de.ibba.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import de.ibba.keepitup.notification.INotificatioManager;
import de.ibba.keepitup.permission.IPermissionManager;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.service.INetworkManager;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    INotificatioManager createNotificationManager(Context context);

    NotificationCompat.Builder createNotificationBuilder(Context context, String channelId);

    INetworkManager createNetworkManager(Context context);

    IPermissionManager createPermissionManager(FragmentActivity activity);
}
