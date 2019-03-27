package de.ibba.keepitup.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.model.LogEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogDAOTest {

    private LogDAO dao;

    @Before
    public void beforeEachTestMethod() {
        dao = new LogDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        dao.deleteAllLogs();
    }

    @Test
    public void testInsertRead() {
        LogEntry insertedLogEntry1 = getLogEntry1();
        dao.insertAndDeleteLog(insertedLogEntry1);
        List<LogEntry> allEntries = dao.readAllLogs();
        assertEquals(1, allEntries.size());
        LogEntry readLogEntry1 = allEntries.get(0);
        assertTrue(readLogEntry1.getId() > 0);
        assertAreEqual(insertedLogEntry1, readLogEntry1);
        readLogEntry1 = dao.readMostRecentLog();
        assertTrue(readLogEntry1.getId() > 0);
        assertAreEqual(insertedLogEntry1, readLogEntry1);
        LogEntry insertedLogEntry2 = getLogEntry2();
        dao.insertAndDeleteLog(insertedLogEntry2);
        LogEntry insertedLogEntry3 = getLogEntry3();
        dao.insertAndDeleteLog(insertedLogEntry3);
        allEntries = dao.readAllLogs();
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
        readLogEntry3 = dao.readMostRecentLog();
        assertTrue(readLogEntry3.getId() > 0);
        assertAreEqual(insertedLogEntry3, readLogEntry3);
        dao.deleteAllLogs();
        allEntries = dao.readAllLogs();
        assertTrue(allEntries.isEmpty());
        readLogEntry1 = dao.readMostRecentLog();
        assertNull(readLogEntry1);
    }

    @Test
    public void testInsertLimitExceeded() {
        for (int ii = 0; ii < 100; ii++) {
            LogEntry logEntry = getLogEntryWithTimestamp(ii);
            dao.insertAndDeleteLog(logEntry);
        }
        List<LogEntry> allEntries = dao.readAllLogs();
        assertEquals(100, allEntries.size());
        LogEntry readLogEntry1 = allEntries.get(0);
        LogEntry readLogEntry2 = allEntries.get(99);
        assertEquals(99, readLogEntry1.getTimestamp());
        assertEquals(0, readLogEntry2.getTimestamp());
        LogEntry logEntry = getLogEntryWithTimestamp(100);
        dao.insertAndDeleteLog(logEntry);
        allEntries = dao.readAllLogs();
        assertEquals(100, allEntries.size());
        readLogEntry1 = allEntries.get(0);
        readLogEntry2 = allEntries.get(99);
        assertEquals(100, readLogEntry1.getTimestamp());
        assertEquals(1, readLogEntry2.getTimestamp());
        logEntry = getLogEntryWithTimestamp(101);
        dao.insertAndDeleteLog(logEntry);
        allEntries = dao.readAllLogs();
        assertEquals(100, allEntries.size());
        readLogEntry1 = allEntries.get(0);
        readLogEntry2 = allEntries.get(99);
        assertEquals(101, readLogEntry1.getTimestamp());
        assertEquals(2, readLogEntry2.getTimestamp());
    }

    private LogEntry getLogEntry1() {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(123);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }

    private LogEntry getLogEntry2() {
        LogEntry insertedLogEntry2 = new LogEntry();
        insertedLogEntry2.setId(0);
        insertedLogEntry2.setSuccess(false);
        insertedLogEntry2.setTimestamp(456);
        insertedLogEntry2.setMessage("TestMessage2");
        return insertedLogEntry2;
    }

    private LogEntry getLogEntry3() {
        LogEntry insertedLogEntry3 = new LogEntry();
        insertedLogEntry3.setId(0);
        insertedLogEntry3.setSuccess(true);
        insertedLogEntry3.setTimestamp(789);
        insertedLogEntry3.setMessage("TestMessage3");
        return insertedLogEntry3;
    }

    private LogEntry getLogEntryWithTimestamp(long timestamp) {
        LogEntry insertedLogEntry = new LogEntry();
        insertedLogEntry.setId(0);
        insertedLogEntry.setSuccess(true);
        insertedLogEntry.setTimestamp(timestamp);
        insertedLogEntry.setMessage("TestMessage");
        return insertedLogEntry;
    }

    private void assertAreEqual(LogEntry logEntry1, LogEntry logEntry2) {
        assertEquals(logEntry1.isSuccess(), logEntry2.isSuccess());
        assertEquals(logEntry1.getTimestamp(), logEntry2.getTimestamp());
        assertEquals(logEntry1.getMessage(), logEntry2.getMessage());
    }
}
