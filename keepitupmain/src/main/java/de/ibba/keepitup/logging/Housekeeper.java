package de.ibba.keepitup.logging;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Housekeeper implements Runnable {

    private final static ReentrantLock housekeepingLock = new ReentrantLock();

    private final String directory;
    private final String baseFileName;
    private final int archiveFileCount;
    private final FilenameFilter filter;

    public Housekeeper(String directory, String baseFileName, int archiveFileCount, FilenameFilter filter) {
        this.directory = directory;
        this.baseFileName = baseFileName;
        this.archiveFileCount = archiveFileCount;
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            housekeepingLock.lock();
            File[] filesToArchive;
            if (filter == null) {
                filesToArchive = new File(directory).listFiles();
            } else {
                filesToArchive = new File(directory).listFiles(filter);
            }
            if (filesToArchive != null && filesToArchive.length >= archiveFileCount) {
                LogFileManager fileManager = new LogFileManager();
                String zipFileName = fileManager.suffixFileName(baseFileName, fileManager.getTimestampSuffix(System.currentTimeMillis()));
                zipFileName = fileManager.getValidFileName(new File(directory), zipFileName, null);
                fileManager.zipFiles(Arrays.asList(filesToArchive), new File(directory, zipFileName));
            }
        } catch (Exception exc) {
            //Do nothing
        } finally {
            housekeepingLock.unlock();
        }
    }
}
