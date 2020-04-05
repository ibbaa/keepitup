package de.ibba.keepitup.service;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
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
public class NetworkTaskProcessServiceSchedulerTest {

    private NetworkTaskProcessServiceScheduler scheduler;
    private NetworkTaskDAO networkTaskDAO;
    private MockAlarmManager alarmManager;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new NetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        alarmManager = (MockAlarmManager) scheduler.getAlarmManager();
        alarmManager.reset();
    }

    @After
    public void afterEachTestMethod() {
        scheduler.cancelAll();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testScheduleCancel() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.schedule(task1);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        task2 = scheduler.schedule(task2);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall2 = setAlarmCalls.get(1);
        assertEquals(0, setAlarmCall2.getDelay());
        assertNotEquals(setAlarmCall1.getPendingIntent(), setAlarmCall2.getPendingIntent());
        task1 = scheduler.cancel(task1);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(1, cancelAlarmCalls.size());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall1 = cancelAlarmCalls.get(0);
        assertEquals(setAlarmCall1.getPendingIntent(), cancelAlarmCall1.getPendingIntent());
        task2 = scheduler.cancel(task2);
        assertFalse(task1.isRunning());
        assertFalse(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
        cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall2 = cancelAlarmCalls.get(1);
        assertEquals(setAlarmCall2.getPendingIntent(), cancelAlarmCall2.getPendingIntent());
    }

    @Test
    public void testRescheduleTerminate() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.reschedule(task1, false);
        assertFalse(task1.isRunning());
        assertFalse(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        alarmManager.reset();
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        int schedulerId = task2.getSchedulerId();
        task2.setSchedulerId(schedulerId + 1);
        task2 = scheduler.reschedule(task2, false);
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        task2.setSchedulerId(schedulerId);
        alarmManager.reset();
        task2 = scheduler.reschedule(task2, false);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(60 * 1000, setAlarmCall1.getDelay());
        task2 = scheduler.terminate(task2);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(1, cancelAlarmCalls.size());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall1 = cancelAlarmCalls.get(0);
        assertEquals(setAlarmCall1.getPendingIntent(), cancelAlarmCall1.getPendingIntent());
    }

    @Test
    public void testStartup() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        scheduler.startup();
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        scheduler.schedule(task1);
        scheduler.terminate(task1);
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        alarmManager.reset();
        scheduler.startup();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        scheduler.schedule(task2);
        alarmManager.reset();
        scheduler.startup();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(alarmManager.wasSetAlarmCalled());
        scheduler.terminate(task2);
        scheduler.startup();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
    }

    @Test
    public void testStartupInstancesReset() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        networkTaskDAO.increaseNetworkTaskInstances(task1.getId());
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertEquals(2, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
        scheduler.startup();
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
    }

    @Test
    public void testCancelAll() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.schedule(task1);
        task2 = scheduler.schedule(task2);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.cancelAll();
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testTerminateAll() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.schedule(task1);
        task2 = scheduler.schedule(task2);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.terminateAll();
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
    }

    private boolean isTaskMarkedAsRunningInDatabase(NetworkTask task) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        return task.isRunning();
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
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(10);
        task.setSchedulerId(2);
        task.setInstances(0);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }
}
