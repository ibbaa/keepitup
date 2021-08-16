package net.ibbaa.keepitup.db;

import android.content.Context;

import net.ibbaa.keepitup.R;

public class SchedulerIdHistoryDBConstants {

    private final String tableName;
    private final String idColumnName;
    private final String schedulerIdColumnName;
    private final String timestampColumnName;

    public SchedulerIdHistoryDBConstants(Context context) {
        tableName = context.getResources().getString(R.string.schedulerid_table_name);
        idColumnName = context.getResources().getString(R.string.schedulerid_history_id_column_name);
        schedulerIdColumnName = context.getResources().getString(R.string.schedulerid_history_schedulerid_column_name);
        timestampColumnName = context.getResources().getString(R.string.schedulerid_history_timestamp_column_name);
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getSchedulerIdColumnName() {
        return schedulerIdColumnName;
    }

    public String getTimestampColumnName() {
        return timestampColumnName;
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE IF NOT EXISTS " + getTableName() + "(") +
                getIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getSchedulerIdColumnName() + " INTEGER NOT NULL, " +
                getTimestampColumnName() + " INTEGER NOT NULL);";
    }

    public String getDropTableStatement() {
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    public String getReadAllSchedulerIdHistoryEntriesStatement() {
        return "SELECT " +
                getIdColumnName() + ", " +
                getSchedulerIdColumnName() + ", " +
                getTimestampColumnName() +
                " FROM " + getTableName() +
                " ORDER BY " + getTimestampColumnName() + " DESC";
    }

    public String getReadOldestSchedulerIdHistoryEntryStatement() {
        return "SELECT MIN(" + getTimestampColumnName() + ")," + getIdColumnName() + " FROM " + getTableName();
    }

    public String getSchedulerIdHistoryCount() {
        return "SELECT COUNT(*) FROM " + getTableName();
    }

    public String getSchedulerIdHistoryCountForSchedulerIdStatement() {
        return "SELECT COUNT(*) FROM " + getTableName() + " WHERE " + getSchedulerIdColumnName() + " = ?";
    }
}
