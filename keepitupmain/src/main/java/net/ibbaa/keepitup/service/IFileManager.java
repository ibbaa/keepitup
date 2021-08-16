package net.ibbaa.keepitup.service;

import java.io.File;
import java.net.URL;
import java.util.List;

import net.ibbaa.keepitup.model.FileEntry;

public interface IFileManager {

    File getInternalDownloadDirectory();

    File getInternalRootDirectory();

    File getExternalDirectory(String directoryName, int externalStorage);

    File getExternalRootDirectory(int externalStorage);

    String getDefaultDownloadDirectoryName();

    String getRelativeSibling(String folder, String sibling);

    String getRelativeParent(String folder);

    String getAbsoluteParent(String root, String absoluteFolder);

    String getAbsolutePath(String root, String path);

    String getNestedPath(String path1, String path2);

    List<FileEntry> getFiles(String root, String absoluteFolder);

    boolean delete(File file);

    String getDownloadFileName(URL url, String specifiedFileName, String mimeType);

    boolean doesFileExist(File folder, String file);

    String getValidFileName(File folder, String file);

    boolean isSDCardSupported();
}