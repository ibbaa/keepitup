package de.ibba.keepitup.service;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchedulerIdGeneratorTest {

    private NetworkKeepAliveServiceScheduler scheduler;
    private SchedulerIdGenerator idGenerator;

    @Before
    public void beforeEachTestMethod() {
        Context context = InstrumentationRegistry.getTargetContext();
        scheduler = new NetworkKeepAliveServiceScheduler(context);
        scheduler.stopAll();
        idGenerator = new SchedulerIdGenerator(context);
    }

    @After
    public void afterEachTestMethod() {
        scheduler.stopAll();
    }

    @Test
    public void testCreateNetworkTaskId() {

    }
}
