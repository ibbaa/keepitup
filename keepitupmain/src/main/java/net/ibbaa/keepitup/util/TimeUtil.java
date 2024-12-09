/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.util;

import android.content.Context;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.service.ITimeService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static long getTimestampToday(Time time, ITimeService timeService) {
        return getTimestampToday(time, timeService.getCurrentTimestamp());
    }

    public static long getTimestampTomorrow(Time time, ITimeService timeService) {
        return getTimestampTomorrow(time, timeService.getCurrentTimestamp());
    }

    public static long getTimestampToday(Time time, long currentTime) {
        return getCalendarFromTime(time, currentTime).getTimeInMillis();
    }

    public static long getTimestampTomorrow(Time time, long currentTime) {
        Calendar date = getCalendarFromTime(time, currentTime);
        date.add(Calendar.DATE, 1);
        return date.getTimeInMillis();
    }

    public static long getRelativeTimestamp(Time time) {
        Calendar date = getCalendarFromTime(time, 0);
        return date.getTimeInMillis();
    }

    public static int getDistance(Time startTime, Time endTime) {
        long start = getTimestampToday(startTime, 0);
        long end = endTime.isBefore(startTime) ? getTimestampTomorrow(endTime, 0) : getTimestampToday(endTime, 0);
        return (int) TimeUnit.MINUTES.convert(end - start, TimeUnit.MILLISECONDS);
    }

    public static int getDuration(Interval interval) {
        if (!interval.isValid()) {
            return 0;
        }
        return getDistance(interval.getStart(), interval.getEnd());
    }

    public static boolean isDurationMin(Interval interval, int minutes) {
        long duration = getDuration(interval);
        return duration >= minutes;
    }

    public static Interval getCurrentGap(List<Interval> intervals, Time time) {
        if (intervals == null || intervals.isEmpty()) {
            return getFullGap();
        }
        if (intervals.size() == 1) {
            return getSingleGap(intervals.get(0));
        }
        for (int ii = 0; ii < intervals.size(); ii++) {
            Interval currentGap = getGap(intervals, ii);
            if (currentGap.isInInterval(time)) {
                return currentGap;
            }
        }
        return getFullGap();
    }

    public static Interval getLargestGap(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return getFullGap();
        }
        if (intervals.size() == 1) {
            return getSingleGap(intervals.get(0));
        }
        Interval largestGap = new Interval();
        for (int ii = 0; ii < intervals.size(); ii++) {
            Interval currentGap = getGap(intervals, ii);
            if (getDuration(currentGap) >= getDuration(largestGap)) {
                largestGap = currentGap;
            }
        }
        return largestGap;
    }

    private static Interval getFullGap() {
        Time start = new Time();
        start.setHour(0);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(23);
        end.setMinute(59);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        return interval;
    }

    private static Interval getSingleGap(Interval interval) {
        Interval newInterval = new Interval();
        newInterval.setStart(interval.getEnd());
        newInterval.setEnd(interval.getStart());
        return newInterval;
    }

    private static Interval getGap(List<Interval> intervals, int ii) {
        Interval interval = intervals.get(ii);
        Interval nextInterval = ii == intervals.size() - 1 ? intervals.get(0) : intervals.get(ii + 1);
        Interval currentGap = new Interval();
        currentGap.setStart(interval.getEnd());
        currentGap.setEnd(nextInterval.getStart());
        return currentGap;
    }


    public static String formatSuspensionIntervalText(Interval interval, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date start = new Date(getRelativeTimestamp(interval.getStart()));
        Date end = new Date(getRelativeTimestamp(interval.getEnd()));
        @SuppressWarnings({"StringBufferReplaceableByString"})
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getResources().getString(R.string.string_start));
        stringBuilder.append(": ");
        stringBuilder.append(dateFormat.format(start));
        stringBuilder.append(" ");
        stringBuilder.append(context.getResources().getString(R.string.string_end));
        stringBuilder.append(": ");
        stringBuilder.append(dateFormat.format(end));
        return stringBuilder.toString();
    }

    public static Interval extendInterval(Interval interval, int minutes) {
        Interval extendedInterval = new Interval();
        extendedInterval.setStart(interval.getStart());
        extendedInterval.setEnd(addMinutes(interval.getEnd(), minutes));
        return extendedInterval;
    }

    public static Time addMinutes(Time time, int minutes) {
        Time changedTime = new Time();
        Date now = new Date();
        Calendar changedDate = new GregorianCalendar();
        changedDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        changedDate.setTime(now);
        changedDate.set(Calendar.HOUR_OF_DAY, time.getHour());
        changedDate.set(Calendar.MINUTE, time.getMinute());
        changedDate.add(Calendar.MINUTE, minutes);
        changedTime.setHour(changedDate.get(Calendar.HOUR_OF_DAY));
        changedTime.setMinute(changedDate.get(Calendar.MINUTE));
        return changedTime;
    }

    public static Time substractMinutes(Time time, int minutes) {
        return addMinutes(time, -minutes);
    }

    public static List<Interval> sortIntervalList(List<Interval> intervalList) {
        List<Interval> sortedList = new ArrayList<>(intervalList);
        Collections.sort(sortedList, new IntervalComparator());
        return sortedList;
    }

    private static Calendar getCalendarFromTime(Time time, long currentTime) {
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(currentTime));
        date.set(Calendar.HOUR_OF_DAY, time.getHour());
        date.set(Calendar.MINUTE, time.getMinute());
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    private static class IntervalComparator implements Comparator<Interval> {
        @Override
        public int compare(Interval interval1, Interval interval2) {
            if (interval1.isEqual(interval2)) {
                return 0;
            }
            return interval1.getStart().isBefore(interval2.getStart()) ? -1 : 1;
        }
    }
}
