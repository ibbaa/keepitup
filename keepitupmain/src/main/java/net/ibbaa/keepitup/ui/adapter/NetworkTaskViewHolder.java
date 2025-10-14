/*
 * Copyright (c) 2025 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class NetworkTaskViewHolder extends RecyclerView.ViewHolder {

    private final NetworkTaskMainActivity mainActivity;
    private final CardView cardView;
    private final TextView titleText;
    private final ImageView startStopImage;
    private final TextView statusText;
    private final TextView instancesText;
    private final TextView accessTypeText;
    private final TextView addressText;
    private final TextView connectToText;
    private final TextView intervalText;
    private final TextView notificationText;
    private final TextView ignoreSSLErrorText;
    private final TextView stopOnSuccessText;
    private final TextView onlyWifiText;
    private final TextView lastExecTimestampText;
    private final TextView failureCountText;
    private final TextView lastExecMessageText;

    public NetworkTaskViewHolder(@NonNull View itemView, NetworkTaskMainActivity mainActivity) {
        super(itemView);
        this.mainActivity = mainActivity;
        cardView = itemView.findViewById(R.id.cardview_list_item_network_task);
        titleText = itemView.findViewById(R.id.textview_list_item_network_task_title);
        titleText.setOnClickListener(this::onTitleClicked);
        startStopImage = itemView.findViewById(R.id.imageview_list_item_network_task_start_stop);
        startStopImage.setOnClickListener(this::onStartStopClicked);
        ImageView deleteImage = itemView.findViewById(R.id.imageview_list_item_network_task_delete);
        deleteImage.setOnClickListener(this::onDeleteClicked);
        ImageView editImage = itemView.findViewById(R.id.imageview_list_item_network_task_edit);
        editImage.setOnClickListener(this::onEditClicked);
        ImageView copyImage = itemView.findViewById(R.id.imageview_list_item_network_task_copy);
        copyImage.setOnClickListener(this::onCopyClicked);
        ImageView logImage = itemView.findViewById(R.id.imageview_list_item_network_task_log);
        logImage.setOnClickListener(this::onLogClicked);
        statusText = itemView.findViewById(R.id.textview_list_item_network_task_status);
        statusText.setOnClickListener(this::onStartStopClicked);
        instancesText = itemView.findViewById(R.id.textview_list_item_network_task_instances);
        accessTypeText = itemView.findViewById(R.id.textview_list_item_network_task_accesstype);
        addressText = itemView.findViewById(R.id.textview_list_item_network_task_address);
        connectToText = itemView.findViewById(R.id.textview_list_item_network_task_connect_to);
        intervalText = itemView.findViewById(R.id.textview_list_item_network_task_interval);
        notificationText = itemView.findViewById(R.id.textview_list_item_network_task_notification);
        ignoreSSLErrorText = itemView.findViewById(R.id.textview_list_item_network_task_ignore_ssl_error);
        stopOnSuccessText = itemView.findViewById(R.id.textview_list_item_network_task_stop_on_success);
        onlyWifiText = itemView.findViewById(R.id.textview_list_item_network_task_only_wifi);
        lastExecTimestampText = itemView.findViewById(R.id.textview_list_item_network_task_last_exec_timestamp);
        failureCountText = itemView.findViewById(R.id.textview_list_item_network_task_failure_count);
        lastExecMessageText = itemView.findViewById(R.id.textview_list_item_network_task_last_exec_message);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setTitleColor(int color) {
        titleText.setTextColor(color);
    }

    public void setStatus(String status, String descriptionStartStopImage, int startStopImageResource) {
        statusText.setText(status);
        startStopImage.setImageResource(startStopImageResource);
        startStopImage.setContentDescription(descriptionStartStopImage);
    }

    public void setInstances(String instances) {
        instancesText.setText(instances);
    }

    public void setAccessType(String accessType) {
        accessTypeText.setText(accessType);
    }

    public void setAddress(String address) {
        addressText.setText(address);
    }

    public void setConnectTo(String connectTo) {
        connectToText.setText(connectTo);
    }

    public void setInterval(String interval) {
        intervalText.setText(interval);
    }

    public void setIgnoreSSLError(String ignoreSSLError) {
        ignoreSSLErrorText.setText(ignoreSSLError);
    }

    public void setStopOnSuccess(String stopOnSuccess) {
        stopOnSuccessText.setText(stopOnSuccess);
    }

    public void setOnlyWifi(String onlyWifi) {
        onlyWifiText.setText(onlyWifi);
    }

    public void setNotification(String notification) {
        notificationText.setText(notification);
    }

    public void setLastExecTimestamp(String lastExecTimestamp) {
        lastExecTimestampText.setText(lastExecTimestamp);
    }

    public void setFailureCount(String failureCount) {
        failureCountText.setText(failureCount);
    }

    public void showFailureCountTextView() {
        failureCountText.setVisibility(View.VISIBLE);
    }

    public void hideFailureCountTextView() {
        failureCountText.setVisibility(View.GONE);
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

    public void showIgnoreSSLErrorTextView() {
        ignoreSSLErrorText.setVisibility(View.VISIBLE);
    }

    public void hideIgnoreSSLErrorTextView() {
        ignoreSSLErrorText.setVisibility(View.GONE);
    }

    public void showStopOnSuccessTextView() {
        stopOnSuccessText.setVisibility(View.VISIBLE);
    }

    public void hideStopOnSuccessTextView() {
        stopOnSuccessText.setVisibility(View.GONE);
    }

    public void showConnectToTextView() {
        connectToText.setVisibility(View.VISIBLE);
    }

    public void hideConnectToTextView() {
        connectToText.setVisibility(View.GONE);
    }

    private void onTitleClicked(View view) {
        mainActivity.onMainTitleClicked(getBindingAdapterPosition());
    }

    private void onStartStopClicked(View view) {
        mainActivity.onMainStartStopClicked(getBindingAdapterPosition());
    }

    private void onDeleteClicked(View view) {
        mainActivity.onMainDeleteClicked(getBindingAdapterPosition());
    }

    private void onEditClicked(View view) {
        mainActivity.onMainEditClicked(getBindingAdapterPosition());
    }

    private void onCopyClicked(View view) {
        mainActivity.onMainCopyClicked(getBindingAdapterPosition());
    }

    private void onLogClicked(View view) {
        mainActivity.onMainLogClicked(getBindingAdapterPosition());
    }
}
