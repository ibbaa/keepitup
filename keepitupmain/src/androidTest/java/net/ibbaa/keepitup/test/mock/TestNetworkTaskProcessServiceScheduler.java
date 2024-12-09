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

import android.content.Context;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;

public class TestNetworkTaskProcessServiceScheduler extends NetworkTaskProcessServiceScheduler {

    private TimeBasedSuspensionScheduler timeBasedScheduler;
    private NetworkTask lastRescheduledTask;

    public TestNetworkTaskProcessServiceScheduler(Context context) {
        super(context);
        lastRescheduledTask = null;
    }

    public void setTimeBasedSuspensionScheduler(TimeBasedSuspensionScheduler timeBasedScheduler) {
        this.timeBasedScheduler = timeBasedScheduler;
    }

    public void reset() {
        lastRescheduledTask = null;
    }

    public NetworkTask getLastRescheduledTask() {
        return lastRescheduledTask;
    }

    public boolean wasRescheduleCalled() {
        return lastRescheduledTask != null;
    }

    @Override
    public TimeBasedSuspensionScheduler getTimeBasedSuspensionScheduler() {
        return timeBasedScheduler;
    }

    @Override
    public NetworkTask reschedule(NetworkTask networkTask, Delay delay) {
        lastRescheduledTask = networkTask;
        return super.reschedule(networkTask, delay);
    }
}
