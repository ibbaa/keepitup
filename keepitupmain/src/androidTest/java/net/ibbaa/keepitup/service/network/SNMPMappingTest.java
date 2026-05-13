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
    public void testToSNMPInterfaceItemsEmptyMap() {
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(new HashMap<>(), 1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToSNMPInterfaceItemsDescrOID() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceDescrOid + ".1", "eth0");
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(values, 42L);
        assertEquals(1, result.size());
        SNMPItem item = result.get(0);
        assertEquals(SNMPItemType.INTERFACEDESCR, item.getSnmpItemType());
        assertEquals(interfaceDescrOid + ".1", item.getOid());
        assertEquals("eth0", item.getName());
        assertEquals(42L, item.getNetworkTaskId());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToSNMPInterfaceItemsTypeOID() {
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "6");
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(values, 42L);
        assertEquals(1, result.size());
        SNMPItem item = result.get(0);
        assertEquals(SNMPItemType.INTERFACETYPE, item.getSnmpItemType());
        assertEquals(interfaceTypeOid + ".1", item.getOid());
        assertEquals("6", item.getName());
        assertEquals(42L, item.getNetworkTaskId());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToSNMPInterfaceItemsAliasOID() {
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        Map<String, String> values = new HashMap<>();
        values.put(interfaceAliasOid + ".1", "LAN");
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(values, 42L);
        assertEquals(1, result.size());
        SNMPItem item = result.get(0);
        assertEquals(SNMPItemType.INTERFACEALIAS, item.getSnmpItemType());
        assertEquals(interfaceAliasOid + ".1", item.getOid());
        assertEquals("LAN", item.getName());
        assertEquals(42L, item.getNetworkTaskId());
        assertFalse(item.isMonitored());
    }

    @Test
    public void testToSNMPInterfaceItemsUnknownOIDsFiltered() {
        String sysDescrOid = TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        Map<String, String> values = new HashMap<>();
        values.put(sysDescrOid, "some system");
        values.put(interfaceOperStatusOid + ".1", "1");
        values.put("1.2.3.4.5", "unknown");
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(values, 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToSNMPInterfaceItemsMixed() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        String sysDescrOid = TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid);
        Map<String, String> values = new TreeMap<>();
        values.put(interfaceDescrOid + ".1", "eth0");
        values.put(interfaceTypeOid + ".1", "6");
        values.put(interfaceAliasOid + ".1", "LAN");
        values.put(sysDescrOid, "some system");
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(values, 10L);
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
    public void testToSNMPInterfaceItemsMultipleInterfaces() {
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
        List<SNMPItem> result = snmpMapping.toSNMPInterfaceItems(values, 5L);
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
    public void testToInterfaceInfoNullList() {
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(null, new HashMap<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToInterfaceInfoEmptyList() {
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(Collections.emptyList(), new HashMap<>());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToInterfaceInfoNullValues() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, null);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToInterfaceInfoEmptyValues() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToInterfaceInfoAllValuesPresent() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".63", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".63", "6");
        values.put(interfaceOperStatusOid + ".63", "1");
        values.put(interfaceAliasOid + ".63", "LAN");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".63");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(6, info.getType());
        assertEquals(1, info.getStatus());
        assertEquals("LAN", info.getAlias());
    }

    @Test
    public void testToInterfaceInfoPartialValues() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".5", "lo"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".5", "24");
        values.put(interfaceOperStatusOid + ".5", "2");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".5");
        assertNotNull(info);
        assertEquals("lo", info.getDescr());
        assertEquals(24, info.getType());
        assertEquals(2, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToInterfaceInfoMultipleInterfaces() {
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
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, values);
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
    public void testToInterfaceInfoNonDescrItemSkipped() {
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
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToInterfaceInfoNullItemSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        List<SNMPItem> list = new ArrayList<>();
        list.add(null);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToInterfaceInfoEmptyOIDSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        SNMPItem emptyOidItem = new SNMPItem();
        emptyOidItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        emptyOidItem.setOid("");
        emptyOidItem.setName("eth0");
        List<SNMPItem> list = new ArrayList<>();
        list.add(emptyOidItem);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth1"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToInterfaceInfoUnparseableOIDSkipped() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        SNMPItem badOidItem = new SNMPItem();
        badOidItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        badOidItem.setOid("not.a.valid.oid");
        badOidItem.setName("eth0");
        List<SNMPItem> list = new ArrayList<>();
        list.add(badOidItem);
        list.add(getSNMPItem(interfaceDescrOid + ".1", "eth1"));
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, new HashMap<>());
        assertEquals(1, result.size());
        assertNotNull(result.get(interfaceDescrOid + ".1"));
    }

    @Test
    public void testToInterfaceInfoValuesNoMatchingIndex() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        String interfaceAliasOid = TestRegistry.getContext().getResources().getString(R.string.interface_alias_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".99", "6");
        values.put(interfaceOperStatusOid + ".99", "1");
        values.put(interfaceAliasOid + ".99", "LAN");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
        assertNull(info.getAlias());
    }

    @Test
    public void testToInterfaceInfoInvalidTypeAndStatusValue() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        List<SNMPItem> list = List.of(getSNMPItem(interfaceDescrOid + ".1", "eth0"));
        Map<String, String> values = new HashMap<>();
        values.put(interfaceTypeOid + ".1", "invalid");
        values.put(interfaceOperStatusOid + ".1", "invalid");
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, values);
        assertEquals(1, result.size());
        SNMPInterfaceInfo info = result.get(interfaceDescrOid + ".1");
        assertNotNull(info);
        assertEquals("eth0", info.getDescr());
        assertEquals(-1, info.getType());
        assertEquals(-1, info.getStatus());
    }

    @Test
    public void testToInterfaceInfoMixedValidAndInvalid() {
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
        Map<String, SNMPInterfaceInfo> result = snmpMapping.toInterfaceInfo(list, values);
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

    private SNMPItem getSNMPItem(String oidString, String name) {
        SNMPItem item = new SNMPItem();
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setOid(oidString);
        item.setName(name);
        return item;
    }
}
