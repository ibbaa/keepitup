package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.ibba.keepitup.R;

class TaskDBOpenHelper extends SQLiteOpenHelper {

    private final Context context;

    public TaskDBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.task_db_name), null, context.getResources().getInteger(R.integer.task_db_version));
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        Log.d(TaskDBOpenHelper.class.getName(), "onCreate");
        Log.i(TaskDBOpenHelper.class.getName(), "Creating database table " + dbConstants.getTaskTableName());
        db.execSQL(dbConstants.getCreateTaskTableStatement());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        Log.d(TaskDBOpenHelper.class.getName(), "onUpgrade");
        Log.i(TaskDBOpenHelper.class.getName(), "Dropping database table " + dbConstants.getTaskTableName());
        db.execSQL(dbConstants.getDropTaskTableStatement());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TaskDBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
