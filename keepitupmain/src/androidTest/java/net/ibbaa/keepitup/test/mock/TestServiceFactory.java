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

import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import net.ibbaa.keepitup.notification.INotificationManager;
import net.ibbaa.keepitup.resources.ISystemSetup;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.ServiceFactory;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.INetworkManager;
import net.ibbaa.keepitup.service.ITimeService;

public class TestServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createAlarmManager");
        return new MockAlarmManager();
    }

    @Override
    public INotificationManager createNotificationManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createNotificatioManager");
        return new MockNotificationManager();
    }

    @Override
    public NotificationCompat.Builder createNotificationBuilder(Context context, String channelId) {
        Log.d(TestServiceFactory.class.getName(), "createNotificationBuilder");
        return new MockNotificationBuilder(context, channelId);
    }

    @Override
    public INetworkManager createNetworkManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createNetworkManager");
        return new MockNetworkManager();
    }

    @Override
    public ITimeService createTimeService() {
        Log.d(TestServiceFactory.class.getName(), "createTimeService");
        return new MockTimeService();
    }

    @Override
    public ISystemSetup createSystemSetup(Context context, String implementation) {
        return new JSONSystemSetup(context);
    }
}
