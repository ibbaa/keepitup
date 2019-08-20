package de.ibba.keepitup.ui.sync;

import android.os.AsyncTask;
import android.util.Log;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class NetworkTaskMainUISyncTask extends AsyncTask<NetworkTask, Integer, NetworkTaskUIWrapper> {

    private final LogDAO logDAO;
    private final NetworkTaskAdapter adapter;

    public NetworkTaskMainUISyncTask(LogDAO logDAO, NetworkTaskAdapter adapter) {
        this.logDAO = logDAO;
        this.adapter = adapter;
    }

    public void start(NetworkTask task) {
        super.execute(task);
    }

    @Override
    protected NetworkTaskUIWrapper doInBackground(NetworkTask... tasks) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "doInBackground");
        NetworkTask networkTask = tasks[0];
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "Updating log entry for network task " + networkTask);
        try {
            LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(networkTask.getId());
            if (logEntry != null) {
                return new NetworkTaskUIWrapper(networkTask, logEntry);
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error updating log entry for network task " + networkTask, exc);
        }
        return null;
    }

    @Override
    protected void onPostExecute(NetworkTaskUIWrapper networkTaskWrapper) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "onPostExecute, networkTaskWrapper is " + networkTaskWrapper);
        if (networkTaskWrapper == null) {
            return;
        }
        if (adapter != null) {
            adapter.replaceItem(networkTaskWrapper);
            adapter.notifyDataSetChanged();
        }
    }
}
