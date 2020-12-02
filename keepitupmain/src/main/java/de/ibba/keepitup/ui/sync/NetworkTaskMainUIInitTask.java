package de.ibba.keepitup.ui.sync;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class NetworkTaskMainUIInitTask extends UIBackgroundTask<List<NetworkTaskUIWrapper>> {

    private final WeakReference<NetworkTaskAdapter> adapterRef;

    public NetworkTaskMainUIInitTask(Activity activity, NetworkTaskAdapter adapter) {
        super(activity);
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        } else {
            adapterRef = null;
        }
    }

    @Override
    protected List<NetworkTaskUIWrapper> runInBackground() {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "runInBackground");
        try {
            Context context = getActivity();
            if (context != null) {
                Log.d(NetworkTaskMainUISyncTask.class.getName(), "Reading all network tasks");
                List<NetworkTaskUIWrapper> wrapperList = new ArrayList<>();
                NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(context);
                List<NetworkTask> tasks = networkTaskDAO.readAllNetworkTasks();
                Log.d(NetworkTaskMainActivity.class.getName(), "Database returned the following network tasks: " + (tasks.isEmpty() ? "no network tasks" : ""));
                for (NetworkTask currentTask : tasks) {
                    Log.d(NetworkTaskMainActivity.class.getName(), currentTask.toString());
                }
                for (NetworkTask currentTask : tasks) {
                    Log.d(NetworkTaskMainActivity.class.getName(), "Reading most recent log for " + currentTask);
                    LogDAO logDAO = new LogDAO(context);
                    LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(currentTask.getId());
                    Log.d(NetworkTaskMainActivity.class.getName(), "Database returned the following log entry: " + (logEntry == null ? "no log entry" : logEntry.toString()));
                    NetworkTaskUIWrapper currentWrapper = new NetworkTaskUIWrapper(currentTask, logEntry);
                    wrapperList.add(currentWrapper);
                }
                return wrapperList;
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskMainUIInitTask.class.getName(), "Error reading all network tasks from database", exc);
        }
        return null;
    }

    @Override
    protected void runOnUIThread(List<NetworkTaskUIWrapper> networkTaskUIWrappers) {
        Log.d(NetworkTaskMainUIInitTask.class.getName(), "runOnUIThread");
        if (networkTaskUIWrappers == null || adapterRef == null) {
            return;
        }
        try {
            NetworkTaskAdapter adapter = adapterRef.get();
            if (adapter != null) {
                Log.d(NetworkTaskMainUIInitTask.class.getName(), "Initializing adapter");
                adapter.replaceItems(networkTaskUIWrappers);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error initializing adapter", exc);
        }
    }
}
