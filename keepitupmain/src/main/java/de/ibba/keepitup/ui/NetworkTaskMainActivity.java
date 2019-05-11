package de.ibba.keepitup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;
import de.ibba.keepitup.ui.dialog.GeneralConfirmDialog;
import de.ibba.keepitup.ui.dialog.NetworkTaskEditDialog;
import de.ibba.keepitup.ui.sync.UISyncController;

public class NetworkTaskMainActivity extends RecyclerViewBaseActivity {

    @Override
    protected int getRecyclerViewId() {
        return R.id.listview_main_activity_network_tasks;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        return new NetworkTaskAdapter(readNetworkTasksFromDatabase(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network_task);
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        Log.d(NetworkTaskMainActivity.class.getName(), "onStart");
        super.onStart();
        UISyncController.start((NetworkTaskAdapter) getAdapter());
    }

    @Override
    protected void onStop() {
        Log.d(NetworkTaskMainActivity.class.getName(), "onStop");
        super.onStop();
        UISyncController.stop();
    }

    private List<NetworkTaskUIWrapper> readNetworkTasksFromDatabase() {
        Log.d(NetworkTaskMainActivity.class.getName(), "readNetworkTasksFromDatabase");
        NetworkTaskDAO networkTaskDAO = new NetworkTaskDAO(this);
        LogDAO logDAO = new LogDAO(this);
        try {
            List<NetworkTaskUIWrapper> wrapperList = new ArrayList<>();
            List<NetworkTask> tasks = networkTaskDAO.readAllNetworkTasks();
            Log.d(NetworkTaskMainActivity.class.getName(), "Database returned the following network tasks: " + (tasks.isEmpty() ? "no network tasks" : ""));
            for (NetworkTask currentTask : tasks) {
                Log.d(NetworkTaskMainActivity.class.getName(), currentTask.toString());
            }
            for (NetworkTask currentTask : tasks) {
                Log.d(NetworkTaskMainActivity.class.getName(), "Reading most recent log for " + currentTask.toString());
                LogEntry logEntry = logDAO.readMostRecentLogForNetworkTask(currentTask.getId());
                Log.d(NetworkTaskMainActivity.class.getName(), "Database returned the following log entry: " + (logEntry == null ? "no log entry" : logEntry.toString()));
                NetworkTaskUIWrapper currentWrapper = new NetworkTaskUIWrapper(currentTask, logEntry);
                wrapperList.add(currentWrapper);
            }
            return wrapperList;
        } catch (Exception exc) {
            Log.e(NetworkTaskMainActivity.class.getName(), "Error reading all network tasks from database", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_read_network_tasks));
            return new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_settings) {
            Log.d(NetworkTaskMainActivity.class.getName(), "menu_action_settings triggered");
            Intent intent = new Intent(this, NetworkTaskSettingsActivity.class);
            startActivityForResult(intent, NetworkTaskSettingsActivity.SETTING_ACTIVITY_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onActivityResult");
    }

    public void onMainAddClicked(View view) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainAddClicked");
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        NetworkTask task = new NetworkTask(this);
        editDialog.setArguments(task.toBundle());
        Log.d(NetworkTaskMainActivity.class.getName(), "opening " + NetworkTaskEditDialog.class.getSimpleName());
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainStartStopClicked(int position) {
        NetworkTask networkTask = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked for network task " + networkTask);
        NetworkTaskHandler handler = new NetworkTaskHandler(this);
        if (networkTask.isRunning()) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is running, stopping " + networkTask);
            handler.stopNetworkTask(networkTask);
        } else {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is not running, starting " + networkTask);
            handler.startNetworkTask(networkTask);
        }
        getAdapter().notifyItemChanged(position);
    }

    public void onMainDeleteClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainDeleteClicked for position " + position + ", opening " + GeneralConfirmDialog.class.getSimpleName() + " for type " + GeneralConfirmDialog.Type.DELETE);
        showConfirmDialog(getResources().getString(R.string.text_dialog_general_confirm_delete_network_task), GeneralConfirmDialog.Type.DELETE, position);
    }

    public void onMainEditClicked(int position) {
        NetworkTask task = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainEditClicked for network task " + task);
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        editDialog.setArguments(task.toBundle());
        Log.d(NetworkTaskMainActivity.class.getName(), "opening " + NetworkTaskEditDialog.class.getSimpleName());
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainLogClicked(int position) {
        NetworkTask networkTask = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainLogClicked for network task " + networkTask);
        Intent intent = new Intent(this, NetworkTaskLogActivity.class);
        intent.putExtras(networkTask.toBundle());
        startActivity(intent);
    }

    public void onEditDialogOkClicked(NetworkTaskEditDialog editDialog) {
        NetworkTask task = editDialog.getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is " + task);
        NetworkTaskHandler handler = new NetworkTaskHandler(this);
        if (task.getId() < 0) {
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is new, inserting " + task);
            handler.insertNetworkTask(task);
        } else {
            Log.d(NetworkTaskMainActivity.class.getName(), "Updating " + task);
            handler.updateNetworkTask(task);
        }
        getAdapter().notifyDataSetChanged();
        editDialog.dismiss();
    }

    public void onEditDialogCancelClicked(NetworkTaskEditDialog editDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogCancelClicked");
        editDialog.dismiss();
    }

    public void onConfirmDialogOkClicked(GeneralConfirmDialog confirmDialog, GeneralConfirmDialog.Type type) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogOkClicked for type " + type);
        if (GeneralConfirmDialog.Type.DELETE.equals(type)) {
            Bundle arguments = confirmDialog.getArguments();
            if (Objects.requireNonNull(arguments).containsKey(getConfirmDialogPositionKey())) {
                NetworkTaskHandler handler = new NetworkTaskHandler(this);
                int position = arguments.getInt(getConfirmDialogPositionKey());
                NetworkTask task = ((NetworkTaskAdapter) getAdapter()).getItem(position).getNetworkTask();
                Log.d(NetworkTaskMainActivity.class.getName(), "Deleting " + task);
                handler.deleteNetworkTask(task);
            } else {
                Log.e(NetworkTaskMainActivity.class.getName(), GeneralConfirmDialog.class.getSimpleName() + " arguments do not contain position key " + getConfirmDialogPositionKey());
            }
        } else {
            Log.e(NetworkTaskMainActivity.class.getName(), "unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    public void onConfirmDialogCancelClicked(GeneralConfirmDialog confirmDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogCancelClicked");
        confirmDialog.dismiss();
    }
}
