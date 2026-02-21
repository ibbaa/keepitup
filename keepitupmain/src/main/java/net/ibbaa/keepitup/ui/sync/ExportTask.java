/*
 * Copyright (c) 2026 Alwin Ibba
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

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.EncryptionInfo;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.resources.encryption.EncryptionSetupResult;
import net.ibbaa.keepitup.resources.encryption.JSONEncryptSetup;
import net.ibbaa.keepitup.service.IDocumentManager;
import net.ibbaa.keepitup.service.SystemDocumentManager;
import net.ibbaa.keepitup.ui.support.ExportSupport;
import net.ibbaa.keepitup.util.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExportTask extends UIBackgroundTask<SystemSetupResult> {

    private final File exportFolder;
    private final String file;
    private final EncryptionInfo encryptionInfo;
    private final boolean useDocumentApi;

    public ExportTask(Activity activity, File exportFolder, String file, EncryptionInfo encryptionInfo, boolean useDocumentApi) {
        super(activity);
        this.exportFolder = exportFolder;
        this.file = file;
        this.encryptionInfo = encryptionInfo;
        this.useDocumentApi = useDocumentApi;
    }

    @Override
    protected SystemSetupResult runInBackground() {
        Log.d(ExportTask.class.getName(), "runInBackground");
        ParcelFileDescriptor fileDescriptor = null;
        FileOutputStream stream = null;
        try {
            Context context = getActivity();
            if (context != null) {
                JSONSystemSetup setup = new JSONSystemSetup(context);
                SystemSetupResult setupResult = setup.exportData();
                Log.d(ExportTask.class.getName(), "Export returned " + setupResult);
                if (setupResult.success()) {
                    Log.d(ExportTask.class.getName(), "Export was successful: " + setupResult.message());
                    String fileData = setupResult.data();
                    if (encryptionInfo.isEncrypt()) {
                        Log.d(ExportTask.class.getName(), "Encrypting data...");
                        JSONEncryptSetup encryptSetup = new JSONEncryptSetup(context);
                        EncryptionSetupResult encryptionResult = encryptSetup.encrypt(encryptionInfo.getPassword(), fileData);
                        if (!encryptionResult.success()) {
                            return new SystemSetupResult(false, false, encryptionResult.message(), "");
                        }
                        fileData = encryptionResult.data();
                    }
                    if (useDocumentApi) {
                        DocumentFile documentFile = getDocumentManager().getFile(file);
                        if (documentFile == null) {
                            Log.e(ExportTask.class.getName(), "Error accessing file uri " + file);
                            return new SystemSetupResult(false, false, "", "");
                        }
                        fileDescriptor = getExportFileDescriptor(documentFile);
                        stream = getExportFileOutputStream(fileDescriptor);
                    } else {
                        File exportFile = new File(exportFolder, file);
                        stream = new FileOutputStream(exportFile);
                    }
                    StreamUtil.stringToOutputStream(fileData, stream, StandardCharsets.UTF_8);
                    return setupResult;
                } else {
                    Log.d(ExportTask.class.getName(), "Export was not successful: " + setupResult.message());
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
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (Exception exc) {
                    Log.e(ExportTask.class.getName(), "Error closing file descriptor", exc);
                }
            }
        }
        return new SystemSetupResult(false, false, "", "");
    }

    @Override
    protected void runOnUIThread(SystemSetupResult result) {
        Log.d(ExportTask.class.getName(), "runOnUIThread, result is " + result);
        if (result == null) {
            result = new SystemSetupResult(false, false, "", "");
        }
        Activity activity = getActivity();
        if (activity != null && !activity.isDestroyed()) {
            if (activity instanceof ExportSupport) {
                ((ExportSupport) activity).onExportDone(result.success(), result.message());
            }
        }
    }

    protected IDocumentManager getDocumentManager() {
        return new SystemDocumentManager(getActivity());
    }

    protected ParcelFileDescriptor getExportFileDescriptor(DocumentFile documentFile) throws IOException {
        return getActivity().getContentResolver().openFileDescriptor(documentFile.getUri(), "w");
    }

    protected FileOutputStream getExportFileOutputStream(ParcelFileDescriptor documentFileDescriptor) {
        return new FileOutputStream(documentFileDescriptor.getFileDescriptor());
    }
}
