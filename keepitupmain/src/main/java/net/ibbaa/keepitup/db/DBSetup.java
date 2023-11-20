/*
 * Copyright (c) 2023. Alwin Ibba
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.validation.IntervalValidator;
import net.ibbaa.keepitup.model.validation.NetworkTaskValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBSetup {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final LogDBConstants logDBConstants;
    private final SchedulerIdHistoryDBConstants schedulerIdDBConstants;
    private final IntervalDBConstants intervalDBConstants;
    private final SchedulerStateDBConstants schedulerStateDBConstants;

    public DBSetup(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        logDBConstants = new LogDBConstants(context);
        schedulerIdDBConstants = new SchedulerIdHistoryDBConstants(context);
        intervalDBConstants = new IntervalDBConstants(context);
        schedulerStateDBConstants = new SchedulerStateDBConstants(context);
    }

    public void createTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "createTables");
        createNetworkTaskTable(db);
        createLogTable(db);
        createSchedulerIdHistoryTable(db);
        createIntervalTable(db);
        createSchedulerStateTable(db);
    }

    public void createNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Creating database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getCreateTableStatement());
    }

    public void createLogTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Creating database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getCreateTableStatement());
    }

    public void createSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Creating database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getCreateTableStatement());
    }

    public void createIntervalTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Creating database table " + intervalDBConstants.getTableName());
        db.execSQL(intervalDBConstants.getCreateTableStatement());
    }

    public void createSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Creating database table " + schedulerStateDBConstants.getTableName());
        db.execSQL(schedulerStateDBConstants.getCreateTableStatement());
        initializeSchedulerStateTable(db);
    }

    public void initializeSchedulerStateTable(SQLiteDatabase db) {
        db.execSQL(schedulerStateDBConstants.getInitializeSchedulerStateStatement());
    }

    public void dropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropTables");
        dropSchedulerIdHistoryTable(db);
        dropLogTable(db);
        dropNetworkTaskTable(db);
        dropIntervalTable(db);
        dropSchedulerStateTable(db);
    }

    public void dropNetworkTaskTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Dropping database table " + networkTaskDBConstants.getTableName());
        db.execSQL(networkTaskDBConstants.getDropTableStatement());
    }

    public void dropLogTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Dropping database table " + logDBConstants.getTableName());
        db.execSQL(logDBConstants.getDropTableStatement());
    }

    public void dropSchedulerIdHistoryTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Dropping database table " + schedulerIdDBConstants.getTableName());
        db.execSQL(schedulerIdDBConstants.getDropTableStatement());
    }

    public void dropIntervalTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Dropping database table " + intervalDBConstants.getTableName());
        db.execSQL(intervalDBConstants.getDropTableStatement());
    }

    public void dropSchedulerStateTable(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "Dropping database table " + schedulerStateDBConstants.getTableName());
        db.execSQL(schedulerStateDBConstants.getDropTableStatement());
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

    public void createIntervalTable(Context context) {
        createIntervalTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void createSchedulerStateTable(Context context) {
        createSchedulerStateTable(DBOpenHelper.getInstance(context).getWritableDatabase());
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

    public void dropIntervalTable(Context context) {
        dropIntervalTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void dropSchedulerStateTable(Context context) {
        dropSchedulerStateTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateNetworkTaskTable(Context context) {
        recreateNetworkTaskTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateLogTable(Context context) {
        recreateLogTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateSchedulerIdHistoryTable(Context context) {
        recreateSchedulerIdHistoryTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateIntervalTable(Context context) {
        recreateIntervalTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateSchedulerStateTable(Context context) {
        recreateSchedulerStateTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateTables(Context context) {
        recreateTables(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void deleteAllNetworkTasks(Context context) {
        Log.d(DBSetup.class.getName(), "deleteAllNetworkTasks");
        NetworkTaskDAO dao = new NetworkTaskDAO(context);
        dao.deleteAllNetworkTasks();
    }

    public void deleteAllLogs(Context context) {
        Log.d(DBSetup.class.getName(), "deleteAllLogs");
        LogDAO dao = new LogDAO(context);
        dao.deleteAllLogs();
    }

    public void deleteAllSchedulerIds(Context context) {
        Log.d(DBSetup.class.getName(), "deleteAllSchedulerIds");
        SchedulerIdHistoryDAO dao = new SchedulerIdHistoryDAO(context);
        dao.deleteAllSchedulerIds();
    }

    public void deleteAllIntervals(Context context) {
        Log.d(DBSetup.class.getName(), "deleteAllIntervals");
        IntervalDAO dao = new IntervalDAO(context);
        dao.deleteAllIntervals();
    }

    public void deleteSchedulerState(Context context) {
        Log.d(DBSetup.class.getName(), "deleteSchedulerState");
        SchedulerStateDAO dao = new SchedulerStateDAO(context);
        dao.deleteSchedulerState();
    }

    public List<Map<String, ?>> exportNetworkTasks(Context context) {
        Log.d(DBSetup.class.getName(), "exportNetworkTasks");
        NetworkTaskDAO dao = new NetworkTaskDAO(context);
        List<NetworkTask> taskList = dao.readAllNetworkTasks();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (NetworkTask task : taskList) {
            exportedList.add(task.toMap());
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportLogsForNetworkTask(Context context, long networkTaskId) {
        Log.d(DBSetup.class.getName(), "exportLogsForNetworkTask, networkTaskId is " + networkTaskId);
        LogDAO dao = new LogDAO(context);
        List<LogEntry> logList = dao.readAllLogsForNetworkTask(networkTaskId);
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (LogEntry entry : logList) {
            exportedList.add(entry.toMap());
        }
        return exportedList;
    }

    public List<Map<String, ?>> exportIntervals(Context context) {
        Log.d(DBSetup.class.getName(), "exportIntervals");
        IntervalDAO dao = new IntervalDAO(context);
        List<Interval> intervalList = dao.readAllIntervals();
        List<Map<String, ?>> exportedList = new ArrayList<>();
        for (Interval interval : intervalList) {
            exportedList.add(interval.toMap());
        }
        return exportedList;
    }

    public void importNetworkTaskWithLogs(Context context, Map<String, ?> taskMap, List<Map<String, ?>> logList) {
        importNetworkTaskWithLogs(context, taskMap, logList, true);
    }

    public void importNetworkTaskWithLogs(Context context, Map<String, ?> taskMap, List<Map<String, ?>> logList, boolean resetRunnning) {
        Log.d(DBSetup.class.getName(), "importNetworkTaskWithLogs, resetRunnning is " + resetRunnning);
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
        LogDAO logDAO = new LogDAO(context);
        NetworkTaskValidator validator = new NetworkTaskValidator(context);
        NetworkTask task = new NetworkTask(taskMap);
        Log.d(DBSetup.class.getName(), "NetworkTask is " + task);
        if (!validator.validate(task)) {
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
    }

    public void importIntervals(Context context, List<Map<String, ?>> intervalList) {
        Log.d(DBSetup.class.getName(), "importIntervals");
        IntervalDAO dao = new IntervalDAO(context);
        List<Interval> insertedList = new ArrayList<>();
        IntervalValidator validator = new IntervalValidator(context);
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
}
