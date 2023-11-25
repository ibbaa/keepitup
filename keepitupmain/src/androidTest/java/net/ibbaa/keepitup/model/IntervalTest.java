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

package net.ibbaa.keepitup.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class IntervalTest {

    @Test
    public void testToBundleDefaultValues() {
        Interval interval = new Interval();
        assertEquals(-1, interval.getId());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        PersistableBundle persistableBundle = interval.toPersistableBundle();
        assertNotNull(persistableBundle);
        interval = new Interval(persistableBundle);
        assertEquals(-1, interval.getId());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        Bundle bundle = interval.toBundle();
        assertNotNull(bundle);
        interval = new Interval(bundle);
        assertEquals(-1, interval.getId());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        Map<String, ?> map = interval.toMap();
        assertNotNull(map);
        interval = new Interval(map);
        assertEquals(-1, interval.getId());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testEmptyMap() {
        Map<String, ?> map = new HashMap<>();
        Interval interval = new Interval(map);
        assertEquals(-1, interval.getId());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "id");
        map.put("hourstart", "hour");
        map.put("minutestart", "minute");
        map.put("hourend", "hour");
        map.put("minuteend", "minute");
        Interval interval = new Interval(map);
        assertEquals(-1, interval.getId());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");
        map.put("hourstart", "1");
        map.put("minutestart", "2");
        Interval interval = new Interval(map);
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        assertEquals(1, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        map.put("id", "2");
        map.put("hourend", "3");
        map.put("minuteend", "4");
        interval = new Interval(map);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        assertEquals(2, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(end.isEqual(interval.getEnd()));
    }

    @Test
    public void testToBundleValues() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval = new Interval();
        interval.setId(5);
        interval.setStart(start);
        PersistableBundle persistableBundle = interval.toPersistableBundle();
        assertNotNull(persistableBundle);
        interval = new Interval(persistableBundle);
        assertEquals(5, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        interval = new Interval();
        interval.setId(5);
        interval.setStart(start);
        interval.setEnd(end);
        persistableBundle = interval.toPersistableBundle();
        assertNotNull(persistableBundle);
        interval = new Interval(persistableBundle);
        assertEquals(5, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(end.isEqual(interval.getEnd()));
        Bundle bundle = interval.toBundle();
        assertNotNull(bundle);
        interval = new Interval(bundle);
        assertEquals(5, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(end.isEqual(interval.getEnd()));
    }

    @Test
    public void testToMap() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval = new Interval();
        interval.setId(2);
        interval.setStart(start);
        Map<String, ?> map = interval.toMap();
        assertNotNull(map);
        interval = new Interval(map);
        assertEquals(2, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        interval = new Interval();
        interval.setId(1);
        interval.setStart(start);
        interval.setEnd(end);
        map = interval.toMap();
        interval = new Interval(map);
        assertEquals(1, interval.getId());
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(end.isEqual(interval.getEnd()));
    }

    @Test
    public void testSetNullValues() {
        Interval interval = new Interval();
        Time time = new Time();
        time.setHour(1);
        time.setMinute(2);
        interval.setStart(null);
        interval.setEnd(null);
        assertNotNull(interval.getStart());
        assertNotNull(interval.getEnd());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        interval.setStart(time);
        interval.setEnd(time);
        assertTrue(time.isEqual(interval.getStart()));
        assertTrue(time.isEqual(interval.getEnd()));
        interval.setStart(null);
        interval.setEnd(null);
        assertNotNull(interval.getStart());
        assertNotNull(interval.getEnd());
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testIsEqual() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertTrue(interval1.isEqual(interval2));
        interval1.setStart(start);
        assertFalse(interval1.isEqual(interval2));
        interval2.setStart(start);
        assertTrue(interval1.isEqual(interval2));
        interval1.setEnd(end);
        assertFalse(interval1.isEqual(interval2));
        interval2.setEnd(end);
        interval2.setId(3);
        assertTrue(interval1.isEqual(interval2));
    }

    @Test
    public void testIsValid() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval = new Interval();
        assertFalse(interval.isValid());
        interval.setStart(start);
        assertTrue(interval.isValid());
        interval.setEnd(end);
        assertTrue(interval.isValid());
        interval.setStart(end);
        interval.setEnd(start);
        assertTrue(interval.isValid());
        start = new Time();
        start.setHour(24);
        start.setMinute(2);
        end = new Time();
        end.setHour(3);
        end.setMinute(4);
        interval.setStart(end);
        interval.setEnd(start);
        assertFalse(interval.isValid());
        start = new Time();
        start.setHour(1);
        start.setMinute(2);
        end = new Time();
        end.setHour(3);
        end.setMinute(444);
        interval.setStart(end);
        interval.setEnd(start);
        assertFalse(interval.isValid());
        start = new Time();
        start.setHour(1);
        start.setMinute(-1);
        end = new Time();
        end.setHour(3);
        end.setMinute(4);
        interval.setStart(end);
        interval.setEnd(start);
        assertFalse(interval.isValid());
    }

    @Test
    public void doesOverlapDays() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertFalse(interval.doesOverlapDays());
        start = new Time();
        start.setHour(1);
        start.setMinute(2);
        end = new Time();
        end.setHour(1);
        end.setMinute(1);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.doesOverlapDays());
        start = new Time();
        start.setHour(1);
        start.setMinute(2);
        end = new Time();
        end.setHour(1);
        end.setMinute(3);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertFalse(interval.doesOverlapDays());
        start = new Time();
        start.setHour(21);
        start.setMinute(21);
        end = new Time();
        end.setHour(0);
        end.setMinute(0);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.doesOverlapDays());
        start = new Time();
        start.setHour(23);
        start.setMinute(59);
        end = new Time();
        end.setHour(12);
        end.setMinute(1);
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.doesOverlapDays());
    }

    @Test
    public void testDoesOverlapInvalid() {
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertFalse(interval1.doesOverlap(interval2));
        Time start = new Time();
        start.setHour(1);
        start.setMinute(61);
        Time end = new Time();
        end.setHour(-1);
        end.setMinute(4);
        interval1.setStart(start);
        interval1.setEnd(end);
        interval2.setStart(start);
        interval2.setEnd(end);
        assertFalse(interval1.doesOverlap(interval2));
        start.setMinute(59);
        interval1.setStart(start);
        interval2.setStart(start);
        assertFalse(interval1.doesOverlap(interval2));
    }

    @Test
    public void testDoesNotOverlap() {
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        Time start2 = new Time();
        start2.setHour(5);
        start2.setMinute(6);
        Time end2 = new Time();
        end2.setHour(7);
        end2.setMinute(8);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertFalse(interval1.doesOverlap(interval2));
        assertFalse(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlap() {
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        Time start2 = new Time();
        start2.setHour(2);
        start2.setMinute(4);
        Time end2 = new Time();
        end2.setHour(7);
        end2.setMinute(8);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapContains() {
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(11);
        end1.setMinute(12);
        Time start2 = new Time();
        start2.setHour(3);
        start2.setMinute(4);
        Time end2 = new Time();
        end2.setHour(10);
        end2.setMinute(1);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesNotOverlapDaysOverlap() {
        Time start1 = new Time();
        start1.setHour(2);
        start1.setMinute(6);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        Time start2 = new Time();
        start2.setHour(23);
        start2.setMinute(59);
        Time end2 = new Time();
        end2.setHour(2);
        end2.setMinute(5);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertFalse(interval1.doesOverlap(interval2));
        assertFalse(interval2.doesOverlap(interval1));
        start1 = new Time();
        start1.setHour(11);
        start1.setMinute(11);
        end1 = new Time();
        end1.setHour(13);
        end1.setMinute(16);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        assertFalse(interval1.doesOverlap(interval2));
        assertFalse(interval2.doesOverlap(interval1));
        start1 = new Time();
        start1.setHour(0);
        start1.setMinute(1);
        end1 = new Time();
        end1.setHour(23);
        end1.setMinute(58);
        start2 = new Time();
        start2.setHour(23);
        start2.setMinute(59);
        end2 = new Time();
        end2.setHour(0);
        end2.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertFalse(interval1.doesOverlap(interval2));
        assertFalse(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapDaysOverlap() {
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        Time start2 = new Time();
        start2.setHour(23);
        start2.setMinute(59);
        Time end2 = new Time();
        end2.setHour(2);
        end2.setMinute(5);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
        start1 = new Time();
        start1.setHour(2);
        start1.setMinute(4);
        end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
        start1 = new Time();
        start1.setHour(0);
        start1.setMinute(1);
        end1 = new Time();
        end1.setHour(0);
        end1.setMinute(0);
        start2 = new Time();
        start2.setHour(0);
        start2.setMinute(0);
        end2 = new Time();
        end2.setHour(1);
        end2.setMinute(1);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapDaysOverlapBoth() {
        Time start1 = new Time();
        start1.setHour(22);
        start1.setMinute(50);
        Time end1 = new Time();
        end1.setHour(1);
        end1.setMinute(1);
        Time start2 = new Time();
        start2.setHour(23);
        start2.setMinute(59);
        Time end2 = new Time();
        end2.setHour(2);
        end2.setMinute(5);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapDaysOverlapBothContains() {
        Time start1 = new Time();
        start1.setHour(22);
        start1.setMinute(50);
        Time end1 = new Time();
        end1.setHour(11);
        end1.setMinute(12);
        Time start2 = new Time();
        start2.setHour(23);
        start2.setMinute(59);
        Time end2 = new Time();
        end2.setHour(2);
        end2.setMinute(5);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
        start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        end1 = new Time();
        end1.setHour(1);
        end1.setMinute(1);
        start2 = new Time();
        start2.setHour(0);
        start2.setMinute(0);
        end2 = new Time();
        end2.setHour(0);
        end2.setMinute(1);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapStartEndEquals() {
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(3);
        end1.setMinute(4);
        Time start2 = new Time();
        start2.setHour(1);
        start2.setMinute(2);
        Time end2 = new Time();
        end2.setHour(3);
        end2.setMinute(4);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
        start2 = new Time();
        start2.setHour(3);
        start2.setMinute(4);
        end2 = new Time();
        end2.setHour(5);
        end2.setMinute(6);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapStartEndEqualsDaysOverlap() {
        Time start1 = new Time();
        start1.setHour(11);
        start1.setMinute(25);
        Time end1 = new Time();
        end1.setHour(1);
        end1.setMinute(1);
        Time start2 = new Time();
        start2.setHour(1);
        start2.setMinute(1);
        Time end2 = new Time();
        end2.setHour(10);
        end2.setMinute(10);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testDoesOverlapStartEndEqualsDaysOverlapBoth() {
        Time start1 = new Time();
        start1.setHour(11);
        start1.setMinute(25);
        Time end1 = new Time();
        end1.setHour(1);
        end1.setMinute(1);
        Time start2 = new Time();
        start2.setHour(11);
        start2.setMinute(25);
        Time end2 = new Time();
        end2.setHour(1);
        end2.setMinute(1);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testIsNotInInterval() {
        Time start = new Time();
        start.setHour(11);
        start.setMinute(25);
        Time end = new Time();
        end.setHour(12);
        end.setMinute(30);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        Time time = new Time();
        time.setHour(11);
        time.setMinute(24);
        assertFalse(interval.isInInterval(time));
        time = new Time();
        time.setHour(2);
        time.setMinute(2);
        assertFalse(interval.isInInterval(time));
    }

    @Test
    public void testIsInInterval() {
        Time start = new Time();
        start.setHour(11);
        start.setMinute(25);
        Time end = new Time();
        end.setHour(12);
        end.setMinute(30);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        Time time = new Time();
        time.setHour(11);
        time.setMinute(25);
        assertTrue(interval.isInInterval(time));
        time = new Time();
        time.setHour(12);
        time.setMinute(30);
        assertTrue(interval.isInInterval(time));
        time = new Time();
        time.setHour(12);
        time.setMinute(0);
        assertTrue(interval.isInInterval(time));
    }

    @Test
    public void testIsNotInIntervalOverlapDays() {
        Time start = new Time();
        start.setHour(22);
        start.setMinute(1);
        Time end = new Time();
        end.setHour(1);
        end.setMinute(30);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        Time time = new Time();
        time.setHour(1);
        time.setMinute(31);
        assertFalse(interval.isInInterval(time));
        time = new Time();
        time.setHour(22);
        time.setMinute(0);
        assertFalse(interval.isInInterval(time));
        time = new Time();
        time.setHour(12);
        time.setMinute(30);
        assertFalse(interval.isInInterval(time));
    }

    @Test
    public void testIsInIntervalOverlapDays() {
        Time start = new Time();
        start.setHour(22);
        start.setMinute(1);
        Time end = new Time();
        end.setHour(1);
        end.setMinute(30);
        Interval interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        Time time = new Time();
        time.setHour(1);
        time.setMinute(30);
        assertTrue(interval.isInInterval(time));
        time = new Time();
        time.setHour(22);
        time.setMinute(1);
        assertTrue(interval.isInInterval(time));
        time = new Time();
        time.setHour(0);
        time.setMinute(0);
        assertTrue(interval.isInInterval(time));
        time = new Time();
        time.setHour(23);
        time.setMinute(59);
        assertTrue(interval.isInInterval(time));
        time = new Time();
        time.setHour(0);
        time.setMinute(1);
        assertTrue(interval.isInInterval(time));
    }
}
