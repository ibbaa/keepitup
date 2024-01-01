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
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;

import java.util.ArrayList;
import java.util.List;

public class LogDAO extends BaseDAO {

    public LogDAO(Context context) {
        super(context);
    }

    public LogEntry insertAndDeleteLog(LogEntry logEntry) {
        Log.d(LogDAO.class.getName(), "Inserting log entry " + logEntry + " and deleting oldest log entry");
        LogEntry returnedEntry = executeDBOperationInTransaction(logEntry, this::insertAndDeleteLog);
        Log.d(LogDAO.class.getName(), "Inserted log entry is " + returnedEntry);
        dumpDatabase("Dump after insertAndDeleteLog call");
        return returnedEntry;
    }

    public LogEntry readMostRecentLogForNetworkTask(long networkTaskId) {
        Log.d(LogDAO.class.getName(), "Reading most recent log entry for network task with id " + networkTaskId);
        LogEntry entry = new LogEntry();
        entry.setNetworkTaskId(networkTaskId);
        LogEntry returnedEntry = executeDBOperationInTransaction(entry, this::readMostRecentLogForNetworkTask);
        Log.d(LogDAO.class.getName(), "Read log entry is " + returnedEntry);
        return returnedEntry;
    }

    public List<LogEntry> readAllLogsForNetworkTask(long networkTaskId) {
        Log.d(LogDAO.class.getName(), "Reading all log entries for network task with id " + networkTaskId);
        LogEntry entry = new LogEntry();
        entry.setNetworkTaskId(networkTaskId);
        List<LogEntry> logEntries = executeDBOperationInTransaction(entry, this::readAllLogsForNetworkTask);
        Log.d(LogDAO.class.getName(), "Number of log entries read: " + logEntries.size());
        return logEntries;
    }

    public List<LogEntry> readAllLogs() {
        Log.d(LogDAO.class.getName(), "Reading all log entries");
        List<LogEntry> logEntries = executeDBOperationInTransaction((LogEntry) null, this::readAllLogs);
        Log.d(LogDAO.class.getName(), "Number of log entries read: " + logEntries.size());
        return logEntries;
    }

    public void deleteAllLogsForNetworkTask(long networkTaskId) {
        Log.d(LogDAO.class.getName(), "Deleting all log entries for network task with id " + networkTaskId);
        LogEntry entry = new LogEntry();
        entry.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(entry, this::deleteAllLogsForNetworkTask);
        dumpDatabase("Dump after deleteAllLogsForNetworkTask call");
    }

    public void deleteAllOrphanLogs() {
        Log.d(LogDAO.class.getName(), "Deleting all orphan log entries");
        executeDBOperationInTransaction((LogEntry) null, this::deleteAllOrphanLogs);
        dumpDatabase("Dump after deleteAllOrphanLogs call");
    }

