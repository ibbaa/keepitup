package de.ibba.keepitup.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.ibba.keepitup.R;
import de.ibba.keepitup.service.NetworkKeepAliveService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(this::onStartClicked);
        Button stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(this::onStopClicked);
    }

    private void onStartClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStartClicked");
        JobScheduler jobScheduler = (JobScheduler)getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this, NetworkKeepAliveService.class);
        JobInfo jobInfo = new JobInfo.Builder(NetworkKeepAliveService.JOBID, componentName).setPeriodic(15 *60 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(false).build();
        jobScheduler.schedule(jobInfo);
    }

    private void onStopClicked(View view) {
        Log.d(MainActivity.class.getName(), "onStopClicked");
        JobScheduler jobScheduler = (JobScheduler)getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(NetworkKeepAliveService.JOBID);
    }
}
