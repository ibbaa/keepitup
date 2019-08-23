package de.ibba.keepitup.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogDAOTest {

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
        assertAreEqual(insertedLogEntry1, readLogEntry1);
        readLogEntry1 = logDAO.readMostRecentLogForNetworkTask(1);
        assertTrue(readLogEntry1.getId() > 0);
        assertAreEqual(insertedLogEntry1, readLogEntry1);
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
        assertAreEqual(insertedLogEntry1, readLogEntry1);
        assertAreEqual(insertedLogEntry2, readLogEntry2);
        assertAreEqual(insertedLogEntry3, readLogEntry3);
        readLogEntry3 = logDAO.readMostRecentLogForNetworkTask(1);
        assertTrue(readLogEntry3.getId() > 0);
        assertAreEqual(insertedLogEntry3, readLogEntry3);
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

    private void assertAreEqual(LogEntry logEntry1, LogEntry logEntry2) {
        assertEquals(logEntry1.getId(), logEntry2.getId());
        assertEquals(logEntry1.getNetworkTaskId(), logEntry2.getNetworkTaskId());
        assertEquals(logEntry1.isSuccess(), logEntry2.isSuccess());
        assertEquals(logEntry1.getTimestamp(), logEntry2.getTimestamp());
        assertEquals(logEntry1.getMessage(), logEntry2.getMessage());
    }
}
