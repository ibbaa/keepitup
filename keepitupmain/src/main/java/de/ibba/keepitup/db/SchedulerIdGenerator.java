package de.ibba.keepitup.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.SecureRandom;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.SchedulerId;

public class SchedulerIdGenerator {

    public final static int ERROR_SCHEDULER_ID = -1;

    private final static SecureRandom randomGenerator = new SecureRandom();

    private final Context context;

    public SchedulerIdGenerator(Context context) {
        this.context = context;
    }

    public int createSchedulerId() {
        return randomGenerator.nextInt();
    }

    public SchedulerId createUniqueSchedulerId(SQLiteDatabase db) {
        Log.d(SchedulerIdGenerator.class.getName(), "createUniqueSchedulerId");
        int schedulerId = createSchedulerId();
        Log.d(SchedulerIdGenerator.class.getName(), "Created random scheduler id is " + schedulerId);
        int retryCount = getResources().getInteger(R.integer.scheduler_id_retry_count);
        while (readSchedulerIdCountFromNetworkTaskTable(schedulerId, db) > 0 || readSchedulerIdCountFromSchedulerIdHistory(schedulerId, db) > 0 || schedulerId == ERROR_SCHEDULER_ID) {
            Log.d(SchedulerIdGenerator.class.getName(), "Created random scheduler id exists. Creating new one.");
            schedulerId = createSchedulerId();
            retryCount--;
            Log.d(SchedulerIdGenerator.class.getName(), "Retry count is " + retryCount);
            if (retryCount < 0) {
                Log.d(SchedulerIdGenerator.class.getName(), "Retry counter expired.");
                Log.d(SchedulerIdGenerator.class.getName(), "Returning invalid result.");
                return createSchedulerIdResult(ERROR_SCHEDULER_ID, false);
            }
            Log.d(SchedulerIdGenerator.class.getName(), "Created random scheduler id is " + schedulerId);
        }
        Log.d(SchedulerIdGenerator.class.getName(), "Created random scheduler id is unique and does not exist.");
        SchedulerIdHistoryDBConstants schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(getContext());
        Log.d(SchedulerIdGenerator.class.getName(), "Inserting new scheduler id in " + schedulerIdDBConstants.getTableName());
        if (insertAndDeleteSchedulerIdHistory(schedulerId, db) < 0) {
            Log.d(SchedulerIdGenerator.class.getName(), "Error inserting new scheduler in " + schedulerIdDBConstants.getTableName());
            return createSchedulerIdResult(ERROR_SCHEDULER_ID, false);
        }
        return createSchedulerIdResult(schedulerId, true);
    }

    private long readSchedulerIdCountFromNetworkTaskTable(int schedulerId, SQLiteDatabase db) {
        Cursor result = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            result = db.rawQuery(dbConstants.getSchedulerIdCountStatement(), new String[]{String.valueOf(schedulerId)});
            if (result.moveToFirst()) {
                return result.getLong(0);
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerIdGenerator.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return 0;
    }

    private long readSchedulerIdCountFromSchedulerIdHistory(int schedulerId, SQLiteDatabase db) {
        Cursor result = null;
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        try {
            result = db.rawQuery(dbConstants.getSchedulerIdHistoryCountForSchedulerIdStatement(), new String[]{String.valueOf(schedulerId)});
            if (result.moveToFirst()) {
                return result.getLong(0);
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerIdGenerator.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return 0;
    }

    private long insertAndDeleteSchedulerIdHistory(int schedulerId, SQLiteDatabase db) {
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getSchedulerIdColumnName(), schedulerId);
        values.put(dbConstants.getTimestampColumnName(), System.currentTimeMillis());
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(SchedulerIdGenerator.class.getName(), "Error inserting scheduler id into history. Insert returned -1.");
            return -1;
        }
        Log.d(SchedulerIdGenerator.class.getName(), "Reading scheduler id history count");
        long scheduleridCount = readSchedulerIdCount(db);
        int limit = getResources().getInteger(R.integer.schedulerid_history_count_maximum);
        if (scheduleridCount > limit) {
            Log.d(SchedulerIdGenerator.class.getName(), "Scheduler id history count of " + scheduleridCount + " exceeds limit of " + limit + ". Performing delete.");
            deleteOldestSchedulerIdHistoryEntry(db);
        } else {
            Log.d(SchedulerIdGenerator.class.getName(), "Scheduler id history count of " + scheduleridCount + " does not exceed limit of " + limit + ". Delete skipped.");
        }
        return rowid;
    }

    private long readSchedulerIdCount(SQLiteDatabase db) {
        Cursor result = null;
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        try {
            result = db.rawQuery(dbConstants.getSchedulerIdHistoryCount(), new String[0]);
            if (result.moveToFirst()) {
                return result.getLong(0);
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerIdGenerator.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return -1;
    }

    private void deleteOldestSchedulerIdHistoryEntry(SQLiteDatabase db) {
        Cursor result = null;
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        try {
            result = db.rawQuery(dbConstants.getReadOldestSchedulerIdHistoryEntryStatement(), new String[0]);
            if (result.moveToFirst()) {
                int indexIdColumn = result.getColumnIndex(dbConstants.getIdColumnName());
                if (!result.isNull(indexIdColumn)) {
                    long id = result.getLong(indexIdColumn);
                    String selection = dbConstants.getIdColumnName() + " = ?";
                    String[] selectionArgs = {String.valueOf(id)};
                    db.delete(dbConstants.getTableName(), selection, selectionArgs);
                } else {
                    Log.d(SchedulerIdGenerator.class.getName(), "Nothing to delete.");
                }
            } else {
                Log.d(SchedulerIdGenerator.class.getName(), "Nothing to delete.");
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerIdGenerator.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
    }

    private SchedulerId createSchedulerIdResult(int schedulerId, boolean valid) {
        SchedulerId schedulerIdObj = new SchedulerId();
        schedulerIdObj.setId(-1);
        schedulerIdObj.setValid(valid);
        schedulerIdObj.setSchedulerId(schedulerId);
        schedulerIdObj.setTimestamp(System.currentTimeMillis());
        return schedulerIdObj;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
