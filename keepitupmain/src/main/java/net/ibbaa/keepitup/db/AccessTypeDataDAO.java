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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.resources.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessTypeDataDAO extends BaseDAO {

    public AccessTypeDataDAO(Context context) {
        super(context);
    }

    public AccessTypeData insertAccessTypeData(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataDAO.class.getName(), "insertAccessTypeData, accessTypeData is " + accessTypeData);
        AccessTypeData returnedAccessTypeData = executeDBOperationInTransaction(accessTypeData, this::insertAccessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "Inserted accessTypeData is " + returnedAccessTypeData);
        dumpDatabase("Dump after insertAccessTypeData call");
        return returnedAccessTypeData;
    }

    public AccessTypeData updateAccessTypeData(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataDAO.class.getName(), "updateAccessTypeData, accessTypeData is " + accessTypeData);
        AccessTypeData returnedAccessTypeData = executeDBOperationInTransaction(accessTypeData, this::updateAccessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "Updated accessTypeData is " + returnedAccessTypeData);
        dumpDatabase("Dump after updateAccessTypeData call");
        return returnedAccessTypeData;
    }

    public AccessTypeData readAccessTypeDataForNetworkTask(long networkTaskId) {
        Log.d(AccessTypeDataDAO.class.getName(), "readAccessTypeDataForNetworkTask, network task id is " + networkTaskId);
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setNetworkTaskId(networkTaskId);
        accessTypeData = executeDBOperationInTransaction(accessTypeData, this::readAccessTypeDataForNetworkTask);
        Log.d(AccessTypeDataDAO.class.getName(), "Read accessTypeData is " + accessTypeData);
        return accessTypeData;
    }

    public List<AccessTypeData> readAllAccessTypeData() {
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeData");
        List<AccessTypeData> accessTypeDataList = executeDBOperationInTransaction((AccessTypeData) null, this::readAllAccessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "Number of accessTypeData read: " + accessTypeDataList.size());
        return accessTypeDataList;
    }

    public Map<Long, AccessTypeData> readAllAccessTypeDataForNetworkTasks() {
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeDataForNetworkTasks");
        return executeDBOperationInTransaction((AccessTypeData) null, this::readAllAccessTypeDataForNetworkTasks);
    }

    public void deleteAccessTypeDataForNetworkTask(long networkTaskId) {
        Log.d(AccessTypeDataDAO.class.getName(), "deleteAccessTypeDataForNetworkTask, network task id is " + networkTaskId);
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(accessTypeData, this::deleteAccessTypeDataForNetworkTask);
        dumpDatabase("Dump after deleteAccessTypeDataForNetworkTask call");
    }

    public void deleteAllOrphanAccessTypeData() {
        Log.d(AccessTypeDataDAO.class.getName(), "deleteAllOrphanAccessTypeData");
        executeDBOperationInTransaction((AccessTypeData) null, this::deleteAllOrphanAccessTypeData);
        dumpDatabase("Dump after deleteAllOrphanAccessTypeData call");
    }

    public void deleteAllAccessTypeData() {
        Log.d(AccessTypeDataDAO.class.getName(), "deleteAllAccessTypeData");
        executeDBOperationInTransaction((AccessTypeData) null, this::deleteAllAccessTypeData);
        dumpDatabase("Dump after deleteAllAccessTypeData call");
    }

    private boolean encrypt(ContentValues values, AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataDAO.class.getName(), "encrypt, accessTypeData is " + accessTypeData);
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        boolean community = encryptSecretField(values, dbConstants.getSnmpCommunityColumnName(), dbConstants.getSnmpCommunityIVColumnName(), accessTypeData.getSnmpCommunity(), accessTypeData::setSnmpCommunity, accessTypeData::setSnmpCommunityValid);
        boolean auth = encryptSecretField(values, dbConstants.getSnmpAuthPassphraseColumnName(), dbConstants.getSnmpAuthPassphraseIVColumnName(), accessTypeData.getSnmpAuthPassphrase(), accessTypeData::setSnmpAuthPassphrase, accessTypeData::setSnmpAuthPassphraseValid);
        boolean priv = encryptSecretField(values, dbConstants.getSnmpPrivPassphraseColumnName(), dbConstants.getSnmpPrivPassphraseIVColumnName(), accessTypeData.getSnmpPrivPassphrase(), accessTypeData::setSnmpPrivPassphrase, accessTypeData::setSnmpPrivPassphraseValid);
        return community && auth && priv;
    }

    private void decrypt(Cursor cursor, AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataDAO.class.getName(), "decrypt, accessTypeData is " + accessTypeData);
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        decryptSecretField(cursor, dbConstants.getSnmpCommunityColumnName(), dbConstants.getSnmpCommunityIVColumnName(), accessTypeData::setSnmpCommunity, accessTypeData::setSnmpCommunityValid);
        decryptSecretField(cursor, dbConstants.getSnmpAuthPassphraseColumnName(), dbConstants.getSnmpAuthPassphraseIVColumnName(), accessTypeData::setSnmpAuthPassphrase, accessTypeData::setSnmpAuthPassphraseValid);
        decryptSecretField(cursor, dbConstants.getSnmpPrivPassphraseColumnName(), dbConstants.getSnmpPrivPassphraseIVColumnName(), accessTypeData::setSnmpPrivPassphrase, accessTypeData::setSnmpPrivPassphraseValid);
    }

    private boolean encryptSecretField(ContentValues values, String valueColumn, String ivColumn, String plainText, StringSetter valueSetter, BooleanSetter validSetter) {
        values.putNull(ivColumn);
        if (plainText == null) {
            values.putNull(valueColumn);
            validSetter.set(true);
            return true;
        }
        EncryptResult result = encrypt(values, valueColumn, ivColumn, plainText);
        if (!result.success()) {
            values.putNull(valueColumn);
            valueSetter.set(null);
            validSetter.set(false);
            return false;
        }
        validSetter.set(true);
        return true;
    }

    private void decryptSecretField(Cursor cursor, String valueColumn, String ivColumn, StringSetter valueSetter, BooleanSetter validSetter) {
        int indexIVColumn = cursor.getColumnIndex(ivColumn);
        if (!cursor.isNull(indexIVColumn)) {
            DecryptResult result = decrypt(cursor, valueColumn, ivColumn);
            valueSetter.set(result.success() ? result.plainText() : null);
            validSetter.set(result.success());
            return;
        }
        valueSetter.set(null);
        validSetter.set(true);
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(AccessTypeDataDAO.class.getName(), message, AccessTypeData.class.getSimpleName().toLowerCase(), this::readAllAccessTypeData);
        }
    }

    private AccessTypeData insertAccessTypeData(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "insertAccessTypeData, accessTypeData is " + accessTypeData);
        ContentValues values = new ContentValues();
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), accessTypeData.getNetworkTaskId());
        values.put(dbConstants.getPingCountColumnName(), accessTypeData.getPingCount());
        values.put(dbConstants.getPingPackageSizeColumnName(), accessTypeData.getPingPackageSize());
        values.put(dbConstants.getConnectCountColumnName(), accessTypeData.getConnectCount());
        values.put(dbConstants.getStopOnSuccessColumnName(), accessTypeData.isStopOnSuccess() ? 1 : 0);
        values.put(dbConstants.getIgnoreSSLErrorColumnName(), accessTypeData.isIgnoreSSLError() ? 1 : 0);
        values.put(dbConstants.getAllowLegacyTLSColumnName(), accessTypeData.isAllowLegacyTLS() ? 1 : 0);
        values.put(dbConstants.getFailureOnCertificateExpiryColumnName(), accessTypeData.isFailureOnCertificateExpiry() ? 1 : 0);
        values.put(dbConstants.getFailureOnCertificateExpiryDaysColumnName(), accessTypeData.getFailureOnCertificateExpiryDays());
        values.put(dbConstants.getUseDefaultHeadersColumnName(), accessTypeData.isUseDefaultHeaders() ? 1 : 0);
        values.put(dbConstants.getSnmpVersionColumnName(), accessTypeData.getSnmpVersion() == null ? null : accessTypeData.getSnmpVersion().getCode());
        values.put(dbConstants.getSnmpTransportColumnName(), accessTypeData.getSnmpTransport() == null ? null : accessTypeData.getSnmpTransport().getCode());
        values.put(dbConstants.getSnmpAuthAlgorithmColumnName(), accessTypeData.getSnmpAuthAlgorithm() == null ? null : accessTypeData.getSnmpAuthAlgorithm().getCode());
        values.put(dbConstants.getSnmpUserNameColumnName(), accessTypeData.getSnmpUserName());
        values.put(dbConstants.getSnmpPrivAlgorithmColumnName(), accessTypeData.getSnmpPrivAlgorithm() == null ? null : accessTypeData.getSnmpPrivAlgorithm().getCode());
        boolean encryptSuccess = encrypt(values, accessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "encryptSuccess is " + encryptSuccess);
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(AccessTypeDataDAO.class.getName(), "Error inserting accessTypeData into database. Insert returned -1.");
        }
        accessTypeData.setId(rowid);
        return accessTypeData;
    }

    private AccessTypeData updateAccessTypeData(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "updateAccessTypeData, accessTypeData is " + accessTypeData);
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(accessTypeData.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getNetworkTaskIdColumnName(), accessTypeData.getNetworkTaskId());
        values.put(dbConstants.getPingCountColumnName(), accessTypeData.getPingCount());
        values.put(dbConstants.getPingPackageSizeColumnName(), accessTypeData.getPingPackageSize());
        values.put(dbConstants.getConnectCountColumnName(), accessTypeData.getConnectCount());
        values.put(dbConstants.getStopOnSuccessColumnName(), accessTypeData.isStopOnSuccess() ? 1 : 0);
        values.put(dbConstants.getIgnoreSSLErrorColumnName(), accessTypeData.isIgnoreSSLError() ? 1 : 0);
        values.put(dbConstants.getAllowLegacyTLSColumnName(), accessTypeData.isAllowLegacyTLS() ? 1 : 0);
        values.put(dbConstants.getFailureOnCertificateExpiryColumnName(), accessTypeData.isFailureOnCertificateExpiry() ? 1 : 0);
        values.put(dbConstants.getFailureOnCertificateExpiryDaysColumnName(), accessTypeData.getFailureOnCertificateExpiryDays());
        values.put(dbConstants.getUseDefaultHeadersColumnName(), accessTypeData.isUseDefaultHeaders() ? 1 : 0);
        values.put(dbConstants.getSnmpVersionColumnName(), accessTypeData.getSnmpVersion() == null ? null : accessTypeData.getSnmpVersion().getCode());
        values.put(dbConstants.getSnmpTransportColumnName(), accessTypeData.getSnmpTransport() == null ? null : accessTypeData.getSnmpTransport().getCode());
        values.put(dbConstants.getSnmpAuthAlgorithmColumnName(), accessTypeData.getSnmpAuthAlgorithm() == null ? null : accessTypeData.getSnmpAuthAlgorithm().getCode());
        values.put(dbConstants.getSnmpUserNameColumnName(), accessTypeData.getSnmpUserName());
        values.put(dbConstants.getSnmpPrivAlgorithmColumnName(), accessTypeData.getSnmpPrivAlgorithm() == null ? null : accessTypeData.getSnmpPrivAlgorithm().getCode());
        boolean encryptSuccess = encrypt(values, accessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "encryptSuccess is " + encryptSuccess);
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return accessTypeData;
    }

    private AccessTypeData readAccessTypeDataForNetworkTask(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "readAccessTypeDataForNetworkTask, accessTypeData is " + accessTypeData);
        Cursor cursor = null;
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        try {
            Log.d(AccessTypeDataDAO.class.getName(), "Executing SQL " + dbConstants.getReadAccessTypeDataForNetworkTaskStatement() + " with a parameter of " + accessTypeData.getNetworkTaskId());
            cursor = db.rawQuery(dbConstants.getReadAccessTypeDataForNetworkTaskStatement(), new String[]{String.valueOf(accessTypeData.getNetworkTaskId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    return mapCursorToAccessTypeData(cursor);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(AccessTypeDataDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(AccessTypeDataDAO.class.getName(), "no accessTypeData found, returning null");
        return null;
    }

    private List<AccessTypeData> readAllAccessTypeData(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeData, accessTypeData is " + accessTypeData);
        List<AccessTypeData> result = new ArrayList<>();
        readAllAccessTypeDataInternal(db, result::add);
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeData, returning " + result);
        return result;
    }

    private Map<Long, AccessTypeData> readAllAccessTypeDataForNetworkTasks(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeDataForNetworkTasks, accessTypeData is " + accessTypeData);
        Map<Long, AccessTypeData> result = new HashMap<>();
        readAllAccessTypeDataInternal(db, mappedAccessTypeData -> result.put(mappedAccessTypeData.getNetworkTaskId(), mappedAccessTypeData));
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeDataForNetworkTasks, returning " + result);
        return result;
    }

    private void readAllAccessTypeDataInternal(SQLiteDatabase db, AccessTypeDataCollector collector) {
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeDataInternal");
        Cursor cursor = null;
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        try {
            Log.d(AccessTypeDataDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllAccessTypeDataStatement());
            cursor = db.rawQuery(dbConstants.getReadAllAccessTypeDataStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    collector.collect(mapCursorToAccessTypeData(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(AccessTypeDataDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
    }

    private int deleteAccessTypeDataForNetworkTask(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "deleteAccessTypeDataForNetworkTask, accessTypeData is " + accessTypeData);
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        String selection = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(accessTypeData.getNetworkTaskId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteAllAccessTypeData(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "deleteAllAccessTypeData, accessTypeData is " + accessTypeData);
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int deleteAllOrphanAccessTypeData(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Log.d(AccessTypeDataDAO.class.getName(), "deleteAllOrphanAccessTypeData, accessTypeData is " + accessTypeData);
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        Log.d(AccessTypeDataDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteOrphanAccessTypeDataStatement());
        db.execSQL(dbConstants.getDeleteOrphanAccessTypeDataStatement());
        return -1;
    }

    private AccessTypeData mapCursorToAccessTypeData(Cursor cursor) {
        AccessTypeData accessTypeData = new AccessTypeData();
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexPingCountColumn = cursor.getColumnIndex(dbConstants.getPingCountColumnName());
        int indexPingPackageSizeColumn = cursor.getColumnIndex(dbConstants.getPingPackageSizeColumnName());
        int indexConnectCountColumn = cursor.getColumnIndex(dbConstants.getConnectCountColumnName());
        int indexStopOnSuccessColumn = cursor.getColumnIndex(dbConstants.getStopOnSuccessColumnName());
        int indexIgnoreSSLErrorColumn = cursor.getColumnIndex(dbConstants.getIgnoreSSLErrorColumnName());
        int indexAllowLegacyTLSColumn = cursor.getColumnIndex(dbConstants.getAllowLegacyTLSColumnName());
        int indexFailureOnCertificateExpiryColumn = cursor.getColumnIndex(dbConstants.getFailureOnCertificateExpiryColumnName());
        int indexFailureOnCertificateExpiryDaysColumn = cursor.getColumnIndex(dbConstants.getFailureOnCertificateExpiryDaysColumnName());
        int indexUseDefaultHeadersColumn = cursor.getColumnIndex(dbConstants.getUseDefaultHeadersColumnName());
        int indexSnmpVersionColumn = cursor.getColumnIndex(dbConstants.getSnmpVersionColumnName());
        int indexSnmpTransportColumn = cursor.getColumnIndex(dbConstants.getSnmpTransportColumnName());
        int indexSnmpAuthAlgorithmColumn = cursor.getColumnIndex(dbConstants.getSnmpAuthAlgorithmColumnName());
        int indexSnmpUserNameColumn = cursor.getColumnIndex(dbConstants.getSnmpUserNameColumnName());
        int indexSnmpPrivAlgorithmColumn = cursor.getColumnIndex(dbConstants.getSnmpPrivAlgorithmColumnName());
        if (!cursor.isNull(indexIdColumn)) {
            accessTypeData.setId(cursor.getLong(indexIdColumn));
        }
        if (!cursor.isNull(indexNetworkTaskIdColumn)) {
            accessTypeData.setNetworkTaskId(cursor.getLong(indexNetworkTaskIdColumn));
        }
        if (!cursor.isNull(indexPingCountColumn)) {
            accessTypeData.setPingCount(cursor.getInt(indexPingCountColumn));
        } else {
            accessTypeData.setPingCount(preferenceManager.getPreferencePingCount());
        }
        if (!cursor.isNull(indexPingPackageSizeColumn)) {
            accessTypeData.setPingPackageSize(cursor.getInt(indexPingPackageSizeColumn));
        } else {
            accessTypeData.setPingPackageSize(preferenceManager.getPreferencePingPackageSize());
        }
        if (!cursor.isNull(indexConnectCountColumn)) {
            accessTypeData.setConnectCount(cursor.getInt(indexConnectCountColumn));
        } else {
            accessTypeData.setConnectCount(preferenceManager.getPreferenceConnectCount());
        }
        if (!cursor.isNull(indexStopOnSuccessColumn)) {
            accessTypeData.setStopOnSuccess(cursor.getInt(indexStopOnSuccessColumn) >= 1);
        } else {
            accessTypeData.setStopOnSuccess(preferenceManager.getPreferenceStopOnSuccess());
        }
        if (!cursor.isNull(indexIgnoreSSLErrorColumn)) {
            accessTypeData.setIgnoreSSLError(cursor.getInt(indexIgnoreSSLErrorColumn) >= 1);
        } else {
            accessTypeData.setIgnoreSSLError(preferenceManager.getPreferenceIgnoreSSLError());
        }
        if (!cursor.isNull(indexAllowLegacyTLSColumn)) {
            accessTypeData.setAllowLegacyTLS(cursor.getInt(indexAllowLegacyTLSColumn) >= 1);
        } else {
            accessTypeData.setAllowLegacyTLS(preferenceManager.getPreferenceAllowLegacyTLS());
        }
        if (!cursor.isNull(indexFailureOnCertificateExpiryColumn)) {
            accessTypeData.setFailureOnCertificateExpiry(cursor.getInt(indexFailureOnCertificateExpiryColumn) >= 1);
        } else {
            accessTypeData.setFailureOnCertificateExpiry(preferenceManager.getPreferenceFailureOnCertificateExpiry());
        }
        if (!cursor.isNull(indexFailureOnCertificateExpiryDaysColumn)) {
            accessTypeData.setFailureOnCertificateExpiryDays(cursor.getInt(indexFailureOnCertificateExpiryDaysColumn));
        } else {
            accessTypeData.setFailureOnCertificateExpiryDays(preferenceManager.getPreferenceFailureOnCertificateExpiryDays());
        }
        if (!cursor.isNull(indexUseDefaultHeadersColumn)) {
            accessTypeData.setUseDefaultHeaders(cursor.getInt(indexUseDefaultHeadersColumn) >= 1);
        } else {
            accessTypeData.setUseDefaultHeaders(preferenceManager.getPreferenceUseDefaultHeaders());
        }
        if (!cursor.isNull(indexSnmpVersionColumn)) {
            accessTypeData.setSnmpVersion(SNMPVersion.forCode(cursor.getInt(indexSnmpVersionColumn)));
        } else {
            accessTypeData.setSnmpVersion(preferenceManager.getPreferenceSNMPVersion());
        }
        if (!cursor.isNull(indexSnmpTransportColumn)) {
            accessTypeData.setSnmpTransport(SNMPTransport.forCode(cursor.getInt(indexSnmpTransportColumn)));
        } else {
            accessTypeData.setSnmpTransport(preferenceManager.getPreferenceSNMPTransport());
        }
        if (!cursor.isNull(indexSnmpAuthAlgorithmColumn)) {
            accessTypeData.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.forCode(cursor.getInt(indexSnmpAuthAlgorithmColumn)));
        } else {
            accessTypeData.setSnmpAuthAlgorithm(preferenceManager.getPreferenceSNMPAuthAlgorithm());
        }
        accessTypeData.setSnmpUserName(cursor.getString(indexSnmpUserNameColumn));
        if (!cursor.isNull(indexSnmpPrivAlgorithmColumn)) {
            accessTypeData.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.forCode(cursor.getInt(indexSnmpPrivAlgorithmColumn)));
        } else {
            accessTypeData.setSnmpPrivAlgorithm(preferenceManager.getPreferenceSNMPPrivAlgorithm());
        }
        decrypt(cursor, accessTypeData);
        return accessTypeData;
    }

    @FunctionalInterface
    private interface AccessTypeDataCollector {
        void collect(AccessTypeData data);
    }

    @SuppressWarnings({"unused"})
    @FunctionalInterface
    private interface StringSetter {
        void set(String value);
    }

    @SuppressWarnings({"unused"})
    @FunctionalInterface
    private interface BooleanSetter {
        void set(boolean value);
    }
}
