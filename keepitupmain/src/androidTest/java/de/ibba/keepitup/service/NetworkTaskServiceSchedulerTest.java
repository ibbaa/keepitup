package de.ibba.keepitup.service;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.MockAlarmManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskServiceSchedulerTest {

    private NetworkTaskServiceScheduler scheduler;
    private MockAlarmManager alarmManager;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new NetworkTaskServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
        alarmManager = (MockAlarmManager) scheduler.getAlarmManager();
    }

    @After
    public void afterEachTestMethod() {
        scheduler.cancelAll();
    }

    @Test
    public void testScheduleCancelRunning() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = scheduler.schedule(task1);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = alarmManager.getSetAlarmCalls().get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        task2 = scheduler.schedule(task2);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        MockAlarmManager.SetAlarmCall setAlarmCall2 = alarmManager.getSetAlarmCalls().get(1);
        assertEquals(0, setAlarmCall2.getDelay());
        assertNotEquals(setAlarmCall1.getPendingIntent(), setAlarmCall2.getPendingIntent());
        task1 = scheduler.cancel(task1);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(alarmManager.wasCancelAlarmCalled());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall1 = alarmManager.getCancelAlarmCalls().get(0);
        assertEquals(setAlarmCall1.getPendingIntent(), cancelAlarmCall1.getPendingIntent());
        task2 = scheduler.cancel(task2);
        assertFalse(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(alarmManager.wasCancelAlarmCalled());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall2 = alarmManager.getCancelAlarmCalls().get(1);
        assertEquals(setAlarmCall2.getPendingIntent(), cancelAlarmCall2.getPendingIntent());
        assertNotEquals(cancelAlarmCall1.getPendingIntent(), cancelAlarmCall2.getPendingIntent());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setNotification(true);
        task.setRunning(false);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(10);
        task.setSchedulerId(2);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }
}
