package de.ibba.keepitup.notification;

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

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.MockNotificationBuilder;
import de.ibba.keepitup.test.mock.MockNotificationManager;
import de.ibba.keepitup.test.mock.TestRegistry;

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
    public void testSendNotification() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime(), "Test");
        notificationHandler.sendNotification(networkTask, logEntry);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.getId());
        assertEquals("KEEPITUP_NOTIFICATION_CHANNEL", notifyCall.getNotification().getChannelId());
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getNotificationBuilder();
        assertEquals(R.drawable.icon_notification, notificationBuilder.getSmallIcon());
        assertEquals("Keep it up notification", notificationBuilder.getContentTitle());
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Timestamp: Mar 17, 1980 12:00:00 AM. Message: Test", notificationBuilder.getContentText());
        assertTrue(notificationBuilder.getStyle() instanceof NotificationCompat.BigTextStyle);
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, notificationBuilder.getPriority());
    }

    @Test
    public void testNotificationText() {
        NetworkTask networkTask = getNetworkTask1();
        LogEntry logEntry = getLogEntry(new GregorianCalendar(1995, Calendar.DECEMBER, 15, 13, 59, 51).getTime().getTime(), null);
        notificationHandler.sendNotification(networkTask, logEntry);
        MockNotificationBuilder notificationBuilder = (MockNotificationBuilder) notificationHandler.getNotificationBuilder();
        assertEquals("Execution of network task 2 failed. Host: 127.0.0.1. Timestamp: Dec 15, 1995 1:59:51 PM. Message: none", notificationBuilder.getContentText());
        networkTask = getNetworkTask2();
        logEntry = getLogEntry(new GregorianCalendar(2004, Calendar.FEBRUARY, 1, 5, 15, 51).getTime().getTime(), "message");
        notificationHandler.sendNotification(networkTask, logEntry);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getNotificationBuilder();
        assertEquals("Execution of network task 6 failed. Host: host.com Port: 23. Timestamp: Feb 1, 2004 5:15:51 AM. Message: message", notificationBuilder.getContentText());
        networkTask = getNetworkTask3();
        logEntry = getLogEntry(new GregorianCalendar(2016, Calendar.JULY, 25, 15, 1, 1).getTime().getTime(), "xyz");
        notificationHandler.sendNotification(networkTask, logEntry);
        notificationBuilder = (MockNotificationBuilder) notificationHandler.getNotificationBuilder();
        assertEquals("Execution of network task 11 failed. URL: http://www.test.com. Timestamp: Jul 25, 2016 3:01:01 PM. Message: xyz", notificationBuilder.getContentText());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(false);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(5);
        task.setSchedulerId(1);
        task.setAddress("host.com");
        task.setPort(23);
        task.setAccessType(AccessType.CONNECT);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(false);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(10);
        task.setSchedulerId(1);
        task.setAddress("http://www.test.com");
        task.setPort(456);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(false);
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
