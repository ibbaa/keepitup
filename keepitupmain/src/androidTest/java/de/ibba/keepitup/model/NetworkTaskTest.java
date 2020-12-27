package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import de.ibba.keepitup.resources.PreferenceManager;
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
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testDefaultValues() {
        NetworkTask task = new NetworkTask();
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
        Bundle bundle = task.toBundle();
        assertNotNull(bundle);
        task = new NetworkTask(bundle);
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
        Map<String, ?> map = task.toMap();
        assertNotNull(map);
        task = new NetworkTask(map);
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
    }

    @Test
    public void testEmptyMap() {
        NetworkTask task = new NetworkTask(new HashMap<>());
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "xyz");
        map.put("index", "index");
        map.put("schedulerid", "abc");
        map.put("instances", "abc");
        map.put("address", null);
        map.put("port", "port");
        map.put("accessType", -1);
        map.put("interval", "interval");
        map.put("onlyWifi", "fal");
        map.put("notification", "tru");
        map.put("running", "tru");
        map.put("lastScheduled", "xyz");
        NetworkTask task = new NetworkTask(map);
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertNull(task.getAddress());
        assertEquals(0, task.getPort());
        assertNull(task.getAccessType());
        assertEquals(0, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("index", "2");
        map.put("schedulerid", "3");
        map.put("instances", "4");
        map.put("address", "address");
        map.put("port", "5");
        map.put("accessType", "1");
        map.put("interval", "6");
        map.put("onlyWifi", "true");
        map.put("notification", "true");
        map.put("running", "true");
        map.put("lastScheduled", "7");
        NetworkTask task = new NetworkTask(map);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals(4, task.getInstances());
        assertEquals("address", task.getAddress());
        assertEquals(5, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(6, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        assertEquals(7, task.getLastScheduled());
    }

    @Test
    public void testPreferenceValues() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
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
        assertEquals(0, task.getInstances());
        assertEquals("host.com", task.getAddress());
        assertEquals(80, task.getPort());
        assertEquals(AccessType.CONNECT, task.getAccessType());
        assertEquals(1, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
        preferenceManager.removeAllPreferences();
        task = new NetworkTask(TestRegistry.getContext());
        assertEquals(-1, task.getId());
        assertEquals(-1, task.getIndex());
        assertEquals(-1, task.getSchedulerId());
        assertEquals(0, task.getInstances());
        assertEquals("192.168.178.1", task.getAddress());
        assertEquals(22, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isRunning());
        assertEquals(-1, task.getLastScheduled());
    }

    @Test
    public void testToBundleValues() {
        long timestamp = System.currentTimeMillis();
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setSchedulerId(3);
        task.setInstances(3);
        task.setAddress("127.0.0.1");
        task.setPort(23);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(true);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(timestamp);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals(3, task.getInstances());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        assertEquals(timestamp, task.getLastScheduled());
        PersistableBundle persistableBundle = task.toPersistableBundle();
        assertNotNull(persistableBundle);
        task = new NetworkTask(persistableBundle);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals(3, task.getInstances());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        assertEquals(timestamp, task.getLastScheduled());
        Bundle bundle = task.toBundle();
        assertNotNull(bundle);
        task = new NetworkTask(bundle);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals(3, task.getInstances());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        assertEquals(timestamp, task.getLastScheduled());
    }

    @Test
    public void testToMap() {
        long timestamp = System.currentTimeMillis();
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setSchedulerId(3);
        task.setInstances(3);
        task.setAddress("127.0.0.1");
        task.setPort(23);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(true);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(timestamp);
        Map<String, ?> map = task.toMap();
        assertNotNull(map);
        task = new NetworkTask(map);
        assertEquals(1, task.getId());
        assertEquals(2, task.getIndex());
        assertEquals(3, task.getSchedulerId());
        assertEquals(3, task.getInstances());
        assertEquals("127.0.0.1", task.getAddress());
        assertEquals(23, task.getPort());
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(15, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isRunning());
        assertEquals(timestamp, task.getLastScheduled());
    }

    @Test
    public void testIsEqual() {
        NetworkTask networkTask1 = new NetworkTask();
        NetworkTask networkTask2 = new NetworkTask();
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setId(0);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setId(0);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setAccessType(AccessType.DOWNLOAD);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setAccessType(AccessType.DOWNLOAD);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setAddress("123");
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setAddress("123");
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setIndex(5);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setIndex(5);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setInstances(8);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setInstances(8);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setInterval(1);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setInterval(1);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setNotification(true);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setNotification(true);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setOnlyWifi(true);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setOnlyWifi(true);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setPort(10);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setPort(10);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setRunning(true);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setRunning(true);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setSchedulerId(11);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setSchedulerId(11);
        assertTrue(networkTask1.isEqual(networkTask2));
        networkTask1.setLastScheduled(25);
        assertFalse(networkTask1.isEqual(networkTask2));
        networkTask2.setLastScheduled(25);
        assertTrue(networkTask1.isEqual(networkTask2));
    }

    @Test
    public void testIsTechnicallyEqual() {
        NetworkTask networkTask1 = new NetworkTask();
        NetworkTask networkTask2 = new NetworkTask();
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setId(0);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setSchedulerId(1);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setRunning(true);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setInstances(6);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setIndex(2);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setPort(9);
        assertFalse(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask2.setPort(9);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setAddress("123");
        assertFalse(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask2.setAddress("123");
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setOnlyWifi(true);
        assertFalse(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask2.setOnlyWifi(true);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setNotification(true);
        assertFalse(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask2.setNotification(true);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setInterval(5);
        assertFalse(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask2.setInterval(5);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask1.setAccessType(AccessType.CONNECT);
        assertFalse(networkTask1.isTechnicallyEqual(networkTask2));
        networkTask2.setAccessType(AccessType.CONNECT);
        assertTrue(networkTask1.isTechnicallyEqual(networkTask2));
    }
}
