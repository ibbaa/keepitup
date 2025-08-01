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

import net.ibbaa.keepitup.service.alarm.IAlarmMediaPlayer;

public class MockAlarmMediaPlayer implements IAlarmMediaPlayer {

    private boolean playAlarmCalled;
    private boolean stopAlarmCalled;

    public MockAlarmMediaPlayer() {
        reset();
    }

    public void reset() {
        this.playAlarmCalled = false;
        this.stopAlarmCalled = false;
    }

    @Override
    public void playAlarm() {
        this.playAlarmCalled = true;
    }

    @Override
    public void stopAlarm() {
        this.stopAlarmCalled = true;
    }

    @Override
    public boolean isPlaying() {
        return this.playAlarmCalled && !this.stopAlarmCalled;
    }

    public boolean wasPlayAlarmCalled() {
        return this.playAlarmCalled;
    }

    public boolean wasStopAlarmCalled() {
        return this.stopAlarmCalled;
    }
}
