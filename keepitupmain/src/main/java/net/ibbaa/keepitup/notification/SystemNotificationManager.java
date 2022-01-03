/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import net.ibbaa.keepitup.logging.Log;

public class SystemNotificationManager implements INotificationManager {

    private final NotificationManager notificationManager;

    public SystemNotificationManager(Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void notify(int id, Notification notification) {
        Log.d(SystemNotificationManager.class.getName(), "Sending notification with id " + id);
        notificationManager.notify(id, notification);
    }
}
