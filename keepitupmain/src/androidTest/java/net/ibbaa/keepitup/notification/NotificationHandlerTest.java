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

import androidx.core.app.NotificationCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.MockNotificationBuilder;
import net.ibbaa.keepitup.test.mock.MockNotificationManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NotificationHandlerTest {

    private NotificationHandler notificationHandler;
    private MockNotificationManager notificationManager;

    @Before
    public void beforeEachTestMethod() {
        setLocale(Locale.US);
        notificationHandler = new NotificationHandler(TestRegistry.getContext());
        notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
    }

    public void setLocale(Locale locale) {
        InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().setLocale(locale);
    }

    @Test
    public void testSendErrorNotification() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test");
        notificationHandler.sendErrorNotification(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.getId());
        assertEquals("KEEPITUP_ERROR_NOTIFICATION_CHANNEL", notifyCall.getNotification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getErrorNotificationBuilder();
        assertEquals(R.drawable.icon_notification, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
    }

    @Test
    public void testErrorNotificationText() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1995, Calendar.DECEMBER, 15, 13, 59, 51).getTime().getTime(), null);
        notificationHandler.sendErrorNotification(networkTask, logEntry);
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getErrorNotificationBuilder();
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Timestamp: Dec 15, 1995 1:59:51 PM. Message: none", notificationBuilder.getContentText());
        networkTask = getNetworkTask2();
        logEntry = getLogEntry(new GregorianCalendar(2004, Calendar.FEBRUARY, 1, 5, 15, 51).getTime().getTime(), "message");
        notificationHandler.sendErrorNotification(networkTask, logEntry);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getErrorNotificationBuilder();
        assertEquals("Execution of network task 6 failed. Host: host.com Port: 23. Timestamp: Feb 1, 2004 5:15:51 AM. Message: message", notificationBuilder.getContentText());
        networkTask = getNetworkTask3();
        logEntry = getLogEntry(new GregorianCalendar(2016, Calendar.JULY, 25, 15, 1, 1).getTime().getTime(), "xyz");
        notificationHandler.sendErrorNotification(networkTask, logEntry);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getErrorNotificationBuilder();
        assertEquals("Execution of network task 11 failed. URL: http://www.test.com. Timestamp: Jul 25, 2016 3:01:01 PM. Message: xyz", notificationBuilder.getContentText());
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
        task.setNotification(true);
        task.setRunning(false);
        task.setLastScheduled(1);
        return task;
    }

    private LogEntry getLogEntry(long timestamp, String message) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(1);
        logEntry.setSuccess(true);
        logEntry.setTimestamp(timestamp);
        logEntry.setMessage(message);
        return logEntry;
    }
}
