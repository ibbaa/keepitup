package net.ibbaa.keepitup.util;

import java.io.File;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.IFileManager;

public class FileUtil {

    public static String getFileNameExtension(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            return "";
        }
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex < 0 ? "" : fileName.substring(extensionIndex + 1);
    }

    public static String getFileNameWithoutExtension(String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            return "";
        }
        int extensionIndex = fileName.lastIndexOf('.');
        return fileName.substring(0, extensionIndex < 0 ? fileName.length() : extensionIndex);
    }

    public static String suffixFileName(String fileName, String suffix) {
        if (StringUtil.isEmpty(fileName)) {
            return "";
        }
        if (StringUtil.isEmpty(suffix)) {
            return fileName;
        }
        String extension = getFileNameExtension(fileName);
        if (!StringUtil.isEmpty(extension)) {
            return getFileNameWithoutExtension(fileName) + "_" + suffix + "." + extension;
        }
        return getFileNameWithoutExtension(fileName) + "_" + suffix;
    }

    public static File getExternalDirectory(IFileManager fileManager, PreferenceManager preferenceManager, String directoryName) {
        return getExternalDirectory(fileManager, preferenceManager, directoryName, false);
    }

    public static File getExternalDirectory(IFileManager fileManager, PreferenceManager preferenceManager, String directoryName, boolean alwaysPrimary) {
        Log.d(FileUtil.class.getName(), "getExternalDirectory");
        if (fileManager.isSDCardSupported() && !alwaysPrimary) {
            Log.d(FileUtil.class.getName(), "SD card is supported");
            return fileManager.getExternalDirectory(directoryName, preferenceManager.getPreferenceExternalStorageType());
        } else {
            Log.d(FileUtil.class.getName(), "SD card is not supported");
            return fileManager.getExternalDirectory(directoryName, 0);
        }
    }

    public static File getExternalRootDirectory(IFileManager fileManager, PreferenceManager preferenceManager) {
        return getExternalRootDirectory(fileManager, preferenceManager, false);
    }

    public static File getExternalRootDirectory(IFileManager fileManager, PreferenceManager preferenceManager, boolean alwaysPrimary) {
        Log.d(FileUtil.class.getName(), "getExternalRootDirectory");
        if (fileManager.isSDCardSupported() && !alwaysPrimary) {
            Log.d(FileUtil.class.getName(), "SD card is supported");
            return fileManager.getExternalRootDirectory(preferenceManager.getPreferenceExternalStorageType());
        } else {
            Log.d(FileUtil.class.getName(), "SD card is not supported");
            return fileManager.getExternalRootDirectory(0);
        }
    }
}
