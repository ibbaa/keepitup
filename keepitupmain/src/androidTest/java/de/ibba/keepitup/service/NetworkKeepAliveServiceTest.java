package de.ibba.keepitup.service;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertFalse(scheduler.isRunning(task1));
        assertFalse(scheduler.isRunning(task2));
        scheduler.start(task1);
        assertTrue(scheduler.isRunning(task1));
        assertFalse(scheduler.isRunning(task2));
        scheduler.start(task2);
        assertTrue(scheduler.isRunning(task1));
        assertTrue(scheduler.isRunning(task2));
        scheduler.stop(task1);
        assertFalse(scheduler.isRunning(task1));
        assertTrue(scheduler.isRunning(task2));
        scheduler.stop(task2);
        assertFalse(scheduler.isRunning(task1));
        assertFalse(scheduler.isRunning(task2));
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task1 = new NetworkTask();
        task1.setId(1);
        task1.setIndex(1);
        task1.setSchedulerid(1);
        task1.setAddress("127.0.0.1");
        task1.setPort(80);
        task1.setAccessType(AccessType.PING);
        task1.setInterval(15);
        task1.setSuccess(true);
        task1.setMessage("TestMessage1");
        task1.setNotification(true);
        return task1;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task2 = new NetworkTask();
        task2.setId(2);
        task2.setIndex(10);
        task2.setSchedulerid(2);
        task2.setAddress("host.com");
        task2.setPort(21);
        task2.setAccessType(null);
        task2.setInterval(1);
        task2.setSuccess(false);
        task2.setMessage(null);
        task2.setNotification(false);
        return task2;
    }
}
