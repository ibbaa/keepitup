package de.ibba.keepitup.resources;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class PreferenceManagerTest {

    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testGetSetRemovePreferenceString() {
        assertEquals("123", preferenceManager.getPreferenceString("testkey", "123"));
        preferenceManager.setPreferenceString("testkey", "456");
        assertEquals("456", preferenceManager.getPreferenceString("testkey", "123"));
        preferenceManager.removePreferenceValue("testkey");
        assertEquals("123", preferenceManager.getPreferenceString("testkey", "123"));
        preferenceManager.setPreferenceString("testkey", "456");
        preferenceManager.removeAllPreferences();
        assertEquals("123", preferenceManager.getPreferenceString("testkey", "123"));
    }

    @Test
    public void testGetSetRemovePreferenceLong() {
        assertEquals(123, preferenceManager.getPreferenceLong("testkey", 123));
        preferenceManager.setPreferenceLong("testkey", 456);
        assertEquals(456, preferenceManager.getPreferenceLong("testkey", 123));
        preferenceManager.removePreferenceValue("testkey");
        assertEquals(123, preferenceManager.getPreferenceLong("testkey", 123));
        preferenceManager.setPreferenceLong("testkey", 456);
        preferenceManager.removeAllPreferences();
        assertEquals(123, preferenceManager.getPreferenceLong("testkey", 123));
    }

    @Test
    public void testGetSetRemovePreferenceInt() {
        assertEquals(123, preferenceManager.getPreferenceInt("testkey", 123));
        preferenceManager.setPreferenceInt("testkey", 456);
        assertEquals(456, preferenceManager.getPreferenceInt("testkey", 123));
        preferenceManager.removePreferenceValue("testkey");
        assertEquals(123, preferenceManager.getPreferenceInt("testkey", 123));
        preferenceManager.setPreferenceInt("testkey", 456);
        preferenceManager.removeAllPreferences();
        assertEquals(123, preferenceManager.getPreferenceInt("testkey", 123));
    }

    @Test
    public void testGetSetRemovePreferenceBoolean() {
        assertTrue(preferenceManager.getPreferenceBoolean("testkey", true));
        preferenceManager.setPreferenceBoolean("testkey", false);
        assertFalse(preferenceManager.getPreferenceBoolean("testkey", true));
        preferenceManager.removePreferenceValue("testkey");
        assertTrue(preferenceManager.getPreferenceBoolean("testkey", true));
        preferenceManager.setPreferenceBoolean("testkey", false);
        preferenceManager.removePreferenceValue("testkey");
        assertTrue(preferenceManager.getPreferenceBoolean("testkey", true));
    }

    @Test
    public void testGetSetRemovePreferenceAccessType() {
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        preferenceManager.setPreferenceAccessType(AccessType.DOWNLOAD);
        assertEquals(AccessType.DOWNLOAD, preferenceManager.getPreferenceAccessType());
        preferenceManager.removeAllPreferences();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        preferenceManager.setPreferenceAccessType(AccessType.DOWNLOAD);
        preferenceManager.removePreferenceAccessType();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
    }

    @Test
    public void testGetSetRemovePreferenceAddress() {
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        preferenceManager.setPreferenceAddress("www.host.com");
        assertEquals("www.host.com", preferenceManager.getPreferenceAddress());
        preferenceManager.removeAllPreferences();
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        preferenceManager.setPreferenceAddress("www.host.com");
        preferenceManager.removePreferenceAddress();
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
    }

    @Test
    public void testGetSetRemovePreferencePort() {
        assertEquals(22, preferenceManager.getPreferencePort());
        preferenceManager.setPreferencePort(80);
        assertEquals(80, preferenceManager.getPreferencePort());
        preferenceManager.removeAllPreferences();
        assertEquals(22, preferenceManager.getPreferencePort());
        preferenceManager.setPreferencePort(80);
        preferenceManager.removePreferencePort();
        assertEquals(22, preferenceManager.getPreferencePort());
    }

    @Test
    public void testGetSetRemovePreferenceInterval() {
        assertEquals(15, preferenceManager.getPreferenceInterval());
        preferenceManager.setPreferenceInterval(1);
        assertEquals(1, preferenceManager.getPreferenceInterval());
        preferenceManager.removeAllPreferences();
        assertEquals(15, preferenceManager.getPreferenceInterval());
        preferenceManager.setPreferenceInterval(1);
        preferenceManager.removePreferenceInterval();
        assertEquals(15, preferenceManager.getPreferenceInterval());
    }

    @Test
    public void testGetSetRemovePreferenceOnlyWifi() {
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        preferenceManager.setPreferenceOnlyWifi(true);
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        preferenceManager.removeAllPreferences();
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.removePreferenceOnlyWifi();
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
    }

    @Test
    public void testGetSetRemovePreferenceNotification() {
        assertFalse(preferenceManager.getPreferenceNotification());
        preferenceManager.setPreferenceNotification(true);
        assertTrue(preferenceManager.getPreferenceNotification());
        preferenceManager.removeAllPreferences();
        assertFalse(preferenceManager.getPreferenceNotification());
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.removePreferenceNotification();
        assertFalse(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testGetSetRemoveNetworkTimeout() {
        assertEquals(15, preferenceManager.getPreferenceNetworkTimeout());
        preferenceManager.setPreferenceNetworkTimeout(10);
        assertEquals(10, preferenceManager.getPreferenceNetworkTimeout());
        preferenceManager.removeAllPreferences();
        assertEquals(15, preferenceManager.getPreferenceNetworkTimeout());
        preferenceManager.setPreferenceNetworkTimeout(2);
        preferenceManager.removePreferenceNetworkTimeout();
        assertEquals(15, preferenceManager.getPreferenceNetworkTimeout());
    }

    @Test
    public void testGetSetRemovePingCount() {
        assertEquals(3, preferenceManager.getPreferencePingCount());
        preferenceManager.setPreferencePingCount(15);
        assertEquals(15, preferenceManager.getPreferencePingCount());
        preferenceManager.removeAllPreferences();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        preferenceManager.setPreferencePingCount(2);
        preferenceManager.removePreferencePingCount();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }
}
