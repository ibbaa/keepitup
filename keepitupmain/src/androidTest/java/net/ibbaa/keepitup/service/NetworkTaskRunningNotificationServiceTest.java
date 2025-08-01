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

package net.ibbaa.keepitup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.test.mock.MockAlarmManager;
import net.ibbaa.keepitup.test.mock.MockFuture;
import net.ibbaa.keepitup.test.mock.MockNotificationBuilder;
import net.ibbaa.keepitup.test.mock.MockNotificationManager;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskRunningNotificationService;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskRunningNotificationServiceTest {

    private TestNetworkTaskRunningNotificationService service;
    private NetworkTaskProcessServiceScheduler scheduler;
    private NetworkTaskDAO networkTaskDAO;
    private MockAlarmManager alarmManager;
    private NotificationHandler notificationHandler;
    private MockNotificationManager notificationManager;

    @Before
    public void beforeEachTestMethod() {
        service = new TestNetworkTaskRunningNotificationService();
        service.onCreate();
        service.reset();
        notificationHandler = service.getNotificationHandler();
        notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        scheduler = service.getScheduler();
        scheduler.cancelAll();
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().reset();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        alarmManager = (MockAlarmManager) scheduler.getAlarmManager();
        alarmManager.reset();
    }

    @After
    public void afterEachTestMethod() {
        scheduler.cancelAll();
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().reset();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testOnCreate() {
        service.onCreate();
        assertTrue(service.wasStartNetworkTaskRunningNotificationForegroundCalled());
        TestNetworkTaskRunningNotificationService.StartNetworkTaskRunningNotificationForegroundCall startNetworkTaskRunningNotificationForegroundCall = service.getStartNetworkTaskRunningNotificationForegroundCalls().get(0);
        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC, startNetworkTaskRunningNotificationForegroundCall.foregroundServiceType());
        Notification notification = startNetworkTaskRunningNotificationForegroundCall.notification();
        assertEquals("KEEPITUP_FOREGROUND_NOTIFICATION_CHANNEL", notification.getChannelId());
    }

    @Test
    public void testOnStartCommand() {
        Intent intent = new Intent(TestRegistry.getContext(), TestNetworkTaskRunningNotificationService.class);
        int startFlag = service.onStartCommand(intent, 1, 1);
        assertEquals(Service.START_NOT_STICKY, startFlag);
        assertFalse(alarmManager.wasSetAlarmCalled());
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        intent.putExtras(task.toBundle());
        startFlag = service.onStartCommand(intent, 1, 1);
        assertEquals(Service.START_NOT_STICKY, startFlag);
        assertFalse(alarmManager.wasSetAlarmCalled());
        intent.putExtra(NetworkTaskRunningNotificationService.getRescheduleDelayKey(), NetworkTaskProcessServiceScheduler.Delay.INTERVAL.name());
        startFlag = service.onStartCommand(intent, 1, 1);
        assertEquals(Service.START_NOT_STICKY, startFlag);
        assertTrue(alarmManager.wasSetAlarmCalled());
        MockAlarmManager.SetAlarmCall setAlarmCall = alarmManager.getSetAlarmCalls().get(0);
        assertEquals(20 * 60 * 1000, setAlarmCall.delay());
    }

    @Test
    public void testOnDestroy() {
        MockFuture<?> future1 = new MockFuture<>();
        MockFuture<?> future2 = new MockFuture<>();
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().pool(1, future1);
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().pool(2, future2);
        service.onDestroy();
        assertTrue(future1.isCancelled());
        assertTrue(future2.isCancelled());
        assertTrue(service.wasStopNetworkTaskRunningNotificationForegroundCalled());
    }

    @Test
    public void testUpdateNotification() {
        Intent intent = new Intent(TestRegistry.getContext(), TestNetworkTaskRunningNotificationService.class);
        service.onStartCommand(intent, 1, 1);
        assertFalse(notificationManager.wasNotifyCalled());
        intent.putExtra(TestNetworkTaskRunningNotificationService.getWithAlarmKey(), false);
        service.onStartCommand(intent, 1, 1);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getForegroundNotificationBuilder();
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Network task running...", notificationBuilder.getContentText());
        assertNull(notificationBuilder.getActionText());
        intent.putExtra(TestNetworkTaskRunningNotificationService.getWithAlarmKey(), true);
        service.onStartCommand(intent, 1, 1);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getForegroundNotificationBuilder();
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Network task running...", notificationBuilder.getContentText());
        assertEquals("Stop alarm", notificationBuilder.getActionText());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setName("name");
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(20);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        task.setHighPrio(true);
        return task;
    }
}
