/*
 * Copyright (c) 2025 Alwin Ibba
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
import android.os.ParcelFileDescriptor;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.PreferenceSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.service.IDocumentManager;
import net.ibbaa.keepitup.service.SystemDocumentManager;
import net.ibbaa.keepitup.ui.ImportSupport;
import net.ibbaa.keepitup.util.StreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ImportTask extends UIBackgroundTask<SystemSetupResult> {

    private final File importFolder;
    private final String file;
    private final boolean useDocumentApi;

    public ImportTask(Activity activity, File importFolder, String file, boolean useDocumentApi) {
        super(activity);
        this.importFolder = importFolder;
        this.file = file;
        this.useDocumentApi = useDocumentApi;
    }

    @Override
    protected SystemSetupResult runInBackground() {
        Log.d(ImportTask.class.getName(), "runInBackground");
        try {
            Context context = getActivity();
            if (context != null) {
                SystemSetupResult checkResult = doImportCheck(context);
                if (!checkResult.success()) {
                    return checkResult;
                }
                if (!doPurge(context)) {
                    return new SystemSetupResult(false, false, "", "");
                }
                return doImport(context, checkResult.data());
            }
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error exporting database", exc);
        }
        return new SystemSetupResult(false, false, "", "");
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

    private SystemSetupResult doImportCheck(Context context) throws Exception {
        Log.d(ImportTask.class.getName(), "doImportCheck");
        ParcelFileDescriptor fileDescriptor = null;
        FileInputStream stream = null;
        try {
            JSONSystemSetup setup = new JSONSystemSetup(context);
            if (useDocumentApi) {
                DocumentFile documentFile = getDocumentManager().getFile(file);
                if (documentFile == null) {
                    Log.e(ExportTask.class.getName(), "Error accessing file uri " + file);
                    return new SystemSetupResult(false, false, "", "");
                }
                fileDescriptor = getImportFileDescriptor(documentFile);
                stream = getImportFileInputStream(fileDescriptor);
            } else {
                File importFile = new File(importFolder, file);
                stream = new FileInputStream(importFile);
            }
            String data = StreamUtil.inputStreamToString(stream, StandardCharsets.UTF_8);
            SystemSetupResult result = setup.checkImportPossible(data);
            return new SystemSetupResult(result.success(), result.versionMismatch(), result.message(), data);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception exc) {
                    Log.e(ImportTask.class.getName(), "Error closing file", exc);
                }
            }
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (Exception exc) {
                    Log.e(ExportTask.class.getName(), "Error closing file descriptor", exc);
                }
            }
        }
    }

    private boolean purgeTables(Context context) {
        Log.d(ImportTask.class.getName(), "purgeTables");
        DBSetup setup = new DBSetup(context);
        boolean logTableSuccess = false;
        boolean networkTaskTableSuccess = false;
        boolean schedulerIdTableSuccess = false;
        boolean intervalTableSuccess = false;
        boolean schedulerStateTableSuccess = false;
        boolean accessTypeDataTableSuccess = false;
        try {
            setup.deleteAllLogs();
            logTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging log table", exc);
        }
        try {
            setup.deleteAllNetworkTasks();
            networkTaskTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging network task table", exc);
        }
        try {
            setup.deleteAllSchedulerIds();
            schedulerIdTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging scheduler id table", exc);
        }
        try {
            setup.deleteAllIntervals();
            intervalTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging interval table", exc);
        }
        try {
            setup.recreateSchedulerStateTable();
            schedulerStateTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging scheduler state table", exc);
        }
        try {
            setup.recreateAccessTypeDataTable();
            accessTypeDataTableSuccess = true;
        } catch (Exception exc) {
            Log.e(ImportTask.class.getName(), "Error purging access type data table", exc);
        }
        Log.d(ImportTask.class.getName(), "logTableSuccess: " + logTableSuccess);
        Log.d(ImportTask.class.getName(), "networkTaskTableSuccess: " + networkTaskTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "schedulerIdTableSuccess: " + schedulerIdTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "intervalTableSuccess: " + intervalTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "schedulerStateTableSuccess: " + schedulerStateTableSuccess);
        Log.d(DBPurgeTask.class.getName(), "accessTypeDataTableSuccess: " + accessTypeDataTableSuccess);
        return logTableSuccess && networkTaskTableSuccess && schedulerIdTableSuccess && intervalTableSuccess && schedulerStateTableSuccess && accessTypeDataTableSuccess;
    }

    private SystemSetupResult doImport(Context context, String data) {
        Log.d(ImportTask.class.getName(), "doImport for data " + data);
        JSONSystemSetup setup = new JSONSystemSetup(context);
        SystemSetupResult result = setup.importData(data);
        Log.d(ImportTask.class.getName(), "Import returned " + result);
        if (result.success()) {
            Log.d(ImportTask.class.getName(), "Import was successful: " + result.message());
        } else {
            Log.d(ImportTask.class.getName(), "Import was not successful: " + result.message());
        }
        return result;
    }

    @Override
    protected void runOnUIThread(SystemSetupResult result) {
        Log.d(ImportTask.class.getName(), "runOnUIThread, result is " + result);
        if (result == null) {
            result = new SystemSetupResult(false, false, "", "");
        }
        Activity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            if (activity instanceof ImportSupport) {
                String message = result.versionMismatch() ? getActivity().getResources().getString(R.string.text_dialog_general_message_config_import_version_mismatch) : null;
                ((ImportSupport) activity).onImportDone(result.success(), message);
            }
        }
    }

    protected IDocumentManager getDocumentManager() {
        return new SystemDocumentManager(getActivity());
    }

    protected ParcelFileDescriptor getImportFileDescriptor(DocumentFile documentFile) throws IOException {
        return getActivity().getContentResolver().openFileDescriptor(documentFile.getUri(), "r");
    }

    protected FileInputStream getImportFileInputStream(ParcelFileDescriptor documentFileDescriptor) {
        return new FileInputStream(documentFileDescriptor.getFileDescriptor());
    }
}
