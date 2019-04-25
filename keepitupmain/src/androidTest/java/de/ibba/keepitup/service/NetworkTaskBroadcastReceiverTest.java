package de.ibba.keepitup.service;

import android.content.Intent;
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
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskBroadcastReceiverTest {

    private LogDAO logDAO;
    private NetworkTaskBroadcastReceiver broadcastReceiver;

    @Before
    public void beforeEachTestMethod() {
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        broadcastReceiver = new NetworkTaskBroadcastReceiver();
        broadcastReceiver.setSynchronous(true);
    }

    @After
    public void afterEachTestMethod() {
        logDAO.deleteAllLogs();
    }

    @Test
    public void testLogWritten() {
        NetworkTask task = getNetworkTask();
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
