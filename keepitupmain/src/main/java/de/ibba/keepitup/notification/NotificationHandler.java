package de.ibba.keepitup.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.ui.mapping.EnumMapping;

public class NotificationHandler {

    private final Context context;
    private final INotificatioManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

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
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    private String getChannelId() {
        return getResources().getString(R.string.notification_channel_id);
    }

    public INotificatioManager getNotificationManager() {
        return notificationManager;
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return notificationBuilder;
    }

    public void sendNotification(NetworkTask task, LogEntry logEntry) {
        Log.d(NotificationHandler.class.getName(), "Sending notification for network task " + task + ", log entry " + logEntry);
        Notification notification = buildNotification(task, logEntry);
        notificationManager.notify(task.getSchedulerId(), notification);
    }

    private Notification buildNotification(NetworkTask task, LogEntry logEntry) {
        Log.d(NotificationHandler.class.getName(), "Building notification for network task " + task + ", log entry " + logEntry);
        String title = getResources().getString(R.string.notification_title);
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        String addressText = String.format(getResources().getString(R.string.notification_address), new EnumMapping(getContext()).getAccessTypeAddressText(task.getAccessType()));
        String formattedAddressText = String.format(addressText, task.getAddress(), task.getPort());
        String text = String.format(getResources().getString(R.string.notification_text), task.getIndex() + 1, formattedAddressText, timestampText, logEntry.getMessage() == null ? getResources().getString(R.string.string_none) : logEntry.getMessage());
        notificationBuilder = createNotificationBuilder();
        notificationBuilder.setSmallIcon(R.drawable.icon_notification).setContentTitle(title).setContentText(text).setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setVibrate(getVibrationPattern());
        }
        return notificationBuilder.build();
    }

    private long[] getVibrationPattern() {
        return new long[]{0, 500, 250, 500};
    }

    private INotificatioManager createNotificationManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationManager(getContext());
    }

    private NotificationCompat.Builder createNotificationBuilder() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationBuilder(getContext(), getChannelId());
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
