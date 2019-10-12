package de.ibba.keepitup.resources;

import java.io.File;

public interface IFileManager {

    File getInternalDownloadDirectory();

    File getInternalRootDirectory();

    File getExternalDownloadDirectory(String downloadDirectoryName);

    File getExternalRootDirectory();

    String getDefaultDownloadDirectoryName();

    boolean deleteDirectory(File directory);
}
