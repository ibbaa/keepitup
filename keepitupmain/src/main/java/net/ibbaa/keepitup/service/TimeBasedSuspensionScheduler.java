/*
 * Copyright (c) 2023. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.resources.ServiceFactoryContributor;
import net.ibbaa.keepitup.util.TimeUtil;

import java.util.List;

public class TimeBasedSuspensionScheduler {

    private final Context context;
    private final IntervalDAO intervalDAO;
    private final ITimeService timeService;

    private static List<Interval> intervals;

    public TimeBasedSuspensionScheduler(Context context) {
        this.context = context;
        this.intervalDAO = new IntervalDAO(context);
        this.timeService = createTimeService();
    }

    public List<Interval> getIntervals() {
        synchronized (TimeBasedSuspensionScheduler.class) {
            if (intervals == null) {
                intervals = intervalDAO.readAllIntervals();
            }
            return intervals;
        }
    }

    public static synchronized void reconfigure() {
        intervals = null;
    }

    public boolean isSuspended() {
        if (getIntervals().isEmpty()) {
            return false;
        }
        long now = timeService.getCurrentTimestamp();
        for (Interval interval : intervals) {
            long start = TimeUtil.getTimestampToday(interval.getStart(), now);
            if (now <= start) {
                return false;
            }
            long end = TimeUtil.getTimestampToday(interval.getEnd(), now);
            if (now <= start) {
                return false;
            }
        }
        return false;
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent createPendingIntent(long timestamp, int uniqueCode) {
        Intent intent = new Intent(getContext(), TimeBasedSuspensionBroadcastReceiver.class);
        intent.setPackage(getContext().getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(getContext(), uniqueCode, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            return PendingIntent.getBroadcast(getContext(), uniqueCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
    }

    private Context getContext() {
        return context;
    }

    private ITimeService createTimeService() {
        ServiceFactoryContributor factoryContributor = new ServiceFactoryContributor(getContext());
        return factoryContributor.createServiceFactory().createTimeService();
    }
}