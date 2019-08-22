package de.ibba.keepitup.resources;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import de.ibba.keepitup.notification.INotificatioManager;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.service.INetworkManager;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.sync.NetworkTaskMainUIInitTask;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    INotificatioManager createNotificationManager(Context context);

    NotificationCompat.Builder createNotificationBuilder(Context context, String channelId);

    INetworkManager createNetworkManager(Context context);

    NetworkTaskMainUIInitTask createUIInitTask(Context context, NetworkTaskAdapter adapter);
}
