package net.ibbaa.keepitup.logging;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.test.mock.MockLogger;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class LogTest {

    private MockLogger mockLogger;

    @Before
    public void beforeEachTestMethod() {
        mockLogger = new MockLogger();
        Log.initialize(mockLogger);
    }

    @After
    public void afterEachTestMethod() {
        Log.initialize(null);
    }

    @Test
    public void testLog() {
        Log.i("tag1", "message1");
        assertEquals(1, mockLogger.numberLogEntries());
        assertLogEntryEquals(mockLogger.getEntry(0), "tag1", "message1", LogLevel.INFO, null);
        NullPointerException exc = new NullPointerException();
        Log.i("tag2", "message2", exc);
        assertEquals(2, mockLogger.numberLogEntries());
        assertLogEntryEquals(mockLogger.getEntry(1), "tag2", "message2", LogLevel.INFO, exc);
        Log.d("tag3", "message3");
        assertEquals(3, mockLogger.numberLogEntries());
        assertLogEntryEquals(mockLogger.getEntry(2), "tag3", "message3", LogLevel.DEBUG, null);
        Log.d("tag4", "message4", exc);
        assertEquals(4, mockLogger.numberLogEntries());
        assertLogEntryEquals(mockLogger.getEntry(3), "tag4", "message4", LogLevel.DEBUG, exc);
        Log.e("tag5", "message5");
        assertEquals(5, mockLogger.numberLogEntries());
        assertLogEntryEquals(mockLogger.getEntry(4), "tag5", "message5", LogLevel.ERROR, null);
        Log.e("tag6", "message6", exc);
        assertEquals(6, mockLogger.numberLogEntries());
        assertLogEntryEquals(mockLogger.getEntry(5), "tag6", "message6", LogLevel.ERROR, exc);
    }

    private void assertLogEntryEquals(LogFileEntry logEntry, String tag, String message, LogLevel level, Throwable exc) {
        assertEquals(tag, logEntry.getTag());
        assertEquals(message, logEntry.getMessage());
        assertEquals(level, logEntry.getLevel());
        assertEquals(exc, logEntry.getThrowable());
    }
}
