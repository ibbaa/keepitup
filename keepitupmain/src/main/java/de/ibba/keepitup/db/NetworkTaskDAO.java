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
import de.ibba.keepitup.model.SchedulerId;

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

    public NetworkTask updateNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating task with id " + networkTask.getId());
        return executeDBOperationInTransaction(networkTask, this::updateNetworkTask);
    }

    public void updateNetworkTaskRunning(long taskId, boolean running) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating running status to " + running + " of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setRunning(running);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskRunning);
    }

    public int readNetworkTaskInstances(int schedulerId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading instances value of task with scheduler id " + schedulerId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setSchedulerId(schedulerId);
        return executeDBOperationInTransaction(networkTask, this::readNetworkTaskInstances);
    }

    public void increaseNetworkTaskInstances(int schedulerId) {
        Log.d(NetworkTaskDAO.class.getName(), "Increasing instances of task with scheduler id " + schedulerId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setSchedulerId(schedulerId);
        executeDBOperationInTransaction(networkTask, this::increaseNetworkTaskInstances);
    }

    public void decreaseNetworkTaskInstances(int schedulerId) {
        Log.d(NetworkTaskDAO.class.getName(), "Decreasing instances of task with scheduler id " + schedulerId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setSchedulerId(schedulerId);
        executeDBOperationInTransaction(networkTask, this::decreaseNetworkTaskInstances);
    }

    public void resetAllNetworkTaskInstances() {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting instances of all tasks");
        executeDBOperationInTransaction((NetworkTask) null, this::resetAllNetworkTaskInstances);
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
        Log.d(NetworkTaskDAO.class.getName(), "insertNetworkTask, task is " + networkTask);
        ContentValues values = new ContentValues();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(getContext());
        SchedulerId schedulerId = idGenerator.createUniqueSchedulerId(db);
        if (!schedulerId.isValid()) {
            Log.e(NetworkTaskDAO.class.getName(), "Error inserting task into database. Scheduler id generation failed");
            networkTask.setSchedulerId(SchedulerIdGenerator.ERROR_SCHEDULER_ID);
            networkTask.setId(-1);
            return networkTask;
        } else {
            Log.d(NetworkTaskDAO.class.getName(), "Generated schduler id is " + schedulerId.getSchedulerId());
            networkTask.setSchedulerId(schedulerId.getSchedulerId());
        }
        networkTask.setInstances(0);
        values.put(dbConstants.getIndexColumnName(), networkTask.getIndex());
        values.put(dbConstants.getSchedulerIdColumnName(), networkTask.getSchedulerId());
        values.put(dbConstants.getInstancesColumnName(), networkTask.getInstances());
        values.put(dbConstants.getAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getPortColumnName(), networkTask.getPort());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getIntervalColumnName(), networkTask.getInterval());
        values.put(dbConstants.getOnlyWifiColumnName(), networkTask.isOnlyWifi() ? 1 : 0);
        values.put(dbConstants.getNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        values.put(dbConstants.getRunningColumnName(), networkTask.isRunning() ? 1 : 0);
        Log.d(NetworkTaskDAO.class.getName(), "Inserting...");
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        if (rowid < 0) {
            Log.e(NetworkTaskDAO.class.getName(), "Error inserting task into database. Insert returned -1.");
        }
        networkTask.setId(rowid);
        return networkTask;
    }

    private int deleteNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "deleteNetworkTask, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        int value = db.delete(dbConstants.getTableName(), selection, selectionArgs);
        Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getUpdateIndexNetworkTasksStatement() + " with a parameter of " + networkTask.getIndex());
        db.execSQL(dbConstants.getUpdateIndexNetworkTasksStatement(), new Object[]{String.valueOf(networkTask.getIndex())});
        return value;
    }

    private int deleteAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "deleteAllNetworkTasks, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private int updateNetworkTaskRunning(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "updateNetworkTaskRunning, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getRunningColumnName(), networkTask.isRunning() ? 1 : 0);
        Log.d(NetworkTaskDAO.class.getName(), "Updating to " + networkTask.isRunning());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int increaseNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "increaseNetworkTaskInstances, task is " + networkTask);
        int instances = readNetworkTaskInstances(networkTask, db);
        Log.d(NetworkTaskDAO.class.getName(), "Current number of instances is " + instances);
        if (instances < 0) {
            networkTask.setInstances(0);
        } else {
            networkTask.setInstances(instances + 1);
        }
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getSchedulerIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getSchedulerId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getInstancesColumnName(), networkTask.getInstances());
        Log.d(NetworkTaskDAO.class.getName(), "Updating instances to " + networkTask.getInstances());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int decreaseNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "decreaseNetworkTaskInstances, task is " + networkTask);
        int instances = readNetworkTaskInstances(networkTask, db);
        Log.d(NetworkTaskDAO.class.getName(), "Current number of instances is " + instances);
        if (instances <= 0) {
            networkTask.setInstances(0);
        } else {
            networkTask.setInstances(instances - 1);
        }
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getSchedulerIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getSchedulerId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getInstancesColumnName(), networkTask.getInstances());
        Log.d(NetworkTaskDAO.class.getName(), "Updating instances to " + networkTask.getInstances());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetAllNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetAllNetworkTaskInstances, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getInstancesColumnName(), 0);
        return db.update(dbConstants.getTableName(), values, null, null);
    }

    private NetworkTask updateNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "updateNetworkTask, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(getContext());
        SchedulerId schedulerId = idGenerator.createUniqueSchedulerId(db);
        if (!schedulerId.isValid()) {
            Log.e(NetworkTaskDAO.class.getName(), "Error updating task. Scheduler id generation failed");
            networkTask.setSchedulerId(SchedulerIdGenerator.ERROR_SCHEDULER_ID);
            return networkTask;
        } else {
            Log.d(NetworkTaskDAO.class.getName(), "Generated schduler id is " + schedulerId.getSchedulerId());
            networkTask.setSchedulerId(schedulerId.getSchedulerId());
        }
        networkTask.setInstances(0);
        ContentValues values = new ContentValues();
        values.put(dbConstants.getSchedulerIdColumnName(), networkTask.getSchedulerId());
        values.put(dbConstants.getInstancesColumnName(), networkTask.getInstances());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getOnlyWifiColumnName(), networkTask.isOnlyWifi() ? 1 : 0);
        values.put(dbConstants.getNotificationColumnName(), networkTask.isNotification() ? 1 : 0);
        values.put(dbConstants.getAddressColumnName(), networkTask.getAddress());
        values.put(dbConstants.getPortColumnName(), networkTask.getPort());
        values.put(dbConstants.getAccessTypeColumnName(), networkTask.getAccessType() == null ? null : networkTask.getAccessType().getCode());
        values.put(dbConstants.getIntervalColumnName(), networkTask.getInterval());
        Log.d(NetworkTaskDAO.class.getName(), "Updating...");
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return networkTask;
    }

    private int readNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskInstances, task is " + networkTask);
        Cursor cursor = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getReadInstancesStatement() + " with a parameter of " + networkTask.getSchedulerId());
            cursor = db.rawQuery(dbConstants.getReadInstancesStatement(), new String[]{String.valueOf(networkTask.getSchedulerId())});
            if (cursor.moveToFirst()) {
                int value = cursor.getInt(0);
                Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskInstances, returning " + value);
                return value;
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
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskInstances, returning -1");
        return -1;
    }

    private NetworkTask readNetworkTask(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTask, task is " + networkTask);
        Cursor cursor = null;
        NetworkTask result = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getReadNetworkTaskStatement() + " with a parameter of " + networkTask.getId());
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
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTask, returning " + result);
        return result;
    }

    private List<NetworkTask> readAllNetworkTasks(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "readAllNetworkTasks, task is " + networkTask);
        Cursor cursor = null;
        List<NetworkTask> result = new ArrayList<>();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllNetworkTasksStatement());
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
        Log.d(NetworkTaskDAO.class.getName(), "readAllNetworkTasks, returning " + result);
        return result;
    }

    private NetworkTask mapCursorToNetworkTask(Cursor cursor) {
        NetworkTask networkTask = new NetworkTask();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexIndexColumn = cursor.getColumnIndex(dbConstants.getIndexColumnName());
        int indexSchedulerIdColumn = cursor.getColumnIndex(dbConstants.getSchedulerIdColumnName());
        int indexInstancesColumn = cursor.getColumnIndex(dbConstants.getInstancesColumnName());
        int indexAddressColumn = cursor.getColumnIndex(dbConstants.getAddressColumnName());
        int indexPortColumn = cursor.getColumnIndex(dbConstants.getPortColumnName());
        int indexAccessTypeColumn = cursor.getColumnIndex(dbConstants.getAccessTypeColumnName());
        int indexIntervalColumn = cursor.getColumnIndex(dbConstants.getIntervalColumnName());
        int indexOnlyWifiColumn = cursor.getColumnIndex(dbConstants.getOnlyWifiColumnName());
        int indexNotificationColumn = cursor.getColumnIndex(dbConstants.getNotificationColumnName());
        int indexRunningColumn = cursor.getColumnIndex(dbConstants.getRunningColumnName());
        networkTask.setId(cursor.getInt(indexIdColumn));
        networkTask.setIndex(cursor.getInt(indexIndexColumn));
        networkTask.setSchedulerId(cursor.getInt(indexSchedulerIdColumn));
        networkTask.setInstances(cursor.getInt(indexInstancesColumn));
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
