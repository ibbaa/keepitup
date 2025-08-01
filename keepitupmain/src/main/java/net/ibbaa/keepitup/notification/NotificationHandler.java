/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.notification;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.SchedulerIdGenerator;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.alarm.StopAlarmReceiver;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.util.UIUtil;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class NotificationHandler {

    public final static int NOTIFICATION_FOREGROUND_START_ID = 1234;
    public final static int NOTIFICATION_MISSING_LOG_FOLDER_PERMISSION = -100;
    public final static int NOTIFICATION_MISSING_DOWNLOAD_FOLDER_PERMISSION = -200;
    public final static int NOTIFICATION_FOREGROUND_NETWORKTASK_RUNNING_SERVICE_ID = 111;

    private final Context context;
    private final INotificationManager notificationManager;
    private final IPermissionManager permissionManager;
    private NotificationCompat.Builder messageNotificationBuilder;
    private NotificationCompat.Builder foregroundNotificationBuilder;

    public NotificationHandler(Context context, IPermissionManager permissionManager) {
        this.context = context;
        this.permissionManager = permissionManager;
        if (permissionManager.hasPostNotificationsPermission(context)) {
            initErrorChannel();
            initForegroundChannel();
            initHighPrioErrorChannel();
        } else {
            Log.e(NotificationHandler.class.getName(), "Skipping initialization of notification channels. Missing permission.");
        }
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

    private void initHighPrioErrorChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = getHighPrioErrorChannelId();
            Log.d(NotificationHandler.class.getName(), "initHighPrioErrorChannel: " + id);
            String name = getResources().getString(R.string.notification_highprio_error_channel_name);
            String description = getResources().getString(R.string.notification_highprio_error_channel_description);
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
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

    public Set<Integer> getReservedIDs() {
        return Set.of(NOTIFICATION_FOREGROUND_START_ID, NOTIFICATION_MISSING_LOG_FOLDER_PERMISSION, NOTIFICATION_MISSING_DOWNLOAD_FOLDER_PERMISSION, NOTIFICATION_FOREGROUND_NETWORKTASK_RUNNING_SERVICE_ID);
    }

    private String getErrorChannelId() {
        return getResources().getString(R.string.notification_error_channel_id);
    }

    private String getHighPrioErrorChannelId() {
        return getResources().getString(R.string.notification_highprio_error_channel_id);
    }

    private String getForegroundChannelId() {
        return getResources().getString(R.string.notification_foreground_channel_id);
    }

    public INotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void sendMessageNotificationForegroundStart() {
        Log.d(NotificationHandler.class.getName(), "sendMessageNotificationForegroundStart");
        if (!permissionManager.hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot send notification because of missing permission.");
            return;
        }
        Notification notification = buildMessageNotificationForegroundStart();
        notificationManager.notify(NOTIFICATION_FOREGROUND_START_ID, notification);
    }

    public void sendMessageNotificationMissingLogFolderPermission() {
        Log.d(NotificationHandler.class.getName(), "sendMessageNotificationMissingLogFolderPermission");
        if (!permissionManager.hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot send notification because of missing permission.");
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        String text = getResources().getString(R.string.notification_log_permission_text, preferenceManager.getPreferenceArbitraryLogFolder());
        Notification notification = buildMessageNotificationFolderPermission(text);
        notificationManager.notify(NOTIFICATION_MISSING_LOG_FOLDER_PERMISSION, notification);
    }

    public void sendMessageNotificationMissingDownloadFolderPermission() {
        Log.d(NotificationHandler.class.getName(), "sendMessageNotificationMissingDownloadFolderPermission");
        if (!permissionManager.hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot send notification because of missing permission.");
            return;
        }
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        String text = getResources().getString(R.string.notification_download_permission_text, preferenceManager.getPreferenceArbitraryDownloadFolder());
        Notification notification = buildMessageNotificationFolderPermission(text);
        notificationManager.notify(NOTIFICATION_MISSING_DOWNLOAD_FOLDER_PERMISSION, notification);
    }

    public void sendMessageNotificationForNetworkTask(NetworkTask task, LogEntry logEntry) {
        Log.d(NotificationHandler.class.getName(), "sendMessageNotification, network task is " + task + ", log entry is " + logEntry);
        if (!permissionManager.hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot send notification because of missing permission.");
            return;
        }
        Notification notification = buildMessageNotificationForNetworkTask(task, logEntry);
        notificationManager.notify(task.getSchedulerId(), notification);
    }

    public void sendForegroundNotification(Notification notification) {
        Log.d(NotificationHandler.class.getName(), "sendForegroundNotification");
        if (!permissionManager.hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot send notification because of missing permission.");
            return;
        }
        notificationManager.notify(NOTIFICATION_FOREGROUND_NETWORKTASK_RUNNING_SERVICE_ID, notification);
    }

    private Notification buildMessageNotificationForNetworkTask(NetworkTask task, LogEntry logEntry) {
        Log.d(NotificationHandler.class.getName(), "buildMessageNotificationForNetworkTask, network task is " + task + ", log entry is " + logEntry);
        String title = getResources().getString(R.string.notification_title);
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        String addressText = String.format(getResources().getString(R.string.notification_address), new EnumMapping(getContext()).getAccessTypeAddressText(task.getAccessType()));
        String formattedAddressText = String.format(addressText, task.getAddress(), task.getPort());
        String text;
        String networkTaskTitle = UIUtil.getTextForNamedTask(context, task);
        if (logEntry.isSuccess()) {
            text = String.format(getResources().getString(R.string.notification_success_text), networkTaskTitle, formattedAddressText, timestampText, logEntry.getMessage() == null ? getResources().getString(R.string.string_none) : logEntry.getMessage());
        } else {
            text = String.format(getResources().getString(R.string.notification_error_text), networkTaskTitle, formattedAddressText, task.getFailureCount(), timestampText, logEntry.getMessage() == null ? getResources().getString(R.string.string_none) : logEntry.getMessage());
        }
        if (task.isHighPrio()) {
            messageNotificationBuilder = createHighPrioMessageNotificationBuilder();
        } else {
            messageNotificationBuilder = createMessageNotificationBuilder();
        }
        messageNotificationBuilder.setSmallIcon(logEntry.isSuccess() ? R.drawable.icon_notification_ok : R.drawable.icon_notification_failure).setContentTitle(title).setContentText(text).setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        messageNotificationBuilder.setPriority(task.isHighPrio() ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT);
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        if (task.isHighPrio() && preferenceManager.getPreferenceAlarmOnHighPrio()) {
            PendingIntent stopPendingIntent = createStopPendingIntent();
            messageNotificationBuilder.addAction(R.drawable.icon_stop, getResources().getString(R.string.notification_stop_alarm_text), stopPendingIntent);
        }
        return buildMessageNotification(NetworkTaskMainActivity.class, messageNotificationBuilder, NetworkTaskMainActivity.getNetworkTaskBundleKey(), task);
    }

    private PendingIntent createStopPendingIntent() {
        Log.d(NotificationHandler.class.getName(), "createStopPendingIntent");
        Intent stopIntent = new Intent(getContext(), StopAlarmReceiver.class);
        stopIntent.setPackage(getContext().getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), SchedulerIdGenerator.STOP_ALARM_SERVICE_ID, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            return PendingIntent.getBroadcast(getContext(), SchedulerIdGenerator.STOP_ALARM_SERVICE_ID, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private Notification buildMessageNotificationForegroundStart() {
        Log.d(NotificationHandler.class.getName(), "buildMessageNotificationForegroundStart");
        String title = getResources().getString(R.string.notification_title);
        String text = getResources().getString(R.string.notification_foreground_start_text);
        messageNotificationBuilder = createMessageNotificationBuilder();
        messageNotificationBuilder.setSmallIcon(R.drawable.icon_notification_foreground_start).setContentTitle(title).setContentText(text).setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return buildMessageNotification(NetworkTaskMainActivity.class, messageNotificationBuilder, null, null);
    }

    private Notification buildMessageNotificationFolderPermission(String notificationText) {
        Log.d(NotificationHandler.class.getName(), "buildMessageNotificationFolderPermission");
        String title = getResources().getString(R.string.notification_title);
        messageNotificationBuilder = createMessageNotificationBuilder();
        messageNotificationBuilder.setSmallIcon(R.drawable.icon_notification_failure).setContentTitle(title).setContentText(notificationText).setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText)).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return buildMessageNotification(GlobalSettingsActivity.class, messageNotificationBuilder, null, null);
    }

    private Notification buildMessageNotification(Class<? extends AppCompatActivity> activityClass, NotificationCompat.Builder notificationBuilder, String bundleKey, NetworkTask task) {
        Log.d(NotificationHandler.class.getName(), "buildMessageNotification, bundle key " + bundleKey + ", network task is " + task);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setVibrate(getVibrationPattern());
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(soundUri);
        }
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_ERROR);
        setActivityIntent(notificationBuilder, activityClass, bundleKey, task);
        return notificationBuilder.build();
    }

    public Notification buildForegroundNotification() {
        return buildForegroundNotification(false);
    }

    public Notification buildForegroundNotification(boolean withAlarm) {
        Log.d(NotificationHandler.class.getName(), "buildForegroundNotification");
        if (!permissionManager.hasPostNotificationsPermission(getContext())) {
            Log.e(NotificationHandler.class.getName(), "Cannot build foreground notification because of missing permission. Returning null.");
            return null;
        }
        String title = getResources().getString(R.string.notification_title);
        String text = getResources().getString(R.string.notification_foreground_text);
        foregroundNotificationBuilder = createForegroundNotificationBuilder();
        foregroundNotificationBuilder.setSmallIcon(R.drawable.icon_notification_foreground).setContentTitle(title).setContentText(text).setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setPriority(NotificationCompat.PRIORITY_LOW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            foregroundNotificationBuilder.setVibrate(null);
            foregroundNotificationBuilder.setSound(null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            foregroundNotificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }
        if (withAlarm) {
            PendingIntent stopPendingIntent = createStopPendingIntent();
            foregroundNotificationBuilder.addAction(R.drawable.icon_stop, getResources().getString(R.string.notification_stop_alarm_text), stopPendingIntent);
        }
        setActivityIntent(foregroundNotificationBuilder, NetworkTaskMainActivity.class, null, null);
        return foregroundNotificationBuilder.build();
    }

    private long[] getVibrationPattern() {
        return new long[]{0, 500, 250, 500};
    }

    private void setActivityIntent(NotificationCompat.Builder builder, Class<? extends AppCompatActivity> activityClass, String bundleKey, NetworkTask task) {
        Log.d(NotificationHandler.class.getName(), "setActivityIntent, bundle key " + bundleKey + ", network task is " + task);
        Intent activityIntent = new Intent(getContext(), activityClass);
        activityIntent.setPackage(getContext().getPackageName());
        int requestCode = 0;
        if (bundleKey != null && task != null) {
            activityIntent.putExtra(bundleKey, task.toBundle());
            requestCode = task.getSchedulerId();
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntentWithParentStack(activityIntent);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resultPendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
    }

    private INotificationManager createNotificationManager() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationManager(getContext());
    }

    private NotificationCompat.Builder createMessageNotificationBuilder() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationBuilder(getContext(), getErrorChannelId());
    }

    private NotificationCompat.Builder createHighPrioMessageNotificationBuilder() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationBuilder(getContext(), getHighPrioErrorChannelId());
    }

    private NotificationCompat.Builder createForegroundNotificationBuilder() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createNotificationBuilder(getContext(), getForegroundChannelId());
    }

    public NotificationCompat.Builder getMessageNotificationBuilder() {
        return messageNotificationBuilder;
    }

    public NotificationCompat.Builder getForegroundNotificationBuilder() {
        return foregroundNotificationBuilder;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
