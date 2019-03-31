package de.ibba.keepitup.service;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;

import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;

public class NetworkKeepAliveServiceScheduler {

    private final Context context;
    private final NetworkTaskDAO networkTaskDAO;

    public NetworkKeepAliveServiceScheduler(Context context) {
        this.context = context;
        this.networkTaskDAO = new NetworkTaskDAO(context);
    }

    public void start(NetworkTask networkTask) {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Start network task " + networkTask);
        if (isRunning(networkTask)) {
            Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Network task " + networkTask + " is already running. Stopping...");
            stop(networkTask);
        }
        networkTask.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), true);
    }

    public void stop(NetworkTask networkTask) {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Stop network task " + networkTask);
        networkTask.setRunning(false);
        networkTaskDAO.updateNetworkTaskRunning(networkTask.getId(), false);
    }

    public void stopAll() {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Stop all network tasks ");
        List<NetworkTask> networkTasks = networkTaskDAO.readAllNetworkTasks();
        for (NetworkTask currentTask : networkTasks) {
            stop(currentTask);
        }
    }

    private boolean isRunning(NetworkTask networkTask) {
        return networkTask.isRunning();
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60 * 1000 * networkTask.getInterval();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
