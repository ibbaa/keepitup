package de.ibba.keepitup.ui.sync;

import android.app.Activity;
import android.content.Context;

import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.DBSetup;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.DBPurgeSupport;

public class DBPurgeTask extends UIBackgroundTask<Boolean> {

    public DBPurgeTask(Activity activity) {
        super(activity);
    }

    @Override
    protected Boolean runInBackground() {
        Log.d(DBPurgeTask.class.getName(), "runInBackground");
        try {
            Context context = getActivity();
            if (context != null) {
                DBSetup setup = new DBSetup(context);
                int dropTableRetry = context.getResources().getInteger(R.integer.drop_table_retry_count);
                int dropTableTimeout = context.getResources().getInteger(R.integer.drop_table_timeout);
                while (dropTableRetry > 0) {
                    boolean dropSuccess = purgeTables(context, setup::recreateLogTable, setup::recreateNetworkTaskTable, setup::recreateSchedulerIdHistoryTable);
                    if (!dropSuccess) {
                        TimeUnit.MILLISECONDS.sleep(dropTableTimeout);
                    } else {
                        return true;
                    }
                    dropTableRetry--;
                }
                Log.d(DBPurgeTask.class.getName(), "Dropping the tables was not successful");
                int deleteTableRetry = context.getResources().getInteger(R.integer.delete_table_retry_count);
                int deleteTableTimeout = context.getResources().getInteger(R.integer.delete_table_timeout);
                while (deleteTableRetry > 0) {
                    boolean deleteSuccess = purgeTables(context, setup::deleteAllLogs, setup::deleteAllNetworkTasks, setup::deleteAllSchedulerIds);
                    if (!deleteSuccess) {
                        TimeUnit.MILLISECONDS.sleep(deleteTableTimeout);
                    } else {
                        return true;
                    }
                    deleteTableRetry--;
                }
                Log.d(DBPurgeTask.class.getName(), "Deleting the tables was not successful");
            }
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging database", exc);
        }
        return false;
    }

    private boolean purgeTables(Context context, PurgeOperation logOperation, PurgeOperation networkOperation, PurgeOperation schedulerIdOperation) {
        Log.d(DBPurgeTask.class.getName(), "purgeTables");
        boolean logTableSuccess = false;
        boolean networkTaskTableSuccess = false;
        boolean schedulerIdTableSuccess = false;
        try {
            logOperation.doPurge(context);
            logTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging log table", exc);
        }
        try {
            networkOperation.doPurge(context);
            networkTaskTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging network task table", exc);
        }
        try {
            schedulerIdOperation.doPurge(context);
            schedulerIdTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging scheduler id table", exc);
        }
        Log.d(DBPurgeTask.class.getName(), "logTableSuccess: " + logTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "networkTaskTableSuccess: " + networkTaskTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "schedulerIdTableSuccess: " + schedulerIdTableSuccess);
        return logTableSuccess && networkTaskTableSuccess && schedulerIdTableSuccess;
    }

    @Override
    protected void runOnUIThread(Boolean success) {
        Log.d(DBPurgeTask.class.getName(), "runOnUIThread, success is " + success);
        if (success == null) {
            success = false;
        }
        Context context = getActivity();
        if (context != null) {
            if (context instanceof DBPurgeSupport) {
                ((DBPurgeSupport) context).onPurgeDone(success);
            }
        }
    }

    @FunctionalInterface
    private interface PurgeOperation {
        void doPurge(Context context);
    }
}
