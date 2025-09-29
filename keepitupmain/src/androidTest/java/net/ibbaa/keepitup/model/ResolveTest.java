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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ResolveTest {

    @Test
    public void testDefaultValues() {
        Resolve resolve = new Resolve();
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(0, resolve.getPort());
        PersistableBundle persistableBundle = resolve.toPersistableBundle();
        assertNotNull(persistableBundle);
        resolve = new Resolve(persistableBundle);
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(0, resolve.getPort());
        Bundle bundle = resolve.toBundle();
        assertNotNull(bundle);
        resolve = new Resolve(bundle);
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(0, resolve.getPort());
        Map<String, ?> map = resolve.toMap();
        assertNotNull(map);
        resolve = new Resolve(map);
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(0, resolve.getPort());
    }

    @Test
    public void testCopy() {
        Resolve resolve = new Resolve();
        resolve.setId(1);
        resolve.setNetworkTaskId(2);
        resolve.setAddress("127.0.0.1");
        resolve.setPort(23);
        Resolve copyResolve = new Resolve(resolve);
        assertEquals(-1, copyResolve.getId());
        assertEquals(-1, copyResolve.getNetworkTaskId());
        assertEquals("127.0.0.1", copyResolve.getAddress());
        assertEquals(23, copyResolve.getPort());
    }

    @Test
    public void testEmptyMap() {
        Resolve resolve = new Resolve();
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(0, resolve.getPort());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("networktaskid", "networktaskid");
        map.put("address", null);
        map.put("port", "port");
        Resolve resolve = new Resolve(map);
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(0, resolve.getPort());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("networktaskid", "2");
        map.put("address", "address");
        map.put("port", "5");
        Resolve resolve = new Resolve(map);
        assertEquals(1, resolve.getId());
        assertEquals(2, resolve.getNetworkTaskId());
        assertEquals("address", resolve.getAddress());
        assertEquals(5, resolve.getPort());
    }

    @Test
    public void testPreferenceValues() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceResolveAddress("127.0.0.1");
        preferenceManager.setPreferenceResolvePort(12);
        Resolve resolve = new Resolve(TestRegistry.getContext());
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertEquals("127.0.0.1", resolve.getAddress());
        assertEquals(12, resolve.getPort());
        preferenceManager.removeAllPreferences();
        resolve = new Resolve(TestRegistry.getContext());
        assertEquals(-1, resolve.getId());
        assertEquals(-1, resolve.getNetworkTaskId());
        assertNull(resolve.getAddress());
        assertEquals(-1, resolve.getPort());
    }

    @Test
    public void testToBundleValues() {
        Resolve resolve = new Resolve();
        resolve.setId(1);
        resolve.setNetworkTaskId(2);
        resolve.setAddress("127.0.0.1");
        resolve.setPort(23);
        assertEquals(1, resolve.getId());
        assertEquals(2, resolve.getNetworkTaskId());
        assertEquals("127.0.0.1", resolve.getAddress());
        assertEquals(23, resolve.getPort());
        PersistableBundle persistableBundle = resolve.toPersistableBundle();
        assertNotNull(persistableBundle);
        resolve = new Resolve(persistableBundle);
        assertEquals(1, resolve.getId());
        assertEquals(2, resolve.getNetworkTaskId());
        assertEquals("127.0.0.1", resolve.getAddress());
        assertEquals(23, resolve.getPort());
        Bundle bundle = resolve.toBundle();
        assertNotNull(bundle);
        resolve = new Resolve(bundle);
        assertEquals(1, resolve.getId());
        assertEquals(2, resolve.getNetworkTaskId());
        assertEquals("127.0.0.1", resolve.getAddress());
        assertEquals(23, resolve.getPort());
    }

    @Test
    public void testToMap() {
        Resolve resolve = new Resolve();
        resolve.setId(1);
        resolve.setNetworkTaskId(2);
        resolve.setAddress("127.0.0.1");
        resolve.setPort(23);
        Map<String, ?> map = resolve.toMap();
        assertNotNull(map);
        resolve = new Resolve(map);
        assertEquals(1, resolve.getId());
        assertEquals(2, resolve.getNetworkTaskId());
        assertEquals("127.0.0.1", resolve.getAddress());
        assertEquals(23, resolve.getPort());
    }

    @Test
    public void testIsEqual() {
        Resolve resolve1 = new Resolve();
        Resolve resolve2 = new Resolve();
        assertTrue(resolve1.isEqual(resolve2));
        resolve1.setId(0);
        assertFalse(resolve1.isEqual(resolve2));
        resolve2.setId(0);
        assertTrue(resolve1.isEqual(resolve2));
        resolve1.setNetworkTaskId(22);
        assertFalse(resolve1.isEqual(resolve2));
        resolve2.setNetworkTaskId(22);
        assertTrue(resolve1.isEqual(resolve2));
        resolve1.setAddress("123");
        assertFalse(resolve1.isEqual(resolve2));
        resolve2.setAddress("123");
        assertTrue(resolve1.isEqual(resolve2));
        resolve1.setPort(10);
        assertFalse(resolve1.isEqual(resolve2));
        resolve2.setPort(10);
        assertTrue(resolve1.isEqual(resolve2));
    }

    @Test
    public void testIsTechnicallyEqual() {
        Resolve resolve1 = new Resolve();
        Resolve resolve2 = new Resolve();
        assertTrue(resolve1.isTechnicallyEqual(resolve2));
        resolve1.setId(0);
        assertTrue(resolve1.isTechnicallyEqual(resolve2));
        resolve2.setId(0);
        assertTrue(resolve1.isTechnicallyEqual(resolve2));
        resolve1.setNetworkTaskId(22);
        assertFalse(resolve1.isTechnicallyEqual(resolve2));
        resolve2.setNetworkTaskId(22);
        assertTrue(resolve1.isTechnicallyEqual(resolve2));
        resolve1.setAddress("123");
        assertFalse(resolve1.isTechnicallyEqual(resolve2));
        resolve2.setAddress("123");
        assertTrue(resolve1.isTechnicallyEqual(resolve2));
        resolve1.setPort(10);
        assertFalse(resolve1.isTechnicallyEqual(resolve2));
        resolve2.setPort(10);
        assertTrue(resolve1.isTechnicallyEqual(resolve2));
    }
}
