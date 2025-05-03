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

package net.ibbaa.keepitup.test.mock;

import android.app.Notification;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.service.alarm.AlarmService;
import net.ibbaa.keepitup.ui.permission.IPermissionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestAlarmService extends AlarmService {

    private final List<StartAlarmForegroundCall> startAlarmForegroundCalls;
    private final List<StopAlarmForegroundCall> stopAlarmForegroundCalls;
    private final List<StartPlayTimerCall> startPlayTimerCalls;
    private final List<StopPlayTimerCall> stopPlayTimerCalls;

    public TestAlarmService() {
        this.startAlarmForegroundCalls = new ArrayList<>();
        this.stopAlarmForegroundCalls = new ArrayList<>();
        this.startPlayTimerCalls = new ArrayList<>();
        this.stopPlayTimerCalls = new ArrayList<>();
        attachBaseContext(TestRegistry.getContext());
    }

    public void reset() {
        startAlarmForegroundCalls.clear();
        stopAlarmForegroundCalls.clear();
        startPlayTimerCalls.clear();
        stopPlayTimerCalls.clear();
    }

    public List<StartAlarmForegroundCall> getStartAlarmForegroundCalls() {
        return Collections.unmodifiableList(startAlarmForegroundCalls);
    }

    public boolean wasStartAlarmForegroundCalled() {
        return !startAlarmForegroundCalls.isEmpty();
    }

    public boolean wasStopAlarmForegroundCalled() {
        return !stopAlarmForegroundCalls.isEmpty();
    }

    public boolean wasStartPlayTimerCalled() {
        return !startPlayTimerCalls.isEmpty();
    }

    public boolean wasStopPlayTimerCalled() {
        return !stopPlayTimerCalls.isEmpty();
    }

    @Override
    protected void startAlarmForeground(@NonNull Notification notification, int foregroundServiceType) {
        startAlarmForegroundCalls.add(new StartAlarmForegroundCall(notification, foregroundServiceType));
    }

    @Override
    protected void stopAlarmForeground() {
        stopAlarmForegroundCalls.add(new StopAlarmForegroundCall());
    }

    @Override
    public synchronized void startPlayTimer(int playbackTime) {
        startPlayTimerCalls.add(new StartPlayTimerCall(playbackTime));
    }

    @Override
    public synchronized void stopPlayTimer() {
        stopPlayTimerCalls.add(new StopPlayTimerCall());
    }

    @Override
    public IPermissionManager getPermissionManager() {
        return new MockPermissionManager();
    }

    public record StartAlarmForegroundCall(Notification notification, int foregroundServiceType) {

    }

    public record StopAlarmForegroundCall() {

    }

    public record StartPlayTimerCall(int playbackTime) {

    }

    public record StopPlayTimerCall() {

    }
}
