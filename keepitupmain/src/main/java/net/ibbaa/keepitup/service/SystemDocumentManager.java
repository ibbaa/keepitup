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

package net.ibbaa.keepitup.service;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.logging.Log;

public class SystemDocumentManager implements IDocumentManager {

    private final Context context;

    public SystemDocumentManager(Context context) {
        this.context = context;
    }

    @Override
    public DocumentFile getArbitraryDirectory(String arbitraryFolder) {
        Log.d(SystemDocumentManager.class.getName(), "getArbitraryDirectory, arbitraryFolder is " + arbitraryFolder);
        try {
            DocumentFile arbitraryDirectory = DocumentFile.fromTreeUri(getContext(), Uri.parse(arbitraryFolder));
            if (arbitraryDirectory != null && arbitraryDirectory.isDirectory()) {
                if (arbitraryDirectory.canRead() && arbitraryDirectory.canWrite()) {
                    return arbitraryDirectory;
                } else {
                    Log.e(SystemFileManager.class.getName(), "Insufficient permission for folder " + arbitraryFolder);
                }
            } else {
                Log.e(SystemFileManager.class.getName(), arbitraryFolder + " is not a directory");
            }
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing arbitraryFolder folder " + arbitraryFolder, exc);
        }
        return null;
    }

    private Context getContext() {
        return context;
    }
}
