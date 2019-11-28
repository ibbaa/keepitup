package de.ibba.keepitup.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class URLUtilTest {

    @Test
    public void testInputValid() {
        assertTrue(URLUtil.isValidIPAddress("127.0.0.1"));
        assertTrue(URLUtil.isValidIPAddress("123.123.123.123"));
        assertTrue(URLUtil.isValidIPAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf"));
        assertFalse(URLUtil.isValidIPAddress("256.123.123.123"));
        assertFalse(URLUtil.isValidIPAddress("Test"));
        assertTrue(URLUtil.isValidHostName("www.host.com"));
        assertTrue(URLUtil.isValidHostName("Test.co.uk"));
        assertTrue(URLUtil.isValidHostName("Test"));
        assertFalse(URLUtil.isValidHostName("http://www.host.com"));
        assertTrue(URLUtil.isValidURL("http://www.host.com"));
        assertTrue(URLUtil.isValidURL("https://test"));
        assertTrue(URLUtil.isValidURL("https://test/test/test"));
        assertTrue(URLUtil.isValidURL("http://www.host.com/test"));
        assertTrue(URLUtil.isValidURL("http://www.host.com/test?x=1"));
        assertTrue(URLUtil.isValidURL("http://www.host.com/t%20est?x=1"));
        assertFalse(URLUtil.isValidURL("www.host.com"));
        assertFalse(URLUtil.isValidURL("www.ho st.com/t est?x=1"));
        assertFalse(URLUtil.isValidURL("http:/www.host.com"));
        assertFalse(URLUtil.isValidURL("http:///www.host.com"));
        assertFalse(URLUtil.isValidURL("https:/test"));
    }

    @Test
    public void testPrefixHTTPProtocol() {
        assertEquals("http://www.host.com", URLUtil.prefixHTTPProtocol("www.host.com"));
        assertEquals("http://www.host.com", URLUtil.prefixHTTPProtocol("http://www.host.com"));
        assertEquals("https://www.host.com", URLUtil.prefixHTTPProtocol("https://www.host.com"));
    }

    @Test
    public void testEncodeURL() {
        assertEquals("http://www.host.com", URLUtil.encodeURL("http://www.host.com"));
        assertEquals("http://www.host.com/t%20est?x=1", URLUtil.encodeURL("http://www.host.com/t est?x=1"));
        assertEquals("http://test/%E2%80%A5/test", URLUtil.encodeURL("http://test/‥/test"));
        assertEquals("www.ho st.com/t est?x=1", URLUtil.encodeURL("www.ho st.com/t est?x=1"));
        assertEquals("http://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]", URLUtil.encodeURL("http://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]"));
    }

    @Test
    public void testGetURL() {
        URL url = URLUtil.getURL("http://www.host.com", null);
        assertNotNull(url);
        assertEquals("www.host.com", url.getHost());
        assertFalse(url.getPort() > 0);
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertTrue(StringUtil.isEmpty(url.getPath()));
        url = URLUtil.getURL("http://www.host.com:8080/test/url?query", null);
        assertNotNull(url);
        assertEquals("www.host.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("query", url.getQuery());
        assertEquals("/test/url", url.getPath());
        url = URLUtil.getURL("http://www.host.com", "127.0.0.1");
        assertNotNull(url);
        assertEquals("127.0.0.1", url.getHost());
        assertFalse(url.getPort() > 0);
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertTrue(StringUtil.isEmpty(url.getPath()));
        url = URLUtil.getURL("http://www.host.com:8080/test/url?query", "xyz");
        assertNotNull(url);
        assertEquals("xyz", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("query", url.getQuery());
        assertEquals("/test/url", url.getPath());
        url = URLUtil.getURL("http://www.host.com:8080/test", "3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertNotNull(url);
        assertEquals("[3ffe:1900:4545:3:200:f8ff:fe21:67cf]", url.getHost());
        assertEquals(8080, url.getPort());
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertEquals("/test", url.getPath());
        assertNull(URLUtil.getURL("https:/123", "3ffe:1900:4545:3:200:f8ff:fe21:67cf"));
        assertNull(URLUtil.getURL("xyz", null));
    }
}
