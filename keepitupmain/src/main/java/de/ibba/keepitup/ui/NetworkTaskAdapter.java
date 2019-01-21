package de.ibba.keepitup.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.ui.mapping.EnumMapping;

public class NetworkTaskAdapter extends RecyclerView.Adapter<NetworkTaskViewHolder> {

    private final List<NetworkTask> networkTasks;
    private final NetworkTaskMainActivity mainActivity;

    public NetworkTaskAdapter(List<NetworkTask> networkTasks, NetworkTaskMainActivity mainActivity) {
        this.networkTasks = networkTasks;
        this.mainActivity = mainActivity;
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
        if (position < networkTasks.size()) {
            NetworkTask networkTask = networkTasks.get(position);
            NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(getContext());
            boolean isRunning = scheduler.isRunning(networkTask);
            bindStatus(networkTaskViewHolder, isRunning);
            bindAccessType(networkTaskViewHolder, networkTask);
            bindAddress(networkTaskViewHolder, networkTask);
            bindInterval(networkTaskViewHolder, networkTask);
            bindLastExecTimestamp(networkTaskViewHolder, networkTask);
            bindLastExecMessage(networkTaskViewHolder, networkTask);
            bindNotification(networkTaskViewHolder, networkTask);
            networkTaskViewHolder.showMainNetworkTaskCard();
        } else {
            networkTaskViewHolder.showAddNetworkTaskImage();
        }
    }

    private void bindStatus(@NonNull NetworkTaskViewHolder networkTaskViewHolder, boolean isRunning) {
        String statusRunning = isRunning ? getResources().getString(R.string.string_running) : getResources().getString(R.string.string_stopped);
        String formattedStatusText = String.format(getResources().getString(R.string.text_list_item_network_task_status), statusRunning);
        int startStopImage = isRunning ? R.drawable.icon_stop_selector : R.drawable.icon_start_selector;
        String descriptionStartStopImage = isRunning ? getResources().getString(R.string.label_stop_network_task) : getResources().getString(R.string.label_start_network_task);
        Log.d(NetworkTaskAdapter.class.getName(), "binding status text " + formattedStatusText);
        networkTaskViewHolder.setStatus(formattedStatusText, descriptionStartStopImage, startStopImage);
    }

    private void bindAccessType(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        String accessTypeText = new EnumMapping(getContext()).getAccessTypeText(networkTask.getAccessType());
        String formattedAccessTypeText = String.format(getResources().getString(R.string.text_list_item_network_task_access_type), accessTypeText);
        Log.d(NetworkTaskAdapter.class.getName(), "binding acccess type text " + formattedAccessTypeText);
        networkTaskViewHolder.setAccessType(formattedAccessTypeText);
    }

    private void bindAddress(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        String addressText = String.format(getResources().getString(R.string.text_list_item_network_task_address), new EnumMapping(getContext()).getAccessTypeAddressText(networkTask.getAccessType()));
        String formattedAddressText = String.format(addressText, networkTask.getAddress(), networkTask.getPort());
        Log.d(NetworkTaskAdapter.class.getName(), "binding address text " + formattedAddressText);
        networkTaskViewHolder.setAddress(formattedAddressText);
    }

    private void bindInterval(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        String intervalUnit = getResources().getString(R.string.string_minutes);
        String formattedIntervalText = String.format(getResources().getString(R.string.text_list_item_network_task_interval), networkTask.getInterval(), intervalUnit);
        networkTaskViewHolder.setInterval(formattedIntervalText);
    }

    private void bindNotification(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        String sendNotification = networkTask.isNotification() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no);
        String formattedNotificationText = String.format(getResources().getString(R.string.text_list_item_network_task_notification), sendNotification);
        networkTaskViewHolder.setNotification(formattedNotificationText);
    }

    private void bindLastExecTimestamp(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        String timestampText;
        if (wasExecuted(networkTask)) {
            timestampText = networkTask.isSuccess() ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful);
            timestampText += ", " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(networkTask.getTimestamp()));
        } else {
            timestampText = getResources().getString(R.string.string_not_executed);
        }
        String formattedLastExecTimestampText = String.format(getResources().getString(R.string.text_list_item_network_task_last_exec_timestamp), timestampText);
        networkTaskViewHolder.setLastExecTimestamp(formattedLastExecTimestampText);
    }

    private void bindLastExecMessage(@NonNull NetworkTaskViewHolder networkTaskViewHolder, NetworkTask networkTask) {
        if (wasExecuted(networkTask)) {
            String formattedMessageText = String.format(getResources().getString(R.string.text_list_item_network_task_last_exec_message), networkTask.getMessage());
            networkTaskViewHolder.setLastExecMessage(formattedMessageText);
            networkTaskViewHolder.showLastExecMessageTextView();
        } else {
            networkTaskViewHolder.setLastExecMessage("");
            networkTaskViewHolder.hideLastExecMessageTextView();
        }
    }

    private boolean wasExecuted(NetworkTask networkTask) {
        return networkTask.getTimestamp() > 0;
    }

    @Override
    public int getItemCount() {
        return networkTasks.size() + 1;
    }

    public NetworkTask getItem(int position) {
        return networkTasks.get(position);
    }

    private Context getContext() {
        return mainActivity;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
