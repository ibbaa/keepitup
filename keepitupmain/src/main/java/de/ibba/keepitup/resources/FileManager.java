package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;

public class FileManager implements IFileManager {

    private final Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public File getInternalDownloadDirectory() {
        Log.d(FileManager.class.getName(), "getInternalDownloadDirectory");
        try {
            File internalDir = getInternalRootDirectory();
            if (internalDir == null) {
                Log.d(FileManager.class.getName(), "Cannot access internal files root directory.");
                return null;
            }
            Log.d(FileManager.class.getName(), "Internal files root directory is " + internalDir.getAbsolutePath());
            String downloadDir = getDefaultDownloadDirectoryName();
            File internalDownloadDir = new File(internalDir, downloadDir);
            Log.d(FileManager.class.getName(), "Internal files download directory is " + internalDownloadDir.getAbsolutePath());
            if (internalDownloadDir.exists()) {
                Log.d(FileManager.class.getName(), "Internal files download directory does exist.");
                return internalDownloadDir;
            } else {
                Log.d(FileManager.class.getName(), "Internal files download directory does not exist. Creating.");
                if (internalDownloadDir.mkdirs()) {
                    return internalDownloadDir;
                }
                Log.e(FileManager.class.getName(), "Failure on creating the directory " + internalDownloadDir.getAbsolutePath());
            }
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing internal files directory", exc);
        }
        return null;
    }

    public File getInternalRootDirectory() {
        Log.d(FileManager.class.getName(), "getInternalRootDirectory");
        try {
            File internalDir = getContext().getFilesDir();
            if (internalDir != null) {
                Log.d(FileManager.class.getName(), "Internal files root directory is " + internalDir.getAbsolutePath());
                return internalDir;
            }
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing internal files root directory", exc);
        }
        return null;
    }

    public File getExternalDirectory(String directoryName) {
        Log.d(FileManager.class.getName(), "getExternalDirectory, directoryName is " + directoryName);
        try {
            File externalRootDir = getExternalRootDirectory();
            if (externalRootDir == null) {
                Log.d(FileManager.class.getName(), "Cannot access external files root directory.");
                return null;
            }
            Log.d(FileManager.class.getName(), "External files root directory is " + externalRootDir.getAbsolutePath());
            File externalDir = new File(externalRootDir, directoryName);
            Log.d(FileManager.class.getName(), "External files directory is " + externalDir.getAbsolutePath());
            if (externalDir.exists()) {
                Log.d(FileManager.class.getName(), "External files directory does exist.");
                return externalDir;
            } else {
                Log.d(FileManager.class.getName(), "External files directory does not exist. Creating.");
                if (externalDir.mkdirs()) {
                    return externalDir;
                }
                Log.e(FileManager.class.getName(), "Failure on creating the directory " + externalDir.getAbsolutePath());
            }
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing external files root directory", exc);
        }
        return null;
    }

    public File getExternalRootDirectory() {
        Log.d(FileManager.class.getName(), "getExternalRootDirectory");
        try {
            File externalDir = getContext().getExternalFilesDir(null);
            if (externalDir != null) {
                Log.d(FileManager.class.getName(), "External files root directory is " + externalDir.getAbsolutePath());
                return externalDir;
            }
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing external files directory", exc);
        }
        return null;
    }

    public String getDefaultDownloadDirectoryName() {
        Log.d(FileManager.class.getName(), "getDefaultDownloadDirectoryName");
        String downloadDir = getResources().getString(R.string.download_folder_default);
        Log.d(FileManager.class.getName(), "Default relative download directory is " + downloadDir);
        return downloadDir;
    }

    public String getParent(String root, String absoluteFolder) {
        Log.d(FileManager.class.getName(), "getParent, root is " + root + ", absoluteFolder is " + absoluteFolder);
        try {
            File rootFile = new File(root);
            File absoluteFolderFile = new File(absoluteFolder);
            if (rootFile.equals(absoluteFolderFile)) {
                Log.d(FileManager.class.getName(), "getParent, root and absoluteFolder are identical");
                return absoluteFolder;
            }
            File parentFile = absoluteFolderFile.getParentFile();
            if (parentFile != null) {
                Log.d(FileManager.class.getName(), "parent is " + parentFile.getAbsolutePath());
                return parentFile.getAbsolutePath();
            }
            Log.d(FileManager.class.getName(), "parent is null");
            return absoluteFolder;
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing parent directory", exc);
            return null;
        }
    }

    public List<FileEntry> getFiles(String root, String absoluteFolder) {
        Log.d(FileManager.class.getName(), "getFiles, root is " + root + ", absoluteFolder is " + absoluteFolder);
        try {
            File rootFile = new File(root);
            File absoluteFolderFile = new File(absoluteFolder);
            File[] files = absoluteFolderFile.listFiles();
            if (files != null && files.length > 0) {
                List<FileEntry> fileEntries = new ArrayList<>(files.length + 1);
                if (!rootFile.equals(absoluteFolderFile)) {
                    FileEntry fileEntry = new FileEntry();
                    fileEntry.setName("..");
                    fileEntry.setDirectory(true);
                    fileEntry.setParent(true);
                    Log.d(FileManager.class.getName(), "Folder is not the root folder. Adding parent entry " + fileEntry);
                    fileEntries.add(fileEntry);
                }
                for (File file : files) {
                    FileEntry fileEntry = new FileEntry();
                    fileEntry.setName(file.getName());
                    fileEntry.setDirectory(file.isDirectory());
                    fileEntry.setParent(false);
                    Log.d(FileManager.class.getName(), "Adding entry " + fileEntry);
                    fileEntries.add(fileEntry);
                }
                return fileEntries;
            }
            Log.d(FileManager.class.getName(), "file list is empty");
            return Collections.emptyList();
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error listing files in directory", exc);
            return null;
        }
    }

    public boolean deleteDirectory(File directory) {
        Log.d(FileManager.class.getName(), "deleteDirectory, directory is " + directory);
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectory(file)) {
                        Log.d(FileManager.class.getName(), "Deletion of the file/directory failed: " + file.getAbsolutePath());
                    }
                }
            }
            return directory.delete();
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error deleting directory", exc);
            return false;
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
