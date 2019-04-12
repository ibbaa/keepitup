package de.ibba.keepitup.resources;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

public class AndroidServiceFactory implements ServiceFactory {

    @Override
    public AlarmManager createAlarmManager(Context context) {
        Log.d(AndroidServiceFactory.class.getName(), "createAlarmManager");
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public Handler createHandler() {
        Log.d(AndroidServiceFactory.class.getName(), "createHandler");
        return new Handler();
    }

    @Override
    public UISyncAsyncTask createUISyncAsyncTask() {
        Log.d(AndroidServiceFactory.class.getName(), "createUISyncAsyncTask");
        return new UISyncAsyncTask();
    }
}
