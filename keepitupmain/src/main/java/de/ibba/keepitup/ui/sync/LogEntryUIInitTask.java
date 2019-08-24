package de.ibba.keepitup.ui.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

public class LogEntryUIInitTask extends AsyncTask<NetworkTask, Integer, List<LogEntry>> {

    private final WeakReference<Context> contextRef;
    private WeakReference<LogEntryAdapter> adapterRef;

    public LogEntryUIInitTask(Context context, LogEntryAdapter adapter) {
        this.contextRef = new WeakReference<>(context);
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        }
    }

    public void start(NetworkTask task) {
        super.execute(task);
    }

    @Override
    protected List<LogEntry> doInBackground(NetworkTask... tasks) {
        Log.d(LogEntryUIInitTask.class.getName(), "doInBackground");
        NetworkTask networkTask = tasks[0];
        Log.d(LogEntryUISyncTask.class.getName(), "Reading log entries for network task " + networkTask);
        try {
            Context context = contextRef.get();
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
    protected void onPostExecute(List<LogEntry> logEntries) {
        Log.d(LogEntryUIInitTask.class.getName(), "onPostExecute");
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
