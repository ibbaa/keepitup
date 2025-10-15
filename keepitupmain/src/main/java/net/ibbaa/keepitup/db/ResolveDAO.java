/*
 * Copyright (c) 2025 Alwin Ibba
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
import java.util.List;

public class ResolveDAO extends BaseDAO {

    public ResolveDAO(Context context) {
        super(context);
    }

    public Resolve insertResolve(Resolve resolve) {
        Log.d(ResolveDAO.class.getName(), "Inserting resolve object " + resolve);
        Resolve returnedResolve = executeDBOperationInTransaction(resolve, this::insertResolve);
        Log.d(ResolveDAO.class.getName(), "Inserted resolve object is " + returnedResolve);
        dumpDatabase("Dump after insertResolve call");
        return returnedResolve;
    }

    public Resolve updateResolve(Resolve resolve) {
        Log.d(ResolveDAO.class.getName(), "Updating resolve object with id " + resolve.getId());
        Resolve returnedResolve = executeDBOperationInTransaction(resolve, this::updateResolve);
        Log.d(ResolveDAO.class.getName(), "Updated resolve object is " + returnedResolve);
        dumpDatabase("Dump after updateResolve call");
        return returnedResolve;
    }

    public Resolve readResolveForNetworkTask(long networkTaskId) {
        Log.d(ResolveDAO.class.getName(), "Reading resolve object for network task with id " + networkTaskId);
        Resolve resolve = new Resolve();
        resolve.setNetworkTaskId(networkTaskId);
        resolve = executeDBOperationInTransaction(resolve, this::readResolveForNetworkTask);
        Log.d(ResolveDAO.class.getName(), "Read resolve object is " + resolve);
        return resolve;
    }

    public List<Resolve> readAllResolve() {
        Log.d(ResolveDAO.class.getName(), "Reading all resolve objects");
        List<Resolve> resolveList = executeDBOperationInTransaction((Resolve) null, this::readAllResolve);
        Log.d(ResolveDAO.class.getName(), "Number of resolve objects read: " + resolveList.size());
        return resolveList;
    }

    public void deleteResolveForNetworkTask(long networkTaskId) {
        Log.d(ResolveDAO.class.getName(), "Deleting all resolve objects for network task with id " + networkTaskId);
        Resolve resolve = new Resolve();
        resolve.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(resolve, this::deleteResolveForNetworkTask);
        dumpDatabase("Dump after deleteResolveForNetworkTask call");
    }

    public void deleteAllOrphanResolve() {
        Log.d(ResolveDAO.class.getName(), "Deleting all orphan resolve objects");
        executeDBOperationInTransaction((Resolve) null, this::deleteAllOrphanResolve);
        dumpDatabase("Dump after deleteAllOrphanResolve call");
    }

    public void deleteAllResolve() {
        Log.d(ResolveDAO.class.getName(), "Deleting all resolve objects");
        executeDBOperationInTransaction((Resolve) null, this::deleteAllResolve);
        dumpDatabase("Dump after deleteAllResolve call");
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(ResolveDAO.class.getName(), message, Resolve.class.getSimpleName().toLowerCase(), this::readAllResolve);
        }
    }

    private Resolve insertResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "insertResolve, resolve object is " + resolve);
        ContentValues values = new ContentValues();
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), resolve.getNetworkTaskId());
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

    private Resolve updateResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "updateResolve, resolve is " + resolve);
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

    private Resolve readResolveForNetworkTask(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "readResolveForNetworkTask, resolve object is " + resolve);
        Cursor cursor = null;
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        try {
            Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getReadResolveForNetworkTaskStatement() + " with a parameter of " + resolve.getNetworkTaskId());
            cursor = db.rawQuery(dbConstants.getReadResolveForNetworkTaskStatement(), new String[]{String.valueOf(resolve.getNetworkTaskId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    return mapCursorToResolve(cursor);
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
        Log.d(ResolveDAO.class.getName(), "no resolve object found, returning null");
        return null;
    }

    private List<Resolve> readAllResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "readAllResolve, resolve object is " + resolve);
        Cursor cursor = null;
        List<Resolve> result = new ArrayList<>();
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        try {
            Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllResolveStatement());
            cursor = db.rawQuery(dbConstants.getReadAllResolveStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    Resolve mappedResolve = mapCursorToResolve(cursor);
                    result.add(mappedResolve);
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
        Log.d(ResolveDAO.class.getName(), "readAllResolve, returning " + result);
        return result;
    }

    private int deleteResolveForNetworkTask(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteResolveForNetworkTask, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        String selection = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(resolve.getNetworkTaskId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteAllResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteAllResolve, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int deleteAllOrphanResolve(Resolve resolve, SQLiteDatabase db) {
        Log.d(ResolveDAO.class.getName(), "deleteAllOrphanResolve, resolve object is " + resolve);
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        Log.d(ResolveDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteOrphanResolveStatement());
        db.execSQL(dbConstants.getDeleteOrphanResolveStatement());
        return -1;
    }

    private Resolve mapCursorToResolve(Cursor cursor) {
        Resolve resolve = new Resolve();
        ResolveDBConstants dbConstants = new ResolveDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexNetworkTaskIdColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexSourceAddressColumn = cursor.getColumnIndex(dbConstants.getSourceAddressColumnName());
        int indexSourcePortColumn = cursor.getColumnIndex(dbConstants.getSourcePortColumnName());
        int indexTargetAddressColumn = cursor.getColumnIndex(dbConstants.getTargetAddressColumnName());
        int indexTargetPortColumn = cursor.getColumnIndex(dbConstants.getTargetPortColumnName());
        resolve.setId(cursor.getLong(indexIdColumn));
        resolve.setNetworkTaskId(cursor.getLong(indexNetworkTaskIdColumn));
        resolve.setSourceAddress(cursor.getString(indexSourceAddressColumn));
        resolve.setSourcePort(cursor.getInt(indexSourcePortColumn));
        resolve.setTargetAddress(cursor.getString(indexTargetAddressColumn));
        resolve.setTargetPort(cursor.getInt(indexTargetPortColumn));
        return resolve;
    }
}
