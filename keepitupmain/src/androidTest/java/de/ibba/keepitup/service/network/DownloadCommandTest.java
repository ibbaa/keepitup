package de.ibba.keepitup.service.network;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.common.base.Charsets;

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

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.test.mock.BlockingTestInputStream;
import de.ibba.keepitup.test.mock.MockFileManager;
import de.ibba.keepitup.test.mock.MockHttpURLConnection;
import de.ibba.keepitup.test.mock.MockTimeService;
import de.ibba.keepitup.test.mock.MockURLConnection;
import de.ibba.keepitup.test.mock.TestDownloadCommand;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DownloadCommandTest {

    private NetworkTaskDAO networkTaskDAO;
    private SystemFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        fileManager = new SystemFileManager(TestRegistry.getContext());
        fileManager.delete(fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName()));
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        fileManager.delete(fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName()));
    }

    private MockHttpURLConnection prepareHttpURLConnection(int responseCode, String responseMessage, InputStream inputStream) throws Exception {
        MockHttpURLConnection urlConnection = new MockHttpURLConnection(new URL("http://test"));
        urlConnection.setRespondeCode(responseCode);
        urlConnection.setResponseMessage(responseMessage);
        urlConnection.setInputStream(inputStream);
        return urlConnection;
    }

    private MockHttpURLConnection prepareHttpURLConnection(InputStream inputStream) throws Exception {
        return prepareHttpURLConnection(HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
    }

    @Test
    public void testConectionFailed() {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertFalse(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(-1, result.getHttpResponseCode());
        assertNull(result.getHttpResponseMessage());
        assertNull(result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, result.getHttpResponseCode());
        assertEquals("not found", result.getHttpResponseMessage());
        assertNull(result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testHTTPResponseCodeNotOkWithLocationHeader() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        urlConnection.addHeader("Location", "new location");
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.getHttpResponseCode());
        assertEquals("moved Location: new location", result.getHttpResponseMessage());
        assertNull(result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
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
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertNull(result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromContentDispositionException() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.jpg", result.getFileName());
        assertEquals(99, result.getDuration());
        assertTrue(result.getException() instanceof IOException);
        assertEquals("Test", result.getException().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromURLException() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com/test.jpg"), externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.jpg", result.getFileName());
        assertEquals(99, result.getDuration());
        assertTrue(result.getException() instanceof IOException);
        assertEquals("Test", result.getException().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testFileNameFromHostException() throws Exception {
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, new URL("http://www.host.com"), externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(null);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setExceptionOnInputStream(new IOException("Test"));
        urlConnection.setContentType("image/jpeg");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("www_host_com.jpg", result.getFileName());
        assertEquals(99, result.getDuration());
        assertTrue(result.getException() instanceof IOException);
        assertEquals("Test", result.getException().getMessage());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testNetworkTaskNotValid() throws Exception {
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        task.setSchedulerId(databaseTask.getSchedulerId() + 1);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(new BlockingTestInputStream(downloadCommand::isValid));
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.isDeleteSuccess());
        assertFalse(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.jpg", result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testNetworkTaskNotRunning() throws Exception {
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        networkTaskDAO.updateNetworkTaskRunning(databaseTask.getId(), false);
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, true);
        setCurrentTime(downloadCommand);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(new BlockingTestInputStream(downloadCommand::isValid));
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.jpg\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.isDeleteSuccess());
        assertFalse(result.isValid());
        assertTrue(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.jpg", result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
    }

    @Test
    public void testSuccessNotDelete() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, externalDir, false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.addHeader("Content-Disposition", "attachment; filename=\"test.txt\"");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertTrue(result.isDownloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.txt", result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testSuccessNonHTTPNotDelete() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com"), externalDir, false);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test"));
        urlConnection.setInputStream(inputStream);
        downloadCommand.setURLConnection(urlConnection);
        urlConnection.setContentType("text/plain");
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertTrue(result.isDownloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(-1, result.getHttpResponseCode());
        assertNull(result.getHttpResponseMessage());
        assertEquals("www_host_com.txt", result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        File downloadedFile = new File(externalDir, "www_host_com.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
    }

    @Test
    public void testSuccessDelete() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, new URL("http://www.host.com/test.jpg"), externalDir, true);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(Charsets.UTF_8));
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(inputStream);
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertTrue(result.isDownloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.jpg", result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
    }

    @Test
    public void testSuccessDeleteFailed() throws Exception {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName());
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
        assertTrue(result.isConnectSuccess());
        assertTrue(result.isDownloadSuccess());
        assertTrue(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertTrue(result.isValid());
        assertFalse(result.isStopped());
        assertEquals(HttpURLConnection.HTTP_OK, result.getHttpResponseCode());
        assertEquals("Everything ok", result.getHttpResponseMessage());
        assertEquals("test.txt", result.getFileName());
        assertEquals(99, result.getDuration());
        assertNull(result.getException());
        assertTrue(urlConnection.isDisconnected());
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
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
        return task;
    }

    private NetworkTask getNetworkTaskWithId(NetworkTask otherTask) {
        NetworkTask task = getNetworkTask();
        task.setSchedulerId(otherTask.getSchedulerId());
        task.setId(otherTask.getId());
        return task;
    }

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
