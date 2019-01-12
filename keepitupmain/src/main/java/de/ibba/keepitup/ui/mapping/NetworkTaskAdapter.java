package de.ibba.keepitup.ui.mapping;

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

public class NetworkTaskAdapter extends RecyclerView.Adapter<NetworkTaskViewHolder> {

    private final List<NetworkTask> networkTasks;
    private final Context context;

    public NetworkTaskAdapter(List<NetworkTask> networkTasks, Context context) {
        this.networkTasks = networkTasks;
        this.context = context;
    }

    @NonNull
    @Override
    public NetworkTaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(NetworkTaskAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_network_task, viewGroup, false);
        return new NetworkTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkTaskViewHolder networkTaskViewHolder, int position) {
        Log.d(NetworkTaskAdapter.class.getName(), "onBindViewHolder");
        NetworkTask networkTask = networkTasks.get(position);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(getContext());
        boolean isRunning = scheduler.isRunning(networkTask);
        bindStatus(networkTaskViewHolder, isRunning);
        bindAccessType(networkTaskViewHolder, networkTask);
        bindAddress(networkTaskViewHolder, networkTask);
        bindInterval(networkTaskViewHolder, networkTask);
        bindLastExecTimestamp(networkTaskViewHolder, networkTask);
        bindLastExecMessage(networkTaskViewHolder, networkTask);
    }

    private void bindStatus(@NonNull NetworkTaskViewHolder networkTaskViewHolder, boolean isRunning) {
        String statusRunning = isRunning ? getResources().getString(R.string.string_running) : getResources().getString(R.string.string_stopped);
        String formattedStatusText = String.format(getResources().getString(R.string.text_list_item_network_task_status), statusRunning);
        int statusImage = isRunning ? R.drawable.icon_running : R.drawable.icon_stopped;
        Log.d(NetworkTaskAdapter.class.getName(), "binding status text " + formattedStatusText);
        networkTaskViewHolder.setStatus(formattedStatusText, statusImage);
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
        return networkTasks.size();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}