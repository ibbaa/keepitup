package de.ibba.keepitup.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;

import de.ibba.keepitup.model.NetworkJob;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class NetworkKeepAliveServiceScheduler {

    private final Context context;

    public NetworkKeepAliveServiceScheduler(Context context) {
        this.context = context;
    }

    public void start(NetworkJob networkJob) {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Start network job " + networkJob);
        if (isRunning(networkJob)) {
            Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Network job " + networkJob + " is already running. Stopping...");
            stop(networkJob);
        }
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getContext(), NetworkKeepAliveService.class);
        long interval = getIntervalMilliseconds(networkJob);
        JobInfo jobInfo = new JobInfo.Builder(networkJob.getId(), componentName).setPeriodic(interval).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(false).setExtras(networkJob.toPersistableBundle()).build();
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Starting NetworkKeepAliveService with periodic interval of " + interval + " msec for job " + networkJob);
        jobScheduler.schedule(jobInfo);
    }

    public void stop(NetworkJob networkJob) {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Stop network job " + networkJob);
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Stopping NetworkKeepAliveService for job " + networkJob);
        jobScheduler.cancel(networkJob.getId());
    }

    public boolean isRunning(NetworkJob networkJob) {
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobList = jobScheduler.getAllPendingJobs();
        for (JobInfo currentJob : jobList) {
            if (currentJob.getId() == networkJob.getId()) {
                return true;
            }
        }
        return false;
    }

    private long getIntervalMilliseconds(NetworkJob networkJob) {
        return 60 * 1000 * networkJob.getInterval();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
