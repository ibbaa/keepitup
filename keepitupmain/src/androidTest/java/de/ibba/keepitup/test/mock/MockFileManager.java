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
    private List<FileEntry> fileEntries;
    private boolean deleteDiractory;

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
        fileEntries = Collections.emptyList();
        deleteDiractory = true;
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
        fileEntries = Collections.emptyList();
        deleteDiractory = true;
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

    public void setFileEntries(List<FileEntry> fileEntries) {
        this.fileEntries = fileEntries;
    }

    public void setDeleteDiractory(boolean deleteDiractory) {
        this.deleteDiractory = deleteDiractory;
    }

    public boolean wasDeieteDirectoryCalled() {
        return !deieteDirectoryCalls.isEmpty();
    }

    @Override
    public File getInternalDownloadDirectory() {
        return internalDownloadDirectory;
    }

    @Override
    public File getInternalRootDirectory() {
        return internalRootDirectory;
    }

    @Override
    public File getExternalDirectory(String downloadDirectoryName) {
        return externalDownloadDirectory;
    }

    @Override
    public File getExternalRootDirectory() {
        return externalRootDirectory;
    }

    @Override
    public String getDefaultDownloadDirectoryName() {
        return defaultDownloadDirectoryName;
    }

    @Override
    public String getRelativeSibling(String folder, String sibling) {
        return relativeSibling;
    }

    @Override
    public String getRelativeParent(String folder) {
        return relativeParent;
    }

    @Override
    public String getAbsoluteParent(String root, String absoluteFolder) {
        return absoluteParent;
    }

    @Override
    public String getAbsoluteFolder(String root, String absoluteFolder) {
        return absoluteFolder;
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
