package net.ibbaa.keepitup.logging;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.phonelog.Charsets;
import net.ibbaa.phonelog.ILogFormatter;
import net.ibbaa.phonelog.ILogger;
import net.ibbaa.phonelog.LogFileEntry;
import net.ibbaa.phonelog.LogFileManager;
import net.ibbaa.phonelog.LogLevel;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class DocumentFileLogger implements ILogger {

    private final static int LOG_QUEUE_PUT_TIMEOUT = 500;
    private final static int LOG_QUEUE_TAKE_TIMEOUT = 1000;
    private final static String UNKNOWN_MIME_TYPE = "unknown/unknown";

    private final static ReentrantLock loggerLock = new ReentrantLock();

    private final Context context;

    private final LogLevel maxLevel;
    private final int maxFileSize;
    private final int archiveFileCount;
    private final int deleteFileCount;
    private final String logDirectory;
    private final String logFileName;
    private final ILogFormatter logFormatter;
    private final ILogger delegateLog;

    private final LinkedBlockingQueue<LogFileEntry> logQueue;
    private final AtomicBoolean logThreadActive;

    public DocumentFileLogger(Context context, LogLevel maxLevel, int maxFileSize, int archiveFileCount, int deleteFileCount,
                              String logDirectory, String logFileName, ILogFormatter logFormatter, ILogger delegateLog) {
        this.context = context;
        this.maxLevel = maxLevel;
        this.maxFileSize = maxFileSize;
        this.archiveFileCount = archiveFileCount;
        this.deleteFileCount = deleteFileCount;
        this.logDirectory = logDirectory;
        this.logFileName = logFileName;
        this.logFormatter = logFormatter;
        this.delegateLog = delegateLog;
        this.logQueue = new LinkedBlockingQueue<>();
        this.logThreadActive = new AtomicBoolean(false);
    }

    @Override
    public void log(String tag, String message, Throwable throwable, LogLevel level) {
        if (delegateLog != null) {
            delegateLog.log(tag, message, throwable, level);
        }
        if (level == null || level.getLevel() < maxLevel.getLevel()) {
            return;
        }
        if (message == null) {
            return;
        }
        try {
            LogFileEntry logEntry = new LogFileEntry(System.currentTimeMillis(), Thread.currentThread().getName(),
                    level, tag, message, throwable);
            logQueue.offer(logEntry, LOG_QUEUE_PUT_TIMEOUT, TimeUnit.MILLISECONDS);
            if (logThreadActive.compareAndSet(false, true)) {
                Thread logThread = new Thread(this::doLog);
                logThread.start();
            }
        } catch (InterruptedException exc) {
            // Do nothing
        }
    }

    private void doLog() {
        OutputStream logStream = null;
        ParcelFileDescriptor logFileDescriptor = null;
        try {
            loggerLock.lock();
            DocumentFile documentLogDirectory = DocumentFile.fromTreeUri(getContext(), Uri.parse(logDirectory));
            if (documentLogDirectory == null) {
                return;
            }
            DocumentFile documentLogFile = getLogFile(documentLogDirectory, logFileName);
            long fileSize = documentLogFile.length();
            logFileDescriptor = getLogFileDescriptor(documentLogFile);
            logStream = initializeLogStream(logFileDescriptor);
            DocumentFileManager fileManager = new DocumentFileManager();
            LogFileEntry entry;
            while ((entry = logQueue.poll(LOG_QUEUE_TAKE_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
                byte[] message = logFormatter.formatLogFileEntry(entry, Charsets.UTF8_CHARSET);
                logStream.write(message);
                fileSize += message.length;
                if (fileSize >= maxFileSize) {
                    closeLogStream(logFileDescriptor, logStream);
                    String newFileName = fileManager.getValidFileName(documentLogDirectory, logFileName, System.currentTimeMillis());
                    if (newFileName != null) {
                        if (documentLogFile.renameTo(newFileName)) {
                            documentLogFile = getLogFile(documentLogDirectory, logFileName);
                            fileSize = documentLogFile.length();
                            logFileDescriptor = getLogFileDescriptor(documentLogFile);
                            logStream = initializeLogStream(logFileDescriptor);
                            if (archiveFileCount > 0) {
                                DocumentFileHousekeeper housekeeper = new DocumentFileHousekeeper(getContext(), logDirectory, logFileName, archiveFileCount, deleteFileCount, this::shouldBeArchived);
                                Thread housekeeperThread = new Thread(housekeeper);
                                housekeeperThread.start();
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        } catch (Exception exc) {
            // Do nothing
        } finally {
            logThreadActive.set(false);
            closeLogStream(logFileDescriptor, logStream);
            loggerLock.unlock();
        }
    }

    private boolean shouldBeArchived(String name) {
        if (logFileName.equals(name)) {
            return false;
        }
        LogFileManager fileManager = new LogFileManager();
        String logFileBaseName = fileManager.getFileNameWithoutExtension(logFileName);
        String logFileSuffix = fileManager.getFileNameExtension(logFileName);
        return name.startsWith(logFileBaseName) && name.endsWith(logFileSuffix);
    }

    private ParcelFileDescriptor getLogFileDescriptor(DocumentFile documentLogFile) throws IOException {
        return getContext().getContentResolver().openFileDescriptor(documentLogFile.getUri(), "wa");
    }

    private OutputStream initializeLogStream(ParcelFileDescriptor documentFileDescriptor) {
        return new BufferedOutputStream(new FileOutputStream(documentFileDescriptor.getFileDescriptor()));
    }

    private void closeLogStream(ParcelFileDescriptor documentFileDescriptor, OutputStream logstream) {
        try {
            if (logstream != null) {
                logstream.flush();
                logstream.close();
            }
        } catch (Exception exc) {
            // Do nothing
        }
        try {
            if (documentFileDescriptor != null) {
                documentFileDescriptor.close();
            }
        } catch (Exception exc) {
            // Do nothing
        }
    }

    private DocumentFile getLogFile(DocumentFile documentLogDirectory, String fileName) {
        DocumentFile documentLogFile = documentLogDirectory.findFile(fileName);
        if (documentLogFile == null) {
            documentLogFile = documentLogDirectory.createFile(UNKNOWN_MIME_TYPE, fileName);
        }
        return documentLogFile;
    }

    private Context getContext() {
        return context;
    }
}
