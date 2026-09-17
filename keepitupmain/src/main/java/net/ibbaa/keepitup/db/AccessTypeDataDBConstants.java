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

package net.ibbaa.keepitup.db;

import android.content.Context;

import net.ibbaa.keepitup.R;

public class AccessTypeDataDBConstants {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final String tableName;
    private final String idColumnName;
    private final String networkTaskIdColumnName;
    private final String pingCountColumnName;
    private final String pingPackageSizeColumnName;
    private final String connectCountColumnName;
    private final String stopOnSuccessColumnName;
    private final String ignoreSSLErrorColumnName;
    private final String allowLegacyTLSColumnName;
    private final String failureOnCertificateExpiryColumnName;
    private final String failureOnCertificateExpiryDaysColumnName;
    private final String useDefaultHeadersColumnName;
    private final String snmpVersionColumnName;
    private final String snmpCommunityColumnName;
    private final String snmpCommunityIVColumnName;
    private final String snmpTransportColumnName;
    private final String snmpAuthAlgorithmColumnName;
    private final String snmpUserNameColumnName;
    private final String snmpAuthPassphraseColumnName;
    private final String snmpAuthPassphraseIVColumnName;
    private final String snmpPrivAlgorithmColumnName;
    private final String snmpPrivPassphraseColumnName;
    private final String snmpPrivPassphraseIVColumnName;

