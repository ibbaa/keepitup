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

package net.ibbaa.keepitup.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.ExceptionUtil;
import net.ibbaa.keepitup.util.StringUtil;

public class NetworkTaskRunningNotificationService extends Service {

    private NetworkTaskProcessServiceScheduler scheduler;
    private NotificationHandler notificationHandler;
    private boolean started;

    @Override
    public void onCreate() {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onCreate");
        scheduler = new NetworkTaskProcessServiceScheduler(this);
        notificationHandler = new NotificationHandler(this, getPermissionManager());
        NotificationHandler notificationHandler = new NotificationHandler(this, getPermissionManager());
        Notification notification = notificationHandler.buildForegroundNotification();
        int foregroundServiceType = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
        }
        startNetworkTaskRunningNotificationForeground(notification, foregroundServiceType);
    }

    protected void startNetworkTaskRunningNotificationForeground(@NonNull Notification notification, int foregroundServiceType) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NotificationHandler.NOTIFICATION_FOREGROUND_NETWORKTASK_RUNNING_SERVICE_ID, notification, foregroundServiceType);
            } else {
                startForeground(NotificationHandler.NOTIFICATION_FOREGROUND_NETWORKTASK_RUNNING_SERVICE_ID, notification);
            }
            setStarted(true);
        } catch (Exception exc) {
            Log.e(NetworkTaskRunningNotificationService.class.getName(), "startService: Error starting the foreground service.", exc);
            if (ExceptionUtil.isForegroundServiceStartNotAllowedException(exc)) {
                Log.d(NetworkTaskProcessServiceScheduler.class.getName(), "Sending notification to open app");
                notificationHandler.sendMessageNotificationForegroundStart();
            }
            setStarted(false);
        }
    }

    public static String getRescheduleDelayKey() {
        return NetworkTaskRunningNotificationService.class.getSimpleName() + "RescheduleDelay";
    }

    public static String getWithAlarmKey() {
        return NetworkTaskRunningNotificationService.class.getSimpleName() + "WithAlarm";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onStartCommand");
        if (intent == null) {
            Log.e(NetworkTaskRunningNotificationService.class.getName(), "intent is null");
            return START_NOT_STICKY;
        }
        Bundle extras = intent.getExtras();
        if (extras == null || extras.isEmpty()) {
            Log.e(NetworkTaskRunningNotificationService.class.getName(), "extras bundle is null");
            return START_NOT_STICKY;
        }
        if (isStarted()) {
            updateNotification(extras);
            notificationHandler.cancelMessageNotificationForegroundStart();
        }
        NetworkTaskProcessServiceScheduler.Delay delay = getDelay(extras);
        if (delay == null) {
            Log.e(NetworkTaskRunningNotificationService.class.getName(), "Delay is invalid");
            return START_NOT_STICKY;
        }
        NetworkTask task = new NetworkTask(extras);
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "Network task is " + task);
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "Delay is " + delay);
        getScheduler().reschedule(task, delay);
        if (!isStarted()) {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void updateNotification(Bundle extras) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "updateNotification");
        boolean withAlarm = getWithAlarm(extras);
        Notification notification = notificationHandler.buildForegroundNotification(withAlarm);
        notificationHandler.sendForegroundNotification(notification);
    }

    private NetworkTaskProcessServiceScheduler.Delay getDelay(Bundle extras) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "getDelay");
        String delayString = BundleUtil.stringFromBundle(getRescheduleDelayKey(), extras);
        if (StringUtil.isEmpty(delayString)) {
            Log.e(NetworkTaskRunningNotificationService.class.getName(), "No delay specified");
            return null;
        }
        try {
            return NetworkTaskProcessServiceScheduler.Delay.valueOf(delayString);
        } catch (IllegalArgumentException exc) {
            Log.e(NetworkTaskRunningNotificationService.class.getName(), NetworkTaskProcessServiceScheduler.Delay.class.getSimpleName() + "." + delayString + " does not exist", exc);
            return null;
        }
    }

    private boolean getWithAlarm(Bundle extras) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "getWithAlarm");
        return BundleUtil.booleanFromBundle(getWithAlarmKey(), extras);
    }

    @Override
    public void onDestroy() {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onDestroy");
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().cancelAll();
        stopNetworkTaskRunningNotificationForeground();
        setStarted(false);
    }

    protected void stopNetworkTaskRunningNotificationForeground() {
        stopForeground(true);
    }

    protected boolean isStarted() {
        return started;
    }

    protected void setStarted(boolean started) {
        this.started = started;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onBind");
        return null;
    }

    public NetworkTaskProcessServiceScheduler getScheduler() {
        return scheduler;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public IPermissionManager getPermissionManager() {
        return new PermissionManager();
    }
}
