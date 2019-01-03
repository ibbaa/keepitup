package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;

public class NetworkTaskAdapter extends RecyclerView.Adapter<NetworkTaskViewHolder> {

    private final List<NetworkTask> networkTasks;

    public NetworkTaskAdapter(List<NetworkTask> networkTasks) {
        this.networkTasks = networkTasks;
    }

    @NonNull
    @Override
    public NetworkTaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_network_task, viewGroup, false);
        return new NetworkTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkTaskViewHolder networkTaskViewHolder, int position) {
//        NetworkTask networkTask = networkTasks.get(position);
//        networkTaskViewHolder.setTitle(networkTask.getAddress());
    }

    @Override
    public int getItemCount() {
        return networkTasks.size();
    }
}
