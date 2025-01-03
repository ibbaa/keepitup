/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui.sync;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;
import net.ibbaa.keepitup.util.ThreadUtil;

import java.util.Objects;

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
        ThreadUtil.execute(syncTask);
    }
}
