/*
 * Copyright (c) 2024. Alwin Ibba
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

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NetworkTaskAdapter extends RecyclerView.Adapter<NetworkTaskViewHolder> {

    private final List<NetworkTaskUIWrapper> networkTaskWrapperList;
    private final NetworkTaskMainActivity mainActivity;

    public NetworkTaskAdapter(List<NetworkTaskUIWrapper> networkTaskWrapperList, NetworkTaskMainActivity mainActivity) {
        this.networkTaskWrapperList = new ArrayList<>();
        this.mainActivity = mainActivity;
        replaceItems(networkTaskWrapperList);
    }

    @NonNull
    @Override
    public NetworkTaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(NetworkTaskAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_network_task, viewGroup, false);
        return new NetworkTaskViewHolder(itemView, mainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkTaskViewHolder networkTaskViewHolder, int position) {
        Log.d(NetworkTaskAdapter.class.getName(), "onBindViewHolder");
        NetworkTask networkTask = networkTaskWrapperList.get(position).getNetworkTask();
        LogEntry logEntry = networkTaskWrapperList.get(position).getLogEntry();
        bindTitle(networkTaskViewHolder, networkTask);
        bindStatus(networkTaskViewHolder, networkTask);
        bindInstances(networkTaskViewHolder, networkTask);
        bindAccessType(networkTaskViewHolder, networkTask);
        bindAddress(networkTaskViewHolder, networkTask);
        bindInterval(networkTaskViewHolder, networkTask);
        bindLastExecTimestamp(networkTaskViewHolder, logEntry);
        bindLastExecMessage(networkTaskViewHolder, logEntry);
        bindOnlyWifi(networkTaskViewHolder, networkTask);
        bindNotification(networkTaskViewHolder, networkTask);
    }

    private void bindTitle(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindTitle, networkTask is " + networkTask);
        String formattedTitleText = getResources().getString(R.string.text_activity_main_list_item_network_task_title, networkTask.getIndex() + 1);
        networkTaskViewHolder.setTitle(formattedTitleText);
    }

    private void bindStatus(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindStatus, networkTask is " + networkTask);
        boolean isRunningAndSuspended = isRunningAndSuspended(networkTask);
        String statusRunning = networkTask.isRunning() ? getResources().getString(R.string.string_started) : getResources().getString(R.string.string_stopped);
        if (isRunningAndSuspended) {
            statusRunning = getResources().getString(R.string.string_suspended);
        }
        String formattedStatusText = getResources().getString(R.string.text_activity_main_list_item_network_task_status, statusRunning);
        int startStopImage = networkTask.isRunning() ? R.drawable.icon_stop_selector : R.drawable.icon_start_selector;
        if (isRunningAndSuspended) {
            startStopImage = R.drawable.icon_suspended_selector;
        }
        String descriptionStartStopImage = networkTask.isRunning() ? getResources().getString(R.string.label_activity_main_stop_network_task) : getResources().getString(R.string.label_activity_main_start_network_task);
        Log.d(NetworkTaskAdapter.class.getName(), "binding status text " + formattedStatusText);
        networkTaskViewHolder.setStatus(formattedStatusText, descriptionStartStopImage, startStopImage);
    }

    private boolean isRunningAndSuspended(NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "isRunningAndSuspended, networkTask is " + networkTask);
        TimeBasedSuspensionScheduler scheduler = new TimeBasedSuspensionScheduler(getContext());
        boolean isSuspended = scheduler.isSuspended();
        boolean isRunning = networkTask.isRunning();
        Log.d(NetworkTaskAdapter.class.getName(), "isSuspended is " + isSuspended);
        Log.d(NetworkTaskAdapter.class.getName(), "isRunning is " + isRunning);
        return isSuspended && isRunning;
    }

    private void bindInstances(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindStatus, networkTask is " + networkTask);
        int instances = networkTask.getInstances();
        String formattedInstancesText = getResources().getString(R.string.text_activity_main_list_item_network_task_instances, instances);
        Log.d(NetworkTaskAdapter.class.getName(), "binding instances text " + formattedInstancesText);
        networkTaskViewHolder.setInstances(formattedInstancesText);
    }

    private void bindAccessType(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindAccessType, networkTask is " + networkTask);
        String accessTypeText = new EnumMapping(getContext()).getAccessTypeText(networkTask.getAccessType());
        String formattedAccessTypeText = getResources().getString(R.string.text_activity_main_list_item_network_task_access_type, accessTypeText);
        Log.d(NetworkTaskAdapter.class.getName(), "binding access type text " + formattedAccessTypeText);
        networkTaskViewHolder.setAccessType(formattedAccessTypeText);
    }

    private void bindAddress(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindAddress, networkTask is " + networkTask);
        String addressText = String.format(getResources().getString(R.string.text_activity_main_list_item_network_task_address), new EnumMapping(getContext()).getAccessTypeAddressText(networkTask.getAccessType()));
        String formattedAddressText = String.format(addressText, networkTask.getAddress(), networkTask.getPort());
        Log.d(NetworkTaskAdapter.class.getName(), "binding address text " + formattedAddressText);
        networkTaskViewHolder.setAddress(formattedAddressText);
    }

    private void bindInterval(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindInterval, networkTask is " + networkTask);
        int interval = networkTask.getInterval();
        String intervalUnit = interval == 1 ? getResources().getString(R.string.string_minute) : getResources().getString(R.string.string_minutes);
        String formattedIntervalText = getResources().getString(R.string.text_activity_main_list_item_network_task_interval, interval, intervalUnit);
        Log.d(NetworkTaskAdapter.class.getName(), "binding interval text " + formattedIntervalText);
        networkTaskViewHolder.setInterval(formattedIntervalText);
    }

    private void bindOnlyWifi(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindOnlyWifi, networkTask is " + networkTask);
        String onlyWifi = networkTask.isOnlyWifi() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no);
        String formattedOnlyWifiText = getResources().getString(R.string.text_activity_main_list_item_network_task_onlywifi, onlyWifi);
        Log.d(NetworkTaskAdapter.class.getName(), "binding only wifi text " + formattedOnlyWifiText);
        networkTaskViewHolder.setOnlyWifi(formattedOnlyWifiText);
    }

    private void bindNotification(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindNotification, networkTask is " + networkTask);
        String sendNotification = networkTask.isNotification() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no);
        String formattedNotificationText = getResources().getString(R.string.text_activity_main_list_item_network_task_notification, sendNotification);
        Log.d(NetworkTaskAdapter.class.getName(), "binding notification text " + formattedNotificationText);
        networkTaskViewHolder.setNotification(formattedNotificationText);
    }

    private void bindLastExecTimestamp(@NonNull NetworkTaskViewHolder networkTaskViewHolder, LogEntry logEntry) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindLastExecTimestamp, logEntry is " + logEntry);
        String timestampText;
        if (wasExecuted(logEntry)) {
            timestampText = logEntry.isSuccess() ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful);
            timestampText += ", " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        } else {
            timestampText = getResources().getString(R.string.string_not_executed);
        }
        String formattedLastExecTimestampText = getResources().getString(R.string.text_activity_main_list_item_network_task_last_exec_timestamp, timestampText);
        Log.d(NetworkTaskAdapter.class.getName(), "binding last exec timestamp text " + formattedLastExecTimestampText);
        networkTaskViewHolder.setLastExecTimestamp(formattedLastExecTimestampText);
    }

    private void bindLastExecMessage(@NonNull NetworkTaskViewHolder networkTaskViewHolder, LogEntry logEntry) {
        Log.d(NetworkTaskAdapter.class.getName(), "bindLastExecMessage, logEntry is " + logEntry);
        if (wasExecuted(logEntry)) {
            String formattedMessageText = getResources().getString(R.string.text_activity_main_list_item_network_task_last_exec_message, logEntry.getMessage());
            Log.d(NetworkTaskAdapter.class.getName(), "binding and showing last exec message text " + formattedMessageText);
            networkTaskViewHolder.setLastExecMessage(formattedMessageText);
            networkTaskViewHolder.showLastExecMessageTextView();
        } else {
            Log.d(NetworkTaskAdapter.class.getName(), "Not executed. Hiding last exec message text.");
            networkTaskViewHolder.setLastExecMessage("");
            networkTaskViewHolder.hideLastExecMessageTextView();
        }
    }

    private boolean wasExecuted(LogEntry logEntry) {
        return logEntry != null && logEntry.getTimestamp() > 0;
    }

    public void addItem(NetworkTaskUIWrapper task) {
        Log.d(NetworkTaskAdapter.class.getName(), "addItem " + task);
        networkTaskWrapperList.add(task);
    }

    public void removeItem(NetworkTaskUIWrapper task) {
        Log.d(NetworkTaskAdapter.class.getName(), "removeItem " + task);
        for (int ii = 0; ii < networkTaskWrapperList.size(); ii++) {
            NetworkTaskUIWrapper currentTask = networkTaskWrapperList.get(ii);
            if (task.getId() == currentTask.getId()) {
                networkTaskWrapperList.remove(ii);
                updateIndex();
                return;
            }
        }
    }

    public void replaceItem(NetworkTaskUIWrapper task) {
        Log.d(NetworkTaskAdapter.class.getName(), "replaceItem " + task);
        for (int ii = 0; ii < networkTaskWrapperList.size(); ii++) {
            NetworkTaskUIWrapper currentTask = networkTaskWrapperList.get(ii);
            if (task.getId() == currentTask.getId()) {
                networkTaskWrapperList.set(ii, task);
                return;
            }
        }
    }

    public void replaceNetworkTask(NetworkTask task) {
        Log.d(NetworkTaskAdapter.class.getName(), "replaceNetworkTask " + task);
        for (int ii = 0; ii < networkTaskWrapperList.size(); ii++) {
            NetworkTaskUIWrapper currentTask = networkTaskWrapperList.get(ii);
            if (task.getId() == currentTask.getId()) {
                networkTaskWrapperList.set(ii, new NetworkTaskUIWrapper(task, currentTask.getLogEntry()));
                return;
            }
        }
    }

    @SuppressWarnings({"unused"})
    public void replaceLogEntry(NetworkTask task, LogEntry logEntry) {
        Log.d(NetworkTaskAdapter.class.getName(), "replaceLogEntry " + logEntry);
        for (int ii = 0; ii < networkTaskWrapperList.size(); ii++) {
            NetworkTaskUIWrapper currentTask = networkTaskWrapperList.get(ii);
            if (task.getId() == currentTask.getId()) {
                networkTaskWrapperList.set(ii, new NetworkTaskUIWrapper(currentTask.getNetworkTask(), logEntry));
                return;
            }
        }
    }

    public void replaceItems(List<NetworkTaskUIWrapper> networkTaskWrapperList) {
        this.networkTaskWrapperList.clear();
        this.networkTaskWrapperList.addAll(networkTaskWrapperList);
    }

    public void updateIndex() {
        Log.d(NetworkTaskAdapter.class.getName(), "updateIndex");
        for (int ii = 0; ii < networkTaskWrapperList.size(); ii++) {
            NetworkTask currentTask = networkTaskWrapperList.get(ii).getNetworkTask();
            currentTask.setIndex(ii);
        }
    }

    public int getNextIndex() {
        return networkTaskWrapperList.size();
    }

    @Override
    public int getItemCount() {
        return networkTaskWrapperList.size();
    }

    public List<NetworkTaskUIWrapper> getAllItems() {
        return Collections.unmodifiableList(networkTaskWrapperList);
    }

    public NetworkTaskUIWrapper getItem(int position) {
        Log.d(LogEntryAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= networkTaskWrapperList.size()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return networkTaskWrapperList.get(position);
    }

    public Context getContext() {
        return mainActivity;
    }

    public Resources getResources() {
        return getContext().getResources();
    }
}
