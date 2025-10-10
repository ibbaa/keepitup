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
import net.ibbaa.keepitup.test.mock.ExceptionResponseBody;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestDownloadCommand;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestResponseBody;

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

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
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

    private Response prepareResponse(String url, int code, String message, InputStream bodyStream) {
        ResponseBody body = new TestResponseBody(bodyStream);
        return new Response.Builder().request(new Request.Builder().url(url).build()).protocol(Protocol.HTTP_1_1).code(code).message(message != null ? message : "").body(body).build();
    }

    @Test
    public void testConnectionFailed() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("https://127.0.0.1"), null, true, null);
        setCurrentTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertFalse(result.connectResults().get(0).success());
        assertEquals("127.0.0.1", result.connectResults().get(0).host());
        assertEquals(443, result.connectResults().get(0).port());
        assertFalse(result.connectResults().get(0).success());
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
    public void testConnectionFailedWithRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null).newBuilder().header("Location", "http://www.host.com").build();
        downloadCommand.addResponse("http://test.com", testResponse);
        setCurrentTimeInverted(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(2, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertFalse(result.connectResults().get(1).success());
        assertEquals("www.host.com", result.connectResults().get(1).host());
        assertEquals(80, result.connectResults().get(1).port());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals("moved Location: http://www.host.com", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        testResponse.close();
    }

    @Test
    public void testConnectionFailedNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        setNegativeTime(downloadCommand);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
    }

    @Test
    public void testHTTPResponseCodeNotOk() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com:333"), null, true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("http://test.com:333", HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.addResponse("http://test.com:333", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(333, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testHTTPResponseCodeNotOkNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        setNegativeTime(downloadCommand);
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_NOT_FOUND, "not found", null);
        downloadCommand.addResponse("http://test.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
        testResponse.close();
    }

    @Test
    public void testHTTPResponseCodeNotOkWithLocationHeaderWithoutRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("https://test.com"), null, true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("https://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        testResponse = testResponse.newBuilder().header("Location", "new location").build();
        downloadCommand.addResponse("https://test.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(443, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testHTTPResponseCodeNotOkRedirectInvalidLocationHeader() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        testResponse = testResponse.newBuilder().header("Location", "ccc://xyz").build();
        downloadCommand.addResponse("http://test.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals("moved Location: ccc://xyz (invalid)", result.httpResponseMessages().get(0));
        assertNull(result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        testResponse.close();
    }

    @Test
    public void testHTTPResponseCodeNotOkRedirectsExceeded() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        setCurrentTime(downloadCommand);
        Response response1 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response1 = response1.newBuilder().header("Location", "http://test1.com").build();
        Response response2 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response2 = response2.newBuilder().header("Location", "http://test2.com").build();
        Response response3 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response3 = response3.newBuilder().header("Location", "http://test3.com").build();
        Response response4 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response4 = response4.newBuilder().header("Location", "http://test4.com").build();
        Response response5 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response5 = response5.newBuilder().header("Location", "http://test5.com").build();
        Response response6 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response6 = response6.newBuilder().header("Location", "http://test6.com").build();
        Response response7 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response7 = response7.newBuilder().header("Location", "http://test7.com").build();
        Response response8 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response8 = response8.newBuilder().header("Location", "http://test8.com").build();
        Response response9 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response9 = response9.newBuilder().header("Location", "http://test9.com").build();
        Response response10 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response10 = response10.newBuilder().header("Location", "http://test10.com").build();
        downloadCommand.addResponse("http://test.com", response1);
        downloadCommand.addResponse("http://test1.com", response2);
        downloadCommand.addResponse("http://test2.com", response3);
        downloadCommand.addResponse("http://test3.com", response4);
        downloadCommand.addResponse("http://test4.com", response5);
        downloadCommand.addResponse("http://test5.com", response6);
        downloadCommand.addResponse("http://test6.com", response7);
        downloadCommand.addResponse("http://test7.com", response8);
        downloadCommand.addResponse("http://test8.com", response9);
        downloadCommand.addResponse("http://test9.com", response10);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(10, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.connectResults().get(1).success());
        assertEquals("test1.com", result.connectResults().get(1).host());
        assertEquals(80, result.connectResults().get(1).port());
        assertTrue(result.connectResults().get(2).success());
        assertEquals("test2.com", result.connectResults().get(2).host());
        assertEquals(80, result.connectResults().get(2).port());
        assertTrue(result.connectResults().get(3).success());
        assertEquals("test3.com", result.connectResults().get(3).host());
        assertEquals(80, result.connectResults().get(3).port());
        assertTrue(result.connectResults().get(4).success());
        assertEquals("test4.com", result.connectResults().get(4).host());
        assertEquals(80, result.connectResults().get(4).port());
        assertTrue(result.connectResults().get(5).success());
        assertEquals("test5.com", result.connectResults().get(5).host());
        assertEquals(80, result.connectResults().get(5).port());
        assertTrue(result.connectResults().get(6).success());
        assertEquals("test6.com", result.connectResults().get(6).host());
        assertEquals(80, result.connectResults().get(6).port());
        assertTrue(result.connectResults().get(7).success());
        assertEquals("test7.com", result.connectResults().get(7).host());
        assertEquals(80, result.connectResults().get(7).port());
        assertTrue(result.connectResults().get(8).success());
        assertEquals("test8.com", result.connectResults().get(8).host());
        assertEquals(80, result.connectResults().get(8).port());
        assertTrue(result.connectResults().get(9).success());
        assertEquals("test9.com", result.connectResults().get(9).host());
        assertEquals(80, result.connectResults().get(9).port());
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(10, result.httpResponseCodes().size());
        assertEquals(10, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals("moved Location: http://test1.com", result.httpResponseMessages().get(0));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(1).intValue());
        assertEquals("moved Location: http://test2.com", result.httpResponseMessages().get(1));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(2).intValue());
        assertEquals("moved Location: http://test3.com", result.httpResponseMessages().get(2));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(3).intValue());
        assertEquals("moved Location: http://test4.com", result.httpResponseMessages().get(3));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(4).intValue());
        assertEquals("moved Location: http://test5.com", result.httpResponseMessages().get(4));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(5).intValue());
        assertEquals("moved Location: http://test6.com", result.httpResponseMessages().get(5));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(6).intValue());
        assertEquals("moved Location: http://test7.com", result.httpResponseMessages().get(6));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(7).intValue());
        assertEquals("moved Location: http://test8.com", result.httpResponseMessages().get(7));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(8).intValue());
        assertEquals("moved Location: http://test9.com", result.httpResponseMessages().get(8));
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(9).intValue());
        assertEquals("moved Location: http://test10.com (max number of redirects exceeded)", result.httpResponseMessages().get(9));
        assertNull(result.fileName());
        assertNull(result.exception());
        response1.close();
        response2.close();
        response3.close();
        response4.close();
        response5.close();
        response6.close();
        response7.close();
        response8.close();
        response9.close();
        response10.close();
    }

    @Test
    public void testHTTPResponseCodeNotOkRedirectsExceededRecursive() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        testResponse = testResponse.newBuilder().header("Location", "http://test.com").build();
        downloadCommand.addResponse("http://test.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(10, result.connectResults().size());
        for (int ii = 0; ii < result.httpResponseCodes().size() - 1; ii++) {
            assertTrue(result.connectResults().get(ii).success());
            assertEquals("test.com", result.connectResults().get(ii).host());
            assertEquals(80, result.connectResults().get(ii).port());
        }
        assertFalse(result.downloadSuccess());
        assertFalse(result.fileExists());
        assertFalse(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(10, result.httpResponseCodes().size());
        assertEquals(10, result.httpResponseMessages().size());
        for (int ii = 0; ii < result.httpResponseCodes().size() - 1; ii++) {
            assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(ii).intValue());
            assertEquals("moved Location: http://test.com", result.httpResponseMessages().get(ii));
        }
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(9).intValue());
        assertEquals("moved Location: http://test.com (max number of redirects exceeded)", result.httpResponseMessages().get(9));
        assertNull(result.fileName());
        assertNull(result.exception());
        testResponse.close();
    }

    @Test
    public void testFileNameIsNull() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), null, true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_OK, "Everything ok", null);
        MockFileManager fileManager = new MockFileManager();
        downloadCommand.addResponse("http://test.com", testResponse);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testFileNameFromContentDispositionException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ResponseBody body = new ExceptionResponseBody();
        Response testResponse = new Response.Builder().request(new Request.Builder().url("http://test.com").build()).protocol(Protocol.HTTP_1_1).code(HttpURLConnection.HTTP_OK).message("Everything ok").header("Content-Disposition", "attachment; filename=\"test.jpg\"").body(body).build();
        downloadCommand.addResponse("http://test.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertFalse(result.downloadSuccess());
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
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        testResponse.close();
    }

    @Test
    public void testFileNameFromContentDispositionExceptionNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://test.com"), externalDir.getAbsolutePath(), true, null);
        setNegativeTime(downloadCommand);
        ResponseBody body = new ExceptionResponseBody();
        Response testResponse = new Response.Builder().request(new Request.Builder().url("http://test.com").build()).protocol(Protocol.HTTP_1_1).code(HttpURLConnection.HTTP_OK).message("Everything ok").header("Content-Disposition", "attachment; filename=\"test.jpg\"").body(body).build();
        downloadCommand.addResponse("http://test.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
        testResponse.close();
    }

    @Test
    public void testFileNameFromURLException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("https://www.host.com/test.jpg"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ResponseBody body = new ExceptionResponseBody();
        Response testResponse = new Response.Builder().request(new Request.Builder().url("https://www.host.com/test.jpg").build()).protocol(Protocol.HTTP_1_1).code(HttpURLConnection.HTTP_OK).message("Everything ok").header("Content-Disposition", "attachment; filename=\"test.jpg\"").body(body).build();
        downloadCommand.addResponse("https://www.host.com/test.jpg", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(443, result.connectResults().get(0).port());
        assertFalse(result.downloadSuccess());
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
        assertTrue(result.exception() instanceof IOException);
        assertEquals("Test", result.exception().getMessage());
        testResponse.close();
    }

    @Test
    public void testFileNameFromHostException() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com:1234"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ResponseBody body = new ExceptionResponseBody();
        Response testResponse = new Response.Builder().request(new Request.Builder().url("http://www.host.com").build()).protocol(Protocol.HTTP_1_1).code(HttpURLConnection.HTTP_OK).message("Everything ok").header("Content-Type", "image/jpeg").body(body).build();
        downloadCommand.addResponse("http://www.host.com:1234", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(1234, result.connectResults().get(0).port());
        assertFalse(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
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
        testResponse.close();
    }

    @Test
    public void testFileNameFromHostExceptionNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true, null);
        setNegativeTime(downloadCommand);
        ResponseBody body = new ExceptionResponseBody();
        Response testResponse = new Response.Builder().request(new Request.Builder().url("http://www.host.com").build()).protocol(Protocol.HTTP_1_1).code(HttpURLConnection.HTTP_OK).message("Everything ok").header("Content-Type", "image/jpeg").body(body).build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
        testResponse.close();
    }

    @Test
    public void testValidFileNameFileExists() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        File existingFile = new File(externalDir, "www_host_com.jpg");
        assertTrue(existingFile.createNewFile());
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Type", "image/jpeg").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testArbitraryFileNameIsNull() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com"), "test", true, null);
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(null);
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Type", "image/jpeg").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testArbitraryFileCreateError() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com"), "test", true, null);
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(DocumentFile.fromFile(new File("test")));
        documentManager.setValidFileName("valid_file.txt");
        documentManager.setFileExists(false);
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://test.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Type", "image/jpeg").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testNetworkTaskNotValid() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        task.setSchedulerId(databaseTask.getSchedulerId() + 1);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", new BlockingTestInputStream(downloadCommand::isValid));
        testResponse = testResponse.newBuilder().header("Content-Disposition", "attachment; filename=\"test.jpg\"").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testNetworkTaskNotRunning() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask databaseTask = networkTaskDAO.insertNetworkTask(getNetworkTask());
        networkTaskDAO.updateNetworkTaskRunning(databaseTask.getId(), false);
        NetworkTask task = getNetworkTaskWithId(databaseTask);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", new BlockingTestInputStream(downloadCommand::isValid));
        testResponse = testResponse.newBuilder().header("Content-Disposition", "attachment; filename=\"test.jpg\"").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testSuccessNot200() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), false, null);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com", 206, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Disposition", "attachment; filename=\"test.jpg\"").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.downloadSuccess());
        testResponse.close();
    }

    @Test
    public void testSuccessNotDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), false, null);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Disposition", "attachment; filename=\"test.txt\"").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
        testResponse.close();
    }

    @Test
    public void testSuccessDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com/test.jpg"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Type", "text/plain").build();
        downloadCommand.addResponse("http://www.host.com/test.jpg", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
        testResponse.close();
    }

    @Test
    public void testArbitraryFileSuccessDeleteInternal() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceDownloadExternalStorage(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com/test.jpg"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com/test.jpg", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("http://www.host.com/test.jpg", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
        testResponse.close();
    }

    @Test
    public void testArbitraryFileSuccessDelete() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com"), "test", true, null);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        File downloadFile = new File(externalDir, "valid_file.txt");
        downloadCommand.setOutputStream(new FileOutputStream(downloadFile));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(DocumentFile.fromFile(new File("test")));
        documentManager.setValidFileName("valid_file.txt");
        documentManager.setFileExists(true);
        documentManager.setDeleteSuccess(true);
        documentManager.setFile(DocumentFile.fromFile(new File("test.txt")));
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Type", "image/jpeg").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(1, result.httpResponseCodes().size());
        assertEquals(1, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(0).intValue());
        assertEquals("Everything ok", result.httpResponseMessages().get(0));
        assertEquals("test.txt", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        testResponse.close();
    }

    @Test
    public void testSuccessDeleteFailed() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("https://www.host.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("https://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("https://www.host.com", testResponse);
        MockFileManager fileManager = new MockFileManager();
        fileManager.setDownloadFileName("test.txt");
        fileManager.setValidFileName("test.txt");
        fileManager.setDelete(false);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(443, result.connectResults().get(0).port());
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
        File downloadedFile = new File(externalDir, "test.txt");
        assertTrue(downloadedFile.exists());
        assertEquals("TestData", getFileContent(downloadedFile));
        testResponse.close();
    }

    @Test
    public void testArbitraryFileSuccessDeleteFailed() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceArbitraryDownloadFolder("test");
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), null, null, new URL("http://www.host.com"), "test", true, null);
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        File downloadFile = new File(externalDir, "valid_file.txt");
        downloadCommand.setOutputStream(new FileOutputStream(downloadFile));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFolder(DocumentFile.fromFile(new File("test")));
        documentManager.setValidFileName("valid_file.txt");
        documentManager.setFileExists(true);
        documentManager.setDeleteSuccess(false);
        documentManager.setFile(DocumentFile.fromFile(new File("test.txt")));
        downloadCommand.setDocumentManager(documentManager);
        setCurrentTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        testResponse = testResponse.newBuilder().header("Content-Type", "image/jpeg").build();
        downloadCommand.addResponse("http://www.host.com", testResponse);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(1, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("www.host.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
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
        testResponse.close();
    }

    @Test
    public void testSuccessDeleteFailedNegativeTime() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://www.host.com"), externalDir.getAbsolutePath(), true, null);
        setNegativeTime(downloadCommand);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response testResponse = prepareResponse("http://www.host.com", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("http://www.host.com", testResponse);
        MockFileManager fileManager = new MockFileManager();
        fileManager.setDownloadFileName("test.txt");
        fileManager.setValidFileName("test.txt");
        fileManager.setDelete(false);
        downloadCommand.setFileManager(fileManager);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(0, result.duration());
        testResponse.close();
    }

    @Test
    public void testSuccessWithOneRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://test.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTimeInverted(downloadCommand);
        Response response1 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response1 = response1.newBuilder().header("Location", "http://www.host.com/test.jpg").build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response response2 = prepareResponse("http://www.host.com/test.jpg", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("http://test.com", response1);
        downloadCommand.addResponse("http://www.host.com/test.jpg", response2);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(2, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.connectResults().get(1).success());
        assertEquals("www.host.com", result.connectResults().get(1).host());
        assertEquals(80, result.connectResults().get(1).port());
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
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
        response1.close();
        response2.close();
    }

    @Test
    public void testSuccessWithOneRelativeRedirect() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://test.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTimeInverted(downloadCommand);
        Response response1 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response1 = response1.newBuilder().header("Location", "/abc").build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response response2 = prepareResponse("http://test.com/abc", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("http://test.com", response1);
        downloadCommand.addResponse("http://test.com/abc", response2);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(2, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.connectResults().get(1).success());
        assertEquals("test.com", result.connectResults().get(1).host());
        assertEquals(80, result.connectResults().get(1).port());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(2, result.httpResponseCodes().size());
        assertEquals(2, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(1).intValue());
        assertEquals("moved Location: /abc", result.httpResponseMessages().get(0));
        assertEquals("Everything ok", result.httpResponseMessages().get(1));
        assertEquals("abc", result.fileName());
        assertEquals(99, result.duration());
        assertNull(result.exception());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
        response1.close();
        response2.close();
    }

    @Test
    public void testSuccessWithTwoRedirects() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://test.com"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        Response response1 = prepareResponse("http://test.com", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response1 = response1.newBuilder().header("Location", "http://test2.com").build();
        Response response2 = prepareResponse("http://test2.com", HttpURLConnection.HTTP_MOVED_TEMP, "moved2", null);
        response2 = response2.newBuilder().header("Location", "http://www.host.com/test.jpg").build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response response3 = prepareResponse("http://www.host.com/test.jpg", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("http://test.com", response1);
        downloadCommand.addResponse("http://test2.com", response2);
        downloadCommand.addResponse("http://www.host.com/test.jpg", response3);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(3, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.connectResults().get(1).success());
        assertEquals("test2.com", result.connectResults().get(1).host());
        assertEquals(80, result.connectResults().get(1).port());
        assertTrue(result.connectResults().get(2).success());
        assertEquals("www.host.com", result.connectResults().get(2).host());
        assertEquals(80, result.connectResults().get(2).port());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(3, result.httpResponseCodes().size());
        assertEquals(3, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals(HttpURLConnection.HTTP_MOVED_TEMP, result.httpResponseCodes().get(1).intValue());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(2).intValue());
        assertEquals("moved Location: http://test2.com", result.httpResponseMessages().get(0));
        assertEquals("moved2 Location: http://www.host.com/test.jpg", result.httpResponseMessages().get(1));
        assertEquals("Everything ok", result.httpResponseMessages().get(2));
        assertEquals("test.jpg", result.fileName());
        assertNull(result.exception());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
        response1.close();
        response2.close();
        response3.close();
    }

    @Test
    public void testSuccessWithTwoRelativeRedirects() throws Exception {
        preferenceManager.setPreferenceDownloadFollowsRedirects(true);
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        File externalDir = fileManager.getExternalDirectory(fileManager.getDefaultDownloadDirectoryName(), 0);
        TestDownloadCommand downloadCommand = new TestDownloadCommand(TestRegistry.getContext(), task, null, new URL("http://test.com/abc/xyz"), externalDir.getAbsolutePath(), true, null);
        setCurrentTime(downloadCommand);
        Response response1 = prepareResponse("http://test.com/abc/xyz", HttpURLConnection.HTTP_MOVED_PERM, "moved", null);
        response1 = response1.newBuilder().header("Location", "abc").build();
        Response response2 = prepareResponse("http://test.com/abc/abc", HttpURLConnection.HTTP_MOVED_TEMP, "moved2", null);
        response2 = response2.newBuilder().header("Location", "/abc").build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("TestData".getBytes(StandardCharsets.UTF_8));
        Response response3 = prepareResponse("http://test.com/abc", HttpURLConnection.HTTP_OK, "Everything ok", inputStream);
        downloadCommand.addResponse("http://test.com/abc/xyz", response1);
        downloadCommand.addResponse("http://test.com/abc/abc", response2);
        downloadCommand.addResponse("http://test.com/abc", response3);
        DownloadCommandResult result = downloadCommand.call();
        assertEquals(3, result.connectResults().size());
        assertTrue(result.connectResults().get(0).success());
        assertEquals("test.com", result.connectResults().get(0).host());
        assertEquals(80, result.connectResults().get(0).port());
        assertTrue(result.connectResults().get(1).success());
        assertEquals("test.com", result.connectResults().get(1).host());
        assertEquals(80, result.connectResults().get(1).port());
        assertTrue(result.connectResults().get(2).success());
        assertEquals("test.com", result.connectResults().get(2).host());
        assertEquals(80, result.connectResults().get(2).port());
        assertTrue(result.downloadSuccess());
        assertTrue(result.fileExists());
        assertTrue(result.deleteSuccess());
        assertTrue(result.valid());
        assertFalse(result.stopped());
        assertEquals(3, result.httpResponseCodes().size());
        assertEquals(3, result.httpResponseMessages().size());
        assertEquals(HttpURLConnection.HTTP_MOVED_PERM, result.httpResponseCodes().get(0).intValue());
        assertEquals(HttpURLConnection.HTTP_MOVED_TEMP, result.httpResponseCodes().get(1).intValue());
        assertEquals(HttpURLConnection.HTTP_OK, result.httpResponseCodes().get(2).intValue());
        assertEquals("moved Location: abc", result.httpResponseMessages().get(0));
        assertEquals("moved2 Location: /abc", result.httpResponseMessages().get(1));
        assertEquals("Everything ok", result.httpResponseMessages().get(2));
        assertEquals("abc", result.fileName());
        assertNull(result.exception());
        File downloadedFile = new File(externalDir, "test.jpg");
        assertFalse(downloadedFile.exists());
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
        task.setName("name");
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
        task.setNotification(true);
        task.setHighPrio(true);
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
