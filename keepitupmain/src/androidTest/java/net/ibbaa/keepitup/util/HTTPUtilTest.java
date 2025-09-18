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

package net.ibbaa.keepitup.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockURLConnection;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HTTPUtilTest {

    @Test
    public void testGetLocation() throws Exception {
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test.com"));
        assertNull(HTTPUtil.getLocation(TestRegistry.getContext(), urlConnection));
        urlConnection.addHeader("Location", "123");
        assertEquals("123", HTTPUtil.getLocation(TestRegistry.getContext(), urlConnection));
    }

    @Test
    public void testGetContentDisposition() throws Exception {
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test.com"));
        assertNull(HTTPUtil.getContentDisposition(TestRegistry.getContext(), urlConnection));
        urlConnection.addHeader("Content-Disposition", "123");
        assertEquals("123", HTTPUtil.getContentDisposition(TestRegistry.getContext(), urlConnection));
    }

    @Test
    public void testSetUserAgent() throws Exception {
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test.com"));
        HTTPUtil.setUserAgent(TestRegistry.getContext(), urlConnection);
        assertEquals("Mozilla/5.0", urlConnection.getHeaderField("User-Agent"));
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceHTTPUserAgent("abc");
        HTTPUtil.setUserAgent(TestRegistry.getContext(), urlConnection);
        assertEquals("abc", urlConnection.getHeaderField("User-Agent"));
        preferenceManager.removePreferenceHTTPUserAgent();
        HTTPUtil.setUserAgent(TestRegistry.getContext(), urlConnection);
        assertEquals("Mozilla/5.0", urlConnection.getHeaderField("User-Agent"));
    }

    @Test
    public void testSetAcceptHeader() throws Exception {
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test.com"));
        HTTPUtil.setAcceptHeader(TestRegistry.getContext(), urlConnection);
        assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", urlConnection.getHeaderField("Accept"));
    }

    @Test
    public void testSetAcceptLanguageHeader() throws Exception {
        MockURLConnection urlConnection = new MockURLConnection(new URL("http://test.com"));
        HTTPUtil.setAcceptLanguageHeader(TestRegistry.getContext(), null, urlConnection);
        assertEquals("en-US,en;q=0.9", urlConnection.getHeaderField("Accept-Language"));
        HTTPUtil.setAcceptLanguageHeader(TestRegistry.getContext(), Locale.GERMANY, urlConnection);
        assertEquals("de-DE,de;q=0.9,en;q=0.8", urlConnection.getHeaderField("Accept-Language"));
        HTTPUtil.setAcceptLanguageHeader(TestRegistry.getContext(), Locale.US, urlConnection);
        assertEquals("en-US,en;q=0.9", urlConnection.getHeaderField("Accept-Language"));
    }

    @Test
    public void testIsHTTPReturnCodeRedirect() {
        assertTrue(HTTPUtil.isHTTPReturnCodeRedirect(HttpURLConnection.HTTP_MOVED_PERM));
        assertTrue(HTTPUtil.isHTTPReturnCodeRedirect(HttpURLConnection.HTTP_MOVED_TEMP));
        assertTrue(HTTPUtil.isHTTPReturnCodeRedirect(307));
        assertTrue(HTTPUtil.isHTTPReturnCodeRedirect(308));
        assertFalse(HTTPUtil.isHTTPReturnCodeRedirect(HttpURLConnection.HTTP_OK));
        assertFalse(HTTPUtil.isHTTPReturnCodeRedirect(HttpURLConnection.HTTP_GONE));
    }

    @Test
    public void testIsHTTPReturnCodeOk() {
        assertTrue(HTTPUtil.isHTTPReturnCodeOk(HttpURLConnection.HTTP_OK));
        assertTrue(HTTPUtil.isHTTPReturnCodeOk(206));
        assertFalse(HTTPUtil.isHTTPReturnCodeOk(HttpURLConnection.HTTP_GONE));
    }

    @Test
    public void testGetFileNameFromContentDisposition() {
        assertNull(HTTPUtil.getFileNameFromContentDisposition(null));
        assertNull(HTTPUtil.getFileNameFromContentDisposition("attachment"));
        assertNull(HTTPUtil.getFileNameFromContentDisposition(""));
        assertNull(HTTPUtil.getFileNameFromContentDisposition("123"));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("filename=\"xyz.jpg\""));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("    attachment; filename=\"xyz.jpg\""));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"xyz.jpg\""));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"abc/xyz.jpg\""));
        assertEquals(" xyz.bin", HTTPUtil.getFileNameFromContentDisposition("attachment;  filename  = \"abc/xyz/ xyz.bin\" "));
        assertEquals("XYZ.jpg", HTTPUtil.getFileNameFromContentDisposition("aTTachment; filename=\"XYZ.jpg\""));
        assertEquals("abc.txt", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=abc.txt;"));
        assertEquals("test.pdf", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"test.pdf\""));
        assertEquals("test.pdf", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=test.pdf"));
        assertEquals("test.pdf", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"C:\\temp\\test.pdf\""));
        assertEquals("test.pdf", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"/var/tmp/test.pdf\""));
        assertEquals("€_rates.txt", HTTPUtil.getFileNameFromContentDisposition("attachment; filename*=UTF-8''%E2%82%AC_rates.txt"));
        assertEquals("über uns.txt", HTTPUtil.getFileNameFromContentDisposition("attachment; filename*=UTF-8''%C3%BCber%20uns.txt"));
        assertEquals("über_uns.txt", HTTPUtil.getFileNameFromContentDisposition("attachment; filename*=ISO-8859-1''%FCber_uns.txt"));
        assertEquals("file.pdf", HTTPUtil.getFileNameFromContentDisposition("ATTACHMENT ;   FILENAME = \"file.pdf\""));
        assertEquals("doc.txt", HTTPUtil.getFileNameFromContentDisposition("inline; creation-date=\"today\"; filename=\"doc.txt\""));
    }

    @Test
    public void testGetMimeTypeFromContentType() {
        assertNull(HTTPUtil.getMimeTypeFromContentType(null));
        assertNull(HTTPUtil.getMimeTypeFromContentType(""));
        assertNull(HTTPUtil.getMimeTypeFromContentType("123"));
        assertNull(HTTPUtil.getMimeTypeFromContentType("text/html charset=UTF-8"));
        assertNull(HTTPUtil.getMimeTypeFromContentType(";"));
        assertEquals("text/html", HTTPUtil.getMimeTypeFromContentType("text/html"));
        assertEquals("text/html", HTTPUtil.getMimeTypeFromContentType("text/html; charset=UTF-8"));
        assertEquals("123/456", HTTPUtil.getMimeTypeFromContentType("   123/456   "));
        assertEquals("ABC/456", HTTPUtil.getMimeTypeFromContentType("   ABC/456   "));
        assertEquals("application/zip", HTTPUtil.getMimeTypeFromContentType("application/zip; boundary=something"));
        assertEquals("image/jpg", HTTPUtil.getMimeTypeFromContentType("    image/jpg; boundary=something;"));
    }
}
