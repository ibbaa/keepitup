package de.ibba.keepitup.notification;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
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
        notificationHandler = new NotificationHandler(TestRegistry.getContext());
        notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
    }

    @Test
    public void testSendNotification() {
        NetworkTask networkTask = getNetworkTask();
        long timestamp = new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime();
        notificationHandler.sendNotification(networkTask, timestamp);
        assertTrue(notificationManager.wasNotifyCalled());
        MockNotificationManager.NotifyCall notifyCall = notificationManager.getNotifyCalls().get(0);
        assertEquals(networkTask.getSchedulerId(), notifyCall.getId());
    }

    private NetworkTask getNetworkTask() {
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
}
