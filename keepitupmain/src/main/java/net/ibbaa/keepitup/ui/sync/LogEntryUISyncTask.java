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
import android.content.Context;

import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

import java.lang.ref.WeakReference;

public class LogEntryUISyncTask extends UIBackgroundTask<LogEntry> {

    private final WeakReference<LogEntryAdapter> adapterRef;
    private final NetworkTask networkTask;

    public LogEntryUISyncTask(Activity activity, NetworkTask networkTask, LogEntryAdapter adapter) {
        super(activity);
        this.networkTask = networkTask;
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        } else {
            this.adapterRef = null;
        }
    }

    @Override
    protected LogEntry runInBackground() {
        Log.d(LogEntryUISyncTask.class.getName(), "runInBackground");
        Log.d(LogEntryUISyncTask.class.getName(), "Reading log entry for network task " + networkTask);
        try {
            Context context = getActivity();
            if (context != null) {
                LogDAO logDAO = new LogDAO(context);
                return logDAO.readMostRecentLogForNetworkTask(networkTask.getId());
            }
        } catch (Exception exc) {
            Log.e(LogEntryUISyncTask.class.getName(), "Error reading log entry for network task " + networkTask, exc);
        }
        return null;
    }

    @Override
    @SuppressWarnings("NotifyDataSetChanged")
    protected void runOnUIThread(LogEntry logEntry) {
        Log.d(LogEntryUISyncTask.class.getName(), "runOnUIThread, logEntry is " + logEntry);
        if (logEntry == null || adapterRef == null) {
            return;
        }
        LogEntryAdapter adapter = adapterRef.get();
        if (adapter != null) {
            try {
                Log.d(LogEntryUISyncTask.class.getName(), "Updating adapter with logEntry" + logEntry);
                adapter.addItem(logEntry);
                adapter.notifyDataSetChanged();
            } catch (Exception exc) {
                Log.e(LogEntryUISyncTask.class.getName(), "Error updating adapter with logEntry " + logEntry, exc);
            }
        }
    }
}
