package de.ibba.keepitup.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;

import de.ibba.keepitup.model.NetworkTask;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class NetworkKeepAliveServiceScheduler {

    private final Context context;

    public NetworkKeepAliveServiceScheduler(Context context) {
        this.context = context;
    }

    public void start(NetworkTask networkTask) {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Start network job " + networkTask);
        if (isRunning(networkTask)) {
            Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Network job " + networkTask + " is already running. Stopping...");
            stop(networkTask);
        }
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getContext(), NetworkKeepAliveService.class);
        long interval = getIntervalMilliseconds(networkTask);
        JobInfo jobInfo = new JobInfo.Builder(networkTask.getId(), componentName).setPeriodic(interval).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(false).setExtras(networkTask.toPersistableBundle()).build();
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Starting NetworkKeepAliveService with periodic interval of " + interval + " msec for job " + networkTask);
        jobScheduler.schedule(jobInfo);
    }

    public void stop(NetworkTask networkTask) {
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Stop network job " + networkTask);
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        Log.d(NetworkKeepAliveServiceScheduler.class.getName(), "Stopping NetworkKeepAliveService for job " + networkTask);
        jobScheduler.cancel(networkTask.getId());
    }

    public boolean isRunning(NetworkTask networkTask) {
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobList = jobScheduler.getAllPendingJobs();
        for (JobInfo currentJob : jobList) {
            if (currentJob.getId() == networkTask.getId()) {
                return true;
            }
        }
        return false;
    }

    private long getIntervalMilliseconds(NetworkTask networkTask) {
        return 60 * 1000 * networkTask.getInterval();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