    public void deleteAllLogs() {
        Log.d(LogDAO.class.getName(), "Deleting all log entries");
        executeDBOperationInTransaction((LogEntry) null, this::deleteAllLogs);
        dumpDatabase("Dump after deleteAllLogs call");
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(LogDAO.class.getName(), message, LogEntry.class.getSimpleName().toLowerCase(), this::readAllLogs);
        }
    }

    private LogEntry insertAndDeleteLog(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "insertAndDeleteLog, log entry is " + logEntry);
        ContentValues values = new ContentValues();
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), logEntry.getNetworkTaskId());
        values.put(dbConstants.getTimestampColumnName(), logEntry.getTimestamp());
        values.put(dbConstants.getSuccessColumnName(), logEntry.isSuccess() ? 1 : 0);
        values.put(dbConstants.getMessageColumnName(), logEntry.getMessage());
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(LogDAO.class.getName(), "Error inserting log entry into database. Insert returned -1.");
            logEntry.setId(rowid);
            return logEntry;
        }
        logEntry.setId(rowid);
        Log.d(LogDAO.class.getName(), "Reading log count");
        long logCount = readLogCountForNetworkTask(logEntry, db);
        int limit = getContext().getResources().getInteger(R.integer.log_count_maximum);
        if (logCount > limit) {
            Log.d(LogDAO.class.getName(), "Log count of " + logCount + " exceeds limit of " + limit + ". Performing delete.");
            deleteOldestLogForNetworkTask(logEntry, db);
        } else {
            Log.d(LogDAO.class.getName(), "Log count of " + logCount + " does not exceed limit of " + limit + ". Delete skipped.");
        }
        return logEntry;
    }

    private LogEntry readMostRecentLogForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "readMostRecentLogForNetworkTask, log entry is " + logEntry);
        Cursor cursor = null;
        LogEntry result = null;
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            Log.d(LogDAO.class.getName(), "Executing SQL " + dbConstants.getReadMostRecentLogStatement() + " with a parameter of " + logEntry.getNetworkTaskId());
            cursor = db.rawQuery(dbConstants.getReadMostRecentLogStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    result = mapCursorToLogEntry(cursor);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(LogDAO.class.getName(), "readMostRecentLogForNetworkTask, returning " + result);
        return result;
    }

    private List<LogEntry> readAllLogsForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "readAllLogsForNetworkTask, log entry is " + logEntry);
        Cursor cursor = null;
        List<LogEntry> result = new ArrayList<>();
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            Log.d(LogDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllLogsForNetworkTaskStatement() + " with a parameter of " + logEntry.getNetworkTaskId());
            cursor = db.rawQuery(dbConstants.getReadAllLogsForNetworkTaskStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    LogEntry mappedLogEntry = mapCursorToLogEntry(cursor);
                    result.add(mappedLogEntry);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(LogDAO.class.getName(), "readAllLogsForNetworkTask, returning " + result);
        return result;
    }

    private List<LogEntry> readAllLogs(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "readAllLogs, log entry is " + logEntry);
        Cursor cursor = null;
        List<LogEntry> result = new ArrayList<>();
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            Log.d(LogDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllLogsStatement());
            cursor = db.rawQuery(dbConstants.getReadAllLogsStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    LogEntry mappedLogEntry = mapCursorToLogEntry(cursor);
                    result.add(mappedLogEntry);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(LogDAO.class.getName(), "readAllLogs, returning " + result);
        return result;
    }

    private int deleteAllLogsForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "deleteAllLogsForNetworkTask, log entry is " + logEntry);
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        String whereClause = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        return db.delete(dbConstants.getTableName(), whereClause, new String[]{String.valueOf(logEntry.getNetworkTaskId())});
    }

    private int deleteAllOrphanLogs(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "deleteAllOrphanLogs, log entry is " + logEntry);
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        Log.d(LogDAO.class.getName(), "Executing SQL " + dbConstants.getDeleteOrphanLogsStatement());
        db.execSQL(dbConstants.getDeleteOrphanLogsStatement());
        return -1;
    }

    private int deleteAllLogs(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "deleteAllLogs, log entry is " + logEntry);
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private long readLogCountForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "readLogCountForNetworkTask, log entry is " + logEntry);
        Cursor result = null;
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            Log.d(LogDAO.class.getName(), "Executing SQL " + dbConstants.getLogCountStatement() + " with a parameter of " + logEntry.getNetworkTaskId());
            result = db.rawQuery(dbConstants.getLogCountStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
            if (result.moveToFirst()) {
                long value = result.getLong(0);
                Log.d(LogDAO.class.getName(), "readLogCountForNetworkTask, returning " + value);
                return value;
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Throwable exc) {
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(LogDAO.class.getName(), "readLogCountForNetworkTask, returning -1");
        return -1;
    }

    private void deleteOldestLogForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Log.d(LogDAO.class.getName(), "deleteOldestLogForNetworkTask, log entry is " + logEntry);
        Cursor result = null;
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            Log.d(LogDAO.class.getName(), "Executing SQL " + dbConstants.getReadOldestLogStatement() + " with a parameter of " + logEntry.getNetworkTaskId());
            result = db.rawQuery(dbConstants.getReadOldestLogStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
            if (result.moveToFirst()) {
                int indexIdColumn = result.getColumnIndex(dbConstants.getIdColumnName());
                if (!result.isNull(indexIdColumn)) {
                    long id = result.getLong(indexIdColumn);
                    Log.d(LogDAO.class.getName(), "Deleting for id " + id);
                    String selection = dbConstants.getIdColumnName() + " = ?";
                    String[] selectionArgs = {String.valueOf(id)};
                    db.delete(dbConstants.getTableName(), selection, selectionArgs);
                } else {
                    Log.d(LogDAO.class.getName(), "Nothing to delete.");
                }
            } else {
                Log.d(LogDAO.class.getName(), "Nothing to delete.");
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Throwable exc) {
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
    }

    private LogEntry mapCursorToLogEntry(Cursor cursor) {
        LogEntry logEntry = new LogEntry();
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexNetworkTaskIndexColumn = cursor.getColumnIndex(dbConstants.getNetworkTaskIdColumnName());
        int indexTimestampColumn = cursor.getColumnIndex(dbConstants.getTimestampColumnName());
        int indexSuccessColumn = cursor.getColumnIndex(dbConstants.getSuccessColumnName());
        int indexMessageColumn = cursor.getColumnIndex(dbConstants.getMessageColumnName());
        logEntry.setId(cursor.getInt(indexIdColumn));
        logEntry.setNetworkTaskId(cursor.getLong(indexNetworkTaskIndexColumn));
        logEntry.setTimestamp(cursor.getLong(indexTimestampColumn));
        logEntry.setSuccess(cursor.getInt(indexSuccessColumn) >= 1);
        logEntry.setMessage(cursor.getString(indexMessageColumn));
        return logEntry;
    }
}
