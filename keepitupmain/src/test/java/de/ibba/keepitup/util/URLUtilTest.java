package de.ibba.keepitup.util;

import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class URLUtilTest {

    @Test
    public void testInputValid() {
        PowerMockito.mockStatic(Log.class);
        Assert.assertTrue(URLUtil.isValidIPAddress("127.0.0.1"));
        Assert.assertTrue(URLUtil.isValidIPAddress("123.123.123.123"));
        Assert.assertTrue(URLUtil.isValidIPAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf"));
        Assert.assertFalse(URLUtil.isValidIPAddress("256.123.123.123"));
        Assert.assertFalse(URLUtil.isValidIPAddress("Test"));
        Assert.assertTrue(URLUtil.isValidHostName("www.host.com"));
        Assert.assertTrue(URLUtil.isValidHostName("Test.co.uk"));
        Assert.assertTrue(URLUtil.isValidHostName("Test"));
        Assert.assertFalse(URLUtil.isValidHostName("http://www.host.com"));
        Assert.assertTrue(URLUtil.isValidURL("http://www.host.com"));
        Assert.assertTrue(URLUtil.isValidURL("https://test"));
        Assert.assertTrue(URLUtil.isValidURL("https://test/test/test"));
        Assert.assertTrue(URLUtil.isValidURL("http://www.host.com/test"));
        Assert.assertTrue(URLUtil.isValidURL("http://www.host.com/test?x=1"));
        Assert.assertTrue(URLUtil.isValidURL("http://www.host.com/t%20est?x=1"));
        Assert.assertFalse(URLUtil.isValidURL("www.ho st.com/t est?x=1"));
    }

    @Test
    public void testPrefixHTTPProtocol() {
        Assert.assertEquals("http://www.host.com", URLUtil.prefixHTTPProtocol("www.host.com"));
        Assert.assertEquals("http://www.host.com", URLUtil.prefixHTTPProtocol("http://www.host.com"));
        Assert.assertEquals("https://www.host.com", URLUtil.prefixHTTPProtocol("https://www.host.com"));
    }

    @Test
    public void testEncodeURL() {
        PowerMockito.mockStatic(Log.class);
        Assert.assertEquals("http://www.host.com", URLUtil.encodeURL("http://www.host.com"));
        Assert.assertEquals("http://www.host.com/t%20est?x=1", URLUtil.encodeURL("http://www.host.com/t est?x=1"));
        Assert.assertEquals("http://test/%E2%80%A5/test", URLUtil.encodeURL("http://test/‥/test"));
        Assert.assertEquals("www.ho st.com/t est?x=1", URLUtil.encodeURL("www.ho st.com/t est?x=1"));
    }
}
