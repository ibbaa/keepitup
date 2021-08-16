package net.ibbaa.keepitup.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.MockAlarmManager;
import net.ibbaa.keepitup.test.mock.MockFuture;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskRunningNotificationService;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskRunningNotificationServiceTest {

    private TestNetworkTaskRunningNotificationService service;
    private NetworkTaskProcessServiceScheduler scheduler;
    private NetworkTaskDAO networkTaskDAO;
    private MockAlarmManager alarmManager;

    @Before
    public void beforeEachTestMethod() {
        service = new TestNetworkTaskRunningNotificationService();
        service.onCreate();
        service.reset();
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
        assertEquals(NetworkTaskRunningNotificationService.NOTIFICATION_SERVICE_ID, startNetworkTaskRunningNotificationForegroundCall.getId());
        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC, startNetworkTaskRunningNotificationForegroundCall.getForegroundServiceType());
        Notification notification = startNetworkTaskRunningNotificationForegroundCall.getNotification();
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
        assertEquals(20 * 60 * 1000, setAlarmCall.getDelay());
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
        TestNetworkTaskRunningNotificationService.StopNetworkTaskRunningNotificationForegroundCall stopNetworkTaskRunningNotificationForegroundCall = service.getStopNetworkTaskRunningNotificationForegroundCalls().get(0);
        assertTrue(stopNetworkTaskRunningNotificationForegroundCall.isRemoveNotification());
    }

    private NetworkTask getNetworkTask() {
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
        task.setRunning(true);
        task.setLastScheduled(1);
        return task;
    }
}
