package de.ibba.keepitup.service;

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
import java.util.concurrent.Executors;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.notification.NotificationHandler;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.test.mock.MockDNSLookup;
import de.ibba.keepitup.test.mock.MockNetworkManager;
import de.ibba.keepitup.test.mock.MockNotificationManager;
import de.ibba.keepitup.test.mock.MockTimeService;
import de.ibba.keepitup.test.mock.TestNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

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
        networkTaskDAO.insertNetworkTask(task);
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
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        assertEquals(1, testNetworkTaskWorker.getInstancesOnExecute());
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
    }

    @Test
    public void testTooManyInstances() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.increaseNetworkTaskInstances(task.getSchedulerId());
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
        assertFalse(entry.isSuccess());
        assertEquals("TestMaxInstancesError 1", entry.getMessage());
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
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        assertEquals(1, testNetworkTaskWorker.getInstancesOnExecute());
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
    }

    @Test
    public void testNetworkTaskDoesNotExist() {
        NetworkTask task = getNetworkTask();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), task, null, false);
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
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(false);
        networkManager.setConnectedWithWiFi(true);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
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
        MockNetworkManager networkManager = (MockNetworkManager) testNetworkTaskWorker.getNetworkManager();
        networkManager.setConnected(true);
        networkManager.setConnectedWithWiFi(false);
        int activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
        testNetworkTaskWorker.run();
        activeInstances = networkTaskDAO.readNetworkTaskInstances(task.getSchedulerId());
        assertEquals(0, activeInstances);
    }

    @Test
    public void testExecuteDNSLookupPreferIP4() throws Exception {
        LogEntry logEntry = getLogEntry();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("::1"), InetAddress.getByName("127.0.0.1"), InetAddress.getByName("2a00:1450:4016:801::200e")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        InetAddress address = testNetworkTaskWorker.executeDNSLookup(Executors.newSingleThreadExecutor(), "host.com", logEntry, true);
        assertEquals(InetAddress.getByName("127.0.0.1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is 127.0.0.1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupPreferIP6() throws Exception {
        LogEntry logEntry = getLogEntry();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("::1"), InetAddress.getByName("127.0.0.1"), InetAddress.getByName("2a00:1450:4016:801::200e")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        InetAddress address = testNetworkTaskWorker.executeDNSLookup(Executors.newSingleThreadExecutor(), "host.com", logEntry, false);
        assertEquals(InetAddress.getByName("::1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is ::1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupPreferIP6NoIP6AddressAvailable() throws Exception {
        LogEntry logEntry = getLogEntry();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("192.168.178.1")), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        InetAddress address = testNetworkTaskWorker.executeDNSLookup(Executors.newSingleThreadExecutor(), "host.com", logEntry, false);
        assertEquals(InetAddress.getByName("127.0.0.1"), address);
        assertTrue(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com successful. Resolved address is 127.0.0.1.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupNoAddresses() {
        LogEntry logEntry = getLogEntry();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), null);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        InetAddress address = testNetworkTaskWorker.executeDNSLookup(Executors.newSingleThreadExecutor(), "host.com", logEntry, true);
        assertNull(address);
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com failed. No address for host.", logEntry.getMessage());
    }

    @Test
    public void testExecuteDNSLookupExceptionThrown() {
        LogEntry logEntry = getLogEntry();
        TestNetworkTaskWorker testNetworkTaskWorker = new TestNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null, true);
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        testNetworkTaskWorker.setMockDNSLookup(new MockDNSLookup("127.0.0.1", dnsLookupResult));
        InetAddress address = testNetworkTaskWorker.executeDNSLookup(Executors.newSingleThreadExecutor(), "host.com", logEntry, true);
        assertNull(address);
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for host.com failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    private void setCurrentTime(TestNetworkTaskWorker testNetworkTaskWorker) {
        MockTimeService timeService = (MockTimeService) testNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
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
        task.setSchedulerId(123);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }

    private LogEntry getLogEntry() {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(1);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(123);
        insertedLogEntry1.setMessage("TestMessage");
        return insertedLogEntry1;
    }
}
