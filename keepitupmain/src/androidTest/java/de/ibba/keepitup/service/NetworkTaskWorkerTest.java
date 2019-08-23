package de.ibba.keepitup.service;

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
import de.ibba.keepitup.test.mock.MockNetworkManager;
import de.ibba.keepitup.test.mock.TestNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskWorkerTest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
    }

    @Test
    public void testSuccessfulExecution() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertTrue(entry.getTimestamp() > 0);
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
    }

    @Test
    public void testNetworkTaskDoesNotExist() {
        NetworkTask task = getNetworkTask();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testNetworkTaskIsNotValid() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        task.setSchedulerId(task.getSchedulerId() + 1);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testNoNetworkConnection() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertTrue(entry.getTimestamp() > 0);
        assertFalse(entry.isSuccess());
        assertEquals("No active network connection.", entry.getMessage());
    }

    @Test
    public void testNoWifiConnection() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        task.setOnlyWifi(true);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertTrue(entry.getTimestamp() > 0);
        assertFalse(entry.isSuccess());
        assertEquals("Skipped. No active wifi connection.", entry.getMessage());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(45);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }
}
