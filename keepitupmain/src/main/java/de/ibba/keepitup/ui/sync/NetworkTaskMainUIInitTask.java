package de.ibba.keepitup.ui.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class NetworkTaskMainUIInitTask extends AsyncTask<Void, Integer, List<NetworkTaskUIWrapper>> {

    private final WeakReference<Context> contextRef;
    private WeakReference<NetworkTaskAdapter> adapterRef;

    public NetworkTaskMainUIInitTask(Context context, NetworkTaskAdapter adapter) {
        this.contextRef = new WeakReference<>(context);
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        }
    }

    public void start() {
        super.execute();
    }

    @Override
    protected List<NetworkTaskUIWrapper> doInBackground(Void... voids) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "doInBackground");
        try {
            Context context = contextRef.get();
            if (context != null) {
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
    protected void onPostExecute(List<NetworkTaskUIWrapper> networkTaskUIWrappers) {
        Log.d(NetworkTaskMainUIInitTask.class.getName(), "onPostExecute");
        if (networkTaskUIWrappers == null || adapterRef == null) {
            return;
        }
        try {
            NetworkTaskAdapter adapter = adapterRef.get();
            if (adapter != null) {
                adapter.initItems(networkTaskUIWrappers);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error initializing adapter", exc);
        }
    }
}
