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

package net.ibbaa.keepitup.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AccessTypeDataTest {

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
        AccessTypeData data = new AccessTypeData();
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        PersistableBundle persistableBundle = data.toPersistableBundle();
        assertNotNull(persistableBundle);
        data = new AccessTypeData(persistableBundle);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        Bundle bundle = data.toBundle();
        assertNotNull(bundle);
        data = new AccessTypeData(bundle);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        Map<String, ?> map = data.toMap();
        assertNotNull(map);
        data = new AccessTypeData(map);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
    }

    @Test
    public void testEmptyMap() {
        AccessTypeData data = new AccessTypeData(new HashMap<>());
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("networktaskid", "networktaskid");
        map.put("pingCount", "pingCount");
        map.put("pingPackageSize", "pingPackageSize");
        map.put("connectCount", "connectCount");
        map.put("stopOnSuccess", "stopOnSuccess");
        map.put("ignoreSSLError", "isIgnoreSSLError");
        AccessTypeData data = new AccessTypeData(map);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("networktaskid", "2");
        map.put("pingCount", "123");
        map.put("pingPackageSize", "456");
        map.put("connectCount", "789");
        map.put("stopOnSuccess", "true");
        map.put("ignoreSSLError", "true");
        AccessTypeData data = new AccessTypeData(map);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
    }

    @Test
    public void testPreferenceValues() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferencePingCount(123);
        preferenceManager.setPreferencePingPackageSize(456);
        preferenceManager.setPreferenceConnectCount(789);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceIgnoreSSLError(true);
        AccessTypeData data = new AccessTypeData(TestRegistry.getContext());
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        preferenceManager.removeAllPreferences();
        data = new AccessTypeData(TestRegistry.getContext());
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
    }

    @Test
    public void testToBundleValues() {
        AccessTypeData data = new AccessTypeData();
        data.setId(1);
        data.setNetworkTaskId(2);
        data.setPingCount(123);
        data.setPingPackageSize(456);
        data.setConnectCount(789);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        PersistableBundle persistableBundle = data.toPersistableBundle();
        assertNotNull(persistableBundle);
        data = new AccessTypeData(persistableBundle);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        Bundle bundle = data.toBundle();
        assertNotNull(bundle);
        data = new AccessTypeData(bundle);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
    }

    @Test
    public void testToMap() {
        AccessTypeData data = new AccessTypeData();
        data.setId(1);
        data.setNetworkTaskId(2);
        data.setPingCount(123);
        data.setPingPackageSize(456);
        data.setConnectCount(789);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        Map<String, ?> map = data.toMap();
        assertNotNull(map);
        data = new AccessTypeData(map);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
    }

    @Test
    public void testIsEqual() {
        AccessTypeData data1 = new AccessTypeData();
        AccessTypeData data2 = new AccessTypeData();
        assertTrue(data1.isEqual(data2));
        data1.setId(0);
        assertFalse(data1.isEqual(data2));
        data2.setId(0);
        assertTrue(data1.isEqual(data2));
        data1.setNetworkTaskId(22);
        assertFalse(data1.isEqual(data2));
        data2.setNetworkTaskId(22);
        assertTrue(data1.isEqual(data2));
        data1.setPingCount(123);
        assertFalse(data1.isEqual(data2));
        data2.setPingCount(123);
        assertTrue(data1.isEqual(data2));
        data1.setPingPackageSize(456);
        assertFalse(data1.isEqual(data2));
        data2.setPingPackageSize(456);
        assertTrue(data1.isEqual(data2));
        data1.setConnectCount(789);
        assertFalse(data1.isEqual(data2));
        data2.setConnectCount(789);
        assertTrue(data1.isEqual(data2));
        data1.setStopOnSuccess(true);
        assertFalse(data1.isEqual(data2));
        data2.setStopOnSuccess(true);
        assertTrue(data1.isEqual(data2));
        data1.setIgnoreSSLError(true);
        assertFalse(data1.isEqual(data2));
        data2.setIgnoreSSLError(true);
        assertTrue(data1.isEqual(data2));
    }

    @Test
    public void testTechnicallyIsEqual() {
        AccessTypeData data1 = new AccessTypeData();
        AccessTypeData data2 = new AccessTypeData();
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setId(0);
        assertTrue(data1.isTechnicallyEqual(data2));
        data2.setId(0);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setNetworkTaskId(22);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setNetworkTaskId(22);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setPingCount(123);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setPingCount(123);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setPingPackageSize(456);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setPingPackageSize(456);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setConnectCount(789);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setConnectCount(789);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setStopOnSuccess(true);
        assertFalse(data1.isEqual(data2));
        data2.setStopOnSuccess(true);
        assertTrue(data1.isEqual(data2));
        data1.setIgnoreSSLError(true);
        assertFalse(data1.isEqual(data2));
        data2.setIgnoreSSLError(true);
        assertTrue(data1.isEqual(data2));
    }
}
