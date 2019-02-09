package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskTest {

    @Test
    public void testToBundleDefaultValues() {
        NetworkTask task = new NetworkTask();
        Assert.assertEquals(-1, task.getId());
        Assert.assertEquals(-1, task.getIndex());
        Assert.assertNull(task.getAddress());
        Assert.assertEquals(0, task.getPort());
        Assert.assertNull(task.getAccessType());
        Assert.assertEquals(0, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertEquals(-1, task.getTimestamp());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        Assert.assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        Assert.assertEquals(-1, task.getId());
        Assert.assertEquals(-1, task.getIndex());
        Assert.assertNull(task.getAddress());
        Assert.assertEquals(0, task.getPort());
        Assert.assertNull(task.getAccessType());
        Assert.assertEquals(0, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertEquals(-1, task.getTimestamp());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
        Bundle bundle = task.toBundle();
        Assert.assertNotNull(bundle);
        task = new NetworkTask(bundle);
        Assert.assertEquals(-1, task.getId());
        Assert.assertEquals(-1, task.getIndex());
        Assert.assertNull(task.getAddress());
        Assert.assertEquals(0, task.getPort());
        Assert.assertNull(task.getAccessType());
        Assert.assertEquals(0, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertEquals(-1, task.getTimestamp());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
    }

    @Test
    public void testToBundleContextValues() {
        NetworkTask task = new NetworkTask(InstrumentationRegistry.getTargetContext());
        Assert.assertEquals(-1, task.getId());
        Assert.assertEquals(-1, task.getIndex());
        Assert.assertEquals("192.168.178.1", task.getAddress());
        Assert.assertEquals(22, task.getPort());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertEquals(-1, task.getTimestamp());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        Assert.assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        Assert.assertEquals(-1, task.getId());
        Assert.assertEquals(-1, task.getIndex());
        Assert.assertEquals("192.168.178.1", task.getAddress());
        Assert.assertEquals(22, task.getPort());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertEquals(-1, task.getTimestamp());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
        Bundle bundle = task.toBundle();
        Assert.assertNotNull(bundle);
        task = new NetworkTask(bundle);
        Assert.assertEquals(-1, task.getId());
        Assert.assertEquals(-1, task.getIndex());
        Assert.assertEquals("192.168.178.1", task.getAddress());
        Assert.assertEquals(22, task.getPort());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertEquals(-1, task.getTimestamp());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
    }

    @Test
    public void testToBundleValues() {
        long timestamp = System.currentTimeMillis();
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setAddress("127.0.0.1");
        task.setPort(23);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setSuccess(true);
        task.setTimestamp(timestamp);
        task.setMessage("Message");
        task.setNotification(true);
        Assert.assertEquals(1, task.getId());
        Assert.assertEquals(2, task.getIndex());
        Assert.assertEquals("127.0.0.1", task.getAddress());
        Assert.assertEquals(23, task.getPort());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertTrue(task.isSuccess());
        Assert.assertEquals(timestamp, task.getTimestamp());
        Assert.assertEquals("Message", task.getMessage());
        Assert.assertTrue(task.isNotification());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        Assert.assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        Assert.assertEquals(1, task.getId());
        Assert.assertEquals(2, task.getIndex());
        Assert.assertEquals("127.0.0.1", task.getAddress());
        Assert.assertEquals(23, task.getPort());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertTrue(task.isSuccess());
        Assert.assertEquals(timestamp, task.getTimestamp());
        Assert.assertEquals("Message", task.getMessage());
        Assert.assertTrue(task.isNotification());
        Bundle bundle = task.toBundle();
        Assert.assertNotNull(bundle);
        task = new NetworkTask(bundle);
        Assert.assertEquals(1, task.getId());
        Assert.assertEquals(2, task.getIndex());
        Assert.assertEquals("127.0.0.1", task.getAddress());
        Assert.assertEquals(23, task.getPort());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertTrue(task.isSuccess());
        Assert.assertEquals(timestamp, task.getTimestamp());
        Assert.assertEquals("Message", task.getMessage());
        Assert.assertTrue(task.isNotification());
    }
}
