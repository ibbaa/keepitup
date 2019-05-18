package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.resources.NetworkTaskPreferenceManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskTest {

    @Before
    public void beforeEachTestMethod() {
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testDefaultValues() {
        NetworkTask task = new NetworkTask();
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        Bundle bundle = task.toBundle();
        assertNotNull(bundle);
        task = new NetworkTask(bundle);
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
    }

    @Test
    public void testPreferenceValues() {
        NetworkTaskPreferenceManager preferenceManager = new NetworkTaskPreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("host.com");
        preferenceManager.setPreferencePort(80);
        preferenceManager.setPreferenceInterval(1);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        NetworkTask task = new NetworkTask(TestRegistry.getContext());
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals("host.com", task.getAddress());
        assertEquals(80, task.getPort());
        assertEquals(AccessType.CONNECT, task.getAccessType());
        assertEquals(1, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertFalse(task.isRunning());
        preferenceManager.removeAllPreferences();
        task = new NetworkTask(TestRegistry.getContext());
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals("192.168.178.1", task.getAddress());
        assertEquals(22, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
    }

    @Test
    public void testToBundle() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setSchedulerId(3);
        task.setAddress("127.0.0.1");
        task.setPort(23);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(true);
        task.setNotification(true);
        task.setRunning(true);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        Bundle bundle = task.toBundle();
        assertNotNull(bundle);
        task = new NetworkTask(bundle);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
    }
}
