package de.ibba.keepitup.ui.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class NetworkTaskMainUISyncTask extends AsyncTask<NetworkTask, Integer, NetworkTaskUIWrapper> {

    private final WeakReference<Context> contextRef;
    private WeakReference<NetworkTaskAdapter> adapterRef;

    public NetworkTaskMainUISyncTask(Context context, NetworkTaskAdapter adapter) {
        this.contextRef = new WeakReference<>(context);
        if (adapter != null) {
            this.adapterRef = new WeakReference<>(adapter);
        }
    }

    public void start(NetworkTask task) {
        super.execute(task);
    }

    @Override
    protected NetworkTaskUIWrapper doInBackground(NetworkTask... tasks) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "doInBackground");
        NetworkTask networkTask = tasks[0];
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "Reading log entry for network task " + networkTask);
        try {
            Context context = contextRef.get();
            if (context != null) {
                LogDAO logDAO = new LogDAO(context);
                LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(networkTask.getId());
                if (logEntry != null) {
                    return new NetworkTaskUIWrapper(networkTask, logEntry);
                }
            }
        } catch (Exception exc) {
            Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error reading log entry for network task " + networkTask, exc);
        }
        return null;
    }

    @Override
    protected void onPostExecute(NetworkTaskUIWrapper networkTaskWrapper) {
        Log.d(NetworkTaskMainUISyncTask.class.getName(), "onPostExecute, networkTaskWrapper is " + networkTaskWrapper);
        if (networkTaskWrapper == null || adapterRef == null) {
            return;
        }
        NetworkTaskAdapter adapter = adapterRef.get();
        if (adapter != null) {
            try {
                Log.d(NetworkTaskMainUISyncTask.class.getName(), "Updating adapter with networkTaskWrapper " + networkTaskWrapper);
                adapter.replaceItem(networkTaskWrapper);
                adapter.notifyDataSetChanged();
            } catch (Exception exc) {
                Log.e(NetworkTaskMainUISyncTask.class.getName(), "Error updating adapter with networkTaskWrapper " + networkTaskWrapper, exc);
            }
        }
    }

}
