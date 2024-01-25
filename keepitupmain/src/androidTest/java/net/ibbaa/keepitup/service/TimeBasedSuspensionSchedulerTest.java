/*
 * Copyright (c) 2024. Alwin Ibba
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
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestTimeBasedSuspensionScheduler;
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

    private TestTimeBasedSuspensionScheduler scheduler;
    private TestNetworkTaskProcessServiceScheduler networkTaskScheduler;
    private PreferenceManager preferenceManager;
    private NetworkTaskDAO networkTaskDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private MockAlarmManager alarmManager;
    private MockAlarmManager networkTaskSchedulerAlarmManager;
    private MockTimeService timeService;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new TestTimeBasedSuspensionScheduler(TestRegistry.getContext());
        networkTaskScheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.setNetworkTaskScheduler(networkTaskScheduler);
        networkTaskScheduler.setTimeBasedSuspensionScheduler(scheduler);
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.resetIsSuspended();
        scheduler.stop();
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
        networkTaskSchedulerAlarmManager = (MockAlarmManager) networkTaskScheduler.getAlarmManager();
        networkTaskSchedulerAlarmManager.reset();
        timeService = (MockTimeService) scheduler.getTimeService();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
        networkTaskDAO.deleteAllNetworkTasks();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.resetIsSuspended();
        scheduler.stop();
        alarmManager.reset();
        networkTaskSchedulerAlarmManager.reset();
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
    public void testResetIsSuspended() {
        schedulerStateDAO.updateSchedulerState(new SchedulerState(0, true, 0));
        assertTrue(scheduler.isSuspended());
        schedulerStateDAO.updateSchedulerState(new SchedulerState(0, false, 0));
        assertTrue(scheduler.isSuspended());
        scheduler.resetIsSuspended();
        assertFalse(scheduler.isSuspended());
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
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
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

    @Test
    public void testStartSuspensionNotActive() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        intervalDAO.insertInterval(getInterval1());
        preferenceManager.setPreferenceSuspensionEnabled(false);
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        assertFalse(scheduler.isRunning());
    }

    @Test
    public void testStartStartupSuspensionDisabledAfterRunning() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 30));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        preferenceManager.setPreferenceSuspensionEnabled(false);
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
    }

    @Test
    public void testStartStartNetworkTaskSuspensionDisabledAfterRunning() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 30));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        preferenceManager.setPreferenceSuspensionEnabled(false);
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start(task1);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
    }

    @Test
    public void testStartIsRunningAndSuspended() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        alarmManager.reset();
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testStartNetworkTaskIsRunningAndSuspended() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), getTestTimestamp(24, 10, 15));
        networkTaskDAO.increaseNetworkTaskInstances(task1.getId());
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        alarmManager.reset();
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start(task1);
        task1 = networkTaskDAO.readNetworkTask(task1.getId());
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        assertTrue(task1.getLastScheduled() < 0);
        assertEquals(0, task1.getInstances());
    }

    @Test
    public void testStartNetworkTaskIsNotRunningAndSuspended() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), getTestTimestamp(24, 10, 15));
        networkTaskDAO.increaseNetworkTaskInstances(task1.getId());
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        alarmManager.reset();
        scheduler.start(task1);
        task1 = networkTaskDAO.readNetworkTask(task1.getId());
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(task1.getLastScheduled() < 0);
        assertEquals(0, task1.getInstances());
    }

    @Test
    public void testStartStartupIsRunning() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 9, 1));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        alarmManager.reset();
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testStartStartIsRunning() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 9, 1));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        alarmManager.reset();
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start(task1);
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testStartSuspend() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval1().getEnd(), getTestTimestamp(24, 10, 15)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartSuspendOverlapDaysSameDay() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 23, 59));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampTomorrow(getInterval4().getEnd(), getTestTimestamp(24, 23, 59)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartSuspendOverlapDaysOtherDay() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 0, 1));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval4().getEnd(), getTestTimestamp(24, 0, 1)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartSuspendThreshold() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 10, 31));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval1().getEnd(), getTestTimestamp(24, 10, 15)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartStartup() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 9, 1));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmRTCCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmRTCCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmRTCCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval1().getStart(), getTestTimestamp(24, 9, 1)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartStartupOverlapDays() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 20, 0));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmRTCCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmRTCCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmRTCCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval4().getStart(), getTestTimestamp(24, 20, 0)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartStartupThreshold() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 11, 11, 31));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmRTCCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmRTCCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmRTCCalls.get(0);
        assertEquals(TimeUtil.getTimestampTomorrow(getInterval1().getStart(), getTestTimestamp(24, 11, 11)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStartStartNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 1, 31));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start(task1);
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmRTCCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmRTCCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmRTCCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval1().getStart(), getTestTimestamp(24, 1, 31)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        assertTrue(scheduler.isRunning());
    }

    @Test
    public void testStop() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(scheduler.isRunning());
        scheduler.stop();
        assertFalse(scheduler.isRunning());
    }

    @Test
    public void testRestartNoIntervals() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 1, 31));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start(task1);
        intervalDAO.deleteAllIntervals();
        scheduler.restart();
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertTrue(scheduler.getIntervals().isEmpty());
    }

    @Test
    public void testRestartSuspensionDisabled() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval4());
        setTestTime(getTestTimestamp(24, 1, 31));
        scheduler.start();
        networkTaskSchedulerAlarmManager.reset();
        preferenceManager.setPreferenceSuspensionEnabled(false);
        scheduler.restart();
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(scheduler.getIntervals().isEmpty());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
    }

    @Test
    public void testRestartNoNetworkTaskRunning() {
        NetworkTask task1 = getNetworkTask1();
        task1.setRunning(false);
        NetworkTask task2 = getNetworkTask2();
        networkTaskDAO.insertNetworkTask(task1);
        networkTaskDAO.insertNetworkTask(task2);
        setTestTime(getTestTimestamp(24, 1, 30));
        scheduler.restart();
        scheduler.start();
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        assertFalse(alarmManager.wasSetAlarmRTCCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testRestart() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        setTestTime(getTestTimestamp(24, 1, 30));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        networkTaskSchedulerAlarmManager.reset();
        scheduler.restart();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval2().getEnd(), getTestTimestamp(24, 1, 30)), setAlarm.getDelay());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
    }

    @Test
    public void testIsSuspensionActiveAndEnabled() {
        preferenceManager.setPreferenceSuspensionEnabled(false);
        assertFalse(scheduler.isSuspensionActiveAndEnabled());
        preferenceManager.setPreferenceSuspensionEnabled(true);
        assertFalse(scheduler.isSuspensionActiveAndEnabled());
        intervalDAO.insertInterval(getInterval1());
        scheduler.reset();
        assertTrue(scheduler.isSuspensionActiveAndEnabled());
        preferenceManager.setPreferenceSuspensionEnabled(false);
        assertFalse(scheduler.isSuspensionActiveAndEnabled());
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
        return getTestTimestamp(day, hour, minute, 1);
    }

    private long getTestTimestamp(int day, int hour, int minute, int second) {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, day, hour, minute, second);
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
