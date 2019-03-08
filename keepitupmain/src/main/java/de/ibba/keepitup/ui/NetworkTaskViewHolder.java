package de.ibba.keepitup.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.ibba.keepitup.R;

public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final NetworkTaskMainActivity mainActivity;
    private final CardView cardView;
    private final ImageView startStopImage;
    private final ImageView deleteImage;
    private final ImageView editImage;
    private final TextView statusText;
    private final TextView accessTypeText;
    private final TextView addressText;
    private final TextView intervalText;
    private final TextView notificationText;
    private final TextView lastExecTimestampText;
    private final TextView lastExecMessageText;
    private final ImageView addImage;

    public NetworkTaskViewHolder(@NonNull View itemView, NetworkTaskMainActivity mainActivity) {
        super(itemView);
        this.mainActivity = mainActivity;
        itemView.setOnClickListener(this::onStartStopClicked);
        cardView = itemView.findViewById(R.id.cardview_list_item_network_task);
        startStopImage = itemView.findViewById(R.id.imageview_list_item_network_task_start_stop);
        startStopImage.setOnClickListener(this::onStartStopClicked);
        deleteImage = itemView.findViewById(R.id.imageview_list_item_network_task_delete);
        deleteImage.setOnClickListener(this::onDeleteClicked);
        editImage = itemView.findViewById(R.id.imageview_list_item_network_task_edit);
        editImage.setOnClickListener(this::onEditClicked);
        statusText = itemView.findViewById(R.id.textview_list_item_network_task_status);
        accessTypeText = itemView.findViewById(R.id.textview_list_item_network_task_accesstype);
        addressText = itemView.findViewById(R.id.textview_list_item_network_task_address);
        intervalText = itemView.findViewById(R.id.textview_list_item_network_task_interval);
        notificationText = itemView.findViewById(R.id.textview_list_item_network_task_notification);
        lastExecTimestampText = itemView.findViewById(R.id.textview_list_item_network_task_last_exec_timestamp);
        lastExecMessageText = itemView.findViewById(R.id.textview_list_item_network_task_last_exec_message);
        addImage = itemView.findViewById(R.id.imageview_list_item_network_task_add);
        addImage.setOnClickListener(mainActivity::onMainAddClicked);
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

    public void showMainNetworkTaskCard() {
        cardView.setVisibility(View.VISIBLE);
        addImage.setVisibility(View.GONE);
    }

    public void showAddNetworkTaskImage() {
        cardView.setVisibility(View.GONE);
        addImage.setVisibility(View.VISIBLE);
    }

    private void onStartStopClicked(@SuppressWarnings("unused") View view) {
        mainActivity.onMainStartStopClicked(getAdapterPosition());
    }

    private void onDeleteClicked(@SuppressWarnings("unused") View view) {
        mainActivity.onMainDeleteClicked(getAdapterPosition());
    }

    private void onEditClicked(@SuppressWarnings("unused") View view) {
        mainActivity.onMainEditClicked(getAdapterPosition());
    }
}
