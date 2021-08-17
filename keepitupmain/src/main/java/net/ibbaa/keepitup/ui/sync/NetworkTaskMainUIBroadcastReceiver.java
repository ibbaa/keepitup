/*
 * Copyright (c) 2021. Alwin Ibba
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
