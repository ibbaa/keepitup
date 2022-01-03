/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

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
        assertEquals(-1, logEntry.getNetworkTaskId());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
        PersistableBundle persistableBundle = logEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        logEntry = new LogEntry(persistableBundle);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworkTaskId());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
        Bundle bundle = logEntry.toBundle();
        assertNotNull(bundle);
        logEntry = new LogEntry(bundle);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworkTaskId());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
        Map<String, ?> map = logEntry.toMap();
        assertNotNull(map);
        logEntry = new LogEntry(map);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworkTaskId());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
    }

    @Test
    public void testEmptyMap() {
        Map<String, ?> map = new HashMap<>();
        LogEntry logEntry = new LogEntry(map);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworkTaskId());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("networktaskid", "networktaskid");
        map.put("success", "success");
        map.put("timestamp", "timestamp");
        map.put("message", null);
        LogEntry logEntry = new LogEntry(map);
        assertEquals(-1, logEntry.getId());
        assertEquals(-1, logEntry.getNetworkTaskId());
        assertEquals(-1, logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertNull(logEntry.getMessage());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("networktaskid", "2");
        map.put("success", "true");
        map.put("timestamp", "3");
        map.put("message", "null");
        LogEntry logEntry = new LogEntry(map);
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworkTaskId());
        assertEquals(3, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("null", logEntry.getMessage());
    }

    @Test
    public void testToBundleValues() {
        long timestamp = System.currentTimeMillis();
        LogEntry logEntry = new LogEntry();
        logEntry.setId(1);
        logEntry.setNetworkTaskId(2);
        logEntry.setTimestamp(timestamp);
        logEntry.setSuccess(true);
        logEntry.setMessage("Message");
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworkTaskId());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
        PersistableBundle persistableBundle = logEntry.toPersistableBundle();
        assertNotNull(persistableBundle);
        logEntry = new LogEntry(persistableBundle);
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworkTaskId());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
        Bundle bundle = logEntry.toBundle();
        assertNotNull(bundle);
        logEntry = new LogEntry(bundle);
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworkTaskId());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
    }

    @Test
    public void testToMap() {
        long timestamp = System.currentTimeMillis();
        LogEntry logEntry = new LogEntry();
        logEntry.setId(1);
        logEntry.setNetworkTaskId(2);
        logEntry.setTimestamp(timestamp);
        logEntry.setSuccess(true);
        logEntry.setMessage("Message");
        Map<String, ?> map = logEntry.toMap();
        assertNotNull(map);
        logEntry = new LogEntry(map);
        assertEquals(1, logEntry.getId());
        assertEquals(2, logEntry.getNetworkTaskId());
        assertEquals(timestamp, logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Message", logEntry.getMessage());
    }

    @Test
    public void testIsEqual() {
        LogEntry logEntry1 = new LogEntry();
        LogEntry logEntry2 = new LogEntry();
        assertTrue(logEntry1.isEqual(logEntry2));
        logEntry1.setId(0);
        assertFalse(logEntry1.isEqual(logEntry2));
        logEntry2.setId(0);
        assertTrue(logEntry1.isEqual(logEntry2));
        logEntry1.setNetworkTaskId(22);
        assertFalse(logEntry1.isEqual(logEntry2));
        logEntry2.setNetworkTaskId(22);
        assertTrue(logEntry1.isEqual(logEntry2));
        logEntry1.setTimestamp(123);
        assertFalse(logEntry1.isEqual(logEntry2));
        logEntry2.setTimestamp(123);
        assertTrue(logEntry1.isEqual(logEntry2));
        logEntry1.setSuccess(true);
        assertFalse(logEntry1.isEqual(logEntry2));
        logEntry2.setSuccess(true);
        assertTrue(logEntry1.isEqual(logEntry2));
        logEntry1.setMessage("message");
        assertFalse(logEntry1.isEqual(logEntry2));
        logEntry2.setMessage("message");
        assertTrue(logEntry1.isEqual(logEntry2));
    }
}
