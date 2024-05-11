/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerId;

import java.util.ArrayList;
import java.util.List;

public class NetworkTaskDAO extends BaseDAO {

    public NetworkTaskDAO(Context context) {
        super(context);
    }

    public NetworkTask insertNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Inserting task " + networkTask);
        NetworkTask returnedTask = executeDBOperationInTransaction(networkTask, this::insertNetworkTask);
        Log.d(NetworkTaskDAO.class.getName(), "Inserted task is " + returnedTask);
        dumpDatabase("Dump after insertNetworkTask call");
        return returnedTask;
    }

    public void deleteNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting task with id " + networkTask.getId());
        executeDBOperationInTransaction(networkTask, this::deleteNetworkTask);
        dumpDatabase("Dump after deleteNetworkTask call");
    }

    public void deleteAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Deleting all tasks");
        executeDBOperationInTransaction((NetworkTask) null, this::deleteAllNetworkTasks);
        dumpDatabase("Dump after deleteAllNetworkTasks call");
    }

    public NetworkTask updateNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating task with id " + networkTask.getId());
        NetworkTask returnedTask = executeDBOperationInTransaction(networkTask, this::updateNetworkTask);
        Log.d(NetworkTaskDAO.class.getName(), "Updated task is " + returnedTask);
        dumpDatabase("Dump after updateNetworkTask call");
        return returnedTask;
    }

    public void updateNetworkTaskRunning(long taskId, boolean running) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating running status to " + running + " of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setRunning(running);
        networkTask.setLastScheduled(-1);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskRunning);
        dumpDatabase("Dump after updateNetworkTaskRunning call");
    }

    public int readNetworkTasksRunning() {
        Log.d(NetworkTaskDAO.class.getName(), "Reading number of running tasks");
        int runningTasks = executeDBOperationInTransaction((NetworkTask) null, this::readNetworkTasksRunning);
        Log.d(NetworkTaskDAO.class.getName(), "Number of running tasks " + runningTasks);
        return runningTasks;
    }

    public int readNetworkTaskInstances(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading instances value of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        int readInstances = executeDBOperationInTransaction(networkTask, this::readNetworkTaskInstances);
        Log.d(NetworkTaskDAO.class.getName(), "Number of instances of task with id " + taskId + " is " + readInstances);
        return readInstances;
    }

    public void increaseNetworkTaskInstances(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Increasing instances of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::increaseNetworkTaskInstances);
        dumpDatabase("Dump after increaseNetworkTaskInstances call");
    }

    public void decreaseNetworkTaskInstances(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Decreasing instances of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::decreaseNetworkTaskInstances);
        dumpDatabase("Dump after decreaseNetworkTaskInstances call");
    }

    public void resetNetworkTaskInstances(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting instances of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::resetNetworkTaskInstances);
        dumpDatabase("Dump after resetNetworkTaskInstances call");
    }

    public void resetAllNetworkTaskInstances() {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting instances of all tasks");
        executeDBOperationInTransaction((NetworkTask) null, this::resetAllNetworkTaskInstances);
        dumpDatabase("Dump after resetAllNetworkTaskInstances call");
    }

    public void updateNetworkTaskLastScheduled(long taskId, long lastScheduled) {
        Log.d(NetworkTaskDAO.class.getName(), "Updating last scheduled timestamp to " + lastScheduled + " of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        networkTask.setLastScheduled(lastScheduled);
        executeDBOperationInTransaction(networkTask, this::updateNetworkTaskLastScheduled);
        dumpDatabase("Dump after updateNetworkTaskLastScheduled call");
    }

    public void resetNetworkTaskLastScheduled(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting last scheduled timestamp of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::resetNetworkTaskLastScheduled);
        dumpDatabase("Dump after resetNetworkTaskLastScheduled call");
    }

    public void resetNetworkTaskLastScheduledAndFailureCount(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting last scheduled timestamp and failure count of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::resetNetworkTaskLastScheduledAndFailureCount);
        dumpDatabase("Dump after resetNetworkTaskLastScheduled call");
    }

    public int readNetworkTaskFailureCount(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading failure count value of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        int readFailureCount = executeDBOperationInTransaction(networkTask, this::readNetworkTaskFailureCount);
        Log.d(NetworkTaskDAO.class.getName(), "Failure count of task with id " + taskId + " is " + readFailureCount);
        return readFailureCount;
    }

    public void increaseNetworkTaskFailureCount(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Increasing failure count of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::increaseNetworkTaskFailureCount);
        dumpDatabase("Dump after increaseNetworkTaskFailureCount call");
    }

    public void resetNetworkTaskFailureCount(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting failure count of task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        executeDBOperationInTransaction(networkTask, this::resetNetworkTaskFailureCount);
        dumpDatabase("Dump after resetNetworkTaskLastScheduled call");
    }

    public void resetAllNetworkTaskFailureCount() {
        Log.d(NetworkTaskDAO.class.getName(), "Resetting failure count of all tasks");
        executeDBOperationInTransaction((NetworkTask) null, this::resetAllNetworkTaskFailureCount);
        dumpDatabase("Dump after resetAllNetworkTaskFailureCount call");
    }

    public NetworkTask readNetworkTask(long taskId) {
        Log.d(NetworkTaskDAO.class.getName(), "Reading task with id " + taskId);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(taskId);
        NetworkTask returnedTask = executeDBOperationInTransaction(networkTask, this::readNetworkTask);
        Log.d(NetworkTaskDAO.class.getName(), "Task with id " + taskId + " is " + returnedTask);
        return returnedTask;
    }

    public List<NetworkTask> readAllNetworkTasks() {
        Log.d(NetworkTaskDAO.class.getName(), "Reading all tasks");
        List<NetworkTask> taskList = executeDBOperationInTransaction((NetworkTask) null, this::readAllNetworkTasks);
        Log.d(NetworkTaskDAO.class.getName(), "Number of tasks read: " + taskList.size());
        return taskList;
    }

    private void dumpDatabase(String message) {
        if (BuildConfig.DEBUG) {
            Dump.dump(NetworkTaskDAO.class.getName(), message, NetworkTask.class.getSimpleName().toLowerCase(), this::readAllNetworkTasks);
            SchedulerIdHistoryDAO historyDAO = new SchedulerIdHistoryDAO(getContext());
            Dump.dump(NetworkTaskDAO.class.getName(), message, SchedulerId.class.getSimpleName().toLowerCase(), historyDAO::readAllSchedulerIds);
        }
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
            Log.d(NetworkTaskDAO.class.getName(), "Generated scheduler id is " + schedulerId.getSchedulerId());
            networkTask.setSchedulerId(schedulerId.getSchedulerId());
        }
        networkTask.setInstances(0);
        networkTask.setLastScheduled(-1);
        networkTask.setFailureCount(0);
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
        values.put(dbConstants.getLastScheduledColumnName(), networkTask.getLastScheduled());
        values.put(dbConstants.getFailureCountColumnName(), networkTask.getFailureCount());
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
        values.put(dbConstants.getLastScheduledColumnName(), networkTask.getLastScheduled());
        if (networkTask.isRunning()) {
            values.put(dbConstants.getFailureCountColumnName(), 0);
        }
        Log.d(NetworkTaskDAO.class.getName(), "Updating to " + networkTask.isRunning());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int readNetworkTasksRunning(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTasksRunning, task is " + networkTask);
        Cursor cursor = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getRunningCountStatement() + " with a parameter of 1");
            cursor = db.rawQuery(dbConstants.getRunningCountStatement(), new String[]{"1"});
            if (cursor.moveToFirst()) {
                int value = cursor.getInt(0);
                Log.d(NetworkTaskDAO.class.getName(), "readNetworkTasksRunning, returning " + value);
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
        Log.d(NetworkTaskDAO.class.getName(), "readNumberNetworkTasksRunning, returning -1");
        return -1;
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
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
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
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getInstancesColumnName(), networkTask.getInstances());
        Log.d(NetworkTaskDAO.class.getName(), "Updating instances to " + networkTask.getInstances());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetNetworkTaskInstances, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getInstancesColumnName(), 0);
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetAllNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetAllNetworkTaskInstances, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getInstancesColumnName(), 0);
        return db.update(dbConstants.getTableName(), values, null, null);
    }

    private int updateNetworkTaskLastScheduled(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "updateNetworkTaskLastScheduled, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getLastScheduledColumnName(), networkTask.getLastScheduled());
        Log.d(NetworkTaskDAO.class.getName(), "Updating to " + networkTask.getLastScheduled());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetNetworkTaskLastScheduled(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetNetworkTaskLastScheduled, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getLastScheduledColumnName(), -1);
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetNetworkTaskLastScheduledAndFailureCount(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetNetworkTaskLastScheduledAndFailureCount, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getLastScheduledColumnName(), -1);
        values.put(dbConstants.getFailureCountColumnName(), 0);
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int increaseNetworkTaskFailureCount(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "increaseNetworkTaskFailureCount, task is " + networkTask);
        int failureCount = readNetworkTaskFailureCount(networkTask, db);
        Log.d(NetworkTaskDAO.class.getName(), "Current failure count is " + failureCount);
        if (failureCount < 0) {
            networkTask.setFailureCount(0);
        } else {
            networkTask.setFailureCount(failureCount + 1);
        }
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getFailureCountColumnName(), networkTask.getFailureCount());
        Log.d(NetworkTaskDAO.class.getName(), "Updating failure count to " + networkTask.getInstances());
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetNetworkTaskFailureCount(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetNetworkTaskFailureCount, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        String selection = dbConstants.getIdColumnName() + " = ?";
        String[] selectionArgs = {String.valueOf(networkTask.getId())};
        ContentValues values = new ContentValues();
        values.put(dbConstants.getFailureCountColumnName(), 0);
        return db.update(dbConstants.getTableName(), values, selection, selectionArgs);
    }

    private int resetAllNetworkTaskFailureCount(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "resetAllNetworkTaskFailureCount, task is " + networkTask);
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getFailureCountColumnName(), 0);
        return db.update(dbConstants.getTableName(), values, null, null);
    }

    @SuppressWarnings({"ExtractMethodRecommender"})
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
            Log.d(NetworkTaskDAO.class.getName(), "Generated scheduler id is " + schedulerId.getSchedulerId());
            networkTask.setSchedulerId(schedulerId.getSchedulerId());
        }
        networkTask.setInstances(0);
        networkTask.setLastScheduled(-1);
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
        values.put(dbConstants.getLastScheduledColumnName(), networkTask.getLastScheduled());
        Log.d(NetworkTaskDAO.class.getName(), "Updating...");
        db.update(dbConstants.getTableName(), values, selection, selectionArgs);
        return networkTask;
    }

    private int readNetworkTaskInstances(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskInstances, task is " + networkTask);
        Cursor cursor = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getReadInstancesStatement() + " with a parameter of " + networkTask.getId());
            cursor = db.rawQuery(dbConstants.getReadInstancesStatement(), new String[]{String.valueOf(networkTask.getId())});
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

    private int readNetworkTaskFailureCount(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskFailureCount, task is " + networkTask);
        Cursor cursor = null;
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        try {
            Log.d(NetworkTaskDAO.class.getName(), "Executing SQL " + dbConstants.getReadFailureCountStatement() + " with a parameter of " + networkTask.getId());
            cursor = db.rawQuery(dbConstants.getReadFailureCountStatement(), new String[]{String.valueOf(networkTask.getId())});
            if (cursor.moveToFirst()) {
                int value = cursor.getInt(0);
                Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskFailureCount, returning " + value);
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
        Log.d(NetworkTaskDAO.class.getName(), "readNetworkTaskFailureCount, returning -1");
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
        int indexLastScheduledColumn = cursor.getColumnIndex(dbConstants.getLastScheduledColumnName());
        int indexFailureCountColumn = cursor.getColumnIndex(dbConstants.getFailureCountColumnName());
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
        networkTask.setLastScheduled(cursor.getLong(indexLastScheduledColumn));
        networkTask.setFailureCount(cursor.getInt(indexFailureCountColumn));
        return networkTask;
    }
}
