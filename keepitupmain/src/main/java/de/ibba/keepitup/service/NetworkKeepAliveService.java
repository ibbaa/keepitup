package de.ibba.keepitup.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class NetworkKeepAliveService extends JobService {

    public static final int JOBID = 1;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(NetworkKeepAliveService.class.getName(), "onStartJob");
        return false;
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(NetworkKeepAliveService.class.getName(), "onStopJob");
        return false;
    }
}
