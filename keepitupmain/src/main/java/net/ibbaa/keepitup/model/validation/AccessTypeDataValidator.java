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

package net.ibbaa.keepitup.model.validation;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.util.SNMPUtil;
import net.ibbaa.keepitup.util.StringUtil;

public class AccessTypeDataValidator {

    private final Context context;

    public AccessTypeDataValidator(Context context) {
        this.context = context;
    }

    public boolean validate(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validate accessTypeData " + accessTypeData);
        return validatePingCount(accessTypeData) && validatePingPackageSize(accessTypeData) && validateConnectCount(accessTypeData) && validateFailureOnCertificateExpiryDays(accessTypeData) && validateSNMPCommunity(accessTypeData) && validateSNMPUserName(accessTypeData) && validateSNMPAuthPassphrase(accessTypeData) && validateSNMPPrivPassphrase(accessTypeData);
    }

    public boolean validatePingCount(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validatePingCount of accessTypeData " + accessTypeData);
        int pingCountMin = context.getResources().getInteger(R.integer.ping_count_minimum);
        int pingCountMax = context.getResources().getInteger(R.integer.ping_count_maximum);
        if (accessTypeData.getPingCount() < pingCountMin || accessTypeData.getPingCount() > pingCountMax) {
            Log.d(AccessTypeDataValidator.class.getName(), "pingCount is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "pingCount is valid. Returning true.");
        return true;
    }

    public boolean validatePingPackageSize(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validatePingPackageSize of accessTypeData " + accessTypeData);
        int pingPackageSizeMin = context.getResources().getInteger(R.integer.ping_package_size_minimum);
        int pingPackageSizeMax = context.getResources().getInteger(R.integer.ping_package_size_maximum);
        if (accessTypeData.getPingPackageSize() < pingPackageSizeMin || accessTypeData.getPingPackageSize() > pingPackageSizeMax) {
            Log.d(AccessTypeDataValidator.class.getName(), "pingPackageSize is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "pingPackageSize is valid. Returning true.");
        return true;
    }

    public boolean validateConnectCount(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateConnectCount of accessTypeData " + accessTypeData);
        int connectCountMin = context.getResources().getInteger(R.integer.connect_count_minimum);
        int connectCountMax = context.getResources().getInteger(R.integer.connect_count_maximum);
        if (accessTypeData.getConnectCount() < connectCountMin || accessTypeData.getConnectCount() > connectCountMax) {
            Log.d(AccessTypeDataValidator.class.getName(), "connectCount is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "connectCount is valid. Returning true.");
        return true;
    }

    public boolean validateFailureOnCertificateExpiryDays(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateFailureOnCertificateExpiryDays of accessTypeData " + accessTypeData);
        int min = context.getResources().getInteger(R.integer.failure_on_certificate_expiry_days_minimum);
        int max = context.getResources().getInteger(R.integer.failure_on_certificate_expiry_days_maximum);
        if (accessTypeData.getFailureOnCertificateExpiryDays() < min || accessTypeData.getFailureOnCertificateExpiryDays() > max) {
            Log.d(AccessTypeDataValidator.class.getName(), "failureOnCertificateExpiryDays is invalid. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "failureOnCertificateExpiryDays is valid. Returning true.");
        return true;
    }

    public boolean validateSNMPCommunity(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateSNMPCommunity of accessTypeData " + accessTypeData);
        String snmpCommunity = accessTypeData.getSnmpCommunity();
        if (StringUtil.isTrimmedEmpty(snmpCommunity)) {
            return true;
        }
        int communityMaxLength = context.getResources().getInteger(R.integer.snmp_community_max_length);
        if (snmpCommunity.length() > communityMaxLength) {
            Log.d(AccessTypeDataValidator.class.getName(), "Community string is too long. Returning false.");
            return false;
        }
        if (!SNMPUtil.validateCommunity(snmpCommunity)) {
            Log.d(AccessTypeDataValidator.class.getName(), "Community string has invalid characters. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "Community string is valid. Returning true.");
        return true;
    }

    public boolean validateSNMPUserName(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateSNMPUserName of accessTypeData " + accessTypeData);
        String snmpUserName = accessTypeData.getSnmpUserName();
        if (StringUtil.isEmpty(snmpUserName)) {
            return true;
        }
        int userNameMaxLength = context.getResources().getInteger(R.integer.snmp_user_name_max_length);
        if (snmpUserName.length() > userNameMaxLength) {
            Log.d(AccessTypeDataValidator.class.getName(), "User name is too long. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "User name is valid. Returning true.");
        return true;
    }

    public boolean validateSNMPAuthPassphrase(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateSNMPAuthPassphrase of accessTypeData " + accessTypeData);
        String snmpAuthPassphrase = accessTypeData.getSnmpAuthPassphrase();
        if (StringUtil.isEmpty(snmpAuthPassphrase)) {
            return true;
        }
        int authPassphraseMaxLength = context.getResources().getInteger(R.integer.snmp_auth_passphrase_max_length);
        if (snmpAuthPassphrase.length() > authPassphraseMaxLength) {
            Log.d(AccessTypeDataValidator.class.getName(), "Auth passphrase is too long. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "Auth passphrase is valid. Returning true.");
        return true;
    }

    public boolean validateSNMPPrivPassphrase(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataValidator.class.getName(), "validateSNMPPrivPassphrase of accessTypeData " + accessTypeData);
        String snmpPrivPassphrase = accessTypeData.getSnmpPrivPassphrase();
        if (StringUtil.isEmpty(snmpPrivPassphrase)) {
            return true;
        }
        int privPassphraseMaxLength = context.getResources().getInteger(R.integer.snmp_priv_passphrase_max_length);
        if (snmpPrivPassphrase.length() > privPassphraseMaxLength) {
            Log.d(AccessTypeDataValidator.class.getName(), "Priv passphrase is too long. Returning false.");
            return false;
        }
        Log.d(AccessTypeDataValidator.class.getName(), "Priv passphrase is valid. Returning true.");
        return true;
    }
}
