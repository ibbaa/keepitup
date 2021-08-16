package net.ibbaa.keepitup.service;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

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

import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockNetworkManager;
import net.ibbaa.keepitup.test.mock.MockNotificationManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskWorkerTest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testSuccessfulExecution() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testSuccessfulExecutionNumberInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        assertEquals(1, testNetworkTaskWorker.getInstancesOnExecute());
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testTooManyInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.increaseNetworkTaskInstances(task.getId());
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true, 1);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("TestMaxInstancesError 1", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testInterrupted() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false, 10, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("failed", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("failed", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureWithoutNotification() {
        NetworkTask task = getNetworkTask();
        task.setNotification(false);
        networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("failed", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testFailureNumberInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        assertEquals(1, testNetworkTaskWorker.getInstancesOnExecute());
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testNetworkTaskDoesNotExist() {
        NetworkTask task = getNetworkTask();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNetworkTaskIsNotValid() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        task.setSchedulerId(task.getSchedulerId() + 1);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testNoNetworkConnectionWithoutNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("No active network connection.", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionWithNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(false);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("No active network connection.", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertTrue(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoNetworkConnectionNumberInstancesAfterExecution() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }


    @Test
    public void testNoWifiConnectionWithoutNotification() {
        NetworkTask task = getNetworkTask();
        networkTaskDAO.insertNetworkTask(task);
        task.setOnlyWifi(true);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        testNetworkTaskWorker.run();
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), entry.getTimestamp());
        assertLastScheduledInDatabase(task, getTestTimestamp());
        assertFalse(entry.isSuccess());
        assertEquals("Skipped. No active wifi connection.", entry.getMessage());
        NotificationHandler notificationHandler = testNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testNoWifiConnectionNumberInstancesAfterExecution() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        task.setOnlyWifi(true);
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, true);
        setCurrentTime(testNetworkTaskWorker);
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getId());
        assertEquals(0, activeInstances);
        assertLastScheduledInDatabase(task, getTestTimestamp());
    }

    @Test
    public void testExecuteDNSLookupPreferIP4() throws Exception {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("::1"), InetAddress.getByName("127.0.0.1"), InetAddress.getByName("2a00:1450:4016:801::200e")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", true);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertEquals(InetAddress.getByName("127.0.0.1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is 127.0.0.1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupPreferIP6() throws Exception {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("::1"), InetAddress.getByName("127.0.0.1"), InetAddress.getByName("2a00:1450:4016:801::200e")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", false);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertEquals(InetAddress.getByName("::1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is ::1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupPreferIP6NoIP6AddressAvailable() throws Exception {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("192.168.178.1")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", false);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertEquals(InetAddress.getByName("127.0.0.1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is 127.0.0.1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupNoAddresses() {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", true);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertNull(address);
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com failed. No address for host.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupExceptionThrown() {
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        NetworkTaskWorker.DNSExecutionResult dnsExecutionResult = testNetworkTaskWorker.executeDNSLookup("host.com", true);
        InetAddress address = dnsExecutionResult.getAddress();
        LogEntry logEntry = dnsExecutionResult.getLogEntry();
        assertNull(address);
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    private void setCurrentTime(TestNetworkTaskWorker testNetworkTaskWorker) {
        MockTimeService timeService = (MockTimeService) testNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
    }

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private void assertLastScheduledInDatabase(NetworkTask task, long value) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(value, task.getLastScheduled());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(45);
        task.setIndex(1);
        task.setSchedulerId(123);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        return task;
    }
}
