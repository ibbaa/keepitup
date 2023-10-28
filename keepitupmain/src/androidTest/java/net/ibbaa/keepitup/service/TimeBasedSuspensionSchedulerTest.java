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

package net.ibbaa.keepitup.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockAlarmManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class TimeBasedSuspensionSchedulerTest {

    private TimeBasedSuspensionScheduler scheduler;
    private PreferenceManager preferenceManager;
    private IntervalDAO intervalDAO;
    private MockAlarmManager alarmManager;
    private MockTimeService timeService;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new TimeBasedSuspensionScheduler(TestRegistry.getContext());
        scheduler.reset();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
        alarmManager = (MockAlarmManager) scheduler.getAlarmManager();
        alarmManager.reset();
        timeService = (MockTimeService) scheduler.getTimeService();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
        intervalDAO.deleteAllIntervals();
        scheduler.reset();
    }

    @Test
    public void testIsSuspendedEmptyOrDisabled() {
        assertFalse(scheduler.isSuspended());
        intervalDAO.insertInterval(getInterval1());
        preferenceManager.setPreferenceSuspensionEnabled(false);
        assertFalse(scheduler.isSuspended());
    }

    @Test
    public void testIsSuspendedNoOverlapOneInterval() {
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 1, 5));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 10, 11));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 11, 0));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 11, 12));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 1, 5));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 10, 11));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 11, 0));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 11, 12));
        assertFalse(scheduler.isSuspended());
    }

    @Test
    public void testIsSuspendedNoOverlapThreeIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        setTestTime(getTestTimestamp(24, 0, 0));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 0, 30));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 1, 30));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 2, 3));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 10, 12));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 11, 12));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 15, 53));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 22, 30));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 23, 58));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 0, 0));
        assertFalse(scheduler.isSuspended());
    }

    @Test
    public void testIsSuspendedOverlapTwoIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 2, 3));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 10, 12));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 21, 00));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 21, 12));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 0, 0));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 1, 29));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 1, 31));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 2, 3));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 10, 12));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 21, 00));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 21, 12));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 0, 0));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 1, 29));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(25, 1, 31));
        assertFalse(scheduler.isSuspended());
    }

    @Test
    public void testIsSuspendedOverlapWholeDay() {
        intervalDAO.insertInterval(getInterval5());
        setTestTime(getTestTimestamp(24, 0, 0));
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 0, 1));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 15, 15));
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 1, 1));
        assertTrue(scheduler.isSuspended());
    }

    private void setTestTime(long time) {
        timeService.setTimestamp(time);
        timeService.setTimestamp2(time);
    }

    private long getTestTimestamp(int day, int hour, int minute) {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, day, hour, minute, 1);
        return calendar.getTimeInMillis();
    }

    private Interval getInterval1() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(10);
        start.setMinute(11);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(12);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval2() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(1);
        start.setMinute(1);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(2);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval3() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(22);
        start.setMinute(15);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(23);
        end.setMinute(59);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval4() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(21);
        start.setMinute(01);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(1);
        end.setMinute(30);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval5() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(0);
        start.setMinute(1);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(0);
        interval.setEnd(end);
        return interval;
    }
}
