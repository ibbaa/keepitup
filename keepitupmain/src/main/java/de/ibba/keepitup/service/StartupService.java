package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.ibba.keepitup.notification.NotificationHandler;

public class StartupService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(StartupService.class.getName(), "Received system boot event. Startup.");
            startup(context);
        } else {
            Log.e(StartupService.class.getName(), "The received intent is not Intent.ACTION_BOOT_COMPLETED.");
        }
    }

    public void startup(Context context) {
        try {
            Log.d(StartupService.class.getName(), "Starting application.");
            new NotificationHandler(context);
            NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
            scheduler.startup();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on starting pending network tasks.", exc);
        }
    }

    public void terminate(Context context) {
        try {
            Log.d(StartupService.class.getName(), "Terminating application.");
            NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
            scheduler.terminateAll();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on stopping network tasks.", exc);
        }
    }
}
