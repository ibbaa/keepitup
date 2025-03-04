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

package net.ibbaa.keepitup.service.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.documentfile.provider.DocumentFile;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.test.mock.BlockingTestInputStream;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.MockHttpURLConnection;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.MockURLConnection;
import net.ibbaa.keepitup.test.mock.TestDownloadCommand;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DownloadCommandTest {

    private NetworkTaskDAO networkTaskDAO;
    private SystemFileManager fileManager;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        fileManager = new SystemFileManager(TestRegistry.getContext());
        fileManager.delete(fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0));
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        fileManager.delete(fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0));
    }

    private MockHttpURLConnection prepareHttpURLConnection(String url, int responseCode, String responseMessage, InputStream inputStream) throws Exception {
        MockHttpURLConnection urlConnection = new MockHttpURLConnection(new URL(url));
        urlConnection.setRespondeCode(responseCode);
        urlConnection.setResponseMessage(responseMessage);
        urlConnection.setInputStream(inputStream);
        return urlConnection;
    }

    private MockHttpURLConnection prepareHttpURLConnection(String url, InputStream inputStream) throws Exception {
        return prepareHttpURLConnection(url, inputStream, HttpURLConnection.HTTP_OK);
    }

    private MockHttpURLConnection prepareHttpURLConnection(String url, InputStream inputStream, int responseCode) throws Exception {
        return prepareHttpURLConnection(url, responseCode, "Everything ok", inputStream);
    }

    @Test
    public void testConnectionFailed() {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertFalse(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertTrue(result.httpResponseCodes().isEmpty());
        assertTrue(result.httpResponseMessages().isEmpty());
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
    }

    @Test
    public void testConnectionFailedNegativeTime() {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setNegativeTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://test.com"), null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://test.com", HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.addURLConnection("http://test.com", urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, result.httpResponseCodes().get(0).intValue());
        assertEquals("not found", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testHTTPResponseCodeNotOkNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://test.com"), null, true);
        setNegativeTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://test.com", HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.addURLConnection("http://test.com", urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testHTTPResponseCodeNotOkWithLocationHeaderWithoutRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://test.com"), null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        urlConnection.addHeader("Location", "new location");
        downloadCommand.addURLConnection("http://test.com", urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals("moved Location: new location", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testSuccessWithOneRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://test.com"), externalDir.getAbsolutePath(), true);
        setCurrentTimeInverted(downloadCommand);
        MockHttpURLConnection urlConnection1 = prepareHttpURLConnection("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        urlConnection1.addHeader("Location", "http://www.host.com/test.jpg");
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection2 = prepareHttpURLConnection("http://www.host.com/test.jpg", inputStream);
        downloadCommand.addURLConnection("http://test.com", urlConnection1);
        downloadCommand.addURLConnection("http://www.host.com/test.jpg", urlConnection2);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(2, result.httpResponseCodes().size());
        assertEquals(2, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(1).intValue());
        assertEquals("moved Location: http://www.host.com/test.jpg", result.httpResponseMessages().get(0));
        assertEquals("Everything ok", result.httpResponseMessages().get(1));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection1.isDisconnected());
        assertTrue(urlConnection2.isDisconnected());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
    }

    @Test
    public void testFileNameIsNull() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://test.com"), null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://test.com", null);
        MockFileManager fileManager = new MockFileManager();
        downloadCommand.addURLConnection("http://test.com", urlConnection);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromContentDispositionException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://test.com"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://test.com", null);
        downloadCommand.addURLConnection("http://test.com", urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromContentDispositionExceptionNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://test.com"), externalDir.getAbsolutePath(), true);
        setNegativeTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://test.com", null);
        downloadCommand.addURLConnection("http://test.com", urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testFileNameFromURLException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com/test.jpg"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com/test.jpg", null);
        downloadCommand.addURLConnection("http://www.host.com/test.jpg", urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromHostException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", null);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("www_host_com.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromHostExceptionNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setNegativeTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", null);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testValidFileNameFileExists() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        File existingFile = new File(externalDir, "www_host_com.jpg");
        assertTrue(existingFile.createNewFile());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("www_host_com_1985.12.24_01_01_01.jpg", result.fileName());
    }

    @Test
    public void testArbitraryFileNameIsNull() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), "test", true);
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(null);
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testArbitraryFileCreateError() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), "test", true);
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(DocumentFile.fromFile(new File("test")));
        documentManager.setValidFileName("valid_file.txt");
        documentManager.setFileExists(false);
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testNetworkTaskNotValid() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        task.setSchedulerId(databaseTask.getSchedulerId() + 1);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", new BlockingTestInputStream(downloadCommand::isValid));
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertFalse(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testNetworkTaskNotRunning() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        networkTaskDAO.updateNetworkTaskRunning(databaseTask.getId(), false);
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", new BlockingTestInputStream(downloadCommand::isValid));
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertFalse(result.valid());
        assertTrue(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testSuccessNot200() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream, 206);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
    }

    @Test
    public void testSuccessNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testSuccessNonHTTPNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test"));
        urlConnection.setInputStream(inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setContentType("text/plain");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertTrue(result.httpResponseCodes().isEmpty());
        assertTrue(result.httpResponseMessages().isEmpty());
        assertEquals("www_host_com.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        File downloadedFile = new File(externalDir, "www_host_com.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testSuccessDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com/test.jpg"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com/test.jpg", inputStream);
        downloadCommand.addURLConnection("http://www.host.com/test.jpg", urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
    }

    @Test
    public void testArbitraryFileSuccessDeleteInternal() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceDownloadExternalStorage(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com/test.jpg"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com/test.jpg", inputStream);
        downloadCommand.addURLConnection("http://www.host.com/test.jpg", urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
    }

    @Test
    public void testArbitraryFileSuccessDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), "test", true);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        File downloadFile = new File(externalDir, "valid_file.txt");
        downloadCommand.setOutputStream(new FileOutputStream(downloadFile));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(DocumentFile.fromFile(new File("test")));
        documentManager.setValidFileName("valid_file.txt");
        documentManager.setFileExists(true);
        documentManager.setDeleteSuccess(true);
        documentManager.setFile(DocumentFile.fromFile(new File("test")));
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("valid_file.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testSuccessDeleteFailed() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        MockFileManager fileManager = new MockFileManager();
        fileManager.setDownloadFileName("test.txt");
        fileManager.setValidFileName("test.txt");
        fileManager.setDelete(false);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testArbitraryFileSuccessDeleteFailed() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), "test", true);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        File downloadFile = new File(externalDir, "valid_file.txt");
        downloadCommand.setOutputStream(new FileOutputStream(downloadFile));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(DocumentFile.fromFile(new File("test")));
        documentManager.setValidFileName("valid_file.txt");
        documentManager.setFileExists(true);
        documentManager.setDeleteSuccess(false);
        documentManager.setFile(DocumentFile.fromFile(new File("test")));
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("valid_file.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testSuccessDeleteFailedNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true);
        setNegativeTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection("http://www.host.com", inputStream);
        downloadCommand.addURLConnection("http://www.host.com", urlConnection);
        MockFileManager fileManager = new MockFileManager();
        fileManager.setDownloadFileName("test.txt");
        fileManager.setValidFileName("test.txt");
        fileManager.setDelete(false);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    private void setNegativeTime(TestDownloadCommand downloadCommand) {
        MockTimeService timeService = (MockTimeService) downloadCommand.getTimeService();
        timeService.setTimestamp(getTestTimestamp2());
        timeService.setTimestamp2(getTestTimestamp());
    }

    private void setCurrentTime(TestDownloadCommand downloadCommand) {
        MockTimeService timeService = (MockTimeService) downloadCommand.getTimeService();
        timeService.setTimestamp(getTestTimestamp());
        timeService.setTimestamp2(getTestTimestamp2());
    }

    private void setCurrentTimeInverted(TestDownloadCommand downloadCommand) {
        MockTimeService timeService = (MockTimeService) downloadCommand.getTimeService();
        timeService.setTimestamp(getTestTimestamp2());
        timeService.setTimestamp2(getTestTimestamp());
    }

    private long getTestTimestamp() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 900);
        return calendar.getTimeInMillis();
    }

    private long getTestTimestamp2() {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(1);
        task.setAddress("http://testurl");
        task.setPort(80);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        return task;
    }

    private NetworkTask getNetworkTaskWithId(NetworkTask otherTask) {
        NetworkTask task = getNetworkTask();
        task.setSchedulerId(otherTask.getSchedulerId());
        task.setId(otherTask.getId());
        return task;
    }

    @SuppressWarnings({"StringOperationCanBeSimplified"})
    private String getFileContent(File file) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[50];
        int read;
        while ((read = inputStream.read(buffer, 0, 50)) >= 0) {
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
