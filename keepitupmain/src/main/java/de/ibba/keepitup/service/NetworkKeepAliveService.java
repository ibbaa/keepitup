package de.ibba.keepitup.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import de.ibba.keepitup.model.NetworkTask;

public class NetworkKeepAliveService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(NetworkKeepAliveService.class.getName(), "onStartJob");
        NetworkTask job = new NetworkTask(jobParameters.getExtras());
        Log.d(NetworkKeepAliveService.class.getName(), "Configured job: " + job);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(NetworkKeepAliveService.class.getName(), "onStopJob");
        NetworkTask job = new NetworkTask(jobParameters.getExtras());
        Log.d(NetworkKeepAliveService.class.getName(), "Configured job: " + job);
        return false;
    }
}
