package de.ibba.keepitup.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import java.util.List;

import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.ui.mapping.NetworkTaskAdapter;

public class NetworkTaskUIController {

    private final List<NetworkTask> networkTasks;
    private final NetworkTaskAdapter adapter;
    private final FragmentActivity activity;

    public NetworkTaskUIController(List<NetworkTask> networkTasks, FragmentActivity activity) {
        this.networkTasks = networkTasks;
        this.activity = activity;
        this.adapter = new NetworkTaskAdapter(this, getContext());
    }

    public NetworkTaskAdapter getAdapter() {
        return adapter;
    }

    public int getItemCount() {
        return networkTasks.size();
    }

    public NetworkTask getItem(int position) {
        return networkTasks.get(position);
    }

    public void onAddClicked(View view) {
        Log.d(NetworkTaskUIController.class.getName(), "onAddClicked");
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        editDialog.show(activity.getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onStartStopClicked(View view, int position) {
        NetworkTask networkTask = networkTasks.get(position);
        Log.d(NetworkTaskUIController.class.getName(), "onStartStopClicked for network task " + networkTask);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(getContext());
        if (scheduler.isRunning(networkTask)) {
            scheduler.stop(networkTask);
        } else {
            scheduler.start(networkTask);
        }
        adapter.notifyItemChanged(position);
    }

    private Context getContext() {
        return activity;
    }
}
