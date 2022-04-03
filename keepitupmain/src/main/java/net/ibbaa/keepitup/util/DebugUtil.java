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

package net.ibbaa.keepitup.util;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.DefaultLogFormatter;
import net.ibbaa.keepitup.logging.FileDump;
import net.ibbaa.keepitup.logging.FileLogger;
import net.ibbaa.keepitup.logging.IDump;
import net.ibbaa.keepitup.logging.ILogger;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.logging.LogLevel;
import net.ibbaa.keepitup.service.IFileManager;

import java.io.File;

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
        File logDirectoryFile = fileManager.getExternalDirectory(relativeLogDirectory, 0);
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
        return new FileLogger(maxLogLevel, maxLogFileSize, archiveFileCount, logDirectory, logFileName, new DefaultLogFormatter());
    }

    public static IDump getFileDump(Context context, IFileManager fileManager) {
        Log.d(DebugUtil.class.getName(), "getFileDump");
        String relativeDumpDirectory = context.getResources().getString(R.string.file_dump_dump_directory_default);
        File dumpDirectoryFile = fileManager.getExternalDirectory(relativeDumpDirectory, 0);
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
