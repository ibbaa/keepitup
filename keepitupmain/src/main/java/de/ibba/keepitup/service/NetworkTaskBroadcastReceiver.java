package de.ibba.keepitup.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Received request for " + task);
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Rescheduling " + task);
        NetworkTaskServiceScheduler scheduler = new NetworkTaskServiceScheduler(context);
        scheduler.reschedule(task, false);
        doWork(task);
    }

    private void doWork(NetworkTask task) {
        Log.d(NetworkTaskBroadcastReceiver.class.getName(), "Doing work for " + task);
    }
}
