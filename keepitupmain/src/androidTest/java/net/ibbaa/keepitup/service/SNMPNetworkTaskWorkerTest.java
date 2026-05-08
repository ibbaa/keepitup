/*
 * Copyright (c) 2026 Alwin Ibba
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

import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.SNMPCommandResult;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPNetworkTaskWorker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeMap;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SNMPNetworkTaskWorkerTest {

    private TestSNMPNetworkTaskWorker worker;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        worker = new TestSNMPNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
    }

    private void prepareWorker(DNSLookupResult dnsLookupResult, SNMPCommandResult snmpCommandResult) {
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        worker.setMockDNSLookup(mockDNSLookup);
        worker.getMockSNMPCommand().setSnmpCommandResult(snmpCommandResult);
        MockTimeService timeService = (MockTimeService) worker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
    }

    private void prepareWorkerWithCommandException(DNSLookupResult dnsLookupResult, RuntimeException exception) {
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        worker.setMockDNSLookup(mockDNSLookup);
        worker.getMockSNMPCommand().setException(exception);
        MockTimeService timeService = (MockTimeService) worker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
    }

    @Test
    public void testSuccessNoSystemValues() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, Collections.emptyMap(), false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 successful. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testSuccessWithSysUpTimeOnly() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.3.0", "1200");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, result, false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 successful. System Uptime (sysUpTime): 12s. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testSuccessWithSystemValuesOnly() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.1.0", "Test system");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, result, false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 successful. System Description (sysDescr): Test system. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testSuccessWithSystemValuesAndSysUpTime() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.1.0", "Test system");
        result.put("1.3.6.1.2.1.1.3.0", "1200");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, result, false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 successful. System Description (sysDescr): Test system, System Uptime (sysUpTime): 12s. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testSuccessWithReboot() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.3.0", "1200");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, result, true, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 successful. Device reboot detected (sysUpTime reset). System Uptime (sysUpTime): 12s. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testSuccessWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, Collections.emptyMap(), false, exception, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 successful. Request time: 0 msec. Last error: IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testFailureNoErrors() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(false, Collections.emptyMap(), false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 failed. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testFailureWithSingleError() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(false, Collections.emptyMap(), false, null, List.of("No response from host."), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 failed. Error: No response from host. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testFailureWithMultipleErrors() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(false, Collections.emptyMap(), false, null, List.of("Error 1", "Error 2"), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 failed. Errors: Error 1, Error 2. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testFailureWithErrorAndSystemValues() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.1.0", "Test system");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(false, result, false, null, List.of("Mandatory OID missing"), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 failed. Error: Mandatory OID missing, System Description (sysDescr): Test system. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testFailureWithErrorAndSystemValuesAndSysUpTime() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.1.0", "Test system");
        result.put("1.3.6.1.2.1.1.3.0", "1200");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(false, result, false, null, List.of("Some error"), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 failed. Error: Some error, System Description (sysDescr): Test system, System Uptime (sysUpTime): 12s. Request time: 0 msec.", logEntry.getMessage());
    }

    @Test
    public void testFailureCommandExceptionThrown() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        prepareWorkerWithCommandException(dnsLookupResult, new RuntimeException("TestException"));
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("SNMP request to 127.0.0.1:161 failed. RuntimeException: TestException", logEntry.getMessage());
    }

    @Test
    public void testDNSLookupNoAddress() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), "127.0.0.1", null);
        prepareWorker(dnsLookupResult, null);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for 127.0.0.1 failed. No address for host.", logEntry.getMessage());
    }

    @Test
    public void testDNSLookupExceptionThrown() throws Exception {
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), "127.0.0.1", exception);
        prepareWorker(dnsLookupResult, null);
        NetworkTaskWorker.ExecutionResult executionResult = worker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for 127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testLastSysUpTimeUpdatedOnSuccess() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        worker = new TestSNMPNetworkTaskWorker(TestRegistry.getContext(), task, null);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.3.0", "1200");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, result, false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        worker.execute(task, getAccessTypeData());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(1200, readTask.getLastSysUpTime());
    }

    @Test
    public void testLastSysUpTimeNotUpdatedWhenMissing() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        worker = new TestSNMPNetworkTaskWorker(TestRegistry.getContext(), task, null);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), "127.0.0.1", null);
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(true, Collections.emptyMap(), false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        worker.execute(task, getAccessTypeData());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(-1, readTask.getLastSysUpTime());
    }

    @Test
    public void testLastSysUpTimeUpdatedOnFailure() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        worker = new TestSNMPNetworkTaskWorker(TestRegistry.getContext(), task, null);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        TreeMap<String, String> result = new TreeMap<>();
        result.put("1.3.6.1.2.1.1.3.0", "500");
        SNMPCommandResult snmpCommandResult = new SNMPCommandResult(false, result, false, null, Collections.emptyList(), 0);
        prepareWorker(dnsLookupResult, snmpCommandResult);
        worker.execute(task, getAccessTypeData());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(500, readTask.getLastSysUpTime());
    }

    @Test
    public void testLastSysUpTimeNotUpdatedOnCommandException() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        worker = new TestSNMPNetworkTaskWorker(TestRegistry.getContext(), task, null);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), "127.0.0.1", null);
        prepareWorkerWithCommandException(dnsLookupResult, new RuntimeException("TestException"));
        worker.execute(task, getAccessTypeData());
        NetworkTask readTask = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(-1, readTask.getLastSysUpTime());
    }

    @Test
    public void testGetMaxInstances() {
        assertEquals(20, worker.getMaxInstances());
    }

    @Test
    public void testGetMaxInstancesErrorMessage() {
        SNMPNetworkTaskWorker snmpNetworkTaskWorker = new SNMPNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
        assertEquals("Currently is 1 SNMP request active, which is the maximum. Skipped execution.", snmpNetworkTaskWorker.getMaxInstancesErrorMessage(1));
        assertEquals("Currently are 2 SNMP requests active, which is the maximum. Skipped execution.", snmpNetworkTaskWorker.getMaxInstancesErrorMessage(2));
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
        task.setName("name");
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(161);
        task.setAccessType(AccessType.SNMP);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setLastSysUpTime(-1);
        task.setFailureCount(1);
        task.setHighPrio(true);
        return task;
    }

    private AccessTypeData getAccessTypeData() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setSnmpVersion(SNMPVersion.V2C);
        data.setSnmpCommunity("community");
        data.setSnmpCommunityValid(true);
        return data;
    }
}