    public AccessTypeDataDBConstants(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        tableName = context.getResources().getString(R.string.accesstypedata_table_name);
        idColumnName = context.getResources().getString(R.string.accesstypedata_id_column_name);
        networkTaskIdColumnName = context.getResources().getString(R.string.accesstypedata_taskid_column_name);
        pingCountColumnName = context.getResources().getString(R.string.accesstypedata_ping_count_column_name);
        pingPackageSizeColumnName = context.getResources().getString(R.string.accesstypedata_ping_package_size_column_name);
        connectCountColumnName = context.getResources().getString(R.string.accesstypedata_connect_count_column_name);
        stopOnSuccessColumnName = context.getResources().getString(R.string.accesstypedata_stop_on_success_column_name);
        ignoreSSLErrorColumnName = context.getResources().getString(R.string.accesstypedata_ignore_ssl_error_column_name);
        allowLegacyTLSColumnName = context.getResources().getString(R.string.accesstypedata_allow_legacy_tls_column_name);
        failureOnCertificateExpiryColumnName = context.getResources().getString(R.string.accesstypedata_failure_on_certificate_expiry_column_name);
        failureOnCertificateExpiryDaysColumnName = context.getResources().getString(R.string.accesstypedata_failure_on_certificate_expiry_days_column_name);
        useDefaultHeadersColumnName = context.getResources().getString(R.string.accesstypedata_use_default_headers_column_name);
        snmpVersionColumnName = context.getResources().getString(R.string.accesstypedata_snmp_version_column_name);
        snmpCommunityColumnName = context.getResources().getString(R.string.accesstypedata_snmp_community_column_name);
        snmpCommunityIVColumnName = context.getResources().getString(R.string.accesstypedata_snmp_community_iv_column_name);
        snmpTransportColumnName = context.getResources().getString(R.string.accesstypedata_snmp_transport_column_name);
        snmpAuthAlgorithmColumnName = context.getResources().getString(R.string.accesstypedata_snmp_auth_algorithm_column_name);
        snmpUserNameColumnName = context.getResources().getString(R.string.accesstypedata_snmp_user_name_column_name);
        snmpAuthPassphraseColumnName = context.getResources().getString(R.string.accesstypedata_snmp_auth_passphrase_column_name);
        snmpAuthPassphraseIVColumnName = context.getResources().getString(R.string.accesstypedata_snmp_auth_passphrase_iv_column_name);
        snmpPrivAlgorithmColumnName = context.getResources().getString(R.string.accesstypedata_snmp_priv_algorithm_column_name);
        snmpPrivPassphraseColumnName = context.getResources().getString(R.string.accesstypedata_snmp_priv_passphrase_column_name);
        snmpPrivPassphraseIVColumnName = context.getResources().getString(R.string.accesstypedata_snmp_priv_passphrase_iv_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getNetworkTaskIdColumnName() {
        return networkTaskIdColumnName;
    }

    public String getPingCountColumnName() {
        return pingCountColumnName;
    }

    public String getPingPackageSizeColumnName() {
        return pingPackageSizeColumnName;
    }

    public String getConnectCountColumnName() {
        return connectCountColumnName;
    }

    public String getStopOnSuccessColumnName() {
        return stopOnSuccessColumnName;
    }

    public String getIgnoreSSLErrorColumnName() {
        return ignoreSSLErrorColumnName;
    }

    public String getAllowLegacyTLSColumnName() {
        return allowLegacyTLSColumnName;
    }

    public String getFailureOnCertificateExpiryColumnName() {
        return failureOnCertificateExpiryColumnName;
    }

    public String getFailureOnCertificateExpiryDaysColumnName() {
        return failureOnCertificateExpiryDaysColumnName;
    }

    public String getUseDefaultHeadersColumnName() {
        return useDefaultHeadersColumnName;
    }

    public String getSnmpVersionColumnName() {
        return snmpVersionColumnName;
    }

    public String getSnmpCommunityColumnName() {
        return snmpCommunityColumnName;
    }

    public String getSnmpCommunityIVColumnName() {
        return snmpCommunityIVColumnName;
    }

    public String getSnmpTransportColumnName() {
        return snmpTransportColumnName;
    }

    public String getSnmpAuthAlgorithmColumnName() {
        return snmpAuthAlgorithmColumnName;
    }

    public String getSnmpUserNameColumnName() {
        return snmpUserNameColumnName;
    }

    public String getSnmpAuthPassphraseColumnName() {
        return snmpAuthPassphraseColumnName;
    }

    public String getSnmpAuthPassphraseIVColumnName() {
        return snmpAuthPassphraseIVColumnName;
    }

    public String getSnmpPrivAlgorithmColumnName() {
        return snmpPrivAlgorithmColumnName;
    }

    public String getSnmpPrivPassphraseColumnName() {
        return snmpPrivPassphraseColumnName;
    }

    public String getSnmpPrivPassphraseIVColumnName() {
        return snmpPrivPassphraseIVColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER, " +
                getAllowLegacyTLSColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryDaysColumnName() + " INTEGER, " +
                getUseDefaultHeadersColumnName() + " INTEGER, " +
                getSnmpVersionColumnName() + " INTEGER, " +
                getSnmpCommunityColumnName() + " TEXT, " +
                getSnmpCommunityIVColumnName() + " TEXT, " +
                getSnmpTransportColumnName() + " INTEGER, " +
                getSnmpAuthAlgorithmColumnName() + " INTEGER, " +
                getSnmpUserNameColumnName() + " TEXT, " +
                getSnmpAuthPassphraseColumnName() + " TEXT, " +
                getSnmpAuthPassphraseIVColumnName() + " TEXT, " +
                getSnmpPrivAlgorithmColumnName() + " INTEGER, " +
                getSnmpPrivPassphraseColumnName() + " TEXT, " +
                getSnmpPrivPassphraseIVColumnName() + " TEXT);";
    }

    public String getCreateTableStatementWithoutAllowLegacyTLSCertificateExpiryAndSnmpV3Columns() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER, " +
                getUseDefaultHeadersColumnName() + " INTEGER, " +
                getSnmpVersionColumnName() + " INTEGER, " +
                getSnmpCommunityColumnName() + " TEXT, " +
                getSnmpCommunityIVColumnName() + " TEXT);";
    }

    public String getCreateTableStatementWithoutStopOnSuccess() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER, " +
                getAllowLegacyTLSColumnName() + " INTEGER, " +
                getUseDefaultHeadersColumnName() + " INTEGER, " +
                getSnmpVersionColumnName() + " INTEGER, " +
                getSnmpCommunityColumnName() + " TEXT, " +
                getSnmpCommunityIVColumnName() + " TEXT, " +
                getFailureOnCertificateExpiryColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryDaysColumnName() + " INTEGER, " +
                getSnmpTransportColumnName() + " INTEGER, " +
                getSnmpAuthAlgorithmColumnName() + " INTEGER, " +
                getSnmpUserNameColumnName() + " TEXT, " +
                getSnmpAuthPassphraseColumnName() + " TEXT, " +
                getSnmpAuthPassphraseIVColumnName() + " TEXT, " +
                getSnmpPrivAlgorithmColumnName() + " INTEGER, " +
                getSnmpPrivPassphraseColumnName() + " TEXT, " +
                getSnmpPrivPassphraseIVColumnName() + " TEXT);";
    }

