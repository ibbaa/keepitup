package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

public class NetworkTaskLogActivity extends RecyclerViewBaseActivity {

    @Override
    protected int getRecyclerViewId() {
        return R.id.listview_log_activity_log_entries;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
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

    private List<LogEntry> readLogEntriesFromDatabase(NetworkTask task) {
        Log.d(NetworkTaskLogActivity.class.getName(), "readLogEntriesFromDatabase");
        LogDAO logDAO = new LogDAO(this);
        try {
            Log.d(NetworkTaskLogActivity.class.getName(), "Reading log entries for network task " + task);
            List<LogEntry> logEntries = logDAO.readAllLogsForNetworkTask(task.getId());
            Log.d(NetworkTaskLogActivity.class.getName(), "Database returned the following log entries: " + (logEntries.isEmpty() ? "no log entries" : ""));
            for (LogEntry logEntry : logEntries) {
                Log.d(NetworkTaskLogActivity.class.getName(), logEntry.toString());
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
        if (id == R.id.menu_action_refresh) {
            Log.d(NetworkTaskLogActivity.class.getName(), "menu_action_refresh triggered");
            if (getIntent() != null && getIntent().getExtras() != null) {
                NetworkTask task = new NetworkTask(Objects.requireNonNull(getIntent().getExtras()));
                List<LogEntry> logEntries = readLogEntriesFromDatabase(task);
                ((LogEntryAdapter) getAdapter()).replaceItems(logEntries);
                getAdapter().notifyDataSetChanged();
            } else {
                Log.e(NetworkTaskLogActivity.class.getName(), "No network task intent present");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
