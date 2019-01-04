package de.ibba.keepitup.ui.mapping;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final TextView statusText;
    private final ImageView statusImage;
    private final TextView accessTypeText;
    private final TextView addressText;

    public NetworkTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        statusText = itemView.findViewById(R.id.textview_list_item_network_task_status);
        statusImage = itemView.findViewById(R.id.imageview_list_item_network_task_status);
        accessTypeText = itemView.findViewById(R.id.textview_list_item_network_task_access_type);
        addressText = itemView.findViewById(R.id.textview_list_item_network_task_address);
    }

    public void setStatus(String status, int image) {
        statusText.setText(status);
        statusImage.setImageResource(image);
    }

    public void setAccessType(String accessType) {
        accessTypeText.setText(accessType);
    }

    public void setAddress(String address) {
        addressText.setText(address);
    }
}
