package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.util.FileUtil;
import de.ibba.keepitup.util.StringUtil;

public class FileManager implements IFileManager {

    private static final Pattern CONTENT_DISPOSITION = Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);

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
        Log.d(FileManager.class.getName(), "getRelativeSibling, folder is " + folder + ", sibling is " + sibling);
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
            List<FileEntry> fileEntries = new ArrayList<>();
            if (files != null && files.length > 0) {
                Log.d(FileManager.class.getName(), "Creating file entries");
                for (File file : files) {
                    FileEntry fileEntry = new FileEntry();
                    fileEntry.setName(file.getName());
                    fileEntry.setDirectory(file.isDirectory());
                    fileEntry.setParent(false);
                    fileEntry.setCanVisit(file.isDirectory());
                    Log.d(FileManager.class.getName(), "Adding entry " + fileEntry);
                    fileEntries.add(fileEntry);
                }
                Log.d(FileManager.class.getName(), "Sorting file entries");
                Collections.sort(fileEntries, (FileEntry entry1, FileEntry entry2) -> entry1.getName().compareTo(entry2.getName()));
            } else {
                Log.d(FileManager.class.getName(), "File list is empty");
            }
            Log.d(FileManager.class.getName(), "Creating parent entry");
            FileEntry parentEntry = new FileEntry();
            parentEntry.setName("..");
            parentEntry.setDirectory(true);
            parentEntry.setParent(true);
            if (absoluteFolderFile.equals(rootFile)) {
                Log.d(FileManager.class.getName(), "Folder is the root folder. Setting canVisit to false.");
                parentEntry.setCanVisit(false);
            } else {
                Log.d(FileManager.class.getName(), "Folder is not the root folder. Setting canVisit to true.");
                parentEntry.setCanVisit(true);
            }
            Log.d(FileManager.class.getName(), "Adding parent entry " + parentEntry);
            fileEntries.add(0, parentEntry);
            return fileEntries;
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

    @Override
    public String getDownloadFileName(URL url, String contentDisposition, String mimeType) {
        Log.d(FileManager.class.getName(), "getDownloadFileName, url is " + url + ", contentDisposition is " + contentDisposition + ", mimeType is " + mimeType);
        String fileName = null;
        if (!StringUtil.isEmpty(contentDisposition)) {
            fileName = parseContentDisposition(contentDisposition);
            if (fileName != null) {
                int index = fileName.lastIndexOf('/') + 1;
                if (index > 0) {
                    fileName = fileName.substring(index);
                }
            }
            Log.d(FileManager.class.getName(), "File name extracted from content disposition is " + fileName);
        }
        if (!isValidFileName(fileName)) {
            fileName = extractFileNameFromURL(url);
            Log.d(FileManager.class.getName(), "File name extracted from URL is " + fileName);
        }
        if (!isValidFileName(fileName)) {
            fileName = createFileNameFromHost(url);
            Log.d(FileManager.class.getName(), "File name extracted from host is " + fileName);
        }
        if (!isValidFileName(fileName)) {
            Log.d(FileManager.class.getName(), "File name is invalid.");
            fileName = "";
        }
        String fileNameWithoutExtension = cleanUp(FileUtil.getFileNameWithoutExtension(fileName));
        String extension = cleanUp(FileUtil.getFileNameExtension(fileName));
        Log.d(FileManager.class.getName(), "File name without extension is " + fileNameWithoutExtension);
        Log.d(FileManager.class.getName(), "File name extension is " + extension);
        if (!isValidFileName(fileNameWithoutExtension)) {
            fileNameWithoutExtension = "downloadfile";
            Log.d(FileManager.class.getName(), "File name without extension is invalid. Setting to " + fileNameWithoutExtension);
        }
        if (StringUtil.isEmpty(extension)) {
            Log.d(FileManager.class.getName(), "File name extension is empty.");
            if (!StringUtil.isEmpty(mimeType)) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                Log.d(FileManager.class.getName(), "File name extension extracted from mime type is " + extension);
            } else {
                Log.d(FileManager.class.getName(), "No mime type specified.");
            }
        }
        if (!StringUtil.isEmpty(extension)) {
            String fileNameWithExtension = fileNameWithoutExtension + "." + extension;
            Log.d(FileManager.class.getName(), "Returing file name " + fileNameWithExtension);
            return fileNameWithExtension;
        }
        Log.d(FileManager.class.getName(), "Returing file name " + fileNameWithoutExtension);
        return fileNameWithoutExtension;
    }

    private static String parseContentDisposition(String contentDisposition) {
        Log.d(FileManager.class.getName(), "parseContentDisposition, contentDisposition is " + contentDisposition);
        try {
            Matcher matcher = CONTENT_DISPOSITION.matcher(contentDisposition);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception exc) {
            Log.d(FileManager.class.getName(), "Exception parsing content disposition " + contentDisposition, exc);
        }
        return null;
    }

    private static String extractFileNameFromURL(URL url) {
        Log.d(FileManager.class.getName(), "extractFileNameFromURL, url is " + url);
        try {
            String fileName = new File(url.toURI().getPath()).getName();
            return Uri.decode(fileName);
        } catch (Exception exc) {
            Log.d(FileManager.class.getName(), "Exception extracting file name from URL " + url, exc);
        }
        return null;
    }

    private static String createFileNameFromHost(URL url) {
        Log.d(FileManager.class.getName(), "createFileNameFromHost, url is " + url);
        try {
            String host = url.toURI().getHost();
            if (!StringUtil.isEmpty(host)) {
                return host.replaceAll("\\.", "_");
            }
        } catch (Exception exc) {
            Log.d(FileManager.class.getName(), "Exception creating file name from URL " + url, exc);
        }
        return null;
    }

    private static boolean isValidFileName(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            return false;
        }
        return !cleanUp(fileName).replaceAll("\\.", "").isEmpty();
    }

    private static String cleanUp(String name) {
        Log.d(FileManager.class.getName(), "cleanUp, name is " + name);
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        return name.replaceAll("/", "");
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
