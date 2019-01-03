package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;

public class NetworkTaskAdapter extends RecyclerView.Adapter<NetworkTaskViewHolder> {

    private final List<NetworkTask> networkTasks;
    private final Context context;

    public NetworkTaskAdapter(List<NetworkTask> networkTasks, Context context) {
        this.networkTasks = networkTasks;
        this.context = context;
    }

    @NonNull
    @Override
    public NetworkTaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_network_task, viewGroup, false);
        return new NetworkTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkTaskViewHolder networkTaskViewHolder, int position) {
        NetworkTask networkTask = networkTasks.get(position);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(getContext());
        boolean isRunning = scheduler.isRunning(networkTask);
        bindStatus(networkTaskViewHolder, isRunning);
    }

    private void bindStatus(@NonNull NetworkTaskViewHolder networkTaskViewHolder, boolean isRunning) {
        String statusRunning = isRunning ? getResources().getString(R.string.string_running) : getResources().getString(R.string.string_stopped);
        String statusText = String.format(getResources().getString(R.string.text_list_item_network_task_status), statusRunning);
        int statusImage = isRunning ? R.drawable.icon_running : R.drawable.icon_stopped;
        networkTaskViewHolder.setStatus(statusText, statusImage);
    }

    @Override
    public int getItemCount() {
        return networkTasks.size();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}