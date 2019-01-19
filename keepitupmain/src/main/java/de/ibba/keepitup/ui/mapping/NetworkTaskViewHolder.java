package de.ibba.keepitup.ui.mapping;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.NetworkTaskUIController;

public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final NetworkTaskUIController uiController;
    private final ImageView startStopImage;
    private final TextView statusText;
    private final TextView accessTypeText;
    private final TextView addressText;
    private final TextView intervalText;
    private final TextView notificationText;
    private final TextView lastExecTimestampText;
    private final TextView lastExecMessageText;

    public NetworkTaskViewHolder(@NonNull View itemView, NetworkTaskUIController uiController) {
        super(itemView);
        this.uiController = uiController;
        itemView.setOnClickListener(this::onStartStopClicked);
        startStopImage = itemView.findViewById(R.id.imageview_list_item_network_task_start_stop);
        startStopImage.setOnClickListener(this::onStartStopClicked);
        statusText = itemView.findViewById(R.id.textview_list_item_network_task_status);
        accessTypeText = itemView.findViewById(R.id.textview_list_item_network_task_access_type);
        addressText = itemView.findViewById(R.id.textview_list_item_network_task_address);
        intervalText = itemView.findViewById(R.id.textview_list_item_network_task_interval);
        notificationText = itemView.findViewById(R.id.textview_list_item_network_task_notification);
        lastExecTimestampText = itemView.findViewById(R.id.textview_list_item_network_task_last_exec_timestamp);
        lastExecMessageText = itemView.findViewById(R.id.textview_list_item_network_task_last_exec_message);
    }

    public void setStatus(String status, String descriptionStartStopImage, int startStopImageResource) {
        statusText.setText(status);
        startStopImage.setImageResource(startStopImageResource);
        startStopImage.setContentDescription(descriptionStartStopImage);
    }

    public void setAccessType(String accessType) {
        accessTypeText.setText(accessType);
    }

    public void setAddress(String address) {
        addressText.setText(address);
    }

    public void setInterval(String interval) {
        intervalText.setText(interval);
    }

    public void setNotification(String notification) {
        notificationText.setText(notification);
    }

    public void setLastExecTimestamp(String lastExecTimestamp) {
        lastExecTimestampText.setText(lastExecTimestamp);
    }

    public void setLastExecMessage(String lastExecMessage) {
        lastExecMessageText.setText(lastExecMessage);
    }

    public void showLastExecMessageTextView() {
        lastExecMessageText.setVisibility(View.VISIBLE);
    }

    public void hideLastExecMessageTextView() {
        lastExecMessageText.setVisibility(View.GONE);
    }

    private void onStartStopClicked(View view) {
        uiController.onStartStopClicked(view, getAdapterPosition());
    }
}
