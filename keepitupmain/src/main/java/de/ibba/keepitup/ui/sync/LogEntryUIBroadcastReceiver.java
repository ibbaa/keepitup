package de.ibba.keepitup.ui.sync;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;
import de.ibba.keepitup.util.ThreadUtil;

public class LogEntryUIBroadcastReceiver extends BroadcastReceiver {

    private final Activity activity;
    private final LogEntryAdapter adapter;

    public LogEntryUIBroadcastReceiver(Activity activity, LogEntryAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(LogEntryUIBroadcastReceiver.class.getName(), "Received request for " + task);
        if (task.getId() == adapter.getNetworkTask().getId()) {
            doSync(task);
        } else {
            Log.d(LogEntryUIBroadcastReceiver.class.getName(), "The received request task does not match the adapter task. Skipping sync.");
        }
    }

    protected void doSync(NetworkTask task) {
        Log.d(LogEntryUIBroadcastReceiver.class.getName(), "doSync, task is " + task);
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(activity, task, adapter);
        ThreadUtil.exexute(syncTask);
    }
}
