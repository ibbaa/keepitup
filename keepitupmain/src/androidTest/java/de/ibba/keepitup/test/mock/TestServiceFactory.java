package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import de.ibba.keepitup.resources.ServiceFactory;
import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

public class TestServiceFactory implements ServiceFactory {

    @Override
    public IAlarmManager createAlarmManager(Context context) {
        Log.d(TestServiceFactory.class.getName(), "createAlarmManager");
        return new MockAlarmManager();
    }

    @Override
    public Handler createHandler() {
        Log.d(TestServiceFactory.class.getName(), "createHandler");
        return new Handler();
    }

    @Override
    public UISyncAsyncTask createUISyncAsyncTask() {
        Log.d(TestServiceFactory.class.getName(), "createUISyncAsyncTask");
        return new UISyncAsyncTask();
    }
}
