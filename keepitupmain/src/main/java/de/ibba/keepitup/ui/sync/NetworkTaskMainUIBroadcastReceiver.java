package de.ibba.keepitup.ui.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;

public class NetworkTaskMainUIBroadcastReceiver extends BroadcastReceiver {

    private final NetworkTaskAdapter adapter;

    public NetworkTaskMainUIBroadcastReceiver(NetworkTaskAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskMainUIBroadcastReceiver.class.getName(), "Received request for " + task);
        LogDAO logDAO = new LogDAO(context);
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask();
        syncTask.start(new NetworkTaskMainUISyncHolder(task, adapter, logDAO));
    }
}
