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
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        PersistableBundle persistableBundle = interval.toPersistableBundle();
        assertNotNull(persistableBundle);
        interval = new Interval(persistableBundle);
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        Bundle bundle = interval.toBundle();
        assertNotNull(bundle);
        interval = new Interval(bundle);
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        Map<String, ?> map = interval.toMap();
        assertNotNull(map);
        interval = new Interval(map);
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testEmptyMap() {
        Map<String, ?> map = new HashMap<>();
        Interval interval = new Interval(map);
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hourstart", "hour");
        map.put("minutestart", "minute");
        map.put("hourend", "hour");
        map.put("minuteend", "minute");
        Interval interval = new Interval(map);
        assertTrue(new Time().isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("hourstart", "1");
        map.put("minutestart", "2");
        Interval interval = new Interval(map);
        Time start = new Time();
        start.setHour(1);
        start.setMinute(2);
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        map.put("hourend", "3");
        map.put("minuteend", "4");
        interval = new Interval(map);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(4);
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
        interval.setStart(start);
        PersistableBundle persistableBundle = interval.toPersistableBundle();
        assertNotNull(persistableBundle);
        interval = new Interval(persistableBundle);
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(new Time().isEqual(interval.getEnd()));
        interval = new Interval();
        interval.setStart(start);
        interval.setEnd(end);
        persistableBundle = interval.toPersistableBundle();
        assertNotNull(persistableBundle);
        interval = new Interval(persistableBundle);
        assertTrue(start.isEqual(interval.getStart()));
        assertTrue(end.isEqual(interval.getEnd()));
        Bundle bundle = interval.toBundle();
        assertNotNull(bundle);
        interval = new Interval(bundle);
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
}
