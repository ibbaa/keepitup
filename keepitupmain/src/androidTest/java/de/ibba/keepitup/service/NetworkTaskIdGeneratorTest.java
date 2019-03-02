package de.ibba.keepitup.service;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ibba.keepitup.model.NetworkTask;

import static org.junit.Assert.assertEquals;

public class NetworkTaskIdGeneratorTest {

    private NetworkKeepAliveServiceScheduler scheduler;
    private NetworkTaskIdGenerator idGenerator;

    @Before
    public void beforeEachTestMethod() {
        Context context = InstrumentationRegistry.getTargetContext();
        scheduler = new NetworkKeepAliveServiceScheduler(context);
        idGenerator = new NetworkTaskIdGenerator(context);
    }

    @After
    public void afterEachTestMethod() {
        scheduler.stopAll();
    }

    @Test
    public void testCreateNetworkTaskId() {
        assertEquals(1, idGenerator.createNetworkTaskId());
        NetworkTask task1 = new NetworkTask();
        task1.setSchedulerid(1);
        scheduler.start(task1);
        assertEquals(2, idGenerator.createNetworkTaskId());
        scheduler.stop(task1);
        assertEquals(1, idGenerator.createNetworkTaskId());
        NetworkTask task2 = new NetworkTask();
        task2.setSchedulerid(2);
        NetworkTask task5 = new NetworkTask();
        task5.setSchedulerid(5);
        scheduler.start(task1);
        scheduler.start(task2);
        scheduler.start(task5);
        assertEquals(3, idGenerator.createNetworkTaskId());
    }
}
