package de.ibba.keepitup.resources;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;

import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

public interface ServiceFactory {

    AlarmManager createAlarmManager(Context context);

    Handler createHandler();

    UISyncAsyncTask createUISyncAsyncTask();
}
