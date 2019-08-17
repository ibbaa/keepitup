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
}
