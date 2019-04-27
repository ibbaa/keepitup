package de.ibba.keepitup.main;

import android.app.Application;
import android.util.Log;

import de.ibba.keepitup.service.StartupService;

public class KeepItUpApplication extends Application {

    @Override
    public void onCreate() {
        Log.d(KeepItUpApplication.class.getName(), "onCreate");
        super.onCreate();
        StartupService startupService = new StartupService(this);
        startupService.startup();
    }

    @Override
    public void onTerminate() {
        Log.d(KeepItUpApplication.class.getName(), "onTerminate");
        super.onTerminate();
        StartupService startupService = new StartupService(this);
        startupService.terminate();
    }
}
