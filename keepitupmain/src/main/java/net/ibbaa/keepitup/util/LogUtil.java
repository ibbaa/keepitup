/*
 * Copyright (c) 2025 Alwin Ibba
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

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.service.IDocumentManager;
import net.ibbaa.keepitup.service.IFileManager;
import net.ibbaa.phonelog.FileLogger;
import net.ibbaa.phonelog.ILogger;
import net.ibbaa.phonelog.LogLevel;
import net.ibbaa.phonelog.PassthroughMessageLogFormatter;
import net.ibbaa.phonelog.android.DocumentFileLogger;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class LogUtil {

    public static ILogger getFileLogger(Context context, IFileManager fileManager, IDocumentManager documentManager, NetworkTask networkTask) {
        Log.d(LogUtil.class.getName(), "getFileLogger");
        if (networkTask.getIndex() < 0) {
            Log.e(LogUtil.class.getName(), "networkTask index is " + networkTask.getIndex() + " which is invalied");
            return null;
        }
        String maxLogLevelText = context.getResources().getString(R.string.networktask_file_logger_log_level_default);
        LogLevel maxLogLevel;
        try {
            maxLogLevel = LogLevel.valueOf(maxLogLevelText);
        } catch (IllegalArgumentException exc) {
            Log.e(LogUtil.class.getName(), "Error accessing debug level " + maxLogLevelText, exc);
            maxLogLevel = LogLevel.DEBUG;
        }
        int maxLogFileSize = context.getResources().getInteger(R.integer.networktask_file_logger_max_file_size_default);
        int archiveFileCount = context.getResources().getInteger(R.integer.networktask_file_logger_archive_file_count_default);
        int deleteFileCount = context.getResources().getInteger(R.integer.networktask_file_logger_delete_file_count_default);
        String logDirectory = getLogDirectory(context, fileManager, documentManager);
        if (logDirectory == null) {
            Log.e(LogUtil.class.getName(), "Error accessing log folder.");
            return null;
        }
        String logFileName = getLogFileName(context, fileManager, networkTask);
        Log.d(LogUtil.class.getName(), "maxLogLevel is " + maxLogLevel.name());
        Log.d(LogUtil.class.getName(), "maxLogFileSize is " + maxLogFileSize);
        Log.d(LogUtil.class.getName(), "archiveFileCount is " + archiveFileCount);
        Log.d(LogUtil.class.getName(), "deleteFileCount is " + deleteFileCount);
        Log.d(LogUtil.class.getName(), "logDirectory is " + logDirectory);
        Log.d(LogUtil.class.getName(), "logFileName is " + logFileName);
        return createLogger(context, maxLogLevel, maxLogFileSize, archiveFileCount, deleteFileCount, logDirectory, logFileName);
    }

    private static ILogger createLogger(Context context, LogLevel maxLogLevel, int maxLogFileSize, int archiveFileCount, int deleteFileCount, String logDirectory, String logFileName) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
            return new DocumentFileLogger(context, maxLogLevel, maxLogFileSize, archiveFileCount, deleteFileCount, logDirectory, logFileName, new PassthroughMessageLogFormatter(), null);
        }
        return new FileLogger(maxLogLevel, maxLogFileSize, archiveFileCount, deleteFileCount, logDirectory, logFileName, new PassthroughMessageLogFormatter(), null);
    }

    private static String getLogDirectory(Context context, IFileManager fileManager, IDocumentManager documentManager) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        if (preferenceManager.getPreferenceAllowArbitraryFileLocation()) {
            String arbitraryLogDirectory = preferenceManager.getPreferenceArbitraryLogFolder();
            DocumentFile logDirectoryFile = documentManager.getArbitraryDirectory(arbitraryLogDirectory);
            if (logDirectoryFile == null) {
                Log.e(LogUtil.class.getName(), "Error accessing arbitrary log folder " + arbitraryLogDirectory);
                return null;
            }
            return logDirectoryFile.getUri().toString();
        }
        String relativeLogDirectory = preferenceManager.getPreferenceLogFolder();
        File logDirectoryFile = FileUtil.getExternalDirectory(fileManager, preferenceManager, relativeLogDirectory);
        if (logDirectoryFile == null) {
            Log.e(LogUtil.class.getName(), "Error accessing relative log folder " + relativeLogDirectory);
            return null;
        }
        return logDirectoryFile.getAbsolutePath();
    }

    public static String getLogFileName(Context context, IFileManager fileManager, NetworkTask networkTask) {
        String baseFileName = context.getResources().getString(R.string.networktask_file_logger_log_file_base_name_default);
        String extension = context.getResources().getString(R.string.networktask_file_logger_log_file_base_extension_default);
        String name = StringUtil.isEmpty(networkTask.getName()) ? context.getResources().getString(R.string.task_name_default) : networkTask.getName();
        if (name.equals(context.getResources().getString(R.string.task_name_default))) {
            name = null;
        }
        return fileManager.getLogFileName(baseFileName, name, extension, networkTask.getSchedulerId(), networkTask.getIndex(), networkTask.getAddress());
    }

    public static String getLogFileKey(Context context, NetworkTask networkTask) {
        String baseFileName = context.getResources().getString(R.string.networktask_file_logger_log_file_base_name_default);
        return baseFileName + "_" + networkTask.getIndex() + "_" + networkTask.getSchedulerId() + "_" + networkTask.getAddress();
    }

    public static String formatLogEntryLog(Context context, NetworkTask task, LogEntry entry) {
        String networkTaskTitle = UIUtil.getTextForNamedTask(context, task);
        String formattedTitleText = context.getResources().getString(R.string.text_activity_log_list_item_log_entry_title, networkTaskTitle);
        String successText = entry.isSuccess() ? context.getResources().getString(R.string.string_successful) : context.getResources().getString(R.string.string_not_successful);
        String formattedSuccessText = context.getResources().getString(R.string.text_activity_log_list_item_log_entry_success, successText);
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(entry.getTimestamp()));
        String formattedTimestampText = context.getResources().getString(R.string.text_activity_log_list_item_log_entry_timestamp, timestampText);
        String formattedMessageText = context.getResources().getString(R.string.text_activity_log_list_item_log_entry_message, entry.getMessage());
        return formattedTitleText + ", " + formattedSuccessText + ", " + formattedTimestampText + ", " + formattedMessageText;
    }
}
