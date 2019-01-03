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

    private final TaskDBOpenHelper taskDbOpenHelper;
    private final Context context;

    public NetworkTaskDAO(Context context) {
        taskDbOpenHelper = new TaskDBOpenHelper(context);
        this.context = context;
    }

    public long insertNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Inserting job " + networkTask);
        return executeDBOperationInTransaction(networkTask, this::insertNetworkTask);
    }

    public void deleteNetworkTask(int taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting job with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::deleteNetworkTask);
    }

    public void deleteAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting all jobs");
        executeDBOperationInTransaction(null, this::deleteAllNetworkTasks);
    }

    public void updateNetworkTaskNotification(int taskId, boolean notification) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating notification status to " + notification + " of job with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setNotification(notification);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskNotification);
    }

    public void updateNetworkTaskSuccess(int taskId, boolean success, String message) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating success status to " + success + " with a message " + message + " of job with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setSuccess(success);
        networkTask.setMessage(message);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskSuccess);
    }

    public List<NetworkTask> readAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Read all jobs");
        return executeDBOperationInTransaction(null, this::readAllNetworkTasks);
    }

    public long readMaximumIndex() {
        Log.d(NetworkTaskDAO.class.getName(), "Read maximum index");
        return executeDBOperationInTransaction(null, this::readMaximumIndex);
    }

    private long insertNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        values.put(dbConstants.getTaskIndexColumnName(), networkTask.getIndex());
        values.put(dbConstants.getTaskAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getTaskAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getTaskIntervalColumnName(), networkTask.getInterval());
        values.put(dbConstants.getTaskSuccessColumnName(), networkTask.isSuccess() ? 1 : 0);
        values.put(dbConstants.getTaskMessageColumnName(), networkTask.getMessage());
        values.put(dbConstants.getTaskNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        long rowid = db.insert(dbConstants.getTaskTableName(), null, values);
        if (rowid < 0) {
            Log.e(NetworkTaskDAO.class.getName(), "Error inserting job into database. Insert returned -1.");
        }
        return rowid;
    }

    private int deleteNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        String selection = dbConstants.getTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        return db.delete(dbConstants.getTaskTableName(), selection, selectionArgs);
    }

    @SuppressWarnings("unused")
    private int deleteAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        return db.delete(dbConstants.getTaskTableName(), null, null);
    }

    private int updateNetworkTaskSuccess(NetworkTask networkTask, SQLiteDatabase db) {
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        String selection = dbConstants.getTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getTaskSuccessColumnName(), networkTask.isSuccess() ? 1 : 0);
        values.put(dbConstants.getTaskMessageColumnName(), networkTask.getMessage());
        return db.update(dbConstants.getTaskTableName(), values, selection, selectionArgs);
    }

    private int updateNetworkTaskNotification(NetworkTask networkTask, SQLiteDatabase db) {
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        String selection = dbConstants.getTaskIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getTaskNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        return db.update(dbConstants.getTaskTableName(), values, selection, selectionArgs);
    }

    @SuppressWarnings("unused")
    private List<NetworkTask> readAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        Cursor cursor = null;
        List<NetworkTask> result = new ArrayList<>();
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        try {
            cursor = db.rawQuery(dbConstants.getAllTasksStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getTaskIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    NetworkTask mappedNetworkTask = mapCursorToNetworkJob(cursor);
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

    @SuppressWarnings("unused")
    private long readMaximumIndex(NetworkTask networkTask, SQLiteDatabase db) {
        Cursor cursor = null;
        long index = 0;
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        try {
            cursor = db.rawQuery(dbConstants.getMaximumIndexStatement(), null);
            while (cursor.moveToNext()) {
                if (!cursor.isNull(0)) {
                    index = cursor.getLong(0);
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
        return index;
    }

    private NetworkTask mapCursorToNetworkJob(Cursor cursor) {
        NetworkTask networkTask = new NetworkTask();
        TaskDBConstants dbConstants = new TaskDBConstants(context);
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getTaskIdColumnName());
        int indexIndexColumn = cursor.getColumnIndex(dbConstants.getTaskIndexColumnName());
        int indexAddressColumn = cursor.getColumnIndex(dbConstants.getTaskAddressColumnName());
        int indexAccessTypeColumn = cursor.getColumnIndex(dbConstants.getTaskAccessTypeColumnName());
        int indexIntervalColumn = cursor.getColumnIndex(dbConstants.getTaskIntervalColumnName());
        int indexSuccessColumn = cursor.getColumnIndex(dbConstants.getTaskSuccessColumnName());
        int indexMessageColumn = cursor.getColumnIndex(dbConstants.getTaskMessageColumnName());
        int indexNotificationColumn = cursor.getColumnIndex(dbConstants.getTaskNotificationColumnName());
        networkTask.setId(cursor.getInt(indexIdColumn));
        networkTask.setIndex(cursor.getInt(indexIndexColumn));
        networkTask.setAddress(cursor.getString(indexAddressColumn));
        if (cursor.isNull(indexAccessTypeColumn)) {
            networkTask.setAccessType(null);
        } else {
            networkTask.setAccessType(AccessType.forCode(cursor.getInt(indexAccessTypeColumn)));
        }
        networkTask.setInterval(cursor.getInt(indexIntervalColumn));
        networkTask.setSuccess(cursor.getInt(indexSuccessColumn) >= 1);
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
