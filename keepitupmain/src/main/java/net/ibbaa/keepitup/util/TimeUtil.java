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

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.service.ITimeService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public static Interval extendInterval(Interval interval, int minutes) {
        Time start = new Time();
        Time end = new Time();
        Date now = new Date();
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(now);
        endDate.set(Calendar.HOUR_OF_DAY, interval.getEnd().getHour());
        endDate.set(Calendar.MINUTE, interval.getEnd().getMinute());
        endDate.add(Calendar.MINUTE, minutes);
        start.setHour(interval.getStart().getHour());
        start.setMinute(interval.getStart().getMinute());
        end.setHour(endDate.get(Calendar.HOUR_OF_DAY));
        end.setMinute(endDate.get(Calendar.MINUTE));
        Interval extendedInterval = new Interval();
        extendedInterval.setStart(start);
        extendedInterval.setEnd(end);
        return extendedInterval;
    }

    private static Calendar getCalendarFromTime(Time time, long currentTime) {
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(currentTime));
        date.set(Calendar.HOUR_OF_DAY, time.getHour());
        date.set(Calendar.MINUTE, time.getMinute());
        return date;
    }
}
