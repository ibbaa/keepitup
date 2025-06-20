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

package net.ibbaa.keepitup.service;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.util.FileUtil;

public class SystemDocumentManager implements IDocumentManager {

    private final Context context;
    private final ITimeService timeService;

    public SystemDocumentManager(Context context) {
        this.context = context;
        this.timeService = createTimeService();
    }

    public SystemDocumentManager(Context context, ITimeService timeService) {
        this.context = context;
        this.timeService = timeService;
    }

    @Override
    public DocumentFile getArbitraryDirectory(String arbitraryFolder) {
        Log.d(SystemDocumentManager.class.getName(), "getArbitraryDirectory, arbitraryFolder is " + arbitraryFolder);
        try {
            DocumentFile arbitraryDirectory = getFolder(arbitraryFolder);
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

    @Override
    public String getValidFileName(DocumentFile folder, String file) {
        Log.d(SystemFileManager.class.getName(), "getValidFileName, file is " + file);
        try {
            file = file.replaceAll("/", "");
            if (!fileExists(folder, file)) {
                Log.d(SystemFileManager.class.getName(), "File " + file + " does not exist");
                return file;
            }
            Log.d(SystemFileManager.class.getName(), "File " + file + " does exist");
            String timestampFileName = FileUtil.suffixFileName(file, FileUtil.getTimestampSuffix(getContext(), getTimeService()));
            if (!fileExists(folder, timestampFileName)) {
                Log.d(SystemFileManager.class.getName(), "File " + timestampFileName + " does not exist");
                return timestampFileName;
            }
            Log.d(SystemFileManager.class.getName(), "File " + timestampFileName + " does exist");
            int maxDuplicateFileNumber = getContext().getResources().getInteger(R.integer.max_duplicate_file_number);
            for (int ii = 1; ii <= maxDuplicateFileNumber; ii++) {
                String numberFileName = FileUtil.suffixFileName(timestampFileName, FileUtil.getNumberSuffix(ii));
                if (!fileExists(folder, numberFileName)) {
                    Log.d(SystemFileManager.class.getName(), "File " + numberFileName + " does not exist");
                    return numberFileName;
                }
                Log.d(SystemFileManager.class.getName(), "File " + numberFileName + " does exist");
            }
            Log.d(SystemFileManager.class.getName(), "Unable to find valid file name");
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error creating valid file name", exc);
        }
        return null;
    }

    @Override
    public DocumentFile getFolder(String folder) {
        Log.d(SystemFileManager.class.getName(), "getFolder, folder is " + folder);
        return DocumentFile.fromTreeUri(getContext(), Uri.parse(folder));
    }

    @Override
    public DocumentFile getFile(String file) {
        Log.d(SystemFileManager.class.getName(), "getFile, file is " + file);
        return DocumentFile.fromSingleUri(getContext(), Uri.parse(file));
    }

    @Override
    public DocumentFile getFile(DocumentFile folder, String fileName) {
        Log.d(SystemFileManager.class.getName(), "getFile, fileName is " + fileName);
        return folder.findFile(fileName);
    }

    @Override
    public boolean fileExists(DocumentFile folder, String fileName) {
        Log.d(SystemFileManager.class.getName(), "fileExists, fileName is " + fileName);
        return folder.findFile(fileName) != null;
    }

    @Override
    public boolean delete(DocumentFile file) {
        Log.d(SystemFileManager.class.getName(), "delete");
        return file.delete();
    }

    public ITimeService getTimeService() {
        return timeService;
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private Context getContext() {
        return context;
    }
}
