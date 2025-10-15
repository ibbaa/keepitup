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

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HeaderTest {

    @Test
    public void testDefaultValues() {
        Header header = new Header();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
        PersistableBundle persistableBundle = header.toPersistableBundle();
        assertNotNull(persistableBundle);
        header = new Header(persistableBundle);
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
        Bundle bundle = header.toBundle();
        assertNotNull(bundle);
        header = new Header(bundle);
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
        Map<String, ?> map = header.toMap();
        assertNotNull(map);
        header = new Header(map);
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
    }

    @Test
    public void testNetworkTaskIdInitialize() {
        Header header = new Header(25);
        assertEquals(-1, header.getId());
        assertEquals(25, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
    }

    @Test
    public void testCopy() {
        Header header = new Header();
        header.setId(1);
        header.setNetworkTaskId(2);
        header.setName("name");
        header.setValue("value");
        Header copyResolve = new Header(header);
        assertEquals(-1, copyResolve.getId());
        assertEquals(-1, copyResolve.getNetworkTaskId());
        assertEquals("name", copyResolve.getName());
        assertEquals("value", copyResolve.getValue());
    }

    @Test
    public void testEmptyMap() {
        Header header = new Header();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("networktaskid", "networktaskid");
        map.put("name", null);
        map.put("value", null);
        Header header = new Header(map);
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertNull(header.getName());
        assertNull(header.getValue());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("networktaskid", "2");
        map.put("name", "name");
        map.put("value", "value");
        Header header = new Header(map);
        assertEquals(1, header.getId());
        assertEquals(2, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
    }

    @Test
    public void testToBundleValues() {
        Header header = new Header();
        header.setId(1);
        header.setNetworkTaskId(2);
        header.setName("name");
        header.setValue("value");
        assertEquals(1, header.getId());
        assertEquals(2, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
        PersistableBundle persistableBundle = header.toPersistableBundle();
        assertNotNull(persistableBundle);
        header = new Header(persistableBundle);
        assertEquals(1, header.getId());
        assertEquals(2, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
        Bundle bundle = header.toBundle();
        assertNotNull(bundle);
        header = new Header(bundle);
        assertEquals(1, header.getId());
        assertEquals(2, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
    }

    @Test
    public void testToMap() {
        Header header = new Header();
        header.setId(1);
        header.setNetworkTaskId(2);
        header.setName("name");
        header.setValue("value");
        Map<String, ?> map = header.toMap();
        assertNotNull(map);
        header = new Header(map);
        assertEquals(1, header.getId());
        assertEquals(2, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
    }

    @Test
    public void testIsEqual() {
        Header header1 = new Header();
        Header header2 = new Header();
        assertTrue(header1.isEqual(header2));
        header1.setId(0);
        assertFalse(header1.isEqual(header2));
        header2.setId(0);
        assertTrue(header1.isEqual(header2));
        header1.setNetworkTaskId(22);
        assertFalse(header1.isEqual(header2));
        header2.setNetworkTaskId(22);
        assertTrue(header1.isEqual(header2));
        header1.setName("name");
        assertFalse(header1.isEqual(header2));
        header2.setName("name");
        assertTrue(header1.isEqual(header2));
        header1.setValue("value");
        assertFalse(header1.isEqual(header2));
        header2.setValue("value");
        assertTrue(header1.isEqual(header2));
    }

    @Test
    public void testIsTechnicallyEqual() {
        Header header1 = new Header();
        Header header2 = new Header();
        assertTrue(header1.isTechnicallyEqual(header2));
        header1.setId(0);
        assertTrue(header1.isTechnicallyEqual(header2));
        header2.setId(0);
        assertTrue(header1.isTechnicallyEqual(header2));
        header1.setNetworkTaskId(22);
        assertFalse(header1.isTechnicallyEqual(header2));
        header2.setNetworkTaskId(22);
        assertTrue(header1.isTechnicallyEqual(header2));
        header1.setName("name");
        assertFalse(header1.isTechnicallyEqual(header2));
        header2.setName("name");
        assertTrue(header1.isTechnicallyEqual(header2));
        header1.setValue("value");
        assertFalse(header1.isTechnicallyEqual(header2));
        header2.setValue("value");
        assertTrue(header1.isTechnicallyEqual(header2));
    }
}
