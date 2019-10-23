package de.ibba.keepitup.resources;

import java.io.File;
import java.util.List;

import de.ibba.keepitup.model.FileEntry;

public interface IFileManager {

    File getInternalDownloadDirectory();

    File getInternalRootDirectory();

    File getExternalDirectory(String directoryName);

    File getExternalRootDirectory();

    String getDefaultDownloadDirectoryName();

    String getParent(String root, String absoluteFolder);

    List<FileEntry> getFiles(String root, String absoluteFolder);

    boolean deleteDirectory(File directory);
}
