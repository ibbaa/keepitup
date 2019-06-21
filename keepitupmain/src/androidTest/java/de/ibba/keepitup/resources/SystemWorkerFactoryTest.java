package de.ibba.keepitup.resources;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.ConnectNetworkTaskWorker;
import de.ibba.keepitup.service.DownloadNetworkTaskWorker;
import de.ibba.keepitup.service.NullNetworkTaskWorker;
import de.ibba.keepitup.service.PingNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SystemWorkerFactoryTest {

    private SystemWorkerFactory systemWorkerFactory;

    @Before
    public void beforeEachTestMethod() {
        systemWorkerFactory = new SystemWorkerFactory();
    }

    @Test
    public void testCreateWorker() {
        assertEquals(NullNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(null), null).getClass());
        assertEquals(PingNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(AccessType.PING), null).getClass());
        assertEquals(ConnectNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(AccessType.CONNECT), null).getClass());
        assertEquals(DownloadNetworkTaskWorker.class, systemWorkerFactory.createWorker(TestRegistry.getContext(), getNetworkTask(AccessType.DOWNLOAD), null).getClass());
    }

    private NetworkTask getNetworkTask(AccessType type) {
        NetworkTask task = new NetworkTask();
        task.setAccessType(type);
        return task;
    }
}
