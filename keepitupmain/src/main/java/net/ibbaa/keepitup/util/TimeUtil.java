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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class TimeUtil {

    public static long getTimestampToday(Time time, ITimeService timeService) {
        return getCalendarFromTime(time, timeService).getTimeInMillis();
    }

    public static long getTimestampTomorrow(Time time, ITimeService timeService) {
        Calendar date = getCalendarFromTime(time, timeService);
        date.add(Calendar.DATE, 1);
        return date.getTimeInMillis();
    }

    private static Calendar getCalendarFromTime(Time time, ITimeService timeService) {
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(timeService.getCurrentTimestamp()));
        date.set(Calendar.HOUR_OF_DAY, time.getHour());
        date.set(Calendar.MINUTE, time.getMinute());
        return date;
    }

    public static List<Interval> merge(List<Interval> intervals) {
        List<Interval> mergedList = new ArrayList<>();
        List<Interval> originalList = new ArrayList<>();
        for(Interval currentInterval : intervals) {
            if(currentInterval.isValid()) {
                originalList.add(currentInterval);
            }
        }
        Collections.sort(originalList, TimeUtil::compareIntervals);
        Iterator<Interval> intervalIterator = originalList.iterator();
        if(intervalIterator.hasNext()) {
            Interval interval1 = intervalIterator.next();
            while(intervalIterator.hasNext()) {
                Interval interval2 = intervalIterator.next();
                if(interval1.doesOverlap(interval2)) {
                    interval1 = interval1.merge(interval2);
                } else {
                    mergedList.add(interval1);
                    interval1 = interval2;
                }
            }
            mergedList.add(interval1);
        }
        return mergedList;
    }

    private static int compareIntervals(Interval interval1, Interval interval2) {
        if(interval1.isBefore(interval2)) {
            return -1;
        } else if (interval1.isAfter(interval2)) {
            return 1;
        }
        return 0;
    }
}
