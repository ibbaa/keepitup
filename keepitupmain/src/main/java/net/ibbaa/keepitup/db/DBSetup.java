/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.model.validation.IntervalValidator;
import net.ibbaa.keepitup.model.validation.NetworkTaskValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBSetup {

    private final Context context;
    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final LogDBConstants logDBConstants;
    private final SchedulerIdHistoryDBConstants schedulerIdDBConstants;
    private final IntervalDBConstants intervalDBConstants;
    private final SchedulerStateDBConstants schedulerStateDBConstants;
    private final AccessTypeDataDBConstants accessTypeDataDBConstants;

    public DBSetup(Context context) {
        this.context = context;
        this.networkTaskDBConstants = new NetworkTaskDBConstants(context);
        this.logDBConstants = new LogDBConstants(context);
        this.schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
        this.intervalDBConstants = new IntervalDBConstants(context);
        this.schedulerStateDBConstants = new SchedulerStateDBConstants(context);
        this.accessTypeDataDBConstants = new AccessTypeDataDBConstants(context);
    }

    public void createTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createTables");
        createNetworkTaskTable(db);
        createLogTable(db);
        createSchedulerIdHistoryTable(db);
        createIntervalTable(db);
        createSchedulerStateTable(db);
        createAccessTypeDataTable(db);
    }

    public void tryDropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "tryDropTables");
        try {
            dropSchedulerIdHistoryTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropSchedulerIdHistoryTable failed ", exc);
        }
        try {
            dropLogTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropLogTable failed ", exc);
        }
        try {
            dropNetworkTaskTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropNetworkTaskTable failed ", exc);
        }
        try {
            dropIntervalTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropIntervalTable failed ", exc);
        }
        try {
            dropSchedulerStateTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropSchedulerStateTable failed ", exc);
        }
        try {
            dropAccessTypeDataTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropAccessTypeDataTable failed ", exc);
        }
    }

    public void createNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Creating database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getCreateTableStatement());
    }

    public void addFailureCountColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Adding column " + networkTaskDBConstants.getFailureCountColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddFailureCountColumnStatement());
    }

    public void addHighPrioColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Adding column " + networkTaskDBConstants.getHighPrioColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddHighPrioColumnStatement());
    }

    public void addNameColumnToNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Adding column " + networkTaskDBConstants.getNameColumnName() + " to table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getAddNameColumnStatement());
    }

    public void initializeFailureCountColumn(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Setting " + networkTaskDBConstants.getFailureCountColumnName() + " to 0 in " + networkTaskDBConstants.getTableName());
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getFailureCountColumnName(), 0);
        db.update(dbConstants.getTableName(), values, null, null);
    }

    public void createLogTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Creating database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getCreateTableStatement());
    }

    public void createSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Creating database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getCreateTableStatement());
    }

    public void createIntervalTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Creating database table " + intervalDBConstants.getTableName());
        db.execSQL(intervalDBConstants.getCreateTableStatement());
    }

    public void initializeSchedulerStateTable(SQLiteDatabase db) {
        db.execSQL(schedulerStateDBConstants.getInitializeSchedulerStateStatement());
    }

    public void createSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Creating database table " + schedulerStateDBConstants.getTableName());
        db.execSQL(schedulerStateDBConstants.getCreateTableStatement());
        initializeSchedulerStateTable(db);
    }

    public void createAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Creating database table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getCreateTableStatement());
    }

    public void initializeAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "initializeAccessTypeDataTable");
        db.execSQL(accessTypeDataDBConstants.getMigrateNetworkTasksAccessTypeDataStatement());
        AccessTypeData accessTypeData = new AccessTypeData(getContext());
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        ContentValues values = new ContentValues();
        values.put(dbConstants.getPingCountColumnName(), accessTypeData.getPingCount());
        values.put(dbConstants.getPingPackageSizeColumnName(), accessTypeData.getPingPackageSize());
        values.put(dbConstants.getConnectCountColumnName(), accessTypeData.getConnectCount());
        db.update(dbConstants.getTableName(), values, null, null);
    }

    public void addStopOnSuccessColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Adding column " + accessTypeDataDBConstants.getStopOnSuccessColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddStopOnSuccessColumnStatement());
    }

    public void addIgnoreSSLErrorColumnToAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Adding column " + accessTypeDataDBConstants.getIgnoreSSLErrorColumnName() + " to table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getAddIgnoreSSLErrorColumnStatement());
    }

    public void dropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropTables");
        dropSchedulerIdHistoryTable(db);
        dropLogTable(db);
        dropNetworkTaskTable(db);
        dropIntervalTable(db);
        dropSchedulerStateTable(db);
        dropAccessTypeDataTable(db);
    }

    public void dropNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropTableStatement());
    }

    public void tryDropNetworkTaskTable(SQLiteDatabase db) {
        try {
            dropNetworkTaskTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropNetworkTaskTable failed ", exc);
        }
    }

    public void dropFailureCountColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping column " + networkTaskDBConstants.getFailureCountColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropFailureCountColumnStatement());
    }

    public void dropHighPrioColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping column " + networkTaskDBConstants.getHighPrioColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropHighPrioColumnStatement());
    }

    public void dropNameColumnFromNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping column " + networkTaskDBConstants.getNameColumnName() + " from table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropNameColumnStatement());
    }

    public void dropLogTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getDropTableStatement());
    }

    public void tryDropLogTable(SQLiteDatabase db) {
        try {
            dropLogTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropLogTable failed ", exc);
        }
    }

    public void dropSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getDropTableStatement());
    }

    public void tryDropSchedulerIdHistoryTable(SQLiteDatabase db) {
        try {
            dropSchedulerIdHistoryTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropSchedulerIdHistoryTable failed ", exc);
        }
    }

    public void dropIntervalTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping database table " + intervalDBConstants.getTableName());
        db.execSQL(intervalDBConstants.getDropTableStatement());
    }

    public void tryDropIntervalTable(SQLiteDatabase db) {
        try {
            dropIntervalTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropIntervalTable failed ", exc);
        }
    }

    public void dropSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping database table " + schedulerStateDBConstants.getTableName());
        db.execSQL(schedulerStateDBConstants.getDropTableStatement());
    }

    public void tryDropSchedulerStateTable(SQLiteDatabase db) {
        try {
            dropSchedulerStateTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropSchedulerStateTable failed ", exc);
        }
    }

    public void dropAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping database table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropTableStatement());
    }

    public void tryDropAccessTypeDataTable(SQLiteDatabase db) {
        try {
            dropAccessTypeDataTable(db);
        } catch (Exception exc) {
            Log.d(DBMigrate.class.getName(), "dropAccessTypeDataTable failed ", exc);
        }
    }

    public void dropStopOnSuccessColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping column " + accessTypeDataDBConstants.getStopOnSuccessColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropStopOnSuccessColumnStatement());
    }

    public void dropIgnoreSSLErrorColumnFromAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "Dropping column " + accessTypeDataDBConstants.getIgnoreSSLErrorColumnName() + " from table " + accessTypeDataDBConstants.getTableName());
        db.execSQL(accessTypeDataDBConstants.getDropIgnoreSSLErrorColumnStatement());
    }

    public void recreateNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateNetworkTaskTable");
        dropNetworkTaskTable(db);
        createNetworkTaskTable(db);
    }

    public void recreateLogTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateLogTable");
        dropLogTable(db);
        createLogTable(db);
    }

    public void recreateSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateSchedulerIdHistoryTable");
        dropSchedulerIdHistoryTable(db);
        createSchedulerIdHistoryTable(db);
    }

    public void recreateIntervalTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateIntervalTable");
        dropIntervalTable(db);
        createIntervalTable(db);
    }

    public void recreateAccessTypeDataTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateAccessTypeDataTable");
        dropAccessTypeDataTable(db);
        createAccessTypeDataTable(db);
    }

    public void recreateSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateSchedulerStateTable");
        dropSchedulerStateTable(db);
        createSchedulerStateTable(db);
    }

    public void recreateTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "recreateTables");
        dropTables(db);
        createTables(db);
    }

    public void createTables() {
        createTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void tryDropTables() {
        tryDropTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createNetworkTaskTable() {
        createNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addFailureCountColumnToNetworkTaskTable() {
        addFailureCountColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addHighPrioColumnToNetworkTaskTable() {
        addHighPrioColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addNameColumnToNetworkTaskTable() {
        addNameColumnToNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void addIgnoreSSLErrorColumnToAccessTypeDataTable() {
        addIgnoreSSLErrorColumnToAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void initializeFailureCountColumn() {
        initializeFailureCountColumn(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createLogTable() {
        createLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createSchedulerIdHistoryTable() {
        createSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createIntervalTable() {
        createIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createSchedulerStateTable() {
        createSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void createAccessTypeDataTable() {
        createAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void initializeAccessTypeDataTable() {
        initializeAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropTables() {
        dropTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropNetworkTaskTable() {
        dropNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropNetworkTaskTable() {
        tryDropNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropFailureCountColumnFromNetworkTaskTable() {
        dropFailureCountColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropHighPrioColumnFromNetworkTaskTable() {
        dropHighPrioColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void dropNameColumnFromNetworkTaskTable() {
        dropNameColumnFromNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropLogTable() {
        dropLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropLogTable() {
        tryDropLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropSchedulerIdHistoryTable() {
        dropSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropSchedulerIdHistoryTable() {
        tryDropSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropIntervalTable() {
        dropIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropIntervalTable() {
        tryDropIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropSchedulerStateTable() {
        dropSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropSchedulerStateTable() {
        tryDropSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void dropAccessTypeDataTable() {
        dropAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    @SuppressWarnings({"unused"})
    public void tryDropAccessTypeDataTable() {
        tryDropAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateNetworkTaskTable() {
        recreateNetworkTaskTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateLogTable() {
        recreateLogTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateSchedulerIdHistoryTable() {
        recreateSchedulerIdHistoryTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateIntervalTable() {
        recreateIntervalTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateSchedulerStateTable() {
        recreateSchedulerStateTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateAccessTypeDataTable() {
        recreateAccessTypeDataTable(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void recreateTables() {
        recreateTables(DBOpenHelper.getInstance(getContext()).getWritableDatabase());
    }

    public void deleteAllNetworkTasks() {
        Log.d(DBSetup.class.getName(), "deleteAllNetworkTasks");
        NetworkTaskDAO dao = new NetworkTaskDAO(getContext());
        dao.deleteAllNetworkTasks();
    }

    public void deleteAllLogs() {
        Log.d(DBSetup.class.getName(), "deleteAllLogs");
        LogDAO dao = new LogDAO(getContext());
        dao.deleteAllLogs();
    }

    public void deleteAllSchedulerIds() {
        Log.d(DBSetup.class.getName(), "deleteAllSchedulerIds");
        SchedulerIdHistoryDAO dao = new SchedulerIdHistoryDAO(getContext());
        dao.deleteAllSchedulerIds();
    }

    public void deleteAllIntervals() {
        Log.d(DBSetup.class.getName(), "deleteAllIntervals");
        IntervalDAO dao = new IntervalDAO(getContext());
        dao.deleteAllIntervals();
    }

    public void deleteSchedulerState() {
        Log.d(DBSetup.class.getName(), "deleteSchedulerState");
        SchedulerStateDAO dao = new SchedulerStateDAO(getContext());
        dao.deleteSchedulerState();
    }

    public void deleteAllAccessTypeData() {
        Log.d(DBSetup.class.getName(), "deleteAllAccessTypeData");
        AccessTypeDataDAO dao = new AccessTypeDataDAO(getContext());
        dao.deleteAllAccessTypeData();
    }

    public List<Map<String, ?>> exportNetworkTasks() {
        Log.d(DBSetup.class.getName(), "exportNetworkTasks");
        NetworkTaskDAO dao = new NetworkTaskDAO(getContext());
        List<NetworkTask> taskList = dao.readAllNetworkTasks();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (NetworkTask task : taskList) {
            exportedList.add(task.toMap());
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportLogsForNetworkTask(long networkTaskId) {
        Log.d(DBSetup.class.getName(), "exportLogsForNetworkTask, networkTaskId is " + networkTaskId);
        LogDAO dao = new LogDAO(getContext());
        List<LogEntry> logList = dao.readAllLogsForNetworkTask(networkTaskId);
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (LogEntry entry : logList) {
            exportedList.add(entry.toMap());
        }
        return exportedList;
    }

    public Map<String, ?> exportAccessTypeDataForNetworkTask(long networkTaskId) {
        Log.d(DBSetup.class.getName(), "exportAccessTypeDataForNetworkTask, networkTaskId is " + networkTaskId);
        AccessTypeDataDAO dao = new AccessTypeDataDAO(getContext());
        AccessTypeData accessTypeData = dao.readAccessTypeDataForNetworkTask(networkTaskId);
        return accessTypeData != null ? accessTypeData.toMap() : null;
    }

    public List<Map<String, ?>> exportIntervals() {
        Log.d(DBSetup.class.getName(), "exportIntervals");
        IntervalDAO dao = new IntervalDAO(getContext());
        List<Interval> intervalList = dao.readAllIntervals();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (Interval interval : intervalList) {
            exportedList.add(interval.toMap());
        }
        return exportedList;
    }

    public void importNetworkTaskWithLogsAndAccessTypeData(Map<String, ?> taskMap, List<Map<String, ?>> logList, Map<String, ?> accessTypeDataMap) {
        importNetworkTaskWithLogsAndAccessTypeData(taskMap, logList, accessTypeDataMap, true);
    }

    public void importNetworkTaskWithLogsAndAccessTypeData(Map<String, ?> taskMap, List<Map<String, ?>> logList, Map<String, ?> accessTypeDataMap, boolean resetRunnning) {
        Log.d(DBSetup.class.getName(), "importNetworkTaskWithLogsAndAccessTypeData, resetRunning is " + resetRunnning);
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(getContext());
        LogDAO logDAO = new LogDAO(getContext());
        AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(getContext());
        NetworkTaskValidator networkTaskValidator = new NetworkTaskValidator(getContext());
        NetworkTask task = new NetworkTask(taskMap);
        if (task.getAddress() != null) {
            task.setAddress(task.getAddress().trim());
        }
        Log.d(DBSetup.class.getName(), "NetworkTask is " + task);
        if (!networkTaskValidator.validate(task)) {
            Log.e(DBSetup.class.getName(), "NetworkTask is invalid and will not be imported: " + task);
            return;
        }
        if (resetRunnning) {
            task.setRunning(false);
        }
        Log.d(DBSetup.class.getName(), "Importing net work task.");
        task = networkTaskDAO.insertNetworkTask(task);
        if (task.getId() > 0 && logList != null) {
            for (Map<String, ?> logMap : logList) {
                LogEntry entry = new LogEntry(logMap);
                entry.setNetworkTaskId(task.getId());
                Log.d(DBSetup.class.getName(), "LogEntry is " + entry);
                Log.d(DBSetup.class.getName(), "Importing log entry.");
                logDAO.insertAndDeleteLog(entry);
            }
        }
        AccessTypeDataValidator accessTypeDataValidator = new AccessTypeDataValidator(getContext());
        if (task.getId() > 0) {
            AccessTypeData accessTypeData = accessTypeDataMap == null ? new AccessTypeData(getContext()) : new AccessTypeData(accessTypeDataMap);
            accessTypeData.setNetworkTaskId(task.getId());
            Log.d(DBSetup.class.getName(), "AccessTypeData is " + accessTypeData);
            if (accessTypeDataValidator.validate(accessTypeData)) {
                Log.d(DBSetup.class.getName(), "Importing accessTypeData.");
                accessTypeDataDAO.insertAccessTypeData(accessTypeData);
            } else {
                Log.e(DBSetup.class.getName(), "AccessTypeData is invalid and will not be imported: " + accessTypeData);
                Log.e(DBSetup.class.getName(), "Importing default AccessTypeData.");
                AccessTypeData defaultAccessTypeData = new AccessTypeData(getContext());
                defaultAccessTypeData.setNetworkTaskId(task.getId());
                accessTypeDataDAO.insertAccessTypeData(defaultAccessTypeData);
            }
        }
    }

    public void importIntervals(List<Map<String, ?>> intervalList) {
        Log.d(DBSetup.class.getName(), "importIntervals");
        IntervalDAO dao = new IntervalDAO(getContext());
        List<Interval> insertedList = new ArrayList<>();
        IntervalValidator validator = new IntervalValidator(getContext());
        for (Map<String, ?> intervalMap : intervalList) {
            Interval interval = new Interval(intervalMap);
            Log.d(DBSetup.class.getName(), "Interval is " + interval);
            if (validator.validate(interval, insertedList)) {
                Log.d(DBSetup.class.getName(), "Importing interval.");
                dao.insertInterval(interval);
                insertedList.add(interval);
            } else {
                Log.e(DBSetup.class.getName(), "Interval is invalid and will not be imported: " + interval);
            }
        }
    }

    private Context getContext() {
        return context;
    }
}
