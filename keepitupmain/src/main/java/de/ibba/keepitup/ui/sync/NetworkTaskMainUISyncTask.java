package de.ibba.keepitup.ui.sync;

import android.os.AsyncTask;
import android.util.Log;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class NetworkTaskMainUISyncTask extends AsyncTask<NetworkTaskMainUISyncHolder, Integer, NetworkTaskUIWrapper> {

    private NetworkTaskMainUISyncHolder syncHolder;

    public void start(NetworkTaskMainUISyncHolder uiSyncHolder) {
        super.execute(uiSyncHolder);
    }

    @Override
    protected NetworkTaskUIWrapper doInBackground(NetworkTaskMainUISyncHolder... syncHolders) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "doInBackground");
        syncHolder = syncHolders[0];
        NetworkTask networkTask = syncHolder.getNetworkTask();
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "Updating log entry for network task " + networkTask);
        LogDAO logDAO = syncHolder.getLogDAO();
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
        NetworkTaskAdapter adapter = syncHolder.getAdapter();
        adapter.replaceItem(networkTaskWrapper);
        adapter.notifyDataSetChanged();
    }
}
