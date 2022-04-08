/*
 * Copyright (c) 2022. Alwin Ibba
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

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HTTPUtilTest {

    @Test
    public void testIsHTTPReturnCodeOk() {
        assertTrue(HTTPUtil.isHTTPReturnCodeOk(HttpURLConnection.HTTP_OK));
        assertFalse(HTTPUtil.isHTTPReturnCodeOk(HttpURLConnection.HTTP_GONE));
    }

    @Test
    public void testGetFileNameFromContentDisposition() {
        assertNull(HTTPUtil.getFileNameFromContentDisposition(null));
        assertNull(HTTPUtil.getFileNameFromContentDisposition(""));
        assertNull(HTTPUtil.getFileNameFromContentDisposition("123"));
        assertNull(HTTPUtil.getFileNameFromContentDisposition("filename=\"xyz.jpg\""));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("    attachment; filename=\"xyz.jpg\""));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"xyz.jpg\""));
        assertEquals("xyz.jpg", HTTPUtil.getFileNameFromContentDisposition("attachment; filename=\"abc/xyz.jpg\""));
        assertEquals(" xyz.bin", HTTPUtil.getFileNameFromContentDisposition("attachment;  filename  = \"abc/xyz/ xyz.bin\" "));
        assertEquals("XYZ.jpg", HTTPUtil.getFileNameFromContentDisposition("aTTachment; filename=\"XYZ.jpg\""));
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
