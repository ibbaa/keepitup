package de.ibba.keepitup.db;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;

public class SchedulerIdHistoryDBConstants {

    private final Context context;

    public SchedulerIdHistoryDBConstants(Context context) {
        this.context = context;
    }

    public String getTableName() {
        return getResources().getString(R.string.schedulerid_table_name);
    }

    public String getIdColumnName() {
        return getResources().getString(R.string.schedulerid_history_id_column_name);
    }

    public String getSchedulerIdColumnName() {
        return getResources().getString(R.string.schedulerid_history_schedulerid_column_name);
    }

    public String getTimestampColumnName() {
        return getResources().getString(R.string.schedulerid_history_timestamp_column_name);
    }

    private Resources getResources() {
        return context.getResources();
    }

    public String getCreateTableStatement() {
        return ("CREATE TABLE " + getTableName() + "(") +
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
