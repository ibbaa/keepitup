package de.ibba.keepitup.service.network;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.SystemFileManager;
import de.ibba.keepitup.test.mock.MockHttpURLConnection;
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
        fileManager.delete(fileManager.getExternalRootDirectory());
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        fileManager.delete(fileManager.getExternalRootDirectory());
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
        DownloadCommandResult result = downloadCommand.call();
        assertFalse(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertEquals(-1, result.getHttpResponseCode());
        assertNull(result.getHttpResponseMessage());
        assertNull(result.getFileName());
        assertNull(result.getException());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, null, true);
        MockHttpURLConnection urlConnection = prepareHttpURLConnection(HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.setURLConnection(urlConnection);
        DownloadCommandResult result = downloadCommand.call();
        assertTrue(result.isConnectSuccess());
        assertFalse(result.isDownloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.isDeleteSuccess());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, result.getHttpResponseCode());
        assertEquals("not found", result.getHttpResponseMessage());
        assertNull(result.getFileName());
        assertNull(result.getException());
    }

    private NetworkTask getNetworkTask1() {
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
}
