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

package net.ibbaa.keepitup.service.alarm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.SchedulerIdGenerator;
import net.ibbaa.keepitup.test.mock.TestAlarmService;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.function.BooleanSupplier;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class AlarmServiceTest {

    private TestAlarmService service;

    @Before
    public void beforeEachTestMethod() {
        service = new TestAlarmService();
    }

    @Test
    public void testOnCreate() {
        service.onCreate();
        service.reset();
        assertTrue(service.wasStartAlarmForegroundCalled());
        TestAlarmService.StartAlarmForegroundCall startAlarmForegroundCall = service.getStartAlarmForegroundCalls().get(0);
        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK, startAlarmForegroundCall.foregroundServiceType());
        Notification notification = startAlarmForegroundCall.notification();
        assertEquals("KEEPITUP_ALARM_FOREGROUND_NOTIFICATION_CHANNEL", notification.getChannelId());
    }

    @Test
    public void testIsRunning() {
        Intent intent = new Intent(TestRegistry.getContext(), TestAlarmService.class);
        assertFalse(TestAlarmService.isRunning());
        int startFlag = service.onStartCommand(intent, 1, 1);
        assertEquals(Service.START_STICKY, startFlag);
        assertTrue(TestAlarmService.isRunning());
        service.onDestroy();
        assertFalse(TestAlarmService.isRunning());
        assertTrue(service.wasStopAlarmForegroundCalled());
    }

    @Test
    public void testStartStopService() throws Exception {
        Intent startIntent = new Intent(TestRegistry.getContext(), AlarmService.class);
        startIntent.setPackage(TestRegistry.getContext().getPackageName());
        assertFalse(TestAlarmService.isRunning());
        TestRegistry.getContext().startForegroundService(startIntent);
        waitUntil(AlarmService::isRunning);
        assertTrue(TestAlarmService.isRunning());
        Intent stopIntent = new Intent(TestRegistry.getContext(), StopAlarmReceiver.class);
        stopIntent.setPackage(TestRegistry.getContext().getPackageName());
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(TestRegistry.getContext(), SchedulerIdGenerator.STOP_ALARM_SERVICE_ID, stopIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        stopPendingIntent.send();
        waitUntil(() -> !AlarmService.isRunning());
        assertFalse(TestAlarmService.isRunning());
    }

    @SuppressWarnings({"BusyWait"})
    private void waitUntil(BooleanSupplier supplier) throws InterruptedException {
        int count = 30;
        while (!supplier.getAsBoolean() && count > 0) {
            count--;
            Thread.sleep(100);
        }
    }
}
