package de.ibba.keepitup.logging;

import com.google.common.base.Charsets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileLogger implements ILogger {

    private final static LogLevel DEFAULT_MAX_LEVEL = LogLevel.DEBUG;
    private final static int DEFAULT_MAX_FILE_SIZE = 1024 * 1024;
    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 50;
    private final static String DEFAULT_LOG_FILE_BASE_NAME = "keepitup.log";

    private final static int LOG_QUEUE_PUT_TIMEOUT = 500;
    private final static int LOG_QUEUE_TAKE_TIMEOUT = 60000;

    private final LogLevel maxLevel;
    private final int maxFileSize;
    private final int archiveFileCount;
    private final String logDirectory;
    private final String logFileName;

    private final LinkedBlockingQueue<LogFileEntry> logQueue;
    private AtomicBoolean logThreadActive;

    private ExecutorService logThreadExecutor;

    public FileLogger(String logDirectory) {
        this(DEFAULT_MAX_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(int maxFileSize, String logDirectory) {
        this(DEFAULT_MAX_LEVEL, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(String logDirectory, String logFileName) {
        this(DEFAULT_MAX_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, logFileName);
    }

    public FileLogger(int maxFileSize, int archiveFileCount, String logDirectory) {
        this(DEFAULT_MAX_LEVEL, maxFileSize, archiveFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(LogLevel maxLevel, String logDirectory) {
        this(maxLevel, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(LogLevel maxLevel, int maxFileSize, String logDirectory) {
        this(maxLevel, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(LogLevel maxLevel, String logDirectory, String logFileName) {
        this(maxLevel, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, logFileName);
    }

    public FileLogger(LogLevel maxLevel, int maxFileSize, int archiveFileCount, String logDirectory) {
        this(maxLevel, maxFileSize, archiveFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(LogLevel maxLevel, int maxFileSize, int archiveFileCount, String logDirectory, String logFileName) {
        this.maxLevel = maxLevel;
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
        if (level == null || level.getLevel() < maxLevel.getLevel()) {
            return;
        }
        if (tag == null || message == null) {
            return;
        }
        try {
            LogFileEntry logEntry = new LogFileEntry(System.currentTimeMillis(), Thread.currentThread().getName(), level, tag, message, throwable);
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
        OutputStream logstream = null;
        synchronized (FileLogger.class) {
            try {
                File logFile = new File(logDirectory, logFileName);
                long fileSize = 0;
                if (logFile.exists()) {
                    fileSize = logFile.length();
                }
                logstream = initializeLogStream(logFile);
                LogFileManager fileManager = new LogFileManager();
                LogFormatter formatter = new LogFormatter();
                LogFileEntry entry;
                while ((entry = logQueue.poll(LOG_QUEUE_TAKE_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
                    byte[] message = formatter.formatLogFileEntry(entry, Charsets.UTF_8);
                    logstream.write(message);
                    fileSize += message.length;
                    if (fileSize >= maxFileSize) {
                        closeLogStream(logstream);
                        String newFileName = fileManager.getValidFileName(new File(logDirectory), logFileName, System.currentTimeMillis());
                        if (newFileName != null) {
                            if (logFile.renameTo(new File(newFileName))) {
                                logFile = new File(logDirectory, logFileName);
                                fileSize = 0;
                                logstream = initializeLogStream(logFile);
                            }
                        }
                    }
                }
            } catch (Exception exc) {
                //Do nothing
            } finally {
                logThreadActive.set(false);
                closeLogStream(logstream);
            }
        }
    }

    private OutputStream initializeLogStream(File logFile) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(logFile, true));
    }

    private void closeLogStream(OutputStream logstream) {
        try {
            if (logstream != null) {
                logstream.flush();
                logstream.close();
            }
        } catch (Exception exc) {
            //Do nothing
        }
    }
}
