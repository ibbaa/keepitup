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

package net.ibbaa.keepitup.test.mock;

import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIBroadcastReceiver;

public class TestNetworkTaskMainUIBroadcastReceiver extends NetworkTaskMainUIBroadcastReceiver {

    private NetworkTask task;

    public TestNetworkTaskMainUIBroadcastReceiver(NetworkTaskMainActivity activity, NetworkTaskAdapter adapter) {
        super(activity, adapter);
    }

    public NetworkTask getDoSyncTask() {
        return task;
    }

    @Override
    protected void doSync(NetworkTask task) {
        this.task = task;
    }
}
