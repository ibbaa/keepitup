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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPMappingTest {

    private SNMPMapping snmpMapping;

    @Before
    public void beforeEachTestMethod() {
        snmpMapping = new SNMPMapping(TestRegistry.getContext());
    }

    @Test
    public void testSupportsOID() {
        assertFalse(snmpMapping.supportsOID(null));
        assertFalse(snmpMapping.supportsOID("unknown"));
        assertTrue(snmpMapping.supportsOID(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid)));
        assertTrue(snmpMapping.supportsOID(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid)));
        assertTrue(snmpMapping.supportsOID(TestRegistry.getContext().getResources().getString(R.string.sys_object_id_oid)));
        assertTrue(snmpMapping.supportsOID(TestRegistry.getContext().getResources().getString(R.string.sys_contact_oid)));
        assertTrue(snmpMapping.supportsOID(TestRegistry.getContext().getResources().getString(R.string.sys_name_oid)));
        assertTrue(snmpMapping.supportsOID(TestRegistry.getContext().getResources().getString(R.string.sys_location_oid)));
    }

    @Test
    public void testGetLabelForOID() {
        assertNull(snmpMapping.getLabelForOID(null));
        assertNull(snmpMapping.getLabelForOID("unknown"));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_descr_label), snmpMapping.getLabelForOID(TestRegistry.getContext().getResources().getString(R.string.sys_descr_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_label), snmpMapping.getLabelForOID(TestRegistry.getContext().getResources().getString(R.string.sys_uptime_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_object_id_label), snmpMapping.getLabelForOID(TestRegistry.getContext().getResources().getString(R.string.sys_object_id_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_contact_label), snmpMapping.getLabelForOID(TestRegistry.getContext().getResources().getString(R.string.sys_contact_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_name_label), snmpMapping.getLabelForOID(TestRegistry.getContext().getResources().getString(R.string.sys_name_oid)));
        assertEquals(TestRegistry.getContext().getResources().getString(R.string.sys_location_label), snmpMapping.getLabelForOID(TestRegistry.getContext().getResources().getString(R.string.sys_location_oid)));
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
}
