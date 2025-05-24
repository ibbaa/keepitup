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

import androidx.documentfile.provider.DocumentFile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.DownloadCommandResult;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.MockDownloadCommand;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.MockNotificationManager;
import net.ibbaa.keepitup.test.mock.MockStoragePermissionManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestDownloadNetworkTaskWorker;
import net.ibbaa.keepitup.test.mock.TestRegistry;

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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        fileManager.setSDCardSupported(false);
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
            mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), getNetworkTask(), new URL("http://127.0.0.1"), "folder", true, downloadCommandResult);
        } else {
            mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), getNetworkTask(), new URL("http://127.0.0.1"), "folder", true, exception);
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
        mockDownloadCommand = new MockDownloadCommand(TestRegistry.getContext(), task, new URL("http://127.0.0.1"), "folder", true, downloadCommandResult, true);
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
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        NetworkTask networkTask = getNetworkTask();
        networkTask.setAddress("invalid url");
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(networkTask, getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The url invalid url is invalid.", logEntry.getMessage());
    }

    @Test
    public void testURLEncoding() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        NetworkTask networkTask = getNetworkTask();
        networkTask.setAddress("https://test.com/tes t");
        downloadNetworkTaskWorker.execute(networkTask, getAccessTypeData());
        MockDownloadCommand downloadCommand = downloadNetworkTaskWorker.getMockDownloadCommand();
        assertEquals("https://test.com/tes%20t", downloadCommand.getUrl().toString());
        assertEquals("/test", downloadCommand.getFolder());
        networkTask.setAddress("https://test.com/tes%20t");
        downloadNetworkTaskWorker.execute(networkTask, getAccessTypeData());
        downloadCommand = downloadNetworkTaskWorker.getMockDownloadCommand();
        assertEquals("https://test.com/tes%20t", downloadCommand.getUrl().toString());
        assertEquals("/test", downloadCommand.getFolder());
        networkTask.setAddress("https://test.com/tes%20t?xyz%20=%201");
        downloadNetworkTaskWorker.execute(networkTask, getAccessTypeData());
        downloadCommand = downloadNetworkTaskWorker.getMockDownloadCommand();
        assertEquals("https://test.com/tes%20t?xyz%20=%201", downloadCommand.getUrl().toString());
        assertEquals("/test", downloadCommand.getFolder());
    }

    @Test
    public void testInvalidInternalFolder() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        preferenceManager.setPreferenceDownloadExternalStorage(false);
        fileManager.setInternalDownloadDirectory(null);
        fileManager.setDefaultDownloadDirectoryName("download");
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The download folder download does not exist and cannot be created.", logEntry.getMessage());
    }

    @Test
    public void testInvalidExternalFolder() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(null, 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. The download folder download does not exist and cannot be created.", logEntry.getMessage());
    }

    @Test
    public void testInvalidArbitraryDownloadFolderNoPermission() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("Test");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        MockDocumentManager documentManager = new MockDocumentManager();
        downloadNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("Test")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        downloadNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(null, 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. Missing permission to access download folder: Test.", logEntry.getMessage());
    }

    @Test
    public void testInvalidArbitraryDownloadFolder() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("%3ATest");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, (DownloadCommandResult) null);
        MockDocumentManager documentManager = new MockDocumentManager();
        downloadNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(null);
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        downloadNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(null, 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Download not possible. Missing permission to access download folder: :Test.", logEntry.getMessage());
    }

    @Test
    public void testValidArbitraryFolder() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("Movies");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        MockDocumentManager documentManager = new MockDocumentManager();
        downloadNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("Test")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        downloadNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The file was deleted after download. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testValidArbitraryFolderWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("Movies");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("test", ""), "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        MockDocumentManager documentManager = new MockDocumentManager();
        downloadNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("Test")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        downloadNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "Movies");
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 test. The download from http://127.0.0.1 was successful. The file was deleted after download. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testConnectionFailed() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), false, false, false, false, false, false, Collections.emptyList(), Collections.emptyList(), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Connection to 127.0.0.1 failed.", logEntry.getMessage());
    }

    @Test
    public void testConnectionFailedWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), false, false, false, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("test1", "test2", ""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 test1. Server returned redirect 302 test2. Connection to 127.0.0.1 failed.", logEntry.getMessage());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, false, List.of(HttpURLConnection.HTTP_NOT_FOUND), List.of(""), null, 20, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. Server return code 404.", logEntry.getMessage());
    }

    @Test
    public void testHTTPResponseCodeNotOkWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_NOT_FOUND), List.of("test", "message"), null, 20, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 test. The download from http://127.0.0.1 failed. Server return code 404 message.", logEntry.getMessage());
    }

    @Test
    public void testHTTPResponseCodeNonHTTP() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, Collections.emptyList(), Collections.emptyList(), null, 20, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The file was deleted after download. 20 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testHTTPResponseCodeNonHTTPWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 20, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The file was deleted after download. 20 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileDoesNotExist() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1", "2", "3"), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileDoesNotExistWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of(""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301. Server returned redirect 301. Server returned redirect 301. The download was stopped.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileDoesNotExistWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileDoesNotExistWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "2"), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 1. The download was stopped. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. Downloaded file: /Test/testfile. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistNotDeleteWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_OK), List.of("---", ""), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. Downloaded file: /Test/testfile. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistNotDeleteWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 10000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. Downloaded file: /Test/testfile. 10 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistNotDeleteWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "2", "3", "4", "5"), "testfile", 10000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. Server returned redirect 302 2. Server returned redirect 302 3. Server returned redirect 302 4. Server returned redirect 302 5. The download was stopped. The file was partially downloaded. Downloaded file: /Test/testfile. 10 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedInternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "1", "1"), "testfile", 1, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedInternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301. The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedInternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 5, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 5 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedInternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1"), "testfile", 5, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 5 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedExternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 100 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedExternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("123", "123", "123"), "testfile", 100000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 123. The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 100 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedExternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 9, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 9 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteFailedExternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, true, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("", "123"), "testfile", 9, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302. The download was stopped. The file was partially downloaded. The deletion of the partially_downloaded file failed. 9 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteSuccess() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1", "1"), "testfile", 1, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The partially downloaded file was deleted. 1 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteSuccessWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The partially downloaded file was deleted. 1 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteSuccessWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, true, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped. The file was partially downloaded. The partially downloaded file was deleted. 1 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadStoppedFileExistDeleteSuccessWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, true, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1", "2", "3", "4", "5", "6", "7"), "testfile", 1, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. Server returned redirect 301 2. Server returned redirect 302 3. Server returned redirect 301 4. Server returned redirect 301 5. Server returned redirect 301 6. The download was stopped. The file was partially downloaded. The partially downloaded file was deleted. 1 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileDoesNotExist() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileDoesNotExistWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of(""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301. Server returned redirect 301. Server returned redirect 301. The download was stopped because the network task is no longer valid.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileDoesNotExistWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "2", "3"), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileDoesNotExistWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("3", "2", "1"), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 3. Server returned redirect 302 2. The download was stopped because the network task is no longer valid. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 3600000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. Downloaded file: /Test/testfile. 3,600 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistNotDeleteWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("123"), "testfile", 3600000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 123. The download was stopped because the network task is no longer valid. The file was partially downloaded. Downloaded file: /Test/testfile. 3,600 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistNotDeleteWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 3, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. Downloaded file: /Test/testfile. 3 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistNotDeleteWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "2"), "testfile", 3, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. Server returned redirect 302 2. The download was stopped because the network task is no longer valid. The file was partially downloaded. Downloaded file: /Test/testfile. 3 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedInternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedInternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedInternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 12000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 12 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedInternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1", "2"), "testfile", 12000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 12 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedExternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 20000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 20 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedExternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "2", "3"), "testfile", 20000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 1. The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 20 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedExternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 7200000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 7,200 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteFailedExternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, false, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 7200000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302. Server returned redirect 301. The download was stopped because the network task is no longer valid. The file was partially downloaded. The deletion of the partially_downloaded file failed. 7,200 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteSuccess() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 50, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The partially downloaded file was deleted. 50 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteSuccessWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("123456"), "testfile", 50, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 123456. The download was stopped because the network task is no longer valid. The file was partially downloaded. The partially downloaded file was deleted. 50 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteSuccessWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 3, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download was stopped because the network task is no longer valid. The file was partially downloaded. The partially downloaded file was deleted. 3 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadInvalidFileExistDeleteSuccessWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, false, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1"), "testfile", 3, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. Server returned redirect 301. The download was stopped because the network task is no longer valid. The file was partially downloaded. The partially downloaded file was deleted. 3 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileDoesNotExist() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileDoesNotExistWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1"), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 1. Server returned redirect 302. The download from http://127.0.0.1 failed for an unknown reason.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileDoesNotExistWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileDoesNotExistWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, false, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 2000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. Downloaded file: /Test/testfile. 2 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testArbitraryDownloadUnknownErrorFileExistNotDeleteDecoded() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("%3ATest");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        MockDocumentManager documentManager = new MockDocumentManager();
        downloadNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("%3ATest")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        downloadNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "%3ATest");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. Downloaded file: :Test/testfile. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistNotDeleteWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1", "2"), "testfile", 2000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. Downloaded file: /Test/testfile. 2 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistNotDeleteWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 44, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. The file was partially downloaded. Downloaded file: /Test/testfile. 44 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistNotDeleteWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("12", "", "34", ""), "testfile", 44, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 12. Server returned redirect 301. Server returned redirect 301 34. The download from http://127.0.0.1 failed. The file was partially downloaded. Downloaded file: /Test/testfile. 44 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedInternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedInternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302. The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedInternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedInternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 1, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. Server returned redirect 302. The download from http://127.0.0.1 failed. The file was partially downloaded. The deletion of the partially_downloaded file failed. 1 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedExternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 3, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. The deletion of the partially_downloaded file failed. 3 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedExternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 3, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. The deletion of the partially_downloaded file failed. 3 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedExternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 500000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. The file was partially downloaded. The deletion of the partially_downloaded file failed. 500 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteFailedExternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("a", "b", "c"), "testfile", 500000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 a. Server returned redirect 301 b. The download from http://127.0.0.1 failed. The file was partially downloaded. The deletion of the partially_downloaded file failed. 500 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteSuccess() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 2, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. The partially downloaded file was deleted. 2 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteSuccessWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("5"), "testfile", 2, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 5. The download from http://127.0.0.1 failed for an unknown reason. The file was partially downloaded. The partially downloaded file was deleted. 2 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteSuccessWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("2", "3", "4"), "testfile", 5, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. The file was partially downloaded. The partially downloaded file was deleted. 5 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadUnknownErrorFileExistDeleteSuccessWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, false, true, true, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("5"), "testfile", 5, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 5. The download from http://127.0.0.1 failed. The file was partially downloaded. The partially downloaded file was deleted. 5 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadExceptionOnCommandExecution() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        RuntimeException exception = new RuntimeException("Test");
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, exception);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. RuntimeException: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileDoesNotExist() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, false, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed for an unknown reason.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileDoesNotExistWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, false, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of(""), null, 0, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301. Server returned redirect 302. Server returned redirect 301. The download from http://127.0.0.1 failed for an unknown reason.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileDoesNotExistWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, false, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 failed. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileDoesNotExistWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, false, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("2", "1"), null, 0, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertFalse(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 2. The download from http://127.0.0.1 failed. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. Downloaded file: /Test/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistNotDeleteWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. Downloaded file: /Test/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testArbitraryDownloadSuccessFileExistNotDeleteDecoded() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("%3ATest");
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        MockDocumentManager documentManager = new MockDocumentManager();
        downloadNetworkTaskWorker.setDocumentManager(documentManager);
        documentManager.setArbitraryDirectory(DocumentFile.fromFile(new File("%3ATest")));
        MockStoragePermissionManager storagePermissionManager = new MockStoragePermissionManager();
        downloadNetworkTaskWorker.setStoragePermissionManager(storagePermissionManager);
        storagePermissionManager.requestPersistentFolderPermission(null, "%3ATest");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. Downloaded file: :Test/testfile. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessSDCardNotSupported() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        fileManager.setExternalDirectory(new File("Test0"), 0);
        fileManager.setExternalDirectory(new File("Test1"), 1);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. Downloaded file: /Test0/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessSDCardNotSupportedWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        fileManager.setExternalDirectory(new File("Test0"), 0);
        fileManager.setExternalDirectory(new File("Test1"), 1);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 302. The download from http://127.0.0.1 was successful. Downloaded file: /Test0/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessSDCardNotSupportedSDCard() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1"), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        fileManager.setSDCardSupported(true);
        fileManager.setExternalDirectory(new File("Test0"), 0);
        fileManager.setExternalDirectory(new File("Test1"), 1);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. Downloaded file: /Test1/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessSDCardNotSupportedSDCardWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("302", "302", "302", "302"), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        fileManager.setSDCardSupported(true);
        fileManager.setExternalDirectory(new File("Test0"), 0);
        fileManager.setExternalDirectory(new File("Test1"), 1);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 302. Server returned redirect 302 302. Server returned redirect 302 302. The download from http://127.0.0.1 was successful. Downloaded file: /Test1/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessSDCardNotSupportedPrimary() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(0);
        fileManager.setSDCardSupported(true);
        fileManager.setExternalDirectory(new File("Test0"), 0);
        fileManager.setExternalDirectory(new File("Test1"), 1);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. Downloaded file: /Test0/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessSDCardNotSupportedPrimaryWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(0);
        fileManager.setSDCardSupported(true);
        fileManager.setExternalDirectory(new File("Test0"), 0);
        fileManager.setExternalDirectory(new File("Test1"), 1);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 301. Server returned redirect 302. The download from http://127.0.0.1 was successful. Downloaded file: /Test0/testfile. 100 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteSuccess() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The file was deleted after download. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteSuccessWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, true, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("301"), "testfile", 999, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 301. The download from http://127.0.0.1 was successful. The file was deleted after download. 999 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedInternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 333, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 333 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedInternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("302"), "testfile", 333, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 302. The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 333 msec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedInternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 33000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 33 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedInternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 33000, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 33 sec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedExternalStorage() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "1"), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedExternalStorageWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_OK), List.of("1", "1", "1", "1"), "testfile", 1000, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 301 1. Server returned redirect 301 1. The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 1 sec download time.", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedExternalStorageWithException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of(""), "testfile", 22, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 22 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    public void testDownloadSuccessFileExistDeleteFailedExternalStorageWithExceptionWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_MOVED_TEMP, HttpURLConnection.HTTP_OK), List.of("1", "1", "1", "1"), "testfile", 22, new Exception("Test"));
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult);
        preferenceManager.setPreferenceDownloadKeep(false);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        fileManager.setExternalDirectory(new File("Test"), 0);
        NetworkTaskWorker.ExecutionResult executionResult = downloadNetworkTaskWorker.execute(getNetworkTask(), getAccessTypeData());
        LogEntry logEntry = executionResult.getLogEntry();
        assertEquals(45, logEntry.getNetworkTaskId());
        assertEquals(getTestTimestamp(), logEntry.getTimestamp());
        assertTrue(logEntry.isSuccess());
        assertEquals("Server returned redirect 302 1. Server returned redirect 302 1. The download from http://127.0.0.1 was successful. The deletion of the downloaded file failed. 22 msec download time. Exception: Test", logEntry.getMessage());
    }

    @Test
    @SuppressWarnings({"BusyWait"})
    public void testDownloadThreadInterrupted() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        DNSLookupResult dnsLookupResult = new DNSLookupResult(Arrays.asList(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("::1")), null);
        DownloadCommandResult downloadCommandResult = new DownloadCommandResult(new URL("http://127.0.0.1"), true, true, true, false, true, false, List.of(HttpURLConnection.HTTP_OK), List.of(""), "testfile", 100, null);
        TestDownloadNetworkTaskWorker downloadNetworkTaskWorker = prepareBlockingTestDownloadNetworkTaskWorker(dnsLookupResult, downloadCommandResult, task);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<?> downloadFuture = executorService.submit(downloadNetworkTaskWorker);
        MockDownloadCommand downloadCommand = downloadNetworkTaskWorker.getMockDownloadCommand();
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
        assertEquals("The download from http://127.0.0.1 failed. The task was stopped.", logEntry.getMessage());
        NotificationHandler notificationHandler = downloadNetworkTaskWorker.getNotificationHandler();
        MockNotificationManager notificationManager = (MockNotificationManager) notificationHandler.getNotificationManager();
        assertFalse(notificationManager.wasNotifyCalled());
    }

    @Test
    public void testGetMaxInstancesErrorMessage() {
        DownloadNetworkTaskWorker downloadNetworkTaskWorker = new DownloadNetworkTaskWorker(TestRegistry.getContext(), getNetworkTask(), null);
        assertEquals("Currently is 1 download active, which is the maximum. Skipped execution.", downloadNetworkTaskWorker.getMaxInstancesErrorMessage(1));
        assertEquals("Currently are 2 downloads active, which is the maximum. Skipped execution.", downloadNetworkTaskWorker.getMaxInstancesErrorMessage(2));
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
        task.setAddress("http://127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        task.setHighPrio(true);
        return task;
    }

    private AccessTypeData getAccessTypeData() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }
}
