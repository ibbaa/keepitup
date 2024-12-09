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

package net.ibbaa.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.db.DBOpenHelper;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.SchedulerIdHistoryDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerId;
import net.ibbaa.keepitup.notification.NotificationHandler;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.ui.permission.PermissionManager;
import net.ibbaa.keepitup.util.DebugUtil;

public class StartupService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(StartupService.class.getName(), "onReceive.");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(StartupService.class.getName(), "Received system boot event.");
        } else {
            Log.e(StartupService.class.getName(), "The received intent is not Intent.ACTION_BOOT_COMPLETED.");
        }
    }

    public void startup(Context context) {
        Log.d(StartupService.class.getName(), "Starting application.");
        initializeDatabase(context);
        if (BuildConfig.DEBUG) {
            Log.d(StartupService.class.getName(), "Debug version. Initialize logging.");
            initializeLogging(context);
        } else {
            Log.d(StartupService.class.getName(), "Release version. Disable logging.");
            Log.initialize(null);
            Dump.initialize(null);
        }
        Log.d(StartupService.class.getName(), "Checking active instances");
        if (!NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().hasActive()) {
            Log.d(StartupService.class.getName(), "There are no active instances");
            Log.d(StartupService.class.getName(), "Reset instances");
            resetInstances(context);
            Log.d(StartupService.class.getName(), "Cleanup files");
            cleanupFiles(context);
        }
        Log.d(StartupService.class.getName(), "Cleanup logs");
        cleanupLogs(context);
        Log.d(StartupService.class.getName(), "Initialize scheduler.");
        initializeScheduler(context);
        initializeTheme(context);
    }

    private void initializeDatabase(Context context) {
        Log.d(StartupService.class.getName(), "initializeDatabase");
        DBOpenHelper.getInstance(context).getWritableDatabase();
    }

    private void shutdownDatabase(Context context) {
        Log.d(StartupService.class.getName(), "shutdownDatabase");
        try {
            DBOpenHelper.getInstance(context).getWritableDatabase().close();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error shutting down database", exc);
        }
    }

    private void initializeLogging(Context context) {
        Log.d(StartupService.class.getName(), "initializeLogging");
        try {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            IFileManager fileManager = new SystemFileManager(context);
            boolean isFileLoggerEnabled = preferenceManager.getPreferenceFileLoggerEnabled();
            boolean isFileDumpEnabled = preferenceManager.getPreferenceFileDumpEnabled();
            if (isFileLoggerEnabled) {
                Log.initialize(DebugUtil.getFileLogger(context, fileManager));
            } else {
                Log.initialize(null);
            }
            if (isFileDumpEnabled) {
                Dump.initialize(DebugUtil.getFileDump(context, fileManager));
                dumpDatabase(context);
            } else {
                Dump.initialize(null);
            }
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error initializing logging.", exc);
        }
    }

    private void initializeScheduler(Context context) {
        Log.d(StartupService.class.getName(), "initializeScheduler");
        try {
            Log.d(StartupService.class.getName(), "Init notification channels.");
            new NotificationHandler(context, new PermissionManager());
            Log.d(StartupService.class.getName(), "Starting scheduler.");
            TimeBasedSuspensionScheduler scheduler = new TimeBasedSuspensionScheduler(context);
            scheduler.restart();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on starting scheduler.", exc);
        }
    }

    private void initializeTheme(Context context) {
        Log.d(StartupService.class.getName(), "initializeTheme");
        try {
            PreferenceManager preferenceManager = new PreferenceManager(context);
            IThemeManager themeManager = new SystemThemeManager();
            int themeCode = preferenceManager.getPreferenceTheme();
            Log.d(StartupService.class.getName(), "theme is " + themeManager.getThemeName(themeCode));
            themeManager.setThemeByCode(themeCode);
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error initializing theme.", exc);
        }
    }

    private void resetInstances(Context context) {
        Log.d(StartupService.class.getName(), "resetInstances");
        try {
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
            networkTaskDAO.resetAllNetworkTaskInstances();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on resetting instances", exc);
        }
    }

    private void cleanupFiles(Context context) {
        Log.d(StartupService.class.getName(), "cleanupFiles");
        try {
            Log.d(StartupService.class.getName(), "Deleting internal download files.");
            IFileManager fileManager = new SystemFileManager(context);
            fileManager.delete(fileManager.getInternalDownloadDirectory());
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on deleting internal download files", exc);
        }
    }

    private void cleanupLogs(Context context) {
        Log.d(StartupService.class.getName(), "cleanupLogs");
        try {
            Log.d(StartupService.class.getName(), "Deleting orphan logs.");
            LogDAO logDAO = new LogDAO(context);
            logDAO.deleteAllOrphanLogs();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on cleaning up logs", exc);
        }
    }

    private void dumpDatabase(Context context) {
        if (BuildConfig.DEBUG) {
            String message = "Dump on application startup";
            NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
            Dump.dump(StartupService.class.getName(), message, NetworkTask.class.getSimpleName().toLowerCase(), networkTaskDAO::readAllNetworkTasks);
            SchedulerIdHistoryDAO historyDAO = new SchedulerIdHistoryDAO(context);
            Dump.dump(StartupService.class.getName(), message, SchedulerId.class.getSimpleName().toLowerCase(), historyDAO::readAllSchedulerIds);
            LogDAO logDAO = new LogDAO(context);
            Dump.dump(StartupService.class.getName(), message, LogEntry.class.getSimpleName().toLowerCase(), logDAO::readAllLogs);
        }
    }

    public void terminate(Context context) {
        Log.d(StartupService.class.getName(), "Terminating application.");
        try {
            Log.d(StartupService.class.getName(), "Stopping pending network tasks.");
            NetworkTaskProcessServiceScheduler scheduler = new NetworkTaskProcessServiceScheduler(context);
            scheduler.terminateAll();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on stopping network tasks.", exc);
        }
        shutdownDatabase(context);
    }
}
