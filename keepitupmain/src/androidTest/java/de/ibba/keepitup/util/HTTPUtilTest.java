package de.ibba.keepitup.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HTTPUtilTest {

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
