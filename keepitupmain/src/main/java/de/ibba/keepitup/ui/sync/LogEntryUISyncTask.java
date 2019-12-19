package de.ibba.keepitup.ui.sync;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

public class LogEntryUISyncTask extends AsyncTask<NetworkTask, Integer, LogEntry> {

    private final WeakReference<Context> contextRef;
    private WeakReference<LogEntryAdapter> adapterRef;

    public LogEntryUISyncTask(Context context, LogEntryAdapter adapter) {
        this.contextRef = new WeakReference<>(context);
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        }
    }

    public void start(NetworkTask task) {
        super.execute(task);
    }

    @Override
    protected LogEntry doInBackground(NetworkTask... tasks) {
        Log.d(LogEntryUISyncTask.class.getName(), "doInBackground");
        NetworkTask networkTask = tasks[0];
        Log.d(LogEntryUISyncTask.class.getName(), "Reading log entry for network task " + networkTask);
        try {
            Context context = contextRef.get();
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
    protected void onPostExecute(LogEntry logEntry) {
        Log.d(LogEntryUISyncTask.class.getName(), "onPostExecute, logEntry is " + logEntry);
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
