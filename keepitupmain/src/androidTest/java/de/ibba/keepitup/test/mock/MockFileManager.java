package de.ibba.keepitup.test.mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.resources.IFileManager;

public class MockFileManager implements IFileManager {

    private final List<DeieteDirectoryCall> deieteDirectoryCalls;

    private File internalDownloadDirectory;
    private File internalRootDirectory;
    private File externalDownloadDirectory;
    private File externalRootDirectory;
    private String defaultDownloadDirectoryName;

    public MockFileManager() {
        deieteDirectoryCalls = new ArrayList<>();
        internalDownloadDirectory = null;
        internalRootDirectory = null;
        externalDownloadDirectory = null;
        externalRootDirectory = null;
        defaultDownloadDirectoryName = null;
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

    public boolean wasdeieteDirectoryCalled() {
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
    public boolean deleteDirectory(File directory) {
        return false;
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
