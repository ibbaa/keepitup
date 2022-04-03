/*
 * Copyright (c) 2022. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.logging;

import com.google.common.base.Charsets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogFileManager {

    private static final int BUFFER_SIZE_1024 = 1024;
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
        if (extension != null && extension.length() > 0) {
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
                timestampFileName = suffixFileName(file, getTimestampSuffix(timestamp));
                resultingFile = new File(folder, timestampFileName);
                if (!resultingFile.exists()) {
                    return timestampFileName;
                }
            }
            for (int ii = 1; ii <= MAX_DUPLICATE_FILES; ii++) {
                String numberFileName = suffixFileName(timestampFileName, getNumberSuffix(ii));
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

    public void zipFiles(List<File> files, File zipFile) {
        if (zipFile.exists()) {
            if (!zipFile.delete()) {
                return;
            }
        }
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File currentFile : files) {
                if (currentFile.exists() && currentFile.isFile()) {
                    writeFileToZip(currentFile, zipOutputStream);
                }
            }
            for (File currentFile : files) {
                if (currentFile.exists() && currentFile.isFile()) {
                    currentFile.delete();
                }
            }
        } catch (FileNotFoundException exc) {
            //do nothing
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.flush();
                    zipOutputStream.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    private void writeFileToZip(File file, ZipOutputStream zipOutputStream) {
        ZipEntry zipEntry = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[BUFFER_SIZE_1024];
            int read;
            while ((read = fileInputStream.read(buffer, 0, BUFFER_SIZE_1024)) >= 0) {
                zipOutputStream.write(buffer, 0, read);
            }
        } catch (Exception exc) {
            //do nothing
        } finally {
            if (zipEntry != null) {
                try {
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }

    public void writeListToFile(String header, String emptyMessage, List<?> objects, File file) {
        if (file.exists()) {
            if (!file.delete()) {
                return;
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            if (header != null) {
                header = header + System.lineSeparator();
                outputStream.write(header.getBytes(Charsets.UTF_8));
            }
            if (objects == null || objects.isEmpty()) {
                if (emptyMessage != null) {
                    emptyMessage = emptyMessage + System.lineSeparator();
                    outputStream.write(emptyMessage.getBytes(Charsets.UTF_8));
                }
            } else {
                for (Object object : objects) {
                    if (object != null) {
                        String data = object + System.lineSeparator();
                        outputStream.write(data.getBytes(Charsets.UTF_8));
                    }
                }
            }
        } catch (Exception exc) {
            //do nothing
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }
    }
}
