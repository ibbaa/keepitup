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

package net.ibbaa.keepitup.test.mock;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.service.IDocumentManager;

@SuppressWarnings({"unused"})
public class MockDocumentManager implements IDocumentManager {

    private DocumentFile arbitraryDirectory;
    private boolean fileExists;
    private DocumentFile folder;
    private DocumentFile file;
    private String validFileName;
    private boolean deleteSuccess;

    public MockDocumentManager() {
        arbitraryDirectory = null;
        fileExists = true;
        folder = null;
        file = null;
        validFileName = "";
        deleteSuccess = true;
    }

    public void reset() {
        arbitraryDirectory = null;
        fileExists = true;
        folder = null;
        file = null;
        validFileName = "";
        deleteSuccess = true;
    }

    @Override
    public DocumentFile getArbitraryDirectory(String arbitraryFolder) {
        return arbitraryDirectory;
    }

    @Override
    public boolean fileExists(DocumentFile folder, String fileName) {
        return fileExists;
    }

    @Override
    public DocumentFile getFolder(String dir) {
        return folder;
    }

    @Override
    public DocumentFile getFile(DocumentFile folder, String fileName) {
        return file;
    }

    @Override
    public boolean delete(DocumentFile file) {
        return deleteSuccess;
    }

    @Override
    public String getValidFileName(DocumentFile folder, String file) {
        return validFileName;
    }

    public void setArbitraryDirectory(DocumentFile arbitraryFolder) {
        this.arbitraryDirectory = arbitraryFolder;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public void setFolder(DocumentFile folder) {
        this.folder = folder;
    }

    public void setFile(DocumentFile file) {
        this.file = file;
    }

    public void setDeleteSuccess(boolean deleteSuccess) {
        this.deleteSuccess = deleteSuccess;
    }

    public void setValidFileName(String validFileName) {
        this.validFileName = validFileName;
    }
}
