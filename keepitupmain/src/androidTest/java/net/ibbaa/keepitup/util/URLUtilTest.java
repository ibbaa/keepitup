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
    }

    @Test
    public void testEncodeURL() {
        assertEquals("http://www.host.com", URLUtil.encodeURL("http://www.host.com"));
        assertEquals("http://www.host.com/t%20est", URLUtil.encodeURL("http://www.host.com/t est"));
        assertEquals("http://www.host.com/%C3%A4%C3%B6%C3%BC", URLUtil.encodeURL("http://www.host.com/äöü"));
        assertEquals("http://www.host.com/t%20est", URLUtil.encodeURL("http://www.host.com/t%20est"));
        assertEquals("http://www.host.com/t%2520est?x=%201", URLUtil.encodeURL("http://www.host.com/t%20est?x= 1"));
        assertEquals("http://www.host.com/t%20est?x=1", URLUtil.encodeURL("http://www.host.com/t est?x=1"));
        assertEquals("http://www.host.com/t%20est?x=1", URLUtil.encodeURL("http://www.host.com/t%20est?x=1"));
        assertEquals("http://www.host.com/test?x=%201", URLUtil.encodeURL("http://www.host.com/test?x= 1"));
        assertEquals("http://www.host.com/test?x=%201", URLUtil.encodeURL("http://www.host.com/test?x=%201"));
        assertEquals("http://test/%E2%80%A5/test", URLUtil.encodeURL("http://test/‥/test"));
        assertEquals("www.ho st.com/t est?x=1", URLUtil.encodeURL("www.ho st.com/t est?x=1"));
        assertEquals("http://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]", URLUtil.encodeURL("http://[3ffe:1900:4545:3:200:f8ff:fe21:67cf]"));
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
    }
}
