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
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.db.SchedulerStateDAO;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerState;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.MockTimeService;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessBroadcastReceiver;
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
public class NetworkTaskProcessBroadcastReceiverTest {

    private TestTimeBasedSuspensionScheduler scheduler;
    private TestNetworkTaskProcessServiceScheduler networkTaskScheduler;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private TestNetworkTaskProcessBroadcastReceiver broadcastReceiver;
    private MockTimeService timeService;

    @Before
    public void beforeEachTestMethod() {
        broadcastReceiver = new TestNetworkTaskProcessBroadcastReceiver();
        scheduler = broadcastReceiver.getScheduler();
        networkTaskScheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        networkTaskScheduler.setTimeBasedSuspensionScheduler(scheduler);
        scheduler.setNetworkTaskScheduler(networkTaskScheduler);
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.resetIsSuspended();
        scheduler.stop();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        timeService = (MockTimeService) scheduler.getTimeService();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 0));
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.resetIsSuspended();
        scheduler.stop();

    }

    @Test
    public void testLogWritten() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(1, entries.size());
        LogEntry entry = entries.get(0);
        assertEquals(task.getId(), entry.getNetworkTaskId());
        assertEquals(getTestTimestamp(0, 0), entry.getTimestamp());
        assertTrue(entry.isSuccess());
        assertEquals("successful", entry.getMessage());
    }

    @Test
    public void testExecutionSkippedNetworkTaskDoesNotExist() {
        NetworkTask task = getNetworkTask();
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testExecutionSkippedMarkedAsNotRunning() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), false);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testExecutionSkippedNotValid() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        task.setSchedulerId(task.getSchedulerId() + 1);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(0, entries.size());
    }

    @Test
    public void testRescheduleRunningNotSuspended() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        intervalDAO.insertInterval(getInterval());
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        scheduler.scheduleStart(getInterval(), getTestTimestamp(0, 0), true);
        scheduler.resetIsSuspended();
        scheduler.reset();
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertTrue(networkTaskScheduler.wasRescheduleCalled());
        NetworkTask scheduledTask = networkTaskScheduler.getLastRescheduledTask();
        assertTrue(scheduledTask.isEqual(task));
    }

    @Test
    public void testRescheduleRunningNotSuspendedTaskInvalid() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), false);
        intervalDAO.insertInterval(getInterval());
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        scheduler.scheduleStart(getInterval(), getTestTimestamp(0, 0), true);
        scheduler.resetIsSuspended();
        scheduler.reset();
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(networkTaskScheduler.wasRescheduleCalled());
    }

    @Test
    public void testRescheduleRunningSuspended() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        intervalDAO.insertInterval(getInterval());
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        scheduler.scheduleSuspend(getInterval(), getTestTimestamp(1, 15), true);
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, true, 0));
        scheduler.resetIsSuspended();
        scheduler.reset();
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(networkTaskScheduler.wasRescheduleCalled());
    }

    @Test
    public void testRescheduleNotRunningDisabled() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        scheduler.resetIsSuspended();
        scheduler.reset();
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertFalse(scheduler.isRunning());
        assertTrue(networkTaskScheduler.wasRescheduleCalled());
        NetworkTask scheduledTask = networkTaskScheduler.getLastRescheduledTask();
        assertTrue(scheduledTask.isEqual(task));
    }

    @Test
    public void testRescheduleNotRunningEnabledRestarted() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        intervalDAO.insertInterval(getInterval());
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        scheduler.resetIsSuspended();
        scheduler.reset();
        setTestTime(getTestTimestamp(0, 0));
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertTrue(networkTaskScheduler.wasRescheduleCalled());
        NetworkTask scheduledTask = networkTaskScheduler.getLastRescheduledTask();
        assertTrue(scheduledTask.isEqual(task));
    }

    @Test
    public void testRescheduleNotRunningEnabledRestartedSuspended() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        networkTaskDAO.updateNetworkTaskRunning(task.getId(), true);
        intervalDAO.insertInterval(getInterval());
        Intent intent = new Intent();
        intent.putExtras(task.toBundle());
        scheduler.resetIsSuspended();
        scheduler.reset();
        setTestTime(getTestTimestamp(1, 15));
        broadcastReceiver.onReceive(TestRegistry.getContext(), intent);
        assertTrue(scheduler.isRunning());
        assertFalse(networkTaskScheduler.wasRescheduleCalled());
    }

    private void setTestTime(long time) {
        timeService.setTimestamp(time);
        timeService.setTimestamp2(time);
    }

    private long getTestTimestamp(int hour, int minute) {
        Calendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        return calendar.getTimeInMillis();
    }

    private NetworkTask getNetworkTask() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(1);
        networkTask.setIndex(1);
        networkTask.setSchedulerId(1);
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        return networkTask;
    }

    private Interval getInterval() {
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
}
