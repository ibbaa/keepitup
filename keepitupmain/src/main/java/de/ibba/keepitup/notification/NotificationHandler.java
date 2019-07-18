package de.ibba.keepitup.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.ui.mapping.EnumMapping;

public class NotificationHandler {

    private final Context context;
    private final INotificatioManager notificationManager;

    public NotificationHandler(Context context) {
        this.context = context;
        initChannel(getChannelId());
        this.notificationManager = createNotificationManager();
    }

    private void initChannel(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(NotificationHandler.class.getName(), "initChannel: " + id);
            String name = getResources().getString(R.string.notification_channel_name);
            String description = getResources().getString(R.string.notification_channel_description);
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(getVibrationPattern());
            channel.enableVibration(true);
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String getChannelId() {
        return getResources().getString(R.string.notification_channel_id);
    }

    public INotificatioManager getNotificationManager() {
        return notificationManager;
    }

    public void sendNotification(NetworkTask task, long timestamp) {
        Log.d(NotificationHandler.class.getName(), "Sending notification for network task " + task + " and timestamp " + timestamp);
        Notification notification = buildNotification(task, timestamp);
        notificationManager.notify(task.getSchedulerId(), notification);
    }

    private Notification buildNotification(NetworkTask task, long timestamp) {
        Log.d(NotificationHandler.class.getName(), "Building notification for network task " + task + " and timestamp " + timestamp);
        String title = getResources().getString(R.string.notification_title);
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(timestamp));
        String addressText = String.format(getResources().getString(R.string.notification_address), new EnumMapping(getContext()).getAccessTypeAddressText(task.getAccessType()));
        String formattedAddressText = String.format(addressText, task.getAddress(), task.getPort());
        String text = String.format(getResources().getString(R.string.notification_text), task.getIndex() + 1, formattedAddressText, timestampText);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getChannelId())
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setVibrate(getVibrationPattern());
        }
        return builder.build();
    }

    private long[] getVibrationPattern() {
        return new long[]{0, 500, 250, 500};
    }

    private INotificatioManager createNotificationManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationManager(getContext());
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
