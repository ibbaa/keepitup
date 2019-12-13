package de.ibba.keepitup.logging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileLogger implements ILogger {

    private final static int DEFAULT_MAX_FILE_SIZE = 1024 * 1024;
    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 50;
    private final static String DEFAULT_LOG_FILE_BASE_NAME = "keepitup.log";

    private final static int LOG_QUEUE_PUT_TIMEOUT = 500;
    private final static int LOG_QUEUE_TAKE_TIMEOUT = 60000;
    private final static int LOG_THREAD_INITIAL_DELAY = 60000;

    private final int maxFileSize;
    private final int archiveFileCount;
    private final String logDirectory;
    private final String logFileName;

    private final LinkedBlockingQueue<LogFileEntry> logQueue;
    private AtomicBoolean logThreadActive;

    private ExecutorService logThreadExecutor;

    public FileLogger(String logDirectory) {
        this(DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(int maxFileSize, String logDirectory) {
        this(maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(String logDirectory, String logFileName) {
        this(DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, logFileName);
    }

    public FileLogger(int maxFileSize, int archiveFileCount, String logDirectory) {
        this(maxFileSize, archiveFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(int maxFileSize, int archiveFileCount, String logDirectory, String logFileName) {
        this.maxFileSize = maxFileSize;
        this.archiveFileCount = archiveFileCount;
        this.logDirectory = logDirectory;
        this.logFileName = logFileName;
        this.logQueue = new LinkedBlockingQueue<>();
        this.logThreadActive = new AtomicBoolean(false);
        this.logThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void log(String tag, String message, Throwable throwable, LogLevel level) {
        try {
            LogFileEntry logEntry = new LogFileEntry(System.currentTimeMillis(), level, tag, message, throwable);
            logQueue.offer(logEntry, LOG_QUEUE_PUT_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!logThreadActive.get()) {
                logThreadActive.set(true);
                logThreadExecutor.execute(this::doLog);
            }
        } catch (InterruptedException exc) {
            //Do nothing
        }
    }

    private void doLog() {

    }
}
