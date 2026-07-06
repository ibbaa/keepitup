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
import net.ibbaa.keepitup.model.SNMPAuthInfo;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPAccess;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@MediumTest
@RunWith(AndroidJUnit4.class)
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
public class SNMPAccessTest {

    private TestSNMPAccess snmpAccess;

    @Before
    public void beforeEachTestMethod() {
        SNMPAccess.resetSharedUSMState();
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setCommunity("public");
        snmpAccess = new TestSNMPAccess(TestRegistry.getContext(), InetAddress.getLoopbackAddress(), 161, SNMPVersion.V2C, SNMPTransport.UDP, authInfo, false);
    }

    @After
    public void afterEachTestMethod() {
        snmpAccess.close();
        SNMPAccess.resetSharedUSMState();
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
    public void testWalkSystemAddsHrSysUpTimeWhenAvailable() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String hrSysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_hr_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(100));
        snmpAccess.setSubtreeResults(subtreeResults);
        snmpAccess.setSingleOIDResult(new SNMPAccess.SingleOIDResult(false, new TimeTicks(999999)));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertTrue(result.success());
        assertEquals(2, result.result().size());
        assertEquals(String.valueOf(100L), result.result().get(sysUpTimeOid));
        assertEquals(String.valueOf(999999L), result.result().get(hrSysUpTimeOid));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
        assertEquals(1, snmpAccess.getGetSingleOIDCallCount());
    }

    @Test
    public void testWalkSystemNoHrSysUpTimeWhenNotSupportedByDevice() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String hrSysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_hr_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(100));
        snmpAccess.setSubtreeResults(subtreeResults);
        snmpAccess.setSingleOIDResult(new SNMPAccess.SingleOIDResult(false, null));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals(String.valueOf(100L), result.result().get(sysUpTimeOid));
        assertNull(result.result().get(hrSysUpTimeOid));
        assertTrue(result.errorMessages().isEmpty());
        assertEquals(1, snmpAccess.getGetSingleOIDCallCount());
    }

    @Test
    public void testWalkSystemFailsOnHrSysUpTimeNetworkProblem() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        String hrSysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_hr_uptime_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysUpTimeOid, new TimeTicks(100));
        subtreeResults.put(sysDescrOid, new OctetString("Test system"));
        snmpAccess.setSubtreeResults(subtreeResults);
        snmpAccess.setSingleOIDResult(new SNMPAccess.SingleOIDResult(true, null));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertEquals(1, result.result().size());
        assertEquals("Test system", result.result().get(sysDescrOid));
        assertNull(result.result().get(sysUpTimeOid));
        assertNull(result.result().get(hrSysUpTimeOid));
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(TestRegistry.getContext().getString(R.string.text_snmp_uptime_no_response), result.errorMessages().get(0));
        assertEquals(1, snmpAccess.getGetSingleOIDCallCount());
    }

    @Test
    public void testWalkSystemSkipsHrSysUpTimeFetchWhenMandatorySysUpTimeMissing() {
        String sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(sysDescrOid, new OctetString("Test system"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertEquals(0, snmpAccess.getGetSingleOIDCallCount());
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
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkSystemExceptionWithNullMessage() {
        snmpAccess.setSubtreeException(new RuntimeException((String) null));
        SNMPAccess.WalkResult result = snmpAccess.walkSystem();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNotNull(result.exception());
        assertNull(result.exception().getMessage());
        assertTrue(result.errorMessages().isEmpty());
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
        }, false);
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
        SNMPAccess.WalkResult result = snmpAccess.walk("1.2.3", results -> Collections.emptyMap(), false);
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesDescrEmptySubtree() {
        snmpAccess.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesDescr();
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesDescrSuccess() {
        String interfaceDescrOid = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceDescrOid + ".1", new OctetString("eth0"));
        subtreeResults.put(interfaceDescrOid + ".2", new OctetString("eth1"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesDescr();
        assertTrue(result.success());
        assertEquals(2, result.result().size());
        assertEquals("eth0", result.result().get(interfaceDescrOid + ".1"));
        assertEquals("eth1", result.result().get(interfaceDescrOid + ".2"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesDescrFiltersUnknownOIDs() {
        String interfaceDescrOid = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getString(R.string.interface_type_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceDescrOid + ".1", new OctetString("eth0"));
        subtreeResults.put(interfaceTypeOid + ".1", new OctetString("6"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesDescr();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals("eth0", result.result().get(interfaceDescrOid + ".1"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesDescrWithErrors() {
        snmpAccess.setSubtreeErrors(List.of("SNMP error"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesDescr();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesDescrException() {
        snmpAccess.setSubtreeException(new RuntimeException("Test exception"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesDescr();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNotNull(result.exception());
        assertEquals("Test exception", result.exception().getMessage());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesTypeEmptySubtree() {
        snmpAccess.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesType();
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesTypeSuccess() {
        String interfaceTypeOid = TestRegistry.getContext().getString(R.string.interface_type_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceTypeOid + ".1", new OctetString("6"));
        subtreeResults.put(interfaceTypeOid + ".2", new OctetString("53"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesType();
        assertTrue(result.success());
        assertEquals(2, result.result().size());
        assertEquals("6", result.result().get(interfaceTypeOid + ".1"));
        assertEquals("53", result.result().get(interfaceTypeOid + ".2"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesTypeFiltersUnknownOIDs() {
        String interfaceTypeOid = TestRegistry.getContext().getString(R.string.interface_type_oid);
        String interfaceDescrOid = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceTypeOid + ".1", new OctetString("6"));
        subtreeResults.put(interfaceDescrOid + ".1", new OctetString("eth0"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesType();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals("6", result.result().get(interfaceTypeOid + ".1"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesTypeWithErrors() {
        snmpAccess.setSubtreeErrors(List.of("SNMP error"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesType();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesAliasEmptySubtree() {
        snmpAccess.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesAlias();
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesAliasSuccess() {
        String interfaceAliasOid = TestRegistry.getContext().getString(R.string.interface_alias_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceAliasOid + ".1", new OctetString("LAN"));
        subtreeResults.put(interfaceAliasOid + ".2", new OctetString("WAN"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesAlias();
        assertTrue(result.success());
        assertEquals(2, result.result().size());
        assertEquals("LAN", result.result().get(interfaceAliasOid + ".1"));
        assertEquals("WAN", result.result().get(interfaceAliasOid + ".2"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesAliasFiltersUnknownOIDs() {
        String interfaceAliasOid = TestRegistry.getContext().getString(R.string.interface_alias_oid);
        String interfaceDescrOid = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceAliasOid + ".1", new OctetString("LAN"));
        subtreeResults.put(interfaceDescrOid + ".1", new OctetString("eth0"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesAlias();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals("LAN", result.result().get(interfaceAliasOid + ".1"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesAliasWithErrors() {
        snmpAccess.setSubtreeErrors(List.of("SNMP error"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesAlias();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesOperStatusEmptySubtree() {
        snmpAccess.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesOperStatus();
        assertTrue(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesOperStatusSuccess() {
        String interfaceOperStatusOid = TestRegistry.getContext().getString(R.string.interface_operstatus_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceOperStatusOid + ".1", new OctetString("1"));
        subtreeResults.put(interfaceOperStatusOid + ".2", new OctetString("2"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesOperStatus();
        assertTrue(result.success());
        assertEquals(2, result.result().size());
        assertEquals("1", result.result().get(interfaceOperStatusOid + ".1"));
        assertEquals("2", result.result().get(interfaceOperStatusOid + ".2"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesOperStatusFiltersUnknownOIDs() {
        String interfaceOperStatusOid = TestRegistry.getContext().getString(R.string.interface_operstatus_oid);
        String interfaceDescrOid = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        Map<String, Variable> subtreeResults = new HashMap<>();
        subtreeResults.put(interfaceOperStatusOid + ".1", new OctetString("1"));
        subtreeResults.put(interfaceDescrOid + ".1", new OctetString("eth0"));
        snmpAccess.setSubtreeResults(subtreeResults);
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesOperStatus();
        assertTrue(result.success());
        assertEquals(1, result.result().size());
        assertEquals("1", result.result().get(interfaceOperStatusOid + ".1"));
        assertNull(result.exception());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testWalkInterfacesOperStatusWithErrors() {
        snmpAccess.setSubtreeErrors(List.of("SNMP error"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesOperStatus();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals("SNMP error", result.errorMessages().get(0));
    }

    @Test
    public void testWalkInterfacesOperStatusException() {
        snmpAccess.setSubtreeException(new RuntimeException("Test exception"));
        SNMPAccess.WalkResult result = snmpAccess.walkInterfacesOperStatus();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNotNull(result.exception());
        assertEquals("Test exception", result.exception().getMessage());
        assertTrue(result.errorMessages().isEmpty());
    }

    @Test
    public void testFormatAddressPreservesScopeV2C() throws UnknownHostException {
        Inet6Address scopedAddress = Inet6Address.getByAddress(null, new byte[]{(byte) 0xfe, (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 2);
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setCommunity("public");
        TestSNMPAccess scopedAccess = new TestSNMPAccess(TestRegistry.getContext(), scopedAddress, 161, SNMPVersion.V2C, SNMPTransport.UDP, authInfo, true);
        scopedAccess.setSubtreeEmpty(true);
        scopedAccess.walkInterfacesDescr();
        assertTrue(scopedAccess.getTargetAddressString().contains(getScopeSuffix(scopedAddress)));
        scopedAccess.close();
    }

    @Test
    public void testFormatAddressPreservesScopeV3() throws UnknownHostException {
        Inet6Address scopedAddress = Inet6Address.getByAddress(null, new byte[]{(byte) 0xfe, (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 5);
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setUserName("scopeTestUser");
        TestSNMPAccess scopedAccess = new TestSNMPAccess(TestRegistry.getContext(), scopedAddress, 161, SNMPVersion.V3, SNMPTransport.UDP, authInfo, true);
        scopedAccess.setSubtreeEmpty(true);
        scopedAccess.walkInterfacesDescr();
        assertTrue(scopedAccess.getTargetAddressString().contains(getScopeSuffix(scopedAddress)));
        scopedAccess.close();
    }

    private String getScopeSuffix(Inet6Address scopedAddress) {
        String hostAddress = scopedAddress.getHostAddress();
        int scopeIndex = Objects.requireNonNull(hostAddress).indexOf('%');
        assertTrue("Test fixture must actually carry a scope", scopeIndex >= 0);
        return hostAddress.substring(scopeIndex);
    }

    @Test
    public void testWalkV3DiscoversEngineIDOnce() throws UnknownHostException {
        TestSNMPAccess v3Access = buildV3SNMPAccess(loopbackVariant(1), 161, "user1");
        v3Access.setSubtreeEmpty(true);
        v3Access.walkInterfacesDescr();
        v3Access.walkInterfacesType();
        assertEquals(1, v3Access.getDiscoverEngineIDCallCount());
        v3Access.close();
    }

    @Test
    public void testWalkV3EngineIDKnownToSessionOnFreshDiscovery() throws UnknownHostException {
        TestSNMPAccess v3Access = buildV3SNMPAccess(loopbackVariant(4), 161, "user4");
        v3Access.setSubtreeEmpty(true);
        v3Access.walkInterfacesDescr();
        assertTrue(v3Access.isEngineIDKnownToSession());
        v3Access.close();
    }

    @Test
    public void testWalkV3EngineIDKnownToSessionOnCacheHit() throws UnknownHostException {
        TestSNMPAccess firstContact = buildV3SNMPAccess(loopbackVariant(5), 161, "user5");
        firstContact.setSubtreeEmpty(true);
        firstContact.walkInterfacesDescr();
        firstContact.close();
        TestSNMPAccess secondContact = buildV3SNMPAccess(loopbackVariant(5), 161, "user5");
        secondContact.setSubtreeEmpty(true);
        secondContact.walkInterfacesDescr();
        assertEquals(0, secondContact.getDiscoverEngineIDCallCount());
        assertTrue(secondContact.isEngineIDKnownToSession());
        secondContact.close();
    }

    @Test
    public void testWalkV3EngineIDDiscoveryFailure() throws UnknownHostException {
        TestSNMPAccess v3Access = buildV3SNMPAccess(loopbackVariant(2), 161, "user2");
        v3Access.setDiscoveredEngineID(null);
        v3Access.setSubtreeEmpty(true);
        SNMPAccess.WalkResult result = v3Access.walkInterfacesDescr();
        assertFalse(result.success());
        assertTrue(result.result().isEmpty());
        assertNull(result.exception());
        assertEquals(1, result.errorMessages().size());
        assertEquals(TestRegistry.getContext().getString(R.string.text_snmp_engine_id_discovery_failed), result.errorMessages().get(0));
        v3Access.close();
    }

    @Test
    public void testWalkV3EngineIDDiscoveryFailureRetriesOnNextWalk() throws UnknownHostException {
        TestSNMPAccess v3Access = buildV3SNMPAccess(loopbackVariant(2), 161, "user2");
        v3Access.setDiscoveredEngineID(null);
        v3Access.setSubtreeEmpty(true);
        v3Access.walkInterfacesDescr();
        assertEquals(1, v3Access.getDiscoverEngineIDCallCount());
        v3Access.setDiscoveredEngineID(new byte[]{1, 2, 3, 4, 5});
        SNMPAccess.WalkResult result = v3Access.walkInterfacesType();
        assertEquals(2, v3Access.getDiscoverEngineIDCallCount());
        assertTrue(result.success());
        v3Access.close();
    }

    @Test
    public void testWalkV3EngineIDCacheHitAvoidsRediscovery() throws UnknownHostException {
        int cacheSize = TestRegistry.getContext().getResources().getInteger(R.integer.snmp_usm_user_cache_size);
        TestSNMPAccess firstContact = buildV3SNMPAccess(loopbackVariant(3), 161, "user3");
        firstContact.setSubtreeEmpty(true);
        firstContact.walkInterfacesDescr();
        assertEquals(1, firstContact.getDiscoverEngineIDCallCount());
        firstContact.close();
        fillEngineIDCacheWithDistinctTargets(cacheSize - 1);
        TestSNMPAccess secondContact = buildV3SNMPAccess(loopbackVariant(3), 161, "user3");
        secondContact.setSubtreeEmpty(true);
        secondContact.walkInterfacesDescr();
        assertEquals(0, secondContact.getDiscoverEngineIDCallCount());
        secondContact.close();
    }

    @Test
    public void testWalkV3EngineIDCacheEvictionForcesRediscovery() throws UnknownHostException {
        int cacheSize = TestRegistry.getContext().getResources().getInteger(R.integer.snmp_usm_user_cache_size);
        TestSNMPAccess firstContact = buildV3SNMPAccess(loopbackVariant(3), 161, "user3");
        firstContact.setSubtreeEmpty(true);
        firstContact.walkInterfacesDescr();
        assertEquals(1, firstContact.getDiscoverEngineIDCallCount());
        firstContact.close();
        fillEngineIDCacheWithDistinctTargets(cacheSize + 1);
        TestSNMPAccess secondContact = buildV3SNMPAccess(loopbackVariant(3), 161, "user3");
        secondContact.setSubtreeEmpty(true);
        secondContact.walkInterfacesDescr();
        assertEquals(1, secondContact.getDiscoverEngineIDCallCount());
        secondContact.close();
    }

    private void fillEngineIDCacheWithDistinctTargets(int count) throws UnknownHostException {
        for (int index = 0; index < count; index++) {
            TestSNMPAccess filler = buildV3SNMPAccess(loopbackVariant(1000 + index), 161, "filler");
            filler.setSubtreeEmpty(true);
            filler.walkInterfacesDescr();
            filler.close();
        }
    }

    private InetAddress loopbackVariant(int variant) throws UnknownHostException {
        return InetAddress.getByAddress(new byte[]{10, (byte) (variant >> 16), (byte) (variant >> 8), (byte) variant});
    }

    @SuppressWarnings("SameParameterValue")
    private TestSNMPAccess buildV3SNMPAccess(InetAddress address, int port, String userName) {
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setUserName(userName);
        return new TestSNMPAccess(TestRegistry.getContext(), address, port, SNMPVersion.V3, SNMPTransport.UDP, authInfo, false);
    }

    private String buildSysUpTimeErrorMessage() {
        String sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        String sysUpTimeLabelShort = TestRegistry.getContext().getString(R.string.sys_uptime_label_short);
        return TestRegistry.getContext().getString(R.string.text_snmp_mandatory_oid_missing, sysUpTimeLabelShort + " (" + sysUpTimeOid + ")");
    }
}
