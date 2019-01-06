package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.ibba.keepitup.R;

class NetworkTaskDBOpenHelper extends SQLiteOpenHelper {

    private final Context context;

    public NetworkTaskDBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.task_db_name), null, context.getResources().getInteger(R.integer.task_db_version));
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        Log.d(NetworkTaskDBOpenHelper.class.getName(), "onCreate");
        Log.i(NetworkTaskDBOpenHelper.class.getName(), "Creating database table " + dbConstants.getTableName());
        db.execSQL(dbConstants.getCreateTableStatement());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        Log.d(NetworkTaskDBOpenHelper.class.getName(), "onUpgrade");
        Log.i(NetworkTaskDBOpenHelper.class.getName(), "Dropping database table " + dbConstants.getTableName());
        db.execSQL(dbConstants.getDropTableStatement());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(NetworkTaskDBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
