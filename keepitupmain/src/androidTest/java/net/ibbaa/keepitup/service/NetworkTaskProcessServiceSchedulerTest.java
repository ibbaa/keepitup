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

import static net.ibbaa.keepitup.service.NetworkTaskProcessServiceScheduler.Delay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import net.ibbaa.keepitup.test.mock.MockAlarmManager;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestTimeBasedSuspensionScheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskProcessServiceSchedulerTest {

    private TestNetworkTaskProcessServiceScheduler scheduler;
    private TestTimeBasedSuspensionScheduler timeBasedScheduler;
    private NetworkTaskDAO networkTaskDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private MockAlarmManager alarmManager;
    private MockTimeService timeService;
    private MockTimeService timeSchedulerTimeService;

    @Before
    public void beforeEachTestMethod() {
        scheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.cancelAll();
        scheduler.reset();
        timeBasedScheduler = new TestTimeBasedSuspensionScheduler(TestRegistry.getContext());
        scheduler.setTimeBasedSuspensionScheduler(timeBasedScheduler);
        timeBasedScheduler.setNetworkTaskScheduler(scheduler);
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().reset();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        alarmManager = (MockAlarmManager) scheduler.getAlarmManager();
        alarmManager.reset();
        timeService = (MockTimeService) scheduler.getTimeService();
        timeSchedulerTimeService = (MockTimeService) timeBasedScheduler.getTimeService();
    }

    @After
    public void afterEachTestMethod() {
        scheduler.reset();
        scheduler.cancelAll();
        NetworkTaskProcessServiceScheduler.getNetworkTaskProcessPool().reset();
        networkTaskDAO.deleteAllNetworkTasks();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
    }

    @Test
    public void testStartCancel() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        setTestTime(125);
        task1 = scheduler.start(task1);
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        task2 = scheduler.start(task2);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall2 = setAlarmCalls.get(1);
        assertEquals(0, setAlarmCall2.getDelay());
        assertNotEquals(setAlarmCall1.getPendingIntent(), setAlarmCall2.getPendingIntent());
        task1 = scheduler.cancel(task1);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(1, cancelAlarmCalls.size());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall1 = cancelAlarmCalls.get(0);
        assertEquals(setAlarmCall1.getPendingIntent(), cancelAlarmCall1.getPendingIntent());
        task2 = scheduler.cancel(task2);
        assertFalse(task1.isRunning());
        assertFalse(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
        cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall2 = cancelAlarmCalls.get(1);
        assertEquals(setAlarmCall2.getPendingIntent(), cancelAlarmCall2.getPendingIntent());
    }

    @Test
    public void testStartNotSuspended() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        intervalDAO.insertInterval(getInterval());
        timeBasedScheduler.reset();
        setTestTime(getTestTimestamp(24, 10, 5));
        task1 = scheduler.start(task1);
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(alarmManager.wasSetAlarmCalled());
    }

    @Test
    public void testStartSuspended() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        intervalDAO.insertInterval(getInterval());
        timeBasedScheduler.reset();
        setTestTime(getTestTimestamp(24, 10, 15));
        task1 = scheduler.start(task1);
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(alarmManager.wasSetAlarmCalled());
    }

    @Test
    public void testSchedule() {
        assertFalse(alarmManager.wasSetAlarmCalled());
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        setTestTime(125);
        task1 = scheduler.schedule(task1);
        assertFalse(task1.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        task1 = scheduler.schedule(task1);
        assertTrue(task1.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testRescheduleTerminateInterval() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        setTestTime(125);
        task1 = scheduler.reschedule(task1, Delay.INTERVAL);
        assertFalse(task1.isRunning());
        assertFalse(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        alarmManager.reset();
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        int schedulerId = task2.getSchedulerId();
        task2.setSchedulerId(schedulerId + 1);
        task2 = scheduler.reschedule(task2, Delay.INTERVAL);
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        task2.setSchedulerId(schedulerId);
        alarmManager.reset();
        task2 = scheduler.reschedule(task2, Delay.INTERVAL);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(60 * 1000, setAlarmCall1.getDelay());
        task2 = scheduler.terminate(task2);
        assertFalse(task1.isRunning());
        assertTrue(task2.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(1, cancelAlarmCalls.size());
        MockAlarmManager.CancelAlarmCall cancelAlarmCall1 = cancelAlarmCalls.get(0);
        assertEquals(setAlarmCall1.getPendingIntent(), cancelAlarmCall1.getPendingIntent());
    }

    @Test
    public void testRescheduleTerminateImmediate() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        setTestTime(125);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        scheduler.reschedule(task1, Delay.IMMEDIATE);
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
    }

    @Test
    public void testRescheduleTerminateLastScheduledZero() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        setTestTime(125);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        task1.setLastScheduled(-125);
        task1 = scheduler.reschedule(task1, Delay.LASTSCHEDULED);
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        alarmManager.reset();
        setTestTime(Long.MAX_VALUE);
        task1.setLastScheduled(1);
        task1 = scheduler.reschedule(task1, Delay.LASTSCHEDULED);
        setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        alarmManager.reset();
        setTestTime(20 * 60 * 1000 + 1);
        task1.setLastScheduled(1);
        scheduler.reschedule(task1, Delay.LASTSCHEDULED);
        setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
    }

    @Test
    public void testRescheduleTerminateLastScheduledPositive() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        setTestTime(125);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        task1.setLastScheduled(125);
        task1 = scheduler.reschedule(task1, Delay.LASTSCHEDULED);
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(20 * 60 * 1000, setAlarmCall1.getDelay());
        alarmManager.reset();
        setTestTime(125);
        task1.setLastScheduled(124);
        task1 = scheduler.reschedule(task1, Delay.LASTSCHEDULED);
        setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(20 * 60 * 1000 - 1, setAlarmCall1.getDelay());
        alarmManager.reset();
        setTestTime(20 * 60 * 1000);
        task1.setLastScheduled(1);
        scheduler.reschedule(task1, Delay.LASTSCHEDULED);
        setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(1, setAlarmCall1.getDelay());
    }

    @Test
    public void testStartup() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        setTestTime(125);
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
        scheduler.startup();
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertFalse(alarmManager.wasSetAlarmCalled());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
        scheduler.start(task1);
        scheduler.terminate(task1);
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        networkTaskDAO.increaseNetworkTaskInstances(task1.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        alarmManager.reset();
        setTestTime(126);
        task1.setLastScheduled(125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), 125);
        scheduler.startup();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertTrue(alarmManager.wasSetAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(1200000 - 1, setAlarmCall1.getDelay());
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
        scheduler.start(task2);
        alarmManager.reset();
        setTestTime(Long.MAX_VALUE);
        task1.setLastScheduled(125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), 125);
        scheduler.startup();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
        assertTrue(alarmManager.wasSetAlarmCalled());
        assertEquals(2, setAlarmCalls.size());
        setAlarmCall1 = setAlarmCalls.get(0);
        assertEquals(0, setAlarmCall1.getDelay());
        MockAlarmManager.SetAlarmCall setAlarmCall2 = setAlarmCalls.get(1);
        assertEquals(0, setAlarmCall2.getDelay());
        scheduler.terminate(task2);
        scheduler.startup();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertTrue(alarmManager.wasSetAlarmCalled());
    }

    @Test
    public void testCancelAll() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.start(task1);
        task2 = scheduler.start(task2);
        task1.setLastScheduled(125);
        task2.setLastScheduled(125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), 125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task2.getId(), 125);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.cancelAll();
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertFalse(isTaskMarkedAsRunningInDatabase(task2));
        assertLastScheduledInDatabase(task1, -1);
        assertLastScheduledInDatabase(task1, -1);
        assertTrue(alarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testTimeBasedSchedulerStartedAndStopped() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        intervalDAO.insertInterval(getInterval());
        timeBasedScheduler.reset();
        setTestTime(getTestTimestamp(24, 10, 5));
        assertFalse(timeBasedScheduler.isRunning());
        task1 = scheduler.start(task1);
        assertTrue(timeBasedScheduler.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(alarmManager.wasSetAlarmCalled());
        scheduler.cancel(task1);
        assertFalse(timeBasedScheduler.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(alarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testTimeBasedSchedulerStartedAndStoppedCancelAll() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1.setRunning(true);
        task2.setRunning(true);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        intervalDAO.insertInterval(getInterval());
        timeBasedScheduler.reset();
        setTestTime(getTestTimestamp(24, 10, 5));
        assertFalse(timeBasedScheduler.isRunning());
        scheduler.start(task1);
        scheduler.start(task2);
        assertTrue(timeBasedScheduler.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(alarmManager.wasSetAlarmCalled());
        scheduler.cancelAll();
        assertFalse(timeBasedScheduler.isRunning());
        assertFalse(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(alarmManager.wasCancelAlarmCalled());
    }

    @Test
    public void testAreNetworkTasksRunning() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        assertFalse(scheduler.areNetworkTasksRunning());
        scheduler.start(task1);
        scheduler.start(task2);
        assertTrue(scheduler.areNetworkTasksRunning());
        scheduler.cancelAll();
        assertFalse(scheduler.areNetworkTasksRunning());
    }

    @Test
    public void testTerminateAll() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        networkTaskDAO.increaseNetworkTaskInstances(task1.getId());
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertEquals(2, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
        task1 = scheduler.start(task1);
        task2 = scheduler.start(task2);
        task1.setLastScheduled(125);
        task2.setLastScheduled(125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), 125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task2.getId(), 125);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.terminateAll();
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertLastScheduledInDatabase(task1, 125);
        assertLastScheduledInDatabase(task1, 125);
        assertTrue(alarmManager.wasCancelAlarmCalled());
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(task1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(task2.getId()));
    }

    @Test
    public void testSuspendAll() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        task1 = scheduler.start(task1);
        task2 = scheduler.start(task2);
        task1.setLastScheduled(125);
        task2.setLastScheduled(125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task1.getId(), 125);
        networkTaskDAO.updateNetworkTaskLastScheduled(task2.getId(), 125);
        assertTrue(task1.isRunning());
        assertTrue(task2.isRunning());
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        scheduler.suspendAll();
        assertTrue(isTaskMarkedAsRunningInDatabase(task1));
        assertTrue(isTaskMarkedAsRunningInDatabase(task2));
        assertLastScheduledInDatabase(task1, 125);
        assertLastScheduledInDatabase(task1, 125);
        assertTrue(alarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = alarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
    }

    private boolean isTaskMarkedAsRunningInDatabase(NetworkTask task) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        return task.isRunning();
    }

    private void assertLastScheduledInDatabase(NetworkTask task, long value) {
        task = networkTaskDAO.readNetworkTask(task.getId());
        assertEquals(value, task.getLastScheduled());
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
        task.setRunning(false);
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

    private void setTestTime(long time) {
        timeService.setTimestamp(time);
        timeService.setTimestamp2(time);
        timeSchedulerTimeService.setTimestamp(time);
        timeSchedulerTimeService.setTimestamp2(time);
    }

    private long getTestTimestamp(int day, int hour, int minute) {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, day, hour, minute, 1);
        return calendar.getTimeInMillis();
    }

    private Interval getInterval() {
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
}
