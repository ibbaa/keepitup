package de.ibba.keepitup.db;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;

class LogDBConstants {

    private final Context context;

    public LogDBConstants(Context context) {
        this.context = context;
    }

    public String getTableName() {
        return getResources().getString(R.string.log_table_name);
    }

    public String getIdColumnName() {
        return getResources().getString(R.string.log_id_column_name);
    }

    public String getNetworkTaskIdColumnName() {
        return getResources().getString(R.string.log_taskid_column_name);
    }

    public String getTimestampColumnName() {
        return getResources().getString(R.string.log_timestamp_column_name);
    }

    public String getSuccessColumnName() {
        return getResources().getString(R.string.log_success_column_name);
    }

    public String getMessageColumnName() {
        return getResources().getString(R.string.log_message_column_name);
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getNetworkTaskIdColumnName() + " INTEGER NOT NULL, " +
                getTimestampColumnName() + " INTEGER NOT NULL, " +
                getSuccessColumnName() + " INTEGER NOT NULL, " +
                getMessageColumnName() + " TEXT);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadAllLogsForNetworkTaskStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getMessageColumnName() +
                " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?" +
                " ORDER BY " + getTimestampColumnName() + " DESC";
    }

    public String getReadAllLogsStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getMessageColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getTimestampColumnName() + " DESC";
    }

    public String getReadMostRecentLogStatement() {
        return "SELECT MAX(" + getTimestampColumnName() + "), " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getMessageColumnName() +
                " FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " = ?";
    }

    public String getReadOldestLogStatement() {
        return "SELECT MIN(" + getTimestampColumnName() + ")," + getIdColumnName() + " FROM " + getTableName() +
                " WHERE " + getNetworkTaskIdColumnName() + " = ?";
    }

    public String getLogCountStatement() {
        return "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " = ?";
    }

    private Resources getResources() {
        return context.getResources();
    }
}
