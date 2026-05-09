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
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SNMPItemDAO extends BaseDAO {

    public SNMPItemDAO(Context context) {
        super(context);
    }

    public SNMPItem insertSNMPItem(SNMPItem snmpItem) {
        Log.d(SNMPItemDAO.class.getName(), "insertSNMPItem, snmpItem object is " + snmpItem);
        SNMPItem returnedSNMPItem = executeDBOperationInTransaction(snmpItem, this::insertSNMPItem);
        Log.d(SNMPItemDAO.class.getName(), "Inserted snmpItem object is " + returnedSNMPItem);
        dumpDatabase("Dump after insertSNMPItem call");
        return returnedSNMPItem;
    }

    public int insertSNMPItems(List<SNMPItem> snmpItems) {
        Log.d(SNMPItemDAO.class.getName(), "insertSNMPItems, snmpItem objects are " + snmpItems);
        int count = executeDBOperationInTransactionWithRollback(snmpItems, this::insertSNMPItems);
        dumpDatabase("Dump after insertSNMPItems call");
        return count;
    }

    @SuppressWarnings("UnusedReturnValue")
    public SNMPItem updateSNMPItem(SNMPItem snmpItem) {
        Log.d(SNMPItemDAO.class.getName(), "updateSNMPItem, snmpItem object is " + snmpItem);
        SNMPItem returnedSNMPItem = executeDBOperationInTransaction(snmpItem, this::updateSNMPItem);
        Log.d(SNMPItemDAO.class.getName(), "Updated snmpItem object is " + returnedSNMPItem);
        dumpDatabase("Dump after updateSNMPItem call");
        return returnedSNMPItem;
    }

    public void updateSNMPItemMonitored(long id, boolean monitored) {
        Log.d(SNMPItemDAO.class.getName(), "updateSNMPItemMonitored, updating monitored to " + monitored + " for item with id " + id);
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(id);
        snmpItem.setMonitored(monitored);
        executeDBOperationInTransaction(snmpItem, this::updateSNMPItemMonitored);
        dumpDatabase("Dump after updateSNMPItemMonitored call");
    }

    public void updateSNMPItemOid(long id, String oid) {
        Log.d(SNMPItemDAO.class.getName(), "updateSNMPItemOid, updating oid to " + oid + " for item with id " + id);
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(id);
        snmpItem.setOid(oid);
        executeDBOperationInTransaction(snmpItem, this::updateSNMPItemOid);
        dumpDatabase("Dump after updateSNMPItemOid call");
    }

    public List<SNMPItem> readAllSNMPItemsForNetworkTask(long networkTaskId) {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItemsForNetworkTask, network task id is " + networkTaskId);
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setNetworkTaskId(networkTaskId);
        List<SNMPItem> snmpItemList = executeDBOperationInTransaction(snmpItem, this::readAllSNMPItemsForNetworkTask);
        Log.d(SNMPItemDAO.class.getName(), "Number of snmpItem objects read: " + snmpItemList.size());
        return snmpItemList;
    }

    public List<SNMPItem> readAllSNMPItems() {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItems");
        List<SNMPItem> snmpItemList = executeDBOperationInTransaction((SNMPItem) null, this::readAllSNMPItems);
        Log.d(SNMPItemDAO.class.getName(), "Number of snmpItem objects read: " + snmpItemList.size());
        return snmpItemList;
    }

    public Map<Long, List<SNMPItem>> readAllSNMPItemsForNetworkTasks() {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItemsForNetworkTasks");
        return executeDBOperationInTransaction((SNMPItem) null, this::readAllSNMPItemsForNetworkTasks);
    }

    public void deleteAllSNMPItemsForNetworkTask(long networkTaskId) {
        Log.d(SNMPItemDAO.class.getName(), "deleteAllSNMPItemsForNetworkTask, network task id is " + networkTaskId);
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(snmpItem, this::deleteAllSNMPItemsForNetworkTask);
        dumpDatabase("Dump after deleteAllSNMPItemsForNetworkTask call");
    }

    public void deleteSNMPItem(SNMPItem snmpItem) {
        Log.d(SNMPItemDAO.class.getName(), "deleteSNMPItem, snmpItem object is " + snmpItem);
        executeDBOperationInTransaction(snmpItem, this::deleteSNMPItem);
        dumpDatabase("Dump after deleteSNMPItem call");
    }

    public void deleteAllOrphanSNMPItems() {
        Log.d(SNMPItemDAO.class.getName(), "deleteAllOrphanSNMPItems");
        executeDBOperationInTransaction((SNMPItem) null, this::deleteAllOrphanSNMPItems);
        dumpDatabase("Dump after deleteAllOrphanSNMPItems call");
    }

    public void deleteAllSNMPItems() {
        Log.d(SNMPItemDAO.class.getName(), "deleteAllSNMPItems");
        executeDBOperationInTransaction((SNMPItem) null, this::deleteAllSNMPItems);
        dumpDatabase("Dump after deleteAllSNMPItems call");
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(SNMPItemDAO.class.getName(), message, SNMPItem.class.getSimpleName().toLowerCase(), this::readAllSNMPItems);
        }
    }

    private SNMPItem insertSNMPItem(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "insertSNMPItem, snmpItem object is " + snmpItem);
        ContentValues values = new ContentValues();
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), snmpItem.getNetworkTaskId());
        values.put(dbConstants.getSnmpItemTypeColumnName(), snmpItem.getSnmpItemType() == null ? null : snmpItem.getSnmpItemType().getCode());
        values.put(dbConstants.getNameColumnName(), snmpItem.getName());
        values.put(dbConstants.getOidColumnName(), snmpItem.getOid());
        values.put(dbConstants.getMonitoredColumnName(), snmpItem.isMonitored() ? 1 : 0);
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(SNMPItemDAO.class.getName(), "Error inserting snmpItem object into database. Insert returned -1.");
        }
        snmpItem.setId(rowid);
        return snmpItem;
    }

    private DBResult<Integer> insertSNMPItems(List<SNMPItem> snmpItems, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "insertSNMPItems with snmpItem objects " + snmpItems);
        int count = 0;
        for (SNMPItem snmpItem : snmpItems) {
            ContentValues values = new ContentValues();
            SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
            values.put(dbConstants.getNetworkTaskIdColumnName(), snmpItem.getNetworkTaskId());
            values.put(dbConstants.getSnmpItemTypeColumnName(), snmpItem.getSnmpItemType() == null ? null : snmpItem.getSnmpItemType().getCode());
            values.put(dbConstants.getNameColumnName(), snmpItem.getName());
            values.put(dbConstants.getOidColumnName(), snmpItem.getOid());
            values.put(dbConstants.getMonitoredColumnName(), snmpItem.isMonitored() ? 1 : 0);
            long rowid = db.insert(dbConstants.getTableName(), null, values);
            if (rowid < 0) {
                Log.e(SNMPItemDAO.class.getName(), "Error inserting snmpItem object into database. Insert returned -1.");
            } else {
                count++;
            }
            snmpItem.setId(rowid);
        }
        return new DBResult<>(count == snmpItems.size(), count);
    }

    private SNMPItem updateSNMPItem(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "updateSNMPItem, snmpItem is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(snmpItem.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getNetworkTaskIdColumnName(), snmpItem.getNetworkTaskId());
        values.put(dbConstants.getSnmpItemTypeColumnName(), snmpItem.getSnmpItemType() == null ? null : snmpItem.getSnmpItemType().getCode());
        values.put(dbConstants.getNameColumnName(), snmpItem.getName());
        values.put(dbConstants.getOidColumnName(), snmpItem.getOid());
        values.put(dbConstants.getMonitoredColumnName(), snmpItem.isMonitored() ? 1 : 0);
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return snmpItem;
    }

    private int updateSNMPItemMonitored(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "updateSNMPItemMonitored, snmpItem is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(snmpItem.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getMonitoredColumnName(), snmpItem.isMonitored() ? 1 : 0);
        Log.d(SNMPItemDAO.class.getName(), "Updating to " + snmpItem.isMonitored());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int updateSNMPItemOid(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "updateSNMPItemOid, snmpItem is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(snmpItem.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getOidColumnName(), snmpItem.getOid());
        Log.d(SNMPItemDAO.class.getName(), "Updating to " + snmpItem.getOid());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private List<SNMPItem> readAllSNMPItemsForNetworkTask(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItemsForNetworkTask, snmpItem object is " + snmpItem);
        Cursor cursor = null;
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        List<SNMPItem> result = new ArrayList<>();
        try {
            Log.d(SNMPItemDAO.class.getName(), "Executing SQL " + dbConstants.getReadSNMPItemForNetworkTaskStatement() + " with a parameter of " + snmpItem.getNetworkTaskId());
            cursor = db.rawQuery(dbConstants.getReadSNMPItemForNetworkTaskStatement(), new String[]{String.valueOf(snmpItem.getNetworkTaskId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    result.add(mapCursorToSNMPItem(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(SNMPItemDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private List<SNMPItem> readAllSNMPItems(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItems, snmpItem object is " + snmpItem);
        List<SNMPItem> result = new ArrayList<>();
        readAllSNMPItemInternal(db, result::add);
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItems, returning " + result);
        return result;
    }

    private Map<Long, List<SNMPItem>> readAllSNMPItemsForNetworkTasks(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItemsForNetworkTasks, snmpItem object is " + snmpItem);
        Map<Long, List<SNMPItem>> result = new HashMap<>();
        readAllSNMPItemInternal(db, mappedSNMPItem -> collectSNMPItemForNetworkTask(result, mappedSNMPItem));
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItemsForNetworkTasks, returning " + result);
        return result;
    }

    private void collectSNMPItemForNetworkTask(Map<Long, List<SNMPItem>> result, SNMPItem mappedSNMPItem) {
        if (mappedSNMPItem.getNetworkTaskId() >= 0) {
            List<SNMPItem> currentList = result.get(mappedSNMPItem.getNetworkTaskId());
            if (currentList == null) {
                currentList = new ArrayList<>();
                result.put(mappedSNMPItem.getNetworkTaskId(), currentList);
            }
            currentList.add(mappedSNMPItem);
        }
    }

    private void readAllSNMPItemInternal(SQLiteDatabase db, SNMPItemCollector collector) {
        Log.d(SNMPItemDAO.class.getName(), "readAllSNMPItemInternal");
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        Cursor cursor = null;
        try {
            Log.d(SNMPItemDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllSNMPItemStatement());
            cursor = db.rawQuery(dbConstants.getReadAllSNMPItemStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    collector.collect(mapCursorToSNMPItem(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(SNMPItemDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
    }

    private int deleteAllSNMPItemsForNetworkTask(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "deleteAllSNMPItemsForNetworkTask, snmpItem object is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        String selection = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(snmpItem.getNetworkTaskId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteSNMPItem(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "deleteSNMPItem, snmpItem object is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(snmpItem.getId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteAllSNMPItems(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "deleteAllSNMPItems, snmpItem object is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int deleteAllOrphanSNMPItems(SNMPItem snmpItem, SQLiteDatabase db) {
        Log.d(SNMPItemDAO.class.getName(), "deleteAllOrphanSNMPItems, snmpItem object is " + snmpItem);
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        Log.d(SNMPItemDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteOrphanSNMPItemStatement());
        db.execSQL(dbConstants.getDeleteOrphanSNMPItemStatement());
        return -1;
    }

    private SNMPItem mapCursorToSNMPItem(Cursor cursor) {
        SNMPItem snmpItem = new SNMPItem();
        SNMPItemDBConstants dbConstants = new SNMPItemDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexSnmpItemTypeColumn = cursor.getColumnIndex(dbConstants.getSnmpItemTypeColumnName());
        int indexNameColumn = cursor.getColumnIndex(dbConstants.getNameColumnName());
        int indexOidColumn = cursor.getColumnIndex(dbConstants.getOidColumnName());
        int indexMonitoredColumn = cursor.getColumnIndex(dbConstants.getMonitoredColumnName());
        snmpItem.setId(cursor.getLong(indexIdColumn));
        snmpItem.setNetworkTaskId(cursor.getLong(indexNetworkTaskIdColumn));
        if (!cursor.isNull(indexSnmpItemTypeColumn)) {
            snmpItem.setSnmpItemType(SNMPItemType.forCode(cursor.getInt(indexSnmpItemTypeColumn)));
        }
        snmpItem.setName(cursor.getString(indexNameColumn));
        snmpItem.setOid(cursor.getString(indexOidColumn));
        snmpItem.setMonitored(cursor.getInt(indexMonitoredColumn) >= 1);
        return snmpItem;
    }

    @FunctionalInterface
    private interface SNMPItemCollector {
        void collect(SNMPItem snmpItem);
    }
}
