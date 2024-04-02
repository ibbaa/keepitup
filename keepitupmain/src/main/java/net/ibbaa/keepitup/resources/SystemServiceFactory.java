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

package net.ibbaa.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.notification.INotificationManager;
import net.ibbaa.keepitup.notification.SystemNotificationManager;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.INetworkManager;
import net.ibbaa.keepitup.service.ITimeService;
import net.ibbaa.keepitup.service.SystemAlarmManager;
import net.ibbaa.keepitup.service.SystemNetworkManager;
import net.ibbaa.keepitup.service.SystemTimeService;

public class SystemServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createAlarmManager");
        return new SystemAlarmManager(context);
    }

    @Override
    public INotificationManager createNotificationManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createNotificationManager");
        return new SystemNotificationManager(context);
    }

    @Override
    public NotificationCompat.Builder createNotificationBuilder(Context context, String channelId) {
        Log.d(SystemServiceFactory.class.getName(), "createNotificationBuilder");
        return new NotificationCompat.Builder(context, channelId);
    }

    @Override
    public INetworkManager createNetworkManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createNetworkManager");
        return new SystemNetworkManager(context);
    }

    @Override
    public ITimeService createTimeService() {
        Log.d(SystemServiceFactory.class.getName(), "createTimeService");
        return new SystemTimeService();
    }
}
