package de.ibba.keepitup.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import de.ibba.keepitup.model.NetworkJob;

public class NetworkKeepAliveService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(NetworkKeepAliveService.class.getName(), "onStartJob");
        NetworkJob job = new NetworkJob(jobParameters.getExtras());
        Log.d(NetworkKeepAliveService.class.getName(), "Configured job: " + job);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(NetworkKeepAliveService.class.getName(), "onStopJob");
        NetworkJob job = new NetworkJob(jobParameters.getExtras());
        Log.d(NetworkKeepAliveService.class.getName(), "Configured job: " + job);
        return false;
    }
}
