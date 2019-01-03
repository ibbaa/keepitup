package de.ibba.keepitup.model;

import android.os.PersistableBundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskTest {

    @Test
    public void testToPersistableBundleDefaultValues() {
        NetworkTask task = new NetworkTask();
        Assert.assertEquals(0, task.getId());
        Assert.assertEquals(0, task.getIndex());
        Assert.assertNull(task.getAddress());
        Assert.assertNull(task.getAccessType());
        Assert.assertEquals(0, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
        PersistableBundle bundle = task.toPersistableBundle();
        Assert.assertNotNull(bundle);
        task = new NetworkTask(bundle);
        Assert.assertEquals(0, task.getId());
        Assert.assertEquals(0, task.getIndex());
        Assert.assertNull(task.getAddress());
        Assert.assertNull(task.getAccessType());
        Assert.assertEquals(0, task.getInterval());
        Assert.assertFalse(task.isSuccess());
        Assert.assertNull(task.getMessage());
        Assert.assertFalse(task.isNotification());
    }

    public void testToPersistableBundleValues() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setAddress("127.0.0.1");
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setSuccess(true);
        task.setMessage("Message");
        task.setNotification(true);
        Assert.assertEquals(1, task.getId());
        Assert.assertEquals(2, task.getIndex());
        Assert.assertEquals("127.0.0.1", task.getAddress());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertTrue(task.isSuccess());
        Assert.assertEquals("Message", task.getMessage());
        Assert.assertTrue(task.isNotification());
        PersistableBundle bundle = task.toPersistableBundle();
        Assert.assertNotNull(bundle);
        task = new NetworkTask(bundle);
        Assert.assertEquals(1, task.getId());
        Assert.assertEquals(2, task.getIndex());
        Assert.assertEquals("127.0.0.1", task.getAddress());
        Assert.assertEquals(AccessType.PING, task.getAccessType());
        Assert.assertEquals(15, task.getInterval());
        Assert.assertTrue(task.isSuccess());
        Assert.assertEquals("Message", task.getMessage());
        Assert.assertTrue(task.isNotification());
    }
}
