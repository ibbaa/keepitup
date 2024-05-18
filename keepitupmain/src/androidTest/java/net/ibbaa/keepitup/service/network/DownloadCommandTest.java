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

package net.ibbaa.keepitup.service.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.SystemFileManager;
import net.ibbaa.keepitup.test.mock.BlockingTestInputStream;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DownloadCommandTest {

    private NetworkTaskDAO networkTaskDAO;
    private SystemFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
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

    private MockHttpURLConnection prepareHttpURLConnection(int responseCode, String responseMessage, InputStream inputStream) throws Exception {
        MockHttpURLConnection urlConnection = new MockHttpURLConnection(new URL("http://test"));
        urlConnection.setRespondeCode(responseCode);
        urlConnection.setResponseMessage(responseMessage);
        urlConnection.setInputStream(inputStream);
        return urlConnection;
    }

    private MockHttpURLConnection prepareHttpURLConnection(InputStream inputStream) throws Exception {
        return prepareHttpURLConnection(inputStream, HttpURLConnection.HTTP_OK);
    }

    private MockHttpURLConnection prepareHttpURLConnection(InputStream inputStream, int responseCode) throws Exception {
        return prepareHttpURLConnection(responseCode, "Everything ok", inputStream);
    }

    @Test
    public void testConnectionFailed() {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertFalse(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(-1, result.httpResponseCode());
        assertNull(result.httpResponseMessage());
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
    }

    @Test
    public void testConnectionFailedNegativeTime() {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setNegativeTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, result.httpResponseCode());
        assertEquals("not found", result.httpResponseMessage());
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testHTTPResponseCodeNotOkNegativeTime() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setNegativeTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testHTTPResponseCodeNotOkWithLocationHeader() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        urlConnection.addHeader("Location", "new location");
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCode());
        assertEquals("moved Location: new location", result.httpResponseMessage());
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameIsNull() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        MockFileManager fileManager = new MockFileManager();
        downloadCommand.setURLConnection(urlConnection);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromContentDispositionException() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromContentDispositionExceptionNegativeTime() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, externalDir, true);
        setNegativeTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testFileNameFromURLException() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com/test.jpg"), externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromHostException() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("www_host_com.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromHostExceptionNegativeTime() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), externalDir, true);
        setNegativeTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testNetworkTaskNotValid() throws Exception {
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        task.setSchedulerId(databaseTask.getSchedulerId() + 1);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(new BlockingTestInputStream(downloadCommand::isValid));
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertFalse(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testNetworkTaskNotRunning() throws Exception {
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        networkTaskDAO.updateNetworkTaskRunning(databaseTask.getId(), false);
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(new BlockingTestInputStream(downloadCommand::isValid));
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertFalse(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertFalse(result.valid());
        assertTrue(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testSuccessNot200() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream, 206);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
    }

    @Test
    public void testSuccessNotDelete() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
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
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir, false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test"));
        urlConnection.setInputStream(inputStream);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setContentType("text/plain");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(-1, result.httpResponseCode());
        assertNull(result.httpResponseMessage());
        assertEquals("www_host_com.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        File downloadedFile = new File(externalDir, "www_host_com.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testSuccessDelete() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com/test.jpg"), externalDir, true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream);
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.connectSuccess());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("test.jpg", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
    }

    @Test
    public void testSuccessDeleteFailed() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream);
        downloadCommand.setURLConnection(urlConnection);
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
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCode());
        assertEquals("Everything ok", result.httpResponseMessage());
        assertEquals("test.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testSuccessDeleteFailedNegativeTime() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, true);
        setNegativeTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream);
        downloadCommand.setURLConnection(urlConnection);
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
        return new String(outputStream.toByteArray(), Charsets.UTF_8);
    }
}
