package de.ibba.keepitup.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.util.BundleUtil;

public class NetworkTaskLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskLogActivity.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_network_task);
        RecyclerView recyclerView = findViewById(R.id.listview_log_activity_log_entries);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        NetworkTask task = new NetworkTask(Objects.requireNonNull(getIntent().getExtras()));
        recyclerView.setAdapter(new LogEntryAdapter(task, readLogEntriesFromDatabase(task), this));
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
            if (logEntries.isEmpty()) {
                logEntries = new ArrayList<>();
                for (int ii = 0; ii < 20; ii++) {
                    LogEntry entry = new LogEntry();
                    entry.setId(ii);
                    entry.setNetworkTaskId(task.getId());
                    entry.setSuccess(true);
                    entry.setTimestamp(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime());
                    entry.setMessage("TestMessage");
                    logEntries.add(entry);
                }
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
                getAdapter().replaceItems(logEntries);
                getAdapter().notifyDataSetChanged();
            } else {
                Log.e(NetworkTaskLogActivity.class.getName(), "No network task intent present");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showErrorDialog(String errorMessage) {
        Log.d(NetworkTaskLogActivity.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.messageToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage));
        errorDialog.show(getSupportFragmentManager(), GeneralErrorDialog.class.getName());
    }

    public LogEntryAdapter getAdapter() {
        RecyclerView recyclerView = findViewById(R.id.listview_log_activity_log_entries);
        return (LogEntryAdapter) recyclerView.getAdapter();
    }
}
