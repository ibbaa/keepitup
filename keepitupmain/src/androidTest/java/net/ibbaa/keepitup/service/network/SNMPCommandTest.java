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
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPCommand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@MediumTest
@RunWith(AndroidJUnit4.class)
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
public class SNMPCommandTest {

    private String sysUpTimeOid;
    private String sysDescrOid;
    private String descrOidBase;
    private String typeOidBase;
    private String statusOidBase;
    private String aliasOidBase;

    @Before
    public void beforeEachTestMethod() {
        sysUpTimeOid = TestRegistry.getContext().getString(R.string.sys_uptime_oid);
        sysDescrOid = TestRegistry.getContext().getString(R.string.sys_descr_oid);
        descrOidBase = TestRegistry.getContext().getString(R.string.interface_descr_oid);
        typeOidBase = TestRegistry.getContext().getString(R.string.interface_type_oid);
        statusOidBase = TestRegistry.getContext().getString(R.string.interface_operstatus_oid);
        aliasOidBase = TestRegistry.getContext().getString(R.string.interface_alias_oid);
    }

    @Test
    public void testCallSuccess() {
        TestSNMPCommand command = createCommand(-1);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysUpTimeOid, "12345");
        systemMap.put(sysDescrOid, "Test system");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertFalse(commandResult.reboot());
        assertEquals(2, commandResult.systemResult().size());
        assertEquals("12345", commandResult.systemResult().get(sysUpTimeOid));
        assertEquals("Test system", commandResult.systemResult().get(sysDescrOid));
        assertNull(commandResult.exception());
        assertTrue(commandResult.errorMessages().isEmpty());
        assertTrue(commandResult.duration() >= 0);
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallSystemWalkFailure() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(failureResult("No response from host."));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertFalse(commandResult.reboot());
        assertTrue(commandResult.systemResult().isEmpty());
        assertNull(commandResult.exception());
        assertEquals(1, commandResult.errorMessages().size());
        assertEquals("No response from host.", commandResult.errorMessages().get(0));
        assertTrue(commandResult.duration() >= 0);
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallSystemWalkFailureWithErrors() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(false, Collections.emptyMap(), null, List.of("Error 1", "Error 2")));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertFalse(commandResult.interfaceResult().canSave());
        assertEquals(2, commandResult.errorMessages().size());
        assertEquals("Error 1", commandResult.errorMessages().get(0));
        assertEquals("Error 2", commandResult.errorMessages().get(1));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallSystemWalkWithException() {
        TestSNMPCommand command = createCommand(-1);
        RuntimeException exception = new RuntimeException("SNMP error");
        command.getMockSNMPAccess().setWalkResult(exceptionResult(exception));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertFalse(commandResult.interfaceResult().canSave());
        assertNotNull(commandResult.exception());
        assertEquals("SNMP error", commandResult.exception().getMessage());
        assertEquals(1, commandResult.errorMessages().size());
        assertEquals("SNMP error", commandResult.errorMessages().get(0));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallNullResultMap() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(new SNMPAccess.WalkResult(false, null, null, Collections.emptyList()));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.systemResult().isEmpty());
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallRebootDetected() {
        TestSNMPCommand command = createCommand(5000);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysUpTimeOid, "100");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.reboot());
    }

    @Test
    public void testCallNoRebootSysUpTimeIncreased() {
        TestSNMPCommand command = createCommand(1000);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysUpTimeOid, "5000");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertFalse(commandResult.reboot());
    }

    @Test
    public void testCallNoRebootLastSysUpTimeNegative() {
        TestSNMPCommand command = createCommand(-1);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysUpTimeOid, "100");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertFalse(commandResult.reboot());
    }

    @Test
    public void testCallNoRebootCurrentSysUpTimeMissing() {
        TestSNMPCommand command = createCommand(5000);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysDescrOid, "Test system");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertFalse(commandResult.reboot());
    }

    @Test
    public void testCallNoRebootCounterOverflow() {
        long aboveThreshold = 4200000000L;
        TestSNMPCommand command = createCommand(aboveThreshold);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysUpTimeOid, "1000");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertFalse(commandResult.reboot());
    }

    @Test
    public void testCallRebootDetectedBelowOverflowThreshold() {
        long belowThreshold = 4000000000L;
        TestSNMPCommand command = createCommand(belowThreshold);
        TreeMap<String, String> systemMap = new TreeMap<>();
        systemMap.put(sysUpTimeOid, "1000");
        command.getMockSNMPAccess().setWalkResult(successResult(systemMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.reboot());
    }

    @Test
    public void testCallInterfaceWalksSkippedWhenInterfacesEmpty() {
        TestSNMPCommand command = createCommand(-1);
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceDescrWalkFailure() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(failureResult("No response from host."));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
        assertEquals(1, commandResult.errorMessages().size());
        assertEquals("No response from host.", commandResult.errorMessages().get(0));
    }

    @Test
    public void testCallInterfaceDescrWalkEmpty() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(emptySuccessResult());
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceDescrWalkEmptyWithMonitoredItems() {
        String eth0Oid = descrOidBase + ".1";
        SNMPItem eth0 = getMonitoredDescrItem(eth0Oid, "eth0");
        TestSNMPCommand command = createCommand(-1, List.of(eth0));
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(emptySuccessResult());
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().result().size());
        assertEquals("eth0", commandResult.interfaceResult().result().get(0).getName());
        assertEquals(1, commandResult.interfaceResult().monitoredNotFound().size());
        assertEquals("eth0", commandResult.interfaceResult().monitoredNotFound().get(0));
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceTypeWalkFailure() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0Oid, "eth0");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(successResult(descrMap));
        command.getMockSNMPAccess().setWalkInterfacesTypeResult(failureResult("No response from host."));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceOperStatusWalkFailure() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0Oid, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(successResult(descrMap));
        command.getMockSNMPAccess().setWalkInterfacesTypeResult(successResult(typeMap));
        command.getMockSNMPAccess().setWalkInterfacesOperStatusResult(failureResult("No response from host."));
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertFalse(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().result().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceAliasWalkFailureSilentlyIgnored() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0Oid, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(successResult(descrMap));
        command.getMockSNMPAccess().setWalkInterfacesTypeResult(successResult(typeMap));
        command.getMockSNMPAccess().setWalkInterfacesOperStatusResult(successResult(statusMap));
        command.getMockSNMPAccess().setWalkInterfacesAliasResult(failureResult("Not implemented."));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceSuccessAllUp() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0Oid, "eth0");
        descrMap.put(descrOidBase + ".2", "eth1");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        typeMap.put(typeOidBase + ".2", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        statusMap.put(statusOidBase + ".2", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceMonitoredInterfaceDown() {
        String eth0Oid = descrOidBase + ".1";
        SNMPItem eth0 = getMonitoredDescrItem(eth0Oid, "eth0");
        TestSNMPCommand command = createCommand(-1, List.of(eth0));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0Oid, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "2");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertEquals(1, commandResult.interfaceResult().monitoredDownStatus().size());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().containsKey("eth0"));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceMonitoredInterfaceNotFound() {
        String eth0Oid = descrOidBase + ".1";
        SNMPItem eth0 = getMonitoredDescrItem(eth0Oid, "eth0");
        TestSNMPCommand command = createCommand(-1, List.of(eth0));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(descrOidBase + ".2", "eth1");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".2", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".2", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().monitoredNotFound().size());
        assertEquals("eth0", commandResult.interfaceResult().monitoredNotFound().get(0));
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallMonitoredNotFoundItemPreservedInResult() {
        String eth0Oid = descrOidBase + ".1";
        String eth1Oid = descrOidBase + ".2";
        SNMPItem eth0 = getMonitoredDescrItem(eth0Oid, "eth0");
        TestSNMPCommand command = createCommand(-1, List.of(eth0));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth1Oid, "eth1");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".2", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".2", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().monitoredNotFound().size());
        assertEquals("eth0", commandResult.interfaceResult().monitoredNotFound().get(0));
        assertTrue(hasItemWithTypeAndName(commandResult.interfaceResult().result(), SNMPItemType.INTERFACEDESCR, "eth0"));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallMonitoredNotFoundCompanionItemsPreservedInResult() {
        String eth0Oid = descrOidBase + ".1";
        String eth1Oid = descrOidBase + ".2";
        SNMPItem eth0 = getMonitoredDescrItem(eth0Oid, "eth0");
        SNMPItem eth0Type = new SNMPItem();
        eth0Type.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        eth0Type.setOid(typeOidBase + ".1");
        eth0Type.setName("6");
        SNMPItem eth0Alias = new SNMPItem();
        eth0Alias.setSnmpItemType(SNMPItemType.INTERFACEALIAS);
        eth0Alias.setOid(aliasOidBase + ".1");
        eth0Alias.setName("uplink");
        TestSNMPCommand command = createCommand(-1, List.of(eth0, eth0Type, eth0Alias));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth1Oid, "eth1");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".2", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".2", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().monitoredNotFound().size());
        List<SNMPItem> result = commandResult.interfaceResult().result();
        assertTrue(hasItemWithTypeAndName(result, SNMPItemType.INTERFACEDESCR, "eth0"));
        assertTrue(hasItemWithTypeAndName(result, SNMPItemType.INTERFACETYPE, "6"));
        assertTrue(hasItemWithTypeAndName(result, SNMPItemType.INTERFACEALIAS, "uplink"));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceAliasWalkSuccessAliasIncluded() {
        String eth0Oid = descrOidBase + ".1";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0Oid, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0Oid, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        TreeMap<String, String> aliasMap = new TreeMap<>();
        aliasMap.put(aliasOidBase + ".1", "uplink");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(successResult(descrMap));
        command.getMockSNMPAccess().setWalkInterfacesTypeResult(successResult(typeMap));
        command.getMockSNMPAccess().setWalkInterfacesOperStatusResult(successResult(statusMap));
        command.getMockSNMPAccess().setWalkInterfacesAliasResult(successResult(aliasMap));
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(hasAliasItem(commandResult.interfaceResult().result(), "uplink"));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallInterfaceMonitoredDownAndNotFound() {
        String eth0Oid = descrOidBase + ".1";
        String eth1Oid = descrOidBase + ".2";
        SNMPItem eth0 = getMonitoredDescrItem(eth0Oid, "eth0");
        SNMPItem eth1 = getMonitoredDescrItem(eth1Oid, "eth1");
        TestSNMPCommand command = createCommand(-1, List.of(eth0, eth1));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth1Oid, "eth1");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".2", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".2", "2");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().monitoredNotFound().size());
        assertEquals("eth0", commandResult.interfaceResult().monitoredNotFound().get(0));
        assertEquals(1, commandResult.interfaceResult().monitoredDownStatus().size());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().containsKey("eth1"));
        assertTrue(commandResult.interfaceResult().duplicateNames().isEmpty());
    }

    @Test
    public void testCallDuplicateDescrNamesReported() {
        String eth0OidLow = descrOidBase + ".1";
        String eth0OidHigh = descrOidBase + ".3";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0OidLow, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0OidLow, "eth0");
        descrMap.put(eth0OidHigh, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        typeMap.put(typeOidBase + ".3", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        statusMap.put(statusOidBase + ".3", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().foundCount());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertEquals(1, commandResult.interfaceResult().duplicateNames().size());
        assertEquals("eth0", commandResult.interfaceResult().duplicateNames().get(0));
    }

    @Test
    public void testCallDuplicateDescrNamesMultiple() {
        String eth0OidLow = descrOidBase + ".1";
        String wlan0OidLow = descrOidBase + ".2";
        String eth0OidHigh = descrOidBase + ".3";
        String wlan0OidHigh = descrOidBase + ".4";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0OidLow, "eth0", false), getSNMPItem(wlan0OidLow, "wlan0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0OidLow, "eth0");
        descrMap.put(wlan0OidLow, "wlan0");
        descrMap.put(eth0OidHigh, "eth0");
        descrMap.put(wlan0OidHigh, "wlan0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        typeMap.put(typeOidBase + ".2", "6");
        typeMap.put(typeOidBase + ".3", "6");
        typeMap.put(typeOidBase + ".4", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        statusMap.put(statusOidBase + ".2", "1");
        statusMap.put(statusOidBase + ".3", "1");
        statusMap.put(statusOidBase + ".4", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(2, commandResult.interfaceResult().foundCount());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertEquals(2, commandResult.interfaceResult().duplicateNames().size());
        assertTrue(commandResult.interfaceResult().duplicateNames().contains("eth0"));
        assertTrue(commandResult.interfaceResult().duplicateNames().contains("wlan0"));
    }

    @Test
    public void testCallDuplicateDescrTypeOverriddenToEthernet() {
        String eth0OidLow = descrOidBase + ".1";
        String eth0OidHigh = descrOidBase + ".3";
        TestSNMPCommand command = createCommand(-1, List.of(getSNMPItem(eth0OidLow, "eth0", false)));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0OidLow, "eth0");
        descrMap.put(eth0OidHigh, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "24");
        typeMap.put(typeOidBase + ".3", "24");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        statusMap.put(statusOidBase + ".3", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertEquals(1, commandResult.interfaceResult().duplicateNames().size());
        SNMPItem typeItem = null;
        for (SNMPItem item : commandResult.interfaceResult().result()) {
            if (SNMPItemType.INTERFACETYPE.equals(item.getSnmpItemType())) {
                typeItem = item;
                break;
            }
        }
        assertNotNull(typeItem);
        assertEquals("6", typeItem.getName());
    }

    @Test
    public void testCallMonitoredDuplicateDescrDownViaOtherOid() {
        String eth0OidLow = descrOidBase + ".1";
        String eth0OidHigh = descrOidBase + ".3";
        SNMPItem eth0 = getMonitoredDescrItem(eth0OidLow, "eth0");
        TestSNMPCommand command = createCommand(-1, List.of(eth0));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0OidLow, "eth0");
        descrMap.put(eth0OidHigh, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        typeMap.put(typeOidBase + ".3", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        statusMap.put(statusOidBase + ".3", "2");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertFalse(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertEquals(1, commandResult.interfaceResult().monitoredDownStatus().size());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().containsKey("eth0"));
        assertEquals(1, commandResult.interfaceResult().duplicateNames().size());
        assertEquals("eth0", commandResult.interfaceResult().duplicateNames().get(0));
    }

    @Test
    public void testCallMonitoredDuplicateDescrAllUpNotDown() {
        String eth0OidLow = descrOidBase + ".1";
        String eth0OidHigh = descrOidBase + ".3";
        SNMPItem eth0 = getMonitoredDescrItem(eth0OidLow, "eth0");
        TestSNMPCommand command = createCommand(-1, List.of(eth0));
        TreeMap<String, String> descrMap = new TreeMap<>();
        descrMap.put(eth0OidLow, "eth0");
        descrMap.put(eth0OidHigh, "eth0");
        TreeMap<String, String> typeMap = new TreeMap<>();
        typeMap.put(typeOidBase + ".1", "6");
        typeMap.put(typeOidBase + ".3", "6");
        TreeMap<String, String> statusMap = new TreeMap<>();
        statusMap.put(statusOidBase + ".1", "1");
        statusMap.put(statusOidBase + ".3", "1");
        command.getMockSNMPAccess().setWalkResult(emptySuccessResult());
        setupInterfaceWalks(command, descrMap, typeMap, statusMap);
        SNMPCommandResult commandResult = command.call();
        assertTrue(commandResult.success());
        assertTrue(commandResult.interfaceResult().canSave());
        assertTrue(commandResult.interfaceResult().monitoredNotFound().isEmpty());
        assertTrue(commandResult.interfaceResult().monitoredDownStatus().isEmpty());
        assertEquals(1, commandResult.interfaceResult().duplicateNames().size());
        assertEquals("eth0", commandResult.interfaceResult().duplicateNames().get(0));
    }

    private TestSNMPCommand createCommand(long lastSysUpTime) {
        return createCommand(lastSysUpTime, Collections.emptyList());
    }

    private TestSNMPCommand createCommand(long lastSysUpTime, List<SNMPItem> interfaces) {
        return new TestSNMPCommand(TestRegistry.getContext(), 0L, InetAddress.getLoopbackAddress(), 161, SNMPVersion.V2C, "public", interfaces, lastSysUpTime, false);
    }

    private SNMPAccess.WalkResult successResult(Map<String, String> map) {
        return new SNMPAccess.WalkResult(true, map, null, Collections.emptyList());
    }

    private SNMPAccess.WalkResult emptySuccessResult() {
        return new SNMPAccess.WalkResult(true, Collections.emptyMap(), null, Collections.emptyList());
    }

    private SNMPAccess.WalkResult failureResult(String error) {
        return new SNMPAccess.WalkResult(false, Collections.emptyMap(), null, List.of(error));
    }

    private SNMPAccess.WalkResult exceptionResult(RuntimeException exc) {
        return new SNMPAccess.WalkResult(false, Collections.emptyMap(), exc, List.of(Objects.requireNonNull(exc.getMessage())));
    }

    private SNMPItem getSNMPItem(String oid, String name, boolean monitored) {
        SNMPItem item = new SNMPItem();
        item.setNetworkTaskId(0);
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setOid(oid);
        item.setName(name);
        item.setMonitored(monitored);
        return item;
    }

    private SNMPItem getMonitoredDescrItem(String oid, String name) {
        return getSNMPItem(oid, name, true);
    }

    private void setupInterfaceWalks(TestSNMPCommand command, Map<String, String> descrMap, Map<String, String> typeMap, Map<String, String> statusMap) {
        command.getMockSNMPAccess().setWalkInterfacesDescrResult(successResult(descrMap));
        command.getMockSNMPAccess().setWalkInterfacesTypeResult(successResult(typeMap));
        command.getMockSNMPAccess().setWalkInterfacesOperStatusResult(successResult(statusMap));
        command.getMockSNMPAccess().setWalkInterfacesAliasResult(emptySuccessResult());
    }

    @SuppressWarnings("SameParameterValue")
    private boolean hasAliasItem(List<SNMPItem> items, String aliasName) {
        for (SNMPItem item : items) {
            if (SNMPItemType.INTERFACEALIAS.equals(item.getSnmpItemType()) && aliasName.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasItemWithTypeAndName(List<SNMPItem> items, SNMPItemType type, String name) {
        for (SNMPItem item : items) {
            if (type.equals(item.getSnmpItemType()) && name.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }
}
