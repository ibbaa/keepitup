package de.ibba.keepitup.resources;

import android.content.Context;

import de.ibba.keepitup.service.IAlarmManager;
import de.ibba.keepitup.ui.sync.IHandler;
import de.ibba.keepitup.ui.sync.UISyncAsyncTask;

public interface ServiceFactory {

    IAlarmManager createAlarmManager(Context context);

    IHandler createHandler();

    UISyncAsyncTask createUISyncAsyncTask();
}
