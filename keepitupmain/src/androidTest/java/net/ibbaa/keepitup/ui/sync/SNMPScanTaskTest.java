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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.service.network.DNSLookupResult;
import net.ibbaa.keepitup.service.network.SNMPAccess;
import net.ibbaa.keepitup.test.mock.MockDNSLookup;
import net.ibbaa.keepitup.test.mock.MockSNMPAccess;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPScanTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class SNMPScanTaskTest {

    private MockSNMPAccess mockSNMPAccess;

    @Before
    public void beforeEachTestMethod() {
        mockSNMPAccess = new MockSNMPAccess(TestRegistry.getContext());
    }

    @Test
    public void testRunInBackgroundValidationFailureEmptyAddress() {
        String expectedError = TestRegistry.getContext().getResources().getString(R.string.task_host_field_name) + " " + TestRegistry.getContext().getResources().getString(R.string.string_invalid);
        TestSNMPScanTask task = createTask("");
        task.setMockDNSLookup(createSuccessfulDNSLookup(""));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals(expectedError, result.errorMessages().get(0));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
        assertNull(result.exception());
    }

    @Test
    public void testRunInBackgroundValidationFailureInvalidAddressNonEmpty() {
        String address = "invalid!host";
        String expectedError = TestRegistry.getContext().getResources().getString(R.string.task_host_field_name) + " " + address + " " + TestRegistry.getContext().getResources().getString(R.string.string_invalid);
        TestSNMPScanTask task = new TestSNMPScanTask(TestRegistry.getContext(), 1L, address, 161, SNMPVersion.V2C, "public");
        task.setMockDNSLookup(createSuccessfulDNSLookup(address));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals(expectedError, result.errorMessages().get(0));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
        assertNull(result.exception());
    }

    @Test
    public void testRunInBackgroundValidationFailureInvalidPort() {
        int port = -1;
        String expectedError = TestRegistry.getContext().getResources().getString(R.string.task_port_field_name) + " " + port + " " + TestRegistry.getContext().getResources().getString(R.string.string_invalid);
        TestSNMPScanTask task = new TestSNMPScanTask(TestRegistry.getContext(), 1L, "192.168.1.1", port, SNMPVersion.V2C, "public");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals(expectedError, result.errorMessages().get(0));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
        assertNull(result.exception());
    }

    @Test
    public void testRunInBackgroundValidationFailureInvalidCommunity() {
        String expectedError = TestRegistry.getContext().getResources().getString(R.string.accesstypedata_snmp_community_field_name) + " " + TestRegistry.getContext().getResources().getString(R.string.string_invalid);
        TestSNMPScanTask task = new TestSNMPScanTask(TestRegistry.getContext(), 1L, "192.168.1.1", 161, SNMPVersion.V2C, "public community");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals(expectedError, result.errorMessages().get(0));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
        assertNull(result.exception());
    }

    @Test
    public void testRunInBackgroundValidationFailureMultipleErrors() {
        String address = "invalid!host";
        int port = -1;
        String expectedAddressError = TestRegistry.getContext().getResources().getString(R.string.task_host_field_name) + " " + address + " " + TestRegistry.getContext().getResources().getString(R.string.string_invalid);
        String expectedPortError = TestRegistry.getContext().getResources().getString(R.string.task_port_field_name) + " " + port + " " + TestRegistry.getContext().getResources().getString(R.string.string_invalid);
        TestSNMPScanTask task = new TestSNMPScanTask(TestRegistry.getContext(), 1L, address, port, SNMPVersion.V2C, "public");
        task.setMockDNSLookup(createSuccessfulDNSLookup(address));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(2, result.errorMessages().size());
        assertEquals(expectedAddressError, result.errorMessages().get(0));
        assertEquals(expectedPortError, result.errorMessages().get(1));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
        assertNull(result.exception());
    }

    @Test
    public void testRunInBackgroundDNSNoAddresses() {
        String address = "192.168.1.1";
        String expectedError = TestRegistry.getContext().getResources().getString(R.string.text_dns_lookup_error, address) + " " + TestRegistry.getContext().getResources().getString(R.string.text_dns_lookup_no_address);
        DNSLookupResult dnsNoAddresses = new DNSLookupResult(Collections.emptyList(), address, null);
        MockDNSLookup mockDNS = new MockDNSLookup(address, dnsNoAddresses);
        TestSNMPScanTask task = createTask(address);
        task.setMockDNSLookup(mockDNS);
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals(expectedError, result.errorMessages().get(0));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
        assertNull(result.exception());
    }

    @Test
    public void testRunInBackgroundWalkDescrFailure() {
        String errorMessage = "SNMP timeout";
        mockSNMPAccess.setWalkInterfacesDescrResult(failureWalkResult(errorMessage));
        TestSNMPScanTask task = createTask("192.168.1.1");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertFalse(result.success());
        assertEquals(1, result.errorMessages().size());
        assertEquals(errorMessage, result.errorMessages().get(0));
        assertTrue(result.descrResult().isEmpty());
        assertTrue(result.interfaceInfos().isEmpty());
    }

    @Test
    public void testRunInBackgroundWalkTypeFailure() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        Map<String, String> descrMap = new HashMap<>();
        descrMap.put(interfaceDescrOid + ".1", "eth0");
        mockSNMPAccess.setWalkInterfacesDescrResult(successWalkResult(descrMap));
        mockSNMPAccess.setWalkInterfacesTypeResult(failureWalkResult("type error"));
        TestSNMPScanTask task = createTask("192.168.1.1");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertTrue(result.success());
        assertEquals(1, result.descrResult().size());
        assertEquals("eth0", result.descrResult().get(0).getName());
        assertTrue(result.interfaceInfos().isEmpty());
        assertEquals(1, result.errorMessages().size());
        assertEquals("type error", result.errorMessages().get(0));
    }

    @Test
    public void testRunInBackgroundWalkOperStatusFailure() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        Map<String, String> descrMap = new HashMap<>();
        descrMap.put(interfaceDescrOid + ".1", "eth0");
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put(interfaceTypeOid + ".1", "6");
        mockSNMPAccess.setWalkInterfacesDescrResult(successWalkResult(descrMap));
        mockSNMPAccess.setWalkInterfacesTypeResult(successWalkResult(typeMap));
        mockSNMPAccess.setWalkInterfacesOperStatusResult(failureWalkResult("status error"));
        TestSNMPScanTask task = createTask("192.168.1.1");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertTrue(result.success());
        assertEquals(1, result.descrResult().size());
        assertEquals("eth0", result.descrResult().get(0).getName());
        assertTrue(result.interfaceInfos().isEmpty());
        assertEquals(1, result.errorMessages().size());
        assertEquals("status error", result.errorMessages().get(0));
    }

    @Test
    public void testRunInBackgroundSuccess() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        String interfaceTypeOid = TestRegistry.getContext().getResources().getString(R.string.interface_type_oid);
        String interfaceOperStatusOid = TestRegistry.getContext().getResources().getString(R.string.interface_operstatus_oid);
        Map<String, String> descrMap = new HashMap<>();
        descrMap.put(interfaceDescrOid + ".1", "eth0");
        descrMap.put(interfaceDescrOid + ".2", "eth1");
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put(interfaceTypeOid + ".1", "6");
        typeMap.put(interfaceTypeOid + ".2", "6");
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(interfaceOperStatusOid + ".1", "1");
        statusMap.put(interfaceOperStatusOid + ".2", "2");
        mockSNMPAccess.setWalkInterfacesDescrResult(successWalkResult(descrMap));
        mockSNMPAccess.setWalkInterfacesTypeResult(successWalkResult(typeMap));
        mockSNMPAccess.setWalkInterfacesOperStatusResult(successWalkResult(statusMap));
        TestSNMPScanTask task = createTask("192.168.1.1");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertTrue(result.success());
        assertTrue(result.errorMessages().isEmpty());
        assertNull(result.exception());
        List<SNMPItem> descrResult = result.descrResult();
        assertEquals(2, descrResult.size());
        assertEquals(SNMPItemType.INTERFACEDESCR, descrResult.get(0).getSnmpItemType());
        assertEquals(SNMPItemType.INTERFACEDESCR, descrResult.get(1).getSnmpItemType());
        Map<String, SNMPInterfaceInfo> interfaceInfos = result.interfaceInfos();
        assertEquals(2, interfaceInfos.size());
        SNMPInterfaceInfo info1 = interfaceInfos.get(interfaceDescrOid + ".1");
        assertNotNull(info1);
        assertEquals("eth0", info1.getDescr());
        assertEquals(6, info1.getType());
        assertEquals(1, info1.getStatus());
        SNMPInterfaceInfo info2 = interfaceInfos.get(interfaceDescrOid + ".2");
        assertNotNull(info2);
        assertEquals("eth1", info2.getDescr());
        assertEquals(6, info2.getType());
        assertEquals(2, info2.getStatus());
    }

    @Test
    public void testRunInBackgroundDescrResultSortedByName() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        Map<String, String> descrMap = new HashMap<>();
        descrMap.put(interfaceDescrOid + ".1", "wlan0");
        descrMap.put(interfaceDescrOid + ".2", "eth0");
        descrMap.put(interfaceDescrOid + ".3", "lo");
        mockSNMPAccess.setWalkInterfacesDescrResult(successWalkResult(descrMap));
        mockSNMPAccess.setWalkInterfacesTypeResult(successWalkResult(new HashMap<>()));
        mockSNMPAccess.setWalkInterfacesOperStatusResult(successWalkResult(new HashMap<>()));
        TestSNMPScanTask task = createTask("192.168.1.1");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertTrue(result.success());
        List<SNMPItem> descrResult = result.descrResult();
        assertEquals(3, descrResult.size());
        assertEquals("eth0", descrResult.get(0).getName());
        assertEquals("lo", descrResult.get(1).getName());
        assertEquals("wlan0", descrResult.get(2).getName());
    }

    @Test
    public void testRunInBackgroundDescrResultSortedNullNameAsEmpty() {
        String interfaceDescrOid = TestRegistry.getContext().getResources().getString(R.string.interface_descr_oid);
        Map<String, String> descrMap = new HashMap<>();
        descrMap.put(interfaceDescrOid + ".1", "eth0");
        descrMap.put(interfaceDescrOid + ".2", null);
        descrMap.put(interfaceDescrOid + ".3", "lo");
        mockSNMPAccess.setWalkInterfacesDescrResult(successWalkResult(descrMap));
        mockSNMPAccess.setWalkInterfacesTypeResult(successWalkResult(new HashMap<>()));
        mockSNMPAccess.setWalkInterfacesOperStatusResult(successWalkResult(new HashMap<>()));
        TestSNMPScanTask task = createTask("192.168.1.1");
        task.setMockDNSLookup(createSuccessfulDNSLookup("192.168.1.1"));
        task.setMockSNMPAccess(mockSNMPAccess);
        SNMPScanResult result = task.runInBackground();
        assertTrue(result.success());
        List<SNMPItem> descrResult = result.descrResult();
        assertEquals(3, descrResult.size());
        assertNull(descrResult.get(0).getName());
        assertEquals("eth0", descrResult.get(1).getName());
        assertEquals("lo", descrResult.get(2).getName());
    }

    private TestSNMPScanTask createTask(String address) {
        return new TestSNMPScanTask(TestRegistry.getContext(), 1L, address, 161, SNMPVersion.V2C, "public");
    }

    private MockDNSLookup createSuccessfulDNSLookup(String address) {
        DNSLookupResult dnsLookupResult = new DNSLookupResult(InetAddress.getLoopbackAddress(), address, null);
        return new MockDNSLookup(address, dnsLookupResult);
    }

    private SNMPAccess.WalkResult successWalkResult(Map<String, String> result) {
        return new SNMPAccess.WalkResult(true, result, null, Collections.emptyList());
    }

    private SNMPAccess.WalkResult failureWalkResult(String errorMessage) {
        return new SNMPAccess.WalkResult(false, Collections.emptyMap(), null, List.of(errorMessage));
    }
}
