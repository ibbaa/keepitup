package de.ibba.keepitup.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.util.NumberUtil;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class NetworkKeppAliveServiceScheduler {

    private final Context context;

    public NetworkKeppAliveServiceScheduler(Context context) {
        this.context = context;
    }

    public void start() {
        Log.d(NetworkKeppAliveServiceScheduler.class.getName(), "start");
        if (isRunning()) {
            Log.d(NetworkKeppAliveServiceScheduler.class.getName(), "Service is already running. Stopping...");
            stop();
        }
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getContext(), NetworkKeepAliveService.class);
        long interval = getIntervalMillisceonds();
        JobInfo jobInfo = new JobInfo.Builder(NetworkKeepAliveService.JOBID, componentName).setPeriodic(interval).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(false).build();
        Log.d(NetworkKeppAliveServiceScheduler.class.getName(), "Starting service with periodic interval of " + interval + " msec");
        jobScheduler.schedule(jobInfo);
    }

    public void stop() {
        Log.d(NetworkKeppAliveServiceScheduler.class.getName(), "stop");
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        Log.d(NetworkKeppAliveServiceScheduler.class.getName(), "Stopping service...");
        jobScheduler.cancel(NetworkKeepAliveService.JOBID);
    }

    public boolean isRunning() {
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobList = jobScheduler.getAllPendingJobs();
        if (jobList == null || jobList.isEmpty()) {
            return false;
        }
        for (JobInfo currentJob : jobList) {
            if (currentJob.getId() == NetworkKeepAliveService.JOBID) {
                return true;
            }
        }
        return false;
    }

    private long getIntervalMillisceonds() {
        return 60 * 1000 * NumberUtil.getPreferenceLongSetting(getResources().getString(R.string.interval_setting_key), getResources().getInteger(R.integer.interval_setting_default), context);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
