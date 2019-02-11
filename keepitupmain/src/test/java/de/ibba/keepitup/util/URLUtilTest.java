package de.ibba.keepitup.util;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class URLUtilTest {

    @Before
    public void beforeEachTestMethod() {
        PowerMockito.mockStatic(Log.class);
    }

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
        assertFalse(URLUtil.isValidURL("www.ho st.com/t est?x=1"));
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
    }
}
