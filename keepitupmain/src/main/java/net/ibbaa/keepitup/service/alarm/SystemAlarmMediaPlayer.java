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

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import net.ibbaa.keepitup.logging.Log;

public class SystemAlarmMediaPlayer implements IAlarmMediaPlayer {

    private final Context context;
    private MediaPlayer mediaPlayer;

    public SystemAlarmMediaPlayer(Context context) {
        this.context = context;
    }

    @Override
    public void playAlarm() {
        Log.d(SystemAlarmMediaPlayer.class.getName(), "playAlarm");
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        if (alarmUri == null) {
            Log.e(SystemAlarmMediaPlayer.class.getName(), "No alarm uri found. Returning.");
            return;
        }
        Log.d(SystemAlarmMediaPlayer.class.getName(), "Alarm uri: " + alarmUri);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setDataSource(getContext(), alarmUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mediaPlayer.setAudioAttributes(getAlarmAudioAttributes());
            }
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception exc) {
            Log.e(SystemAlarmMediaPlayer.class.getName(), "Error playing alarm.", exc);
            stopAlarm();
        }
    }

    private AudioAttributes getAlarmAudioAttributes() {
        return new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
    }

    @Override
    public void stopAlarm() {
        Log.d(SystemAlarmMediaPlayer.class.getName(), "stopAlarm");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        Log.d(SystemAlarmMediaPlayer.class.getName(), "isPlaying");
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    private Context getContext() {
        return context;
    }
}
