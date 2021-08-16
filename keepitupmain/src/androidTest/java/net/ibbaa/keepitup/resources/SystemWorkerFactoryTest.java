package net.ibbaa.keepitup.resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.ConnectNetworkTaskWorker;
import net.ibbaa.keepitup.service.DownloadNetworkTaskWorker;
import net.ibbaa.keepitup.service.NullNetworkTaskWorker;
import net.ibbaa.keepitup.service.PingNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

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
