/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.network.ConnectCommandResult;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.test.mock.MockConnectCommand;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestConnectNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

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
        MockConnectCommand mockConnectCommand = new MockConnectCommand(TestRegistry.getContext(), InetAddress.getByName("127.0.0.1"), 80, 1, connectCommandResult);
        connectNetworkTaskWorker.setMockDNSLookup(mockDNSLookup);
        connectNetworkTaskWorker.setMockConnectCommand(mockConnectCommand);
        MockTimeService timeService = (MockTimeService) connectNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
    }

    @Test
    public void testSuccessfulCallOneAttempt() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(true, 1, 1, 0, 0, 1, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Connected to 127.0.0.1:22 successfully. 1 connection attempt. 1 successful connection attempt. 0 timeouts. 0 other errors. 1 msec average time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallMultipleAttempts() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(true, 3, 3, 0, 0, 1000, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Connected to 127.0.0.1:22 successfully. 3 connection attempts. 3 successful connection attempts. 0 timeouts. 0 other errors. 1 sec average time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallOneAttemptWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(true, 1, 1, 0, 0, 5000, exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Connected to 127.0.0.1:22 successfully. 1 connection attempt. 1 successful connection attempt. 0 timeouts. 0 other errors. 5 sec average time. Last error: IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallMultipleErrorsWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(true, 10, 7, 1, 2, 3, exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Connected to 127.0.0.1:22 successfully. 10 connection attempts. 7 successful connection attempts. 1 timeout. 2 other errors. 3 msec average time. Last error: IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testDNSLookupExceptionThrown() throws Exception {
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, null);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for 127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testFailureOneTimeout() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, 1, 0, 1, 0, 500, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. 1 connection attempt. 0 successful connection attempts. 1 timeout. 0 other errors.", logEntry.getMessage());
    }

    @Test
    public void testFailureMultipleTimeouts() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, 10, 0, 10, 0, 500, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. 10 connection attempts. 0 successful connection attempts. 10 timeouts. 0 other errors.", logEntry.getMessage());
    }

    @Test
    public void testFailureOneErrorExceptionThrown() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, 1, 0, 0, 1, 500, exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. 1 connection attempt. 0 successful connection attempts. 0 timeouts. 1 other error. Last error: IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testFailureMultipleErrorsExceptionThrown() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, 5, 0, 0, 5, 500, exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. 5 connection attempts. 0 successful connection attempts. 0 timeouts. 5 other errors. Last error: IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testFailureWithoutException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, 1, 0, 0, 1, 1, null);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. 1 connection attempt. 0 successful connection attempts. 0 timeouts. 1 other error.", logEntry.getMessage());
    }

    @Test
    public void testFailureMultipleErrorsAndTimeoutsExceptionThrown() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        ConnectCommandResult connectCommandResult = new ConnectCommandResult(false, 5, 0, 3, 2, 500, exception);
        prepareTestConnectNetworkTaskWorker(dnsLookupResult, connectCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = connectNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:22 failed. 5 connection attempts. 0 successful connection attempts. 3 timeouts. 2 other errors. Last error: IllegalArgumentException: TestException", logEntry.getMessage());
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
        task.setLastScheduled(1);
        return task;
    }
}
