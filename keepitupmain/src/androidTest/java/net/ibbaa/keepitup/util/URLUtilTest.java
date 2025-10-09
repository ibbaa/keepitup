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
import static org.junit.Assert.assertNull;
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
    public void testIsSameHostAndPort() throws Exception {
        assertFalse(URLUtil.isSameHostAndPort(null, null));
        assertFalse(URLUtil.isSameHostAndPort(null, new URL("https://example.com")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://www.example.com"), new URL("https://www.EXAMPLE.com:443")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://www.example.com"), new URL("https://api.example.com")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://127.0.0.1"), new URL("https://127.0.0.1:443")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://127.0.0.1:443"), new URL("https://127.0.0.1:8443")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://[::1]"), new URL("https://[0:0:0:0:0:0:0:1]:443")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://[::1]"), new URL("https://[::2]")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://localhost"), new URL("https://127.0.0.1")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("http://www.example.com"), new URL("https://www.example.com")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://[::1]"), new URL("https://[::1]:443")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("http://127.0.0.1"), new URL("http://127.0.0.1:80")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://www.example.com"), new URL("https://www.example.com:443")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("http://example.org"), new URL("http://example.org:80")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://EXAMPLE.com"), new URL("https://example.COM")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://www.example.com"), new URL("https://api.example.com")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://example.com"), new URL("https://example.net")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://www.example.com:443"), new URL("https://www.example.com:8443")));
        assertFalse(URLUtil.isSameHostAndPort(new URL("https://127.0.0.1"), new URL("https://127.0.0.2")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("https://[::1]"), new URL("https://[::1]:443")));
        assertTrue(URLUtil.isSameHostAndPort(new URL("http://example.com:80"), new URL("http://example.com")));
    }

    @Test
    public void testNormalizeHost() {
        assertNull(URLUtil.normalizeHost(null));
        assertEquals("", URLUtil.normalizeHost(""));
        assertEquals("a", URLUtil.normalizeHost("a"));
        assertEquals("", URLUtil.normalizeHost("[]"));
        assertEquals("www.host.com", URLUtil.normalizeHost("www.host.com"));
        assertEquals("[www.host.com", URLUtil.normalizeHost("[www.host.com"));
        assertEquals("www.host.com", URLUtil.normalizeHost("[www.host.com]"));
        assertEquals("3ffe:1900:4545:3:200:f8ff:fe21:67cf", URLUtil.normalizeHost("[3ffe:1900:4545:3:200:f8ff:fe21:67cf]"));
    }

    @Test
    public void testIsEncodedPath() {
        assertTrue(URLUtil.isEncoded("", URLUtil.URLComponent.PATH));
        assertTrue(URLUtil.isEncoded("is-ok", URLUtil.URLComponent.PATH));
        assertTrue(URLUtil.isEncoded("file%20name", URLUtil.URLComponent.PATH));
        assertTrue(URLUtil.isEncoded("abc%3Ddef", URLUtil.URLComponent.PATH));
        assertFalse(URLUtil.isEncoded("file name", URLUtil.URLComponent.PATH));
        assertFalse(URLUtil.isEncoded("äpfel", URLUtil.URLComponent.PATH));
        assertFalse(URLUtil.isEncoded("folder=name", URLUtil.URLComponent.PATH));
        assertFalse(URLUtil.isEncoded("path/segment", URLUtil.URLComponent.PATH));
        assertFalse(URLUtil.isEncoded("a+b", URLUtil.URLComponent.PATH));
        assertFalse(URLUtil.isEncoded("abc%2", URLUtil.URLComponent.PATH));
    }

    @Test
    public void testIsEncodedQueryKey() {
        assertTrue(URLUtil.isEncoded("", URLUtil.URLComponent.QUERY_KEY));
        assertTrue(URLUtil.isEncoded("key", URLUtil.URLComponent.QUERY_KEY));
        assertTrue(URLUtil.isEncoded("na%20me", URLUtil.URLComponent.QUERY_KEY));
        assertTrue(URLUtil.isEncoded("user%3Aname", URLUtil.URLComponent.QUERY_KEY));
        assertFalse(URLUtil.isEncoded("user name", URLUtil.URLComponent.QUERY_KEY));
        assertFalse(URLUtil.isEncoded("x=1", URLUtil.URLComponent.QUERY_KEY));
        assertFalse(URLUtil.isEncoded("k&v", URLUtil.URLComponent.QUERY_KEY));
        assertFalse(URLUtil.isEncoded("abc+def", URLUtil.URLComponent.QUERY_KEY));
        assertFalse(URLUtil.isEncoded("äöü", URLUtil.URLComponent.QUERY_KEY));
        assertFalse(URLUtil.isEncoded("wrong%text", URLUtil.URLComponent.QUERY_KEY));
    }

    @Test
    public void testIsEncodedQueryValue() {
        assertTrue(URLUtil.isEncoded("", URLUtil.URLComponent.QUERY_VALUE));
        assertTrue(URLUtil.isEncoded("1", URLUtil.URLComponent.QUERY_VALUE));
        assertTrue(URLUtil.isEncoded("a=b", URLUtil.URLComponent.QUERY_VALUE));
        assertTrue(URLUtil.isEncoded("text%20mit%3Dzeichen", URLUtil.URLComponent.QUERY_VALUE));
        assertFalse(URLUtil.isEncoded("äpfel", URLUtil.URLComponent.QUERY_VALUE));
        assertFalse(URLUtil.isEncoded("text with space", URLUtil.URLComponent.QUERY_VALUE));
        assertFalse(URLUtil.isEncoded("val&ue", URLUtil.URLComponent.QUERY_VALUE));
        assertFalse(URLUtil.isEncoded("abc+def", URLUtil.URLComponent.QUERY_VALUE));
        assertFalse(URLUtil.isEncoded("abc%2", URLUtil.URLComponent.QUERY_VALUE));
    }

    @Test
    public void testIsEncodedFragment() {
        assertTrue(URLUtil.isEncoded("section1", URLUtil.URLComponent.FRAGMENT));
        assertTrue(URLUtil.isEncoded("%C3%BCbersicht%20kapitel", URLUtil.URLComponent.FRAGMENT));
        assertTrue(URLUtil.isEncoded("anchor%3Dvalue%26another%3D2", URLUtil.URLComponent.FRAGMENT));
        assertTrue(URLUtil.isEncoded("x%3Dy%26z", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("übersicht%20kapitel", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("section 1", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("kapitel#2", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("abc+def", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("äöü", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("abc%2", URLUtil.URLComponent.FRAGMENT));
        assertFalse(URLUtil.isEncoded("title<name>", URLUtil.URLComponent.FRAGMENT));
    }

    @Test
    public void testIsEncodedUserInfo() {
        assertTrue(URLUtil.isEncoded("user", URLUtil.URLComponent.USER_INFO));
        assertTrue(URLUtil.isEncoded("user:pass", URLUtil.URLComponent.USER_INFO));
        assertTrue(URLUtil.isEncoded("user%40example.com", URLUtil.URLComponent.USER_INFO));
        assertTrue(URLUtil.isEncoded("john.doe%2Badmin", URLUtil.URLComponent.USER_INFO));
        assertTrue(URLUtil.isEncoded("us%24er%21", URLUtil.URLComponent.USER_INFO));
        assertFalse(URLUtil.isEncoded("user name", URLUtil.URLComponent.USER_INFO));
        assertFalse(URLUtil.isEncoded("pässword", URLUtil.URLComponent.USER_INFO));
        assertFalse(URLUtil.isEncoded("admin@example.com", URLUtil.URLComponent.USER_INFO));
        assertFalse(URLUtil.isEncoded("abc%2", URLUtil.URLComponent.USER_INFO));
        assertFalse(URLUtil.isEncoded("abc+def", URLUtil.URLComponent.USER_INFO));
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
        assertEquals("http://www.host.com:8080/test/url?query=", url.toString());
        assertEquals("www.host.com", url.getHost());
        assertEquals(8080, url.getPort());
        assertEquals("query=", url.getQuery());
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
        assertNull(URLUtil.getURL(null, "ftp://www.host.com/"));
        assertNull(URLUtil.getURL(null, "/only-path"));
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
        url = URLUtil.getURL("http://my:password@www.host.com");
        assertEquals("http://my:password@www.host.com", url.toString());
        url = URLUtil.getURL("http://my:pässword@www.host.com");
        assertEquals("http://my:p%C3%A4ssword@www.host.com", url.toString());
        url = URLUtil.getURL(new URL("http://www.älg.com"), "http://127.0.0.1/b l a");
        assertEquals("http://127.0.0.1/b%20l%20a", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://example.com/path/"), "https://www.test.de/index.html");
        assertEquals("https://www.test.de/index.html", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "sub/page.html");
        assertEquals("https://www.host.com/path/sub/page.html", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "file name.txt");
        assertEquals("https://www.host.com/path/file%20name.txt", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "äöü.html");
        assertEquals("https://www.host.com/path/%C3%A4%C3%B6%C3%BC.html", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "?q=test value&x=ü");
        assertEquals("https://www.host.com/path/?q=test%20value&x=%C3%BC", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "?a=1&b=%20");
        assertEquals("https://www.host.com/path/?a=1&b=%20", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "page.html#top");
        assertEquals("https://www.host.com/path/page.html#top", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "page.html#to p");
        assertEquals("https://www.host.com/path/page.html#to%20p", Objects.requireNonNull(url).toString());
        url = URLUtil.getURL(new URL("https://www.host.com/path/"), "#section2");
        assertEquals("https://www.host.com/path/#section2", Objects.requireNonNull(url).toString());
    }

    @Test
    public void testGetEncodedQuery() {
        assertEquals("=", URLUtil.getEncodedQuery("="));
        assertEquals("", URLUtil.getEncodedQuery(""));
        assertEquals("a=1&b=2", URLUtil.getEncodedQuery("a=1&b=2"));
        assertEquals("key=value%201", URLUtil.getEncodedQuery("key=value 1"));
        assertEquals("x=%C3%A4", URLUtil.getEncodedQuery("x=ä"));
        assertEquals("x=%20", URLUtil.getEncodedQuery("x=%20"));
        assertEquals("onlykey=", URLUtil.getEncodedQuery("onlykey"));
        assertEquals("k1=&k2=", URLUtil.getEncodedQuery("k1=&k2="));
        assertEquals("first%20name=John", URLUtil.getEncodedQuery("first name=John"));
        assertEquals("a=b=c", URLUtil.getEncodedQuery("a=b=c"));
        assertEquals("x=%C3%B6", URLUtil.getEncodedQuery("x=ö"));
        assertEquals("key=%2520", URLUtil.getEncodedQuery("key=%2520"));
        assertEquals("=value", URLUtil.getEncodedQuery("=value"));
        assertEquals("a=1&b=%C3%BC&c=%20", URLUtil.getEncodedQuery("a=1&b=ü&c= "));
    }

    @Test
    public void testGetEncodedPath() {
        assertEquals("", URLUtil.getEncodedPath(""));
        assertEquals("test/other/test2", URLUtil.getEncodedPath("test/other/test2"));
        assertEquals("test/other%20name/file.txt", URLUtil.getEncodedPath("test/other name/file.txt"));
        assertEquals("%C3%A4%C3%B6%C3%BC/datei", URLUtil.getEncodedPath("äöü/datei"));
        assertEquals("test/%20/other", URLUtil.getEncodedPath("test/%20/other"));
        assertEquals("/api//v1", URLUtil.getEncodedPath("/api//v1"));
    }

    @Test
    public void testGetEncodedUserInfo() {
        assertEquals("", URLUtil.getEncodedUserInfo(""));
        assertEquals("user", URLUtil.getEncodedUserInfo("user"));
        assertEquals("user%20name", URLUtil.getEncodedUserInfo("user name"));
        assertEquals("user:pass", URLUtil.getEncodedUserInfo("user:pass"));
        assertEquals("user%23hash", URLUtil.getEncodedUserInfo("user#hash"));
        assertEquals("%C3%BCser", URLUtil.getEncodedUserInfo("üser"));
        assertEquals("user%20name:p%C3%A4ssword", URLUtil.getEncodedUserInfo("user name:pässword"));
        assertEquals("user%20name:p%C3%A4ssword", URLUtil.getEncodedUserInfo("user%20name:p%C3%A4ssword"));
        assertEquals("user!name'()~:pass", URLUtil.getEncodedUserInfo("user!name'()~:pass"));
    }

    @Test
    public void testGetEncodedRef() {
        assertEquals("", URLUtil.getEncodedRef(""));
        assertEquals("simpleRef", URLUtil.getEncodedRef("simpleRef"));
        assertEquals("space%20here", URLUtil.getEncodedRef("space here"));
        assertEquals("slash/test", URLUtil.getEncodedRef("slash/test"));
        assertEquals("%C3%BCnicode", URLUtil.getEncodedRef("ünicode"));
        assertEquals("special%23chars%3F", URLUtil.getEncodedRef("special#chars?"));
        assertEquals("%C3%BCbersicht%20kapitel!%20~%20test", URLUtil.getEncodedRef("übersicht kapitel! ~ test"));
        assertEquals("%C3%BCbersicht%20kapitel!%20~%20test", URLUtil.getEncodedRef("%C3%BCbersicht%20kapitel!%20~%20test"));
        assertEquals("chapter%231%26section%3D2", URLUtil.getEncodedRef("chapter#1&section=2"));
    }

    @Test
    public void testAssembleURL() {
        assertEquals("https://www.example.com/test", URLUtil.assembleURL("https", null, "www.example.com", -1, "/test", null, null));
        assertEquals("https://user:pass@host.com:8080/path?a=b&c=d#frag", URLUtil.assembleURL("https", "user:pass", "host.com", 8080, "/path", "a=b&c=d", "frag"));
        assertEquals("http://example.com", URLUtil.assembleURL("http", null, "example.com", -1, null, null, null));
    }
}
