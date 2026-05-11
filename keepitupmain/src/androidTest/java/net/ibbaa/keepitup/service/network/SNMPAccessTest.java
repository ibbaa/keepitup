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
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPAccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@MediumTest
@RunWith(AndroidJUnit4.class)
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
public class SNMPAccessTest {

    private TestSNMPAccess snmpAccess;

    @Before
    public void beforeEachTestMethod() {
        snmpAccess = new TestSNMPAccess(TestRegistry.getContext(), InetAddress.getLoopbackAddress(), 161, SNMPVersion.V2C, "public", false);
    }

    @Test
    public void testWalkSystemEmptySubtree() {
        snmpAccess.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(TestRegistry.getContext().getString(R.string.text_snmp_no_response), result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemNoResults() {
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(buildSysUpTimeErrorMessage(), result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemOnlyUnknownOIDs() {
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put("1.2.3.4.5.0", new OctetString("unknown value"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(buildSysUpTimeErrorMessage(), result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemMissingSysUpTime() {
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysDescrOid, new OctetString("Test system"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertEquals(1, result.result().size());
        assertEquals("Test system", result.result().get(sysDescrOid));
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(buildSysUpTimeErrorMessage(), result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemSuccessOnlySysUpTime() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(12345));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals(String.valueOf(12345L), result.result().get(sysUpTimeOid));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkSystemSuccessWithMultipleOIDs() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        String sysNameOid = TestRegistry.getContext().getString(R.string.sys_name_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(99999));
        subtreeResults.put(sysDescrOid, new OctetString("Test system"));
        subtreeResults.put(sysNameOid, new OctetString("router01"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertTrue(result.success());
        assertEquals(3, result.result().size());
        assertEquals(String.valueOf(99999L), result.result().get(sysUpTimeOid));
        assertEquals("Test system", result.result().get(sysDescrOid));
        assertEquals("router01", result.result().get(sysNameOid));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkSystemSuccessFiltersUnknownOIDs() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(5000));
        subtreeResults.put("1.2.3.4.5.0", new OctetString("unknown"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals(String.valueOf(5000L), result.result().get(sysUpTimeOid));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkSystemWithSubtreeErrors() {
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysDescrOid, new OctetString("Test system"));
        snmpAccess.setSubtreeResults(subtreeResults);
        snmpAccess.setSubtreeErrors(List.of("Error 1", "Error 2"));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertEquals(1, result.result().size());
        assertEquals("Test system", result.result().get(sysDescrOid));
        assertNull(result.exception());
        assertEquals(2, result.errorMessages().size());
        assertEquals("Error 1", result.errorMessages().get(0));
        assertEquals("Error 2", result.errorMessages().get(1));
    }

    @Test
    public void testWalkSystemWithSubtreeErrorsSkipsSysUpTimeCheck() {
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysDescrOid, new OctetString("Test system"));
        snmpAccess.setSubtreeResults(subtreeResults);
        snmpAccess.setSubtreeErrors(List.of("SNMP error"));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemWithSubtreeErrorsAndNoResults() {
        snmpAccess.setSubtreeErrors(List.of("SNMP timeout"));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP timeout", result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemException() {
        snmpAccess.setSubtreeException(new RuntimeException("Test exception"));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNotNull(result.exception());
        assertEquals("Test exception", result.exception().getMessage());
        assertEquals(1, result.errorMessages().size());
        assertEquals("Test exception", result.errorMessages().get(0));
    }

    @Test
    public void testWalkSystemExceptionWithNullMessage() {
        snmpAccess.setSubtreeException(new RuntimeException((String) null));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNotNull(result.exception());
        assertNull(result.exception().getMessage());
        assertEquals(1, result.errorMessages().size());
        assertEquals("", result.errorMessages().get(0));
    }

    @Test
    public void testWalkWithDummyFilterAcceptsAll() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(5000));
        subtreeResults.put("1.2.3.4.5.0", new OctetString("some value"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walk("1.2.3", results -> {
            Map<String, String> filtered = new TreeMap<>();
            for (Map.Entry<String, Variable> entry : results.entrySet()) {
                filtered.put(entry.getKey(), entry.getValue().toString());
            }
            return filtered;
        });
        assertTrue(result.success());
        assertEquals(2, result.result().size());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkWithDummyFilterRejectsAll() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(5000));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walk("1.2.3", results -> Collections.emptyMap());
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesEmptySubtreeOnFirstWalk() {
        snmpAccess.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(TestRegistry.getContext().getString(R.string.text_snmp_no_response), result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesEmptySubtreeOnSecondWalk() {
        snmpAccess.setSubtreeEmptySequence(List.of(false, true));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(TestRegistry.getContext().getString(R.string.text_snmp_no_response), result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesEmptySubtreeOnThirdWalk() {
        snmpAccess.setSubtreeEmptySequence(List.of(false, false, true));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(TestRegistry.getContext().getString(R.string.text_snmp_no_response), result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesSuccessNoResults() {
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesSuccessCombinesResults() {
        String interfaceDescrOid = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getString(R.string.interface_type_oid);
        String interfaceAliasOid = TestRegistry.getContext().getString(R.string.interface_alias_oid);
        Map<String, Variable> descrResults = new HashMap<>();
        descrResults.put(interfaceDescrOid + ".1", new OctetString("eth0"));
        descrResults.put(interfaceDescrOid + ".2", new OctetString("eth1"));
        Map<String, Variable> typeResults = new HashMap<>();
        typeResults.put(interfaceTypeOid + ".1", new OctetString("6"));
        typeResults.put(interfaceTypeOid + ".2", new OctetString("53"));
        Map<String, Variable> aliasResults = new HashMap<>();
        aliasResults.put(interfaceAliasOid + ".1", new OctetString("LAN"));
        aliasResults.put(interfaceAliasOid + ".2", new OctetString("WAN"));
        snmpAccess.setSubtreeResultsSequence(List.of(descrResults, typeResults, aliasResults));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertTrue(result.success());
        assertEquals(6, result.result().size());
        assertEquals("eth0", result.result().get(interfaceDescrOid + ".1"));
        assertEquals("eth1", result.result().get(interfaceDescrOid + ".2"));
        assertEquals("6", result.result().get(interfaceTypeOid + ".1"));
        assertEquals("53", result.result().get(interfaceTypeOid + ".2"));
        assertEquals("LAN", result.result().get(interfaceAliasOid + ".1"));
        assertEquals("WAN", result.result().get(interfaceAliasOid + ".2"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesWithErrorsOnFirstWalk() {
        snmpAccess.setSubtreeErrors(List.of("SNMP error"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesWithErrorsOnSecondWalk() {
        snmpAccess.setSubtreeErrorsSequence(List.of(Collections.emptyList(), List.of("SNMP error")));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesWithErrorsOnThirdWalk() {
        snmpAccess.setSubtreeErrorsSequence(List.of(Collections.emptyList(), Collections.emptyList(), List.of("SNMP error")));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesException() {
        snmpAccess.setSubtreeException(new RuntimeException("Test exception"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfaces();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNotNull(result.exception());
        assertEquals("Test exception", result.exception().getMessage());
        assertEquals(1, result.errorMessages().size());
        assertEquals("Test exception", result.errorMessages().get(0));
    }

    private String buildSysUpTimeErrorMessage() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String sysUpTimeLabelShort = TestRegistry.getContext().getString(R.string.sys_uptime_label_short);
        return TestRegistry.getContext().getString(R.string.text_snmp_mandatory_oid_missing, sysUpTimeLabelShort + " (" + sysUpTimeOid + ")");
    }
}
