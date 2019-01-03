package de.ibba.keepitup.db;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;

class TaskDBConstants {

    private final Context context;

    public TaskDBConstants(Context context) {
        this.context = context;
    }

    public String getTaskTableName() {
        return getResources().getString(R.string.task_table_name);
    }

    public String getTaskIdColumnName() {
        return getResources().getString(R.string.task_id_column_name);
    }

    public String getTaskIndexColumnName() {
        return getResources().getString(R.string.task_index_column_name);
    }

    public String getTaskAddressColumnName() {
        return getResources().getString(R.string.task_address_column_name);
    }

    public String getTaskAccessTypeColumnName() {
        return getResources().getString(R.string.task_accesstype_column_name);
    }

    public String getTaskIntervalColumnName() {
        return getResources().getString(R.string.task_interval_column_name);
    }

    public String getTaskSuccessColumnName() {
        return getResources().getString(R.string.task_success_column_name);
    }

    public String getTaskMessageColumnName() {
        return getResources().getString(R.string.task_message_column_name);
    }

    public String getTaskNotificationColumnName() {
        return getResources().getString(R.string.task_notification_column_name);
    }

    public String getCreateTaskTableStatement() {
        return ("CREATE TABLE " + getTaskTableName() + "(") +
                getTaskIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getTaskIndexColumnName() + " INTEGER, " +
                getTaskAddressColumnName() + " TEXT, " +
                getTaskAccessTypeColumnName() + " INTEGER, " +
                getTaskIntervalColumnName() + " INTEGER, " +
                getTaskSuccessColumnName() + " INTEGER, " +
                getTaskMessageColumnName() + " TEXT, " +
                getTaskNotificationColumnName() + " INTEGER);";
    }

    public String getDropTaskTableStatement() {
        return "DROP TABLE IF EXISTS " + getTaskTableName();
    }

    public String getAllTasksStatement() {
        return "SELECT " +
                getTaskIdColumnName() + ", " +
                getTaskIndexColumnName() + ", " +
                getTaskAddressColumnName() + ", " +
                getTaskAccessTypeColumnName() + ", " +
                getTaskIntervalColumnName() + ", " +
                getTaskSuccessColumnName() + ", " +
                getTaskMessageColumnName() + ", " +
                getTaskNotificationColumnName() + ", " +
                " FROM " + getTaskTableName() +
                " ORDER BY " + getTaskIndexColumnName() + " ASC";
    }

    public String getMaximumIndexStatement() {
        return "SELECT MAX(" + getTaskIndexColumnName() + ") FROM " + getTaskTableName();
    }

    private Resources getResources() {
        return context.getResources();
    }
}