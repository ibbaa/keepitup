package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.ibba.keepitup.R;

class DBOpenHelper extends SQLiteOpenHelper {

    private final Context context;

    public DBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        NetworkTaskDBConstants networkTaskDBConstants = new NetworkTaskDBConstants(context);
        SchedulerIdHistoryDBConstants schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
        LogDBConstants logDBConstants = new LogDBConstants(context);
        Log.d(DBOpenHelper.class.getName(), "onCreate");
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getCreateTableStatement());
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getCreateTableStatement());
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getCreateTableStatement());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NetworkTaskDBConstants networkTaskDBConstants = new NetworkTaskDBConstants(context);
        SchedulerIdHistoryDBConstants schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
        LogDBConstants logDBConstants = new LogDBConstants(context);
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
