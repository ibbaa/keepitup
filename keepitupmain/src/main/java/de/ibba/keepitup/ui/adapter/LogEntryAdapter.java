package de.ibba.keepitup.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.NetworkTaskLogActivity;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryViewHolder> {

    private final NetworkTask networkTask;
    private final List<LogEntry> logEntries;
    private final NetworkTaskLogActivity logActivity;

    public LogEntryAdapter(NetworkTask networkTask, List<LogEntry> logEntries, NetworkTaskLogActivity logActivity) {
        this.networkTask = networkTask;
        this.logEntries = new ArrayList<>();
        this.logActivity = logActivity;
        replaceItems(logEntries);
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
        if (!logEntries.isEmpty()) {
            if (position < logEntries.size()) {
                LogEntry logEntry = logEntries.get(position);
                bindTitle(logEntryViewHolder);
                bindSuccess(logEntryViewHolder, logEntry);
                bindTimestamp(logEntryViewHolder, logEntry);
                bindMessage(logEntryViewHolder, logEntry);
                logEntryViewHolder.showLogEntryCardView();
                logEntryViewHolder.hideNoLogTextView();
            } else {
                logEntryViewHolder.hideLogEntryCardView();
                logEntryViewHolder.hideNoLogTextView();
            }
            logEntryViewHolder.hideNoLogTextView();
        } else {
            bindNoLog(logEntryViewHolder);
            logEntryViewHolder.hideLogEntryCardView();
            logEntryViewHolder.showNoLogTextView();
        }
    }

    private void bindNoLog(@NonNull LogEntryViewHolder logEntryViewHolder) {
        String formattedNoLogText = getResources().getString(R.string.text_activity_log_list_item_log_entry_no_log, networkTask.getIndex() + 1);
        logEntryViewHolder.setNoLogText(formattedNoLogText);
    }

    private void bindTitle(@NonNull LogEntryViewHolder logEntryViewHolder) {
        String formattedTitleText = getResources().getString(R.string.text_activity_log_list_item_log_entry_title, networkTask.getIndex() + 1);
        logEntryViewHolder.setTitleText(formattedTitleText);
    }

    private void bindSuccess(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        String successText = logEntry.isSuccess() ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful);
        String formattedSuccessText = getResources().getString(R.string.text_activity_log_list_item_log_entry_success, successText);
        logEntryViewHolder.setSuccessText(formattedSuccessText);
    }

    private void bindTimestamp(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        String formattedTimestampText = getResources().getString(R.string.text_activity_log_list_item_log_entry_timestamp, timestampText);
        logEntryViewHolder.setTimestampText(formattedTimestampText);
    }

    private void bindMessage(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        String formattedMessageText = getResources().getString(R.string.text_activity_log_list_item_log_entry_message, logEntry.getMessage());
        logEntryViewHolder.setMessageText(formattedMessageText);
    }

    public LogEntry getItem(int position) {
        return logEntries.get(position);
    }

    @Override
    public int getItemCount() {
        return logEntries.size() <= 0 ? 1 : logEntries.size();
    }

    public void addItem(LogEntry logEntry) {
        this.logEntries.add(0, logEntry);
        int limit = getContext().getResources().getInteger(R.integer.log_count_maximum);
        if (logEntries.size() > limit && logEntries.size() > 0) {
            logEntries.remove(logEntries.size() - 1);
        }
    }

    public void replaceItems(List<LogEntry> logEntries) {
        this.logEntries.clear();
        int limit = getContext().getResources().getInteger(R.integer.log_count_maximum);
        this.logEntries.addAll(logEntries.size() > limit ? logEntries.subList(0, limit) : logEntries);
    }

    private Context getContext() {
        return logActivity;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
