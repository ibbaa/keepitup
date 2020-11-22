package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final LogDBConstants logDBConstants;
    private final SchedulerIdHistoryDBConstants schedulerIdDBConstants;

    private static DBOpenHelper dbOpenHelper;

    private DBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        logDBConstants = new LogDBConstants(context);
        schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
    }

    public static synchronized DBOpenHelper getInstance(Context context) {
        if (dbOpenHelper == null) {
            dbOpenHelper = new DBOpenHelper(context);
        }
        return dbOpenHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "onCreate");
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getCreateTableStatement());
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getCreateTableStatement());
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getCreateTableStatement());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onUpgrade");
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropTableStatement());
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getDropTableStatement());
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getDropTableStatement());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
