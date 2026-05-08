/*
 * Copyright (c) 2026 Alwin Ibba
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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPItemTest {

    @Test
    public void testDefaultValues() {
        SNMPItem item = new SNMPItem();
        assertEquals(-1, item.getId());
        assertEquals(-1, item.getNetworkTaskId());
        assertNull(item.getSnmpItemType());
        assertNull(item.getName());
        assertNull(item.getOid());
        assertFalse(item.isMonitored());
        Bundle bundle = item.toBundle();
        assertNotNull(bundle);
        item = new SNMPItem(bundle);
        assertEquals(-1, item.getId());
        assertEquals(-1, item.getNetworkTaskId());
        assertNull(item.getSnmpItemType());
        assertNull(item.getName());
        assertNull(item.getOid());
        assertFalse(item.isMonitored());
        Map<String, ?> map = item.toMap();
        assertNotNull(map);
        item = new SNMPItem(map);
        assertEquals(-1, item.getId());
        assertEquals(-1, item.getNetworkTaskId());
        assertNull(item.getSnmpItemType());
        assertNull(item.getName());
        assertNull(item.getOid());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testCopy() {
        SNMPItem item = new SNMPItem();
        item.setId(1);
        item.setNetworkTaskId(2);
        item.setSnmpItemType(SNMPItemType.INTERFACE);
        item.setName("eth0");
        item.setOid("1.3.6.1.2.1.2.2.1.1.1");
        item.setMonitored(true);
        SNMPItem copyItem = new SNMPItem(item);
        assertEquals(-1, copyItem.getId());
        assertEquals(-1, copyItem.getNetworkTaskId());
        assertEquals(SNMPItemType.INTERFACE, copyItem.getSnmpItemType());
        assertEquals("eth0", copyItem.getName());
        assertEquals("1.3.6.1.2.1.2.2.1.1.1", copyItem.getOid());
        assertTrue(copyItem.isMonitored());
    }

    @Test
    public void testEmptyMap() {
        SNMPItem item = new SNMPItem(new HashMap<>());
        assertEquals(-1, item.getId());
        assertEquals(-1, item.getNetworkTaskId());
        assertNull(item.getSnmpItemType());
        assertNull(item.getName());
        assertNull(item.getOid());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("networktaskid", "networktaskid");
        map.put("snmpItemType", "snmpItemType");
        SNMPItem item = new SNMPItem(map);
        assertEquals(-1, item.getId());
        assertEquals(-1, item.getNetworkTaskId());
        assertNull(item.getSnmpItemType());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("networktaskid", "2");
        map.put("snmpItemType", "1");
        map.put("name", "eth0");
        map.put("oid", "1.3.6.1.2.1.2.2.1.1.1");
        map.put("monitored", "true");
        SNMPItem item = new SNMPItem(map);
        assertEquals(1, item.getId());
        assertEquals(2, item.getNetworkTaskId());
        assertEquals(SNMPItemType.INTERFACE, item.getSnmpItemType());
        assertEquals("eth0", item.getName());
        assertEquals("1.3.6.1.2.1.2.2.1.1.1", item.getOid());
        assertTrue(item.isMonitored());
        map.put("monitored", "false");
        item = new SNMPItem(map);
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToBundleValues() {
        SNMPItem item = new SNMPItem();
        item.setId(1);
        item.setNetworkTaskId(2);
        item.setSnmpItemType(SNMPItemType.NUMERIC);
        item.setName("counter");
        item.setOid("1.3.6.1.2.1.31.1.1.1.10.1");
        item.setMonitored(true);
        assertEquals(1, item.getId());
        assertEquals(2, item.getNetworkTaskId());
        assertEquals(SNMPItemType.NUMERIC, item.getSnmpItemType());
        assertEquals("counter", item.getName());
        assertEquals("1.3.6.1.2.1.31.1.1.1.10.1", item.getOid());
        assertTrue(item.isMonitored());
        Bundle bundle = item.toBundle();
        assertNotNull(bundle);
        item = new SNMPItem(bundle);
        assertEquals(1, item.getId());
        assertEquals(2, item.getNetworkTaskId());
        assertEquals(SNMPItemType.NUMERIC, item.getSnmpItemType());
        assertEquals("counter", item.getName());
        assertEquals("1.3.6.1.2.1.31.1.1.1.10.1", item.getOid());
        assertTrue(item.isMonitored());
    }

    @Test
    public void testToMap() {
        SNMPItem item = new SNMPItem();
        item.setId(1);
        item.setNetworkTaskId(2);
        item.setSnmpItemType(SNMPItemType.INTERFACE);
        item.setName("eth0");
        item.setOid("1.3.6.1.2.1.2.2.1.1.1");
        item.setMonitored(true);
        Map<String, ?> map = item.toMap();
        assertNotNull(map);
        item = new SNMPItem(map);
        assertEquals(1, item.getId());
        assertEquals(2, item.getNetworkTaskId());
        assertEquals(SNMPItemType.INTERFACE, item.getSnmpItemType());
        assertEquals("eth0", item.getName());
        assertEquals("1.3.6.1.2.1.2.2.1.1.1", item.getOid());
        assertTrue(item.isMonitored());
    }

    @Test
    public void testIsEqual() {
        SNMPItem item1 = new SNMPItem();
        SNMPItem item2 = new SNMPItem();
        assertTrue(item1.isEqual(item2));
        item1.setId(0);
        assertFalse(item1.isEqual(item2));
        item2.setId(0);
        assertTrue(item1.isEqual(item2));
        item1.setNetworkTaskId(22);
        assertFalse(item1.isEqual(item2));
        item2.setNetworkTaskId(22);
        assertTrue(item1.isEqual(item2));
        item1.setSnmpItemType(SNMPItemType.INTERFACE);
        assertFalse(item1.isEqual(item2));
        item2.setSnmpItemType(SNMPItemType.INTERFACE);
        assertTrue(item1.isEqual(item2));
        item1.setName("eth0");
        assertFalse(item1.isEqual(item2));
        item2.setName("eth0");
        assertTrue(item1.isEqual(item2));
        item1.setOid("1.3.6.1.2.1.2.2.1.1.1");
        assertFalse(item1.isEqual(item2));
        item2.setOid("1.3.6.1.2.1.2.2.1.1.1");
        assertTrue(item1.isEqual(item2));
        item1.setMonitored(true);
        assertFalse(item1.isEqual(item2));
        item2.setMonitored(true);
        assertTrue(item1.isEqual(item2));
    }

    @Test
    public void testIsTechnicallyEqual() {
        SNMPItem item1 = new SNMPItem();
        SNMPItem item2 = new SNMPItem();
        assertTrue(item1.isTechnicallyEqual(item2));
        item1.setId(0);
        assertTrue(item1.isTechnicallyEqual(item2));
        item2.setId(0);
        assertTrue(item1.isTechnicallyEqual(item2));
        item1.setNetworkTaskId(22);
        assertFalse(item1.isTechnicallyEqual(item2));
        item2.setNetworkTaskId(22);
        assertTrue(item1.isTechnicallyEqual(item2));
        item1.setSnmpItemType(SNMPItemType.NUMERIC);
        assertFalse(item1.isTechnicallyEqual(item2));
        item2.setSnmpItemType(SNMPItemType.NUMERIC);
        assertTrue(item1.isTechnicallyEqual(item2));
        item1.setName("counter");
        assertFalse(item1.isTechnicallyEqual(item2));
        item2.setName("counter");
        assertTrue(item1.isTechnicallyEqual(item2));
        item1.setOid("1.3.6.1.2.1.31.1.1.1.10.1");
        assertFalse(item1.isTechnicallyEqual(item2));
        item2.setOid("1.3.6.1.2.1.31.1.1.1.10.1");
        assertTrue(item1.isTechnicallyEqual(item2));
        item1.setMonitored(true);
        assertFalse(item1.isTechnicallyEqual(item2));
        item2.setMonitored(true);
        assertTrue(item1.isTechnicallyEqual(item2));
    }
}
