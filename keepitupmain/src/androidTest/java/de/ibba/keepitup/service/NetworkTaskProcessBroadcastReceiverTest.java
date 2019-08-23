package de.ibba.keepitup.service;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskProcessBroadcastReceiverTest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private NetworkTaskProcessBroadcastReceiver broadcastReceiver;

    @Before
    public void beforeEachTestMethod() {
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        broadcastReceiver = new NetworkTaskProcessBroadcastReceiver();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
    }

    @Test
    public void testLogWritten() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertTrue(entry.getTimestamp() > 0);
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
    }

    @Test
    public void testExecutionSkippedNetworkTaskDoesNotExist() {
        NetworkTask task = getNetworkTask();
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testExecutionSkippedMarkedAsNotRunning() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), false);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testExecutionSkippedNotValid() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        task.setSchedulerId(task.getSchedulerId() + 1);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(1);
        networkTask.setIndex(1);
        networkTask.setSchedulerId(1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }
}
