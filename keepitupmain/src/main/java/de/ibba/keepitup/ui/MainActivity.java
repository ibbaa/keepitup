package de.ibba.keepitup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.ui.mapping.NetworkTaskAdapter;

public class MainActivity extends AppCompatActivity {

    private NetworkTaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this::onStartClicked);
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(this::onStopClicked);

        RecyclerView recyclerView = findViewById(R.id.listview_network_tasks);
        List<NetworkTask> taskList = prepareTaskList();
        adapter = new NetworkTaskAdapter(taskList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
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
            Log.d(MainActivity.class.getName(), "menu_action_settings triggered");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SettingsActivity.SETTING_ACTIVITY_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MainActivity.class.getName(), "onActivityResult");
    }

    @SuppressWarnings("unused")
    private void onStartClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStartClicked");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setInterval(15);
        scheduler.start(task);
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    private void onStopClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStopClicked");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setInterval(15);
        scheduler.stop(task);
        adapter.notifyDataSetChanged();
    }
}
