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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Time;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    private long testNow() {
        Calendar calendar = new GregorianCalendar(1991, Calendar.JULY, 27, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    private long testNow2() {
        Calendar calendar = new GregorianCalendar(1991, Calendar.JULY, 31, 0, 0, 0);
        return calendar.getTimeInMillis();
    }
}
