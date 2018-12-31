package de.ibba.keepitup.db;

import android.content.Context;
import android.content.res.Resources;

import de.ibba.keepitup.R;

class JobDBConstants {

    private final Context context;

    public JobDBConstants(Context context) {
        this.context = context;
    }

    public String getJobTableName() {
        return getResources().getString(R.string.job_table_name);
    }

    public String getJobIdColumnName() {
        return getResources().getString(R.string.job_id_column_name);
    }

    public String getJobAddressColumnName() {
        return getResources().getString(R.string.job_address_column_name);
    }

    public String getJobAccessTypeColumnName() {
        return getResources().getString(R.string.job_accesstype_column_name);
    }

    public String getJobIntervalColumnName() {
        return getResources().getString(R.string.job_interval_column_name);
    }

    public String getJobRunningColumnName() {
        return getResources().getString(R.string.job_running_column_name);
    }

    public String getCreateJobTableStatement() {
        return ("CREATE TABLE " + getJobTableName() + "(") +
                getJobIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getJobAddressColumnName() + " TEXT, " +
                getJobAccessTypeColumnName() + " INTEGER, " +
                getJobIntervalColumnName() + " INTEGER, " +
                getJobRunningColumnName() + " INTEGER);";
    }

    public String getDropJobTableStatement() {
        return "DROP TABLE IF EXISTS " + getJobTableName();
    }

    public String getAllJobsStatement() {
        return "SELECT " +
                getJobIdColumnName() + ", " +
                getJobAddressColumnName() + ", " +
                getJobAccessTypeColumnName() + ", " +
                getJobIntervalColumnName() + ", " +
                getJobRunningColumnName() +
                " FROM " + getJobTableName() +
                " ORDER BY " + getJobIdColumnName() + " ASC";
    }

    private Resources getResources() {
        return context.getResources();
    }
}
