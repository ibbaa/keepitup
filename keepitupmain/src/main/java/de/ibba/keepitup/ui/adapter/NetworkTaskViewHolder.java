package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final TextView titleText;

    public NetworkTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        titleText = itemView.findViewById(R.id.text_list_item_network_task_title);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }
}
