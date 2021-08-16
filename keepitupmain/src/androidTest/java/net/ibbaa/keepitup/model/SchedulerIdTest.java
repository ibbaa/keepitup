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
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SchedulerIdTest {

    @Test
    public void testToBundleDefaultValues() {
        SchedulerId schedulerId = new SchedulerId();
        assertEquals(-1, schedulerId.getId());
        assertEquals(-1, schedulerId.getSchedulerId());
        assertEquals(-1, schedulerId.getTimestamp());
        assertFalse(schedulerId.isValid());
        PersistableBundle persistableBundle = schedulerId.toPersistableBundle();
        assertNotNull(persistableBundle);
        schedulerId = new SchedulerId(persistableBundle);
        assertEquals(-1, schedulerId.getId());
        assertEquals(-1, schedulerId.getSchedulerId());
        assertEquals(-1, schedulerId.getTimestamp());
        assertFalse(schedulerId.isValid());
        Bundle bundle = schedulerId.toBundle();
        assertNotNull(bundle);
        schedulerId = new SchedulerId(bundle);
        assertEquals(-1, schedulerId.getId());
        assertEquals(-1, schedulerId.getSchedulerId());
        assertEquals(-1, schedulerId.getTimestamp());
        assertFalse(schedulerId.isValid());
        Map<String, ?> map = schedulerId.toMap();
        assertNotNull(map);
        schedulerId = new SchedulerId(map);
        assertEquals(-1, schedulerId.getId());
        assertEquals(-1, schedulerId.getSchedulerId());
        assertEquals(-1, schedulerId.getTimestamp());
        assertFalse(schedulerId.isValid());
    }

    @Test
    public void testEmptyMap() {
        Map<String, ?> map = new HashMap<>();
        SchedulerId schedulerId = new SchedulerId(map);
        assertEquals(-1, schedulerId.getId());
        assertEquals(-1, schedulerId.getSchedulerId());
        assertEquals(-1, schedulerId.getTimestamp());
        assertFalse(schedulerId.isValid());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("schedulerid", "schedulerid");
        map.put("valid", "valid");
        map.put("timestamp", "timestamp");
        SchedulerId schedulerId = new SchedulerId(map);
        assertEquals(-1, schedulerId.getId());
        assertEquals(-1, schedulerId.getSchedulerId());
        assertEquals(-1, schedulerId.getTimestamp());
        assertFalse(schedulerId.isValid());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("schedulerid", "2");
        map.put("valid", "true");
        map.put("timestamp", "3");
        SchedulerId schedulerId = new SchedulerId(map);
        assertEquals(1, schedulerId.getId());
        assertEquals(2, schedulerId.getSchedulerId());
        assertEquals(3, schedulerId.getTimestamp());
        assertTrue(schedulerId.isValid());
    }

    @Test
    public void testToBundleValues() {
        SchedulerId schedulerId = new SchedulerId();
        schedulerId.setId(100);
        schedulerId.setSchedulerId(25);
        schedulerId.setTimestamp(7000);
        schedulerId.setValid(true);
        PersistableBundle persistableBundle = schedulerId.toPersistableBundle();
        assertNotNull(persistableBundle);
        schedulerId = new SchedulerId(persistableBundle);
        assertEquals(100, schedulerId.getId());
        assertEquals(25, schedulerId.getSchedulerId());
        assertEquals(7000, schedulerId.getTimestamp());
        assertTrue(schedulerId.isValid());
        Bundle bundle = schedulerId.toBundle();
        assertNotNull(bundle);
        schedulerId = new SchedulerId(bundle);
        assertEquals(100, schedulerId.getId());
        assertEquals(25, schedulerId.getSchedulerId());
        assertEquals(7000, schedulerId.getTimestamp());
        assertTrue(schedulerId.isValid());
    }

    @Test
    public void testToMap() {
        SchedulerId schedulerId = new SchedulerId();
        schedulerId.setId(100);
        schedulerId.setSchedulerId(25);
        schedulerId.setTimestamp(7000);
        schedulerId.setValid(true);
        Map<String, ?> map = schedulerId.toMap();
        assertNotNull(map);
        schedulerId = new SchedulerId(map);
        assertEquals(100, schedulerId.getId());
        assertEquals(25, schedulerId.getSchedulerId());
        assertEquals(7000, schedulerId.getTimestamp());
        assertTrue(schedulerId.isValid());
    }
}
