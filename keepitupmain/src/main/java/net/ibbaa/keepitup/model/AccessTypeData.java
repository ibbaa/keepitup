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

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccessTypeData {

    private long id;
    private long networktaskid;
    private int pingCount;
    private int pingPackageSize;
    private int connectCount;
    private boolean stopOnSuccess;
    private boolean ignoreSSLError;
    private boolean allowLegacyTLS;
    private boolean failureOnCertificateExpiry;
    private int failureOnCertificateExpiryDays;
    private boolean useDefaultHeaders;
    private SNMPVersion snmpVersion;
    private String snmpCommunity;
    private boolean snmpCommunityValid;
    private SNMPTransport snmpTransport;
    private SNMPAuthAlgorithm snmpAuthAlgorithm;
    private String snmpUserName;
    private String snmpAuthPassphrase;
    private boolean snmpAuthPassphraseValid;
    private SNMPPrivAlgorithm snmpPrivAlgorithm;
    private String snmpPrivPassphrase;
    private boolean snmpPrivPassphraseValid;

    public AccessTypeData() {
        this.id = -1;
        this.networktaskid = -1;
        this.pingCount = 3;
        this.pingPackageSize = 56;
        this.connectCount = 1;
        this.stopOnSuccess = false;
        this.ignoreSSLError = false;
        this.allowLegacyTLS = false;
        this.failureOnCertificateExpiry = false;
        this.failureOnCertificateExpiryDays = 30;
        this.useDefaultHeaders = true;
        this.snmpVersion = null;
        this.snmpCommunity = null;
        this.snmpCommunityValid = true;
        this.snmpTransport = null;
        this.snmpAuthAlgorithm = null;
        this.snmpUserName = null;
        this.snmpAuthPassphrase = null;
        this.snmpAuthPassphraseValid = true;
        this.snmpPrivAlgorithm = null;
        this.snmpPrivPassphrase = null;
        this.snmpPrivPassphraseValid = true;
    }

    public AccessTypeData(AccessTypeData otherData) {
        this();
        this.pingCount = otherData.getPingCount();
        this.pingPackageSize = otherData.getPingPackageSize();
        this.connectCount = otherData.getConnectCount();
        this.stopOnSuccess = otherData.isStopOnSuccess();
        this.ignoreSSLError = otherData.isIgnoreSSLError();
        this.allowLegacyTLS = otherData.isAllowLegacyTLS();
        this.failureOnCertificateExpiry = otherData.isFailureOnCertificateExpiry();
        this.failureOnCertificateExpiryDays = otherData.getFailureOnCertificateExpiryDays();
        this.useDefaultHeaders = otherData.isUseDefaultHeaders();
        this.snmpVersion = otherData.getSnmpVersion();
        this.snmpCommunity = otherData.getSnmpCommunity();
        this.snmpTransport = otherData.getSnmpTransport();
        this.snmpAuthAlgorithm = otherData.getSnmpAuthAlgorithm();
        this.snmpUserName = otherData.getSnmpUserName();
        this.snmpAuthPassphrase = otherData.getSnmpAuthPassphrase();
        this.snmpPrivAlgorithm = otherData.getSnmpPrivAlgorithm();
        this.snmpPrivPassphrase = otherData.getSnmpPrivPassphrase();
    }

    public AccessTypeData(Context context) {
        this();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        this.pingCount = preferenceManager.getPreferencePingCount();
        this.pingPackageSize = preferenceManager.getPreferencePingPackageSize();
        this.connectCount = preferenceManager.getPreferenceConnectCount();
        this.stopOnSuccess = preferenceManager.getPreferenceStopOnSuccess();
        this.ignoreSSLError = preferenceManager.getPreferenceIgnoreSSLError();
        this.allowLegacyTLS = preferenceManager.getPreferenceAllowLegacyTLS();
        this.failureOnCertificateExpiry = preferenceManager.getPreferenceFailureOnCertificateExpiry();
        this.failureOnCertificateExpiryDays = preferenceManager.getPreferenceFailureOnCertificateExpiryDays();
        this.useDefaultHeaders = preferenceManager.getPreferenceUseDefaultHeaders();
        this.snmpVersion = preferenceManager.getPreferenceSNMPVersion();
        this.snmpTransport = preferenceManager.getPreferenceSNMPTransport();
        this.snmpAuthAlgorithm = preferenceManager.getPreferenceSNMPAuthAlgorithm();
        this.snmpPrivAlgorithm = preferenceManager.getPreferenceSNMPPrivAlgorithm();
    }

    public AccessTypeData(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public AccessTypeData(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        this.pingCount = bundle.getInt("pingCount");
        this.pingPackageSize = bundle.getInt("pingPackageSize");
        this.connectCount = bundle.getInt("connectCount");
        this.stopOnSuccess = bundle.getInt("stopOnSuccess") >= 1;
        this.ignoreSSLError = bundle.getInt("ignoreSSLError") >= 1;
        this.allowLegacyTLS = bundle.getInt("allowLegacyTLS") >= 1;
        this.failureOnCertificateExpiry = bundle.getInt("failureOnCertificateExpiry") >= 1;
        this.failureOnCertificateExpiryDays = bundle.getInt("failureOnCertificateExpiryDays");
        this.useDefaultHeaders = bundle.getInt("useDefaultHeaders") >= 1;
        if (bundle.containsKey("snmpVersion")) {
            snmpVersion = SNMPVersion.forCode(bundle.getInt("snmpVersion"));
        }
        this.snmpCommunity = bundle.getString("snmpCommunity");
        this.snmpCommunityValid = bundle.getInt("snmpCommunityValid") >= 1;
        if (bundle.containsKey("snmpTransport")) {
            snmpTransport = SNMPTransport.forCode(bundle.getInt("snmpTransport"));
        }
        if (bundle.containsKey("snmpAuthAlgorithm")) {
            snmpAuthAlgorithm = SNMPAuthAlgorithm.forCode(bundle.getInt("snmpAuthAlgorithm"));
        }
        this.snmpUserName = bundle.getString("snmpUserName");
        this.snmpAuthPassphrase = bundle.getString("snmpAuthPassphrase");
        this.snmpAuthPassphraseValid = bundle.getInt("snmpAuthPassphraseValid") >= 1;
        if (bundle.containsKey("snmpPrivAlgorithm")) {
            snmpPrivAlgorithm = SNMPPrivAlgorithm.forCode(bundle.getInt("snmpPrivAlgorithm"));
        }
        this.snmpPrivPassphrase = bundle.getString("snmpPrivPassphrase");
        this.snmpPrivPassphraseValid = bundle.getInt("snmpPrivPassphraseValid") >= 1;
    }

    public AccessTypeData(Context context, Map<String, ?> map) {
        this(context);
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (NumberUtil.isValidLongValue(map.get("networktaskid"))) {
            this.networktaskid = NumberUtil.getLongValue(map.get("networktaskid"), -1);
        }
        if (NumberUtil.isValidIntValue(map.get("pingCount"))) {
            this.pingCount = NumberUtil.getIntValue(map.get("pingCount"), 3);
        }
        if (NumberUtil.isValidIntValue(map.get("pingPackageSize"))) {
            this.pingPackageSize = NumberUtil.getIntValue(map.get("pingPackageSize"), 56);
        }
        if (NumberUtil.isValidIntValue(map.get("connectCount"))) {
            this.connectCount = NumberUtil.getIntValue(map.get("connectCount"), 1);
        }
        if (map.get("stopOnSuccess") != null) {
            this.stopOnSuccess = Boolean.parseBoolean(Objects.requireNonNull(map.get("stopOnSuccess")).toString());
        }
        if (map.get("ignoreSSLError") != null) {
            this.ignoreSSLError = Boolean.parseBoolean(Objects.requireNonNull(map.get("ignoreSSLError")).toString());
        }
        if (map.get("allowLegacyTLS") != null) {
            this.allowLegacyTLS = Boolean.parseBoolean(Objects.requireNonNull(map.get("allowLegacyTLS")).toString());
        }
        if (map.get("failureOnCertificateExpiry") != null) {
            this.failureOnCertificateExpiry = Boolean.parseBoolean(Objects.requireNonNull(map.get("failureOnCertificateExpiry")).toString());
        }
        if (NumberUtil.isValidIntValue(map.get("failureOnCertificateExpiryDays"))) {
            this.failureOnCertificateExpiryDays = NumberUtil.getIntValue(map.get("failureOnCertificateExpiryDays"), 30);
        }
        if (map.get("useDefaultHeaders") != null) {
            this.useDefaultHeaders = !"false".equalsIgnoreCase(Objects.requireNonNull(map.get("useDefaultHeaders")).toString());
        }
        if (NumberUtil.isValidIntValue(map.get("snmpVersion"))) {
            this.snmpVersion = SNMPVersion.forCode(NumberUtil.getIntValue(map.get("snmpVersion"), -1));
        }
        if (map.get("snmpCommunity") != null) {
            this.snmpCommunity = Objects.requireNonNull(map.get("snmpCommunity")).toString();
        }
        if (map.get("snmpCommunityValid") != null) {
            this.snmpCommunityValid = !"false".equalsIgnoreCase(Objects.requireNonNull(map.get("snmpCommunityValid")).toString());
        }
        if (NumberUtil.isValidIntValue(map.get("snmpTransport"))) {
            this.snmpTransport = SNMPTransport.forCode(NumberUtil.getIntValue(map.get("snmpTransport"), -1));
        }
        if (NumberUtil.isValidIntValue(map.get("snmpAuthAlgorithm"))) {
            this.snmpAuthAlgorithm = SNMPAuthAlgorithm.forCode(NumberUtil.getIntValue(map.get("snmpAuthAlgorithm"), -1));
        }
        if (map.get("snmpUserName") != null) {
            this.snmpUserName = Objects.requireNonNull(map.get("snmpUserName")).toString();
        }
        if (map.get("snmpAuthPassphrase") != null) {
            this.snmpAuthPassphrase = Objects.requireNonNull(map.get("snmpAuthPassphrase")).toString();
        }
        if (map.get("snmpAuthPassphraseValid") != null) {
            this.snmpAuthPassphraseValid = !"false".equalsIgnoreCase(Objects.requireNonNull(map.get("snmpAuthPassphraseValid")).toString());
        }
        if (NumberUtil.isValidIntValue(map.get("snmpPrivAlgorithm"))) {
            this.snmpPrivAlgorithm = SNMPPrivAlgorithm.forCode(NumberUtil.getIntValue(map.get("snmpPrivAlgorithm"), -1));
        }
        if (map.get("snmpPrivPassphrase") != null) {
            this.snmpPrivPassphrase = Objects.requireNonNull(map.get("snmpPrivPassphrase")).toString();
        }
        if (map.get("snmpPrivPassphraseValid") != null) {
            this.snmpPrivPassphraseValid = !"false".equalsIgnoreCase(Objects.requireNonNull(map.get("snmpPrivPassphraseValid")).toString());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNetworkTaskId() {
        return networktaskid;
    }

    public void setNetworkTaskId(long networktaskid) {
        this.networktaskid = networktaskid;
    }

    public int getPingCount() {
        return pingCount;
    }

    public void setPingCount(int pingCount) {
        this.pingCount = pingCount;
    }

    public int getPingPackageSize() {
        return pingPackageSize;
    }

    public void setPingPackageSize(int pingPackageSize) {
        this.pingPackageSize = pingPackageSize;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public boolean isStopOnSuccess() {
        return stopOnSuccess;
    }

    public void setStopOnSuccess(boolean stopOnSuccess) {
        this.stopOnSuccess = stopOnSuccess;
    }

    public boolean isIgnoreSSLError() {
        return ignoreSSLError;
    }

    public void setIgnoreSSLError(boolean ignoreSSLError) {
        this.ignoreSSLError = ignoreSSLError;
    }

    public boolean isAllowLegacyTLS() {
        return allowLegacyTLS;
    }

    public void setAllowLegacyTLS(boolean allowLegacyTLS) {
        this.allowLegacyTLS = allowLegacyTLS;
    }

    public boolean isFailureOnCertificateExpiry() {
        return failureOnCertificateExpiry;
    }

    public void setFailureOnCertificateExpiry(boolean failureOnCertificateExpiry) {
        this.failureOnCertificateExpiry = failureOnCertificateExpiry;
    }

    public int getFailureOnCertificateExpiryDays() {
        return failureOnCertificateExpiryDays;
    }

    public void setFailureOnCertificateExpiryDays(int failureOnCertificateExpiryDays) {
        this.failureOnCertificateExpiryDays = failureOnCertificateExpiryDays;
    }

    public boolean isUseDefaultHeaders() {
        return useDefaultHeaders;
    }

    public void setUseDefaultHeaders(boolean useDefaultHeaders) {
        this.useDefaultHeaders = useDefaultHeaders;
    }

    public SNMPVersion getSnmpVersion() {
        return snmpVersion;
    }

    public void setSnmpVersion(SNMPVersion snmpVersion) {
        this.snmpVersion = snmpVersion;
    }

    public String getSnmpCommunity() {
        return snmpCommunity;
    }

    public void setSnmpCommunity(String snmpCommunity) {
        this.snmpCommunity = snmpCommunity;
    }

    public boolean isSnmpCommunityValid() {
        return snmpCommunityValid;
    }

    public void setSnmpCommunityValid(boolean snmpCommunityValid) {
        this.snmpCommunityValid = snmpCommunityValid;
    }

    public SNMPTransport getSnmpTransport() {
        return snmpTransport;
    }

    public void setSnmpTransport(SNMPTransport snmpTransport) {
        this.snmpTransport = snmpTransport;
    }

    public SNMPAuthAlgorithm getSnmpAuthAlgorithm() {
        return snmpAuthAlgorithm;
    }

    public void setSnmpAuthAlgorithm(SNMPAuthAlgorithm snmpAuthAlgorithm) {
        this.snmpAuthAlgorithm = snmpAuthAlgorithm;
    }

    public String getSnmpUserName() {
        return snmpUserName;
    }

    public void setSnmpUserName(String snmpUserName) {
        this.snmpUserName = snmpUserName;
    }

    public String getSnmpAuthPassphrase() {
        return snmpAuthPassphrase;
    }

    public void setSnmpAuthPassphrase(String snmpAuthPassphrase) {
        this.snmpAuthPassphrase = snmpAuthPassphrase;
    }

    public boolean isSnmpAuthPassphraseValid() {
        return snmpAuthPassphraseValid;
    }

    public void setSnmpAuthPassphraseValid(boolean snmpAuthPassphraseValid) {
        this.snmpAuthPassphraseValid = snmpAuthPassphraseValid;
    }

    public SNMPPrivAlgorithm getSnmpPrivAlgorithm() {
        return snmpPrivAlgorithm;
    }

    public void setSnmpPrivAlgorithm(SNMPPrivAlgorithm snmpPrivAlgorithm) {
        this.snmpPrivAlgorithm = snmpPrivAlgorithm;
    }

    public String getSnmpPrivPassphrase() {
        return snmpPrivPassphrase;
    }

    public void setSnmpPrivPassphrase(String snmpPrivPassphrase) {
        this.snmpPrivPassphrase = snmpPrivPassphrase;
    }

    public boolean isSnmpPrivPassphraseValid() {
        return snmpPrivPassphraseValid;
    }

    public void setSnmpPrivPassphraseValid(boolean snmpPrivPassphraseValid) {
        this.snmpPrivPassphraseValid = snmpPrivPassphraseValid;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        bundle.putInt("pingCount", pingCount);
        bundle.putInt("pingPackageSize", pingPackageSize);
        bundle.putInt("connectCount", connectCount);
        bundle.putInt("stopOnSuccess", stopOnSuccess ? 1 : 0);
        bundle.putInt("ignoreSSLError", ignoreSSLError ? 1 : 0);
        bundle.putInt("allowLegacyTLS", allowLegacyTLS ? 1 : 0);
        bundle.putInt("failureOnCertificateExpiry", failureOnCertificateExpiry ? 1 : 0);
        bundle.putInt("failureOnCertificateExpiryDays", failureOnCertificateExpiryDays);
        bundle.putInt("useDefaultHeaders", useDefaultHeaders ? 1 : 0);
        if (snmpVersion != null) {
            bundle.putInt("snmpVersion", snmpVersion.getCode());
        }
        if (snmpCommunity != null) {
            bundle.putString("snmpCommunity", snmpCommunity);
        }
        bundle.putInt("snmpCommunityValid", snmpCommunityValid ? 1 : 0);
        if (snmpTransport != null) {
            bundle.putInt("snmpTransport", snmpTransport.getCode());
        }
        if (snmpAuthAlgorithm != null) {
            bundle.putInt("snmpAuthAlgorithm", snmpAuthAlgorithm.getCode());
        }
        if (snmpUserName != null) {
            bundle.putString("snmpUserName", snmpUserName);
        }
        if (snmpAuthPassphrase != null) {
            bundle.putString("snmpAuthPassphrase", snmpAuthPassphrase);
        }
        bundle.putInt("snmpAuthPassphraseValid", snmpAuthPassphraseValid ? 1 : 0);
        if (snmpPrivAlgorithm != null) {
            bundle.putInt("snmpPrivAlgorithm", snmpPrivAlgorithm.getCode());
        }
        if (snmpPrivPassphrase != null) {
            bundle.putString("snmpPrivPassphrase", snmpPrivPassphrase);
        }
        bundle.putInt("snmpPrivPassphraseValid", snmpPrivPassphraseValid ? 1 : 0);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("networktaskid", networktaskid);
        map.put("pingCount", pingCount);
        map.put("pingPackageSize", pingPackageSize);
        map.put("connectCount", connectCount);
        map.put("stopOnSuccess", stopOnSuccess);
        map.put("ignoreSSLError", ignoreSSLError);
        map.put("allowLegacyTLS", allowLegacyTLS);
        map.put("failureOnCertificateExpiry", failureOnCertificateExpiry);
        map.put("failureOnCertificateExpiryDays", failureOnCertificateExpiryDays);
        map.put("useDefaultHeaders", useDefaultHeaders);
        if (snmpVersion != null) {
            map.put("snmpVersion", snmpVersion.getCode());
        }
        if (snmpCommunity != null) {
            map.put("snmpCommunity", snmpCommunity);
        }
        map.put("snmpCommunityValid", snmpCommunityValid);
        if (snmpTransport != null) {
            map.put("snmpTransport", snmpTransport.getCode());
        }
        if (snmpAuthAlgorithm != null) {
            map.put("snmpAuthAlgorithm", snmpAuthAlgorithm.getCode());
        }
        if (snmpUserName != null) {
            map.put("snmpUserName", snmpUserName);
        }
        if (snmpAuthPassphrase != null) {
            map.put("snmpAuthPassphrase", snmpAuthPassphrase);
        }
        map.put("snmpAuthPassphraseValid", snmpAuthPassphraseValid);
        if (snmpPrivAlgorithm != null) {
            map.put("snmpPrivAlgorithm", snmpPrivAlgorithm.getCode());
        }
        if (snmpPrivPassphrase != null) {
            map.put("snmpPrivPassphrase", snmpPrivPassphrase);
        }
        map.put("snmpPrivPassphraseValid", snmpPrivPassphraseValid);
        return map;
    }

    public boolean isEqual(AccessTypeData other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (pingCount != other.pingCount) {
            return false;
        }
        if (pingPackageSize != other.pingPackageSize) {
            return false;
        }
        if (connectCount != other.connectCount) {
            return false;
        }
        if (stopOnSuccess != other.stopOnSuccess) {
            return false;
        }
        if (useDefaultHeaders != other.useDefaultHeaders) {
            return false;
        }
        if (!Objects.equals(ignoreSSLError, other.ignoreSSLError)) {
            return false;
        }
        if (!Objects.equals(allowLegacyTLS, other.allowLegacyTLS)) {
            return false;
        }
        if (!Objects.equals(failureOnCertificateExpiry, other.failureOnCertificateExpiry)) {
            return false;
        }
        if (failureOnCertificateExpiryDays != other.failureOnCertificateExpiryDays) {
            return false;
        }
        if (!Objects.equals(snmpVersion, other.snmpVersion)) {
            return false;
        }
        if (!Objects.equals(snmpCommunity, other.snmpCommunity)) {
            return false;
        }
        if (snmpCommunityValid != other.snmpCommunityValid) {
            return false;
        }
        if (!Objects.equals(snmpTransport, other.snmpTransport)) {
            return false;
        }
        if (!Objects.equals(snmpAuthAlgorithm, other.snmpAuthAlgorithm)) {
            return false;
        }
        if (!Objects.equals(snmpUserName, other.snmpUserName)) {
            return false;
        }
        if (!Objects.equals(snmpAuthPassphrase, other.snmpAuthPassphrase)) {
            return false;
        }
        if (snmpAuthPassphraseValid != other.snmpAuthPassphraseValid) {
            return false;
        }
        if (!Objects.equals(snmpPrivAlgorithm, other.snmpPrivAlgorithm)) {
            return false;
        }
        if (!Objects.equals(snmpPrivPassphrase, other.snmpPrivPassphrase)) {
            return false;
        }
        return snmpPrivPassphraseValid == other.snmpPrivPassphraseValid;
    }

    public boolean isTechnicallyEqual(AccessTypeData other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (pingCount != other.pingCount) {
            return false;
        }
        if (pingPackageSize != other.pingPackageSize) {
            return false;
        }
        if (connectCount != other.connectCount) {
            return false;
        }
        if (stopOnSuccess != other.stopOnSuccess) {
            return false;
        }
        if (useDefaultHeaders != other.useDefaultHeaders) {
            return false;
        }
        if (!Objects.equals(ignoreSSLError, other.ignoreSSLError)) {
            return false;
        }
        if (!Objects.equals(allowLegacyTLS, other.allowLegacyTLS)) {
            return false;
        }
        if (!Objects.equals(failureOnCertificateExpiry, other.failureOnCertificateExpiry)) {
            return false;
        }
        if (failureOnCertificateExpiryDays != other.failureOnCertificateExpiryDays) {
            return false;
        }
        if (!Objects.equals(snmpVersion, other.snmpVersion)) {
            return false;
        }
        if (!Objects.equals(snmpCommunity, other.snmpCommunity)) {
            return false;
        }
        if (snmpCommunityValid != other.snmpCommunityValid) {
            return false;
        }
        if (!Objects.equals(snmpTransport, other.snmpTransport)) {
            return false;
        }
        if (!Objects.equals(snmpAuthAlgorithm, other.snmpAuthAlgorithm)) {
            return false;
        }
        if (!Objects.equals(snmpUserName, other.snmpUserName)) {
            return false;
        }
        if (!Objects.equals(snmpAuthPassphrase, other.snmpAuthPassphrase)) {
            return false;
        }
        if (snmpAuthPassphraseValid != other.snmpAuthPassphraseValid) {
            return false;
        }
        if (!Objects.equals(snmpPrivAlgorithm, other.snmpPrivAlgorithm)) {
            return false;
        }
        if (!Objects.equals(snmpPrivPassphrase, other.snmpPrivPassphrase)) {
            return false;
        }
        return snmpPrivPassphraseValid == other.snmpPrivPassphraseValid;
    }

    @NonNull
    @Override
    public String toString() {
        return "AccessTypeData{" +
                "id=" + id +
                ", networktaskid=" + networktaskid +
                ", pingCount=" + pingCount +
                ", pingPackageSize=" + pingPackageSize +
                ", connectCount=" + connectCount +
                ", stopOnSuccess=" + stopOnSuccess +
                ", ignoreSSLError=" + ignoreSSLError +
                ", allowLegacyTLS=" + allowLegacyTLS +
                ", failureOnCertificateExpiry=" + failureOnCertificateExpiry +
                ", failureOnCertificateExpiryDays=" + failureOnCertificateExpiryDays +
                ", useDefaultHeaders=" + useDefaultHeaders +
                ", snmpVersion=" + snmpVersion +
                ", snmpCommunity='" + StringUtil.maskSecret(snmpCommunity, true) + '\'' +
                ", snmpCommunityValid=" + snmpCommunityValid +
                ", snmpTransport=" + snmpTransport +
                ", snmpAuthAlgorithm=" + snmpAuthAlgorithm +
                ", snmpUserName='" + snmpUserName + '\'' +
                ", snmpAuthPassphrase='" + StringUtil.maskSecret(snmpAuthPassphrase, true) + '\'' +
                ", snmpAuthPassphraseValid=" + snmpAuthPassphraseValid +
                ", snmpPrivAlgorithm=" + snmpPrivAlgorithm +
                ", snmpPrivPassphrase='" + StringUtil.maskSecret(snmpPrivPassphrase, true) + '\'' +
                ", snmpPrivPassphraseValid=" + snmpPrivPassphraseValid +
                '}';
    }
}
