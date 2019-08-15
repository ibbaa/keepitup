package de.ibba.keepitup.service;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.network.PingCommandResult;
import de.ibba.keepitup.test.mock.MockPingCommand;
import de.ibba.keepitup.test.mock.TestPingNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class PingNetworkTaskWorkerTest {

    private TestPingNetworkTaskWorker pingNetworkTaskWorker;

    @Before
    public void beforeEachTestMethod() {
        pingNetworkTaskWorker = new TestPingNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
    }

    @Test
    public void testSuccessfulCall() {
        PingCommandResult result = new PingCommandResult(0, "testoutput", null);
        MockPingCommand mockPingCommandExecutionCallable = new MockPingCommand(TestRegistry.getContext(), getNetworkTask(), result);
        pingNetworkTaskWorker.setMockPingCommandExecutionCallable(mockPingCommandExecutionCallable);
        LogEntry logEntry = pingNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertTrue(logEntry.getTimestamp() > -1);
        assertTrue(logEntry.isSuccess());
        assertEquals("testoutput", logEntry.getMessage());
    }

    @Test
    public void testExceptionThrown() {
        IllegalArgumentException excpetion = new IllegalArgumentException("TestException");
        PingCommandResult result = new PingCommandResult(0, "testoutput", excpetion);
        MockPingCommand mockPingCommandExecutionCallable = new MockPingCommand(TestRegistry.getContext(), getNetworkTask(), result);
        pingNetworkTaskWorker.setMockPingCommandExecutionCallable(mockPingCommandExecutionCallable);
        LogEntry logEntry = pingNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertTrue(logEntry.getTimestamp() > -1);
        assertFalse(logEntry.isSuccess());
        assertEquals("IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessage() {
        PingCommandResult result = new PingCommandResult(1, "testoutput", null);
        MockPingCommand mockPingCommandExecutionCallable = new MockPingCommand(TestRegistry.getContext(), getNetworkTask(), result);
        pingNetworkTaskWorker.setMockPingCommandExecutionCallable(mockPingCommandExecutionCallable);
        LogEntry logEntry = pingNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertTrue(logEntry.getTimestamp() > -1);
        assertFalse(logEntry.isSuccess());
        assertEquals("testoutput", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithoutMessage() {
        PingCommandResult result = new PingCommandResult(1, "", null);
        MockPingCommand mockPingCommandExecutionCallable = new MockPingCommand(TestRegistry.getContext(), getNetworkTask(), result);
        pingNetworkTaskWorker.setMockPingCommandExecutionCallable(mockPingCommandExecutionCallable);
        LogEntry logEntry = pingNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertTrue(logEntry.getTimestamp() > -1);
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping failed. Return code: 1", logEntry.getMessage());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(45);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }
}
