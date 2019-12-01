package de.ibba.keepitup.util;

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
}
