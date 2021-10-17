/*
 * Copyright (c) 2021. Alwin Ibba
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

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.ui.ExportSupport;
import net.ibbaa.keepitup.util.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;

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
