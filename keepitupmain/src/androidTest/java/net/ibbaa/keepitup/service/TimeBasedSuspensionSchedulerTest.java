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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.SchedulerStateDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerState;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockAlarmManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.util.TimeUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class TimeBasedSuspensionSchedulerTest {

    private TimeBasedSuspensionScheduler scheduler;
    private PreferenceManager preferenceManager;
    private NetworkTaskDAO networkTaskDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private MockAlarmManager alarmManager;
    private MockTimeService timeService;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new TimeBasedSuspensionScheduler(TestRegistry.getContext());
        scheduler.reset();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        alarmManager = (MockAlarmManager) scheduler.getAlarmManager();
        alarmManager.reset();
        timeService = (MockTimeService) scheduler.getTimeService();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
        networkTaskDAO.deleteAllNetworkTasks();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        scheduler.reset();
    }

    @Test
    public void testReset() {
        intervalDAO.insertInterval(getInterval1());
        assertEquals(1, scheduler.getIntervals().size());
        intervalDAO.deleteAllIntervals();
        assertEquals(1, scheduler.getIntervals().size());
        scheduler.reset();
        assertEquals(0, scheduler.getIntervals().size());
    }

    @Test
    public void testFindCurrentSuspendIntervalNoOverlapOneInterval() {
        intervalDAO.insertInterval(getInterval1());
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 1, 5)));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 10, 11))));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 11, 0))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 11, 12)));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 1, 5)));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 10, 11))));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 11, 0))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 11, 12)));
    }

    @Test
    public void testFindCurrentSuspendIntervalNoOverlapThreeIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 0, 0)));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 0, 30)));
        assertTrue(getInterval2().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 1, 30))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 2, 3)));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 10, 12))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 11, 12)));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 15, 53)));
        assertTrue(getInterval3().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 22, 30))));
        assertTrue(getInterval3().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 23, 58))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 0, 0)));
    }

    @Test
    public void testFindCurrentSuspendIntervalOverlapTwoIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval4());
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 2, 3)));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 10, 12))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 21, 0)));
        assertTrue(getInterval4().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 21, 12))));
        assertTrue(getInterval4().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 0, 0))));
        assertTrue(getInterval4().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 1, 29))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 1, 31)));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 2, 3)));
        assertTrue(getInterval1().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 10, 12))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 21, 0)));
        assertTrue(getInterval4().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 21, 12))));
        assertTrue(getInterval4().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 0, 0))));
        assertTrue(getInterval4().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 1, 29))));
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(25, 1, 31)));
    }

    @Test
    public void testFindCurrentSuspendIntervalOverlapWholeDay() {
        intervalDAO.insertInterval(getInterval5());
        assertNull(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 0, 0)));
        assertTrue(getInterval5().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 0, 1))));
        assertTrue(getInterval5().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 15, 15))));
        assertTrue(getInterval5().isEqual(scheduler.findCurrentSuspendInterval(getTestTimestamp(24, 1, 1))));
    }

    @Test
    public void testFindNextSuspendInterval() {
        assertNull(scheduler.findNextSuspendInterval(getTestTimestamp(24, 0, 0)));
        assertNull(scheduler.findNextSuspendInterval(getTestTimestamp(24, 10, 11)));
        assertNull(scheduler.findNextSuspendInterval(getTestTimestamp(24, 11, 0)));
        scheduler.reset();
        intervalDAO.deleteAllIntervals();
        intervalDAO.insertInterval(getInterval1());
        assertTrue(getInterval1().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 10, 1))));
        assertTrue(getInterval1().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 10, 15))));
        assertTrue(getInterval1().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 23, 0))));
        scheduler.reset();
        intervalDAO.deleteAllIntervals();
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval4());
        assertTrue(getInterval1().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 10, 1))));
        assertTrue(getInterval4().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 10, 15))));
        assertTrue(getInterval1().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 23, 59))));
        scheduler.reset();
        intervalDAO.deleteAllIntervals();
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        assertTrue(getInterval2().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 0, 30))));
        assertTrue(getInterval1().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 3, 30))));
        assertTrue(getInterval3().isEqual(scheduler.findNextSuspendInterval(getTestTimestamp(24, 22, 0))));
    }

    @Test
    public void testScheduleStart() {
        scheduler.scheduleStart(getInterval1(), getTestTimestamp(24, 10, 1), true);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval1().getStart(), getTestTimestamp(24, 10, 1)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        alarmManager.reset();
        scheduler.scheduleStart(getInterval1(), getTestTimestamp(24, 10, 15), false);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampTomorrow(getInterval1().getStart(), getTestTimestamp(24, 10, 15)), setAlarm.getDelay());
        assertFalse(scheduler.getWasRestartedFlag());
    }

    @Test
    public void testIsRunning() {
        assertFalse(scheduler.isRunning());
        scheduler.scheduleStart(getInterval5(), getTestTimestamp(24, 0, 0), true);
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testScheduleStartOverlapDays() {
        scheduler.scheduleStart(getInterval5(), getTestTimestamp(24, 0, 0), true);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval5().getStart(), getTestTimestamp(24, 0, 0)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
    }

    @Test
    public void testScheduleSuspend() {
        scheduler.scheduleSuspend(getInterval1(), getTestTimestamp(24, 10, 15), true);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval1().getEnd(), getTestTimestamp(24, 10, 15)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        alarmManager.reset();
        scheduler.scheduleSuspend(getInterval1(), getTestTimestamp(24, 11, 15), false);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampTomorrow(getInterval1().getEnd(), getTestTimestamp(24, 11, 15)), setAlarm.getDelay());
        assertFalse(scheduler.getWasRestartedFlag());
    }

    @Test
    public void testScheduleSuspendOverlapDays() {
        scheduler.scheduleSuspend(getInterval5(), getTestTimestamp(24, 10, 15), false);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampTomorrow(getInterval5().getEnd(), getTestTimestamp(24, 0, 0)), setAlarm.getDelay());
        assertFalse(scheduler.getWasRestartedFlag());
    }

    @Test
    public void testStartSingle() {
        MockAlarmManager networkTaskSchedulerAlarmManager = (MockAlarmManager) scheduler.getNetworkTaskScheduler().getAlarmManager();
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, true, 0));
        NetworkTask task = getNetworkTask1();
        task = networkTaskDAO.insertNetworkTask(task);
        scheduler.startSingle(task, getTestTimestamp(24, 3, 30));
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
    }

    @Test
    public void testStartup() {
        MockAlarmManager networkTaskSchedulerAlarmManager = (MockAlarmManager) scheduler.getNetworkTaskScheduler().getAlarmManager();
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, true, 0));
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        networkTaskDAO.insertNetworkTask(task1);
        networkTaskDAO.insertNetworkTask(task2);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.startup(12345);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertEquals(12345, schedulerStateDAO.readSchedulerState().getTimestamp());
    }

    @Test
    public void testSuspend() {
        MockAlarmManager networkTaskSchedulerAlarmManager = (MockAlarmManager) scheduler.getNetworkTaskScheduler().getAlarmManager();
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.getNetworkTaskScheduler().start(task1);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), 125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task2.getId(), 125);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.suspend(12345);
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertLastScheduledInDatabase(task1, 125);
        assertLastScheduledInDatabase(task1, 125);
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(1, cancelAlarmCalls.size());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertEquals(12345, schedulerStateDAO.readSchedulerState().getTimestamp());
    }

    private boolean isTaskMarkedAsRunningInDatabase(NetworkTask task) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        return task.isRunning();
    }

    private void assertLastScheduledInDatabase(NetworkTask task, long value) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(value, task.getLastScheduled());
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
        start.setMinute(1);
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

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(10);
        task.setSchedulerId(2);
        task.setInstances(0);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(1);
        return task;
    }
}
