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

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;

public class NetworkTaskMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network_task);
        RecyclerView recyclerView = findViewById(R.id.listview_main_activity_network_tasks);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new NetworkTaskAdapter(new ArrayList<>(), this));
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
        Log.d(NetworkTaskMainActivity.class.getName(), "opening NetworkTaskEditDialog");
        editDialog.show(getSupportFragmentManager(), NetworkTaskEditDialog.class.getName());
    }

    public void onMainStartStopClicked(int position) {
        NetworkTask networkTask = getAdapter().getItem(position);
        Log.d(NetworkTaskMainActivity.class.getName(), "onMainStartStopClicked for network task " + networkTask);
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        if (scheduler.isRunning(networkTask)) {
            scheduler.stop(networkTask);
        } else {
            scheduler.start(networkTask);
        }
        getAdapter().notifyItemChanged(position);
    }

    public void onEditDialogOkClicked(NetworkTaskEditDialog editDialog) {
        NetworkTask task = editDialog.getNetworkTask();
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is " + task);
        NetworkTaskDAO dao = new NetworkTaskDAO(this);
        if (task.getId() < 0) {
            int index = getAdapter().getNextIndex();
            task.setIndex(index);
            Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is new, inserting " + task);
            task = dao.insertNetworkTask(task);
            getAdapter().addItem(task);
        } else {
            Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogOkClicked, network task is new, updating " + task);
            dao.updateNetworkTask(task);
            getAdapter().replaceItem(task);
        }
        getAdapter().notifyDataSetChanged();
        editDialog.dismiss();
    }

    public void onEditDialogCancelClicked(NetworkTaskEditDialog editDialog) {
        Log.d(NetworkTaskMainActivity.class.getName(), "onEditDialogCancelClicked");
        editDialog.dismiss();
    }

    private NetworkTaskAdapter getAdapter() {
        RecyclerView recyclerView = findViewById(R.id.listview_main_activity_network_tasks);
        return (NetworkTaskAdapter) recyclerView.getAdapter();
    }
}
