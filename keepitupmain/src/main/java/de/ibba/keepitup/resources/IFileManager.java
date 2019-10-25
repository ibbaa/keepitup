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

    String getRelativeSibling(String folder, String sibling);

    String getRelativeParent(String folder);

    String getAbsoluteParent(String root, String absoluteFolder);

    String getAbsoluteFolder(String root, String absoluteFolder);

    String getNestedFolder(String folder1, String folder2);

    List<FileEntry> getFiles(String root, String absoluteFolder);

    boolean deleteDirectory(File directory);
}
