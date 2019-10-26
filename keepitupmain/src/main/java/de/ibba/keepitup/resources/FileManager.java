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
import de.ibba.keepitup.util.StringUtil;

public class FileManager implements IFileManager {

    private final Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public String getDefaultDownloadDirectoryName() {
        Log.d(FileManager.class.getName(), "getDefaultDownloadDirectoryName");
        String downloadDir = getResources().getString(R.string.download_folder_default);
        Log.d(FileManager.class.getName(), "Default relative download directory is " + downloadDir);
        return downloadDir;
    }

    @Override
    public String getRelativeSibling(String folder, String sibling) {
        Log.d(FileManager.class.getName(), "getRelativeSibling, folder is " + folder + ", siblimg is " + sibling);
        folder = StringUtil.notNull(folder);
        if (StringUtil.isEmpty(sibling)) {
            return folder;
        }
        try {
            File folderFile = new File(folder);
            String parent = folderFile.getParent();
            if (parent == null) {
                return sibling;
            }
            if (!parent.endsWith("/")) {
                parent += "/";
            }
            return parent + sibling;
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing parent directory", exc);
            return null;
        }
    }

    @Override
    public String getRelativeParent(String folder) {
        Log.d(FileManager.class.getName(), "getRelativeParent, folder is " + folder);
        folder = StringUtil.notNull(folder);
        try {
            File folderFile = new File(folder);
            String parent = folderFile.getParent();
            if (parent == null) {
                return "";
            }
            return parent;
        } catch (Exception exc) {
            Log.e(FileManager.class.getName(), "Error accessing parent directory", exc);
            return null;
        }
    }

    @Override
    public String getAbsoluteParent(String root, String absoluteFolder) {
        Log.d(FileManager.class.getName(), "getAbsoluteParent, root is " + root + ", absoluteFolder is " + absoluteFolder);
        try {
            File rootFile = new File(root);
            File absoluteFolderFile = new File(absoluteFolder);
            if (rootFile.equals(absoluteFolderFile)) {
                Log.d(FileManager.class.getName(), "getAbsoluteParent, root and absoluteFolder are identical");
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

    @Override
    public String getAbsoluteFolder(String root, String folder) {
        Log.d(FileManager.class.getName(), "getAbsoluteFolder, root is " + root + ", folder is " + folder);
        if (StringUtil.isEmpty(folder)) {
            return root;
        }
        if (!root.endsWith("/")) {
            root += "/";
        }
        return root + folder;
    }

    @Override
    public String getNestedFolder(String folder1, String folder2) {
        Log.d(FileManager.class.getName(), "getNestedFolder, folder1 is " + folder1 + ", folder2 is " + folder2);
        folder2 = StringUtil.notNull(folder2);
        if (StringUtil.isEmpty(folder1)) {
            return folder2;
        }
        if (!folder1.endsWith("/") && !folder2.isEmpty()) {
            folder1 += "/";
        }
        return folder1 + folder2;
    }

    public List<FileEntry> getFiles(String root, String absoluteFolder) {
        Log.d(FileManager.class.getName(), "getFiles, root is " + root + ", absoluteFolder is " + absoluteFolder);
        try {
            File rootFile = new File(root);
            File absoluteFolderFile = new File(absoluteFolder);
            File[] files = absoluteFolderFile.listFiles();
            if (files != null) {
                Log.d(FileManager.class.getName(), "listFiles returned " + files.length + " files");
                Log.d(FileManager.class.getName(), "Creating entries...");
            }
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

    @Override
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
