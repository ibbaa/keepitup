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

import net.ibbaa.keepitup.notification.INotificationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class MockNotificationManager implements INotificationManager {

    private final List<NotifyCall> notifyCalls;
    private final List<CancelCall> cancelCalls;

    public MockNotificationManager() {
        notifyCalls = new ArrayList<>();
        cancelCalls = new ArrayList<>();
    }

    public List<NotifyCall> getNotifyCalls() {
        return Collections.unmodifiableList(notifyCalls);
    }

    public List<CancelCall> getCancelCalls() {
        return Collections.unmodifiableList(cancelCalls);
    }

    public void reset() {
        notifyCalls.clear();
    }

    public boolean wasNotifyCalled() {
        return !notifyCalls.isEmpty();
    }

    public boolean wasCancelCalled() {
        return !cancelCalls.isEmpty();
    }

    @Override
    public void notify(int id, Notification notification) {
        notifyCalls.add(new NotifyCall(id, notification));
    }

    @Override
    public void cancel(int id) {
        cancelCalls.add(new CancelCall(id));
    }

    public record NotifyCall(int id, Notification notification) {

    }

    public record CancelCall(int id) {

    }
}
