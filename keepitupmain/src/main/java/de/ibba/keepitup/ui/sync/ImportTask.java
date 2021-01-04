package de.ibba.keepitup.ui.sync;

import android.app.Activity;
import android.content.Context;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.DBSetup;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.JSONSystemSetup;
import de.ibba.keepitup.resources.PreferenceSetup;
import de.ibba.keepitup.resources.SystemSetupResult;
import de.ibba.keepitup.ui.ImportSupport;
import de.ibba.keepitup.util.StreamUtil;

public class ImportTask extends UIBackgroundTask<Boolean> {

    private final File importFolder;
    private final String file;

    public ImportTask(Activity activity, File importFolder, String file) {
        super(activity);
        this.importFolder = importFolder;
        this.file = file;
    }

    @Override
    protected Boolean runInBackground() {
        Log.d(ImportTask.class.getName(), "runInBackground");
        try {
            Context context = getActivity();
            if (context != null) {
                if (!doPurge(context)) {
                    return false;
                }
                return doImport(context);
            }
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error exporting database", exc);
        }
        return false;
    }

    private boolean doPurge(Context context) throws Exception {
        Log.d(ImportTask.class.getName(), "doPurge");
        int deleteTableRetry = context.getResources().getInteger(R.integer.delete_table_retry_count);
        int deleteTableTimeout = context.getResources().getInteger(R.integer.delete_table_timeout);
        while (deleteTableRetry > 0) {
            boolean deleteSuccess = purgeTables(context);
            if (!deleteSuccess) {
                TimeUnit.MILLISECONDS.sleep(deleteTableTimeout);
            } else {
                PreferenceSetup setup = new PreferenceSetup(context);
                setup.removeAllSettings();
                return true;
            }
            deleteTableRetry--;
        }
        return false;
    }

    private boolean purgeTables(Context context) {
        Log.d(ImportTask.class.getName(), "purgeTables");
        DBSetup setup = new DBSetup(context);
        boolean logTableSuccess = false;
        boolean networkTaskTableSuccess = false;
        try {
            setup.deleteAllLogs(context);
            logTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging log table", exc);
        }
        try {
            setup.deleteAllNetworkTasks(context);
            networkTaskTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging network task table", exc);
        }
        Log.d(ImportTask.class.getName(), "logTableSuccess: " + logTableSuccess);
        Log.d(ImportTask.class.getName(), "networkTaskTableSuccess: " + networkTaskTableSuccess);
        return logTableSuccess && networkTaskTableSuccess;
    }

    private boolean doImport(Context context) throws Exception {
        Log.d(ImportTask.class.getName(), "doImport");
        FileInputStream stream = null;
        try {
            JSONSystemSetup setup = new JSONSystemSetup(context);
            File importFile = new File(importFolder, file);
            stream = new FileInputStream(importFile);
            String data = StreamUtil.inputStreamToString(stream, Charsets.UTF_8);
            SystemSetupResult result = setup.importData(data);
            Log.d(ImportTask.class.getName(), "Import returned " + result);
            if (result.isSuccess()) {
                Log.d(ImportTask.class.getName(), "Import was successful: " + result.getMessage());
                return true;
            } else {
                Log.d(ImportTask.class.getName(), "Import was not successful: " + result.getMessage());
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception exc) {
                    Log.e(ImportTask.class.getName(), "Error closing file", exc);
                }
            }
        }
        return false;
    }

    @Override
    protected void runOnUIThread(Boolean success) {
        Log.d(ImportTask.class.getName(), "runOnUIThread, success is " + success);
        if (success == null) {
            success = false;
        }
        Activity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            if (activity instanceof ImportSupport) {
                ((ImportSupport) activity).onImportDone(success);
            }
        }
    }
}
