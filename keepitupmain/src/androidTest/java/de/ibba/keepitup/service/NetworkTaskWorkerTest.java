package de.ibba.keepitup.service;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskWorkerTest {

    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        logDAO.deleteAllLogs();
    }

    @Test
    public void testLogWritten() {
        NetworkTask task = getNetworkTask();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertTrue(entry.getTimestamp() > 0);
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
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
