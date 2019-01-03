package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkJob;

public class NetworkJobAdapter extends RecyclerView.Adapter<NetworkJobViewHolder> {

    private List<NetworkJob> networkJobs;

    public NetworkJobAdapter(List<NetworkJob> networkJobs) {
        this.networkJobs = networkJobs;
    }

    @NonNull
    @Override
    public NetworkJobViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_network_job, viewGroup, false);
        return new NetworkJobViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkJobViewHolder networkJobViewHolder, int position) {
        NetworkJob networkJob = networkJobs.get(position);
        networkJobViewHolder.setAddress(networkJob.getAddress());
    }

    @Override
    public int getItemCount() {
        return networkJobs.size();
    }
}
