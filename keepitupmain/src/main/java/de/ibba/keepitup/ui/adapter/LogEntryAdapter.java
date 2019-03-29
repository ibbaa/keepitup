package de.ibba.keepitup.ui.adapter;

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
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.NetworkTaskLogActivity;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryViewHolder> {

    private final List<NetworkTaskUIWrapper> networkTaskWrapperList;
    private final NetworkTaskLogActivity logActivity;

    public LogEntryAdapter(List<NetworkTaskUIWrapper> networkTaskWrapperList, NetworkTaskLogActivity logActivity) {
        this.networkTaskWrapperList = networkTaskWrapperList;
        this.logActivity = logActivity;
    }

    @NonNull
    @Override
    public LogEntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(LogEntryAdapter.class.getName(), "onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_log_entry, viewGroup, false);
        return new LogEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LogEntryViewHolder logEntryViewHolder, int position) {
        Log.d(LogEntryAdapter.class.getName(), "onBindViewHolder");
        NetworkTask networkTask = networkTaskWrapperList.get(position).getNetworkTask();
        LogEntry logEntry = networkTaskWrapperList.get(position).getLogEntry();
        bindTitle(logEntryViewHolder, networkTask);
        bindSuccess(logEntryViewHolder, logEntry);
        bindTimestamp(logEntryViewHolder, logEntry);
        bindMessage(logEntryViewHolder, logEntry);
    }

    private void bindTitle(@NonNull LogEntryViewHolder logEntryViewHolder, NetworkTask networkTask) {
        String formattedTitleText = String.format(getResources().getString(R.string.text_list_item_log_entry_title), networkTask.getIndex() + 1);
        logEntryViewHolder.setTitleText(formattedTitleText);
    }

    private void bindSuccess(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        String successText = logEntry.isSuccess() ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful);
        String formattedSuccessText = String.format(getResources().getString(R.string.text_list_item_log_entry_success), successText);
        logEntryViewHolder.setSuccessText(formattedSuccessText);
    }

    private void bindTimestamp(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        String formattedTimestampText = String.format(getResources().getString(R.string.text_list_item_log_entry_timestamp), timestampText);
        logEntryViewHolder.setTimestampText(formattedTimestampText);
    }

    private void bindMessage(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        String formattedMessageText = String.format(getResources().getString(R.string.text_list_item_log_entry_message), logEntry.getMessage());
        logEntryViewHolder.setMessageText(formattedMessageText);
    }

    @Override
    public int getItemCount() {
        return networkTaskWrapperList.size();
    }

    private Context getContext() {
        return logActivity;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
