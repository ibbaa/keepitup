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
import de.ibba.keepitup.model.NetworkJob;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.ui.adapter.NetworkJobAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this::onStartClicked);
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(this::onStopClicked);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_network_jobs);
        List<NetworkJob> jobList = prepareJobList();
        NetworkJobAdapter adapter = new NetworkJobAdapter(jobList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private List<NetworkJob> prepareJobList() {
        List<NetworkJob> jobList = new ArrayList<>();
        NetworkJob job1 = new NetworkJob();
        job1.setId(1);
        job1.setAddress("Address1");
        NetworkJob job2 = new NetworkJob();
        job2.setId(2);
        job2.setAddress("Address2");
        jobList.add(job1);
        jobList.add(job2);
        return jobList;
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
        NetworkJob job = new NetworkJob();
        job.setId(1);
        job.setInterval(15);
        scheduler.start(job);
    }

    @SuppressWarnings("unused")
    private void onStopClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStopClicked");
        NetworkKeepAliveServiceScheduler scheduler = new NetworkKeepAliveServiceScheduler(this);
        NetworkJob job = new NetworkJob();
        job.setId(1);
        job.setInterval(15);
        scheduler.stop(job);
    }
}
