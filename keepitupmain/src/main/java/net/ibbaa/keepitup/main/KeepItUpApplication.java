package net.ibbaa.keepitup.main;

import android.app.Application;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.StartupService;

public class KeepItUpApplication extends Application {

    @Override
    public void onCreate() {
        Log.d(KeepItUpApplication.class.getName(), "onCreate");
        super.onCreate();
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