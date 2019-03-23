package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.ibba.keepitup.R;

class LogDBOpenHelper extends SQLiteOpenHelper {

    private final Context context;

    public LogDBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.log_db_name), null, context.getResources().getInteger(R.integer.log_db_version));
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        LogDBConstants dbConstants = new LogDBConstants(context);
        Log.d(LogDBOpenHelper.class.getName(), "onCreate");
        Log.i(LogDBOpenHelper.class.getName(), "Creating database table " + dbConstants.getTableName());
        db.execSQL(dbConstants.getCreateTableStatement());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogDBConstants dbConstants = new LogDBConstants(context);
        Log.d(LogDBOpenHelper.class.getName(), "onUpgrade");
        Log.i(LogDBOpenHelper.class.getName(), "Dropping database table " + dbConstants.getTableName());
        db.execSQL(dbConstants.getDropTableStatement());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LogDBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
