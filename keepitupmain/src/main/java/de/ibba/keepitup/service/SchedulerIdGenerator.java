package de.ibba.keepitup.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;

import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class SchedulerIdGenerator {

    private final Context context;

    public SchedulerIdGenerator(Context context) {
        this.context = context;
    }

    public int createSchedulerId() {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        List<JobInfo> jobList = jobScheduler.getAllPendingJobs();
        for (int ii = 1; ii < Integer.MAX_VALUE; ii++) {
            if (!isInUse(jobList, ii)) {
                return ii;
            }
        }
        return -1;
    }

    private boolean isInUse(List<JobInfo> jobList, int ii) {
        for (JobInfo currentJob : jobList) {
            if (currentJob.getId() == ii) {
                return true;
            }
        }
        return false;
    }
}
