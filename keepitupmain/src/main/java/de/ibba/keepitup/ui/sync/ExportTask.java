package de.ibba.keepitup.ui.sync;

import android.app.Activity;
import android.content.Context;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.FileOutputStream;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.JSONSystemSetup;
import de.ibba.keepitup.resources.SystemSetupResult;
import de.ibba.keepitup.ui.ExportSupport;
import de.ibba.keepitup.util.StreamUtil;

public class ExportTask extends UIBackgroundTask<Boolean> {

    private final File exportFolder;
    private final String file;

    public ExportTask(Activity activity, File exportFolder, String file) {
        super(activity);
        this.exportFolder = exportFolder;
        this.file = file;
    }

    @Override
    protected Boolean runInBackground() {
        Log.d(ExportTask.class.getName(), "runInBackground");
        FileOutputStream stream = null;
        try {
            Context context = getActivity();
            if (context != null) {
                JSONSystemSetup setup = new JSONSystemSetup(context);
                SystemSetupResult result = setup.exportData();
                Log.d(ExportTask.class.getName(), "Export returned " + result);
                if (result.isSuccess()) {
                    Log.d(ExportTask.class.getName(), "Export was successful: " + result.getMessage());
                    File exportFile = new File(exportFolder, file);
                    stream = new FileOutputStream(exportFile);
                    StreamUtil.stringToOutputStream(result.getData(), stream, Charsets.UTF_8);
                    return true;
                } else {
                    Log.d(ExportTask.class.getName(), "Export was not successful: " + result.getMessage());
                }
            }
        } catch (Exception exc) {
            Log.e(ExportTask.class.getName(), "Error exporting database", exc);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception exc) {
                    Log.e(ExportTask.class.getName(), "Error closing file", exc);
                }
            }
        }
        return false;
    }

    @Override
    protected void runOnUIThread(Boolean success) {
        Log.d(ExportTask.class.getName(), "runOnUIThread, success is " + success);
        if (success == null) {
            success = false;
        }
        Activity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            if (activity instanceof ExportSupport) {
                ((ExportSupport) activity).onExportDone(success);
            }
        }
    }
}
