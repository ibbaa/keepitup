package de.ibba.keepitup.main;

import android.app.Application;
import android.util.Log;

import de.ibba.keepitup.service.StartupService;

public class KeepItUpApplication extends Application {

    @Override
    public void onCreate() {
        Log.d(KeepItUpApplication.class.getName(), "onCreate");
        super.onCreate();
        de.ibba.keepitup.logging.Log.initialize(null);
        StartupService startupService = new StartupService();
        startupService.startup(this);
    }

    @Override
    public void onTerminate() {
        Log.d(KeepItUpApplication.class.getName(), "onTerminate");
        super.onTerminate();
        StartupService startupService = new StartupService();
        startupService.terminate(this);
    }
}
