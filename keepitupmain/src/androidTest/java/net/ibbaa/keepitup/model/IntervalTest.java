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
import static org.junit.Assert.assertNull;
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
        interval.setStart(start);
        Map<String, ?> map = interval.toMap();
        assertNotNull(map);
        interval = new Interval(map);
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        map = interval.toMap();
        interval = new Interval(map);
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
        assertFalse(interval.isValid());
        interval.setEnd(end);
        assertTrue(interval.isValid());
        interval.setStart(end);
        interval.setEnd(start);
        assertFalse(interval.isValid());
    }

    @Test
    public void testIsBefore() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertFalse(interval1.isBefore(interval2));
        interval1.setStart(start);
        interval1.setEnd(end);
        assertFalse(interval1.isBefore(interval2));
        interval2.setStart(start);
        interval2.setEnd(end);
        assertFalse(interval1.isBefore(interval2));
        start = new Time();
        start.setHour(5);
        start.setMinute(6);
        end = new Time();
        end.setHour(7);
        end.setMinute(8);
        interval2.setStart(start);
        interval2.setEnd(end);
        assertTrue(interval1.isBefore(interval2));
        assertFalse(interval2.isBefore(interval1));
    }

    @Test
    public void testIsAfter() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertFalse(interval1.isAfter(interval2));
        interval1.setStart(start);
        interval1.setEnd(end);
        assertFalse(interval1.isAfter(interval2));
        interval2.setStart(start);
        interval2.setEnd(end);
        assertFalse(interval1.isAfter(interval2));
        start = new Time();
        start.setHour(5);
        start.setMinute(6);
        end = new Time();
        end.setHour(7);
        end.setMinute(8);
        interval1.setStart(start);
        interval1.setEnd(end);
        assertTrue(interval1.isAfter(interval2));
        assertFalse(interval2.isAfter(interval1));
    }

    @Test
    public void testStartsBefore() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertFalse(interval1.startsBefore(interval2));
        interval1.setStart(start);
        interval1.setEnd(end);
        assertFalse(interval1.startsBefore(interval2));
        interval2.setStart(start);
        interval2.setEnd(end);
        assertFalse(interval1.startsBefore(interval2));
        start = new Time();
        start.setHour(1);
        start.setMinute(1);
        interval1.setStart(start);
        assertTrue(interval1.startsBefore(interval2));
        assertFalse(interval2.startsBefore(interval1));
    }

    @Test
    public void testEndsAfter() {
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertFalse(interval1.endsAfter(interval2));
        interval1.setStart(start);
        interval1.setEnd(end);
        assertFalse(interval1.endsAfter(interval2));
        interval2.setStart(start);
        interval2.setEnd(end);
        assertFalse(interval1.endsAfter(interval2));
        end = new Time();
        end.setHour(4);
        end.setMinute(4);
        interval1.setEnd(end);
        assertTrue(interval1.endsAfter(interval2));
        assertFalse(interval2.endsAfter(interval1));
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
    public void testDoesOverlap() {
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(2);
        Time end1 = new Time();
        end1.setHour(10);
        end1.setMinute(20);
        Time start2 = new Time();
        start2.setHour(3);
        start2.setMinute(3);
        Time end2 = new Time();
        end2.setHour(10);
        end2.setMinute(19);
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
        end2 = new Time();
        end2.setHour(11);
        end2.setMinute(18);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        assertTrue(interval1.doesOverlap(interval2));
        assertTrue(interval2.doesOverlap(interval1));
    }

    @Test
    public void testMergeInvalid() {
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        assertNull(interval1.merge(interval2));
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
        assertNull(interval1.merge(interval2));
    }

    @Test
    public void testMergeSame() {
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        Time start = new Time();
        Time end = new Time();
        end.setMinute(1);
        interval1.setStart(start);
        interval1.setEnd(end);
        interval2.setStart(start);
        interval2.setEnd(end);
        Interval merged = interval1.merge(interval2);
        assertTrue(merged.getStart().isEqual(start));
        assertTrue(merged.getEnd().isEqual(end));
        start = new Time();
        start.setHour(1);
        start.setMinute(1);
        end = new Time();
        end.setHour(2);
        end.setMinute(2);
        interval1.setStart(start);
        interval1.setEnd(end);
        interval2.setStart(start);
        interval2.setEnd(end);
        merged = interval1.merge(interval2);
        assertTrue(merged.getStart().isEqual(start));
        assertTrue(merged.getEnd().isEqual(end));
    }

    @Test
    public void testMergeNotOverlap() {
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(1);
        Time end1 = new Time();
        end1.setHour(2);
        end1.setMinute(2);
        Time start2 = new Time();
        start2.setHour(3);
        start2.setMinute(3);
        Time end2 = new Time();
        end2.setHour(4);
        end2.setMinute(4);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Interval merged = interval1.merge(interval2);
        assertTrue(merged.getStart().isEqual(start1));
        assertTrue(merged.getEnd().isEqual(end2));
        merged = interval2.merge(interval1);
        assertTrue(merged.getStart().isEqual(start1));
        assertTrue(merged.getEnd().isEqual(end2));
    }

    @Test
    public void testMergeOverlap() {
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(1);
        Time end1 = new Time();
        end1.setHour(5);
        end1.setMinute(5);
        Time start2 = new Time();
        start2.setHour(3);
        start2.setMinute(3);
        Time end2 = new Time();
        end2.setHour(8);
        end2.setMinute(8);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Interval merged = interval1.merge(interval2);
        assertTrue(merged.getStart().isEqual(start1));
        assertTrue(merged.getEnd().isEqual(end2));
        merged = interval2.merge(interval1);
        assertTrue(merged.getStart().isEqual(start1));
        assertTrue(merged.getEnd().isEqual(end2));
    }

    @Test
    public void testMergeContains() {
        Interval interval1 = new Interval();
        Interval interval2 = new Interval();
        Time start1 = new Time();
        start1.setHour(1);
        start1.setMinute(1);
        Time end1 = new Time();
        end1.setHour(5);
        end1.setMinute(5);
        Time start2 = new Time();
        start2.setHour(3);
        start2.setMinute(3);
        Time end2 = new Time();
        end2.setHour(4);
        end2.setMinute(4);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        Interval merged = interval1.merge(interval2);
        assertTrue(merged.getStart().isEqual(start1));
        assertTrue(merged.getEnd().isEqual(end1));
        merged = interval2.merge(interval1);
        assertTrue(merged.getStart().isEqual(start1));
        assertTrue(merged.getEnd().isEqual(end1));
    }
}
