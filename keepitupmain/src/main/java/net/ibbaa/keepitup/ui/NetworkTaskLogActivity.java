/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.sync.LogEntryUIBroadcastReceiver;
import net.ibbaa.keepitup.ui.sync.LogEntryUIInitTask;
import net.ibbaa.keepitup.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NetworkTaskLogActivity extends RecyclerViewBaseActivity {

    private LogEntryUIBroadcastReceiver broadcastReceiver;
    private MenuAdapterDataObserver menuAdapterDataObserver;

    @Override
    protected int getRecyclerViewId() {
        return R.id.listview_activity_log_log_entries;
    }

    @Override
    protected RecyclerView.Adapter<?> createAdapter() {
        NetworkTask task = new NetworkTask(Objects.requireNonNull(getIntent().getExtras()));
        return new LogEntryAdapter(task, readLogEntriesFromDatabase(task), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskLogActivity.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_network_task);
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        Log.d(NetworkTaskLogActivity.class.getName(), "onResume");
        super.onResume();
        registerReceiver();
        registerMenuAdapterDataObserver();
        NetworkTask task = new NetworkTask(Objects.requireNonNull(getIntent().getExtras()));
        LogEntryUIInitTask uiInitTask = getUIInitTask(task, (LogEntryAdapter) getAdapter());
        ThreadUtil.exexute(uiInitTask);
    }

    @Override
    protected void onPause() {
        Log.d(NetworkTaskLogActivity.class.getName(), "onPause");
        super.onPause();
        unregisterReceiver();
        unregisterMenuAdapterDataObserver();
    }

    private void registerReceiver() {
        Log.d(NetworkTaskLogActivity.class.getName(), "registerReceiver");
        unregisterReceiver();
        broadcastReceiver = new LogEntryUIBroadcastReceiver(this, (LogEntryAdapter) getAdapter());
        registerReceiver(broadcastReceiver, new IntentFilter(LogEntryUIBroadcastReceiver.class.getName()));
    }

    private void unregisterReceiver() {
        Log.d(NetworkTaskLogActivity.class.getName(), "unregisterReceiver");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void registerMenuAdapterDataObserver() {
        Log.d(NetworkTaskLogActivity.class.getName(), "registerMenuAdapterDataObserver");
        unregisterMenuAdapterDataObserver();
        if (menuAdapterDataObserver == null) {
            menuAdapterDataObserver = new MenuAdapterDataObserver();
            getAdapter().registerAdapterDataObserver(menuAdapterDataObserver);
        }
    }

    private void unregisterMenuAdapterDataObserver() {
        Log.d(NetworkTaskLogActivity.class.getName(), "unregisterMenuAdapterDataObserver");
        if (menuAdapterDataObserver != null) {
            getAdapter().unregisterAdapterDataObserver(menuAdapterDataObserver);
            menuAdapterDataObserver = null;
        }
    }

    private List<LogEntry> readLogEntriesFromDatabase(NetworkTask task) {
        Log.d(NetworkTaskLogActivity.class.getName(), "readLogEntriesFromDatabase");
        try {
            LogEntryUIInitTask uiInitTask = getUIInitTask(task, null);
            Future<List<LogEntry>> logEntriesFuture = ThreadUtil.exexute(uiInitTask);
            List<LogEntry> logEntries = logEntriesFuture.get(getResources().getInteger(R.integer.database_access_timeout), TimeUnit.SECONDS);
            if (logEntries == null) {
                Log.e(NetworkTaskLogActivity.class.getName(), "Reading all log entries from database returned null");
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_read_log_entries));
                return new ArrayList<>();
            }
            return logEntries;
        } catch (Exception exc) {
            Log.e(NetworkTaskLogActivity.class.getName(), "Error reading all log entries from database", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_read_log_entries));
            return new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(NetworkTaskLogActivity.class.getName(), "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_log, menu);
        MenuItem item = menu.findItem(R.id.menu_action_activity_log_delete);
        LogEntryAdapter adapter = (LogEntryAdapter) getAdapter();
        if (adapter.hasValidEntries()) {
            Log.d(NetworkTaskLogActivity.class.getName(), "Adapter has valid entries. Showing log delete menu item.");
            item.setVisible(true);
        } else {
            Log.d(NetworkTaskLogActivity.class.getName(), "Adapter has no valid entries. Hiding log delete menu item.");
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_activity_log_delete) {
            Log.d(NetworkTaskLogActivity.class.getName(), "menu_action_activity_log_delete triggered");
            showConfirmDialog(getResources().getString(R.string.text_dialog_confirm_delete_logs), ConfirmDialog.Type.DELETELOGS);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onConfirmDialogOkClicked(ConfirmDialog confirmDialog, ConfirmDialog.Type type) {
        Log.d(NetworkTaskLogActivity.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (ConfirmDialog.Type.DELETELOGS.equals(type)) {
            NetworkTask task = new NetworkTask(Objects.requireNonNull(getIntent().getExtras()));
            LogHandler handler = new LogHandler(this);
            handler.deleteLogsForNetworkTask(task);
            getAdapter().notifyDataSetChanged();
        } else {
            Log.e(NetworkTaskLogActivity.class.getName(), "Unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    public void onConfirmDialogCancelClicked(ConfirmDialog confirmDialog) {
        Log.d(NetworkTaskLogActivity.class.getName(), "onConfirmDialogCancelClicked");
        confirmDialog.dismiss();
    }

    private LogEntryUIInitTask getUIInitTask(NetworkTask task, LogEntryAdapter adapter) {
        return new LogEntryUIInitTask(this, task, adapter);
    }

    private class MenuAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            invalidateOptionsMenu();
        }
    }
}
