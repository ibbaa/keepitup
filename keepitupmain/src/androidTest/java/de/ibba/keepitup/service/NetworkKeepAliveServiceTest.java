package de.ibba.keepitup.service;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkKeepAliveServiceTest {

    private NetworkKeepAliveServiceScheduler scheduler;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new NetworkKeepAliveServiceScheduler(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testStartStopRunning() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        Assert.assertFalse(scheduler.isRunning(task1));
        Assert.assertFalse(scheduler.isRunning(task2));
        scheduler.start(task1);
        Assert.assertTrue(scheduler.isRunning(task1));
        Assert.assertFalse(scheduler.isRunning(task2));
        scheduler.start(task2);
        Assert.assertTrue(scheduler.isRunning(task1));
        Assert.assertTrue(scheduler.isRunning(task2));
        scheduler.stop(task1);
        Assert.assertFalse(scheduler.isRunning(task1));
        Assert.assertTrue(scheduler.isRunning(task2));
        scheduler.stop(task2);
        Assert.assertFalse(scheduler.isRunning(task1));
        Assert.assertFalse(scheduler.isRunning(task2));
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask insertedTask1 = new NetworkTask();
        insertedTask1.setId(1);
        insertedTask1.setIndex(1);
        insertedTask1.setAddress("127.0.0.1");
        insertedTask1.setPort(80);
        insertedTask1.setAccessType(AccessType.PING);
        insertedTask1.setInterval(15);
        insertedTask1.setSuccess(true);
        insertedTask1.setMessage("TestMessage1");
        insertedTask1.setNotification(true);
        return insertedTask1;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask insertedTask2 = new NetworkTask();
        insertedTask2.setId(2);
        insertedTask2.setIndex(10);
        insertedTask2.setAddress("host.com");
        insertedTask2.setPort(21);
        insertedTask2.setAccessType(null);
        insertedTask2.setInterval(1);
        insertedTask2.setSuccess(false);
        insertedTask2.setMessage(null);
        insertedTask2.setNotification(false);
        return insertedTask2;
    }
}
