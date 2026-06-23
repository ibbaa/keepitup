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
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
@SuppressWarnings({"ExtractMethodRecommender"})
public class AccessTypeDataTest {

    @Before
    public void beforeEachTestMethod() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testDefaultValues() {
        AccessTypeData data = new AccessTypeData();
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertNull(data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertNull(data.getSnmpTransport());
        assertNull(data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertNull(data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
        PersistableBundle persistableBundle = data.toPersistableBundle();
        assertNotNull(persistableBundle);
        data = new AccessTypeData(persistableBundle);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertNull(data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertNull(data.getSnmpTransport());
        assertNull(data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertNull(data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
        Bundle bundle = data.toBundle();
        assertNotNull(bundle);
        data = new AccessTypeData(bundle);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertNull(data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertNull(data.getSnmpTransport());
        assertNull(data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertNull(data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
        Map<String, ?> map = data.toMap();
        assertNotNull(map);
        data = new AccessTypeData(map);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertNull(data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertNull(data.getSnmpTransport());
        assertNull(data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertNull(data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testCopy() {
        AccessTypeData data = new AccessTypeData();
        data.setId(1);
        data.setNetworkTaskId(2);
        data.setPingCount(123);
        data.setPingPackageSize(456);
        data.setConnectCount(789);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        data.setFailureOnCertificateExpiry(true);
        data.setFailureOnCertificateExpiryDays(14);
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V2C);
        data.setSnmpCommunity("public");
        data.setSnmpCommunityValid(false);
        data.setSnmpTransport(SNMPTransport.TCP);
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpUserName("user");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthPassphraseValid(false);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivPassphraseValid(false);
        AccessTypeData copyData = new AccessTypeData(data);
        assertEquals(-1, copyData.getId());
        assertEquals(-1, copyData.getNetworkTaskId());
        assertEquals(123, copyData.getPingCount());
        assertEquals(456, copyData.getPingPackageSize());
        assertEquals(789, copyData.getConnectCount());
        assertTrue(copyData.isStopOnSuccess());
        assertTrue(copyData.isIgnoreSSLError());
        assertTrue(copyData.isFailureOnCertificateExpiry());
        assertEquals(14, copyData.getFailureOnCertificateExpiryDays());
        assertFalse(copyData.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V2C, copyData.getSnmpVersion());
        assertEquals("public", copyData.getSnmpCommunity());
        assertTrue(copyData.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, copyData.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, copyData.getSnmpAuthAlgorithm());
        assertEquals("user", copyData.getSnmpUserName());
        assertEquals("authpass", copyData.getSnmpAuthPassphrase());
        assertTrue(copyData.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES256, copyData.getSnmpPrivAlgorithm());
        assertEquals("privpass", copyData.getSnmpPrivPassphrase());
        assertTrue(copyData.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testEmptyMap() {
        AccessTypeData data = new AccessTypeData(new HashMap<>());
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertNull(data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertNull(data.getSnmpTransport());
        assertNull(data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertNull(data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("networktaskid", "networktaskid");
        map.put("pingCount", "pingCount");
        map.put("pingPackageSize", "pingPackageSize");
        map.put("connectCount", "connectCount");
        map.put("stopOnSuccess", "stopOnSuccess");
        map.put("ignoreSSLError", "isIgnoreSSLError");
        map.put("failureOnCertificateExpiry", "failureOnCertificateExpiry");
        map.put("failureOnCertificateExpiryDays", "failureOnCertificateExpiryDays");
        map.put("useDefaultHeaders", "zyx");
        map.put("snmpVersion", "snmpVersion");
        map.put("snmpCommunityValid", "zyx");
        map.put("snmpTransport", "snmpTransport");
        map.put("snmpAuthAlgorithm", "snmpAuthAlgorithm");
        map.put("snmpAuthPassphraseValid", "zyx");
        map.put("snmpPrivAlgorithm", "snmpPrivAlgorithm");
        map.put("snmpPrivPassphraseValid", "zyx");
        AccessTypeData data = new AccessTypeData(map);
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertNull(data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertNull(data.getSnmpTransport());
        assertNull(data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertNull(data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("networktaskid", "2");
        map.put("pingCount", "123");
        map.put("pingPackageSize", "456");
        map.put("connectCount", "789");
        map.put("stopOnSuccess", "true");
        map.put("ignoreSSLError", "true");
        map.put("failureOnCertificateExpiry", "true");
        map.put("failureOnCertificateExpiryDays", "14");
        map.put("useDefaultHeaders", "false");
        map.put("snmpVersion", "2");
        map.put("snmpCommunity", "public");
        map.put("snmpCommunityValid", "false");
        map.put("snmpTransport", "2");
        map.put("snmpAuthAlgorithm", "5");
        map.put("snmpUserName", "user");
        map.put("snmpAuthPassphrase", "authpass");
        map.put("snmpAuthPassphraseValid", "false");
        map.put("snmpPrivAlgorithm", "3");
        map.put("snmpPrivPassphrase", "privpass");
        map.put("snmpPrivPassphraseValid", "false");
        AccessTypeData data = new AccessTypeData(map);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(14, data.getFailureOnCertificateExpiryDays());
        assertFalse(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertEquals("public", data.getSnmpCommunity());
        assertFalse(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertEquals("user", data.getSnmpUserName());
        assertEquals("authpass", data.getSnmpAuthPassphrase());
        assertFalse(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("privpass", data.getSnmpPrivPassphrase());
        assertFalse(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testPreferenceValues() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferencePingCount(123);
        preferenceManager.setPreferencePingPackageSize(456);
        preferenceManager.setPreferenceConnectCount(789);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceIgnoreSSLError(true);
        preferenceManager.setPreferenceFailureOnCertificateExpiry(true);
        preferenceManager.setPreferenceFailureOnCertificateExpiryDays(14);
        preferenceManager.setPreferenceUseDefaultHeaders(false);
        preferenceManager.setPreferenceSNMPVersion(SNMPVersion.V1);
        preferenceManager.setPreferenceSNMPTransport(SNMPTransport.TCP);
        preferenceManager.setPreferenceSNMPAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        preferenceManager.setPreferenceSNMPPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        AccessTypeData data = new AccessTypeData(TestRegistry.getContext());
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(14, data.getFailureOnCertificateExpiryDays());
        assertFalse(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES256, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
        preferenceManager.removeAllPreferences();
        data = new AccessTypeData(TestRegistry.getContext());
        assertEquals(-1, data.getId());
        assertEquals(-1, data.getNetworkTaskId());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        assertTrue(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertTrue(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertTrue(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertTrue(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testToBundleValues() {
        AccessTypeData data = new AccessTypeData();
        data.setId(1);
        data.setNetworkTaskId(2);
        data.setPingCount(123);
        data.setPingPackageSize(456);
        data.setConnectCount(789);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        data.setFailureOnCertificateExpiry(true);
        data.setFailureOnCertificateExpiryDays(14);
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("public");
        data.setSnmpCommunityValid(false);
        data.setSnmpTransport(SNMPTransport.TCP);
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpUserName("user");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthPassphraseValid(false);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES128);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivPassphraseValid(false);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(14, data.getFailureOnCertificateExpiryDays());
        assertFalse(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals("public", data.getSnmpCommunity());
        assertFalse(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertEquals("user", data.getSnmpUserName());
        assertEquals("authpass", data.getSnmpAuthPassphrase());
        assertFalse(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("privpass", data.getSnmpPrivPassphrase());
        assertFalse(data.isSnmpPrivPassphraseValid());
        PersistableBundle persistableBundle = data.toPersistableBundle();
        assertNotNull(persistableBundle);
        data = new AccessTypeData(persistableBundle);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(14, data.getFailureOnCertificateExpiryDays());
        assertFalse(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals("public", data.getSnmpCommunity());
        assertFalse(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertEquals("user", data.getSnmpUserName());
        assertEquals("authpass", data.getSnmpAuthPassphrase());
        assertFalse(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("privpass", data.getSnmpPrivPassphrase());
        assertFalse(data.isSnmpPrivPassphraseValid());
        Bundle bundle = data.toBundle();
        assertNotNull(bundle);
        data = new AccessTypeData(bundle);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(14, data.getFailureOnCertificateExpiryDays());
        assertFalse(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals("public", data.getSnmpCommunity());
        assertFalse(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertEquals("user", data.getSnmpUserName());
        assertEquals("authpass", data.getSnmpAuthPassphrase());
        assertFalse(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("privpass", data.getSnmpPrivPassphrase());
        assertFalse(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testToMap() {
        AccessTypeData data = new AccessTypeData();
        data.setId(1);
        data.setNetworkTaskId(2);
        data.setPingCount(123);
        data.setPingPackageSize(456);
        data.setConnectCount(789);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        data.setFailureOnCertificateExpiry(true);
        data.setFailureOnCertificateExpiryDays(14);
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("public");
        data.setSnmpCommunityValid(false);
        data.setSnmpTransport(SNMPTransport.TCP);
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpUserName("user");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthPassphraseValid(false);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES128);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivPassphraseValid(false);
        Map<String, ?> map = data.toMap();
        assertNotNull(map);
        data = new AccessTypeData(map);
        assertEquals(1, data.getId());
        assertEquals(2, data.getNetworkTaskId());
        assertEquals(123, data.getPingCount());
        assertEquals(456, data.getPingPackageSize());
        assertEquals(789, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(14, data.getFailureOnCertificateExpiryDays());
        assertFalse(data.isUseDefaultHeaders());
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals("public", data.getSnmpCommunity());
        assertFalse(data.isSnmpCommunityValid());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertEquals("user", data.getSnmpUserName());
        assertEquals("authpass", data.getSnmpAuthPassphrase());
        assertFalse(data.isSnmpAuthPassphraseValid());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("privpass", data.getSnmpPrivPassphrase());
        assertFalse(data.isSnmpPrivPassphraseValid());
    }

    @Test
    public void testIsEqual() {
        AccessTypeData data1 = new AccessTypeData();
        AccessTypeData data2 = new AccessTypeData();
        assertTrue(data1.isEqual(data2));
        data1.setId(0);
        assertFalse(data1.isEqual(data2));
        data2.setId(0);
        assertTrue(data1.isEqual(data2));
        data1.setNetworkTaskId(22);
        assertFalse(data1.isEqual(data2));
        data2.setNetworkTaskId(22);
        assertTrue(data1.isEqual(data2));
        data1.setPingCount(123);
        assertFalse(data1.isEqual(data2));
        data2.setPingCount(123);
        assertTrue(data1.isEqual(data2));
        data1.setPingPackageSize(456);
        assertFalse(data1.isEqual(data2));
        data2.setPingPackageSize(456);
        assertTrue(data1.isEqual(data2));
        data1.setConnectCount(789);
        assertFalse(data1.isEqual(data2));
        data2.setConnectCount(789);
        assertTrue(data1.isEqual(data2));
        data1.setStopOnSuccess(true);
        assertFalse(data1.isEqual(data2));
        data2.setStopOnSuccess(true);
        assertTrue(data1.isEqual(data2));
        data1.setIgnoreSSLError(true);
        assertFalse(data1.isEqual(data2));
        data2.setIgnoreSSLError(true);
        assertTrue(data1.isEqual(data2));
        data1.setFailureOnCertificateExpiry(true);
        assertFalse(data1.isEqual(data2));
        data2.setFailureOnCertificateExpiry(true);
        assertTrue(data1.isEqual(data2));
        data1.setFailureOnCertificateExpiryDays(14);
        assertFalse(data1.isEqual(data2));
        data2.setFailureOnCertificateExpiryDays(14);
        assertTrue(data1.isEqual(data2));
        data1.setUseDefaultHeaders(false);
        assertFalse(data1.isEqual(data2));
        data2.setUseDefaultHeaders(false);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpVersion(SNMPVersion.V1);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpVersion(SNMPVersion.V1);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpCommunity("public");
        assertFalse(data1.isEqual(data2));
        data2.setSnmpCommunity("public");
        assertTrue(data1.isEqual(data2));
        data1.setSnmpCommunityValid(false);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpCommunityValid(false);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpTransport(SNMPTransport.TCP);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpTransport(SNMPTransport.TCP);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpUserName("user");
        assertFalse(data1.isEqual(data2));
        data2.setSnmpUserName("user");
        assertTrue(data1.isEqual(data2));
        data1.setSnmpAuthPassphrase("authpass");
        assertFalse(data1.isEqual(data2));
        data2.setSnmpAuthPassphrase("authpass");
        assertTrue(data1.isEqual(data2));
        data1.setSnmpAuthPassphraseValid(false);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpAuthPassphraseValid(false);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpPrivPassphrase("privpass");
        assertFalse(data1.isEqual(data2));
        data2.setSnmpPrivPassphrase("privpass");
        assertTrue(data1.isEqual(data2));
        data1.setSnmpPrivPassphraseValid(false);
        assertFalse(data1.isEqual(data2));
        data2.setSnmpPrivPassphraseValid(false);
        assertTrue(data1.isEqual(data2));
    }

    @Test
    public void testIsTechnicallyEqual() {
        AccessTypeData data1 = new AccessTypeData();
        AccessTypeData data2 = new AccessTypeData();
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setId(0);
        assertTrue(data1.isTechnicallyEqual(data2));
        data2.setId(0);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setNetworkTaskId(22);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setNetworkTaskId(22);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setPingCount(123);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setPingCount(123);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setPingPackageSize(456);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setPingPackageSize(456);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setConnectCount(789);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setConnectCount(789);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setStopOnSuccess(true);
        assertFalse(data1.isEqual(data2));
        data2.setStopOnSuccess(true);
        assertTrue(data1.isEqual(data2));
        data1.setIgnoreSSLError(true);
        assertFalse(data1.isEqual(data2));
        data2.setIgnoreSSLError(true);
        assertTrue(data1.isEqual(data2));
        data1.setFailureOnCertificateExpiry(true);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setFailureOnCertificateExpiry(true);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setFailureOnCertificateExpiryDays(14);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setFailureOnCertificateExpiryDays(14);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setUseDefaultHeaders(false);
        assertFalse(data1.isEqual(data2));
        data2.setUseDefaultHeaders(false);
        assertTrue(data1.isEqual(data2));
        data1.setSnmpVersion(SNMPVersion.V1);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpVersion(SNMPVersion.V1);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpCommunity("public");
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpCommunity("public");
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpCommunityValid(false);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpCommunityValid(false);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpTransport(SNMPTransport.TCP);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpTransport(SNMPTransport.TCP);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpUserName("user");
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpUserName("user");
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpAuthPassphrase("authpass");
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpAuthPassphrase("authpass");
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpAuthPassphraseValid(false);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpAuthPassphraseValid(false);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpPrivPassphrase("privpass");
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpPrivPassphrase("privpass");
        assertTrue(data1.isTechnicallyEqual(data2));
        data1.setSnmpPrivPassphraseValid(false);
        assertFalse(data1.isTechnicallyEqual(data2));
        data2.setSnmpPrivPassphraseValid(false);
        assertTrue(data1.isTechnicallyEqual(data2));
    }
}
