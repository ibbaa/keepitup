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
public class TimeTest {

    @Test
    public void testToBundleDefaultValues() {
        Time time = new Time();
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        PersistableBundle persistableBundle = time.toPersistableBundle();
        assertNotNull(persistableBundle);
        time = new Time(persistableBundle);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        Bundle bundle = time.toBundle();
        assertNotNull(bundle);
        time = new Time(bundle);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        Map<String, ?> map = time.toMap();
        assertNotNull(map);
        time = new Time(map);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }

    @Test
    public void testEmptyMap() {
        Map<String, ?> map = new HashMap<>();
        Time time = new Time(map);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }

    @Test
    public void testInvalidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hour", "hour");
        map.put("minute", "minute");
        Time time = new Time(map);
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
    }

    @Test
    public void testMapStringValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("hour", "1");
        map.put("minute", "2");
        Time time = new Time(map);
        assertEquals(1, time.getHour());
        assertEquals(2, time.getMinute());
    }

    @Test
    public void testToBundleValues() {
        Time time = new Time();
        time.setHour(1);
        time.setMinute(2);
        PersistableBundle persistableBundle = time.toPersistableBundle();
        assertNotNull(persistableBundle);
        time = new Time(persistableBundle);
        assertEquals(1, time.getHour());
        assertEquals(2, time.getMinute());
        Bundle bundle = time.toBundle();
        assertNotNull(bundle);
        time = new Time(bundle);
        assertEquals(1, time.getHour());
        assertEquals(2, time.getMinute());
    }

    @Test
    public void testToMap() {
        Time time = new Time();
        time.setHour(1);
        time.setMinute(2);
        Map<String, ?> map = time.toMap();
        assertNotNull(map);
        time = new Time(map);
        assertEquals(1, time.getHour());
        assertEquals(2, time.getMinute());
    }

    @Test
    public void testIsEqual() {
        Time time1 = new Time();
        Time time2 = new Time();
        assertTrue(time1.isEqual(time2));
        time1.setHour(1);
        assertFalse(time1.isEqual(time2));
        time2.setHour(1);
        assertTrue(time1.isEqual(time2));
        time1.setMinute(22);
        assertFalse(time1.isEqual(time2));
        time2.setMinute(22);
        assertTrue(time1.isEqual(time2));
    }

    @Test
    public void testIsValid() {
        Time time = new Time();
        assertTrue(time.isValid());
        time.setHour(1);
        assertTrue(time.isValid());
        time.setMinute(1);
        assertTrue(time.isValid());
        time.setHour(24);
        assertFalse(time.isValid());
        time.setHour(23);
        time.setMinute(60);
        assertFalse(time.isValid());
        time.setMinute(59);
        assertTrue(time.isValid());
        time.setHour(-1);
        assertFalse(time.isValid());
        time.setHour(0);
        time.setMinute(-1);
        assertFalse(time.isValid());
    }

    @Test
    public void testIsBefore() {
        Time time1 = new Time();
        Time time2 = new Time();
        assertFalse(time1.isBefore(time2));
        time2.setMinute(1);
        assertTrue(time1.isBefore(time2));
        assertFalse(time2.isBefore(time1));
        time2.setMinute(0);
        time2.setHour(1);
        assertTrue(time1.isBefore(time2));
        assertFalse(time2.isBefore(time1));
        time1.setHour(3);
        time1.setMinute(59);
        time2.setHour(4);
        time2.setMinute(1);
        assertTrue(time1.isBefore(time2));
        assertFalse(time2.isBefore(time1));
    }

    @Test
    public void testIsAfter() {
        Time time1 = new Time();
        Time time2 = new Time();
        assertFalse(time1.isAfter(time2));
        time1.setMinute(1);
        assertTrue(time1.isAfter(time2));
        assertFalse(time2.isAfter(time1));
        time1.setMinute(0);
        time1.setHour(1);
        assertTrue(time1.isAfter(time2));
        assertFalse(time2.isAfter(time1));
        time1.setHour(4);
        time1.setMinute(1);
        time2.setHour(3);
        time2.setMinute(59);
        assertTrue(time1.isAfter(time2));
        assertFalse(time2.isAfter(time1));
    }
}
