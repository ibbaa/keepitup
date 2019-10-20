package de.ibba.keepitup.resources;

import java.io.File;

public interface IFileManager {

    File getInternalDownloadDirectory();

    File getInternalRootDirectory();

    File getExternalDirectory(String directoryName);

    File getExternalRootDirectory();

    String getDefaultDownloadDirectoryName();

    boolean deleteDirectory(File directory);
}
