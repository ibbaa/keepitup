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

package net.ibbaa.keepitup.service.alarm;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;

public class AlarmService extends Service {

    private static boolean isRunning = false;

    private IAlarmMediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable timeoutCallback;

    @Override
    public void onCreate() {
        Log.d(AlarmService.class.getName(), "onCreate");
        NotificationHandler notificationHandler = new NotificationHandler(this, getPermissionManager());
        Notification notification = notificationHandler.buildAlarmForegroundNotification();
        int foregroundServiceType = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            foregroundServiceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
        }
        startAlarmForeground(notification, foregroundServiceType);
    }

    protected void startAlarmForeground(@NonNull Notification notification, int foregroundServiceType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NotificationHandler.NOTIFICATION_FOREGROUND_ALARM_SERVICE_ID, notification, foregroundServiceType);
        } else {
            startForeground(NotificationHandler.NOTIFICATION_FOREGROUND_ALARM_SERVICE_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AlarmService.class.getName(), "onStartCommand");
        setRunning(true);
        startMediaPlayer();
        int playbackTime = this.getResources().getInteger(R.integer.task_alarm_duration);
        int intentPlaybackTime = intent.getIntExtra(getResources().getString(R.string.task_alarm_duration_key), playbackTime);
        startPlayTimer(intentPlaybackTime);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(AlarmService.class.getName(), "onDestroy");
        stopAlarmForeground();
        setRunning(false);
        stopMediaPlayer();
        stopPlayTimer();
    }

    protected void stopAlarmForeground() {
        stopForeground(true);
    }

    private synchronized void startMediaPlayer() {
        Log.d(AlarmService.class.getName(), "startMediaPlayer");
        if (mediaPlayer == null) {
            mediaPlayer = createAlarmMediaPlayer();
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.playAlarm();
        }
    }

    private synchronized void stopMediaPlayer() {
        Log.d(AlarmService.class.getName(), "stopMediaPlayer");
        if (mediaPlayer != null) {
            mediaPlayer.stopAlarm();
            mediaPlayer = null;
        }
    }

    public synchronized void startPlayTimer(int playbackTime) {
        stopPlayTimer();
        Log.d(AlarmService.class.getName(), "startPlayTimer");
        timeoutCallback = this::stopSelf;
        handler = new Handler();
        handler.postDelayed(timeoutCallback, playbackTime * 1000L);
    }

    public synchronized void stopPlayTimer() {
        Log.d(AlarmService.class.getName(), "stopPlayTimer");
        if (handler != null && timeoutCallback != null) {
            handler.removeCallbacks(timeoutCallback);
        }
        handler = null;
        timeoutCallback = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(AlarmService.class.getName(), "onBind");
        return null;
    }

    public synchronized static boolean isRunning() {
        return AlarmService.isRunning;
    }

    private synchronized static void setRunning(boolean running) {
        AlarmService.isRunning = running;
    }

    public IPermissionManager getPermissionManager() {
        return new PermissionManager();
    }

    public synchronized IAlarmMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private IAlarmMediaPlayer createAlarmMediaPlayer() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(this);
        return factoryContributor.createServiceFactory().createAlarmMediaPlayer(this);
    }
}