    public String getCreateTableStatementWithoutIgnoreSSLError() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER, " +
                getAllowLegacyTLSColumnName() + " INTEGER, " +
                getUseDefaultHeadersColumnName() + " INTEGER, " +
                getSnmpVersionColumnName() + " INTEGER, " +
                getSnmpCommunityColumnName() + " TEXT, " +
                getSnmpCommunityIVColumnName() + " TEXT, " +
                getFailureOnCertificateExpiryColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryDaysColumnName() + " INTEGER, " +
                getSnmpTransportColumnName() + " INTEGER, " +
                getSnmpAuthAlgorithmColumnName() + " INTEGER, " +
                getSnmpUserNameColumnName() + " TEXT, " +
                getSnmpAuthPassphraseColumnName() + " TEXT, " +
                getSnmpAuthPassphraseIVColumnName() + " TEXT, " +
                getSnmpPrivAlgorithmColumnName() + " INTEGER, " +
                getSnmpPrivPassphraseColumnName() + " TEXT, " +
                getSnmpPrivPassphraseIVColumnName() + " TEXT);";
    }

    public String getCreateTableStatementWithoutUseDefaultHeaders() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER, " +
                getAllowLegacyTLSColumnName() + " INTEGER, " +
                getSnmpVersionColumnName() + " INTEGER, " +
                getSnmpCommunityColumnName() + " TEXT, " +
                getSnmpCommunityIVColumnName() + " TEXT, " +
                getFailureOnCertificateExpiryColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryDaysColumnName() + " INTEGER, " +
                getSnmpTransportColumnName() + " INTEGER, " +
                getSnmpAuthAlgorithmColumnName() + " INTEGER, " +
                getSnmpUserNameColumnName() + " TEXT, " +
                getSnmpAuthPassphraseColumnName() + " TEXT, " +
                getSnmpAuthPassphraseIVColumnName() + " TEXT, " +
                getSnmpPrivAlgorithmColumnName() + " INTEGER, " +
                getSnmpPrivPassphraseColumnName() + " TEXT, " +
                getSnmpPrivPassphraseIVColumnName() + " TEXT);";
    }

    public String getCreateTableStatementWithoutSnmpVersionAndCommunityColumns() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER, " +
                getStopOnSuccessColumnName() + " INTEGER, " +
                getIgnoreSSLErrorColumnName() + " INTEGER, " +
                getAllowLegacyTLSColumnName() + " INTEGER, " +
                getUseDefaultHeadersColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryColumnName() + " INTEGER, " +
                getFailureOnCertificateExpiryDaysColumnName() + " INTEGER, " +
                getSnmpTransportColumnName() + " INTEGER, " +
                getSnmpAuthAlgorithmColumnName() + " INTEGER, " +
                getSnmpUserNameColumnName() + " TEXT, " +
                getSnmpAuthPassphraseColumnName() + " TEXT, " +
                getSnmpAuthPassphraseIVColumnName() + " TEXT, " +
                getSnmpPrivAlgorithmColumnName() + " INTEGER, " +
                getSnmpPrivPassphraseColumnName() + " TEXT, " +
                getSnmpPrivPassphraseIVColumnName() + " TEXT);";
    }

    public String getCreateTableStatementWithoutAddedColumns() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getPingCountColumnName() + " INTEGER, " +
                getPingPackageSizeColumnName() + " INTEGER, " +
                getConnectCountColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadAccessTypeDataForNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getPingCountColumnName() + ", " +
                getPingPackageSizeColumnName() + ", " +
                getConnectCountColumnName() + ", " +
                getStopOnSuccessColumnName() + ", " +
                getIgnoreSSLErrorColumnName() + ", " +
                getAllowLegacyTLSColumnName() + ", " +
                getUseDefaultHeadersColumnName() + ", " +
                getSnmpVersionColumnName() + ", " +
                getSnmpCommunityColumnName() + ", " +
                getSnmpCommunityIVColumnName() + ", " +
                getFailureOnCertificateExpiryColumnName() + ", " +
                getFailureOnCertificateExpiryDaysColumnName() + ", " +
                getSnmpTransportColumnName() + ", " +
                getSnmpAuthAlgorithmColumnName() + ", " +
                getSnmpUserNameColumnName() + ", " +
                getSnmpAuthPassphraseColumnName() + ", " +
                getSnmpAuthPassphraseIVColumnName() + ", " +
                getSnmpPrivAlgorithmColumnName() + ", " +
                getSnmpPrivPassphraseColumnName() + ", " +
                getSnmpPrivPassphraseIVColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?";
    }

    public String getReadAllAccessTypeDataStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getPingCountColumnName() + ", " +
                getPingPackageSizeColumnName() + ", " +
                getConnectCountColumnName() + ", " +
                getStopOnSuccessColumnName() + ", " +
                getIgnoreSSLErrorColumnName() + ", " +
                getAllowLegacyTLSColumnName() + ", " +
                getUseDefaultHeadersColumnName() + ", " +
                getSnmpVersionColumnName() + ", " +
                getSnmpCommunityColumnName() + ", " +
                getSnmpCommunityIVColumnName() + ", " +
                getFailureOnCertificateExpiryColumnName() + ", " +
                getFailureOnCertificateExpiryDaysColumnName() + ", " +
                getSnmpTransportColumnName() + ", " +
                getSnmpAuthAlgorithmColumnName() + ", " +
                getSnmpUserNameColumnName() + ", " +
                getSnmpAuthPassphraseColumnName() + ", " +
                getSnmpAuthPassphraseIVColumnName() + ", " +
                getSnmpPrivAlgorithmColumnName() + ", " +
                getSnmpPrivPassphraseColumnName() + ", " +
                getSnmpPrivPassphraseIVColumnName() +
                " FROM " + getTableName();
    }

    public String getReadEncryptedCommunityAndCommunityIV() {
        return "SELECT " +
                getSnmpCommunityColumnName() + ", " +
                getSnmpCommunityIVColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getIdColumnName() + " = ?";
    }

    public String getReadEncryptedAuthPassphraseAndAuthPassphraseIV() {
        return "SELECT " +
                getSnmpAuthPassphraseColumnName() + ", " +
                getSnmpAuthPassphraseIVColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getIdColumnName() + " = ?";
    }

    public String getReadEncryptedPrivPassphraseAndPrivPassphraseIV() {
        return "SELECT " +
                getSnmpPrivPassphraseColumnName() + ", " +
                getSnmpPrivPassphraseIVColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getIdColumnName() + " = ?";
    }

    public String getDeleteOrphanAccessTypeDataStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " NOT IN (SELECT " + networkTaskDBConstants.getIdColumnName() + " FROM " + networkTaskDBConstants.getTableName() + ");";
    }

    public String getMigrateNetworkTasksAccessTypeDataStatement() {
        return "INSERT INTO " + getTableName() + "(" +
                getNetworkTaskIdColumnName() + ") SELECT " +
                networkTaskDBConstants.getIdColumnName() +
                " FROM " + networkTaskDBConstants.getTableName();
    }

    public String getAddStopOnSuccessColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getStopOnSuccessColumnName() + " INTEGER;";
    }

    public String getAddIgnoreSSLErrorColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getIgnoreSSLErrorColumnName() + " INTEGER;";
    }

    public String getAddAllowLegacyTLSColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getAllowLegacyTLSColumnName() + " INTEGER;";
    }

    public String getAddFailureOnCertificateExpiryColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getFailureOnCertificateExpiryColumnName() + " INTEGER;";
    }

    public String getAddFailureOnCertificateExpiryDaysColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getFailureOnCertificateExpiryDaysColumnName() + " INTEGER;";
    }

    public String getDropStopOnSuccessColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getStopOnSuccessColumnName() + ";";
    }

    public String getDropIgnoreSSLErrorColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getIgnoreSSLErrorColumnName() + ";";
    }

    public String getDropAllowLegacyTLSColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getAllowLegacyTLSColumnName() + ";";
    }

    public String getDropFailureOnCertificateExpiryColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getFailureOnCertificateExpiryColumnName() + ";";
    }

    public String getDropFailureOnCertificateExpiryDaysColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getFailureOnCertificateExpiryDaysColumnName() + ";";
    }

    public String getAddUseDefaultHeadersColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getUseDefaultHeadersColumnName() + " INTEGER;";
    }

    public String getDropUseDefaultHeadersColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getUseDefaultHeadersColumnName() + " INTEGER;";
    }

    public String getAddSnmpVersionColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpVersionColumnName() + " INTEGER;";
    }

    public String getDropSnmpVersionColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpVersionColumnName() + ";";
    }

    public String getAddSnmpCommunityColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpCommunityColumnName() + " TEXT;";
    }

    public String getDropSnmpCommunityColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpCommunityColumnName() + ";";
    }

    public String getAddSnmpCommunityIVColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpCommunityIVColumnName() + " TEXT;";
    }

    public String getDropSnmpCommunityIVColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpCommunityIVColumnName() + ";";
    }

    public String getAddSnmpTransportColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpTransportColumnName() + " INTEGER;";
    }

    public String getDropSnmpTransportColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpTransportColumnName() + ";";
    }

    public String getAddSnmpAuthAlgorithmColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpAuthAlgorithmColumnName() + " INTEGER;";
    }

    public String getDropSnmpAuthAlgorithmColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpAuthAlgorithmColumnName() + ";";
    }

    public String getAddSnmpUserNameColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpUserNameColumnName() + " TEXT;";
    }

    public String getDropSnmpUserNameColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpUserNameColumnName() + ";";
    }

    public String getAddSnmpAuthPassphraseColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpAuthPassphraseColumnName() + " TEXT;";
    }

    public String getDropSnmpAuthPassphraseColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpAuthPassphraseColumnName() + ";";
    }

    public String getAddSnmpAuthPassphraseIVColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpAuthPassphraseIVColumnName() + " TEXT;";
    }

    public String getDropSnmpAuthPassphraseIVColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpAuthPassphraseIVColumnName() + ";";
    }

    public String getAddSnmpPrivAlgorithmColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpPrivAlgorithmColumnName() + " INTEGER;";
    }

    public String getDropSnmpPrivAlgorithmColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpPrivAlgorithmColumnName() + ";";
    }

    public String getAddSnmpPrivPassphraseColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpPrivPassphraseColumnName() + " TEXT;";
    }

    public String getDropSnmpPrivPassphraseColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpPrivPassphraseColumnName() + ";";
    }

    public String getAddSnmpPrivPassphraseIVColumnStatement() {
        return "ALTER TABLE " + getTableName() + " ADD COLUMN " + getSnmpPrivPassphraseIVColumnName() + " TEXT;";
    }

    public String getDropSnmpPrivPassphraseIVColumnStatement() {
        return "ALTER TABLE " + getTableName() + " DROP COLUMN " + getSnmpPrivPassphraseIVColumnName() + ";";
    }
}
