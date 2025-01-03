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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.util.ThreadUtil;

import java.util.Objects;

public class NetworkTaskMainUIBroadcastReceiver extends BroadcastReceiver {

    private final NetworkTaskMainActivity mainActivity;
    private final NetworkTaskAdapter adapter;

    public NetworkTaskMainUIBroadcastReceiver(NetworkTaskMainActivity mainActivity, NetworkTaskAdapter adapter) {
        this.mainActivity = mainActivity;
        this.adapter = adapter;
    }

    @Override
    @SuppressWarnings("NotifyDataSetChanged")
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(context.getResources().getString(R.string.sync_action_key));
        String notifyAction = context.getResources().getString(R.string.sync_action_notify);
        if (notifyAction.equals(action)) {
            Log.d(NetworkTaskMainUIBroadcastReceiver.class.getName(), "Received request with notify action. Refreshing UI.");
            adapter.notifyDataSetChanged();
            return;
        }
        NetworkTask task = new NetworkTask(Objects.requireNonNull(intent.getExtras()));
        Log.d(NetworkTaskMainUIBroadcastReceiver.class.getName(), "Received request for " + task);
        NetworkTaskDAO dao = new NetworkTaskDAO(mainActivity);
        NetworkTask databaseTask = dao.readNetworkTask(task.getId());
        if (isNetworkTaskValid(task, databaseTask)) {
            doSync(databaseTask);
        } else {
            Log.d(NetworkTaskMainUIBroadcastReceiver.class.getName(), "Task " + task + " is invalid. Skipping sync.");
        }
    }

    protected void doSync(NetworkTask task) {
        Log.d(NetworkTaskMainUIBroadcastReceiver.class.getName(), "doSync, task is " + task);
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(mainActivity, task, adapter);
        ThreadUtil.execute(syncTask);
    }

    private boolean isNetworkTaskValid(NetworkTask task, NetworkTask databaseTask) {
        return databaseTask != null && task.getSchedulerId() == databaseTask.getSchedulerId();
    }
}
