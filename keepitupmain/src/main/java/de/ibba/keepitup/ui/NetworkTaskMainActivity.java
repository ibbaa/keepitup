package de.ibba.keepitup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.service.SchedulerIdGenerator;
import de.ibba.keepitup.ui.dialog.GeneralConfirmDialog;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.ui.dialog.NetworkTaskEditDialog;
import de.ibba.keepitup.util.BundleUtil;

public class NetworkTaskMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network_task);
        RecyclerView recyclerView = findViewById(R.id.listview_main_activity_network_tasks);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new NetworkTaskAdapter(readNetworkTasksFromDatabase(), this));
    }

    private List<NetworkTask> readNetworkTasksFromDatabase() {
        Log.d(NetworkTaskMainActivity.class.getName(), "readNetworkTasksFromDatabase");
        NetworkTaskDAO dao = new NetworkTaskDAO(this);
        try {
            List<NetworkTask> tasks = dao.readAllNetworkTasks();
            Log.d(NetworkTaskMainActivity.class.getName(), "Database returned:");
            for (NetworkTask currentTask : tasks) {
                Log.d(NetworkTaskMainActivity.class.getName(), currentTask.toString());
            }
            return tasks;
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SettingsActivity.SETTING_ACTIVITY_CODE);
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
        NetworkTask networkTask = getAdapter().getItem(position);
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked for network task " + networkTask);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        if (scheduler.isRunning(networkTask)) {
            stopNetworkTask(networkTask);
        } else {
            startNetworkTask(networkTask);
        }
        getAdapter().notifyItemChanged(position);
    }

    public void onMainDeleteClicked(int position) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainDeleteClicked for position " + position + ", opening " + GeneralConfirmDialog.class.getSimpleName() + " for type " + GeneralConfirmDialog.Type.DELETE);
        showConfirmDialog(getResources().getString(R.string.text_dialog_general_confirm_delete_network_task), GeneralConfirmDialog.Type.DELETE, position);
    }

    public void onMainEditClicked(int position) {
        NetworkTask task = getAdapter().getItem(position);
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainAddClicked for network task " + task);
        NetworkTaskEditDialog editDialog = new NetworkTaskEditDialog();
        editDialog.setArguments(task.toBundle());
        Log.d(NetworkTaskMainActivity.class.getName(), "opening " + NetworkTaskEditDialog.class.getSimpleName());
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainLogClicked(int position) {
        NetworkTask networkTask = getAdapter().getItem(position);
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainLogClicked for network task " + networkTask);
    }

    public void onEditDialogOkClicked(NetworkTaskEditDialog editDialog) {
        NetworkTask task = editDialog.getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is " + task);
        NetworkTaskDAO dao = new NetworkTaskDAO(this);
        if (task.getId() < 0) {
            int index = getAdapter().getNextIndex();
            task.setIndex(index);
            Log.d(NetworkTaskMainActivity.class.getName(), "Network task is new, inserting " + task);
            try {
                task = dao.insertNetworkTask(task);
                if (task.getId() < 0) {
                    Log.e(NetworkTaskMainActivity.class.getName(), "Error inserting task into database. Showing error dialog.");
                    showErrorDialog(getResources().getString(R.string.text_dialog_general_error_insert_network_task));
                } else {
                    getAdapter().addItem(task);
                }
            } catch (Exception exc) {
                Log.e(NetworkTaskMainActivity.class.getName(), "Error inserting task into database. Showing error dialog.", exc);
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_insert_network_task));
            }
        } else {
            Log.d(NetworkTaskMainActivity.class.getName(), "Updating " + task);
            try {
                dao.updateNetworkTask(task);
                NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
                if (scheduler.isRunning(task)) {
                    Log.d(NetworkTaskMainActivity.class.getName(), "Network task is running. Restarting.");
                    scheduler.stop(task);
                    scheduler.start(task);
                }
                getAdapter().replaceItem(task);
            } catch (Exception exc) {
                Log.e(NetworkTaskMainActivity.class.getName(), "Error updating task. Showing error dialog.", exc);
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_update_network_task));
            }
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
            doNetworkTaskDelete(confirmDialog);
        } else {
            Log.e(NetworkTaskMainActivity.class.getName(), "unknown type " + type);
        }
        confirmDialog.dismiss();
    }

    public void onConfirmDialogCancelClicked(GeneralConfirmDialog confirmDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onConfirmDialogCancelClicked");
        confirmDialog.dismiss();
    }

    private void startNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskMainActivity.class.getName(), "startNetworkTask");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        NetworkTaskDAO dao = new NetworkTaskDAO(this);
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(this);
        int schedulerId = idGenerator.createSchedulerId();
        try {
            dao.updateNetworkTaskSchedulerId(networkTask.getId(), schedulerId);
            networkTask.setSchedulerid(schedulerId);
            scheduler.start(networkTask);
        } catch (Exception exc) {
            Log.e(NetworkTaskMainActivity.class.getName(), "Error updating scheduler id to " + schedulerId + ". Showing error dialog.", exc);
            showErrorDialog(getResources().getString(R.string.text_dialog_general_error_start_network_task));
        }
    }

    private void stopNetworkTask(NetworkTask networkTask) {
        Log.d(NetworkTaskMainActivity.class.getName(), "stopNetworkTask");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        NetworkTaskDAO dao = new NetworkTaskDAO(this);
        scheduler.stop(networkTask);
        networkTask.setSchedulerid(-1);
        try {
            dao.updateNetworkTaskSchedulerId(networkTask.getId(), -1);
        } catch (Exception exc) {
            Log.e(NetworkTaskMainActivity.class.getName(), "Error updating scheduler id to -1", exc);
        }
    }

    private void showErrorDialog(String errorMessage) {
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.messageToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage));
        errorDialog.show(getSupportFragmentManager(), GeneralErrorDialog.class.getName());
    }

    private void showConfirmDialog(String confirmMessage, GeneralConfirmDialog.Type type, int position) {
        GeneralConfirmDialog confirmDialog = new GeneralConfirmDialog();
        Bundle bundle = BundleUtil.messagesToBundle(new String[]{GeneralConfirmDialog.class.getSimpleName(), GeneralConfirmDialog.Type.class.getSimpleName()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(getConfirmDialogPositionKey(), position);
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), GeneralConfirmDialog.class.getName());
    }

    private String getConfirmDialogPositionKey() {
        return GeneralConfirmDialog.class.getSimpleName() + ".position";
    }

    private void doNetworkTaskDelete(GeneralConfirmDialog confirmDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "doNetworkTaskDelete, performing delete");
        Bundle arguments = confirmDialog.getArguments();
        if (arguments.containsKey(getConfirmDialogPositionKey())) {
            try {
                NetworkTaskDAO dao = new NetworkTaskDAO(this);
                int position = arguments.getInt(getConfirmDialogPositionKey());
                NetworkTask task = getAdapter().getItem(position);
                NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
                if (scheduler.isRunning(task)) {
                    Log.d(NetworkTaskMainActivity.class.getName(), "Network task is running. Stopping.");
                    scheduler.stop(task);
                }
                dao.deleteNetworkTask(task);
                getAdapter().removeItem(task);
                getAdapter().notifyDataSetChanged();
            } catch (Exception exc) {
                Log.e(NetworkTaskMainActivity.class.getName(), "Error deleting network task.", exc);
                showErrorDialog(getResources().getString(R.string.text_dialog_general_error_delete_network_task));
            }
        } else {
            Log.e(NetworkTaskMainActivity.class.getName(), GeneralConfirmDialog.class.getSimpleName() + " arguments do not contain position key " + getConfirmDialogPositionKey());
        }
    }

    private NetworkTaskAdapter getAdapter() {
        RecyclerView recyclerView = findViewById(R.id.listview_main_activity_network_tasks);
        return (NetworkTaskAdapter) recyclerView.getAdapter();
    }
}
