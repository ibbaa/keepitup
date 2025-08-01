/*
 * Copyright (c) 2025 Alwin Ibba
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
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
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
import net.ibbaa.keepitup.test.mock.TestTimeBasedSuspensionBroadcastReceiver;
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
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class TimeBasedSuspensionBroadcastReceiverTest {

    private TestTimeBasedSuspensionBroadcastReceiver receiver;
    private TestTimeBasedSuspensionScheduler scheduler;
    private TestNetworkTaskProcessServiceScheduler networkTaskScheduler;
    private NetworkTaskDAO networkTaskDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private MockTimeService timeService;
    private MockAlarmManager alarmManager;
    private MockAlarmManager networkTaskSchedulerAlarmManager;

    @Before
    public void beforeEachTestMethod() {
        receiver = new TestTimeBasedSuspensionBroadcastReceiver();
        scheduler = receiver.getScheduler();
        networkTaskScheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        networkTaskScheduler.setTimeBasedSuspensionScheduler(scheduler);
        scheduler.setNetworkTaskScheduler(networkTaskScheduler);
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.stop();
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
        timeService = receiver.getMockTimeService();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.stop();
        alarmManager.reset();
        networkTaskSchedulerAlarmManager.reset();
    }

    @Test
    public void testIntentNull() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        intervalDAO.insertInterval(getInterval());
        networkTaskSchedulerAlarmManager.reset();
        scheduler.reset();
        receiver.onReceive(TestRegistry.getContext(), null);
        assertFalse(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
    }

    @Test
    public void testWasRestartedNoMarkerReceived() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.restart();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 13, 0));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.reset();
        Intent intent = new Intent();
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_action_key), TimeBasedSuspensionScheduler.Action.UP.name());
        receiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
    }

    @Test
    public void testWasRestartedMarkerReceived() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task1 = networkTaskDAO.insertNetworkTask(task1);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.restart();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 13, 0));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.reset();
        Intent intent = new Intent();
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_action_key), TimeBasedSuspensionScheduler.Action.UP.name());
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_restart_key), true);
        receiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
    }

    @Test
    public void testSuspend() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval());
        setTestTime(getTestTimestamp(24, 9, 0));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.resetWasRestartedFlag();
        scheduler.reset();
        alarmManager.reset();
        Intent intent = new Intent();
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_action_key), TimeBasedSuspensionScheduler.Action.DOWN.name());
        receiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmCalls.get(0);
        assertEquals(TimeUtil.getTimestampToday(getInterval().getEnd(), getTestTimestamp(24, 10, 15)), setAlarm.delay());
        assertFalse(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertTrue(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.CancelAlarmCall> cancelAlarmCalls = networkTaskSchedulerAlarmManager.getCancelAlarmCalls();
        assertEquals(2, cancelAlarmCalls.size());
    }

    @Test
    public void testSuspendThreshold() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskScheduler.schedule(task1);
        intervalDAO.insertInterval(getInterval());
        setTestTime(getTestTimestamp(24, 10, 9, 0));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 10, 10, 51));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.resetWasRestartedFlag();
        scheduler.reset();
        alarmManager.reset();
        Intent intent = new Intent();
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_action_key), TimeBasedSuspensionScheduler.Action.DOWN.name());
        receiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
    }

    @Test
    public void testStart() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 13, 0));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.resetWasRestartedFlag();
        scheduler.reset();
        alarmManager.reset();
        Intent intent = new Intent();
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_action_key), TimeBasedSuspensionScheduler.Action.UP.name());
        receiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
        assertTrue(alarmManager.wasSetAlarmRTCCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmRTCCalls = alarmManager.getSetAlarmRTCCalls();
        assertEquals(1, setAlarmRTCCalls.size());
        MockAlarmManager.SetAlarmCall setAlarm = setAlarmRTCCalls.get(0);
        assertEquals(TimeUtil.getTimestampTomorrow(getInterval().getStart(), getTestTimestamp(24, 13, 0)), setAlarm.delay());
        assertTrue(networkTaskSchedulerAlarmManager.wasSetAlarmCalled());
        assertFalse(networkTaskSchedulerAlarmManager.wasCancelAlarmCalled());
        List<MockAlarmManager.SetAlarmCall> setAlarmCalls = networkTaskSchedulerAlarmManager.getSetAlarmCalls();
        assertEquals(2, setAlarmCalls.size());
    }

    @Test
    public void testStartThreshold() {
        NetworkTask task1 = getNetworkTask1();
        NetworkTask task2 = getNetworkTask2();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        networkTaskDAO.updateNetworkTaskRunning(task1.getId(), true);
        networkTaskDAO.updateNetworkTaskRunning(task2.getId(), true);
        networkTaskScheduler.schedule(task1);
        networkTaskScheduler.schedule(task2);
        intervalDAO.insertInterval(getInterval());
        setTestTime(getTestTimestamp(24, 10, 15));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.start();
        assertTrue(scheduler.isRunning());
        assertTrue(scheduler.getWasRestartedFlag());
        assertTrue(schedulerStateDAO.readSchedulerState().isSuspended());
        assertTrue(scheduler.isSuspended());
        setTestTime(getTestTimestamp(24, 11, 11, 41));
        networkTaskSchedulerAlarmManager.reset();
        scheduler.resetWasRestartedFlag();
        scheduler.reset();
        alarmManager.reset();
        Intent intent = new Intent();
        intent.putExtra(TestRegistry.getContext().getResources().getString(R.string.scheduler_action_key), TimeBasedSuspensionScheduler.Action.UP.name());
        receiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(scheduler.getWasRestartedFlag());
        assertFalse(schedulerStateDAO.readSchedulerState().isSuspended());
        assertFalse(scheduler.isSuspended());
    }

    private void setTestTime(long time) {
        timeService.setTimestamp(time);
        timeService.setTimestamp2(time);
        ((MockTimeService) scheduler.getTimeService()).setTimestamp(time);
        ((MockTimeService) scheduler.getTimeService()).setTimestamp2(time);
    }

    private long getTestTimestamp(int day, int hour, int minute) {
        return getTestTimestamp(day, hour, minute, 1);
    }

    private long getTestTimestamp(int day, int hour, int minute, int second) {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, day, hour, minute, second);
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

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setName("name");
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(20);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(1);
        task.setHighPrio(true);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(10);
        task.setSchedulerId(2);
        task.setName("name");
        task.setInstances(0);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(1);
        task.setFailureCount(2);
        return task;
    }
}
