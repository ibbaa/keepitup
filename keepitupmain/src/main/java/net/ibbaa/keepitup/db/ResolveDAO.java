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
import net.ibbaa.keepitup.model.Resolve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolveDAO extends BaseDAO {

    public ResolveDAO(Context context) {
        super(context);
    }

    public Resolve insertResolve(Resolve resolve) {
        Log.d(ResolveDAO.class.getName(), "insertResolve, resolve object is " + resolve);
        Resolve returnedResolve = executeDBOperationInTransaction(resolve, this::insertResolve);
        Log.d(ResolveDAO.class.getName(), "Inserted resolve object is " + returnedResolve);
        dumpDatabase("Dump after insertResolve call");
        return returnedResolve;
    }

    public int insertResolves(List<Resolve> resolves) {
        Log.d(ResolveDAO.class.getName(), "insertResolves, resolve objects are " + resolves);
        int count = executeDBOperationInTransactionWithRollback(resolves, this::insertResolves);
        dumpDatabase("Dump after insertResolves call");
        return count;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Resolve updateResolve(Resolve resolve) {
        Log.d(ResolveDAO.class.getName(), "updateResolve, resolve object is " + resolve);
        Resolve returnedResolve = executeDBOperationInTransaction(resolve, this::updateResolve);
        Log.d(ResolveDAO.class.getName(), "Updated resolve object is " + returnedResolve);
        dumpDatabase("Dump after updateResolve call");
        return returnedResolve;
    }

    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    public Resolve readResolveForNetworkTask(long networkTaskId) {
        Log.d(ResolveDAO.class.getName(), "readResolveForNetworkTask, network task id is " + networkTaskId);
        Resolve resolve = new Resolve();
        resolve.setNetworkTaskId(networkTaskId);
        List<Resolve> resolveList = executeDBOperationInTransaction(resolve, this::readAllResolvesForNetworkTask);
        Log.d(ResolveDAO.class.getName(), "Number of resolve objects read: " + resolveList.size());
        return resolveList.isEmpty() ? null : resolveList.get(0);
    }

    public List<Resolve> readAllResolvesForNetworkTask(long networkTaskId) {
        Log.d(ResolveDAO.class.getName(), "readAllResolvesForNetworkTask, network task id is " + networkTaskId);
        Resolve resolve = new Resolve();
        resolve.setNetworkTaskId(networkTaskId);
        List<Resolve> resolveList = executeDBOperationInTransaction(resolve, this::readAllResolvesForNetworkTask);
        Log.d(ResolveDAO.class.getName(), "Number of resolve objects read: " + resolveList.size());
        return resolveList;
    }

    public List<Resolve> readAllResolves() {
        Log.d(ResolveDAO.class.getName(), "readAllResolves");
        List<Resolve> resolveList = executeDBOperationInTransaction((Resolve) null, this::readAllResolves);
        Log.d(ResolveDAO.class.getName(), "Number of resolve objects read: " + resolveList.size());
        return resolveList;
    }

    public Map<Long, List<Resolve>> readAllResolvesForNetworkTasks() {
        Log.d(ResolveDAO.class.getName(), "readAllResolvesForNetworkTasks");
        return executeDBOperationInTransaction((Resolve) null, this::readAllResolvesForNetworkTasks);
    }

    public void deleteAllResolvesForNetworkTask(long networkTaskId) {
        Log.d(ResolveDAO.class.getName(), "deleteAllResolvesForNetworkTask, network task id is " + networkTaskId);
        Resolve resolve = new Resolve();
        resolve.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(resolve, this::deleteAllResolvesForNetworkTask);
        dumpDatabase("Dump after deleteAllResolvesForNetworkTask call");
    }

    public void deleteResolve(Resolve resolve) {
        Log.d(ResolveDAO.class.getName(), "deleteResolve, resolve object is " + resolve);
        executeDBOperationInTransaction(resolve, this::deleteResolve);
        dumpDatabase("Dump after deleteResolve call");
    }

    public void deleteAllOrphanResolves() {
        Log.d(ResolveDAO.class.getName(), "deleteAllOrphanResolves");
        executeDBOperationInTransaction((Resolve) null, this::deleteAllOrphanResolves);
        dumpDatabase("Dump after deleteAllOrphanResolves call");
    }

    public void deleteAllResolves() {
        Log.d(ResolveDAO.class.getName(), "deleteAllResolves");
        executeDBOperationInTransaction((Resolve) null, this::deleteAllResolves);
        dumpDatabase("Dump after deleteAllResolves call");
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean normalizeUIIndex() {
        Log.d(ResolveDAO.class.getName(), "normalizeUIIndex");
        boolean inconsistency = executeDBOperationInTransaction((Resolve) null, this::normalizeUIIndex);
        dumpDatabase("Dump after normalizeUIIndex call");
        return inconsistency;
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(ResolveDAO.class.getName(), message, Resolve.class.getSimpleName().toLowerCase(), this::readAllResolves);
        }
    }

    private Resolve insertResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "insertResolve, resolve object is " + resolve);
        ContentValues values = new ContentValues();
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), resolve.getNetworkTaskId());
        values.put(dbConstants.getIndexColumnName(), resolve.getIndex());
        values.put(dbConstants.getSourceAddressColumnName(), resolve.getSourceAddress());
        values.put(dbConstants.getSourcePortColumnName(), resolve.getSourcePort());
        values.put(dbConstants.getTargetAddressColumnName(), resolve.getTargetAddress());
        values.put(dbConstants.getTargetPortColumnName(), resolve.getTargetPort());
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(ResolveDAO.class.getName(), "Error inserting resolve object into database. Insert returned -1.");
        }
        resolve.setId(rowid);
        return resolve;
    }

    private DBResult<Integer> insertResolves(List<Resolve> resolves, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "insertResolves with resolve objects " + resolves);
        int count = 0;
        for (Resolve resolve : resolves) {
            ContentValues values = new ContentValues();
            ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
            values.put(dbConstants.getNetworkTaskIdColumnName(), resolve.getNetworkTaskId());
            values.put(dbConstants.getIndexColumnName(), resolve.getIndex());
            values.put(dbConstants.getSourceAddressColumnName(), resolve.getSourceAddress());
            values.put(dbConstants.getSourcePortColumnName(), resolve.getSourcePort());
            values.put(dbConstants.getTargetAddressColumnName(), resolve.getTargetAddress());
            values.put(dbConstants.getTargetPortColumnName(), resolve.getTargetPort());
            long rowid = db.insert(dbConstants.getTableName(), null, values);
            if (rowid < 0) {
                Log.e(ResolveDAO.class.getName(), "Error inserting resolve object into database. Insert returned -1.");
            } else {
                count++;
            }
            resolve.setId(rowid);
        }
        return new DBResult<>(count == resolves.size(), count);
    }

    private Resolve updateResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "updateResolve, resolve is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(resolve.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getNetworkTaskIdColumnName(), resolve.getNetworkTaskId());
        values.put(dbConstants.getSourceAddressColumnName(), resolve.getSourceAddress());
        values.put(dbConstants.getSourcePortColumnName(), resolve.getSourcePort());
        values.put(dbConstants.getTargetAddressColumnName(), resolve.getTargetAddress());
        values.put(dbConstants.getTargetPortColumnName(), resolve.getTargetPort());
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return resolve;
    }

    private List<Resolve> readAllResolvesForNetworkTask(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "readAllResolvesForNetworkTask, resolve object is " + resolve);
        Cursor cursor = null;
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        List<Resolve> result = new ArrayList<>();
        try {
            Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getReadResolveForNetworkTaskStatement() + " with a parameter of " + resolve.getNetworkTaskId());
            cursor = db.rawQuery(dbConstants.getReadResolveForNetworkTaskStatement(), new String[]{String.valueOf(resolve.getNetworkTaskId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    result.add(mapCursorToResolve(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(ResolveDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private List<Resolve> readAllResolves(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "readAllResolves, resolve object is " + resolve);
        List<Resolve> result = new ArrayList<>();
        readAllResolveInternal(db, result::add);
        Log.d(ResolveDAO.class.getName(), "readAllResolves, returning " + result);
        return result;
    }

    private Map<Long, List<Resolve>> readAllResolvesForNetworkTasks(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "readAllResolvesForNetworkTasks, resolve object is " + resolve);
        Map<Long, List<Resolve>> result = new HashMap<>();
        readAllResolveInternal(db, mappedResolve -> collectResolveForNetworkTask(result, mappedResolve));
        Log.d(ResolveDAO.class.getName(), "readAllResolvesForNetworkTasks, returning " + result);
        return result;
    }

    private void collectResolveForNetworkTask(Map<Long, List<Resolve>> result, Resolve mappedResolve) {
        if (mappedResolve.getNetworkTaskId() >= 0) {
            List<Resolve> currentList = result.get(mappedResolve.getNetworkTaskId());
            if (currentList == null) {
                currentList = new ArrayList<>();
                result.put(mappedResolve.getNetworkTaskId(), currentList);
            }
            currentList.add(mappedResolve);
        }
    }

    private void readAllResolveInternal(SQLiteDatabase db, ResolveCollector collector) {
        Log.d(ResolveDAO.class.getName(), "readAllResolveInternal");
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        Cursor cursor = null;
        try {
            Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllResolveStatement());
            cursor = db.rawQuery(dbConstants.getReadAllResolveStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    collector.collect(mapCursorToResolve(cursor));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(ResolveDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
    }

    private int deleteAllResolvesForNetworkTask(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteAllResolvesForNetworkTask, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        String selection = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(resolve.getNetworkTaskId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteResolve, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(resolve.getId())};
        int value = db.delete(dbConstants.getTableName(), selection, selectionArgs);
        Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getUpdateIndexResolveStatement() + " with parametera " + resolve.getIndex() + " and " + resolve.getNetworkTaskId());
        db.execSQL(dbConstants.getUpdateIndexResolveStatement(), new Object[]{String.valueOf(resolve.getIndex()), String.valueOf(resolve.getNetworkTaskId())});
        return value;
    }

    private int deleteAllResolves(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteAllResolves, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int deleteAllOrphanResolves(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteAllOrphanResolves, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteOrphanResolveStatement());
        db.execSQL(dbConstants.getDeleteOrphanResolveStatement());
        return -1;
    }

    @SuppressWarnings("unused")
    private boolean normalizeUIIndex(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "normalizeUIIndex");
        Cursor cursor = null;
        List<ResolveDBConstants.IndexResolve> result = new ArrayList<>();
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        boolean inconsistencyDetected = false;
        try {
            Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getReadResolveIndexStatement());
            cursor = db.rawQuery(dbConstants.getReadResolveIndexStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
                int indexIndexColumn = cursor.getColumnIndex(dbConstants.getIndexColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    result.add(new ResolveDBConstants.IndexResolve(cursor.getLong(indexIdColumn), cursor.getLong(indexNetworkTaskIdColumn), cursor.getInt(indexIndexColumn)));
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(ResolveDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(ResolveDAO.class.getName(), "Database returned the following index resolve objects: " + (result.isEmpty() ? "no index resolve object" : ""));
        if (BuildConfig.DEBUG) {
            for (ResolveDBConstants.IndexResolve indexResolve : result) {
                Log.d(ResolveDAO.class.getName(), indexResolve.toString());
            }
        }
        String selection = dbConstants.getIdColumnName() + " = ?";
        long currentNetworkTaskId = -1;
        int currentIndex = 0;
        for (ResolveDBConstants.IndexResolve indexResolve : result) {
            if (indexResolve.networkTaskId() != currentNetworkTaskId) {
                currentNetworkTaskId = indexResolve.networkTaskId();
                currentIndex = 0;
            }
            if (indexResolve.uiIndex() != currentIndex) {
                Log.e(ResolveDAO.class.getName(), "Index resolve is inconsistent: " + indexResolve);
                inconsistencyDetected = true;
                ContentValues values = new ContentValues();
                values.put(dbConstants.getIndexColumnName(), currentIndex);
                String[] selectionArgs = {String.valueOf(indexResolve.id())};
                db.update(dbConstants.getTableName(), values, selection, selectionArgs);
            }
            currentIndex++;
        }
        return inconsistencyDetected;
    }

    private Resolve mapCursorToResolve(Cursor cursor) {
        Resolve resolve = new Resolve();
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexIndexColumn = cursor.getColumnIndex(dbConstants.getIndexColumnName());
        int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexSourceAddressColumn = cursor.getColumnIndex(dbConstants.getSourceAddressColumnName());
        int indexSourcePortColumn = cursor.getColumnIndex(dbConstants.getSourcePortColumnName());
        int indexTargetAddressColumn = cursor.getColumnIndex(dbConstants.getTargetAddressColumnName());
        int indexTargetPortColumn = cursor.getColumnIndex(dbConstants.getTargetPortColumnName());
        resolve.setId(cursor.getLong(indexIdColumn));
        resolve.setIndex(cursor.getInt(indexIndexColumn));
        resolve.setNetworkTaskId(cursor.getLong(indexNetworkTaskIdColumn));
        resolve.setSourceAddress(cursor.getString(indexSourceAddressColumn));
        resolve.setSourcePort(cursor.getInt(indexSourcePortColumn));
        resolve.setTargetAddress(cursor.getString(indexTargetAddressColumn));
        resolve.setTargetPort(cursor.getInt(indexTargetPortColumn));
        return resolve;
    }

    @FunctionalInterface
    private interface ResolveCollector {
        void collect(Resolve resolve);
    }
}
