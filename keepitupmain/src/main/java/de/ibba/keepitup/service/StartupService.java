package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.ibba.keepitup.notification.NotificationHandler;

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
        de.ibba.keepitup.logging.Log.initialize(null);
        de.ibba.keepitup.logging.Dump.initialize(null);
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
