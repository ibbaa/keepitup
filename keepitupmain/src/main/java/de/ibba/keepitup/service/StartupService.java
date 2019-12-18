package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.ibba.keepitup.BuildConfig;
import de.ibba.keepitup.notification.NotificationHandler;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.util.DebugUtil;

public class StartupService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(StartupService.class.getName(), "onReceive.");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(StartupService.class.getName(), "Received system boot event. Startup.");
            startup(context);
        } else {
            Log.e(StartupService.class.getName(), "The received intent is not Intent.ACTION_BOOT_COMPLETED.");
        }
    }

    public void startup(Context context) {
        Log.d(StartupService.class.getName(), "Starting application.");
        if (BuildConfig.DEBUG) {
            Log.d(StartupService.class.getName(), "Debug version. Initialize logging.");
            try {
                PreferenceManager preferenceManager = new PreferenceManager(context);
                IFileManager fileManager = new SystemFileManager(context);
                boolean isFileLoggerEnabled = preferenceManager.getPreferenceFileLoggerEnabled();
                boolean isFileDumpEnabled = preferenceManager.getPreferenceFileDumpEnabled();
                if (isFileLoggerEnabled) {
                    de.ibba.keepitup.logging.Log.initialize(DebugUtil.getFileLogger(context, fileManager));
                } else {
                    de.ibba.keepitup.logging.Log.initialize(null);
                }
                if (isFileDumpEnabled) {
                    de.ibba.keepitup.logging.Dump.initialize(DebugUtil.getFileDump(context, fileManager));
                } else {
                    de.ibba.keepitup.logging.Dump.initialize(null);
                }
            } catch (Exception exc) {
                Log.e(StartupService.class.getName(), "Error initializing logging.", exc);
            }
        } else {
            Log.d(StartupService.class.getName(), "Release version. Disable logging.");
            de.ibba.keepitup.logging.Log.initialize(null);
            de.ibba.keepitup.logging.Dump.initialize(null);
        }
        try {
            Log.d(StartupService.class.getName(), "Init notification channels.");
            new NotificationHandler(context);
            Log.d(StartupService.class.getName(), "Starting pending network tasks.");
            NetworkTaskProcessServiceScheduler scheduler = new NetworkTaskProcessServiceScheduler(context);
            scheduler.startup();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on starting pending network tasks.", exc);
        }
        try {
            Log.d(StartupService.class.getName(), "Deleting internal download files.");
            IFileManager fileManager = new SystemFileManager(context);
            fileManager.delete(fileManager.getInternalDownloadDirectory());
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on deleting internal download files", exc);
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
