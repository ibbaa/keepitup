package de.ibba.keepitup.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.mapping.EnumMapping;

public class NotificationHandler {

    private final Context context;
    private final INotificationManager notificationManager;
    private NotificationCompat.Builder errorNotificationBuilder;
    private NotificationCompat.Builder foregroundNotificationBuilder;

    public NotificationHandler(Context context) {
        this.context = context;
        initErrorChannel();
        initForegroundChannel();
        this.notificationManager = createNotificationManager();
    }

    private void initErrorChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = getErrorChannelId();
            Log.d(NotificationHandler.class.getName(), "initErrorChannel: " + id);
            String name = getResources().getString(R.string.notification_error_channel_name);
            String description = getResources().getString(R.string.notification_error_channel_description);
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(getVibrationPattern());
            channel.enableVibration(true);
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    private void initForegroundChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = getForegroundChannelId();
            Log.d(NotificationHandler.class.getName(), "initForegroundChannel: " + id);
            String name = getResources().getString(R.string.notification_foreground_channel_name);
            String description = getResources().getString(R.string.notification_foreground_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            channel.enableVibration(false);
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    private String getErrorChannelId() {
        return getResources().getString(R.string.notification_error_channel_id);
    }

    private String getForegroundChannelId() {
        return getResources().getString(R.string.notification_foreground_channel_id);
    }

    public INotificationManager getNotificationManager() {
        return notificationManager;
    }

    public NotificationCompat.Builder getErrorNotificationBuilder() {
        return errorNotificationBuilder;
    }

    public NotificationCompat.Builder getForegroundNotificationBuilder() {
        return foregroundNotificationBuilder;
    }

    public void sendErrorNotification(NetworkTask task, LogEntry logEntry) {
        Log.d(NotificationHandler.class.getName(), "Sending error notification for network task " + task + ", log entry " + logEntry);
        Notification notification = buildErrorNotification(task, logEntry);
        notificationManager.notify(task.getSchedulerId(), notification);
    }

    private Notification buildErrorNotification(NetworkTask task, LogEntry logEntry) {
        Log.d(NotificationHandler.class.getName(), "Building error notification for network task " + task + ", log entry " + logEntry);
        String title = getResources().getString(R.string.notification_title);
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        String addressText = String.format(getResources().getString(R.string.notification_address), new EnumMapping(getContext()).getAccessTypeAddressText(task.getAccessType()));
        String formattedAddressText = String.format(addressText, task.getAddress(), task.getPort());
        String text = String.format(getResources().getString(R.string.notification_error_text), task.getIndex() + 1, formattedAddressText, timestampText, logEntry.getMessage() == null ? getResources().getString(R.string.string_none) : logEntry.getMessage());
        errorNotificationBuilder = createErrorNotificationBuilder();
        errorNotificationBuilder.setSmallIcon(R.drawable.icon_notification).setContentTitle(title).setContentText(text).setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            errorNotificationBuilder.setVibrate(getVibrationPattern());
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            errorNotificationBuilder.setSound(soundUri);
        }
        errorNotificationBuilder.setCategory(NotificationCompat.CATEGORY_ERROR);
        setMainActivityIntent(errorNotificationBuilder);
        return errorNotificationBuilder.build();
    }

    public Notification buildForegroundNotification() {
        Log.d(NotificationHandler.class.getName(), "Building foreground notification");
        String title = getResources().getString(R.string.notification_title);
        String text = getResources().getString(R.string.notification_foreground_text);
        foregroundNotificationBuilder = createForegroundNotificationBuilder();
        foregroundNotificationBuilder.setSmallIcon(R.drawable.icon_notification_foreground).setContentTitle(title).setContentText(text).setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setPriority(NotificationCompat.PRIORITY_LOW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            foregroundNotificationBuilder.setVibrate(null);
            foregroundNotificationBuilder.setSound(null);
        }
        return foregroundNotificationBuilder.build();
    }

    private long[] getVibrationPattern() {
        return new long[]{0, 500, 250, 500};
    }

    private void setMainActivityIntent(NotificationCompat.Builder builder) {
        Intent mainActivityIntent = new Intent(getContext(), NetworkTaskMainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntentWithParentStack(mainActivityIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
    }

    private INotificationManager createNotificationManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationManager(getContext());
    }

    private NotificationCompat.Builder createErrorNotificationBuilder() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationBuilder(getContext(), getErrorChannelId());
    }

    private NotificationCompat.Builder createForegroundNotificationBuilder() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationBuilder(getContext(), getForegroundChannelId());
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
