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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogEntryAdapter extends RecyclerView.Adapter<LogEntryViewHolder> {

    private final NetworkTask networkTask;
    private final List<LogEntry> logEntries;
    private final Context context;

    public LogEntryAdapter(NetworkTask networkTask, List<LogEntry> logEntries, Context context) {
        this.networkTask = networkTask;
        this.logEntries = new ArrayList<>();
        this.context = context;
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
        Log.d(LogEntryAdapter.class.getName(), "bindNoLog");
        String formattedNoLogText = getResources().getString(R.string.text_activity_log_list_item_log_entry_no_log, networkTask.getIndex() + 1);
        logEntryViewHolder.setNoLogText(formattedNoLogText);
    }

    private void bindTitle(@NonNull LogEntryViewHolder logEntryViewHolder) {
        Log.d(LogEntryAdapter.class.getName(), "bindTitle");
        String formattedTitleText = getResources().getString(R.string.text_activity_log_list_item_log_entry_title, networkTask.getIndex() + 1);
        logEntryViewHolder.setTitleText(formattedTitleText);
    }

    private void bindSuccess(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        Log.d(LogEntryAdapter.class.getName(), "bindSuccess");
        String successText = logEntry.isSuccess() ? getResources().getString(R.string.string_successful) : getResources().getString(R.string.string_not_successful);
        String formattedSuccessText = getResources().getString(R.string.text_activity_log_list_item_log_entry_success, successText);
        logEntryViewHolder.setSuccessText(formattedSuccessText);
    }

    private void bindTimestamp(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        Log.d(LogEntryAdapter.class.getName(), "bindTimestamp");
        String timestampText = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(logEntry.getTimestamp()));
        String formattedTimestampText = getResources().getString(R.string.text_activity_log_list_item_log_entry_timestamp, timestampText);
        logEntryViewHolder.setTimestampText(formattedTimestampText);
    }

    private void bindMessage(@NonNull LogEntryViewHolder logEntryViewHolder, LogEntry logEntry) {
        Log.d(LogEntryAdapter.class.getName(), "bindMessage");
        String formattedMessageText = getResources().getString(R.string.text_activity_log_list_item_log_entry_message, logEntry.getMessage());
        logEntryViewHolder.setMessageText(formattedMessageText);
    }

    public LogEntry getItem(int position) {
        Log.d(LogEntryAdapter.class.getName(), "getItem for position " + position);
        if (position < 0 || position >= logEntries.size()) {
            Log.e(LogEntryAdapter.class.getName(), "position " + position + " is invalid");
            return null;
        }
        return logEntries.get(position);
    }

    @Override
    public int getItemCount() {
        return logEntries.size() <= 0 ? 1 : logEntries.size();
    }

    public boolean hasValidEntries() {
        return logEntries.size() > 0;
    }

    public void addItem(LogEntry logEntry) {
        Log.d(LogEntryAdapter.class.getName(), "addItem " + logEntry);
        this.logEntries.add(0, logEntry);
        int limit = getContext().getResources().getInteger(R.integer.log_count_maximum);
        if (logEntries.size() > limit && logEntries.size() > 0) {
            logEntries.remove(logEntries.size() - 1);
        }
    }

    public void replaceItems(List<LogEntry> logEntries) {
        Log.d(LogEntryAdapter.class.getName(), "replaceItems");
        this.logEntries.clear();
        int limit = getContext().getResources().getInteger(R.integer.log_count_maximum);
        this.logEntries.addAll(logEntries.size() > limit ? logEntries.subList(0, limit) : logEntries);
    }

    public void removeItems() {
        Log.d(LogEntryAdapter.class.getName(), "removeItems");
        this.logEntries.clear();
    }

    public NetworkTask getNetworkTask() {
        return networkTask;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
