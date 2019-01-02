package de.ibba.keepitup.model;

import android.os.PersistableBundle;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkJobTest {

    @Test
    public void testToPersistableBundleDefaultValues() {
        NetworkJob job = new NetworkJob();
        Assert.assertEquals(0, job.getId());
        Assert.assertEquals(0, job.getIndex());
        Assert.assertNull(job.getAddress());
        Assert.assertNull(job.getAccessType());
        Assert.assertEquals(0, job.getInterval());
        Assert.assertFalse(job.isSuccess());
        Assert.assertNull(job.getMessage());
        Assert.assertFalse(job.isNotification());
        Assert.assertFalse(job.isRunning());
        PersistableBundle bundle = job.toPersistableBundle();
        Assert.assertNotNull(bundle);
        job = new NetworkJob(bundle);
        Assert.assertEquals(0, job.getId());
        Assert.assertEquals(0, job.getIndex());
        Assert.assertNull(job.getAddress());
        Assert.assertNull(job.getAccessType());
        Assert.assertEquals(0, job.getInterval());
        Assert.assertFalse(job.isSuccess());
        Assert.assertNull(job.getMessage());
        Assert.assertFalse(job.isNotification());
        Assert.assertFalse(job.isRunning());
    }

    public void testToPersistableBundleValues() {
        NetworkJob job = new NetworkJob();
        job.setId(1);
        job.setIndex(2);
        job.setAddress("127.0.0.1");
        job.setAccessType(AccessType.PING);
        job.setInterval(15);
        job.setSuccess(true);
        job.setMessage("Message");
        job.setNotification(true);
        job.setRunning(true);
        Assert.assertEquals(1, job.getId());
        Assert.assertEquals(2, job.getIndex());
        Assert.assertEquals("127.0.0.1", job.getAddress());
        Assert.assertEquals(AccessType.PING, job.getAccessType());
        Assert.assertEquals(15, job.getInterval());
        Assert.assertTrue(job.isSuccess());
        Assert.assertEquals("Message", job.getMessage());
        Assert.assertTrue(job.isNotification());
        Assert.assertTrue(job.isRunning());
        PersistableBundle bundle = job.toPersistableBundle();
        Assert.assertNotNull(bundle);
        job = new NetworkJob(bundle);
        Assert.assertEquals(1, job.getId());
        Assert.assertEquals(2, job.getIndex());
        Assert.assertEquals("127.0.0.1", job.getAddress());
        Assert.assertEquals(AccessType.PING, job.getAccessType());
        Assert.assertEquals(15, job.getInterval());
        Assert.assertTrue(job.isSuccess());
        Assert.assertEquals("Message", job.getMessage());
        Assert.assertTrue(job.isNotification());
        Assert.assertTrue(job.isRunning());
    }
}
