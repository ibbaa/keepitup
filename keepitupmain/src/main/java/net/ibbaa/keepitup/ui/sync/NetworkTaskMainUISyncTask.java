/*
 * Copyright (c) 2024. Alwin Ibba
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
import android.content.Context;

import net.ibbaa.keepitup.db.AccessTypeDataDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;

import java.lang.ref.WeakReference;

public class NetworkTaskMainUISyncTask extends UIBackgroundTask<NetworkTaskUIWrapper> {

    private final WeakReference<NetworkTaskAdapter> adapterRef;
    private final NetworkTask networkTask;

    public NetworkTaskMainUISyncTask(Activity activity, NetworkTask networkTask, NetworkTaskAdapter adapter) {
        super(activity);
        this.networkTask = networkTask;
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        } else {
            this.adapterRef = null;
        }
    }

    @Override
    protected NetworkTaskUIWrapper runInBackground() {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "runInBackground");
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "Reading log entry for network task " + networkTask);
        try {
            Context context = getActivity();
            if (context != null) {
                LogDAO logDAO = new LogDAO(context);
                LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(networkTask.getId());
                AccessTypeDataDAO accessTypeDataDAO = new AccessTypeDataDAO(context);
                AccessTypeData data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(networkTask.getId());
                if (data == null) {
                    Log.d(NetworkTaskMainActivity.class.getName(), "Database returned null for access type data. Creating new one.");
                    data = new AccessTypeData(context);
                    data.setNetworkTaskId(networkTask.getId());
                    accessTypeDataDAO.insertAccessTypeData(data);
                }
                return new NetworkTaskUIWrapper(networkTask, data, logEntry);
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error reading log entry for network task " + networkTask, exc);
        }
        return null;
    }

    @Override
    protected void runOnUIThread(NetworkTaskUIWrapper networkTaskWrapper) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "runOnUIThread, networkTaskWrapper is " + networkTaskWrapper);
        if (networkTaskWrapper == null || adapterRef == null) {
            return;
        }
        NetworkTaskAdapter adapter = adapterRef.get();
        if (adapter != null) {
            try {
                Log.d(NetworkTaskMainUISyncTask.class.getName(), "Updating adapter with network task ui wrapper " + networkTaskWrapper);
                int replacePosition = adapter.replaceItem(networkTaskWrapper);
                if (replacePosition >= 0) {
                    adapter.notifyItemChanged(replacePosition);
                }
            } catch (Exception exc) {
                Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error updating adapter with network task ui wrapper " + networkTaskWrapper, exc);
            }
        }
    }

}
