/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui.sync;

import android.app.Activity;
import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.DBPurgeSupport;

import java.util.concurrent.TimeUnit;

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
                    boolean dropSuccess = purgeTables(setup::recreateLogTable, setup::recreateNetworkTaskTable, setup::recreateSchedulerIdHistoryTable, setup::recreateIntervalTable, setup::recreateSchedulerStateTable);
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
                    boolean deleteSuccess = purgeTables(setup::deleteAllLogs, setup::deleteAllNetworkTasks, setup::deleteAllSchedulerIds, setup::deleteAllIntervals, setup::recreateSchedulerStateTable);
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

    private boolean purgeTables(PurgeOperation logOperation, PurgeOperation networkOperation, PurgeOperation schedulerIdOperation, PurgeOperation intervalOperation, PurgeOperation schedulerStateOperation) {
        Log.d(DBPurgeTask.class.getName(), "purgeTables");
        boolean logTableSuccess = false;
        boolean networkTaskTableSuccess = false;
        boolean schedulerIdTableSuccess = false;
        boolean intervalTableSuccess = false;
        boolean schedulerStateTableSuccess = false;
        try {
            logOperation.doPurge();
            logTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging log table", exc);
        }
        try {
            networkOperation.doPurge();
            networkTaskTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging network task table", exc);
        }
        try {
            schedulerIdOperation.doPurge();
            schedulerIdTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging scheduler id table", exc);
        }
        try {
            intervalOperation.doPurge();
            intervalTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging interval table", exc);
        }
        try {
            schedulerStateOperation.doPurge();
            schedulerStateTableSuccess = true;
        } catch (Exception exc) {
            Log.e(DBPurgeTask.class.getName(), "Error purging scheduler state table", exc);
        }
        Log.d(DBPurgeTask.class.getName(), "logTableSuccess: " + logTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "networkTaskTableSuccess: " + networkTaskTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "schedulerIdTableSuccess: " + schedulerIdTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "intervalTableSuccess: " + intervalTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "schedulerStateTableSuccess: " + schedulerStateTableSuccess);
        return logTableSuccess && networkTaskTableSuccess && schedulerIdTableSuccess && intervalTableSuccess && schedulerStateTableSuccess;
    }

    @Override
    protected void runOnUIThread(Boolean success) {
        Log.d(DBPurgeTask.class.getName(), "runOnUIThread, success is " + success);
        if (success == null) {
            success = false;
        }
        Activity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            if (activity instanceof DBPurgeSupport) {
                ((DBPurgeSupport) activity).onPurgeDone(success);
            }
        }
    }

    @FunctionalInterface
    private interface PurgeOperation {
        @SuppressWarnings({"unused"})
        void doPurge();
    }
}
