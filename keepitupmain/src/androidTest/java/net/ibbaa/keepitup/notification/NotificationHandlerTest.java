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

package net.ibbaa.keepitup.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Notification;

import androidx.core.app.NotificationCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockNotificationBuilder;
import net.ibbaa.keepitup.test.mock.MockNotificationManager;
import net.ibbaa.keepitup.test.mock.MockPermissionManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NotificationHandlerTest {

    private NotificationHandler notificationHandler;
    private MockNotificationManager notificationManager;
    private MockPermissionManager permissionManager;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        setLocale(Locale.US);
        permissionManager = new MockPermissionManager();
        notificationHandler = new NotificationHandler(TestRegistry.getContext(), permissionManager);
        notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
    }

    public void setLocale(Locale locale) {
        InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().setLocale(locale);
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskSuccess() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", true);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.id());
        assertEquals("KEEPITUP_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_ok, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 successful. Host: 127.0.0.1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
        assertNull(notificationBuilder.getActionText());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskSuccessHighPrioNoAlarm() {
        NetworkTask networkTask = getNetworkTask1();
        networkTask.setHighPrio(true);
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", true);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.id());
        assertEquals("KEEPITUP_HIGH_PRIO_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_ok, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 successful. Host: 127.0.0.1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.getPriority());
        assertNull(notificationBuilder.getActionText());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskSuccessHighPrioWithAlarm() {
        NetworkTask networkTask = getNetworkTask1();
        networkTask.setHighPrio(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", true);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.id());
        assertEquals("KEEPITUP_HIGH_PRIO_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_ok, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 successful. Host: 127.0.0.1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.getPriority());
        assertEquals("Stop alarm", notificationBuilder.getActionText());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskFailure() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", false);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.id());
        assertEquals("KEEPITUP_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_failure, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Failures since last success: 1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
        assertNull(notificationBuilder.getActionText());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskFailureHighPrioNoAlarm() {
        NetworkTask networkTask = getNetworkTask1();
        networkTask.setHighPrio(true);
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", false);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.id());
        assertEquals("KEEPITUP_HIGH_PRIO_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_failure, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Failures since last success: 1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.getPriority());
        assertNull(notificationBuilder.getActionText());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskFailureHighPrioWithAlarm() {
        NetworkTask networkTask = getNetworkTask1();
        networkTask.setHighPrio(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", false);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.id());
        assertEquals("KEEPITUP_HIGH_PRIO_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_failure, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Failures since last success: 1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.getPriority());
        assertEquals("Stop alarm", notificationBuilder.getActionText());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskWithoutPermission() {
        permissionManager.setHasPostNotificationsPermission(false);
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test", false);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSendMessageNotificationForNetworkTaskText() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1995, Calendar.DECEMBER, 15, 13, 59, 51).getTime().getTime(), null, false);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Failures since last success: 1. Timestamp: Dec 15, 1995 1:59:51 PM. Message: none", notificationBuilder.getContentText());
        networkTask = getNetworkTask2();
        logEntry = getLogEntry(new GregorianCalendar(2004, Calendar.FEBRUARY, 1, 5, 15, 51).getTime().getTime(), "message", false);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals("Execution of network task 6 failed. Host: host.com Port: 23. Failures since last success: 2. Timestamp: Feb 1, 2004 5:15:51 AM. Message: message", notificationBuilder.getContentText());
        networkTask = getNetworkTask3();
        logEntry = getLogEntry(new GregorianCalendar(2016, Calendar.JULY, 25, 15, 1, 1).getTime().getTime(), "xyz", true);
        notificationHandler.sendMessageNotificationForNetworkTask(networkTask, logEntry);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals("Execution of network task 11 successful. URL: http://www.test.com. Timestamp: Jul 25, 2016 3:01:01 PM. Message: xyz", notificationBuilder.getContentText());
    }

    @Test
    public void testSendMessageNotificationForegroundStart() {
        notificationHandler.sendMessageNotificationForegroundStart();
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(NotificationHandler.NOTIFICATION_FOREGROUND_START_ID, notifyCall.id());
        assertEquals("KEEPITUP_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_foreground_start, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Please click here to open the app after device boot to start the foreground service for running network tasks", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
    }

    @Test
    public void testSendMessageNotificationForegroundStartWithoutPermission() {
        permissionManager.setHasPostNotificationsPermission(false);
        notificationHandler.sendMessageNotificationForegroundStart();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSendMessageNotificationMissingLogFolderPermission() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceArbitraryLogFolder("Test");
        notificationHandler.sendMessageNotificationMissingLogFolderPermission();
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(NotificationHandler.NOTIFICATION_MISSING_LOG_FOLDER_PERMISSION, notifyCall.id());
        assertEquals("KEEPITUP_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_failure, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Missing write permission for configured log folder: Test. Please click here to grant the permission.", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
    }

    @Test
    public void testSendMessageNotificationMissingLogFolderPermissionWithoutPermission() {
        permissionManager.setHasPostNotificationsPermission(false);
        notificationHandler.sendMessageNotificationMissingLogFolderPermission();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSendMessageNotificationMissingDownloadFolderPermission() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceArbitraryDownloadFolder("Test");
        notificationHandler.sendMessageNotificationMissingDownloadFolderPermission();
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(NotificationHandler.NOTIFICATION_MISSING_DOWNLOAD_FOLDER_PERMISSION, notifyCall.id());
        assertEquals("KEEPITUP_ERROR_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getMessageNotificationBuilder();
        assertEquals(R.drawable.icon_notification_failure, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Missing write permission for configured download folder: Test. Please click here to grant the permission.", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
    }

    @Test
    public void testSendMessageNotificationMissingDownloadolderPermissionWithoutPermission() {
        permissionManager.setHasPostNotificationsPermission(false);
        notificationHandler.sendMessageNotificationMissingDownloadFolderPermission();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testBuildForegroundNotification() {
        Notification notification = notificationHandler.buildForegroundNotification();
        assertEquals("KEEPITUP_FOREGROUND_NOTIFICATION_CHANNEL", notification.getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getForegroundNotificationBuilder();
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Network task running...", notificationBuilder.getContentText());
        assertEquals(R.drawable.icon_notification_foreground, notificationBuilder.getSmallIcon());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_LOW, notificationBuilder.getPriority());
        assertNull(notificationBuilder.getActionText());
    }

    @Test
    public void testBuildForegroundNotificationWithStopAlarmAction() {
        Notification notification = notificationHandler.buildForegroundNotification(true);
        assertEquals("KEEPITUP_FOREGROUND_NOTIFICATION_CHANNEL", notification.getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getForegroundNotificationBuilder();
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Network task running...", notificationBuilder.getContentText());
        assertEquals(R.drawable.icon_notification_foreground, notificationBuilder.getSmallIcon());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_LOW, notificationBuilder.getPriority());
        assertEquals("Stop alarm", notificationBuilder.getActionText());
    }

    @Test
    public void testBuildForegroundNotificationWithoutPermission() {
        permissionManager.setHasPostNotificationsPermission(false);
        Notification notification = notificationHandler.buildForegroundNotification();
        assertNull(notification);
    }

    @Test
    public void testSendForegroundNotification() {
        Notification notification = notificationHandler.buildForegroundNotification();
        notificationHandler.sendForegroundNotification(notification);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(NotificationHandler.NOTIFICATION_FOREGROUND_NETWORKTASK_RUNNING_SERVICE_ID, notifyCall.id());
        assertEquals("KEEPITUP_FOREGROUND_NOTIFICATION_CHANNEL", notifyCall.notification().getChannelId());
    }

    @Test
    public void testGetReservedIDs() {
        Set<Integer> reservedIDs = notificationHandler.getReservedIDs();
        assertTrue(reservedIDs.contains(NotificationHandler.NOTIFICATION_FOREGROUND_START_ID));
        assertTrue(reservedIDs.contains(NotificationHandler.NOTIFICATION_MISSING_LOG_FOLDER_PERMISSION));
        assertTrue(reservedIDs.contains(NotificationHandler.NOTIFICATION_MISSING_DOWNLOAD_FOLDER_PERMISSION));
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(false);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        task.setHighPrio(false);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(5);
        task.setSchedulerId(1);
        task.setInstances(0);
        task.setAddress("host.com");
        task.setPort(23);
        task.setAccessType(AccessType.CONNECT);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(false);
        task.setLastScheduled(1);
        task.setFailureCount(2);
        task.setHighPrio(false);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(10);
        task.setSchedulerId(1);
        task.setInstances(0);
        task.setAddress("http://www.test.com");
        task.setPort(456);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(20);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(false);
        task.setLastScheduled(1);
        task.setFailureCount(3);
        task.setHighPrio(true);
        return task;
    }

    private LogEntry getLogEntry(long timestamp, String message, boolean success) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(1);
        logEntry.setSuccess(success);
        logEntry.setTimestamp(timestamp);
        logEntry.setMessage(message);
        return logEntry;
    }
}
