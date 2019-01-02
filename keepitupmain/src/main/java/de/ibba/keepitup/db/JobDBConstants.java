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

    public String getJobIndexColumnName() {
        return getResources().getString(R.string.job_index_column_name);
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

    public String getJobSuccessColumnName() {
        return getResources().getString(R.string.job_success_column_name);
    }

    public String getJobMessageColumnName() {
        return getResources().getString(R.string.job_message_column_name);
    }

    public String getJobNotificationColumnName() {
        return getResources().getString(R.string.job_notification_column_name);
    }

    public String getCreateJobTableStatement() {
        return ("CREATE TABLE " + getJobTableName() + "(") +
                getJobIdColumnName() + " INTEGER PRIMARY KEY ASC, " +
                getJobIndexColumnName() + " INTEGER, " +
                getJobAddressColumnName() + " TEXT, " +
                getJobAccessTypeColumnName() + " INTEGER, " +
                getJobIntervalColumnName() + " INTEGER, " +
                getJobSuccessColumnName() + " INTEGER, " +
                getJobMessageColumnName() + " TEXT, " +
                getJobNotificationColumnName() + " INTEGER);";
    }

    public String getDropJobTableStatement() {
        return "DROP TABLE IF EXISTS " + getJobTableName();
    }

    public String getAllJobsStatement() {
        return "SELECT " +
                getJobIdColumnName() + ", " +
                getJobIndexColumnName() + ", " +
                getJobAddressColumnName() + ", " +
                getJobAccessTypeColumnName() + ", " +
                getJobIntervalColumnName() + ", " +
                getJobSuccessColumnName() + ", " +
                getJobMessageColumnName() + ", " +
                getJobNotificationColumnName() + ", " +
                " FROM " + getJobTableName() +
                " ORDER BY " + getJobIndexColumnName() + " ASC";
    }

    public String getMaximumIndexStatement() {
        return "SELECT MAX(" + getJobIndexColumnName() + ") FROM " + getJobTableName();
    }

    private Resources getResources() {
        return context.getResources();
    }
}
