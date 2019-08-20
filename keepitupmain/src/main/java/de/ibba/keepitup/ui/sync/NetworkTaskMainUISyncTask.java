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
    private final WeakReference<NetworkTaskAdapter> adapterRef;

    public NetworkTaskMainUISyncTask(Context context, NetworkTaskAdapter adapter) {
        this.contextRef = new WeakReference<>(context);
        this.adapterRef = new WeakReference<>(adapter);
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
            Context context = contextRef.get();
            if (context != null) {
                LogDAO logDAO = new LogDAO(context);
                LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(networkTask.getId());
                if (logEntry != null) {
                    return new NetworkTaskUIWrapper(networkTask, logEntry);
                }
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
        NetworkTaskAdapter adapter = adapterRef.get();
        if (adapter != null) {
            adapter.replaceItem(networkTaskWrapper);
            adapter.notifyDataSetChanged();
        }
    }

}
