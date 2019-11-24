package de.ibba.keepitup.test.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.resources.IFileManager;

public class MockFileManager implements IFileManager {

    private final List<DeieteDirectoryCall> deieteDirectoryCalls;

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
    private boolean deleteDiractory;
    private String downloadFileName;

    public MockFileManager() {
        deieteDirectoryCalls = new ArrayList<>();
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
        deleteDiractory = true;
        downloadFileName = null;
    }

    public List<DeieteDirectoryCall> getDeieteDirectoryCalls() {
        return Collections.unmodifiableList(deieteDirectoryCalls);
    }

    public void reset() {
        deieteDirectoryCalls.clear();
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
        deleteDiractory = true;
        downloadFileName = null;
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

    public void setDeleteDiractory(boolean deleteDiractory) {
        this.deleteDiractory = deleteDiractory;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public boolean wasDeieteDirectoryCalled() {
        return !deieteDirectoryCalls.isEmpty();
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
    public boolean deleteDirectory(File directory) {
        deieteDirectoryCalls.add(new DeieteDirectoryCall(directory));
        return deleteDiractory;
    }

    @Override
    public String getDownloadFileName(String url, String contentDisposition, String mimeType) {
        return downloadFileName;
    }

    public static class DeieteDirectoryCall {

        private final File directory;

        public DeieteDirectoryCall(File directory) {
            this.directory = directory;
        }

        public File getDirectory() {
            return directory;
        }
    }
}
