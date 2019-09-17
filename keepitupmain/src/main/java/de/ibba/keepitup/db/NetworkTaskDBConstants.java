package de.ibba.keepitup.db;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;

class NetworkTaskDBConstants {

    private final Context context;

    public NetworkTaskDBConstants(Context context) {
        this.context = context;
    }

    public String getTableName() {
        return getResources().getString(R.string.task_table_name);
    }

    public String getIdColumnName() {
        return getResources().getString(R.string.task_id_column_name);
    }

    public String getIndexColumnName() {
        return getResources().getString(R.string.task_index_column_name);
    }

    public String getSchedulerIdColumnName() {
        return getResources().getString(R.string.task_schedulerid_column_name);
    }

    public String getInstancesColumnName() {
        return getResources().getString(R.string.task_instances_column_name);
    }

    public String getAddressColumnName() {
        return getResources().getString(R.string.task_address_column_name);
    }

    public String getPortColumnName() {
        return getResources().getString(R.string.task_port_column_name);
    }

    public String getAccessTypeColumnName() {
        return getResources().getString(R.string.task_accesstype_column_name);
    }

    public String getIntervalColumnName() {
        return getResources().getString(R.string.task_interval_column_name);
    }

    public String getOnlyWifiColumnName() {
        return getResources().getString(R.string.task_onlywifi_column_name);
    }

    public String getNotificationColumnName() {
        return getResources().getString(R.string.task_notification_column_name);
    }

    public String getRunningColumnName() {
        return getResources().getString(R.string.task_running_column_name);
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getIndexColumnName() + " INTEGER NOT NULL, " +
                getSchedulerIdColumnName() + " INTEGER, " +
                getInstancesColumnName() + " INTEGER, " +
                getAddressColumnName() + " TEXT, " +
                getPortColumnName() + " INTEGER, " +
                getAccessTypeColumnName() + " INTEGER, " +
                getIntervalColumnName() + " INTEGER, " +
                getOnlyWifiColumnName() + " TEXT, " +
                getNotificationColumnName() + " INTEGER, " +
                getRunningColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getSchedulerIdColumnName() + ", " +
                getInstancesColumnName() + ", " +
                getAddressColumnName() + ", " +
                getPortColumnName() + ", " +
                getAccessTypeColumnName() + ", " +
                getIntervalColumnName() + ", " +
                getOnlyWifiColumnName() + ", " +
                getNotificationColumnName() + ", " +
                getRunningColumnName() +
                " FROM " + getTableName() +
                "  WHERE " + getIdColumnName() + " = ?;";
    }


    public String getReadAllNetworkTasksStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getSchedulerIdColumnName() + ", " +
                getInstancesColumnName() + ", " +
                getAddressColumnName() + ", " +
                getPortColumnName() + ", " +
                getAccessTypeColumnName() + ", " +
                getIntervalColumnName() + ", " +
                getOnlyWifiColumnName() + ", " +
                getNotificationColumnName() + ", " +
                getRunningColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getIndexColumnName() + " ASC";
    }

    public String getSchedulerIdCountStatement() {
        return "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getSchedulerIdColumnName() + " = ?";
    }

    public String getReadInstancesStatement() {
        return "SELECT INSTANCES FROM " + getTableName() + " WHERE " + getSchedulerIdColumnName() + " = ?";
    }

    public String getUpdateIndexNetworkTasksStatement() {
        return "UPDATE " + getTableName() + " SET " + getIndexColumnName() + " = " + getIndexColumnName() + " - 1 WHERE " + getIndexColumnName() + " > ?;";
    }

    private Resources getResources() {
        return context.getResources();
    }
}
