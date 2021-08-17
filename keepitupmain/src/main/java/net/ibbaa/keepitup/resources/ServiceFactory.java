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

package net.ibbaa.keepitup.resources;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import net.ibbaa.keepitup.notification.INotificationManager;
import net.ibbaa.keepitup.service.IAlarmManager;
import net.ibbaa.keepitup.service.INetworkManager;
import net.ibbaa.keepitup.service.ITimeService;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    INotificationManager createNotificationManager(Context context);

    NotificationCompat.Builder createNotificationBuilder(Context context, String channelId);

    INetworkManager createNetworkManager(Context context);

    ITimeService createTimeService();

    ISystemSetup createSystemSetup(Context context, String implementation);
}
