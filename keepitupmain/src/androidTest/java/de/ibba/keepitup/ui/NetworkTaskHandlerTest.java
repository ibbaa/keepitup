package de.ibba.keepitup.ui;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NetworkTaskHandlerTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;
    private NetworkTaskHandler handler;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        lauchRecyclerViewBaseActivity(rule);
        activity = rule.getActivity();
        handler = new NetworkTaskHandler(activity);
    }

    @Test
    public void testStartStopNetworkTask() {
        NetworkTask task = getNetworkTask1();
        getNetworkTaskDAO().insertNetworkTask(task);
        handler.startNetworkTask(task);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.isRunning());
        handler.stopNetworkTask(task);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task = tasks.get(0);
        assertFalse(task.isRunning());
    }

    @Test
    public void testInsertNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(2, activity.getAdapter().getItemCount());
        task1 = tasks.get(0);
        assertEquals(0, task1.getIndex());
        assertTrue(task1.getId() >= 0);
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertNull(adapterWrapper1.getLogEntry());
        assertAreEqual(task1, adapterWrapper1.getNetworkTask());
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(2, tasks.size());
        assertEquals(2, getAdapter().getNextIndex());
        assertEquals(3, activity.getAdapter().getItemCount());
        task2 = tasks.get(1);
        NetworkTaskUIWrapper adapterWrapper2 = getAdapter().getItem(1);
        assertNull(adapterWrapper2.getLogEntry());
        assertAreEqual(task2, adapterWrapper2.getNetworkTask());
    }

    @Test
    public void testUpdateNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        task1.setAddress("192.168.178.1");
        handler.updateNetworkTask(task1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertNull(adapterWrapper1.getLogEntry());
        assertEquals("192.168.178.1", adapterWrapper1.getNetworkTask().getAddress());
        assertAreEqual(task1, adapterWrapper1.getNetworkTask());
        assertFalse(task1.isRunning());
        task2 = tasks.get(1);
        handler.startNetworkTask(task2);
        handler.updateNetworkTask(task2);
        assertTrue(task2.isRunning());
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
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task2 = tasks.get(1);
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task2.getId());
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        handler.startNetworkTask(task2);
        handler.deleteNetworkTask(task2);
        assertFalse(task2.isRunning());
        List<LogEntry> allEntries = getLogDAO().readAllLogsForNetworkTask(task2.getId());
        assertTrue(allEntries.isEmpty());
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(3, tasks.size());
        task1 = tasks.get(0);
        task3 = tasks.get(1);
        task4 = tasks.get(2);
        assertEquals(0, task1.getIndex());
        assertEquals(1, task3.getIndex());
        assertEquals(2, task4.getIndex());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("localhost");
        networkTask.setPort(22);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(40);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("192.168.178.100");
        networkTask.setPort(8080);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(85);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask4() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("192.168.178.200");
        networkTask.setPort(3389);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(100);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        return networkTask;
    }

    private LogEntry getLogEntryWithNetworkTaskId(long networkTaskId) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(networkTaskId);
        logEntry.setSuccess(false);
        logEntry.setTimestamp(1);
        logEntry.setMessage("TestMessage");
        return logEntry;
    }

    private void assertAreEqual(NetworkTask task1, NetworkTask task2) {
        assertEquals(task1.getId(), task2.getId());
        assertEquals(task1.getIndex(), task2.getIndex());
        assertEquals(task1.getSchedulerId(), task2.getSchedulerId());
        assertEquals(task1.getAccessType(), task2.getAccessType());
        assertEquals(task1.getAddress(), task2.getAddress());
        assertEquals(task1.getPort(), task2.getPort());
        assertEquals(task1.getInterval(), task2.getInterval());
        assertEquals(task1.isOnlyWifi(), task2.isOnlyWifi());
        assertEquals(task1.isNotification(), task2.isNotification());
        assertEquals(task1.isRunning(), task2.isRunning());
    }

    private NetworkTaskAdapter getAdapter() {
        return (NetworkTaskAdapter) activity.getAdapter();
    }
}
