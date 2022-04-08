/*
 * Copyright (c) 2022. Alwin Ibba
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

import net.ibbaa.keepitup.model.FileEntry;
import net.ibbaa.keepitup.service.IFileManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockFileManager implements IFileManager {

    private final List<DeleteCall> deieteCalls;

    private File internalDownloadDirectory;
    private File internalRootDirectory;
    private ArrayList<File> externalDirectories;
    private ArrayList<File> externalRootDirectories;
    private String defaultDownloadDirectoryName;
    private String relativeSibling;
    private String relativeParent;
    private String absoluteParent;
    private String absolutePath;
    private String nestedPath;
    private List<FileEntry> fileEntries;
    private boolean delete;
    private String downloadFileName;
    private String logFileName;
    private boolean fileExists;
    private String validFileName;
    private boolean sdCardSupported;

    public MockFileManager() {
        deieteCalls = new ArrayList<>();
        internalDownloadDirectory = null;
        internalRootDirectory = null;
        externalDirectories = new ArrayList<>();
        externalRootDirectories = new ArrayList<>();
        defaultDownloadDirectoryName = null;
        relativeSibling = null;
        relativeParent = null;
        absoluteParent = null;
        absolutePath = null;
        nestedPath = null;
        fileEntries = Collections.emptyList();
        delete = true;
        downloadFileName = null;
        logFileName = null;
        fileExists = false;
        validFileName = null;
        sdCardSupported = false;
    }

    public List<DeleteCall> getDeieteCalls() {
        return Collections.unmodifiableList(deieteCalls);
    }

    public void reset() {
        deieteCalls.clear();
        internalDownloadDirectory = null;
        internalRootDirectory = null;
        externalDirectories = new ArrayList<>();
        externalRootDirectories = new ArrayList<>();
        defaultDownloadDirectoryName = null;
        relativeSibling = null;
        relativeParent = null;
        absoluteParent = null;
        absolutePath = null;
        nestedPath = null;
        fileEntries = Collections.emptyList();
        delete = true;
        downloadFileName = null;
        logFileName = null;
        fileExists = false;
        validFileName = null;
        sdCardSupported = false;
    }

    public void setInternalDownloadDirectory(File internalDownloadDirectory) {
        this.internalDownloadDirectory = internalDownloadDirectory;
    }

    public void setInternalRootDirectory(File internalRootDirectory) {
        this.internalRootDirectory = internalRootDirectory;
    }

    public void setExternalDirectory(File externalDirectory, int externalStorage) {
        int size = externalDirectories.size();
        for (int ii = size; ii <= externalStorage + 1; ii++) {
            externalDirectories.add(null);
        }
        this.externalDirectories.set(externalStorage, externalDirectory);
    }

    public void setExternalRootDirectory(File externalRootDirectory, int externalStorage) {
        int size = externalRootDirectories.size();
        for (int ii = size; ii <= externalStorage + 1; ii++) {
            externalRootDirectories.add(null);
        }
        this.externalRootDirectories.set(externalStorage, externalRootDirectory);
    }

    public void setDefaultDownloadDirectoryName(String defaultDownloadDirectoryName) {
        this.defaultDownloadDirectoryName = defaultDownloadDirectoryName;
    }

    public void setRelativeSibling(String relativeSibling) {
        this.relativeSibling = relativeSibling;
    }

    public void setRelativeParent(String relativeParent) {
        this.relativeParent = relativeParent;
    }

    public void setAbsoluteParent(String absoluteParent) {
        this.absoluteParent = absoluteParent;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
    }

    public void setFileEntries(List<FileEntry> fileEntries) {
        this.fileEntries = fileEntries;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void setDoesFileExist(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public void setValidFileName(String validFileName) {
        this.validFileName = validFileName;
    }

    public void setSDCardSupported(boolean sdCardSupported) {
        this.sdCardSupported = sdCardSupported;
    }

    public boolean wasDeieteDirectoryCalled() {
        return !deieteCalls.isEmpty();
    }

    @Override
    public File getInternalDownloadDirectory() {
        return this.internalDownloadDirectory;
    }

    @Override
    public File getInternalRootDirectory() {
        return this.internalRootDirectory;
    }

    @Override
    public File getExternalDirectory(String directoryName, int externalStorage) {
        return this.externalDirectories.get(externalStorage);
    }

    @Override
    public File getExternalRootDirectory(int externalStorage) {
        return this.externalRootDirectories.get(externalStorage);
    }

    @Override
    public String getDefaultDownloadDirectoryName() {
        return this.defaultDownloadDirectoryName;
    }

    @Override
    public String getRelativeSibling(String folder, String sibling) {
        return this.relativeSibling;
    }

    @Override
    public String getRelativeParent(String folder) {
        return this.relativeParent;
    }

    @Override
    public String getAbsoluteParent(String root, String absoluteFolder) {
        return this.absoluteParent;
    }

    @Override
    public String getAbsolutePath(String root, String absoluteFolder) {
        return this.absolutePath;
    }

    @Override
    public String getNestedPath(String folder1, String folder2) {
        return this.nestedPath;
    }

    @Override
    public List<FileEntry> getFiles(String root, String absoluteFolder) {
        return fileEntries;
    }

    @Override
    public boolean delete(File file) {
        deieteCalls.add(new DeleteCall(file));
        return delete;
    }

    @Override
    public String getDownloadFileName(URL url, String specifiedFileName, String mimeType) {
        return downloadFileName;
    }

    @Override
    public String getLogFileName(String baseFileName, String extension, int index, String address) {
        return logFileName;
    }

    @Override
    public boolean doesFileExist(File folder, String file) {
        return fileExists;
    }

    @Override
    public String getValidFileName(File folder, String file) {
        return validFileName;
    }

    @Override
    public boolean isSDCardSupported() {
        return sdCardSupported;
    }

    public static class DeleteCall {

        private final File file;

        public DeleteCall(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
}
