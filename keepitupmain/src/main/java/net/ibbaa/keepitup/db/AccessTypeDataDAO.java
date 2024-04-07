/*
 * Copyright (c) 2024. Alwin Ibba
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

import java.util.ArrayList;
import java.util.List;

public class AccessTypeDataDAO extends BaseDAO {

    public AccessTypeDataDAO(Context context) {
        super(context);
    }

    public AccessTypeData insertAccessTypeData(AccessTypeData accessTypeData) {
        Log.d(AccessTypeDataDAO.class.getName(), "Inserting accessTypeData " + accessTypeData);
        AccessTypeData returnedAccessTypeData = executeDBOperationInTransaction(accessTypeData, this::insertAccessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "Inserted accessTypeData is " + returnedAccessTypeData);
        dumpDatabase("Dump after insertAccessTypeData call");
        return returnedAccessTypeData;
    }

    public AccessTypeData readAccessTypeDataForNetworkTask(long networkTaskId) {
        Log.d(AccessTypeDataDAO.class.getName(), "Reading accessTypeData for network task with id " + networkTaskId);
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setNetworkTaskId(networkTaskId);
        accessTypeData = executeDBOperationInTransaction(accessTypeData, this::readAccessTypeDataForNetworkTask);
        Log.d(AccessTypeDataDAO.class.getName(), "Read accessTypeData is " + accessTypeData);
        return accessTypeData;
    }

    public List<AccessTypeData> readAllAccessTypeData() {
        Log.d(AccessTypeDataDAO.class.getName(), "Reading all accessTypeData");
        List<AccessTypeData> accessTypeDataList = executeDBOperationInTransaction((AccessTypeData) null, this::readAllAccessTypeData);
        Log.d(AccessTypeDataDAO.class.getName(), "Number of accessTypeData read: " + accessTypeDataList.size());
        return accessTypeDataList;
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
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(AccessTypeDataDAO.class.getName(), "Error inserting accessTypeData into database. Insert returned -1.");
        }
        accessTypeData.setId(rowid);
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
        Cursor cursor = null;
        List<AccessTypeData> result = new ArrayList<>();
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        try {
            Log.d(AccessTypeDataDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllAccessTypeDataStatement());
            cursor = db.rawQuery(dbConstants.getReadAllAccessTypeDataStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    AccessTypeData mappedAccessTypeData = mapCursorToAccessTypeData(cursor);
                    result.add(mappedAccessTypeData);
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
        Log.d(AccessTypeDataDAO.class.getName(), "readAllAccessTypeData, returning " + result);
        return result;
    }

    private AccessTypeData mapCursorToAccessTypeData(Cursor cursor) {
        AccessTypeData accessTypeData = new AccessTypeData();
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexPingCountColumn = cursor.getColumnIndex(dbConstants.getPingCountColumnName());
        int indexPingPackageSizeColumn = cursor.getColumnIndex(dbConstants.getPingPackageSizeColumnName());
        int indexConnectCountColumn = cursor.getColumnIndex(dbConstants.getConnectCountColumnName());
        accessTypeData.setId(cursor.getInt(indexIdColumn));
        accessTypeData.setNetworkTaskId(cursor.getLong(indexNetworkTaskIdColumn));
        accessTypeData.setPingCount(cursor.getInt(indexPingCountColumn));
        accessTypeData.setPingPackageSize(cursor.getInt(indexPingPackageSizeColumn));
        accessTypeData.setConnectCount(cursor.getInt(indexConnectCountColumn));
        return accessTypeData;
    }
}
