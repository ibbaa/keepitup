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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class URLUtilTest {

    @Test
    public void testInputValid() {
        assertTrue(URLUtil.isValidIPAddress("127.0.0.1"));
        assertTrue(URLUtil.isValidIPAddress("123.123.123.123"));
        assertTrue(URLUtil.isValidIPAddress("   123.123.123.123   "));
        assertTrue(URLUtil.isValidIPAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf"));
        assertFalse(URLUtil.isValidIPAddress("256.123.123.123"));
        assertFalse(URLUtil.isValidIPAddress("Test"));
        assertTrue(URLUtil.isValidHostName("www.host.com"));
        assertTrue(URLUtil.isValidHostName("  www.host.com"));
        assertTrue(URLUtil.isValidHostName("Test.co.uk"));
        assertTrue(URLUtil.isValidHostName("Test"));
        assertFalse(URLUtil.isValidHostName("http://www.host.com"));
        assertTrue(URLUtil.isValidURL("http://www.host.com"));
        assertTrue(URLUtil.isValidURL("https://test"));
        assertTrue(URLUtil.isValidURL("https://test/test/test"));
        assertTrue(URLUtil.isValidURL("http://www.host.com/test"));
        assertTrue(URLUtil.isValidURL("http://www.host.com/test?x=1"));
        assertTrue(URLUtil.isValidURL("http://www.host.com/t%20est?x=1"));
        assertTrue(URLUtil.isValidURL("http://ftp://127.0.0.1"));
        assertTrue(URLUtil.isValidURL("http://ftp//127.0.0.1"));
        assertTrue(URLUtil.isValidURL("https://mailto:abc@example.com"));
        assertTrue(URLUtil.isValidURL("http://example.com/path?redirect=ftp://127.0.0.1"));
        assertFalse(URLUtil.isValidURL("www.host.com"));
        assertFalse(URLUtil.isValidURL("www.ho st.com/t est?x=1"));
        assertFalse(URLUtil.isValidURL("http:/www.host.com"));
        assertFalse(URLUtil.isValidURL("http:///www.host.com"));
        assertFalse(URLUtil.isValidURL("https:/test"));
        assertFalse(URLUtil.isValidURL("ftp:/test.org"));
        assertFalse(URLUtil.isValidURL("ftp://test.org"));
        assertFalse(URLUtil.isValidURL("file://abc"));
        assertFalse(URLUtil.isValidURL("ftp://127.0.0.1"));
    }

    @Test
    public void testPrefixHTTPProtocol() {
        assertEquals("https://www.host.com", URLUtil.prefixHTTPProtocol("www.host.com"));
        assertEquals("https://www.host.com", URLUtil.prefixHTTPProtocol("  www.host.com"));
        assertEquals("https://www.host.com", URLUtil.prefixHTTPProtocol("ftp://www.host.com"));
        assertEquals("http://www.host.com", URLUtil.prefixHTTPProtocol("http://www.host.com"));
        assertEquals("https://www.host.com", URLUtil.prefixHTTPProtocol("https://www.host.com"));
    }

    @Test
    public void testIsEncoded() {
        assertFalse(URLUtil.isEncoded("http://www.ho st.com"));
        assertTrue(URLUtil.isEncoded("http://www.host.com"));
        assertFalse(URLUtil.isEncoded("http://www.host.com/t est"));
        assertFalse(URLUtil.isEncoded("http://www.host.com/äöü"));
        assertTrue(URLUtil.isEncoded("http://www.host.com/t%20est"));
        assertFalse(URLUtil.isEncoded("http://www.host.com/t est?x=1"));
        assertTrue(URLUtil.isEncoded("http://www.host.com/t%20est?x=1"));
        assertFalse(URLUtil.isEncoded("http://www.host.com/test?x= 1"));
        assertTrue(URLUtil.isEncoded("http://www.host.com/test?x=%201"));
        assertFalse(URLUtil.isEncoded("http://www.host.com/t%20est?x= 1"));
        assertFalse(URLUtil.isEncoded("x= 1"));
        assertTrue(URLUtil.isEncoded("x=%201"));
        assertTrue(URLUtil.isEncoded("test?x=%201"));
    }

    @Test
    public void testGetHostAndPort() throws MalformedURLException {
        assertEquals("www.host.com", URLUtil.getHostAndPort(new URL("http://www.host.com")));
        assertEquals("www.host.com:8080", URLUtil.getHostAndPort(new URL("http://www.host.com:8080")));
        assertEquals("www.host.com:8080", URLUtil.getHostAndPort(new URL("http://www.host.com:8080/test/url?query")));
        assertEquals("127.0.0.1:0", URLUtil.getHostAndPort(new URL("http://127.0.0.1:0")));
        assertEquals("[3ffe:1900:4545:3:200:f8ff:fe21:67cf]", URLUtil.getHostAndPort(new URL("http://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]")));
        assertEquals("[3ffe:1900:4545:3:200:f8ff:fe21:67cf]:123", URLUtil.getHostAndPort(new URL("http://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]:123")));
    }

    @Test
    public void testGetURL() throws Exception {
        URL url = URLUtil.getURL("http://www.host.com");
        assertNotNull(url);
        assertEquals("http://www.host.com", url.toString());
        assertEquals("www.host.com", url.getHost());
        assertFalse(url.getPort() > 0);
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertTrue(StringUtil.isEmpty(url.getPath()));
        url = URLUtil.getURL("http://www.host.com:8080/test/url?query");
        assertNotNull(url);
        assertEquals("http://www.host.com:8080/test/url?query", url.toString());
        assertEquals("www.host.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("query", url.getQuery());
        assertEquals("/test/url", url.getPath());
        url = URLUtil.getURL(new URL("http://www.host.com"), "http://127.0.0.1");
        assertNotNull(url);
        assertEquals("http://127.0.0.1", url.toString());
        assertEquals("127.0.0.1", url.getHost());
        assertFalse(url.getPort() > 0);
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertTrue(StringUtil.isEmpty(url.getPath()));
        url = URLUtil.getURL(new URL("http://www.host.com:8080/test/url?query"), "xyz");
        assertNotNull(url);
        assertEquals("http://www.host.com:8080/test/xyz", url.toString());
        assertEquals("www.host.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertEquals("/test/xyz", url.getPath());
        url = URLUtil.getURL(new URL("http://www.host.com:8080/test/url"), "/xyz");
        assertNotNull(url);
        assertEquals("http://www.host.com:8080/xyz", url.toString());
        assertEquals("www.host.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertTrue(StringUtil.isEmpty(url.getQuery()));
        assertEquals("/xyz", url.getPath());
        url = URLUtil.getURL(new URL("http://www.host.com:8080/test"), "https://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]");
        assertNotNull(url);
        assertEquals("https://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]", url.toString());
        assertEquals("[3ffe:1900:4545:3:200:f8ff:fe21:67cf]", url.getHost());
        assertFalse(url.getPort() > 0);
        assertTrue(StringUtil.isEmpty(url.getQuery()));
    }

    @Test
    public void testGetURLEncoded() throws Exception {
        URL url = URLUtil.getURL("http://www.host.com/t est?x=1");
        assertEquals("http://www.host.com/t%20est?x=1", url.toString());
        url = URLUtil.getURL("http://www.host.com/t%20est?x=1");
        assertEquals("http://www.host.com/t%20est?x=1", url.toString());
        url = URLUtil.getURL("http://www.host.com/test?x=%201");
        assertEquals("http://www.host.com/test?x=%201", url.toString());
        url = URLUtil.getURL("http://www.host.com/t%20est?x=%201");
        assertEquals("http://www.host.com/t%20est?x=%201", url.toString());
        url = URLUtil.getURL(new URL("http://www.host.com:8080/test/url"), "/xy z");
        assertEquals("http://www.host.com:8080/xy%20z", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("http://www.host.com"), "http://127.0.0.1/b l a");
        assertEquals("http://127.0.0.1/b%20l%20a", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL("http://äöü/b l a");
        assertEquals("http://xn--4ca0bs/b%20l%20a", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("http://www.älg.com"), "http://127.0.0.1/b l a");
        assertEquals("http://127.0.0.1/b%20l%20a", Objects.requireNonNull(url).toString());
    }

    @Test
    public void testAssembleURL() {
        assertEquals("https://www.example.com/test", URLUtil.assembleURL("https", null, "www.example.com", -1, "/test", null, null));
        assertEquals("https://user:pass@host.com:8080/path?a=b&c=d#frag", URLUtil.assembleURL("https", "user:pass", "host.com", 8080, "/path", "a=b&c=d", "frag"));
        assertEquals("http://example.com", URLUtil.assembleURL("http", null, "example.com", -1, null, null, null));
    }

    @Test
    public void testEncodeQuery() {
        assertEquals("token", URLUtil.encodeQuery("token"));
        assertEquals("=value", URLUtil.encodeQuery("=value"));
        assertEquals("a=", URLUtil.encodeQuery("a="));
        assertEquals("a=b&c=d%20e", URLUtil.encodeQuery("a=b&c=d e"));
        assertEquals("a=b%2Bc&x=%C3%A4%C3%B6%C3%BC&y=1%2B1%3D2", URLUtil.encodeQuery("a=b+c&x=äöü&y=1+1=2"));
        assertEquals("a=b%3Dc%3Dd", URLUtil.encodeQuery("a=b=c=d"));
    }

    @Test
    public void testEncodePath() {
        assertEquals("/test/", URLUtil.encodePath("/test/"));
        assertEquals("/", URLUtil.encodePath("/"));
        assertEquals("/", URLUtil.encodePath(""));
        assertEquals("/a%20b/c%2Bd", URLUtil.encodePath("/a b/c+d"));
    }
}
