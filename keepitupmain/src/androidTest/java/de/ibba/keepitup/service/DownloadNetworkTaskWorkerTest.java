package de.ibba.keepitup.service;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Dump;
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
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DownloadNetworkTaskWorkerTest {

    private MockFileManager fileManager;
    private PreferenceManager preferenceManager;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        fileManager = new MockFileManager();
        fileManager.setInternalDownloadDirectory(new File("test"));
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        InstrumentationRegistry.getInstrumentation().getTargetContext().getResources().getConfiguration().setLocale(Locale.US);
    }

    @After
    public void afterEachTestMethod() {
        fileManager.reset();
        preferenceManager.removeAllPreferences();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    private TestDownloadNetworkTaskWorker prepareTestDownloadNetworkTaskWorker(DNSLookupResult dnsLookupResult, DownloadCommandResult downloadCommandResult, RuntimeException exception) throws Exception {
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = new TestDownloadNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        MockDownloadCommand mockDownloadCommand;
        if (exception == null) {
            mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), getNetworkTask(), new URL("http://127.0.0.1"), new File("folder"), true, downloadCommandResult);
        } else {
            mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), getNetworkTask(), new URL("http://127.0.0.1"), new File("folder"), true, exception);
        }
        downloadNetworkTaskWorker.setMockDNSLookup(mockDNSLookup);
        downloadNetworkTaskWorker.setMockDownloadCommand(mockDownloadCommand);
        downloadNetworkTaskWorker.setMockFileManager(fileManager);
        MockTimeService timeService = (MockTimeService) downloadNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
        return downloadNetworkTaskWorker;
    }

    private TestDownloadNetworkTaskWorker prepareBlockingTestDownloadNetworkTaskWorker(DNSLookupResult dnsLookupResult, DownloadCommandResult downloadCommandResult, NetworkTask task) throws Exception {
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = new TestDownloadNetworkTaskWorker(TestRegistry.getContext(), task, null);
        MockDNSLookup mockDNSLookup = new MockDNSLookup("127.0.0.1", dnsLookupResult);
        MockDownloadCommand mockDownloadCommand;
        mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), task, new URL("http://127.0.0.1"), new File("folder"), true, downloadCommandResult, true);
        downloadNetworkTaskWorker.setMockDNSLookup(mockDNSLookup);
        downloadNetworkTaskWorker.setMockDownloadCommand(mockDownloadCommand);
        downloadNetworkTaskWorker.setMockFileManager(fileManager);
        MockTimeService timeService = (MockTimeService) downloadNetworkTaskWorker.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp());
        return downloadNetworkTaskWorker;
    }

    private TestDownloadNetworkTaskWorker prepareTestDownloadNetworkTaskWorker(DNSLookupResult dnsLookupResult, DownloadCommandResult downloadCommandResult) throws Exception {
        return prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult, null);
    }

    private TestDownloadNetworkTaskWorker prepareTestDownloadNetworkTaskWorker(DNSLookupResult dnsLookupResult, RuntimeException exception) throws Exception {
        return prepareTestDownloadNetworkTaskWorker(dnsLookupResult, null, exception);
    }

    @Test
    public void testInvalidURL() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        NetworkTask networkTask = getNetworkTask();
        networkTask.setAddress("invalid url");
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(networkTask);
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The url invalid url is invalid.", logEntry.getMessage());
    }

    @Test
    public void testInvalidInternalFolder() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        preferenceManager.setPreferenceDownloadExternalStorage(false);
        fileManager.setInternalDownloadDirectory(null);
        fileManager.setDefaultDownloadDirectoryName("download");
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The download folder download does not exist and cannot be created.", logEntry.getMessage());
    }

    @Test
    public void testInvalidExternalFolder() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(null);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The download folder download does not exist and cannot be created.", logEntry.getMessage());
    }

    @Test
    public void testConnectionFailed() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(false, false, false, false, false, false, -1, null, null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1:80 failed.", logEntry.getMessage());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, false, false, HttpURLConnection.HTTP_NOT_FOUND, "message", null, 20, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. Server return code 404 message.", logEntry.getMessage());
    }

    @Test
    public void testHTTPResponseCodeNonHTTP() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, true, true, false, -1, "message", null, 20, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. The file was deleted after download. 20 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileDoesNotExist() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, false, true, HttpURLConnection.HTTP_OK, null, null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileDoesNotExistWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, false, true, HttpURLConnection.HTTP_OK, null, null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistNotDelete() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. Downloaded file: /Test/testfile. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistNotDeleteWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 10000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. Downloaded file: /Test/testfile. 10 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedInternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 1, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedInternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 5, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 5 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedExternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 100000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 100 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedExternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 9, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 9 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, true, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 1, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The partially downloaded file was deleted. 1 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteSuccessWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, true, false, true, HttpURLConnection.HTTP_OK, null, "testfile", 1, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The partially downloaded file was deleted. 1 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileDoesNotExist() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, false, false, HttpURLConnection.HTTP_OK, null, null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileDoesNotExistWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, false, false, HttpURLConnection.HTTP_OK, null, null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistNotDelete() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 3600000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. Downloaded file: /Test/testfile. 3,600 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistNotDeleteWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 3, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. Downloaded file: /Test/testfile. 3 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedInternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedInternalStorageWithExcpetion() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 12000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 12 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedExternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 20000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 20 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedExternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 7200000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 7,200 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, true, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 50, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The partially downloaded file was deleted. 50 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteSuccessWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, true, false, false, HttpURLConnection.HTTP_OK, null, "testfile", 3, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The partially downloaded file was deleted. 3 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileDoesNotExist() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, true, false, HttpURLConnection.HTTP_OK, null, null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed for an unknown reason.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileDoesNotExistWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, false, false, true, false, HttpURLConnection.HTTP_OK, null, null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistNotDelete() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 2000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed for an unknown reason. The file was partially downloaded. Downloaded file: /Test/testfile. 2 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistNotDeleteWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 44, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. The file was partially downloaded. Downloaded file: /Test/testfile. 44 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedInternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed for an unknown reason. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedInternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 1, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedExternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 3, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed for an unknown reason. The file was partially downloaded. The deletion of the partially_downloaded file failed. 3 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedExternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 500000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. The file was partially downloaded. The deletion of the partially_downloaded file failed. 500 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, true, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 2, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed for an unknown reason. The file was partially downloaded. The partially downloaded file was deleted. 2 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteSuccessWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, false, true, true, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 5, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. The file was partially downloaded. The partially downloaded file was deleted. 5 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadExceptionOnCommandExecution() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        RuntimeException exception = new RuntimeException("Test");
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, exception);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. RuntimeException: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileDoesNotExist() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, false, false, true, false, HttpURLConnection.HTTP_OK, null, null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed for an unknown reason.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileDoesNotExistWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, false, false, true, false, HttpURLConnection.HTTP_OK, null, null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 failed. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistNotDelete() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. Downloaded file: /Test/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteSuccess() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, true, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. The file was deleted after download. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedInternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 333, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. The deletion of the downloaded file failed. 333 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedInternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 33000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. The deletion of the downloaded file failed. 33 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedExternalStorage() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. The deletion of the downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedExternalStorageWithException() throws Exception {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 22, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"));
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1:80 was successful. The deletion of the downloaded file failed. 22 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadThreadInterrupted() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(true, true, true, false, true, false, HttpURLConnection.HTTP_OK, null, "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareBlockingTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult, task);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> downloadFuture = executorService.submit(downloadNetworkTaskWorker);
        MockDownloadCommand downloadCommand = (MockDownloadCommand) downloadNetworkTaskWorker.getDownloadCommand(null, null, null, false);
        downloadCommand.waitUntilReady();
        downloadFuture.cancel(true);
        List<LogEntry> logs = logDAO.readAllLogsForNetworkTask(task.getId());
        while (logs.isEmpty()) {
            Thread.sleep(1000);
            logs = logDAO.readAllLogsForNetworkTask(task.getId());
        }
        assertEquals(1, logs.size());
        LogEntry logEntry = logs.get(0);
        assertFalse(logEntry.isSuccess());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertEquals("The download from http://127.0.0.1:80 failed. The task was stopped manually.", logEntry.getMessage());
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
        task.setAddress("http://127.0.0.1:80");
        task.setPort(80);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        return task;
    }
}
