package de.ibba.keepitup.resources;

import android.content.Context;
import android.util.Log;

import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.service.SystemAlarmManager;
import de.ibba.keepitup.ui.sync.IHandler;
import de.ibba.keepitup.ui.sync.SystemHandler;
import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

public class SystemServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(SystemServiceFactory.class.getName(), "createAlarmManager");
        return new SystemAlarmManager(context);
    }

    @Override
    public IHandler createHandler() {
        Log.d(SystemServiceFactory.class.getName(), "createHandler");
        return new SystemHandler();
    }

    @Override
    public UISyncAsyncTask createUISyncAsyncTask() {
        Log.d(SystemServiceFactory.class.getName(), "createUISyncAsyncTask");
        return new UISyncAsyncTask();
    }
}
