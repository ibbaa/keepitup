package de.ibba.keepitup.db;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;

class LogDBConstants {

    private final Context context;
    private final NetworkTaskDBConstants networkTaskDBConstants;

    public LogDBConstants(Context context) {
        this.context = context;
        this.networkTaskDBConstants = new NetworkTaskDBConstants(context);
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
                getNetworkTaskIdColumnName() + " INTEGER, " +
                getTimestampColumnName() + " INTEGER, " +
                getSuccessColumnName() + " INTEGER, " +
                getMessageColumnName() + " TEXT, " +
                "FOREIGN KEY(" + getNetworkTaskIdColumnName() + ") REFERENCES " +
                networkTaskDBConstants.getTableName() + "(" + networkTaskDBConstants.getIdColumnName() + "));";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getAllLogsStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getMessageColumnName() + ", " +
                " FROM " + getTableName() +
                " ORDER BY " + getTimestampColumnName() + " DESC";
    }

    public String getMostRecentLogStatement() {
        return "SELECT MAX(" + getTimestampColumnName() + ")" +
                getIdColumnName() + ", " +
                getNetworkTaskIdColumnName() + ", " +
                getTimestampColumnName() + ", " +
                getSuccessColumnName() + ", " +
                getMessageColumnName() + ", " +
                " FROM " + getTableName();
    }

    public String getOldestLogStatement() {
        return "SELECT MIN(" + getTimestampColumnName() + ")," + getIdColumnName() + " FROM " + getTableName();
    }

    public String getLogCountStatement() {
        return "SELECT COUNT(*) FROM " + getTableName();
    }

    private Resources getResources() {
        return context.getResources();
    }
}
