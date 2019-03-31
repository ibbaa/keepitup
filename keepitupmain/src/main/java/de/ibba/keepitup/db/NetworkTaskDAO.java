package de.ibba.keepitup.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.SchedulerIdGenerator;

public class NetworkTaskDAO extends BaseDAO {

    public NetworkTaskDAO(Context context) {
        super(context);
    }

    public NetworkTask insertNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Inserting task " + networkTask);
        return executeDBOperationInTransaction(networkTask, this::insertNetworkTask);
    }

    public void deleteNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting task with id " + networkTask.getId());
        executeDBOperationInTransaction(networkTask, this::deleteNetworkTask);
    }

    public void deleteAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting all tasks");
        executeDBOperationInTransaction((NetworkTask) null, this::deleteAllNetworkTasks);
    }

    public void updateNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating task with id " + networkTask.getId());
        executeDBOperationInTransaction(networkTask, this::updateNetworkTask);
    }

    public void updateNetworkTaskRunning(long taskId, boolean running) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating running status to " + running + " of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setRunning(running);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskRunning);
    }

    public NetworkTask readNetworkTask(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        return executeDBOperationInTransaction(networkTask, this::readNetworkTask);
    }

    public List<NetworkTask> readAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Reading all tasks");
        return executeDBOperationInTransaction((NetworkTask) null, this::readAllNetworkTasks);
    }

    private NetworkTask insertNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        networkTask.setSchedulerId(getUniqueSchedulerId(db));
        values.put(dbConstants.getIndexColumnName(), networkTask.getIndex());
        values.put(dbConstants.getSchedulerIdColumnName(), networkTask.getSchedulerId());
        values.put(dbConstants.getAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getPortColumnName(), networkTask.getPort());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getIntervalColumnName(), networkTask.getInterval());
        values.put(dbConstants.getOnlyWifiColumnName(), networkTask.isOnlyWifi() ? 1 : 0);
        values.put(dbConstants.getNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        values.put(dbConstants.getRunningColumnName(), networkTask.isRunning() ? 1 : 0);
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(NetworkTaskDAO.class.getName(), "Error inserting task into database. Insert returned -1.");
        }
        networkTask.setId(rowid);

        return networkTask;
    }

    private int deleteNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        int value = db.delete(dbConstants.getTableName(), selection, selectionArgs);
        db.execSQL(dbConstants.getUpdateIndexNetworkTasksStatement(), new Object[]{String.valueOf(networkTask.getIndex())});
        return value;
    }

    private int deleteAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int updateNetworkTaskRunning(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getRunningColumnName(), networkTask.isRunning() ? 1 : 0);
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int updateNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getOnlyWifiColumnName(), networkTask.isOnlyWifi() ? 1 : 0);
        values.put(dbConstants.getNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        values.put(dbConstants.getAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getPortColumnName(), networkTask.getPort());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getIntervalColumnName(), networkTask.getInterval());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private NetworkTask readNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        Cursor cursor = null;
        NetworkTask result = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getReadNetworkTaskStatement(), new String[]{String.valueOf(networkTask.getId())});
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    result = mapCursorToNetworkTask(cursor);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(NetworkTaskDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private List<NetworkTask> readAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        Cursor cursor = null;
        List<NetworkTask> result = new ArrayList<>();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getReadAllNetworkTasksStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    NetworkTask mappedNetworkTask = mapCursorToNetworkTask(cursor);
                    result.add(mappedNetworkTask);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(NetworkTaskDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }

    private int getUniqueSchedulerId(SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "getUniqueSchedulerId");
        int schedulerId = SchedulerIdGenerator.createSchedulerId();
        Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id is " + schedulerId);
        while (readSchedulerIdCount(schedulerId, db) > 0) {
            Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id exists. Creating new one.");
            schedulerId = SchedulerIdGenerator.createSchedulerId();
            Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id is " + schedulerId);
        }
        Log.d(NetworkTaskDAO.class.getName(), "Created random scheduler id is unique and does not exist.");
        return schedulerId;
    }

    private long readSchedulerIdCount(int schedulerId, SQLiteDatabase db) {
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
                    Log.e(LogDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return 0;
    }

    private NetworkTask mapCursorToNetworkTask(Cursor cursor) {
        NetworkTask networkTask = new NetworkTask();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexIndexColumn = cursor.getColumnIndex(dbConstants.getIndexColumnName());
        int schedulerIdIndexColumn = cursor.getColumnIndex(dbConstants.getSchedulerIdColumnName());
        int indexAddressColumn = cursor.getColumnIndex(dbConstants.getAddressColumnName());
        int indexPortColumn = cursor.getColumnIndex(dbConstants.getPortColumnName());
        int indexAccessTypeColumn = cursor.getColumnIndex(dbConstants.getAccessTypeColumnName());
        int indexIntervalColumn = cursor.getColumnIndex(dbConstants.getIntervalColumnName());
        int indexOnlyWifiColumn = cursor.getColumnIndex(dbConstants.getOnlyWifiColumnName());
        int indexNotificationColumn = cursor.getColumnIndex(dbConstants.getNotificationColumnName());
        int indexRunningColumn = cursor.getColumnIndex(dbConstants.getRunningColumnName());
        networkTask.setId(cursor.getInt(indexIdColumn));
        networkTask.setIndex(cursor.getInt(indexIndexColumn));
        networkTask.setSchedulerId(cursor.getInt(schedulerIdIndexColumn));
        networkTask.setAddress(cursor.getString(indexAddressColumn));
        networkTask.setPort(cursor.getInt(indexPortColumn));
        if (cursor.isNull(indexAccessTypeColumn)) {
            networkTask.setAccessType(null);
        } else {
            networkTask.setAccessType(AccessType.forCode(cursor.getInt(indexAccessTypeColumn)));
        }
        networkTask.setInterval(cursor.getInt(indexIntervalColumn));
        networkTask.setOnlyWifi(cursor.getInt(indexOnlyWifiColumn) >= 1);
        networkTask.setNotification(cursor.getInt(indexNotificationColumn) >= 1);
        networkTask.setRunning(cursor.getInt(indexRunningColumn) >= 1);
        return networkTask;
    }
}
