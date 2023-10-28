/*
 * Copyright (c) 2023. Alwin Ibba
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
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;

import java.util.ArrayList;
import java.util.List;

public class IntervalDAO extends BaseDAO {

    public IntervalDAO(Context context) {
        super(context);
    }

    public Interval insertInterval(Interval interval) {
        Log.d(IntervalDAO.class.getName(), "Inserting interval " + interval);
        Interval returnedInterval = executeDBOperationInTransaction(interval, this::insertInterval);
        Log.d(IntervalDAO.class.getName(), "Inserted interval is " + returnedInterval);
        dumpDatabase("Dump after insertInterval call");
        return returnedInterval;
    }

    public void deleteInterval(Interval interval) {
        Log.d(IntervalDAO.class.getName(), "Deleting interval with id " + interval.getId());
        executeDBOperationInTransaction(interval, this::deleteInterval);
        dumpDatabase("Dump after deleteInterval call");
    }

    public void deleteAllIntervals() {
        Log.d(IntervalDAO.class.getName(), "Deleting all intervals");
        executeDBOperationInTransaction((Interval) null, this::deleteAllIntervals);
        dumpDatabase("Dump after deleteAllIntervals call");
    }

    public Interval updateInterval(Interval interval) {
        Log.d(IntervalDAO.class.getName(), "Updating interval with id " + interval.getId());
        Interval returnedInterval = executeDBOperationInTransaction(interval, this::updateInterval);
        Log.d(IntervalDAO.class.getName(), "Updated interval is " + returnedInterval);
        dumpDatabase("Dump after updateInterval call");
        return interval;
    }

    public Interval readInterval(long intervalId) {
        Log.d(IntervalDAO.class.getName(), "Reading interval with id " + intervalId);
        Interval interval = new Interval();
        interval.setId(intervalId);
        Interval returnedInterval = executeDBOperationInTransaction(interval, this::readInterval);
        Log.d(IntervalDAO.class.getName(), "Interval with id " + intervalId + " is " + returnedInterval);
        return returnedInterval;
    }

    public List<Interval> readAllIntervals() {
        Log.d(IntervalDAO.class.getName(), "Reading all intervals");
        List<Interval> intervalList = executeDBOperationInTransaction((Interval) null, this::readAllIntervals);
        Log.d(IntervalDAO.class.getName(), "Number of intervals read: " + intervalList.size());
        return intervalList;
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            IntervalDAO intervalDAO = new IntervalDAO(getContext());
            Dump.dump(IntervalDAO.class.getName(), message, Interval.class.getSimpleName().toLowerCase(), intervalDAO::readAllIntervals);
        }
    }

    private Interval insertInterval(Interval interval, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "insertInterval, interval is " + interval);
        ContentValues values = new ContentValues();
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        values.put(dbConstants.getHourstartColumnName(), interval.getStart().getHour());
        values.put(dbConstants.getMinutestartColumnName(), interval.getStart().getMinute());
        values.put(dbConstants.getHourendColumnName(), interval.getEnd().getHour());
        values.put(dbConstants.getMinuteendColumnName(), interval.getEnd().getMinute());
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(IntervalDAO.class.getName(), "Error inserting interval into database. Insert returned -1.");
        }
        interval.setId(rowid);
        return interval;
    }

    private int deleteInterval(Interval interval, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "deleteInterval, interval is " + interval);
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(interval.getId())};
        return db.delete(dbConstants.getTableName(), selection, selectionArgs);
    }

    private int deleteAllIntervals(Interval interval, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "deleteAllIntervals, interval is " + interval);
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private Interval updateInterval(Interval interval, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "updateInterval, interval is " + interval);
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(interval.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getHourstartColumnName(), interval.getStart().getHour());
        values.put(dbConstants.getMinutestartColumnName(), interval.getStart().getMinute());
        values.put(dbConstants.getHourendColumnName(), interval.getEnd().getHour());
        values.put(dbConstants.getMinuteendColumnName(), interval.getEnd().getMinute());
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return interval;
    }

    private Interval readInterval(Interval interval, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "readInterval, interval is " + interval);
        Cursor cursor = null;
        Interval result = null;
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        try {
            Log.d(IntervalDAO.class.getName(), "Executing SQL " + dbConstants.getReadIntervalStatement() + " with a parameter of " + interval.getId());
            cursor = db.rawQuery(dbConstants.getReadIntervalStatement(), new String[]{String.valueOf(interval.getId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    result = mapCursorToInterval(cursor);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(IntervalDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(IntervalDAO.class.getName(), "readInterval, returning " + result);
        return result;
    }

    private List<Interval> readAllIntervals(Interval interval, SQLiteDatabase db) {
        Log.d(IntervalDAO.class.getName(), "readAllIntervals, interval is " + interval);
        Cursor cursor = null;
        List<Interval> result = new ArrayList<>();
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        try {
            Log.d(IntervalDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllIntervalsStatement());
            cursor = db.rawQuery(dbConstants.getReadAllIntervalsStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    Interval mappedInterval = mapCursorToInterval(cursor);
                    result.add(mappedInterval);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(IntervalDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(IntervalDAO.class.getName(), "readAllIntervals, returning " + result);
        return result;
    }

    private Interval mapCursorToInterval(Cursor cursor) {
        Interval interval = new Interval();
        IntervalDBConstants dbConstants = new IntervalDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexHourstart = cursor.getColumnIndex(dbConstants.getHourstartColumnName());
        int indexMinutestart = cursor.getColumnIndex(dbConstants.getMinutestartColumnName());
        int indexHourend = cursor.getColumnIndex(dbConstants.getHourendColumnName());
        int indexMinuteend = cursor.getColumnIndex(dbConstants.getMinuteendColumnName());
        interval.setId(cursor.getInt(indexIdColumn));
        Time start = new Time();
        start.setHour(cursor.getInt(indexHourstart));
        start.setMinute(cursor.getInt(indexMinutestart));
        interval.setStart(start);
        Time end = new Time();
        end.setHour(cursor.getInt(indexHourend));
        end.setMinute(cursor.getInt(indexMinuteend));
        interval.setEnd(end);
        return interval;
    }
}
