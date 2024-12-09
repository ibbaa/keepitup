/*
 * Copyright (c) 2025 Alwin Ibba
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
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.PingCommandResult;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockPingCommand;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestPingNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;

@MediumTest
@SuppressWarnings({"TextBlockMigration"})
@RunWith(AndroidJUnit4.class)
public class PingNetworkTaskWorkerTest {

    private TestPingNetworkTaskWorker pingNetworkTaskWorker;

    @Before
    public void beforeEachTestMethod() {
        InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().setLocale(Locale.US);
        pingNetworkTaskWorker = new TestPingNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
    }

    private void prepareTestPingNetworkTaskWorker(DNSLookupResult dnsLookupResult, PingCommandResult pingCommandResult) {
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        MockPingCommand mockPingCommand = new MockPingCommand(TestRegistry.getContext(), "127.0.0.1", 3, false, 56, false, false, pingCommandResult);
        pingNetworkTaskWorker.setMockDNSLookup(mockDNSLookup);
        pingNetworkTaskWorker.setMockPingCommand(mockPingCommand);
        MockTimeService timeService = (MockTimeService) pingNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
    }

    @Test
    public void testSuccessfulCallUnparseableResult() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, "testoutput", null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged 127.0.0.1 successfully. testoutput", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallUnparseableResultStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, "testoutput", null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged 127.0.0.1 successfully. testoutput", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResult() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("::1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, getTestIP4Ping(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged ::1 successfully. 64 bytes received per packet. 3 packets transmitted. 3 packets received. 0% packet loss. 0.08 msec average time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResultStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("::1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, getTestIP4PingOnePacket(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged ::1 successfully. 64 bytes received per packet. 1 packet transmitted. 1 packet received. 0% packet loss. 1 sec ping time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResultStopOnSuccess2PingCalls() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("::1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 2, getTestIP4PingOnePacket(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged ::1 successfully. 64 bytes received per packet. 2 packets transmitted. 1 packet received. 50% packet loss. 1 sec ping time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResultIP6MaxBytes() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("::1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, getTestIP6PingMaxBytes(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged ::1 successfully. 65535 bytes received per packet. 3 packets transmitted. 3 packets received. 0% packet loss. 20.5 msec average time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResultOnePacket() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("::1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, getTestIP4PingOnePacket(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged ::1 successfully. 64 bytes received per packet. 1 packet transmitted. 1 packet received. 0% packet loss. 1 sec ping time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResultOnePacket8Bytes() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("::1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, getTestIP4PingOnePacket8Bytes(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged ::1 successfully. 8 bytes received per packet. 1 packet transmitted. 1 packet received. 0% packet loss. 1 sec ping time.", logEntry.getMessage());
    }

    @Test
    public void testSuccessfulCallParseableResultAverageTime() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, getTestIP4PingAverageTime(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertTrue(logEntry.isSuccess());
        assertEquals("Pinged 127.0.0.1 successfully. 64 bytes received per packet. 3 packets transmitted. 3 packets received. 0% packet loss. 2 sec average time.", logEntry.getMessage());
    }

    @Test
    public void testDNSLookupExceptionThrown() {
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, null);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for 127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testPingCommandExceptionThrown() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        PingCommandResult pingCommandResult = new PingCommandResult(0, 1, "testoutput", exception);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testPingCommandExceptionThrownStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        PingCommandResult pingCommandResult = new PingCommandResult(0, 5, "testoutput", exception);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageUnparseable() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 1, "testoutput", null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. testoutput", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageUnparseableStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 1, "testoutput", null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. testoutput", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageParseable() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 1, getTestIP4Ping(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. 64 bytes received per packet. 3 packets transmitted. 3 packets received. 0% packet loss. 0.08 msec average time.", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageParseableStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 2, getTestIP4PingOnePacket(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. 64 bytes received per packet. 2 packets transmitted. 1 packet received. 50% packet loss. 1 sec ping time.", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageParseableOnePacket() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 1, getTestIP4PingOnePacket(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. 64 bytes received per packet. 1 packet transmitted. 1 packet received. 0% packet loss. 1 sec ping time.", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageParseable100Loss() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("192.168.178.12"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 1, getTestIP4PingFailure(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 192.168.178.12 failed. 3 packets transmitted. 0 packets received. 100% packet loss.", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithMessageParseable100LossStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("192.168.178.12"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 3, getTestIP4PingFailure(), null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 192.168.178.12 failed. 3 packets transmitted. 0 packets received. 100% packet loss.", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithoutMessage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 1, "", null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(false));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. Return code: 1", logEntry.getMessage());
    }

    @Test
    public void testFailureCodeReturnedWithoutMessageStopOnSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getByName("127.0.0.1"), null);
        PingCommandResult pingCommandResult = new PingCommandResult(1, 2, "", null);
        prepareTestPingNetworkTaskWorker(dnsLookupResult, pingCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = pingNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData(true));
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals(10, pingNetworkTaskWorker.getPingCount());
        assertFalse(logEntry.isSuccess());
        assertEquals("Ping to 127.0.0.1 failed. Return code: 1", logEntry.getMessage());
    }

    @Test
    public void testGetMaxInstancesErrorMessage() {
        PingNetworkTaskWorker pingNetworkTaskWorker = new PingNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
        assertEquals("Currently is 1 ping attempt active, which is the maximum. Skipped execution.", pingNetworkTaskWorker.getMaxInstancesErrorMessage(1));
        assertEquals("Currently are 2 ping attempts active, which is the maximum. Skipped execution.", pingNetworkTaskWorker.getMaxInstancesErrorMessage(2));
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
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        return task;
    }

    private AccessTypeData getAccessTypeData(boolean stopOnSuccess) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(stopOnSuccess);
        return data;
    }

    private String getTestIP4Ping() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.084 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=0.083 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=0.083 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "3 packets transmitted, 3 received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP4PingAverageTime() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=1000 ms\n" +
                " 64 bytes from 127.0.0.1: icmp_seq=2 ttl=64 time=2000 ms\n" +
                "64 bytes from 127.0.0.1: icmp_seq=3 ttl=64 time=3000 ms\n\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "3 packets transmitted, 3 received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP4PingOnePacket() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=1000 ms\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "1 packets transmitted, 1 received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP4PingOnePacket8Bytes() {
        return "PING 127.0.0.1 (127.0.0.1) 56(84) bytes of data.\n" +
                "8 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=1000 ms\n" +
                "--- 127.0.0.1 ping statistics ---\n" +
                "1 packets transmitted, 1 received, 0% packet loss, time 1998ms\n" +
                "rtt min/avg/max/mdev = 0.083/0.083/0.084/0.007 ms";
    }

    private String getTestIP4PingFailure() {
        return "PING 192.168.178.12 (192.168.178.12) 56(84) bytes of data.\n" +
                "--- 192.168.178.12 ping statistics ---\n" +
                "3 packets transmitted, 0 received, 100% packet loss, time 2008ms";
    }

    private String getTestIP6PingMaxBytes() {
        return "PING 2a00:1450:4016:801::200e(2a00:1450:4016:801::200e) 56 data bytes\n" +
                "65535 bytes from 2a00:1450:4016:801::200e: icmp_seq=1 ttl=57 time=10.5 ms\n" +
                "65535 bytes from 2a00:1450:4016:801::200e: icmp_seq=2 ttl=57 time=20.5 ms\n" +
                "65535 bytes from 2a00:1450:4016:801::200e: icmp_seq=3 ttl=57 time=30.5 ms\n\n" +
                "--- 2a00:1450:4016:801::200e ping statistics ---\n" +
                " 3 packets transmitted, 3 received, 0% packet loss, time 2003ms\n" +
                "rtt min/avg/max/mdev = 10.511/20.134/26.160/6.877 ms";
    }
}
