package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.ibba.keepitup.BuildConfig;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.db.SchedulerIdHistoryDAO;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.model.SchedulerId;
import de.ibba.keepitup.notification.NotificationHandler;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.util.DebugUtil;

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
        Log.d(StartupService.class.getName(), "Initialize scheduler.");
        initializeScheduler(context);
        Log.d(StartupService.class.getName(), "Cleanup logs");
        cleanupLogs(context);
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
                dumpDatabase("Dump on application startup", context);
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
            new NotificationHandler(context);
            Log.d(StartupService.class.getName(), "Starting pending network tasks.");
            NetworkTaskProcessServiceScheduler scheduler = new NetworkTaskProcessServiceScheduler(context);
            scheduler.startup();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on starting pending network tasks.", exc);
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

    private void dumpDatabase(String message, Context context) {
        if (BuildConfig.DEBUG) {
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
    }
}
