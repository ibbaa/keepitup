/*
 * Copyright (c) 2022. Alwin Ibba
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

import java.lang.ref.WeakReference;
import java.util.List;

import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

public class LogEntryUIInitTask extends UIBackgroundTask<List<LogEntry>> {

    private final WeakReference<LogEntryAdapter> adapterRef;
    private final NetworkTask networkTask;

    public LogEntryUIInitTask(Activity activity, NetworkTask networkTask, LogEntryAdapter adapter) {
        super(activity);
        this.networkTask = networkTask;
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        } else {
            this.adapterRef = null;
        }
    }

    @Override
    protected List<LogEntry> runInBackground() {
        Log.d(LogEntryUIInitTask.class.getName(), "runInBackground");
        Log.d(LogEntryUISyncTask.class.getName(), "Reading log entries for network task " + networkTask);
        try {
            Context context = getActivity();
            if (context != null) {
                LogDAO logDAO = new LogDAO(context);
                List<LogEntry> logEntries = logDAO.readAllLogsForNetworkTask(networkTask.getId());
                Log.d(LogEntryUIInitTask.class.getName(), "Database returned the following log entries: " + (logEntries.isEmpty() ? "no log entries" : ""));
                for (LogEntry logEntry : logEntries) {
                    Log.d(LogEntryUIInitTask.class.getName(), logEntry.toString());
                }
                return logEntries;
            }
        } catch (Exception exc) {
            Log.e(LogEntryUIInitTask.class.getName(), "Error reading log entries for network task " + networkTask, exc);
        }
        return null;
    }

    @Override
    protected void runOnUIThread(List<LogEntry> logEntries) {
        Log.d(LogEntryUIInitTask.class.getName(), "runOnUIThread");
        if (logEntries == null || adapterRef == null) {
            return;
        }
        LogEntryAdapter adapter = adapterRef.get();
        if (adapter != null) {
            try {
                Log.d(NetworkTaskMainUIInitTask.class.getName(), "Initializing adapter");
                adapter.replaceItems(logEntries);
                adapter.notifyDataSetChanged();
            } catch (Exception exc) {
                Log.e(LogEntryUIInitTask.class.getName(), "Error initializing adapter", exc);
            }
        }
    }
}
