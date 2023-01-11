/*
 * Copyright (c) 2023. Alwin Ibba
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FileDump implements IDump {

    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 50;
    private final static int DEFAULT_DELETE_FILE_COUNT = -1;
    private final static String DEFAULT_DUMP_FILE_EXTENSION = "txt";
    private final static String DEFAULT_EMPTY_MESSAGE = "No entries.";

    private final int archiveFileCount;
    private final int deleteFileCount;
    private final String dumpFileExtension;
    private final String emptyMessage;
    private final String dumpDirectory;

    private final static ReentrantLock dumpLock = new ReentrantLock();

    public FileDump(String dumpDirectory) {
        this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, DEFAULT_DUMP_FILE_EXTENSION, DEFAULT_EMPTY_MESSAGE);
    }

    public FileDump(String dumpDirectory, String dumpFileExtension) {
        this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, dumpFileExtension, DEFAULT_EMPTY_MESSAGE);
    }

    public FileDump(String dumpDirectory, int archiveFileCount, int deleteFileCount, String dumpFileExtension, String emptyMessage) {
        this.archiveFileCount = archiveFileCount;
        this.deleteFileCount = deleteFileCount;
        this.dumpFileExtension = dumpFileExtension;
        this.emptyMessage = emptyMessage;
        this.dumpDirectory = dumpDirectory;
    }

    @Override
    public void dump(String tag, String message, String baseFileName, IDumpSource source) {
        if (source == null) {
            return;
        }
        LogFileEntry logEntry = null;
        if (tag != null && message != null) {
            logEntry = new LogFileEntry(System.currentTimeMillis(), Thread.currentThread().getName(), LogLevel.DEBUG, tag, message, null);
        }
        Thread dumpThread = new Thread(new DumpThread(logEntry, baseFileName, source));
        dumpThread.start();
    }

    private class DumpThread implements Runnable {

        private final LogFileEntry logEntry;
        private final String baseFileName;
        private final IDumpSource source;

        public DumpThread(LogFileEntry logEntry, String baseFileName, IDumpSource source) {
            this.logEntry = logEntry;
            this.baseFileName = baseFileName;
            this.source = source;
        }

        @Override
        public void run() {
            try {
                dumpLock.lock();
                List<?> objectsToDump = source.objectsToDump();
                File dumpFolder = new File(dumpDirectory);
                if (!dumpFolder.exists()) {
                    dumpFolder.mkdirs();
                }
                LogFileManager fileManager = new LogFileManager();
                DefaultLogFormatter formatter = new DefaultLogFormatter();
                String baseDumpFileName = baseFileName;
                if (baseDumpFileName == null) {
                    if (objectsToDump == null || objectsToDump.isEmpty()) {
                        return;
                    }
                    baseDumpFileName = objectsToDump.get(0).getClass().getSimpleName().toLowerCase();
                }
                baseDumpFileName += "." + dumpFileExtension;
                long timestamp = logEntry != null ? logEntry.getTimestamp() : System.currentTimeMillis();
                String header = logEntry != null ? formatter.formatLogFileEntry(logEntry) : null;
                String dumpFileName = fileManager.suffixFileName(baseDumpFileName, fileManager.getTimestampSuffix(timestamp));
                dumpFileName = fileManager.getValidFileName(dumpFolder, dumpFileName, null);
                fileManager.writeListToFile(header, emptyMessage, objectsToDump, new File(dumpFolder, dumpFileName));
                if (archiveFileCount > 0) {
                    Housekeeper housekeeper = new Housekeeper(dumpDirectory, baseDumpFileName, archiveFileCount, deleteFileCount, new DumpFilenameFilter(baseDumpFileName));
                    housekeeper.doHousekeepingNow();
                }
            } catch (Exception exc) {
                //Do nothing
            } finally {
                dumpLock.unlock();
            }
        }
    }

    private class DumpFilenameFilter implements FilenameFilter {

        private final String baseDumpFileName;

        public DumpFilenameFilter(String baseDumpFileName) {
            this.baseDumpFileName = baseDumpFileName;
        }

        @Override
        public boolean accept(File dir, String name) {
            LogFileManager fileManager = new LogFileManager();
            String dumpFileBaseName = fileManager.getFileNameWithoutExtension(baseDumpFileName);
            String dumpFileSuffix = fileManager.getFileNameExtension(baseDumpFileName);
            return name.startsWith(dumpFileBaseName) && name.endsWith(dumpFileSuffix);
        }
    }
}
