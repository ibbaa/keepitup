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

    public static boolean isDurationMin(Interval interval, int minutes) {
        if (!interval.isValid()) {
            return false;
        }
        long start = getTimestampToday(interval.getStart(), 0);
        long end = interval.doesOverlapDays() ? getTimestampTomorrow(interval.getEnd(), 0) : getTimestampToday(interval.getEnd(), 0);
        long duration = TimeUnit.MINUTES.convert(end - start, TimeUnit.MILLISECONDS);
        return duration >= minutes;
    }

    public static String formatSuspensionIntervalText(Interval interval, Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date start = new Date(getRelativeTimestamp(interval.getStart()));
        Date end = new Date(getRelativeTimestamp(interval.getEnd()));
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
        changedDate.setTime(now);
        changedDate.set(Calendar.HOUR_OF_DAY, time.getHour());
        changedDate.set(Calendar.MINUTE, time.getMinute());
        changedDate.add(Calendar.MINUTE, minutes);
        changedTime.setHour(changedDate.get(Calendar.HOUR_OF_DAY));
        changedTime.setMinute(changedDate.get(Calendar.MINUTE));
        return changedTime;
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
