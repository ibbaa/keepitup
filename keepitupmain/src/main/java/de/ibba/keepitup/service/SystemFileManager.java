package de.ibba.keepitup.service;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.resources.ServiceFactoryContributor;
import de.ibba.keepitup.util.FileUtil;
import de.ibba.keepitup.util.StringUtil;

public class SystemFileManager implements IFileManager {

    private final Context context;
    private final ITimeService timeService;

    public SystemFileManager(Context context) {
        this.context = context;
        this.timeService = createTimeService();
    }

    @Override
    public File getInternalDownloadDirectory() {
        Log.d(SystemFileManager.class.getName(), "getInternalDownloadDirectory");
        try {
            File internalDir = getInternalRootDirectory();
            if (internalDir == null) {
                Log.d(SystemFileManager.class.getName(), "Cannot access internal files root directory.");
                return null;
            }
            Log.d(SystemFileManager.class.getName(), "Internal files root directory is " + internalDir.getAbsolutePath());
            String downloadDir = getDefaultDownloadDirectoryName();
            File internalDownloadDir = new File(internalDir, downloadDir);
            Log.d(SystemFileManager.class.getName(), "Internal files download directory is " + internalDownloadDir.getAbsolutePath());
            if (internalDownloadDir.exists()) {
                Log.d(SystemFileManager.class.getName(), "Internal files download directory does exist.");
                return internalDownloadDir;
            } else {
                Log.d(SystemFileManager.class.getName(), "Internal files download directory does not exist. Creating.");
                if (internalDownloadDir.mkdirs()) {
                    return internalDownloadDir;
                }
                Log.e(SystemFileManager.class.getName(), "Failure on creating the directory " + internalDownloadDir.getAbsolutePath());
            }
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing internal files directory", exc);
        }
        return null;
    }

    @Override
    public File getInternalRootDirectory() {
        Log.d(SystemFileManager.class.getName(), "getInternalRootDirectory");
        try {
            File internalDir = getContext().getFilesDir();
            if (internalDir != null) {
                Log.d(SystemFileManager.class.getName(), "Internal files root directory is " + internalDir.getAbsolutePath());
                return internalDir;
            }
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing internal files root directory", exc);
        }
        return null;
    }

    @Override
    public File getExternalDirectory(String directoryName) {
        Log.d(SystemFileManager.class.getName(), "getExternalDirectory, directoryName is " + directoryName);
        try {
            File externalRootDir = getExternalRootDirectory();
            if (externalRootDir == null) {
                Log.d(SystemFileManager.class.getName(), "Cannot access external files root directory.");
                return null;
            }
            Log.d(SystemFileManager.class.getName(), "External files root directory is " + externalRootDir.getAbsolutePath());
            File externalDir = new File(externalRootDir, directoryName);
            Log.d(SystemFileManager.class.getName(), "External files directory is " + externalDir.getAbsolutePath());
            if (externalDir.exists()) {
                Log.d(SystemFileManager.class.getName(), "External files directory does exist.");
                return externalDir;
            } else {
                Log.d(SystemFileManager.class.getName(), "External files directory does not exist. Creating.");
                if (externalDir.mkdirs()) {
                    return externalDir;
                }
                Log.e(SystemFileManager.class.getName(), "Failure on creating the directory " + externalDir.getAbsolutePath());
            }
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing external files root directory", exc);
        }
        return null;
    }

    @Override
    public File getExternalRootDirectory() {
        Log.d(SystemFileManager.class.getName(), "getExternalRootDirectory");
        try {
            File externalDir = getContext().getExternalFilesDir(null);
            if (externalDir != null) {
                Log.d(SystemFileManager.class.getName(), "External files root directory is " + externalDir.getAbsolutePath());
                return externalDir;
            }
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing external files directory", exc);
        }
        return null;
    }

    @Override
    public String getDefaultDownloadDirectoryName() {
        Log.d(SystemFileManager.class.getName(), "getDefaultDownloadDirectoryName");
        String downloadDir = getResources().getString(R.string.download_folder_default);
        Log.d(SystemFileManager.class.getName(), "Default relative download directory is " + downloadDir);
        return downloadDir;
    }

