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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class TimeUtilTest {

    @Test
    public void testGetTimestampToday() {
        Time time = new Time();
        time.setHour(17);
        time.setMinute(58);
        long timestamp = TimeUtil.getTimestampToday(time, this::testNow);
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(timestamp));
        assertEquals(1991, date.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, date.get(Calendar.MONTH));
        assertEquals(27, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, date.get(Calendar.MINUTE));
    }

    @Test
    public void testGetTimestampTomorrow() {
        Time time = new Time();
        time.setHour(17);
        time.setMinute(58);
        long timestamp = TimeUtil.getTimestampTomorrow(time, this::testNow);
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(timestamp));
        assertEquals(1991, date.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, date.get(Calendar.MONTH));
        assertEquals(28, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, date.get(Calendar.MINUTE));
        timestamp = TimeUtil.getTimestampTomorrow(time, this::testNow2);
        date = new GregorianCalendar();
        date.setTime(new Date(timestamp));
        assertEquals(1991, date.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, date.get(Calendar.MONTH));
        assertEquals(1, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, date.get(Calendar.MINUTE));
    }

    @Test
    public void testCleanAndSortEmpty() {
        List<Interval> list = Collections.emptyList();
        assertTrue(TimeUtil.cleanAndSort(list).isEmpty());
    }

    @Test
    public void testCleanAndSortInvalid() {
        List<Interval> list = new ArrayList<>();
        Interval interval = new Interval();
        list.add(interval);
        assertTrue(TimeUtil.cleanAndSort(list).isEmpty());
        interval = new Interval();
        interval.setStart(new Time());
        interval.setEnd(new Time());
        list.add(interval);
        assertTrue(TimeUtil.cleanAndSort(list).isEmpty());
        Time time = new Time();
        time.setHour(1);
        time.setMinute(2);
        interval = new Interval();
        interval.setStart(time);
        interval.setEnd(new Time());
        list.add(interval);
        assertTrue(TimeUtil.cleanAndSort(list).isEmpty());
    }

    @Test
    public void testCleanAndSortOneEntry() {
        List<Interval> list = new ArrayList<>();
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        interval.setStart(start);
        interval.setEnd(end);
        list.add(interval);
        List<Interval> result = TimeUtil.cleanAndSort(list);
        assertEquals(1, result.size());
        Interval intervalResult = result.get(0);
        assertTrue(intervalResult.isEqual(interval));
    }

    @Test
    public void testCleanAndSortTwoEntries() {
        List<Interval> list = new ArrayList<>();
        Interval interval1 = new Interval();
        Time start1 = new Time();
        start1.setHour(5);
        start1.setMinute(6);
        Time end1 = new Time();
        end1.setHour(5);
        end1.setMinute(7);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval2 = new Interval();
        Time start2 = new Time();
        start2.setHour(1);
        start2.setMinute(2);
        Time end2 = new Time();
        end2.setHour(3);
        end2.setMinute(4);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        list.add(interval1);
        list.add(interval2);
        List<Interval> result = TimeUtil.cleanAndSort(list);
        assertEquals(2, result.size());
        Interval intervalResult1 = result.get(0);
        Interval intervalResult2 = result.get(1);
        assertTrue(intervalResult1.isEqual(interval2));
        assertTrue(intervalResult2.isEqual(interval1));
    }

    @Test
    public void testCleanAndSortThreeEntries() {
        List<Interval> list = new ArrayList<>();
        Interval interval1 = new Interval();
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval2 = new Interval();
        Time start2 = new Time();
        start2.setHour(5);
        start2.setMinute(5);
        Time end2 = new Time();
        end2.setHour(5);
        end2.setMinute(10);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Interval interval3 = new Interval();
        Time start3 = new Time();
        start3.setHour(0);
        start3.setMinute(1);
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(2);
        interval3.setStart(start3);
        interval3.setEnd(end3);
        list.add(interval1);
        list.add(interval2);
        list.add(interval3);
        List<Interval> result = TimeUtil.cleanAndSort(list);
        assertEquals(3, result.size());
        Interval intervalResult1 = result.get(0);
        Interval intervalResult2 = result.get(1);
        Interval intervalResult3 = result.get(2);
        assertTrue(intervalResult1.isEqual(interval3));
        assertTrue(intervalResult2.isEqual(interval1));
        assertTrue(intervalResult3.isEqual(interval2));
    }

    @Test
    public void testCleanAndSortThreeEntriesWithInvalid() {
        List<Interval> list = new ArrayList<>();
        Interval interval1 = new Interval();
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval2 = new Interval();
        Time start2 = new Time();
        start2.setHour(5);
        start2.setMinute(5);
        Time end2 = new Time();
        end2.setHour(5);
        end2.setMinute(10);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Interval interval3 = new Interval();
        Time start3 = new Time();
        start3.setHour(0);
        start3.setMinute(1);
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(2);
        interval3.setStart(start3);
        interval3.setEnd(end3);
        list.add(new Interval());
        list.add(new Interval());
        list.add(interval1);
        list.add(interval2);
        list.add(new Interval());
        list.add(interval3);
        List<Interval> result = TimeUtil.cleanAndSort(list);
        assertEquals(3, result.size());
        Interval intervalResult1 = result.get(0);
        Interval intervalResult2 = result.get(1);
        Interval intervalResult3 = result.get(2);
        assertTrue(intervalResult1.isEqual(interval3));
        assertTrue(intervalResult2.isEqual(interval1));
        assertTrue(intervalResult3.isEqual(interval2));
    }

    private long testNow() {
        Calendar calendar = new GregorianCalendar(1991, Calendar.JULY, 27, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    private long testNow2() {
        Calendar calendar = new GregorianCalendar(1991, Calendar.JULY, 31, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
}
