package de.ibba.keepitup.logging;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class FileDump implements IDump {

    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 20;
    private final static String DEFAULT_DUMP_FILE_EXTENSION = "txt";

    private final int archiveFileCount;
    private final String dumpFileExtension;
    private final String dumpDirectory;

    public FileDump(String dumpDirectory) {
        this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DUMP_FILE_EXTENSION);
    }

    public FileDump(String dumpDirectory, String dumpFileExtension) {
        this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, dumpFileExtension);
    }

    public FileDump(String dumpDirectory, int archiveFileCount, String dumpFileExtension) {
        this.archiveFileCount = archiveFileCount;
        this.dumpFileExtension = dumpFileExtension;
        this.dumpDirectory = dumpDirectory;
    }

    @Override
    public void dump(IDumpSource source) {
        if (source == null) {
            return;
        }
        Thread dumpThread = new Thread(new DumpThread(source));
        dumpThread.start();
    }

    private class DumpThread implements Runnable {

        private final IDumpSource source;

        public DumpThread(IDumpSource source) {
            this.source = source;
        }

        @Override
        public void run() {
            try {
                List<?> objectsToDump = source.objectsToDump();
                if (objectsToDump == null || objectsToDump.isEmpty()) {
                    return;
                }
                LogFileManager fileManager = new LogFileManager();
                String baseDumpFileName = objectsToDump.get(0).getClass().getSimpleName().toLowerCase() + "." + dumpFileExtension;
                String dumpFileName = fileManager.suffixFileName(baseDumpFileName, fileManager.getTimestampSuffix(System.currentTimeMillis()));
                dumpFileName = fileManager.getValidFileName(new File(dumpDirectory), dumpFileName, null);
                fileManager.writeListToFile(objectsToDump, new File(dumpDirectory, dumpFileName));
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
