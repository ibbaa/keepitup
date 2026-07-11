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

package net.ibbaa.keepitup.model.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.model.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AccessTypeDataValidatorTest {

    private AccessTypeDataValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new AccessTypeDataValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidatePingCount() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validatePingCount(data));
        assertTrue(validator.validate(data));
        data.setPingCount(11);
        assertFalse(validator.validatePingCount(data));
        assertFalse(validator.validate(data));
        data.setPingCount(0);
        assertFalse(validator.validatePingCount(data));
        assertFalse(validator.validate(data));
        data.setPingCount(10);
        assertTrue(validator.validatePingCount(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidatePingPackageSize() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validatePingPackageSize(data));
        assertTrue(validator.validate(data));
        data.setPingPackageSize(65528);
        assertFalse(validator.validatePingPackageSize(data));
        assertFalse(validator.validate(data));
        data.setPingPackageSize(-1);
        assertFalse(validator.validatePingPackageSize(data));
        assertFalse(validator.validate(data));
        data.setPingPackageSize(65527);
        assertTrue(validator.validatePingPackageSize(data));
        assertTrue(validator.validate(data));
        data.setPingPackageSize(0);
        assertTrue(validator.validatePingPackageSize(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateConnectCount() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateConnectCount(data));
        assertTrue(validator.validate(data));
        data.setConnectCount(11);
        assertFalse(validator.validateConnectCount(data));
        assertFalse(validator.validate(data));
        data.setConnectCount(0);
        assertFalse(validator.validateConnectCount(data));
        assertFalse(validator.validate(data));
        data.setConnectCount(10);
        assertTrue(validator.validateConnectCount(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateFailureOnCertificateExpiryDays() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateFailureOnCertificateExpiryDays(data));
        assertTrue(validator.validate(data));
        data.setFailureOnCertificateExpiryDays(3651);
        assertFalse(validator.validateFailureOnCertificateExpiryDays(data));
        assertFalse(validator.validate(data));
        data.setFailureOnCertificateExpiryDays(0);
        assertFalse(validator.validateFailureOnCertificateExpiryDays(data));
        assertFalse(validator.validate(data));
        data.setFailureOnCertificateExpiryDays(1);
        assertTrue(validator.validateFailureOnCertificateExpiryDays(data));
        assertTrue(validator.validate(data));
        data.setFailureOnCertificateExpiryDays(3650);
        assertTrue(validator.validateFailureOnCertificateExpiryDays(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateSNMPCommunity() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateSNMPCommunity(data));
        assertTrue(validator.validate(data));
        data.setSnmpCommunity(null);
        assertTrue(validator.validateSNMPCommunity(data));
        assertTrue(validator.validate(data));
        data.setSnmpCommunity("");
        assertTrue(validator.validateSNMPCommunity(data));
        assertTrue(validator.validate(data));
        data.setSnmpCommunity("   ");
        assertTrue(validator.validateSNMPCommunity(data));
        assertTrue(validator.validate(data));
        data.setSnmpCommunity("x".repeat(256));
        assertFalse(validator.validateSNMPCommunity(data));
        assertFalse(validator.validate(data));
        data.setSnmpCommunity("x".repeat(255));
        assertTrue(validator.validateSNMPCommunity(data));
        assertTrue(validator.validate(data));
        data.setSnmpCommunity("invalid community");
        assertFalse(validator.validateSNMPCommunity(data));
        assertFalse(validator.validate(data));
        data.setSnmpCommunity("valid_community!");
        assertTrue(validator.validateSNMPCommunity(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateSNMPUserName() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateSNMPUserName(data));
        assertTrue(validator.validate(data));
        data.setSnmpUserName(null);
        assertTrue(validator.validateSNMPUserName(data));
        assertTrue(validator.validate(data));
        data.setSnmpUserName("");
        assertTrue(validator.validateSNMPUserName(data));
        assertTrue(validator.validate(data));
        data.setSnmpUserName("x".repeat(256));
        assertFalse(validator.validateSNMPUserName(data));
        assertFalse(validator.validate(data));
        data.setSnmpUserName("x".repeat(255));
        assertTrue(validator.validateSNMPUserName(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateSNMPAuthPassphrase() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateSNMPAuthPassphrase(data));
        assertTrue(validator.validate(data));
        data.setSnmpAuthPassphrase(null);
        assertTrue(validator.validateSNMPAuthPassphrase(data));
        assertTrue(validator.validate(data));
        data.setSnmpAuthPassphrase("");
        assertTrue(validator.validateSNMPAuthPassphrase(data));
        assertTrue(validator.validate(data));
        data.setSnmpAuthPassphrase("x".repeat(256));
        assertFalse(validator.validateSNMPAuthPassphrase(data));
        assertFalse(validator.validate(data));
        data.setSnmpAuthPassphrase("x".repeat(255));
        assertTrue(validator.validateSNMPAuthPassphrase(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateSNMPPrivPassphrase() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateSNMPPrivPassphrase(data));
        assertTrue(validator.validate(data));
        data.setSnmpPrivPassphrase(null);
        assertTrue(validator.validateSNMPPrivPassphrase(data));
        assertTrue(validator.validate(data));
        data.setSnmpPrivPassphrase("");
        assertTrue(validator.validateSNMPPrivPassphrase(data));
        assertTrue(validator.validate(data));
        data.setSnmpPrivPassphrase("x".repeat(256));
        assertFalse(validator.validateSNMPPrivPassphrase(data));
        assertFalse(validator.validate(data));
        data.setSnmpPrivPassphrase("x".repeat(255));
        assertTrue(validator.validateSNMPPrivPassphrase(data));
        assertTrue(validator.validate(data));
    }

    private AccessTypeData getAccessTypeData() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(3);
        data.setPingPackageSize(56);
        data.setConnectCount(1);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        data.setAllowLegacyTLS(true);
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("public");
        data.setSnmpCommunityValid(true);
        data.setSnmpTransport(SNMPTransport.UDP);
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.MD5);
        data.setSnmpUserName("user");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthPassphraseValid(true);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.DES);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivPassphraseValid(true);
        data.setFailureOnCertificateExpiry(false);
        data.setFailureOnCertificateExpiryDays(30);
        return data;
    }
}
