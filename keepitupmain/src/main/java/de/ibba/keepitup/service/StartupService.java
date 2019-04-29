package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(StartupService.class.getName(), "received system boot event");
        startup(context);
    }

    public void startup(Context context) {
        try {
            Log.d(StartupService.class.getName(), "Starting application.");
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
