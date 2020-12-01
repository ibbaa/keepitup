package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    private final DBSetup setup;

    private static DBOpenHelper dbOpenHelper;

    private DBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        setup = new DBSetup(context);
    }

    public static synchronized DBOpenHelper getInstance(Context context) {
        if (dbOpenHelper == null) {
            dbOpenHelper = new DBOpenHelper(context);
        }
        return dbOpenHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "onCreate");
        setup.createTables(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onUpgrade");
        setup.recreateTables(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
