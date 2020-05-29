package de.ibba.keepitup.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;
import de.ibba.keepitup.ui.dialog.ConfirmDialog;
import de.ibba.keepitup.ui.sync.LogEntryUIBroadcastReceiver;
import de.ibba.keepitup.ui.sync.LogEntryUIInitTask;

public class NetworkTaskLogActivity extends RecyclerViewBaseActivity {

    private LogEntryUIBroadcastReceiver broadcastReceiver;
    private LogEntryUIInitTask uiInitTask;

    public void injectUIInitTask(LogEntryUIInitTask uiInitTask) {
        this.uiInitTask = uiInitTask;
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.listview_log_activity_log_entries;
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
        NetworkTask task = new NetworkTask(Objects.requireNonNull(getIntent().getExtras()));
        LogEntryUIInitTask uiInitTask = getUIInitTask((LogEntryAdapter) getAdapter());
        uiInitTask.start(task);
    }

    @Override
    protected void onPause() {
        Log.d(NetworkTaskLogActivity.class.getName(), "onPause");
        super.onPause();
        unregisterReceiver();
    }

    private void registerReceiver() {
        Log.d(NetworkTaskLogActivity.class.getName(), "registerReceiver");
        unregisterReceiver();
        broadcastReceiver = new LogEntryUIBroadcastReceiver((LogEntryAdapter) getAdapter());
        registerReceiver(broadcastReceiver, new IntentFilter(LogEntryUIBroadcastReceiver.class.getName()));
    }

    private void unregisterReceiver() {
        Log.d(NetworkTaskLogActivity.class.getName(), "unregisterReceiver");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private List<LogEntry> readLogEntriesFromDatabase(NetworkTask task) {
        Log.d(NetworkTaskLogActivity.class.getName(), "readLogEntriesFromDatabase");
        try {
            LogEntryUIInitTask uiInitTask = getUIInitTask(null);
            uiInitTask.start(task);
            List<LogEntry> logEntries = uiInitTask.get(getResources().getInteger(R.integer.database_access_timeout), TimeUnit.SECONDS);
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
        getMenuInflater().inflate(R.menu.menu_log, menu);
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

    private LogEntryUIInitTask getUIInitTask(LogEntryAdapter adapter) {
        if (uiInitTask != null) {
            return uiInitTask;
        }
        return new LogEntryUIInitTask(this, adapter);
    }
}
