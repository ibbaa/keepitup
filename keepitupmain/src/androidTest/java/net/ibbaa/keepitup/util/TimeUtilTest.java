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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
        assertEquals(0, date.get(Calendar.SECOND));
        timestamp = TimeUtil.getTimestampToday(time, timestamp);
        date.setTime(new Date(timestamp));
        assertEquals(1991, date.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, date.get(Calendar.MONTH));
        assertEquals(27, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, date.get(Calendar.MINUTE));
        assertEquals(0, date.get(Calendar.SECOND));
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
        assertEquals(0, date.get(Calendar.SECOND));
        timestamp = TimeUtil.getTimestampTomorrow(time, testNow2());
        date = new GregorianCalendar();
        date.setTime(new Date(timestamp));
        assertEquals(1991, date.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, date.get(Calendar.MONTH));
        assertEquals(1, date.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, date.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, date.get(Calendar.MINUTE));
        assertEquals(0, date.get(Calendar.SECOND));
    }

    @Test
    public void testExtendInterval() {
        Time start = new Time();
        start.setHour(17);
        start.setMinute(58);
        Time end = new Time();
        end.setHour(23);
        end.setMinute(0);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        interval = TimeUtil.extendInterval(interval, 10);
        assertEquals(17, interval.getStart().getHour());
        assertEquals(58, interval.getStart().getMinute());
        assertEquals(23, interval.getEnd().getHour());
        assertEquals(10, interval.getEnd().getMinute());
        start = new Time();
        start.setHour(0);
        start.setMinute(1);
        end = new Time();
        end.setHour(23);
        end.setMinute(58);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        interval = TimeUtil.extendInterval(interval, 10);
        assertEquals(0, interval.getStart().getHour());
        assertEquals(1, interval.getStart().getMinute());
        assertEquals(0, interval.getEnd().getHour());
        assertEquals(8, interval.getEnd().getMinute());
    }

    @Test
    public void testAddMinutes() {
        Time time = new Time();
        time.setHour(17);
        time.setMinute(58);
        time = TimeUtil.addMinutes(time, 10);
        assertEquals(18, time.getHour());
        assertEquals(8, time.getMinute());
        time = new Time();
        time.setHour(22);
        time.setMinute(0);
        time = TimeUtil.addMinutes(time, 360);
        assertEquals(4, time.getHour());
        assertEquals(0, time.getMinute());
        time = new Time();
        time.setHour(0);
        time.setMinute(0);
        time = TimeUtil.addMinutes(time, 1440);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }

    @Test
    public void testSubstractMinutes() {
        Time time = new Time();
        time.setHour(17);
        time.setMinute(58);
        time = TimeUtil.substractMinutes(time, 10);
        assertEquals(17, time.getHour());
        assertEquals(48, time.getMinute());
        time = new Time();
        time.setHour(2);
        time.setMinute(0);
        time = TimeUtil.substractMinutes(time, 360);
        assertEquals(20, time.getHour());
        assertEquals(0, time.getMinute());
        time = new Time();
        time.setHour(0);
        time.setMinute(0);
        time = TimeUtil.substractMinutes(time, 1440);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }

    @Test
    public void testGetDuration() {
        assertEquals(0, TimeUtil.getDuration(new Interval()));
        Time start = new Time();
        start.setHour(17);
        start.setMinute(58);
        Time end = new Time();
        end.setHour(17);
        end.setMinute(59);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertEquals(1, TimeUtil.getDuration(interval));
        start = new Time();
        start.setHour(0);
        start.setMinute(0);
        end = new Time();
        end.setHour(23);
        end.setMinute(59);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertEquals(1439, TimeUtil.getDuration(interval));
        start = new Time();
        start.setHour(22);
        start.setMinute(0);
        end = new Time();
        end.setHour(4);
        end.setMinute(0);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertEquals(360, TimeUtil.getDuration(interval));
    }

    @Test
    public void testIsDurationMin() {
        Time start = new Time();
        start.setHour(17);
        start.setMinute(58);
        Time end = new Time();
        end.setHour(17);
        end.setMinute(59);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(TimeUtil.isDurationMin(interval, 1));
        assertFalse(TimeUtil.isDurationMin(interval, 2));
        start = new Time();
        start.setHour(1);
        start.setMinute(0);
        end = new Time();
        end.setHour(1);
        end.setMinute(15);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(TimeUtil.isDurationMin(interval, 14));
        assertTrue(TimeUtil.isDurationMin(interval, 15));
        assertFalse(TimeUtil.isDurationMin(interval, 16));
        assertFalse(TimeUtil.isDurationMin(interval, 20));
        start = new Time();
        start.setHour(0);
        start.setMinute(0);
        end = new Time();
        end.setHour(23);
        end.setMinute(59);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(TimeUtil.isDurationMin(interval, 1000));
        assertTrue(TimeUtil.isDurationMin(interval, 15));
        start = new Time();
        start.setHour(23);
        start.setMinute(0);
        end = new Time();
        end.setHour(1);
        end.setMinute(0);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(TimeUtil.isDurationMin(interval, 120));
        assertFalse(TimeUtil.isDurationMin(interval, 121));
    }

    @Test
    public void testGetMaxGapEmptyOrOne() {
        Time start = new Time();
        start.setHour(0);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(23);
        end.setMinute(59);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        Interval maxGap = TimeUtil.getMaxGap(Collections.emptyList());
        assertTrue(maxGap.isEqual(interval));
        assertEquals(1439, TimeUtil.getDuration(maxGap));
        start = new Time();
        start.setHour(22);
        start.setMinute(0);
        end = new Time();
        end.setHour(4);
        end.setMinute(0);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        maxGap = TimeUtil.getMaxGap(Collections.singletonList(interval));
        interval = new Interval();
        interval.setStart(end);
        interval.setEnd(start);
        assertTrue(maxGap.isEqual(interval));
        assertEquals(1080, TimeUtil.getDuration(maxGap));
        start = new Time();
        start.setHour(1);
        start.setMinute(0);
        end = new Time();
        end.setHour(2);
        end.setMinute(0);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        maxGap = TimeUtil.getMaxGap(Collections.singletonList(interval));
        interval = new Interval();
        interval.setStart(end);
        interval.setEnd(start);
        assertTrue(maxGap.isEqual(interval));
        assertEquals(1380, TimeUtil.getDuration(maxGap));
        start = new Time();
        start.setHour(2);
        start.setMinute(0);
        end = new Time();
        end.setHour(1);
        end.setMinute(0);
        interval.setStart(start);
        interval.setEnd(end);
        maxGap = TimeUtil.getMaxGap(Collections.singletonList(interval));
        interval = new Interval();
        interval.setStart(end);
        interval.setEnd(start);
        assertTrue(maxGap.isEqual(interval));
        assertEquals(60, TimeUtil.getDuration(maxGap));
    }

    @Test
    public void testGetMaxGapThree() {
        Time start1 = new Time();
        start1.setHour(5);
        start1.setMinute(0);
        Time end1 = new Time();
        end1.setHour(8);
        end1.setMinute(0);
        Interval interval1 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Time start2 = new Time();
        start2.setHour(11);
        start2.setMinute(15);
        Time end2 = new Time();
        end2.setHour(12);
        end2.setMinute(30);
        Interval interval2 = new Interval();
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Time start3 = new Time();
        start3.setHour(22);
        start3.setMinute(0);
        Time end3 = new Time();
        end3.setHour(4);
        end3.setMinute(0);
        Interval interval3 = new Interval();
        interval3.setStart(start3);
        interval3.setEnd(end3);
        Interval maxGap = TimeUtil.getMaxGap(Arrays.asList(interval1, interval2, interval3));
        Time start = new Time();
        start.setHour(12);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(22);
        end.setMinute(0);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(maxGap.isEqual(interval));
        assertEquals(570, TimeUtil.getDuration(maxGap));
    }

    @Test
    public void testGetMaxGapFour() {
        Time start1 = new Time();
        start1.setHour(5);
        start1.setMinute(0);
        Time end1 = new Time();
        end1.setHour(8);
        end1.setMinute(0);
        Interval interval1 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Time start2 = new Time();
        start2.setHour(11);
        start2.setMinute(15);
        Time end2 = new Time();
        end2.setHour(12);
        end2.setMinute(30);
        Interval interval2 = new Interval();
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Time start3 = new Time();
        start3.setHour(13);
        start3.setMinute(20);
        Time end3 = new Time();
        end3.setHour(20);
        end3.setMinute(30);
        Interval interval3 = new Interval();
        interval3.setStart(start3);
        interval3.setEnd(end3);
        Time start4 = new Time();
        start4.setHour(22);
        start4.setMinute(0);
        Time end4 = new Time();
        end4.setHour(4);
        end4.setMinute(0);
        Interval interval4 = new Interval();
        interval4.setStart(start4);
        interval4.setEnd(end4);
        Interval maxGap = TimeUtil.getMaxGap(Arrays.asList(interval1, interval2, interval3, interval4));
        Time start = new Time();
        start.setHour(8);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(15);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(maxGap.isEqual(interval));
        assertEquals(195, TimeUtil.getDuration(maxGap));
    }

    @Test
    public void testGetMaxGapZeroGap() {
        Time start1 = new Time();
        start1.setHour(4);
        start1.setMinute(0);
        Time end1 = new Time();
        end1.setHour(8);
        end1.setMinute(0);
        Interval interval1 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Time start2 = new Time();
        start2.setHour(8);
        start2.setMinute(0);
        Time end2 = new Time();
        end2.setHour(12);
        end2.setMinute(30);
        Interval interval2 = new Interval();
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Time start3 = new Time();
        start3.setHour(12);
        start3.setMinute(30);
        Time end3 = new Time();
        end3.setHour(22);
        end3.setMinute(0);
        Interval interval3 = new Interval();
        interval3.setStart(start3);
        interval3.setEnd(end3);
        Time start4 = new Time();
        start4.setHour(22);
        start4.setMinute(0);
        Time end4 = new Time();
        end4.setHour(4);
        end4.setMinute(0);
        Interval interval4 = new Interval();
        interval4.setStart(start4);
        interval4.setEnd(end4);
        Interval maxGap = TimeUtil.getMaxGap(Arrays.asList(interval1, interval2, interval3, interval4));
        Time start = new Time();
        start.setHour(4);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(0);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(maxGap.isEqual(interval));
        assertEquals(0, TimeUtil.getDuration(maxGap));
    }

    @Test
    public void testGetRelativeTimestamp() {
        Time time = new Time();
        time.setHour(17);
        time.setMinute(59);
        long timestamp = TimeUtil.getRelativeTimestamp(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        assertEquals("17:59", dateFormat.format(timestamp));
    }

    @Test
    public void testFormatSuspensionIntervalText() {
        Time start = new Time();
        start.setHour(17);
        start.setMinute(58);
        Time end = new Time();
        end.setHour(17);
        end.setMinute(59);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertEquals("Start: 17:58 End: 17:59", TimeUtil.formatSuspensionIntervalText(interval, TestRegistry.getContext()));
        start = new Time();
        start.setHour(1);
        start.setMinute(2);
        end = new Time();
        end.setHour(3);
        end.setMinute(4);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertEquals("Start: 01:02 End: 03:04", TimeUtil.formatSuspensionIntervalText(interval, TestRegistry.getContext()));
    }

    @Test
    public void testSortIntervalList() {
        Time start = new Time();
        start.setHour(17);
        start.setMinute(58);
        Time end = new Time();
        end.setHour(17);
        end.setMinute(59);
        Interval interval1 = new Interval();
        interval1.setStart(start);
        interval1.setEnd(end);
        start = new Time();
        start.setHour(23);
        start.setMinute(1);
        end = new Time();
        end.setHour(1);
        end.setMinute(0);
        Interval interval2 = new Interval();
        interval2.setStart(start);
        interval2.setEnd(end);
        start = new Time();
        start.setHour(2);
        start.setMinute(1);
        end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval3 = new Interval();
        interval3.setStart(start);
        interval3.setEnd(end);
        List<Interval> sortedList = TimeUtil.sortIntervalList(Arrays.asList(interval1, interval2, interval3));
        assertTrue(interval3.isEqual(sortedList.get(0)));
        assertTrue(interval1.isEqual(sortedList.get(1)));
        assertTrue(interval2.isEqual(sortedList.get(2)));
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
