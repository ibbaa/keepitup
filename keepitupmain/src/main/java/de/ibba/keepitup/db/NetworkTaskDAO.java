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

public class NetworkTaskDAO {

    private final NetworkTaskDBOpenHelper taskDbOpenHelper;
    private final Context context;

    public NetworkTaskDAO(Context context) {
        taskDbOpenHelper = new NetworkTaskDBOpenHelper(context);
        this.context = context;
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
        executeDBOperationInTransaction(null, this::deleteAllNetworkTasks);
    }

    public void updateNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating task with id " + networkTask.getId());
        executeDBOperationInTransaction(networkTask, this::updateNetworkTask);
    }

    public void updateNetworkTaskSuccess(long taskId, boolean success, long timestamp, String message) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating success status to " + success + " with a message " + message + " of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setSuccess(success);
        networkTask.setTimestamp(timestamp);
        networkTask.setMessage(message);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskSuccess);
    }

    public NetworkTask readNetworkTask(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        return executeDBOperationInTransaction(networkTask, this::readNetworkTask);
    }

    public List<NetworkTask> readAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Reading all tasks");
        return executeDBOperationInTransaction(null, this::readAllNetworkTasks);
    }

    private NetworkTask insertNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        values.put(dbConstants.getIndexColumnName(), networkTask.getIndex());
        values.put(dbConstants.getSchedulerIdColumnName(), networkTask.getSchedulerid());
        values.put(dbConstants.getAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getPortColumnName(), networkTask.getPort());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getIntervalColumnName(), networkTask.getInterval());
        values.put(dbConstants.getSuccessColumnName(), networkTask.isSuccess() ? 1 : 0);
        values.put(dbConstants.getTimestampColumnName(), networkTask.getTimestamp());
        values.put(dbConstants.getMessageColumnName(), networkTask.getMessage());
        values.put(dbConstants.getNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(NetworkTaskDAO.class.getName(), "Error inserting task into database. Insert returned -1.");
        }
        networkTask.setId(rowid);
        return networkTask;
    }

    private int deleteNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        int value = db.delete(dbConstants.getTableName(), selection, selectionArgs);
        db.execSQL(dbConstants.getUpdateIndexNetworkTasksStatement(), new Object[] { String.valueOf(networkTask.getIndex())});
        return value;
    }

    @SuppressWarnings("unused")
    private int deleteAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int updateNetworkTaskSuccess(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getSuccessColumnName(), networkTask.isSuccess() ? 1 : 0);
        values.put(dbConstants.getTimestampColumnName(), networkTask.getTimestamp());
        values.put(dbConstants.getMessageColumnName(), networkTask.getMessage());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int updateNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        values.put(dbConstants.getAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getPortColumnName(), networkTask.getPort());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getIntervalColumnName(), networkTask.getInterval());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    @SuppressWarnings("unused")
    private NetworkTask readNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        Cursor cursor = null;
        NetworkTask result = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
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

    @SuppressWarnings("unused")
    private List<NetworkTask> readAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        Cursor cursor = null;
        List<NetworkTask> result = new ArrayList<>();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
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

    private NetworkTask mapCursorToNetworkTask(Cursor cursor) {
        NetworkTask networkTask = new NetworkTask();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(context);
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexIndexColumn = cursor.getColumnIndex(dbConstants.getIndexColumnName());
        int schedulerIdIndexColumn = cursor.getColumnIndex(dbConstants.getSchedulerIdColumnName());
        int indexAddressColumn = cursor.getColumnIndex(dbConstants.getAddressColumnName());
        int indexPortColumn = cursor.getColumnIndex(dbConstants.getPortColumnName());
        int indexAccessTypeColumn = cursor.getColumnIndex(dbConstants.getAccessTypeColumnName());
        int indexIntervalColumn = cursor.getColumnIndex(dbConstants.getIntervalColumnName());
        int indexSuccessColumn = cursor.getColumnIndex(dbConstants.getSuccessColumnName());
        int indexTimestampColumn = cursor.getColumnIndex(dbConstants.getTimestampColumnName());
        int indexMessageColumn = cursor.getColumnIndex(dbConstants.getMessageColumnName());
        int indexNotificationColumn = cursor.getColumnIndex(dbConstants.getNotificationColumnName());
        networkTask.setId(cursor.getInt(indexIdColumn));
        networkTask.setIndex(cursor.getInt(indexIndexColumn));
        networkTask.setSchedulerid(cursor.getInt(schedulerIdIndexColumn));
        networkTask.setAddress(cursor.getString(indexAddressColumn));
        networkTask.setPort(cursor.getInt(indexPortColumn));
        if (cursor.isNull(indexAccessTypeColumn)) {
            networkTask.setAccessType(null);
        } else {
            networkTask.setAccessType(AccessType.forCode(cursor.getInt(indexAccessTypeColumn)));
        }
        networkTask.setInterval(cursor.getInt(indexIntervalColumn));
        networkTask.setSuccess(cursor.getInt(indexSuccessColumn) >= 1);
        networkTask.setTimestamp(cursor.getLong(indexTimestampColumn));
        networkTask.setMessage(cursor.getString(indexMessageColumn));
        networkTask.setNotification(cursor.getInt(indexNotificationColumn) >= 1);
        return networkTask;
    }

    private <T> T executeDBOperationInTransaction(NetworkTask networkTask, DBOperation<T> dbOperation) {
        SQLiteDatabase db = null;
        T result;
        try {
            db = taskDbOpenHelper.getWritableDatabase();
            db.beginTransaction();
            result = dbOperation.execute(networkTask, db);
            db.setTransactionSuccessful();
        } catch (Throwable exc) {
            Log.e(NetworkTaskDAO.class.getName(), "Error executing database operation", exc);
            throw exc;
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Throwable exc) {
                    Log.e(NetworkTaskDAO.class.getName(), "Error committing changes to database", exc);
                }
                try {
                    db.close();
                } catch (Throwable exc) {
                    Log.e(NetworkTaskDAO.class.getName(), "Error closing database", exc);
                }
            }
        }
        return result;
    }
}
