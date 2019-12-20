package de.ibba.keepitup.logging;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class FileDump implements IDump {

    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 20;
    private final static String DEFAULT_DUMP_FILE_EXTENSION = "txt";
    private final static String DEFAULT_EMPTY_MESSAGE = "No entries.";

    private final int archiveFileCount;
    private final String dumpFileExtension;
    private final String emptyMessage;
    private final String dumpDirectory;

    public FileDump(String dumpDirectory) {
        this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DUMP_FILE_EXTENSION, DEFAULT_EMPTY_MESSAGE);
    }

    public FileDump(String dumpDirectory, String dumpFileExtension) {
        this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, dumpFileExtension, DEFAULT_EMPTY_MESSAGE);
    }

    public FileDump(String dumpDirectory, int archiveFileCount, String dumpFileExtension, String emptyMessage) {
        this.archiveFileCount = archiveFileCount;
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
                List<?> objectsToDump = source.objectsToDump();
                File dumpFolder = new File(dumpDirectory);
                if (!dumpFolder.exists()) {
                    dumpFolder.mkdirs();
                }
                LogFileManager fileManager = new LogFileManager();
                LogFormatter formatter = new LogFormatter();
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
                Housekeeper housekeeper = new Housekeeper(dumpDirectory, baseDumpFileName, archiveFileCount, new DumpFilenameFilter(baseDumpFileName));
                Thread housekeepingThread = new Thread(housekeeper);
                housekeepingThread.start();
            } catch (Exception exc) {
                //Do nothing
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
