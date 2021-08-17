/*
 * Copyright (c) 2021. Alwin Ibba
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class FileLogger implements ILogger {

    private final static LogLevel DEFAULT_LOG_LEVEL = LogLevel.DEBUG;
    private final static int DEFAULT_MAX_FILE_SIZE = 1024 * 1024 * 10;
    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 50;
    private final static String DEFAULT_LOG_FILE_BASE_NAME = "keepitup.log";

    private final static int LOG_QUEUE_PUT_TIMEOUT = 500;
    private final static int LOG_QUEUE_TAKE_TIMEOUT = 1000;

    private final static ReentrantLock loggerLock = new ReentrantLock();

    private final LogLevel maxLevel;
    private final int maxFileSize;
    private final int archiveFileCount;
    private final String logDirectory;
    private final String logFileName;

    private final LinkedBlockingQueue<LogFileEntry> logQueue;
    private final AtomicBoolean logThreadActive;

    public FileLogger(String logDirectory) {
        this(DEFAULT_LOG_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(int maxFileSize, String logDirectory) {
        this(DEFAULT_LOG_LEVEL, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
    }

    public FileLogger(String logDirectory, String logFileName) {
        this(DEFAULT_LOG_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, logDirectory, logFileName);
    }

    public FileLogger(int maxFileSize, int archiveFileCount, String logDirectory) {
        this(DEFAULT_LOG_LEVEL, maxFileSize, archiveFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME);
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
            if (logThreadActive.compareAndSet(false, true)) {
                Thread logThread = new Thread(this::doLog);
                logThread.start();
            }
        } catch (InterruptedException exc) {
            //Do nothing
        }
    }

    private void doLog() {
        OutputStream logStream = null;
        try {
            loggerLock.lock();
            File logFolder = new File(logDirectory);
            if (!logFolder.exists()) {
                logFolder.mkdirs();
            }
            File logFile = new File(logFolder, logFileName);
            long fileSize = 0;
            if (logFile.exists()) {
                fileSize = logFile.length();
            }
            logStream = initializeLogStream(logFile);
            LogFileManager fileManager = new LogFileManager();
            LogFormatter formatter = new LogFormatter();
            LogFileEntry entry;
            while ((entry = logQueue.poll(LOG_QUEUE_TAKE_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
                byte[] message = formatter.formatLogFileEntry(entry, Charsets.UTF_8);
                logStream.write(message);
                fileSize += message.length;
                if (fileSize >= maxFileSize) {
                    closeLogStream(logStream);
                    String newFileName = fileManager.getValidFileName(new File(logDirectory), logFileName, System.currentTimeMillis());
                    if (newFileName != null) {
                        if (logFile.renameTo(new File(new File(logDirectory), newFileName))) {
                            logFile = new File(logDirectory, logFileName);
                            fileSize = 0;
                            logStream = initializeLogStream(logFile);
                            Housekeeper housekeeper = new Housekeeper(logDirectory, logFileName, archiveFileCount, this::shouldBeArchived);
                            Thread housekeeperThread = new Thread(housekeeper);
                            housekeeperThread.start();
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        } catch (Exception exc) {
            //Do nothing
        } finally {
            logThreadActive.set(false);
            closeLogStream(logStream);
            loggerLock.unlock();
        }
    }

    private boolean shouldBeArchived(File dir, String name) {
        if (logFileName.equals(name)) {
            return false;
        }
        LogFileManager fileManager = new LogFileManager();
        String logFileBaseName = fileManager.getFileNameWithoutExtension(logFileName);
        String logFileSuffix = fileManager.getFileNameExtension(logFileName);
        return name.startsWith(logFileBaseName) && name.endsWith(logFileSuffix);
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
