package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class LogEntryTest {

    @Test
    public void testToBundleDefaultValues() {
        LogEntry logEntry = new LogEntry();
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworktaskid());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
        PersistableBundle persistableBundle = logEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        logEntry = new LogEntry(persistableBundle);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworktaskid());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
        Bundle bundle = logEntry.toBundle();
        assertNotNull(bundle);
        logEntry = new LogEntry(bundle);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworktaskid());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
    }

    @Test
    public void testToBundleValues() {
        long timestamp = System.currentTimeMillis();
        LogEntry logEntry = new LogEntry();
        logEntry.setId(1);
        logEntry.setNetworktaskid(2);
        logEntry.setTimestamp(timestamp);
        logEntry.setSuccess(true);
        logEntry.setMessage("Message");
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworktaskid());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
        PersistableBundle persistableBundle = logEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworktaskid());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
        Bundle bundle = logEntry.toBundle();
        assertNotNull(bundle);
        logEntry = new LogEntry(bundle);
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworktaskid());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
    }
}
