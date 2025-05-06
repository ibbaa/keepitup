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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;

import java.util.HashSet;
import java.util.Set;

public class AlarmService extends Service {

    private final static Object LOCK = new Object();

    private final static Set<Integer> alarmTasks = new HashSet<>();

    private static boolean isRunning = false;

    private IAlarmMediaPlayer mediaPlayer;
    private NetworkTaskProcessServiceScheduler scheduler;
    private Handler handler;
    private Runnable timeoutCallback;

    @Override
    public void onCreate() {
        Log.d(AlarmService.class.getName(), "onCreate");
        scheduler = createNetworkTaskProcessServiceScheduler();
        alarmTasks.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AlarmService.class.getName(), "onStartCommand");
        boolean doStop = false;
        NetworkTask task;
        synchronized (LOCK) {
            setRunning(true);
            task = getNetworkTask(intent);
            Log.d(AlarmService.class.getName(), "onStartCommand, network task is " + task);
            if (task == null) {
                Log.e(AlarmService.class.getName(), "onStartCommand, network task is null");
                doStop = true;
            } else {
                alarmTasks.add(task.getSchedulerId());
            }
            startMediaPlayer();
        }
        scheduler.restartForegroundService(true);
        if (doStop) {
            stop();
        } else {
            int playbackTime = this.getResources().getInteger(R.integer.task_alarm_duration);
            int intentPlaybackTime = intent.getIntExtra(getResources().getString(R.string.task_alarm_duration_key), playbackTime);
            startPlayTimer(intentPlaybackTime);
            sendNetworkTaskUINotificationBroadcast(this);
        }
        return START_NOT_STICKY;
    }

    public void stop() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(AlarmService.class.getName(), "onDestroy");
        synchronized (LOCK) {
            setRunning(false);
            stopMediaPlayer();
            stopPlayTimer();
            alarmTasks.clear();
        }
        sendNetworkTaskUINotificationBroadcast(this);
        scheduler.restartForegroundService(false);
    }

    private void startMediaPlayer() {
        Log.d(AlarmService.class.getName(), "startMediaPlayer");
        if (mediaPlayer == null) {
            mediaPlayer = createAlarmMediaPlayer();
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.playAlarm();
        }
    }

    private void stopMediaPlayer() {
        Log.d(AlarmService.class.getName(), "stopMediaPlayer");
        if (mediaPlayer != null) {
            mediaPlayer.stopAlarm();
            mediaPlayer = null;
        }
    }

    public void startPlayTimer(int playbackTime) {
        Log.d(AlarmService.class.getName(), "startPlayTimer");
        Handler localHandler;
        Runnable localCallback;
        synchronized (LOCK) {
            stopPlayTimer();
            handler = new Handler();
            timeoutCallback = this::stopSelf;
            localHandler = handler;
            localCallback = timeoutCallback;
        }
        localHandler.postDelayed(localCallback, playbackTime * 1000L);
    }

    public void stopPlayTimer() {
        Log.d(AlarmService.class.getName(), "stopPlayTimer");
        synchronized (LOCK) {
            if (handler != null && timeoutCallback != null) {
                handler.removeCallbacks(timeoutCallback);
            }
            handler = null;
            timeoutCallback = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(AlarmService.class.getName(), "onBind");
        return null;
    }

    public static boolean isRunning() {
        synchronized (LOCK) {
            Log.d(AlarmService.class.getName(), "isRunning");
            return AlarmService.isRunning;
        }
    }

    private static void setRunning(boolean running) {
        Log.d(AlarmService.class.getName(), "setRunning");
        AlarmService.isRunning = running;
    }

    public static boolean isPlayingAlarm(NetworkTask task) {
        Log.d(AlarmService.class.getName(), "isPlayingAlarm, network task is " + task);
        if (task == null) {
            Log.e(AlarmService.class.getName(), "isPlayingAlarm, network task is null");
            return false;
        }
        if (!isRunning()) {
            return false;
        }
        synchronized (LOCK) {
            return alarmTasks.contains(task.getSchedulerId());
        }
    }

    public static void removeNetworkTask(Context context, NetworkTask task) {
        Log.d(AlarmService.class.getName(), "removeNetworkTask, network task is " + task);
        boolean doStop = false;
        synchronized (LOCK) {
            if (task == null) {
                Log.e(AlarmService.class.getName(), "removeNetworkTask, network task is null");
                return;
            }
            alarmTasks.remove(task.getSchedulerId());
            if (alarmTasks.isEmpty()) {
                Log.d(AlarmService.class.getName(), "No more alarm tasks.");
                doStop = isRunning();
                if (doStop) {
                    Log.d(AlarmService.class.getName(), "Service is currently running and will be stopped.");
                } else {
                    Log.d(AlarmService.class.getName(), "Service is currently not running. Stop not necessary.");
                }
            }
        }

        if (doStop) {
            context.stopService(new Intent(context, AlarmService.class));
        } else {
            sendNetworkTaskUINotificationBroadcast(context);
        }
    }

    public IAlarmMediaPlayer getMediaPlayer() {
        synchronized (LOCK) {
            return mediaPlayer;
        }
    }

    public NetworkTaskProcessServiceScheduler getNetworkTaskProcessServiceScheduler() {
        return scheduler;
    }

    private static void sendNetworkTaskUINotificationBroadcast(Context context) {
        String actionKey = context.getResources().getString(R.string.sync_action_key);
        String notifyAction = context.getResources().getString(R.string.sync_action_notify);
        Intent mainUIintent = new Intent(NetworkTaskMainUIBroadcastReceiver.class.getName());
        mainUIintent.setPackage(context.getPackageName());
        mainUIintent.putExtra(actionKey, notifyAction);
        context.sendBroadcast(mainUIintent);
    }

    private NetworkTask getNetworkTask(Intent intent) {
        Log.d(AlarmService.class.getName(), "getNetworkTask");
        Bundle taskBundle = intent.getBundleExtra(getNetworkTaskBundleKey());
        if (taskBundle == null) {
            return null;
        }
        return new NetworkTask(taskBundle);
    }

    public static String getNetworkTaskBundleKey() {
        return AlarmService.class.getName() + ".NetworkTaskBundle";
    }

    private IAlarmMediaPlayer createAlarmMediaPlayer() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(this);
        return factoryContributor.createServiceFactory().createAlarmMediaPlayer(this);
    }

    public NetworkTaskProcessServiceScheduler createNetworkTaskProcessServiceScheduler() {
        return new NetworkTaskProcessServiceScheduler(this);
    }
}
