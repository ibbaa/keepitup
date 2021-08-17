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

package net.ibbaa.keepitup.test.mock;

import android.app.Notification;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ibbaa.keepitup.service.NetworkTaskRunningNotificationService;

public class TestNetworkTaskRunningNotificationService extends NetworkTaskRunningNotificationService {

    private final List<StartNetworkTaskRunningNotificationForegroundCall> startNetworkTaskRunningNotificationForegroundCalls;
    private final List<StopNetworkTaskRunningNotificationForegroundCall> stopNetworkTaskRunningNotificationForegroundCalls;

    public TestNetworkTaskRunningNotificationService() {
        this.startNetworkTaskRunningNotificationForegroundCalls = new ArrayList<>();
        stopNetworkTaskRunningNotificationForegroundCalls = new ArrayList<>();
        attachBaseContext(TestRegistry.getContext());
    }

    public void reset() {
        startNetworkTaskRunningNotificationForegroundCalls.clear();
        stopNetworkTaskRunningNotificationForegroundCalls.clear();
    }

    public List<StartNetworkTaskRunningNotificationForegroundCall> getStartNetworkTaskRunningNotificationForegroundCalls() {
        return Collections.unmodifiableList(startNetworkTaskRunningNotificationForegroundCalls);
    }

    public List<StopNetworkTaskRunningNotificationForegroundCall> getStopNetworkTaskRunningNotificationForegroundCalls() {
        return Collections.unmodifiableList(stopNetworkTaskRunningNotificationForegroundCalls);
    }

    public boolean wasStartNetworkTaskRunningNotificationForegroundCalled() {
        return !startNetworkTaskRunningNotificationForegroundCalls.isEmpty();
    }

    public boolean wasStopNetworkTaskRunningNotificationForegroundCalled() {
        return !stopNetworkTaskRunningNotificationForegroundCalls.isEmpty();
    }

    @Override
    protected void startNetworkTaskRunningNotificationForeground(int id, @NonNull Notification notification, int foregroundServiceType) {
        startNetworkTaskRunningNotificationForegroundCalls.add(new StartNetworkTaskRunningNotificationForegroundCall(id, notification, foregroundServiceType));
    }

    @Override
    protected void stopNetworkTaskRunningNotificationForeground(boolean removeNotification) {
        stopNetworkTaskRunningNotificationForegroundCalls.add(new StopNetworkTaskRunningNotificationForegroundCall(removeNotification));
    }

    public static class StartNetworkTaskRunningNotificationForegroundCall {

        private final int id;
        private final Notification notification;
        private final int foregroundServiceType;

        public StartNetworkTaskRunningNotificationForegroundCall(int id, Notification notification, int foregroundServiceType) {
            this.id = id;
            this.notification = notification;
            this.foregroundServiceType = foregroundServiceType;
        }

        public int getId() {
            return id;
        }

        public Notification getNotification() {
            return notification;
        }

        public int getForegroundServiceType() {
            return foregroundServiceType;
        }
    }

    public static class StopNetworkTaskRunningNotificationForegroundCall {

        private final boolean removeNotification;

        public StopNetworkTaskRunningNotificationForegroundCall(boolean removeNotification) {
            this.removeNotification = removeNotification;
        }

        public boolean isRemoveNotification() {
            return removeNotification;
        }
    }
}
