/*
 * Copyright (c) 2024. Alwin Ibba
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

import net.ibbaa.keepitup.notification.INotificationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class MockNotificationManager implements INotificationManager {

    private final List<NotifyCall> notifyCalls;

    public MockNotificationManager() {
        notifyCalls = new ArrayList<>();
    }

    public List<NotifyCall> getNotifyCalls() {
        return Collections.unmodifiableList(notifyCalls);
    }

    public void reset() {
        notifyCalls.clear();
    }

    public boolean wasNotifyCalled() {
        return !notifyCalls.isEmpty();
    }

    @Override
    public void notify(int id, Notification notification) {
        notifyCalls.add(new NotifyCall(id, notification));
    }

    public record NotifyCall(int id, Notification notification) {

    }
}
