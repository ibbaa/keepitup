package net.ibbaa.keepitup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.util.URLUtil;

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

    public void dropTables(SQLiteDatabase db) {
        Log.d(DBSetup.class.getName(), "dropTables");
        dropSchedulerIdHistoryTable(db);
        dropLogTable(db);
        dropNetworkTaskTable(db);
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

    public void recreateNetworkTaskTable(Context context) {
        recreateNetworkTaskTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateLogTable(Context context) {
        recreateLogTable(DBOpenHelper.getInstance(context).getWritableDatabase());
    }

    public void recreateSchedulerIdHistoryTable(Context context) {
        recreateSchedulerIdHistoryTable(DBOpenHelper.getInstance(context).getWritableDatabase());
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

    public void importNetworkTaskWithLogs(Context context, Map<String, ?> taskMap, List<Map<String, ?>> logList) {
        importNetworkTaskWithLogs(context, taskMap, logList, true);
    }

    public void importNetworkTaskWithLogs(Context context, Map<String, ?> taskMap, List<Map<String, ?>> logList, boolean resetRunnning) {
        Log.d(DBSetup.class.getName(), "importNetworkTaskWithLogs, resetRunnning is " + resetRunnning);
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
        LogDAO logDAO = new LogDAO(context);
        NetworkTask task = new NetworkTask(taskMap);
        Log.d(DBSetup.class.getName(), "NetworkTask is " + task);
        if (!isNetworkTaskValid(context, task)) {
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

    private boolean isNetworkTaskValid(Context context, NetworkTask task) {
        Log.d(DBSetup.class.getName(), "isNetworkTaskValid");
        if (task.getAccessType() == null) {
            return false;
        }
        int portMin = context.getResources().getInteger(R.integer.task_port_minimum);
        int portMax = context.getResources().getInteger(R.integer.task_port_maximum);
        if (task.getPort() < portMin || task.getPort() > portMax) {
            return false;
        }
        int intervalMin = context.getResources().getInteger(R.integer.task_interval_minimum);
        int intervalMax = context.getResources().getInteger(R.integer.task_interval_maximum);
        if (task.getInterval() < intervalMin || task.getInterval() > intervalMax) {
            return false;
        }
        String address = task.getAddress();
        if (address == null) {
            return false;
        }
        return URLUtil.isValidIPAddress(address) || URLUtil.isValidHostName(address) || URLUtil.isValidURL(address);
    }
}
