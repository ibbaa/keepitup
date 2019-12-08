package de.ibba.keepitup.test.mock;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.service.IFileManager;

public class MockFileManager implements IFileManager {

    private final List<DeieteCall> deieteCalls;

    private File internalDownloadDirectory;
    private File internalRootDirectory;
    private File externalDownloadDirectory;
    private File externalRootDirectory;
    private String defaultDownloadDirectoryName;
    private String relativeSibling;
    private String relativeParent;
    private String absoluteParent;
    private String absoluteFolder;
    private String nestedFolder;
    private List<FileEntry> fileEntries;
    private boolean delete;
    private String downloadFileName;
    private String validFileName;

    public MockFileManager() {
        deieteCalls = new ArrayList<>();
        internalDownloadDirectory = null;
        internalRootDirectory = null;
        externalDownloadDirectory = null;
        externalRootDirectory = null;
        defaultDownloadDirectoryName = null;
        relativeSibling = null;
        relativeParent = null;
        absoluteParent = null;
        absoluteFolder = null;
        nestedFolder = null;
        fileEntries = Collections.emptyList();
        delete = true;
        downloadFileName = null;
        validFileName = null;
    }

    public List<DeieteCall> getDeieteCalls() {
        return Collections.unmodifiableList(deieteCalls);
    }

    public void reset() {
        deieteCalls.clear();
        internalDownloadDirectory = null;
        internalRootDirectory = null;
        externalDownloadDirectory = null;
        externalRootDirectory = null;
        defaultDownloadDirectoryName = null;
        relativeSibling = null;
        relativeParent = null;
        absoluteParent = null;
        absoluteFolder = null;
        nestedFolder = null;
        fileEntries = Collections.emptyList();
        delete = true;
        downloadFileName = null;
        validFileName = null;
    }

    public void setInternalDownloadDirectory(File internalDownloadDirectory) {
        this.internalDownloadDirectory = internalDownloadDirectory;
    }

    public void setInternalRootDirectory(File internalRootDirectory) {
        this.internalRootDirectory = internalRootDirectory;
    }

    public void setExternalDownloadDirectory(File externalDownloadDirectory) {
        this.externalDownloadDirectory = externalDownloadDirectory;
    }

    public void setExternalRootDirectory(File externalRootDirectory) {
        this.externalRootDirectory = externalRootDirectory;
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

    public void setAbsoluteFolder(String absoluteFolder) {
        this.absoluteFolder = absoluteFolder;
    }

    public void setNestedFolder(String nestedFolder) {
        this.nestedFolder = nestedFolder;
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

    public void setValidFileName(String validFileName) {
        this.validFileName = validFileName;
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
    public File getExternalDirectory(String downloadDirectoryName) {
        return this.externalDownloadDirectory;
    }

    @Override
    public File getExternalRootDirectory() {
        return this.externalRootDirectory;
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
    public String getAbsoluteFolder(String root, String absoluteFolder) {
        return this.absoluteFolder;
    }

    @Override
    public String getNestedFolder(String folder1, String folder2) {
        return this.nestedFolder;
    }

    @Override
    public List<FileEntry> getFiles(String root, String absoluteFolder) {
        return fileEntries;
    }

    @Override
    public boolean delete(File file) {
        deieteCalls.add(new DeieteCall(file));
        return delete;
    }

    @Override
    public String getDownloadFileName(URL url, String specifiedFileName, String mimeType) {
        return downloadFileName;
    }

    @Override
    public String getValidFileName(File folder, String file) {
        return validFileName;
    }

    public static class DeieteCall {

        private final File file;

        public DeieteCall(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
}
