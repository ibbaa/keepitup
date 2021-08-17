/*
 * Copyright (c) 2021. Alwin Ibba
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
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

public class NetworkTaskRunningNotificationService extends Service {

    public final static int NOTIFICATION_SERVICE_ID = 111;

    private NetworkTaskProcessServiceScheduler scheduler;

    @Override
    public void onCreate() {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onCreate");
        scheduler = new NetworkTaskProcessServiceScheduler(this);
        NotificationHandler notificationHandler = new NotificationHandler(this);
        Notification notification = notificationHandler.buildForegroundNotification();
        int foregroundServiceType = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
        }
        startNetworkTaskRunningNotificationForeground(NOTIFICATION_SERVICE_ID, notification, foregroundServiceType);
    }

    protected void startNetworkTaskRunningNotificationForeground(int id, @NonNull Notification notification, int foregroundServiceType) {
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
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.d(NetworkTaskRunningNotificationService.class.getName(), "extras bundle is null");
            return START_NOT_STICKY;
        }
        NetworkTask task = new NetworkTask(extras);
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "Network task is " + task);
        NetworkTaskProcessServiceScheduler.Delay delay = getDelay(extras);
        if (delay == null) {
            Log.d(NetworkTaskRunningNotificationService.class.getName(), "Delay is invalid");
            return START_NOT_STICKY;
        }
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "Delay is " + delay);
        getScheduler().reschedule(task, delay);
        return START_NOT_STICKY;
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

    @Override
    public void onDestroy() {
        Log.d(NetworkTaskRunningNotificationService.class.getName(), "onDestroy");
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().cancelAll();
        stopNetworkTaskRunningNotificationForeground(true);
    }

    protected void stopNetworkTaskRunningNotificationForeground(boolean removeNotification) {
        stopForeground(removeNotification);
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
}