    @Override
    public String getRelativeSibling(String folder, String sibling) {
        Log.d(SystemFileManager.class.getName(), "getRelativeSibling, folder is " + folder + ", sibling is " + sibling);
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
            Log.e(SystemFileManager.class.getName(), "Error accessing parent directory", exc);
            return null;
        }
    }

    @Override
    public String getRelativeParent(String folder) {
        Log.d(SystemFileManager.class.getName(), "getRelativeParent, folder is " + folder);
        folder = StringUtil.notNull(folder);
        try {
            File folderFile = new File(folder);
            String parent = folderFile.getParent();
            if (parent == null) {
                return "";
            }
            return parent;
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing parent directory", exc);
            return null;
        }
    }

    @Override
    public String getAbsoluteParent(String root, String absoluteFolder) {
        Log.d(SystemFileManager.class.getName(), "getAbsoluteParent, root is " + root + ", absoluteFolder is " + absoluteFolder);
        try {
            File rootFile = new File(root);
            File absoluteFolderFile = new File(absoluteFolder);
            if (rootFile.equals(absoluteFolderFile)) {
                Log.d(SystemFileManager.class.getName(), "getAbsoluteParent, root and absoluteFolder are identical");
                return absoluteFolder;
            }
            File parentFile = absoluteFolderFile.getParentFile();
            if (parentFile != null) {
                Log.d(SystemFileManager.class.getName(), "parent is " + parentFile.getAbsolutePath());
                return parentFile.getAbsolutePath();
            }
            Log.d(SystemFileManager.class.getName(), "parent is null");
            return absoluteFolder;
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error accessing parent directory", exc);
            return null;
        }
    }

    @Override
    public String getAbsoluteFolder(String root, String folder) {
        Log.d(SystemFileManager.class.getName(), "getAbsoluteFolder, root is " + root + ", folder is " + folder);
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
        Log.d(SystemFileManager.class.getName(), "getNestedFolder, folder1 is " + folder1 + ", folder2 is " + folder2);
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
        Log.d(SystemFileManager.class.getName(), "getFiles, root is " + root + ", absoluteFolder is " + absoluteFolder);
        try {
            File rootFile = new File(root);
            File absoluteFolderFile = new File(absoluteFolder);
            File[] files = absoluteFolderFile.listFiles();
            if (files != null) {
                Log.d(SystemFileManager.class.getName(), "listFiles returned " + files.length + " files");
                Log.d(SystemFileManager.class.getName(), "Creating entries...");
            }
            List<FileEntry> fileEntries = new ArrayList<>();
            if (files != null && files.length > 0) {
                Log.d(SystemFileManager.class.getName(), "Creating file entries");
                for (File file : files) {
                    FileEntry fileEntry = new FileEntry();
                    fileEntry.setName(file.getName());
                    fileEntry.setDirectory(file.isDirectory());
                    fileEntry.setParent(false);
                    fileEntry.setCanVisit(file.isDirectory());
                    Log.d(SystemFileManager.class.getName(), "Adding entry " + fileEntry);
                    fileEntries.add(fileEntry);
                }
                Log.d(SystemFileManager.class.getName(), "Sorting file entries");
                Collections.sort(fileEntries, (FileEntry entry1, FileEntry entry2) -> entry1.getName().compareTo(entry2.getName()));
            } else {
                Log.d(SystemFileManager.class.getName(), "File list is empty");
            }
            Log.d(SystemFileManager.class.getName(), "Creating parent entry");
            FileEntry parentEntry = new FileEntry();
            parentEntry.setName("..");
            parentEntry.setDirectory(true);
            parentEntry.setParent(true);
            if (absoluteFolderFile.equals(rootFile)) {
                Log.d(SystemFileManager.class.getName(), "Folder is the root folder. Setting canVisit to false.");
                parentEntry.setCanVisit(false);
            } else {
                Log.d(SystemFileManager.class.getName(), "Folder is not the root folder. Setting canVisit to true.");
                parentEntry.setCanVisit(true);
            }
            Log.d(SystemFileManager.class.getName(), "Adding parent entry " + parentEntry);
            fileEntries.add(0, parentEntry);
            return fileEntries;
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error listing files in directory", exc);
            return null;
        }
    }

    @Override
    public boolean delete(File file) {
        Log.d(SystemFileManager.class.getName(), "delete, file is " + file);
        try {
            File[] files = file.listFiles();
            if (files != null) {
                for (File currentFile : files) {
                    if (!delete(currentFile)) {
                        Log.d(SystemFileManager.class.getName(), "Deletion of the file/directory failed: " + file.getAbsolutePath());
                    }
                }
            }
            return file.delete();
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error deleting file/directory", exc);
            return false;
        }
    }

    @Override
    public String getDownloadFileName(URL url, String specifiedFileName, String mimeType) {
        Log.d(SystemFileManager.class.getName(), "getDownloadFileName, url is " + url + ", specifiedFileName is " + specifiedFileName + ", mimeType is " + mimeType);
        String fileName = null;
        if (!StringUtil.isEmpty(specifiedFileName)) {
            fileName = specifiedFileName;
            Log.d(SystemFileManager.class.getName(), "Specified file name is " + fileName);
        }
        if (!isValidFileName(fileName)) {
            fileName = extractFileNameFromURL(url);
            Log.d(SystemFileManager.class.getName(), "File name extracted from URL is " + fileName);
        }
        if (!isValidFileName(fileName)) {
            fileName = createFileNameFromHost(url);
            Log.d(SystemFileManager.class.getName(), "File name extracted from host is " + fileName);
        }
        if (!isValidFileName(fileName)) {
            Log.d(SystemFileManager.class.getName(), "File name is invalid.");
            fileName = "";
        }
        String fileNameWithoutExtension = FileUtil.getFileNameWithoutExtension(fileName);
        String extension = FileUtil.getFileNameExtension(fileName);
        Log.d(SystemFileManager.class.getName(), "File name without extension is " + fileNameWithoutExtension);
        Log.d(SystemFileManager.class.getName(), "File name extension is " + extension);
        if (!isValidFileName(fileNameWithoutExtension)) {
            fileNameWithoutExtension = "downloadfile";
            Log.d(SystemFileManager.class.getName(), "File name without extension is invalid. Setting to " + fileNameWithoutExtension);
        }
        if (StringUtil.isEmpty(extension)) {
            Log.d(SystemFileManager.class.getName(), "File name extension is empty.");
            if (!StringUtil.isEmpty(mimeType)) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                Log.d(SystemFileManager.class.getName(), "File name extension extracted from mime type is " + extension);
            } else {
                Log.d(SystemFileManager.class.getName(), "No mime type specified.");
            }
        }
        if (!StringUtil.isEmpty(extension)) {
            String fileNameWithExtension = fileNameWithoutExtension + "." + extension;
            Log.d(SystemFileManager.class.getName(), "Returning file name " + fileNameWithExtension);
            return fileNameWithExtension;
        }
        Log.d(SystemFileManager.class.getName(), "Returning file name " + fileNameWithoutExtension);
        return fileNameWithoutExtension;
    }

    private static String extractFileNameFromURL(URL url) {
        Log.d(SystemFileManager.class.getName(), "extractFileNameFromURL, url is " + url);
        try {
            String fileName = new File(url.toURI().getPath()).getName();
            return Uri.decode(fileName);
        } catch (Exception exc) {
            Log.d(SystemFileManager.class.getName(), "Exception extracting file name from URL " + url, exc);
        }
        return null;
    }

    private static String createFileNameFromHost(URL url) {
        Log.d(SystemFileManager.class.getName(), "createFileNameFromHost, url is " + url);
        try {
            String host = url.toURI().getHost();
            if (!StringUtil.isEmpty(host)) {
                return host.replaceAll("\\.", "_");
            }
        } catch (Exception exc) {
            Log.d(SystemFileManager.class.getName(), "Exception creating file name from URL " + url, exc);
        }
        return null;
    }

    private static boolean isValidFileName(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            return false;
        }
        return !fileName.replaceAll("/", "").replaceAll("\\.", "").isEmpty();
    }

    @Override
    public String getValidFileName(File folder, String file) {
        Log.d(SystemFileManager.class.getName(), "getValidFileName, folder is " + folder + ", file is " + file);
        try {
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    Log.e(SystemFileManager.class.getName(), "Error creating " + folder);
                    return null;
                }
            }
            File resultingFile = new File(folder, file);
            if (!resultingFile.exists()) {
                Log.d(SystemFileManager.class.getName(), "File " + resultingFile + " does not exist");
                return file;
            }
            Log.d(SystemFileManager.class.getName(), "File " + resultingFile + " does exist");
            String timestampFileName = FileUtil.suffixFileName(file, getTimestampSuffix());
            resultingFile = new File(folder, timestampFileName);
            if (!resultingFile.exists()) {
                Log.d(SystemFileManager.class.getName(), "File " + resultingFile + " does not exist");
                return timestampFileName;
            }
            Log.d(SystemFileManager.class.getName(), "File " + resultingFile + " does exist");
            int maxDuplicateFileNumber = getResources().getInteger(R.integer.max_duplicate_file_number);
            for (int ii = 1; ii <= maxDuplicateFileNumber; ii++) {
                String numberFileName = FileUtil.suffixFileName(timestampFileName, getNumberSuffix(ii));
                resultingFile = new File(folder, numberFileName);
                if (!resultingFile.exists()) {
                    Log.d(SystemFileManager.class.getName(), "File " + resultingFile + " does not exist");
                    return numberFileName;
                }
                Log.d(SystemFileManager.class.getName(), "File " + resultingFile + " does exist");
            }
            Log.d(SystemFileManager.class.getName(), "Unable to find valid file name");
        } catch (Exception exc) {
            Log.e(SystemFileManager.class.getName(), "Error creating valid file name", exc);
        }
        return null;
    }

    public ITimeService getTimeService() {
        return timeService;
    }

    private String getTimestampSuffix() {
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(getResources().getString(R.string.timestamp_suffix_file_pattern), Locale.US);
        return fileNameDateFormat.format(new Date(getTimeService().getCurrentTimestamp()));
    }

    private String getNumberSuffix(int number) {
        return "(" + number + ")";
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
