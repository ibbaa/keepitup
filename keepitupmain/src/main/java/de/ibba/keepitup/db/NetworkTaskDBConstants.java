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

    public String getSuccessColumnName() {
        return getResources().getString(R.string.task_success_column_name);
    }

    public String getTimestampColumnName() {
        return getResources().getString(R.string.task_timestamp_column_name);
    }

    public String getMessageColumnName() {
        return getResources().getString(R.string.task_message_column_name);
    }

    public String getNotificationColumnName() {
        return getResources().getString(R.string.task_notification_column_name);
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getIndexColumnName() + " INTEGER, " +
                getAddressColumnName() + " TEXT, " +
                getPortColumnName() + " INTEGER, " +
                getAccessTypeColumnName() + " INTEGER, " +
                getIntervalColumnName() + " INTEGER, " +
                getSuccessColumnName() + " INTEGER, " +
                getTimestampColumnName() + " INTEGER, " +
                getMessageColumnName() + " TEXT, " +
                getNotificationColumnName() + " INTEGER);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getAddressColumnName() + ", " +
                getPortColumnName() + ", " +
                getAccessTypeColumnName() + ", " +
                getIntervalColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getMessageColumnName() + ", " +
                getNotificationColumnName() +
                " FROM " + getTableName() +
                "  WHERE " + getIdColumnName() + " = ?;";
    }


    public String getReadAllNetworkTasksStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getIndexColumnName() + ", " +
                getAddressColumnName() + ", " +
                getPortColumnName() + ", " +
                getAccessTypeColumnName() + ", " +
                getIntervalColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getMessageColumnName() + ", " +
                getNotificationColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getIndexColumnName() + " ASC";
    }

    public String getReadMaximumIndexStatement() {
        return "SELECT MAX(" + getIndexColumnName() + ") FROM " + getTableName();
    }

    private Resources getResources() {
        return context.getResources();
    }
}
