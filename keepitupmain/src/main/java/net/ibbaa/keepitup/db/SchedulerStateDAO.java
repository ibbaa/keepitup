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
import net.ibbaa.keepitup.model.SchedulerState;

import java.util.Collections;

public class SchedulerStateDAO extends BaseDAO {

    public SchedulerStateDAO(Context context) {
        super(context);
    }

    public SchedulerState insertSchedulerState(SchedulerState schedulerState) {
        Log.d(SchedulerStateDAO.class.getName(), "Inserting schedulerState " + schedulerState);
        SchedulerState returnedSchedulerState = executeDBOperationInTransaction(schedulerState, this::insertSchedulerState);
        Log.d(IntervalDAO.class.getName(), "Inserted schedulerState is " + returnedSchedulerState);
        dumpDatabase("Dump after insertInterval call");
        return returnedSchedulerState;
    }

    public void deleteSchedulerState() {
        Log.d(SchedulerStateDAO.class.getName(), "Deleting the existing schedulerState");
        executeDBOperationInTransaction((SchedulerState) null, this::deleteSchedulerState);
        dumpDatabase("Dump after deleteSchedulerState call");
    }

    public SchedulerState readSchedulerState() {
        Log.d(SchedulerStateDAO.class.getName(), "Reading the existing schedulerState");
        SchedulerState schedulerState = executeDBOperationInTransaction((SchedulerState) null, this::readSchedulerState);
        Log.d(SchedulerStateDAO.class.getName(), "Read schedulerState is " + schedulerState);
        return schedulerState;
    }

    public SchedulerState updateSchedulerState(SchedulerState schedulerState) {
        Log.d(SchedulerStateDAO.class.getName(), "Updating the existing schedulerState");
        SchedulerState returnedSchedulerState = executeDBOperationInTransaction(schedulerState, this::updateSchedulerState);
        Log.d(SchedulerStateDAO.class.getName(), "Updated schedulerState is " + returnedSchedulerState);
        dumpDatabase("Dump after updateSchedulerState call");
        return returnedSchedulerState;
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(SchedulerStateDAO.class.getName(), message, SchedulerState.class.getSimpleName().toLowerCase(), () -> Collections.singletonList(readSchedulerState()));
        }
    }

    private SchedulerState insertSchedulerState(SchedulerState schedulerState, SQLiteDatabase db) {
        Log.d(SchedulerStateDAO.class.getName(), "insertSchedulerState, schedulerState is " + schedulerState);
        ContentValues values = new ContentValues();
        SchedulerStateDBConstants dbConstants = new SchedulerStateDBConstants(getContext());
        Log.d(SchedulerStateDAO.class.getName(), "Deleting existing schedulerState.");
        db.delete(dbConstants.getTableName(), null, null);
        values.put(dbConstants.getSuspendedColumnName(), schedulerState.isSuspended() ? 1 : 0);
        values.put(dbConstants.getTimestampColumnName(), schedulerState.getTimestamp());
        Log.d(SchedulerStateDAO.class.getName(), "Inserting new schedulerState.");
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(SchedulerStateDAO.class.getName(), "Error inserting schedulerState into database. Insert returned -1.");
        }
        return new SchedulerState(rowid, schedulerState.isSuspended(), schedulerState.getTimestamp());
    }

    private SchedulerState updateSchedulerState(SchedulerState schedulerState, SQLiteDatabase db) {
        Log.d(SchedulerStateDAO.class.getName(), "updateSchedulerState, schedulerState is " + schedulerState);
        SchedulerStateDBConstants dbConstants = new SchedulerStateDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getSuspendedColumnName(), schedulerState.isSuspended() ? 1 : 0);
        values.put(dbConstants.getTimestampColumnName(), schedulerState.getTimestamp());
        db.update(dbConstants.getTableName(), values, null, null);
        return schedulerState;
    }

    private int deleteSchedulerState(SchedulerState schedulerState, SQLiteDatabase db) {
        Log.d(SchedulerStateDAO.class.getName(), "deleteSchedulerState, schedulerState is " + schedulerState);
        SchedulerStateDBConstants dbConstants = new SchedulerStateDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private SchedulerState readSchedulerState(SchedulerState schedulerState, SQLiteDatabase db) {
        Log.d(SchedulerStateDAO.class.getName(), "readSchedulerState, schedulerState is " + schedulerState);
        Cursor cursor = null;
        SchedulerStateDBConstants dbConstants = new SchedulerStateDBConstants(getContext());
        try {
            Log.d(SchedulerStateDAO.class.getName(), "Executing SQL " + dbConstants.getReadSchedulerStateStatement());
            cursor = db.rawQuery(dbConstants.getReadSchedulerStateStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    return mapCursorToSchedulerState(cursor);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerStateDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.e(SchedulerStateDAO.class.getName(), "no schedulerState found, returning null");
        return null;
    }

    private SchedulerState mapCursorToSchedulerState(Cursor cursor) {
        SchedulerStateDBConstants dbConstants = new SchedulerStateDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexSuspendedColumn = cursor.getColumnIndex(dbConstants.getSuspendedColumnName());
        int indexTimestampColumn = cursor.getColumnIndex(dbConstants.getTimestampColumnName());
        boolean suspended = cursor.getInt(indexSuspendedColumn) > 0;
        return new SchedulerState(cursor.getInt(indexIdColumn), suspended, cursor.getInt(indexTimestampColumn));
    }
}
