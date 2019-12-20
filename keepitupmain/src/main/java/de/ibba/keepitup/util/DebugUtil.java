package de.ibba.keepitup.util;

import android.content.Context;

import java.io.File;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.FileDump;
import de.ibba.keepitup.logging.FileLogger;
import de.ibba.keepitup.logging.IDump;
import de.ibba.keepitup.logging.ILogger;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.logging.LogLevel;
import de.ibba.keepitup.service.IFileManager;

public class DebugUtil {

    public static ILogger getFileLogger(Context context, IFileManager fileManager) {
        Log.d(DebugUtil.class.getName(), "getFileLogger");
        String maxLogLevelText = context.getResources().getString(R.string.file_logger_log_level_default);
        LogLevel maxLogLevel;
        try {
            maxLogLevel = LogLevel.valueOf(maxLogLevelText);
        } catch (IllegalArgumentException exc) {
            Log.e(DebugUtil.class.getName(), "Error accessing debug level " + maxLogLevelText, exc);
            maxLogLevel = LogLevel.DEBUG;
        }
        int maxLogFileSize = context.getResources().getInteger(R.integer.file_logger_max_file_size_default);
        int archiveFileCount = context.getResources().getInteger(R.integer.file_logger_archive_file_count_default);
        String relativeLogDirectory = context.getResources().getString(R.string.file_logger_log_directory_default);
        File logDirectoryFile = fileManager.getExternalDirectory(relativeLogDirectory);
        if (logDirectoryFile == null) {
            Log.e(DebugUtil.class.getName(), "Error accessing log folder.");
            return null;
        }
        String logDirectory = logDirectoryFile.getAbsolutePath();
        String logFileName = context.getResources().getString(R.string.file_logger_log_file_base_name_default);
        Log.d(DebugUtil.class.getName(), "maxLogLevel is " + maxLogLevel.name());
        Log.d(DebugUtil.class.getName(), "maxLogFileSize is " + maxLogFileSize);
        Log.d(DebugUtil.class.getName(), "archiveFileCount is " + archiveFileCount);
        Log.d(DebugUtil.class.getName(), "logDirectory is " + logDirectory);
        Log.d(DebugUtil.class.getName(), "logFileName is " + logFileName);
        return new FileLogger(maxLogLevel, maxLogFileSize, archiveFileCount, logDirectory, logFileName);
    }

    public static IDump getFileDump(Context context, IFileManager fileManager) {
        Log.d(DebugUtil.class.getName(), "getFileDump");
        String relativeDumpDirectory = context.getResources().getString(R.string.file_dump_dump_directory_default);
        File dumpDirectoryFile = fileManager.getExternalDirectory(relativeDumpDirectory);
        if (dumpDirectoryFile == null) {
            Log.e(DebugUtil.class.getName(), "Error accessing dump folder.");
            return null;
        }
        String dumpDirectory = dumpDirectoryFile.getAbsolutePath();
        int archiveFileCount = context.getResources().getInteger(R.integer.file_dump_archive_file_count_default);
        String dumpFileExtension = context.getResources().getString(R.string.file_dump_dump_file_extension_default);
        String emptyMessage = context.getResources().getString(R.string.file_dump_empty_message_default);
        Log.d(DebugUtil.class.getName(), "dumpDirectory is " + dumpDirectory);
        Log.d(DebugUtil.class.getName(), "archiveFileCount is " + archiveFileCount);
        Log.d(DebugUtil.class.getName(), "dumpFileExtension is " + dumpFileExtension);
        return new FileDump(dumpDirectory, archiveFileCount, dumpFileExtension, emptyMessage);
    }
}
