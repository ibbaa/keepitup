package de.ibba.keepitup.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;

public class LogDAO extends BaseDAO {

    public LogDAO(Context context) {
        super(context);
    }

    public LogEntry insertAndDeleteLog(LogEntry logEntry) {
        Log.d(NetworkTaskDAO.class.getName(), "Inserting log entry " + logEntry + " and deleting oldest log entry");
        return executeDBOperationInTransaction(logEntry, this::insertAndDeleteLog);
    }

    public LogEntry readMostRecentLogForNetworkTask(long networkTaskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading most recent log entry for network task with id " + networkTaskId);
        LogEntry entry = new LogEntry();
        entry.setNetworkTaskId(networkTaskId);
        return executeDBOperationInTransaction(entry, this::readMostRecentLogForNetworkTask);
    }

    public List<LogEntry> readAllLogsForNetworkTask(long networkTaskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading all log entries for network task with id " + networkTaskId);
        LogEntry entry = new LogEntry();
        entry.setNetworkTaskId(networkTaskId);
        return executeDBOperationInTransaction(entry, this::readAllLogsForNetworkTask);
    }

    public void deleteAllLogsForNetworkTask(long networkTaskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting all log entries for network task with id " + networkTaskId);
        LogEntry entry = new LogEntry();
        entry.setNetworkTaskId(networkTaskId);
        executeDBOperationInTransaction(entry, this::deleteAllLogsForNetworkTask);
    }

    public void deleteAllLogs() {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting all log entries");
        executeDBOperationInTransaction((LogEntry) null, this::deleteAllLogs);
    }

    private LogEntry insertAndDeleteLog(LogEntry logEntry, SQLiteDatabase db) {
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
        int limit = getContext().getResources().getInteger(R.integer.task_count_maximum);
        if (logCount > limit) {
            Log.d(LogDAO.class.getName(), "Log count of " + logCount + " exceeds limit of " + limit + ". Performing delete.");
            deleteOldestLogForNetworkTask(logEntry, db);
        } else {
            Log.d(LogDAO.class.getName(), "Log count of " + logCount + " does not exceed limit of " + limit + ". Delete skipped.");
        }
        return logEntry;
    }

    private LogEntry readMostRecentLogForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Cursor cursor = null;
        LogEntry result = null;
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getMostRecentLogStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
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
                    Log.e(NetworkTaskDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private List<LogEntry> readAllLogsForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Cursor cursor = null;
        List<LogEntry> result = new ArrayList<>();
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getAllLogsStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
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
                    Log.e(NetworkTaskDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private int deleteAllLogsForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        String whereClause = dbConstants.getNetworkTaskIdColumnName() + " = ?";
        return db.delete(dbConstants.getTableName(), whereClause, new String[]{String.valueOf(logEntry.getNetworkTaskId())});
    }

    private int deleteAllLogs(LogEntry logEntry, SQLiteDatabase db) {
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private long readLogCountForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Cursor result = null;
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            result = db.rawQuery(dbConstants.getLogCountStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
            if (result.moveToFirst()) {
                return result.getLong(0);
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
        return -1;
    }

    private void deleteOldestLogForNetworkTask(LogEntry logEntry, SQLiteDatabase db) {
        Cursor result = null;
        LogDBConstants dbConstants = new LogDBConstants(getContext());
        try {
            result = db.rawQuery(dbConstants.getOldestLogStatement(), new String[]{String.valueOf(logEntry.getNetworkTaskId())});
            if (result.moveToFirst()) {
                int indexIdColumn = result.getColumnIndex(dbConstants.getIdColumnName());
                if (!result.isNull(indexIdColumn)) {
                    long id = result.getLong(indexIdColumn);
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
