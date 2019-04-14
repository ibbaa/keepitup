package de.ibba.keepitup.service;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskServiceSchedulerTest {

    private NetworkTaskServiceScheduler scheduler;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new NetworkTaskServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
    }

    @After
    public void afterEachTestMethod() {
        scheduler.cancelAll();
    }

    @Test
    public void testScheduleCancelRunning() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = scheduler.schedule(task1);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        task2 = scheduler.schedule(task2);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        task1 = scheduler.cancel(task1);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        task2 = scheduler.cancel(task2);
        assertFalse(task1.isRunning());
        assertFalse(task2.isRunning());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setNotification(true);
        task.setRunning(false);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(10);
        task.setSchedulerId(0);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }
}
