package net.ibbaa.keepitup.ui.sync;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

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
