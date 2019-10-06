package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;

import de.ibba.keepitup.R;

public class FileManager {

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

    public File getExternalDownloadDirectory(String downloadDirectoryName) {
        Log.d(FileManager.class.getName(), "getExternalDownloadDirectory, downloadDirectoryName is " + downloadDirectoryName);
        try {
            File externalDir = getExternalRootDirectory();
            if (externalDir == null) {
                Log.d(FileManager.class.getName(), "Cannot access external files root directory.");
                return null;
            }
            Log.d(FileManager.class.getName(), "External files root directory is " + externalDir.getAbsolutePath());
            File externalDownloadDir = new File(externalDir, downloadDirectoryName);
            Log.d(FileManager.class.getName(), "External files download directory is " + externalDownloadDir.getAbsolutePath());
            if (externalDownloadDir.exists()) {
                Log.d(FileManager.class.getName(), "External files download directory does exist.");
                return externalDownloadDir;
            } else {
                Log.d(FileManager.class.getName(), "External files download directory does not exist. Creating.");
                if (externalDownloadDir.mkdirs()) {
                    return externalDownloadDir;
                }
                Log.e(FileManager.class.getName(), "Failure on creating the directory " + externalDownloadDir.getAbsolutePath());
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
