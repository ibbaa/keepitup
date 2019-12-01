package de.ibba.keepitup.service;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.network.ConnectCommandResult;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.test.mock.MockConnectCommand;
import de.ibba.keepitup.test.mock.MockDNSLookup;
import de.ibba.keepitup.test.mock.MockTimeService;
import de.ibba.keepitup.test.mock.TestConnectNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ConnectNetworkTaskWorkerTest {

    private TestConnectNetworkTaskWorker connectNetworkTaskWorker;

    @Before
    public void beforeEachTestMethod() {
        connectNetworkTaskWorker = new TestConnectNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
    }

    private void prepareTestConnectNetworkTaskWorker(DNSLookupResult dnsLookupResult, ConnectCommandResult connectCommandResult) throws Exception {
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        MockConnectCommand mockConnectCommand = new MockConnectCommand(TestRegistry.getContext(), InetAddress.getByName("127.0.0.1"), 80, connectCommandResult);
        connectNetworkTaskWorker.setMockDNSLookup(mockDNSLookup);
        connectNetworkTaskWorker.setMockConnectCommand(mockConnectCommand);
        MockTimeService timeService = (MockTimeService) connectNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
    }

    @Test
    public void testSuccessfulCall() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(true, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        LogEntry logEntry = connectNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Connected to 127.0.0.1:22 successfully.", logEntry.getMessage());
    }

    @Test
    public void testDNSLookupExceptionThrown() throws Exception {
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, null);
        LogEntry logEntry = connectNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for 127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testConnectCommandExceptionThrown() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        LogEntry logEntry = connectNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testConnectCommandFailedWithoutException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        LogEntry logEntry = connectNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed.", logEntry.getMessage());
    }

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(45);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(22);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }
}
