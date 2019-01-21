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
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;

public class NetworkTaskMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network_task);
        RecyclerView recyclerView = findViewById(R.id.listview_main_activity_network_tasks);
        List<NetworkTask> taskList = prepareTaskList();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new NetworkTaskAdapter(prepareTaskList(), this));
    }

    private List<NetworkTask> prepareTaskList() {
        List<NetworkTask> taskList = new ArrayList<>();
        NetworkTask task1 = new NetworkTask();
        task1.setId(1);
        task1.setAddress("Address1");
        task1.setPort(21);
        task1.setAccessType(AccessType.PING);
        task1.setInterval(15);
        task1.setNotification(true);
        task1.setTimestamp(System.currentTimeMillis());
        task1.setSuccess(true);
        task1.setMessage("Successful execution");
        NetworkTask task2 = new NetworkTask();
        task2.setId(2);
        task2.setAddress("Address2");
        task2.setPort(21);
        task2.setAccessType(null);
        task2.setInterval(30);
        task2.setNotification(false);
        taskList.add(task1);
        taskList.add(task2);
        task2.setTimestamp(-1);
        task2.setSuccess(false);
        task2.setMessage("Not executed");
        return taskList;
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
        Log.d(NetworkTaskMainActivity.class.getName(), "opening NetworkTaskEditDialog");
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainStartStopClicked(int position) {
        NetworkTask networkTask = getApdapter().getItem(position);
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked for network task " + networkTask);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        if (scheduler.isRunning(networkTask)) {
            scheduler.stop(networkTask);
        } else {
            scheduler.start(networkTask);
        }
        getApdapter().notifyItemChanged(position);
    }

    public void onEditDialogOkClicked(NetworkTaskEditDialog editDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked");
        editDialog.dismiss();
    }

    public void onEditDialogCancelClicked(NetworkTaskEditDialog editDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogCancelClicked");
        editDialog.dismiss();
    }

    private NetworkTaskAdapter getApdapter() {
        RecyclerView recyclerView = findViewById(R.id.listview_main_activity_network_tasks);
        return (NetworkTaskAdapter) recyclerView.getAdapter();
    }
}
