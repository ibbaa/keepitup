package net.ibbaa.keepitup.db;

import android.content.Context;

import net.ibbaa.keepitup.R;

class LogDBConstants {

    private final NetworkTaskDBConstants networkTaskDBConstants;
    private final String tableName;
    private final String idColumnName;
    private final String networkTaskIdColumnName;
    private final String timestampColumnName;
    private final String successColumnName;
    private final String messageColumnName;

    public LogDBConstants(Context context) {
        networkTaskDBConstants = new NetworkTaskDBConstants(context);
        tableName = context.getResources().getString(R.string.log_table_name);
        idColumnName = context.getResources().getString(R.string.log_id_column_name);
        networkTaskIdColumnName = context.getResources().getString(R.string.log_taskid_column_name);
        timestampColumnName = context.getResources().getString(R.string.log_timestamp_column_name);
        successColumnName = context.getResources().getString(R.string.log_success_column_name);
        messageColumnName = context.getResources().getString(R.string.log_message_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getNetworkTaskIdColumnName() {
        return networkTaskIdColumnName;
    }

    public String getTimestampColumnName() {
        return timestampColumnName;
    }

    public String getSuccessColumnName() {
        return successColumnName;
    }

    public String getMessageColumnName() {
        return messageColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS  " + getTableName() + "(") +
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

    public String getDeleteOrphanLogsStatement() {
        return "DELETE FROM " + getTableName() + " WHERE " + getNetworkTaskIdColumnName() + " NOT IN (SELECT " + networkTaskDBConstants.getIdColumnName() + " FROM " + networkTaskDBConstants.getTableName() + ");";
    }
}
