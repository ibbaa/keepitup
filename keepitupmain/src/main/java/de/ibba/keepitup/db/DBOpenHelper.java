package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.ibba.keepitup.R;

class DBOpenHelper extends SQLiteOpenHelper {

    private final Context context;

    public DBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.job_db_name), null, context.getResources().getInteger(R.integer.job_db_version));
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        Log.d(DBOpenHelper.class.getName(), "onCreate");
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + dbConstants.getJobTableName());
        db.execSQL(dbConstants.getCreateJobTableStatement());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        JobDBConstants dbConstants = new JobDBConstants(context);
        Log.d(DBOpenHelper.class.getName(), "onUpgrade");
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + dbConstants.getJobTableName());
        db.execSQL(dbConstants.getDropJobTableStatement());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
