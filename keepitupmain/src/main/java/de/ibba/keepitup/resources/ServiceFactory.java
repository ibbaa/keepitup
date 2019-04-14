package de.ibba.keepitup.resources;

import android.content.Context;
import android.os.Handler;

import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    Handler createHandler();

    UISyncAsyncTask createUISyncAsyncTask();
}
