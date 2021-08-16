package net.ibbaa.keepitup.ui.sync;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.util.ThreadUtil;

public class NetworkTaskMainUIBroadcastReceiver extends BroadcastReceiver {

    private final Activity activity;
    private final NetworkTaskAdapter adapter;

    public NetworkTaskMainUIBroadcastReceiver(Activity activity, NetworkTaskAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskMainUIBroadcastReceiver.class.getName(), "Received request for " + task);
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(activity, task, adapter);
        ThreadUtil.exexute(syncTask);
    }
}
