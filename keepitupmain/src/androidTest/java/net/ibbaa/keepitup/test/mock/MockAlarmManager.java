/*
 * Copyright (c) 2023. Alwin Ibba
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

import android.app.PendingIntent;

import net.ibbaa.keepitup.service.IAlarmManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockAlarmManager implements IAlarmManager {

    private final List<SetAlarmCall> setAlarmCalls;
    private final List<CancelAlarmCall> cancelAlarmCalls;

    public MockAlarmManager() {
        setAlarmCalls = new ArrayList<>();
        cancelAlarmCalls = new ArrayList<>();
    }

    public List<SetAlarmCall> getSetAlarmCalls() {
        return Collections.unmodifiableList(setAlarmCalls);
    }

    public List<CancelAlarmCall> getCancelAlarmCalls() {
        return Collections.unmodifiableList(cancelAlarmCalls);
    }

    public void reset() {
        setAlarmCalls.clear();
        cancelAlarmCalls.clear();
    }

    public boolean wasSetAlarmCalled() {
        return !setAlarmCalls.isEmpty();
    }

    public boolean wasCancelAlarmCalled() {
        return !cancelAlarmCalls.isEmpty();
    }

    @Override
    public boolean canScheduleAlarms() {
        return true;
    }

    @Override
    public void setAlarm(long delay, PendingIntent pendingIntent) {
        setAlarmCalls.add(new SetAlarmCall(delay, pendingIntent));
    }

    @Override
    public void cancelAlarm(PendingIntent pendingIntent) {
        cancelAlarmCalls.add(new CancelAlarmCall(pendingIntent));
    }

    public static class SetAlarmCall {

        private final long delay;
        private final PendingIntent pendingIntent;

        public SetAlarmCall(long delay, PendingIntent pendingIntent) {
            this.delay = delay;
            this.pendingIntent = pendingIntent;
        }

        public long getDelay() {
            return delay;
        }

        public PendingIntent getPendingIntent() {
            return pendingIntent;
        }
    }

    public static class CancelAlarmCall {

        private final PendingIntent pendingIntent;

        public CancelAlarmCall(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;
        }

        public PendingIntent getPendingIntent() {
            return pendingIntent;
        }
    }
}
