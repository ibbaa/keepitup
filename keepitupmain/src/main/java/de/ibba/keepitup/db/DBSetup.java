package de.ibba.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.ibba.keepitup.logging.Log;

public class DBSetup {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final LogDBConstants logDBConstants;
    private final SchedulerIdHistoryDBConstants schedulerIdDBConstants;

    public DBSetup(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        logDBConstants = new LogDBConstants(context);
        schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
    }

    public void createTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createTables");
        createNetworkTaskTable(db);
        createLogTable(db);
        createSchedulerIdHistoryTable(db);
    }

    public void createNetworkTaskTable(SQLiteDatabase db) {
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getCreateTableStatement());
    }

    public void createLogTable(SQLiteDatabase db) {
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getCreateTableStatement());
    }

    public void createSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.i(DBOpenHelper.class.getName(), "Creating database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getCreateTableStatement());
    }

    public void dropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropTables");
        dropSchedulerIdHistoryTable(db);
        dropLogTable(db);
        dropNetworkTaskTable(db);
    }

    public void dropNetworkTaskTable(SQLiteDatabase db) {
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropTableStatement());
    }

    public void dropLogTable(SQLiteDatabase db) {
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getDropTableStatement());
    }

    public void dropSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.i(DBOpenHelper.class.getName(), "Dropping database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getDropTableStatement());
    }

    public void recreateTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateTables");
        dropTables(db);
        createTables(db);
    }

    public void createTables(Context context) {
        createTables(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void createNetworkTaskTable(Context context) {
        createNetworkTaskTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void createLogTable(Context context) {
        createLogTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void createSchedulerIdHistoryTable(Context context) {
        createSchedulerIdHistoryTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void dropTables(Context context) {
        dropTables(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void dropNetworkTaskTable(Context context) {
        dropNetworkTaskTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void dropLogTable(Context context) {
        dropLogTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void dropSchedulerIdHistoryTable(Context context) {
        dropSchedulerIdHistoryTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateTables(Context context) {
        recreateTables(DBOpenHelper.getInstance(context).getWritableDatabase());
    }
}
