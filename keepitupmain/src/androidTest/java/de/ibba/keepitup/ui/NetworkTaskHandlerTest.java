package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkTaskHandlerTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;
    private NetworkTaskDAO dao;
    private NetworkKeepAliveServiceScheduler scheduler;
    private NetworkTaskHandler handler;

    @Before
    public void beforeEachTestMethod() {
        dao = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteAllNetworkTasks();
        scheduler = new NetworkKeepAliveServiceScheduler(InstrumentationRegistry.getTargetContext());
        scheduler.stopAll();
        rule.launchActivity(null);
        activity = rule.getActivity();
        handler = new NetworkTaskHandler(activity);
    }

    @After
    public void afterEachTestMethod() {
        dao.deleteAllNetworkTasks();
        scheduler.stopAll();
    }

    @Test
    public void testStartStopNetworkTask() {
        NetworkTask task = getNetworkTask1();
        dao.insertNetworkTask(task);
        handler.startNetworkTask(task);
        assertTrue(task.getSchedulerid() >= 0);
        List<NetworkTask> tasks = dao.readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.getSchedulerid() >= 0);
        assertTrue(scheduler.isRunning(task));
        handler.stopNetworkTask(task);
        assertTrue(task.getSchedulerid() < 0);
        tasks = dao.readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.getSchedulerid() < 0);
        assertFalse(scheduler.isRunning(task));
    }

    @Test
    public void testInsertNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        List<NetworkTask> tasks = dao.readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, activity.getAdapter().getNextIndex());
        assertEquals(2, activity.getAdapter().getItemCount());
        task1 = tasks.get(0);
        assertEquals(0, task1.getIndex());
        assertTrue(task1.getId() >= 0);
        NetworkTask adapterTask1 = activity.getAdapter().getItem(0);
        assertAreEqual(task1, adapterTask1);
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        tasks = dao.readAllNetworkTasks();
        assertEquals(2, tasks.size());
        assertEquals(2, activity.getAdapter().getNextIndex());
        assertEquals(3, activity.getAdapter().getItemCount());
        task2 = tasks.get(1);
        NetworkTask adapterTask2 = activity.getAdapter().getItem(1);
        assertAreEqual(task2, adapterTask2);
    }

    @Test
    public void testUpdateNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        task1.setAddress("192.168.178.1");
        handler.updateNetworkTask(task1);
        List<NetworkTask> tasks = dao.readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        NetworkTask adapterTask1 = activity.getAdapter().getItem(0);
        assertEquals("192.168.178.1", adapterTask1.getAddress());
        assertAreEqual(task1, adapterTask1);
        assertFalse(scheduler.isRunning(task1));
        task2 = tasks.get(1);
        handler.startNetworkTask(task2);
        handler.updateNetworkTask(task2);
        assertTrue(scheduler.isRunning(task2));
    }

    @Test
    public void testDeleteNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        NetworkTask task3 = getNetworkTask3();
        handler.insertNetworkTask(task3);
        NetworkTask task4 = getNetworkTask4();
        handler.insertNetworkTask(task4);
        List<NetworkTask> tasks = dao.readAllNetworkTasks();
        task2 = tasks.get(1);
        handler.startNetworkTask(task2);
        handler.deleteNetworkTask(task2);
        assertFalse(scheduler.isRunning(task2));
        tasks = dao.readAllNetworkTasks();
        assertEquals(3, tasks.size());
        task1 = tasks.get(0);
        task3 = tasks.get(1);
        task4 = tasks.get(2);
        assertEquals(0, task1.getIndex());
        assertEquals(1, task3.getIndex());
        assertEquals(2, task4.getIndex());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask1 = new NetworkTask();
        networkTask1.setId(-1);
        networkTask1.setIndex(-1);
        networkTask1.setSchedulerid(-1);
        networkTask1.setAddress("127.0.0.1");
        networkTask1.setPort(80);
        networkTask1.setAccessType(AccessType.PING);
        networkTask1.setInterval(15);
        networkTask1.setSuccess(true);
        networkTask1.setTimestamp(789);
        networkTask1.setMessage("TestMessage1");
        networkTask1.setOnlyWifi(false);
        networkTask1.setNotification(true);
        return networkTask1;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask networkTask1 = new NetworkTask();
        networkTask1.setId(-1);
        networkTask1.setIndex(-1);
        networkTask1.setSchedulerid(-1);
        networkTask1.setAddress("localhost");
        networkTask1.setPort(22);
        networkTask1.setAccessType(AccessType.PING);
        networkTask1.setInterval(40);
        networkTask1.setSuccess(false);
        networkTask1.setTimestamp(123);
        networkTask1.setMessage("TestMessage2");
        networkTask1.setOnlyWifi(true);
        networkTask1.setNotification(false);
        return networkTask1;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask networkTask1 = new NetworkTask();
        networkTask1.setId(-1);
        networkTask1.setIndex(-1);
        networkTask1.setSchedulerid(-1);
        networkTask1.setAddress("192.168.178.100");
        networkTask1.setPort(8080);
        networkTask1.setAccessType(AccessType.CONNECT);
        networkTask1.setInterval(85);
        networkTask1.setSuccess(true);
        networkTask1.setTimestamp(987);
        networkTask1.setMessage("TestMessage3");
        networkTask1.setOnlyWifi(true);
        networkTask1.setNotification(true);
        return networkTask1;
    }

    private NetworkTask getNetworkTask4() {
        NetworkTask networkTask1 = new NetworkTask();
        networkTask1.setId(-1);
        networkTask1.setIndex(-1);
        networkTask1.setSchedulerid(-1);
        networkTask1.setAddress("192.168.178.200");
        networkTask1.setPort(3389);
        networkTask1.setAccessType(AccessType.CONNECT);
        networkTask1.setInterval(100);
        networkTask1.setSuccess(false);
        networkTask1.setTimestamp(321);
        networkTask1.setMessage("TestMessage4");
        networkTask1.setOnlyWifi(false);
        networkTask1.setNotification(false);
        return networkTask1;
    }

    private void assertAreEqual(NetworkTask task1, NetworkTask task2) {
        assertEquals(task1.getId(), task2.getId());
        assertEquals(task1.getIndex(), task2.getIndex());
        assertEquals(task1.getSchedulerid(), task2.getSchedulerid());
        assertEquals(task1.getAccessType(), task2.getAccessType());
        assertEquals(task1.getAddress(), task2.getAddress());
        assertEquals(task1.getPort(), task2.getPort());
        assertEquals(task1.getInterval(), task2.getInterval());
        assertEquals(task1.isSuccess(), task2.isSuccess());
        assertEquals(task1.getTimestamp(), task2.getTimestamp());
        assertEquals(task1.getMessage(), task2.getMessage());
        assertEquals(task1.isOnlyWifi(), task2.isOnlyWifi());
        assertEquals(task1.isNotification(), task2.isNotification());
    }
}
