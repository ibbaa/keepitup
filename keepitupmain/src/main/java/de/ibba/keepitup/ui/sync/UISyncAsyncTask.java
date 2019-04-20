package de.ibba.keepitup.ui.sync;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

public class UISyncAsyncTask extends AsyncTask<UISyncHolder, Integer, List<NetworkTaskUIWrapper>> {

    private UISyncHolder syncHolder;

    public void start(UISyncHolder uiSyncHolder) {
        super.execute(uiSyncHolder);
    }

    @Override
    protected List<NetworkTaskUIWrapper> doInBackground(UISyncHolder... syncHolders) {
        syncHolder = syncHolders[0];
        List<NetworkTaskUIWrapper> networkTaskWrapperList = syncHolder.getNetworkTaskWrapperList();
        LogDAO logDAO = syncHolder.getLogDAO();
        List<NetworkTaskUIWrapper> updatedNetworkTaskWrapperList = new ArrayList<>();
        for (NetworkTaskUIWrapper currentWrapper : networkTaskWrapperList) {
            NetworkTask currentTask = currentWrapper.getNetworkTask();
            try {
                if (currentTask.isRunning()) {
                    LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(currentTask.getId());
                    if (logEntry != null) {
                        NetworkTaskUIWrapper updatedWrapper = new NetworkTaskUIWrapper(currentTask, logEntry);
                        updatedNetworkTaskWrapperList.add(updatedWrapper);
                    }
                }
            } catch (Exception exc) {
                Log.e(UISyncAsyncTask.class.getName(), "Error updating log entry for network task " + currentTask, exc);
            }
        }
        return updatedNetworkTaskWrapperList;
    }

    @Override
    protected void onPostExecute(List<NetworkTaskUIWrapper> networkTaskWrapperList) {
        NetworkTaskAdapter adapter = syncHolder.getAdapter();
        for (NetworkTaskUIWrapper currentWrapper : networkTaskWrapperList) {
            adapter.replaceItem(currentWrapper);
        }
        adapter.notifyDataSetChanged();
    }
}
