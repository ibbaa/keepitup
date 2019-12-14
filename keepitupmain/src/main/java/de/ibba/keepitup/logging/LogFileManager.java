package de.ibba.keepitup.logging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.ibba.keepitup.util.FileUtil;
import de.ibba.keepitup.util.StringUtil;

public class LogFileManager {

    private final static int MAX_DUPLICATE_FILES = 99;
    private final static String SUFFIX_FILE_PATTERN = "yyyy.MM.dd_HH_mm_ss.SSS";

    public String getFileNameExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex < 0 ? "" : fileName.substring(extensionIndex + 1);
    }

    public String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int extensionIndex = fileName.lastIndexOf('.');
        return fileName.substring(0, extensionIndex < 0 ? fileName.length() : extensionIndex);
    }

    public String suffixFileName(String fileName, String suffix) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        if (suffix == null || suffix.isEmpty()) {
            return fileName;
        }
        String extension = getFileNameExtension(fileName);
        if (!StringUtil.isEmpty(extension)) {
            return getFileNameWithoutExtension(fileName) + "_" + suffix + "." + extension;
        }
        return getFileNameWithoutExtension(fileName) + "_" + suffix;
    }

    public boolean delete(File file) {
        try {
            File[] files = file.listFiles();
            if (files != null) {
                for (File currentFile : files) {
                    delete(currentFile);
                }
            }
            return file.delete();
        } catch (Exception exc) {
            return false;
        }
    }

    public String getValidFileName(File folder, String file, Long timestamp) {
        try {
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    return null;
                }
            }
            File resultingFile = new File(folder, file);
            if (!resultingFile.exists()) {
                return file;
            }
            String timestampFileName = file;
            if (timestamp != null) {
                timestampFileName = suffixFileName(file, getTimestampSuffix(timestamp.longValue()));
                resultingFile = new File(folder, timestampFileName);
                if (!resultingFile.exists()) {
                    return timestampFileName;
                }
            }
            for (int ii = 1; ii <= MAX_DUPLICATE_FILES; ii++) {
                String numberFileName = FileUtil.suffixFileName(timestampFileName, getNumberSuffix(ii));
                resultingFile = new File(folder, numberFileName);
                if (!resultingFile.exists()) {
                    return numberFileName;
                }
            }
        } catch (Exception exc) {
            //Do nothing
        }
        return null;
    }

    public String getTimestampSuffix(long timestamp) {
        SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(SUFFIX_FILE_PATTERN, Locale.US);
        return fileNameDateFormat.format(new Date(timestamp));
    }

    private String getNumberSuffix(int number) {
        return "(" + number + ")";
    }
}
