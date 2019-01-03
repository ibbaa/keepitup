package de.ibba.keepitup.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final TextView statusText;
    private final ImageView statusImage;

    public NetworkTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        statusText = itemView.findViewById(R.id.textview_list_item_network_task_status);
        statusImage = itemView.findViewById(R.id.image_list_item_network_task_status);
    }

    public void setStatus(String status, int image) {
        statusText.setText(status);
        statusImage.setImageResource(image);
    }
}
