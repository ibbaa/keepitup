package de.ibba.keepitup.service;

import android.content.Context;
import android.util.Log;

public class StartupService {

    private final Context context;

    public StartupService(Context context) {
        this.context = context;
    }

    public void startup() {
        try {
            Log.d(StartupService.class.getName(), "Starting application.");
            NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
            scheduler.startup();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on starting pending network tasks.", exc);
        }
    }

    public void terminate() {
        try {
            Log.d(StartupService.class.getName(), "Terminating application.");
            NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
            scheduler.terminateAll();
        } catch (Exception exc) {
            Log.e(StartupService.class.getName(), "Error on stopping network tasks.", exc);
        }
    }
}
