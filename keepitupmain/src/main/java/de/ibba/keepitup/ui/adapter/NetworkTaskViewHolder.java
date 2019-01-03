package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final TextView statusText;

    public NetworkTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        statusText = itemView.findViewById(R.id.textview_list_item_network_task_status);
    }

    public void setStatus(String status) {
        statusText.setText(status);
    }
}
