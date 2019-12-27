package de.ibba.keepitup.service;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.network.DNSLookupResult;
import de.ibba.keepitup.service.network.DownloadCommandResult;
import de.ibba.keepitup.test.mock.MockDNSLookup;
import de.ibba.keepitup.test.mock.MockDownloadCommand;
import de.ibba.keepitup.test.mock.MockFileManager;
import de.ibba.keepitup.test.mock.MockTimeService;
import de.ibba.keepitup.test.mock.TestDownloadNetworkTaskWorker;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DownloadNetworkTaskWorkerTest {

    private TestDownloadNetworkTaskWorker downloadNetworkTaskWorker;
    private MockFileManager fileManager;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        downloadNetworkTaskWorker = new TestDownloadNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
        fileManager = new MockFileManager();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        fileManager.reset();
        preferenceManager.removeAllPreferences();
    }

    private void prepareTestDownloadNetworkTaskWorker(DNSLookupResult dnsLookupResult, DownloadCommandResult downloadCommandResult) throws Exception {
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        MockDownloadCommand mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), getNetworkTask(), new URL("http://127.0.0.1"), new File("folder"), true, downloadCommandResult);
        downloadNetworkTaskWorker.setMockDNSLookup(mockDNSLookup);
        downloadNetworkTaskWorker.setMockDownloadCommand(mockDownloadCommand);
        downloadNetworkTaskWorker.setMockFileManager(fileManager);
        MockTimeService timeService = (MockTimeService) downloadNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
    }

    @Test
    public void testDNSLookupExceptionThrown() throws Exception {
        IllegalArgumentException exception = new IllegalArgumentException("TestException");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Collections.emptyList(), exception);
        prepareTestDownloadNetworkTaskWorker(dnsLookupResult, null);
        LogEntry logEntry = downloadNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("DNS lookup for http://127.0.0.1 failed. IllegalArgumentException: TestException", logEntry.getMessage());
    }

    @Test
    public void testInvalidURL() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        prepareTestDownloadNetworkTaskWorker(dnsLookupResult, null);
        NetworkTask networkTask = getNetworkTask();
        networkTask.setAddress("invalid url");
        LogEntry logEntry = downloadNetworkTaskWorker.execute(networkTask);
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The url invalid url is invalid.", logEntry.getMessage());
    }

    @Test
    public void testInvalidInternalFolder() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        prepareTestDownloadNetworkTaskWorker(dnsLookupResult, null);
        preferenceManager.setPreferenceDownloadExternalStorage(false);
        fileManager.setInternalDownloadDirectory(null);
        fileManager.setDefaultDownloadDirectoryName("download");
        LogEntry logEntry = downloadNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The download folder download does not exist and cannot be created.", logEntry.getMessage());
    }

    @Test
    public void testInvalidExternalFolder() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        prepareTestDownloadNetworkTaskWorker(dnsLookupResult, null);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(null);
        LogEntry logEntry = downloadNetworkTaskWorker.execute(getNetworkTask());
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The download folder download does not exist and cannot be created.", logEntry.getMessage());
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
        task.setAddress("http://127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }
}
