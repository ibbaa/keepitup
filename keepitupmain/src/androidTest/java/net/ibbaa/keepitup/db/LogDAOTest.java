/*
 * Copyright (c) 2021. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogDAOTest {

    private LogDAO logDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @After
    public void afterEachTestMethod() {
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testInsertRead() {
        LogEntry logEntryForId2 = getLogEntryWithNetworkTaskId(2);
        logDAO.insertAndDeleteLog(logEntryForId2);
        logDAO.insertAndDeleteLog(logEntryForId2);
        LogEntry insertedLogEntry1 = getLogEntry1();
        logDAO.insertAndDeleteLog(insertedLogEntry1);
        List<LogEntry> allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(1, allEntries.size());
        LogEntry readLogEntry1 = allEntries.get(0);
        assertTrue(readLogEntry1.getId() > 0);
        assertTrue(insertedLogEntry1.isEqual(readLogEntry1));
        readLogEntry1 = logDAO.readMostRecentLogForNetworkTask(1);
        assertTrue(readLogEntry1.getId() > 0);
        assertTrue(insertedLogEntry1.isEqual(readLogEntry1));
        LogEntry insertedLogEntry2 = getLogEntry2();
        logDAO.insertAndDeleteLog(insertedLogEntry2);
        LogEntry insertedLogEntry3 = getLogEntry3();
        logDAO.insertAndDeleteLog(insertedLogEntry3);
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(3, allEntries.size());
        readLogEntry1 = allEntries.get(2);
        LogEntry readLogEntry2 = allEntries.get(1);
        LogEntry readLogEntry3 = allEntries.get(0);
        assertTrue(readLogEntry1.getId() > 0);
        assertTrue(readLogEntry2.getId() > 0);
        assertTrue(readLogEntry3.getId() > 0);
        assertTrue(insertedLogEntry1.isEqual(readLogEntry1));
        assertTrue(insertedLogEntry2.isEqual(readLogEntry2));
        assertTrue(insertedLogEntry3.isEqual(readLogEntry3));
        readLogEntry3 = logDAO.readMostRecentLogForNetworkTask(1);
        assertTrue(readLogEntry3.getId() > 0);
        assertTrue(insertedLogEntry3.isEqual(readLogEntry3));
        logDAO.deleteAllLogsForNetworkTask(1);
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertTrue(allEntries.isEmpty());
        readLogEntry1 = logDAO.readMostRecentLogForNetworkTask(1);
        assertNull(readLogEntry1);
    }

    @Test
    public void testInsertReadForNetworkTask() {
        LogEntry logEntryForId1 = getLogEntryWithNetworkTaskId(1);
        LogEntry logEntryForId2 = getLogEntryWithNetworkTaskId(2);
        LogEntry logEntryForId3 = getLogEntryWithNetworkTaskId(3);
        LogEntry logEntryForId4 = getLogEntryWithNetworkTaskId(4);
        logDAO.insertAndDeleteLog(logEntryForId1);
        logDAO.insertAndDeleteLog(logEntryForId2);
        logDAO.insertAndDeleteLog(logEntryForId3);
        logDAO.insertAndDeleteLog(logEntryForId4);
        List<LogEntry> allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(1, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(2);
        assertEquals(1, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(3);
        assertEquals(1, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(4);
        assertEquals(1, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(5);
        assertTrue(allEntries.isEmpty());
        logDAO.insertAndDeleteLog(logEntryForId1);
        logDAO.insertAndDeleteLog(logEntryForId2);
        logDAO.insertAndDeleteLog(logEntryForId3);
        logDAO.insertAndDeleteLog(logEntryForId4);
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(2, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(2);
        assertEquals(2, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(3);
        assertEquals(2, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(4);
        assertEquals(2, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(5);
        assertTrue(allEntries.isEmpty());
        logDAO.deleteAllLogsForNetworkTask(1);
        logDAO.deleteAllLogsForNetworkTask(2);
        logDAO.deleteAllLogsForNetworkTask(3);
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertTrue(allEntries.isEmpty());
        allEntries = logDAO.readAllLogsForNetworkTask(2);
        assertTrue(allEntries.isEmpty());
        allEntries = logDAO.readAllLogsForNetworkTask(3);
        assertTrue(allEntries.isEmpty());
        allEntries = logDAO.readAllLogsForNetworkTask(4);
        assertEquals(2, allEntries.size());
        allEntries = logDAO.readAllLogsForNetworkTask(5);
        assertTrue(allEntries.isEmpty());

    }

    @Test
    public void testInsertLimitExceeded() {
        LogEntry logEntryForId2 = getLogEntryWithNetworkTaskId(2);
        logDAO.insertAndDeleteLog(logEntryForId2);
        logDAO.insertAndDeleteLog(logEntryForId2);
        for (int ii = 0; ii < 100; ii++) {
            LogEntry logEntry = getLogEntryWithTimestamp(ii);
            logDAO.insertAndDeleteLog(logEntry);
        }
        List<LogEntry> allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(100, allEntries.size());
        LogEntry readLogEntry1 = allEntries.get(0);
        LogEntry readLogEntry2 = allEntries.get(99);
        assertEquals(99, readLogEntry1.getTimestamp());
        assertEquals(0, readLogEntry2.getTimestamp());
        LogEntry logEntry = getLogEntryWithTimestamp(100);
        logDAO.insertAndDeleteLog(logEntry);
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(100, allEntries.size());
        readLogEntry1 = allEntries.get(0);
        readLogEntry2 = allEntries.get(99);
        assertEquals(100, readLogEntry1.getTimestamp());
        assertEquals(1, readLogEntry2.getTimestamp());
        logEntry = getLogEntryWithTimestamp(101);
        logDAO.insertAndDeleteLog(logEntry);
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(100, allEntries.size());
        readLogEntry1 = allEntries.get(0);
        readLogEntry2 = allEntries.get(99);
        assertEquals(101, readLogEntry1.getTimestamp());
        assertEquals(2, readLogEntry2.getTimestamp());
    }

    @Test
    public void testReadAllLogs() {
        LogEntry insertedLogEntry1 = getLogEntry1();
        insertedLogEntry1.setTimestamp(10);
        insertedLogEntry1 = logDAO.insertAndDeleteLog(insertedLogEntry1);
        LogEntry insertedLogEntry2 = getLogEntry2();
        insertedLogEntry2.setTimestamp(9);
        insertedLogEntry2 = logDAO.insertAndDeleteLog(insertedLogEntry2);
        LogEntry insertedLogEntry3 = getLogEntry3();
        insertedLogEntry3.setTimestamp(8);
        insertedLogEntry3 = logDAO.insertAndDeleteLog(insertedLogEntry3);
        LogEntry insertedLogEntry4 = getLogEntryWithNetworkTaskId(4);
        insertedLogEntry4.setTimestamp(7);
        insertedLogEntry4 = logDAO.insertAndDeleteLog(insertedLogEntry4);
        LogEntry insertedLogEntry5 = getLogEntryWithNetworkTaskId(5);
        insertedLogEntry5.setTimestamp(6);
        insertedLogEntry5 = logDAO.insertAndDeleteLog(insertedLogEntry5);
        List<LogEntry> allEntries = logDAO.readAllLogs();
        assertEquals(5, allEntries.size());
        assertTrue(insertedLogEntry1.isEqual(allEntries.get(0)));
        assertTrue(insertedLogEntry2.isEqual(allEntries.get(1)));
        assertTrue(insertedLogEntry3.isEqual(allEntries.get(2)));
        assertTrue(insertedLogEntry4.isEqual(allEntries.get(3)));
        assertTrue(insertedLogEntry5.isEqual(allEntries.get(4)));
        allEntries = logDAO.readAllLogsForNetworkTask(1);
        assertEquals(3, allEntries.size());
        assertTrue(insertedLogEntry1.isEqual(allEntries.get(0)));
        assertTrue(insertedLogEntry2.isEqual(allEntries.get(1)));
        assertTrue(insertedLogEntry3.isEqual(allEntries.get(2)));
    }

    @Test
    public void testDeleteAllOrphanLogs() {
        NetworkTask task1 = getNetworkTask();
        NetworkTask task2 = getNetworkTask();
        NetworkTask task3 = getNetworkTask();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task3 = networkTaskDAO.insertNetworkTask(task3);
        networkTaskDAO.deleteNetworkTask(task3);
        LogEntry entry11 = getLogEntryWithNetworkTaskId(task1.getId());
        LogEntry entry12 = getLogEntryWithNetworkTaskId(task1.getId());
        entry11 = logDAO.insertAndDeleteLog(entry11);
        entry12 = logDAO.insertAndDeleteLog(entry12);
        LogEntry entry21 = getLogEntryWithNetworkTaskId(task2.getId());
        entry21 = logDAO.insertAndDeleteLog(entry21);
        LogEntry orphan1 = getLogEntryWithNetworkTaskId(task3.getId());
        LogEntry orphan2 = getLogEntryWithNetworkTaskId(task3.getId());
        orphan1 = logDAO.insertAndDeleteLog(orphan1);
        orphan2 = logDAO.insertAndDeleteLog(orphan2);
        assertEquals(5, logDAO.readAllLogs().size());
        logDAO.deleteAllOrphanLogs();
        List<LogEntry> logEntries = logDAO.readAllLogs();
        assertEquals(3, logEntries.size());
        assertTrue(containsLogEntry(logEntries, entry11));
        assertTrue(containsLogEntry(logEntries, entry12));
        assertTrue(containsLogEntry(logEntries, entry21));
        assertFalse(containsLogEntry(logEntries, orphan1));
        assertFalse(containsLogEntry(logEntries, orphan2));
    }

    private boolean containsLogEntry(List<LogEntry> logEntries, LogEntry entry) {
        for (LogEntry currentEntry : logEntries) {
            if (currentEntry.isEqual(entry)) {
                return true;
            }
        }
        return false;
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        return task;
    }

    private LogEntry getLogEntry1() {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(1);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(123);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }

    private LogEntry getLogEntry2() {
        LogEntry insertedLogEntry2 = new LogEntry();
        insertedLogEntry2.setId(0);
        insertedLogEntry2.setNetworkTaskId(1);
        insertedLogEntry2.setSuccess(false);
        insertedLogEntry2.setTimestamp(456);
        insertedLogEntry2.setMessage("TestMessage2");
        return insertedLogEntry2;
    }

    private LogEntry getLogEntry3() {
        LogEntry insertedLogEntry3 = new LogEntry();
        insertedLogEntry3.setId(0);
        insertedLogEntry3.setNetworkTaskId(1);
        insertedLogEntry3.setSuccess(true);
        insertedLogEntry3.setTimestamp(789);
        insertedLogEntry3.setMessage("TestMessage3");
        return insertedLogEntry3;
    }

    private LogEntry getLogEntryWithNetworkTaskId(long networkTaskId) {
        LogEntry insertedLogEntry = new LogEntry();
        insertedLogEntry.setId(0);
        insertedLogEntry.setNetworkTaskId(networkTaskId);
        insertedLogEntry.setSuccess(false);
        insertedLogEntry.setTimestamp(1);
        insertedLogEntry.setMessage("TestMessage");
        return insertedLogEntry;
    }

    private LogEntry getLogEntryWithTimestamp(long timestamp) {
        LogEntry insertedLogEntry = new LogEntry();
        insertedLogEntry.setId(0);
        insertedLogEntry.setNetworkTaskId(1);
        insertedLogEntry.setSuccess(true);
        insertedLogEntry.setTimestamp(timestamp);
        insertedLogEntry.setMessage("TestMessage");
        return insertedLogEntry;
    }
}
