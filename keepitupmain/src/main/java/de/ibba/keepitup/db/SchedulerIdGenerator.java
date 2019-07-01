package de.ibba.keepitup.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.SecureRandom;

import de.ibba.keepitup.R;

public class SchedulerIdGenerator {

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
        Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id is " + schedulerId);
        int retryCount = context.getResources().getInteger(R.integer.scheduler_id_retry_count);
        while (readSchedulerIdCount(schedulerId, db) > 0) {
            Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id exists. Creating new one.");
            schedulerId = createSchedulerId();
            retryCount--;
            Log.d(SchedulerIdGenerator.class.getName(), "Retry count is " + retryCount);
            if (retryCount < 0) {
                Log.d(SchedulerIdGenerator.class.getName(), "Retry counter expired.");
                Log.d(SchedulerIdGenerator.class.getName(), "Returning invalid result.");
                return new SchedulerId(false, 0);
            }
            Log.d(SchedulerIdGenerator.class.getName(), "Created random scheduler id is " + schedulerId);
        }
        Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id is unique and does not exist.");
        return new SchedulerId(true, schedulerId);
    }

    private long readSchedulerIdCount(int schedulerId, SQLiteDatabase db) {
        Cursor result = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
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
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return 0;
    }

    public static class SchedulerId {
        private final boolean isValid;
        private final int id;

        public SchedulerId(boolean isValid, int id) {
            this.isValid = isValid;
            this.id = id;
        }

        public boolean isValid() {
            return isValid;
        }

        public int getId() {
            return id;
        }
    }
}
