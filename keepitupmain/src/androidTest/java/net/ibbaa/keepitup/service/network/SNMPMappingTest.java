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

package net.ibbaa.keepitup.service.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemMergeResult;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@SmallTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class SNMPMappingTest {

    private SNMPMapping snmpMapping;

    @Before
    public void beforeEachTestMethod() {
        snmpMapping = new SNMPMapping(TestRegistry.getContext());
    }

    @Test
    public void testSupportsSystemOID() {
        assertFalse(snmpMapping.supportsSystemOID(null));
        assertFalse(snmpMapping.supportsSystemOID("unknown"));
        assertTrue(snmpMapping.supportsSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid)));
        assertTrue(snmpMapping.supportsSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid)));
        assertTrue(snmpMapping.supportsSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_object_id_oid)));
        assertTrue(snmpMapping.supportsSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_contact_oid)));
        assertTrue(snmpMapping.supportsSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_name_oid)));
        assertTrue(snmpMapping.supportsSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_location_oid)));
    }

    @Test
    public void testGetLabelForSystemOID() {
        assertNull(snmpMapping.getLabelForSystemOID(null));
        assertNull(snmpMapping.getLabelForSystemOID("unknown"));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_descr_label), snmpMapping.getLabelForSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_label), snmpMapping.getLabelForSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_object_id_label), snmpMapping.getLabelForSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_object_id_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_contact_label), snmpMapping.getLabelForSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_contact_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_name_label), snmpMapping.getLabelForSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_name_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_location_label), snmpMapping.getLabelForSystemOID(TestRegistry.getContext().getResources().getString(R.string.sys_location_oid)));
    }

    @Test
    public void testGetValueForOID() {
        assertNull(snmpMapping.getValueForOID(null, new OctetString("test")));
        assertNull(snmpMapping.getValueForOID("", new OctetString("test")));
        assertNull(snmpMapping.getValueForOID(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid), null));
        String sysUpTimeOid = TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid);
        TimeTicks timeTicks = new TimeTicks(12345);
        assertEquals(String.valueOf(timeTicks.toLong()), snmpMapping.getValueForOID(sysUpTimeOid, timeTicks));
        OctetString octetString = new OctetString("text");
        assertEquals(octetString.toString(), snmpMapping.getValueForOID(sysUpTimeOid, octetString));
        String sysDescrOid = TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid);
        OctetString descrValue = new OctetString("System description");
        assertEquals(descrValue.toString(), snmpMapping.getValueForOID(sysDescrOid, descrValue));
    }

    @Test
    public void testGetSysUpTime() {
        assertEquals(-1, snmpMapping.getSysUpTime(null));
        assertEquals(-1, snmpMapping.getSysUpTime(new HashMap<>()));
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        Map<String, String> valuesWithoutSysUpTime = new HashMap<>();
        valuesWithoutSysUpTime.put(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid), "description");
        assertEquals(-1, snmpMapping.getSysUpTime(valuesWithoutSysUpTime));
        Map<String, String> valuesWithSysUpTime = new HashMap<>();
        valuesWithSysUpTime.put(sysUpTimeOid, "12345");
        assertEquals(12345, snmpMapping.getSysUpTime(valuesWithSysUpTime));
        Map<String, String> valuesWithInvalidSysUpTime = new HashMap<>();
        valuesWithInvalidSysUpTime.put(sysUpTimeOid, "invalid");
        assertEquals(-1, snmpMapping.getSysUpTime(valuesWithInvalidSysUpTime));
    }

    @Test
    public void testGetSystemOID() {
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.system_oid), snmpMapping.getSystemOID());
    }

    @Test
    public void testGetInterfaceDescrOID() {
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid), snmpMapping.getInterfaceDescrOID());
    }

    @Test
    public void testGetInterfaceTypeOID() {
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_type_oid), snmpMapping.getInterfaceTypeOID());
    }

    @Test
    public void testGetInterfaceOperStatusOID() {
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid), snmpMapping.getInterfaceOperStatusOID());
    }

    @Test
    public void testGetInterfaceAliasOID() {
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid), snmpMapping.getInterfaceAliasOID());
    }

    @Test
    public void testGetSysUpTimeOID() {
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid), snmpMapping.getSysUpTimeOID());
    }

    @Test
    public void testIsSysUpTimeOID() {
        String sysUpTimeOid = TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid);
        assertFalse(snmpMapping.isSysUpTimeOID(null));
        assertFalse(snmpMapping.isSysUpTimeOID(""));
        assertFalse(snmpMapping.isSysUpTimeOID(" "));
        assertFalse(snmpMapping.isSysUpTimeOID("unknown"));
        assertFalse(snmpMapping.isSysUpTimeOID(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid)));
        assertTrue(snmpMapping.isSysUpTimeOID(sysUpTimeOid));
    }

    @Test
    public void testGetLabelForInterfaceOperStatus() {
        String unknownLabel = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_unknown_label);
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_up_label), snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_up)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_down_label), snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_down)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_testing_label), snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_testing)));
        assertEquals(unknownLabel, snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_unknown)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_dormant_label), snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_dormant)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_notpresent_label), snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_notpresent)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_lowerlayerdown_label), snmpMapping.getLabelForInterfaceOperStatus(TestRegistry.getContext().getResources().getInteger(R.integer.interface_operstatus_lowerlayerdown)));
        assertEquals(unknownLabel, snmpMapping.getLabelForInterfaceOperStatus(99));
        assertEquals(unknownLabel, snmpMapping.getLabelForInterfaceOperStatus(0));
    }

    @Test
    public void testToSNMPItemsEmptyMap() {
        List<SNMPItem> result = snmpMapping.toSNMPItems(new HashMap<>(), 1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToSNMPItemsDescrOID() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceDescrOid + ".1", "eth0");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 42L);
        assertEquals(1, result.size());
        SNMPItem item = result.get(0);
        assertEquals(SNMPItemType.INTERFACEDESCR, item.getSnmpItemType());
        assertEquals(interfaceDescrOid + ".1", item.getOid());
        assertEquals("eth0", item.getName());
        assertEquals(42L, item.getNetworkTaskId());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToSNMPItemsTypeOID() {
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "6");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 42L);
        assertEquals(1, result.size());
        SNMPItem item = result.get(0);
        assertEquals(SNMPItemType.INTERFACETYPE, item.getSnmpItemType());
        assertEquals(interfaceTypeOid + ".1", item.getOid());
        assertEquals("6", item.getName());
        assertEquals(42L, item.getNetworkTaskId());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToSNMPItemsAliasOID() {
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceAliasOid + ".1", "LAN");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 42L);
        assertEquals(1, result.size());
        SNMPItem item = result.get(0);
        assertEquals(SNMPItemType.INTERFACEALIAS, item.getSnmpItemType());
        assertEquals(interfaceAliasOid + ".1", item.getOid());
        assertEquals("LAN", item.getName());
        assertEquals(42L, item.getNetworkTaskId());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToSNMPItemsUnknownOIDsFiltered() {
        String sysDescrOid = TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        Map<String, String> values = new HashMap<>();
        values.put(sysDescrOid, "some system");
        values.put(interfaceOperStatusOid + ".1", "1");
        values.put("1.2.3.4.5", "unknown");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToSNMPItemsMixed() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        String sysDescrOid = TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid);
        Map<String, String> values = new TreeMap<>();
        values.put(interfaceDescrOid + ".1", "eth0");
        values.put(interfaceTypeOid + ".1", "6");
        values.put(interfaceAliasOid + ".1", "LAN");
        values.put(sysDescrOid, "some system");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 10L);
        assertEquals(3, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
        assertEquals(interfaceDescrOid + ".1", result.get(0).getOid());
        assertEquals("eth0", result.get(0).getName());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(1).getSnmpItemType());
        assertEquals(interfaceTypeOid + ".1", result.get(1).getOid());
        assertEquals("6", result.get(1).getName());
        assertEquals(SNMPItemType.INTERFACEALIAS, result.get(2).getSnmpItemType());
        assertEquals(interfaceAliasOid + ".1", result.get(2).getOid());
        assertEquals("LAN", result.get(2).getName());
        for (SNMPItem item : result) {
            assertEquals(10L, item.getNetworkTaskId());
            assertFalse(item.isMonitored());
        }
    }

    @Test
    public void testToSNMPItemsMultipleInterfaces() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        Map<String, String> values = new TreeMap<>();
        values.put(interfaceDescrOid + ".1", "eth0");
        values.put(interfaceDescrOid + ".2", "eth1");
        values.put(interfaceTypeOid + ".1", "6");
        values.put(interfaceTypeOid + ".2", "6");
        values.put(interfaceAliasOid + ".1", "LAN");
        values.put(interfaceAliasOid + ".2", "WAN");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 5L);
        assertEquals(6, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
        assertEquals(interfaceDescrOid + ".1", result.get(0).getOid());
        assertEquals("eth0", result.get(0).getName());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(1).getSnmpItemType());
        assertEquals(interfaceDescrOid + ".2", result.get(1).getOid());
        assertEquals("eth1", result.get(1).getName());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(2).getSnmpItemType());
        assertEquals(interfaceTypeOid + ".1", result.get(2).getOid());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(3).getSnmpItemType());
        assertEquals(interfaceTypeOid + ".2", result.get(3).getOid());
        assertEquals(SNMPItemType.INTERFACEALIAS, result.get(4).getSnmpItemType());
        assertEquals(interfaceAliasOid + ".1", result.get(4).getOid());
        assertEquals("LAN", result.get(4).getName());
        assertEquals(SNMPItemType.INTERFACEALIAS, result.get(5).getSnmpItemType());
        assertEquals(interfaceAliasOid + ".2", result.get(5).getOid());
        assertEquals("WAN", result.get(5).getName());
        for (SNMPItem item : result) {
            assertEquals(5L, item.getNetworkTaskId());
            assertFalse(item.isMonitored());
        }
    }

    @Test
    public void testToSNMPItemsInvalidNameNotAdded() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceDescrOid + ".1", "eth\u0001");
        values.put(interfaceDescrOid + ".2", "eth1");
        List<SNMPItem> result = snmpMapping.toSNMPItems(values, 1L);
        assertEquals(1, result.size());
        assertEquals(interfaceDescrOid + ".2", result.get(0).getOid());
        assertEquals("eth1", result.get(0).getName());
    }

    @Test
    public void testToSNMPInterfaceInfoNullList() {
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(null, new HashMap<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToSNMPInterfaceInfoEmptyList() {
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(Collections.emptyList(), new HashMap<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToSNMPInterfaceInfoNullValues() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, null);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoEmptyValues() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoAllValuesPresent() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".63", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".63", "6");
        values.put(interfaceOperStatusOid + ".63", "1");
        values.put(interfaceAliasOid + ".63", "LAN");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".63");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals(1, info.getStatus());
        assertEquals("LAN", info.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoPartialValues() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".5", "lo"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".5", "24");
        values.put(interfaceOperStatusOid + ".5", "2");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".5");
        assertNotNull(info);
        assertEquals("lo", info.getDescr());
        assertEquals(24, info.getType());
        assertEquals(2, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoMultipleInterfaces() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> list = new ArrayList<>();
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        list.add(getSNMPItem(interfaceDescrOid + ".2", "eth1"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "6");
        values.put(interfaceOperStatusOid + ".1", "1");
        values.put(interfaceAliasOid + ".1", "LAN");
        values.put(interfaceTypeOid + ".2", "6");
        values.put(interfaceOperStatusOid + ".2", "2");
        values.put(interfaceAliasOid + ".2", "WAN");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(2, result.size());
        SNMPInterfaceInfo info1 = result.get(interfaceDescrOid + ".1");
        assertNotNull(info1);
        assertEquals("eth0", info1.getDescr());
        assertEquals(6, info1.getType());
        assertEquals(1, info1.getStatus());
        assertEquals("LAN", info1.getAlias());
        SNMPInterfaceInfo info2 = result.get(interfaceDescrOid + ".2");
        assertNotNull(info2);
        assertEquals("eth1", info2.getDescr());
        assertEquals(6, info2.getType());
        assertEquals(2, info2.getStatus());
        assertEquals("WAN", info2.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoNonDescrItemSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        SNMPItem typeItem = new SNMPItem();
        typeItem.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        typeItem.setOid(interfaceTypeOid + ".1");
        typeItem.setName("6");
        SNMPItem aliasItem = new SNMPItem();
        aliasItem.setSnmpItemType(SNMPItemType.INTERFACEALIAS);
        aliasItem.setOid(interfaceAliasOid + ".1");
        aliasItem.setName("LAN");
        List<SNMPItem> list = new ArrayList<>();
        list.add(typeItem);
        list.add(aliasItem);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToSNMPInterfaceInfoNullItemSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = new ArrayList<>();
        list.add(null);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToSNMPInterfaceInfoEmptyOIDSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        SNMPItem emptyOidItem = new SNMPItem();
        emptyOidItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        emptyOidItem.setOid("");
        emptyOidItem.setName("eth0");
        List<SNMPItem> list = new ArrayList<>();
        list.add(emptyOidItem);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth1"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToSNMPInterfaceInfoUnparseableOIDSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        SNMPItem badOidItem = new SNMPItem();
        badOidItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        badOidItem.setOid("not.a.valid.oid");
        badOidItem.setName("eth0");
        List<SNMPItem> list = new ArrayList<>();
        list.add(badOidItem);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth1"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToSNMPInterfaceInfoValuesNoMatchingIndex() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".99", "6");
        values.put(interfaceOperStatusOid + ".99", "1");
        values.put(interfaceAliasOid + ".99", "LAN");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoInvalidTypeAndStatusValue() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "invalid");
        values.put(interfaceOperStatusOid + ".1", "invalid");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
    }

    @Test
    public void testToSNMPInterfaceInfoMixedValidAndInvalid() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        SNMPItem nullOidItem = new SNMPItem();
        nullOidItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        nullOidItem.setName("broken");
        List<SNMPItem> list = new ArrayList<>();
        list.add(null);
        list.add(nullOidItem);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        list.add(getSNMPItem(interfaceDescrOid + ".2", "eth1"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "6");
        values.put(interfaceOperStatusOid + ".1", "1");
        values.put(interfaceAliasOid + ".1", "LAN");
        values.put(interfaceTypeOid + ".2", "131");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(2, result.size());
        SNMPInterfaceInfo info1 = result.get(interfaceDescrOid + ".1");
        assertNotNull(info1);
        assertEquals("eth0", info1.getDescr());
        assertEquals(6, info1.getType());
        assertEquals(1, info1.getStatus());
        assertEquals("LAN", info1.getAlias());
        SNMPInterfaceInfo info2 = result.get(interfaceDescrOid + ".2");
        assertNotNull(info2);
        assertEquals("eth1", info2.getDescr());
        assertEquals(131, info2.getType());
        assertEquals(-1, info2.getStatus());
        assertNull(info2.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoInvalidDescrSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = new ArrayList<>();
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth\u0001"));
        list.add(getSNMPItem(interfaceDescrOid + ".2", "eth1"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNull(result.get(interfaceDescrOid + ".1"));
        assertNotNull(result.get(interfaceDescrOid + ".2"));
    }

    @Test
    public void testToSNMPInterfaceInfoInvalidAliasCleared() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceAliasOid + ".1", "LAN\u0001");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertNull(info.getAlias());
    }

    @Test
    public void testToSNMPInterfaceInfoInvalidTypeCleared() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "0");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
    }

    @Test
    public void testToSNMPInterfaceInfoInvalidStatusCleared() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceOperStatusOid + ".1", "0");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toSNMPInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getStatus());
    }

    @Test
    public void testFilterDescrItemsNull() {
        List<SNMPItem> result = snmpMapping.filterDescrItems(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterDescrItemsEmpty() {
        List<SNMPItem> result = snmpMapping.filterDescrItems(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterDescrItemsMixed() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String aliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> allItems = new ArrayList<>();
        allItems.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        allItems.add(createTypeItem(20, 1, typeOid + ".1", "6"));
        allItems.add(createAliasItem(30, 1, aliasOid + ".1", "LAN"));
        allItems.add(createDescrItem(11, 1, descrOid + ".2", "lo", false));
        List<SNMPItem> result = snmpMapping.filterDescrItems(allItems);
        assertEquals(2, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
        assertEquals("eth0", result.get(0).getName());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(1).getSnmpItemType());
        assertEquals("lo", result.get(1).getName());
    }

    @Test
    public void testExtractSNMPInterfaceInfosNull() {
        Map<String, SNMPInterfaceInfo> result = snmpMapping.extractSNMPInterfaceInfos(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractSNMPInterfaceInfosEmpty() {
        Map<String, SNMPInterfaceInfo> result = snmpMapping.extractSNMPInterfaceInfos(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractSNMPInterfaceInfosOnlyDescr() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> allItems = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.extractSNMPInterfaceInfos(allItems);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(descrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testExtractSNMPInterfaceInfosWithTypeAndAlias() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String aliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> allItems = new ArrayList<>();
        allItems.add(createDescrItem(10, 1, descrOid + ".3", "eth0", false));
        allItems.add(createTypeItem(20, 1, typeOid + ".3", "6"));
        allItems.add(createAliasItem(30, 1, aliasOid + ".3", "LAN"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.extractSNMPInterfaceInfos(allItems);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(descrOid + ".3");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals("LAN", info.getAlias());
    }

    @Test
    public void testExtractSNMPInterfaceInfosMultipleInterfaces() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String aliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> allItems = new ArrayList<>();
        allItems.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        allItems.add(createTypeItem(20, 1, typeOid + ".1", "6"));
        allItems.add(createAliasItem(30, 1, aliasOid + ".1", "LAN"));
        allItems.add(createDescrItem(11, 1, descrOid + ".2", "lo", false));
        allItems.add(createTypeItem(21, 1, typeOid + ".2", "24"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.extractSNMPInterfaceInfos(allItems);
        assertEquals(2, result.size());
        SNMPInterfaceInfo info1 = result.get(descrOid + ".1");
        assertNotNull(info1);
        assertEquals("eth0", info1.getDescr());
        assertEquals(6, info1.getType());
        assertEquals("LAN", info1.getAlias());
        SNMPInterfaceInfo info2 = result.get(descrOid + ".2");
        assertNotNull(info2);
        assertEquals("lo", info2.getDescr());
        assertEquals(24, info2.getType());
        assertNull(info2.getAlias());
    }

    @Test
    public void testExtractSNMPInterfaceInfosNullOrEmptyOIDSkipped() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> allItems = new ArrayList<>();
        allItems.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        SNMPItem nullOid = createTypeItem(20, 1, null, "6");
        SNMPItem emptyOid = createTypeItem(21, 1, "", "6");
        SNMPItem nullName = createTypeItem(22, 1, typeOid + ".1", null);
        allItems.add(nullOid);
        allItems.add(emptyOid);
        allItems.add(nullName);
        Map<String, SNMPInterfaceInfo> result = snmpMapping.extractSNMPInterfaceInfos(allItems);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(descrOid + ".1");
        assertNotNull(info);
        assertEquals(-1, info.getType());
    }

    @Test
    public void testMergeDescrItemsBothEmpty() {
        SNMPItemMergeResult result = snmpMapping.mergeDescrItems(Collections.emptyList(), Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.mergedItems().isEmpty());
        assertTrue(result.removedMonitoredItems().isEmpty());
    }

    @Test
    public void testMergeDescrItemsExistingEmpty() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> scanned = new ArrayList<>();
        scanned.add(createDescrItem(-1, 5, descrOid + ".1", "eth0", false));
        scanned.add(createDescrItem(-1, 5, descrOid + ".2", "lo", false));
        SNMPItemMergeResult result = snmpMapping.mergeDescrItems(Collections.emptyList(), scanned);
        assertEquals(2, result.mergedItems().size());
        assertTrue(result.removedMonitoredItems().isEmpty());
        assertEquals("eth0", result.mergedItems().get(0).getName());
        assertEquals("lo", result.mergedItems().get(1).getName());
    }

    @Test
    public void testMergeDescrItemsScannedEmptyMonitoredRemoved() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> existing = new ArrayList<>();
        existing.add(createDescrItem(10, 1, descrOid + ".1", "eth0", true));
        existing.add(createDescrItem(11, 1, descrOid + ".2", "lo", false));
        SNMPItemMergeResult result = snmpMapping.mergeDescrItems(existing, Collections.emptyList());
        assertTrue(result.mergedItems().isEmpty());
        assertEquals(1, result.removedMonitoredItems().size());
        assertEquals("eth0", result.removedMonitoredItems().get(0).getName());
    }

    @Test
    public void testMergeDescrItemsSameNamesPreservesIdAndMonitored() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> existing = new ArrayList<>();
        existing.add(createDescrItem(10, 1, descrOid + ".5", "eth0", true));
        List<SNMPItem> scanned = new ArrayList<>();
        scanned.add(createDescrItem(-1, 1, descrOid + ".3", "eth0", false));
        SNMPItemMergeResult result = snmpMapping.mergeDescrItems(existing, scanned);
        assertEquals(1, result.mergedItems().size());
        assertTrue(result.removedMonitoredItems().isEmpty());
        SNMPItem merged = result.mergedItems().get(0);
        assertEquals(10, merged.getId());
        assertEquals(descrOid + ".3", merged.getOid());
        assertTrue(merged.isMonitored());
    }

    @Test
    public void testMergeDescrItemsNewAndRemovedMixed() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> existing = new ArrayList<>();
        existing.add(createDescrItem(10, 1, descrOid + ".1", "eth0", true));
        existing.add(createDescrItem(11, 1, descrOid + ".2", "lo", false));
        List<SNMPItem> scanned = new ArrayList<>();
        scanned.add(createDescrItem(-1, 1, descrOid + ".1", "eth0", false));
        scanned.add(createDescrItem(-1, 1, descrOid + ".3", "wlan0", false));
        SNMPItemMergeResult result = snmpMapping.mergeDescrItems(existing, scanned);
        assertEquals(2, result.mergedItems().size());
        assertTrue(result.removedMonitoredItems().isEmpty());
        assertEquals("eth0", result.mergedItems().get(0).getName());
        assertEquals(10, result.mergedItems().get(0).getId());
        assertEquals("wlan0", result.mergedItems().get(1).getName());
        assertEquals(-1, result.mergedItems().get(1).getId());
    }

    @Test
    public void testMergeDescrItemsSortedByName() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> scanned = new ArrayList<>();
        scanned.add(createDescrItem(-1, 1, descrOid + ".3", "wlan0", false));
        scanned.add(createDescrItem(-1, 1, descrOid + ".1", "eth0", false));
        scanned.add(createDescrItem(-1, 1, descrOid + ".2", "lo", false));
        SNMPItemMergeResult result = snmpMapping.mergeDescrItems(Collections.emptyList(), scanned);
        assertEquals(3, result.mergedItems().size());
        assertEquals("eth0", result.mergedItems().get(0).getName());
        assertEquals("lo", result.mergedItems().get(1).getName());
        assertEquals("wlan0", result.mergedItems().get(2).getName());
    }

    @Test
    public void testMergeAllSNMPItemsEmptyOriginal() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> newDescr = List.of(createDescrItem(-1, 5, descrOid + ".1", "eth0", false));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".1", createInterfaceInfo(6, 1, null));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(Collections.emptyList(), newDescr, newInfos, 5);
        assertEquals(2, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
        assertEquals(-1, result.get(0).getId());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(1).getSnmpItemType());
        assertEquals(typeOid + ".1", result.get(1).getOid());
        assertEquals("6", result.get(1).getName());
        assertEquals(-1, result.get(1).getId());
    }

    @Test
    public void testMergeAllSNMPItemsDescrRemoved() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String aliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> originalAll = new ArrayList<>();
        originalAll.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        originalAll.add(createTypeItem(20, 1, typeOid + ".1", "6"));
        originalAll.add(createAliasItem(30, 1, aliasOid + ".1", "LAN"));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, Collections.emptyList(), Collections.emptyMap(), 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMergeAllSNMPItemsOIDIndexChanged() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String aliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> originalAll = new ArrayList<>();
        originalAll.add(createDescrItem(10, 1, descrOid + ".5", "eth0", false));
        originalAll.add(createTypeItem(20, 1, typeOid + ".5", "6"));
        originalAll.add(createAliasItem(30, 1, aliasOid + ".5", "LAN"));
        List<SNMPItem> editedDescr = List.of(createDescrItem(10, 1, descrOid + ".3", "eth0", false));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".3", createInterfaceInfo(6, 1, "LAN"));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, newInfos, 1);
        assertEquals(3, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(descrOid + ".3", result.get(0).getOid());
        assertEquals(20, result.get(1).getId());
        assertEquals(typeOid + ".3", result.get(1).getOid());
        assertEquals("6", result.get(1).getName());
        assertEquals(30, result.get(2).getId());
        assertEquals(aliasOid + ".3", result.get(2).getOid());
        assertEquals("LAN", result.get(2).getName());
    }

    @Test
    public void testMergeAllSNMPItemsMonitoredFlagUpdated() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> originalAll = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        List<SNMPItem> editedDescr = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", true));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, Collections.emptyMap(), 1);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertTrue(result.get(0).isMonitored());
    }

    @Test
    public void testMergeAllSNMPItemsTypeDisappearedFromScan() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> originalAll = new ArrayList<>();
        originalAll.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        originalAll.add(createTypeItem(20, 1, typeOid + ".1", "6"));
        List<SNMPItem> editedDescr = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".1", createInterfaceInfo(-1, -1, null));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, newInfos, 1);
        assertEquals(1, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
    }

    @Test
    public void testMergeAllSNMPItemsNewTypeForExistingDescr() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> originalAll = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        List<SNMPItem> editedDescr = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".1", createInterfaceInfo(6, 1, null));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, newInfos, 1);
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(1).getSnmpItemType());
        assertEquals(typeOid + ".1", result.get(1).getOid());
        assertEquals("6", result.get(1).getName());
        assertEquals(-1, result.get(1).getId());
    }

    @Test
    public void testMergeAllSNMPItemsNewDescrWithTypeAndAlias() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String aliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> editedDescr = List.of(createDescrItem(-1, 5, descrOid + ".2", "eth0", false));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".2", createInterfaceInfo(6, 1, "LAN"));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(Collections.emptyList(), editedDescr, newInfos, 5);
        assertEquals(3, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
        assertEquals(-1, result.get(0).getId());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(1).getSnmpItemType());
        assertEquals(typeOid + ".2", result.get(1).getOid());
        assertEquals(SNMPItemType.INTERFACEALIAS, result.get(2).getSnmpItemType());
        assertEquals(aliasOid + ".2", result.get(2).getOid());
        assertEquals("LAN", result.get(2).getName());
        assertEquals(5, result.get(1).getNetworkTaskId());
        assertEquals(5, result.get(2).getNetworkTaskId());
    }

    @Test
    public void testMergeAllSNMPItemsOriginalOrderPreserved() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> originalAll = new ArrayList<>();
        originalAll.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        originalAll.add(createTypeItem(20, 1, typeOid + ".1", "6"));
        originalAll.add(createDescrItem(11, 1, descrOid + ".2", "lo", false));
        originalAll.add(createTypeItem(21, 1, typeOid + ".2", "24"));
        List<SNMPItem> editedDescr = new ArrayList<>();
        editedDescr.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        editedDescr.add(createDescrItem(11, 1, descrOid + ".2", "lo", false));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".1", createInterfaceInfo(6, 1, null));
        newInfos.put(descrOid + ".2", createInterfaceInfo(24, 1, null));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, newInfos, 1);
        assertEquals(4, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
        assertEquals(20, result.get(1).getId());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(1).getSnmpItemType());
        assertEquals(11, result.get(2).getId());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(2).getSnmpItemType());
        assertEquals(21, result.get(3).getId());
        assertEquals(SNMPItemType.INTERFACETYPE, result.get(3).getSnmpItemType());
    }

    @Test
    public void testMergeAllSNMPItemsNoMutationOfOriginals() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        SNMPItem originalDescr = createDescrItem(10, 1, descrOid + ".5", "eth0", false);
        SNMPItem originalType = createTypeItem(20, 1, typeOid + ".5", "6");
        List<SNMPItem> originalAll = new ArrayList<>();
        originalAll.add(originalDescr);
        originalAll.add(originalType);
        List<SNMPItem> editedDescr = List.of(createDescrItem(10, 1, descrOid + ".3", "eth0", true));
        Map<String, SNMPInterfaceInfo> newInfos = new HashMap<>();
        newInfos.put(descrOid + ".3", createInterfaceInfo(24, 1, null));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, newInfos, 1);
        assertEquals(descrOid + ".5", originalDescr.getOid());
        assertFalse(originalDescr.isMonitored());
        assertEquals(typeOid + ".5", originalType.getOid());
        assertEquals(descrOid + ".3", result.get(0).getOid());
        assertTrue(result.get(0).isMonitored());
        assertEquals(typeOid + ".3", result.get(1).getOid());
        assertEquals("24", result.get(1).getName());
    }

    @Test
    public void testMergeAllSNMPItemsNewInfosNull() {
        String descrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String typeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        List<SNMPItem> originalAll = new ArrayList<>();
        originalAll.add(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        originalAll.add(createTypeItem(20, 1, typeOid + ".1", "6"));
        List<SNMPItem> editedDescr = List.of(createDescrItem(10, 1, descrOid + ".1", "eth0", false));
        List<SNMPItem> result = snmpMapping.mergeAllSNMPItems(originalAll, editedDescr, null, 1);
        assertEquals(1, result.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, result.get(0).getSnmpItemType());
    }

    @Test
    public void testMergeSNMPInterfaceInfosScannedNull() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMergeSNMPInterfaceInfosScannedEmpty() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, new HashMap<>());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMergeSNMPInterfaceInfosExistingNull() {
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(null, scanned);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get("1.1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals(1, info.getStatus());
        assertEquals("alias0", info.getAlias());
    }

    @Test
    public void testMergeSNMPInterfaceInfosExistingEmpty() {
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(new HashMap<>(), scanned);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get("1.1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
    }

    @Test
    public void testMergeSNMPInterfaceInfosInterfaceRemoved() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.2", createInterfaceInfoWithDescr("eth1", 6, 1, "alias1"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(1, result.size());
        assertNull(result.get("1.1"));
        assertNotNull(result.get("1.2"));
    }

    @Test
    public void testMergeSNMPInterfaceInfosNewInterface() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get("1.1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals(1, info.getStatus());
        assertEquals("alias0", info.getAlias());
    }

    @Test
    public void testMergeSNMPInterfaceInfosOIDChanged() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.5", createInterfaceInfoWithDescr("eth0", 6, 2, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(1, result.size());
        assertNull(result.get("1.1"));
        assertNotNull(result.get("1.5"));
        assertEquals("eth0", result.get("1.5").getDescr());
    }

    @Test
    public void testMergeSNMPInterfaceInfosStatusFromScannedUsed() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 2, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(2, Objects.requireNonNull(result.get("1.1")).getStatus());
    }

    @Test
    public void testMergeSNMPInterfaceInfosStatusClearedWhenNotInScanned() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, -1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(-1, Objects.requireNonNull(result.get("1.1")).getStatus());
    }

    @Test
    public void testMergeSNMPInterfaceInfosAliasPreservedWhenScannedNull() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "oldAlias"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, null));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals("oldAlias", Objects.requireNonNull(result.get("1.1")).getAlias());
    }

    @Test
    public void testMergeSNMPInterfaceInfosAliasOverwrittenByEmptyString() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "oldAlias"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, ""));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals("", Objects.requireNonNull(result.get("1.1")).getAlias());
    }

    @Test
    public void testMergeSNMPInterfaceInfosAliasOverwrittenByScanned() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "oldAlias"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "newAlias"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals("newAlias", Objects.requireNonNull(result.get("1.1")).getAlias());
    }

    @Test
    public void testMergeSNMPInterfaceInfosTypePreservedWhenScannedNegative() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", -1, 1, null));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(6, Objects.requireNonNull(result.get("1.1")).getType());
    }

    @Test
    public void testMergeSNMPInterfaceInfosTypeOverwrittenByScanned() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", 131, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(131, Objects.requireNonNull(result.get("1.1")).getType());
    }

    @Test
    public void testMergeSNMPInterfaceInfosDescrNullInScanned() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        SNMPInterfaceInfo noDescr = createInterfaceInfoWithDescr(null, 6, 1, "alias1");
        scanned.put("1.2", noDescr);
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(1, result.size());
        assertNotNull(result.get("1.2"));
        assertNull(Objects.requireNonNull(result.get("1.2")).getDescr());
    }

    @Test
    public void testMergeSNMPInterfaceInfosMultipleInterfaces() {
        Map<String, SNMPInterfaceInfo> existing = new HashMap<>();
        existing.put("1.1", createInterfaceInfoWithDescr("eth0", 6, 1, "alias0"));
        existing.put("1.2", createInterfaceInfoWithDescr("eth1", 6, 1, "alias1"));
        existing.put("1.3", createInterfaceInfoWithDescr("eth2", 6, 1, "alias2"));
        Map<String, SNMPInterfaceInfo> scanned = new HashMap<>();
        scanned.put("1.1", createInterfaceInfoWithDescr("eth0", -1, -1, null));
        scanned.put("1.10", createInterfaceInfoWithDescr("eth2", 131, 2, "newAlias2"));
        scanned.put("1.4", createInterfaceInfoWithDescr("eth3", 6, 1, "alias3"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.mergeSNMPInterfaceInfos(existing, scanned);
        assertEquals(3, result.size());
        assertNull(result.get("1.2"));
        SNMPInterfaceInfo eth0 = result.get("1.1");
        assertNotNull(eth0);
        assertEquals(6, eth0.getType());
        assertEquals(-1, eth0.getStatus());
        assertEquals("alias0", eth0.getAlias());
        SNMPInterfaceInfo eth2 = result.get("1.10");
        assertNotNull(eth2);
        assertEquals(131, eth2.getType());
        assertEquals(2, eth2.getStatus());
        assertEquals("newAlias2", eth2.getAlias());
        SNMPInterfaceInfo eth3 = result.get("1.4");
        assertNotNull(eth3);
        assertEquals("eth3", eth3.getDescr());
        assertEquals(6, eth3.getType());
    }

    private SNMPItem getSNMPItem(String oidString, String name) {
        SNMPItem item = new SNMPItem();
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setOid(oidString);
        item.setName(name);
        return item;
    }

    private SNMPItem createDescrItem(long id, long networkTaskId, String oid, String name, boolean monitored) {
        SNMPItem item = new SNMPItem();
        item.setId(id);
        item.setNetworkTaskId(networkTaskId);
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setOid(oid);
        item.setName(name);
        item.setMonitored(monitored);
        return item;
    }

    @SuppressWarnings("SameParameterValue")
    private SNMPItem createTypeItem(long id, long networkTaskId, String oid, String name) {
        SNMPItem item = new SNMPItem();
        item.setId(id);
        item.setNetworkTaskId(networkTaskId);
        item.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        item.setOid(oid);
        item.setName(name);
        return item;
    }

    @SuppressWarnings("SameParameterValue")
    private SNMPItem createAliasItem(long id, long networkTaskId, String oid, String name) {
        SNMPItem item = new SNMPItem();
        item.setId(id);
        item.setNetworkTaskId(networkTaskId);
        item.setSnmpItemType(SNMPItemType.INTERFACEALIAS);
        item.setOid(oid);
        item.setName(name);
        return item;
    }

    private SNMPInterfaceInfo createInterfaceInfo(int type, int status, String alias) {
        SNMPInterfaceInfo info = new SNMPInterfaceInfo();
        info.setType(type);
        info.setStatus(status);
        info.setAlias(alias);
        return info;
    }

    private SNMPInterfaceInfo createInterfaceInfoWithDescr(String descr, int type, int status, String alias) {
        SNMPInterfaceInfo info = new SNMPInterfaceInfo();
        info.setDescr(descr);
        info.setType(type);
        info.setStatus(status);
        info.setAlias(alias);
        return info;
    }
}
