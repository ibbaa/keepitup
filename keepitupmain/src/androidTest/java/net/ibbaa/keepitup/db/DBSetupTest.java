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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.util.TimeUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBSetupTest {

    private NetworkTaskDAO networkTaskDAO;
    private SchedulerIdHistoryDAO schedulerIdHistoryDAO;
    private LogDAO logDAO;
    private IntervalDAO intervalDAO;
    private DBSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        setup = new DBSetup(TestRegistry.getContext());
        setup.createTables(TestRegistry.getContext());
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        schedulerIdHistoryDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
    }

    @After
    public void afterEachTestMethod() {
        setup.createTables(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
    }

    @Test
    public void testDropCreateTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.dropTables(TestRegistry.getContext());
        setup.createTables(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.recreateTables(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
    }

    @Test
    public void testDropCreateNetworkTaskTable() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.dropNetworkTaskTable(TestRegistry.getContext());
        setup.createNetworkTaskTable(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.recreateNetworkTaskTable(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
    }

    @Test
    public void testDropCreateLogTable() {
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.dropLogTable(TestRegistry.getContext());
        setup.createLogTable(TestRegistry.getContext());
        assertTrue(logDAO.readAllLogs().isEmpty());
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.recreateLogTable(TestRegistry.getContext());
        assertTrue(logDAO.readAllLogs().isEmpty());
    }

    @Test
    public void testDropCreateSchedulerIdHistoryTable() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        setup.dropSchedulerIdHistoryTable(TestRegistry.getContext());
        setup.createSchedulerIdHistoryTable(TestRegistry.getContext());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        setup.recreateSchedulerIdHistoryTable(TestRegistry.getContext());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
    }

    @Test
    public void testDropCreateIntervalTable() {
        intervalDAO.insertInterval(new Interval());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.dropIntervalTable(TestRegistry.getContext());
        setup.createIntervalTable(TestRegistry.getContext());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        intervalDAO.insertInterval(new Interval());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.recreateIntervalTable(TestRegistry.getContext());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
    }

    @Test
    public void testDeleteTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.deleteAllNetworkTasks(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.deleteAllSchedulerIds(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.deleteAllLogs(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.deleteAllIntervals(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
    }

    @Test
    public void testExportNetworkTasks() {
        networkTaskDAO.insertNetworkTask(getNetworkTask1());
        networkTaskDAO.insertNetworkTask(getNetworkTask2());
        networkTaskDAO.insertNetworkTask(getNetworkTask3());
        List<Map<String, ?>> taskList = setup.exportNetworkTasks(TestRegistry.getContext());
        NetworkTask task1 = new NetworkTask(taskList.get(0));
        NetworkTask task2 = new NetworkTask(taskList.get(1));
        NetworkTask task3 = new NetworkTask(taskList.get(2));
        assertTrue(task1.isTechnicallyEqual(getNetworkTask1()));
        assertTrue(task2.isTechnicallyEqual(getNetworkTask2()));
        assertTrue(task3.isTechnicallyEqual(getNetworkTask3()));
        assertEquals(1, task1.getIndex());
        assertEquals(2, task2.getIndex());
        assertEquals(3, task3.getIndex());
        assertTrue(task1.isRunning());
        assertFalse(task2.isRunning());
        assertFalse(task3.isRunning());
    }

    @Test
    public void testExportLogsForNetworkTask() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        LogEntry entry1 = getLogEntry1(task.getId());
        LogEntry entry2 = getLogEntry2(task.getId());
        LogEntry entry3 = getLogEntry3(task.getId());
        logDAO.insertAndDeleteLog(entry1);
        logDAO.insertAndDeleteLog(entry2);
        logDAO.insertAndDeleteLog(entry3);
        List<Map<String, ?>> entryList = setup.exportLogsForNetworkTask(TestRegistry.getContext(), task.getId());
        entry1 = new LogEntry(entryList.get(0));
        entry2 = new LogEntry(entryList.get(1));
        entry3 = new LogEntry(entryList.get(2));
        logEntryEquals(entry1, getLogEntry1(task.getId()));
        logEntryEquals(entry2, getLogEntry2(task.getId()));
        logEntryEquals(entry3, getLogEntry3(task.getId()));
    }

    @Test
    public void testExportIntervals() {
        Interval interval1 = intervalDAO.insertInterval(getInterval1());
        Interval interval2 = intervalDAO.insertInterval(getInterval2());
        Interval interval3 = intervalDAO.insertInterval(getInterval3());
        List<Map<String, ?>> intervalList = setup.exportIntervals(TestRegistry.getContext());
        Interval exportedInterval1 = new Interval(intervalList.get(0));
        Interval exportedInterval2 = new Interval(intervalList.get(1));
        Interval exportedInterval3 = new Interval(intervalList.get(2));
        List<Interval> intervals = Arrays.asList(exportedInterval1, exportedInterval2, exportedInterval3);
        List<Interval> originalIntervals = Arrays.asList(interval1, interval2, interval3);
        originalIntervals = TimeUtil.cleanAndSort(originalIntervals);
        intervals = TimeUtil.cleanAndSort(intervals);
        intervalEquals(originalIntervals.get(0), intervals.get(0));
        intervalEquals(originalIntervals.get(1), intervals.get(1));
        intervalEquals(originalIntervals.get(2), intervals.get(2));
    }

    @Test
    public void testImportNetworkTaskWithLogs() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        Map<String, ?> entryMap1 = getLogEntry1(0).toMap();
        Map<String, ?> entryMap2 = getLogEntry2(0).toMap();
        Map<String, ?> entryMap3 = getLogEntry3(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        setup.importNetworkTaskWithLogs(TestRegistry.getContext(), taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3));
        List<NetworkTask> taskList = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, taskList.size());
        NetworkTask task = taskList.get(0);
        List<LogEntry> entryList = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(3, entryList.size());
        assertTrue(task.isTechnicallyEqual(getNetworkTask1()));
        assertFalse(task.isRunning());
        logEntryEquals(entryList.get(0), getLogEntry1(task.getId()));
        logEntryEquals(entryList.get(1), getLogEntry2(task.getId()));
        logEntryEquals(entryList.get(2), getLogEntry3(task.getId()));
    }

    @Test
    public void testImportNetworkTaskWithLogsNotResetRunning() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.importNetworkTaskWithLogs(TestRegistry.getContext(), taskMap, null, false);
        List<NetworkTask> taskList = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, taskList.size());
        NetworkTask task = taskList.get(0);
        assertTrue(task.isTechnicallyEqual(getNetworkTask1()));
        assertTrue(task.isRunning());
    }

    @Test
    public void testImportNetworkTaskInvalid() {
        NetworkTask task = getNetworkTask1();
        task.setAddress("http:// xyz abc");
        Map<String, ?> taskMap = task.toMap();
        Map<String, ?> entryMap1 = getLogEntry1(0).toMap();
        Map<String, ?> entryMap2 = getLogEntry2(0).toMap();
        Map<String, ?> entryMap3 = getLogEntry3(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        setup.importNetworkTaskWithLogs(TestRegistry.getContext(), taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3));
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        task = getNetworkTask1();
        task.setPort(80000);
        setup.importNetworkTaskWithLogs(TestRegistry.getContext(), taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3));
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        task = getNetworkTask1();
        task.setAccessType(null);
        setup.importNetworkTaskWithLogs(TestRegistry.getContext(), taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3));
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        task = getNetworkTask1();
        task.setInterval(-1);
        setup.importNetworkTaskWithLogs(TestRegistry.getContext(), taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3));
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
    }

    @Test
    public void testImportIntervals() {
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = getInterval3().toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(TestRegistry.getContext(), Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(3, intervalList.size());
        List<Interval> originalList = Arrays.asList(getInterval1(), getInterval2(), getInterval3());
        originalList = TimeUtil.cleanAndSort(originalList);
        intervalList = TimeUtil.cleanAndSort(intervalList);
        intervalEquals(originalList.get(0), intervalList.get(0));
        intervalEquals(originalList.get(1), intervalList.get(1));
        intervalEquals(originalList.get(2), intervalList.get(2));
    }

    @Test
    public void testImportIntervalsInvalid() {
        Interval interval1 = getInterval1();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(11);
        interval1.setStart(start);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(9);
        interval1.setEnd(end);
        Map<String, ?> intervalMap1 = interval1.toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = getInterval3().toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(TestRegistry.getContext(), Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        List<Interval> originalList = Arrays.asList(getInterval2(), getInterval3());
        originalList = TimeUtil.cleanAndSort(originalList);
        intervalList = TimeUtil.cleanAndSort(intervalList);
        intervalEquals(originalList.get(0), intervalList.get(0));
        intervalEquals(originalList.get(1), intervalList.get(1));
    }

    @Test
    public void testImportIntervalsOverlap() {
        Interval interval3 = getInterval1();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(16);
        interval3.setStart(start);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(25);
        interval3.setEnd(end);
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = interval3.toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(TestRegistry.getContext(), Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        List<Interval> originalList = Arrays.asList(getInterval1(), getInterval2());
        originalList = TimeUtil.cleanAndSort(originalList);
        intervalList = TimeUtil.cleanAndSort(intervalList);
        intervalEquals(originalList.get(0), intervalList.get(0));
        intervalEquals(originalList.get(1), intervalList.get(1));
    }

    private void logEntryEquals(LogEntry entry1, LogEntry entry2) {
        assertEquals(entry1.getNetworkTaskId(), entry2.getNetworkTaskId());
        assertEquals(entry1.isSuccess(), entry2.isSuccess());
        assertEquals(entry1.getTimestamp(), entry2.getTimestamp());
        assertEquals(entry1.getMessage(), entry2.getMessage());
    }

    private void intervalEquals(Interval interval1, Interval interval2) {
        assertEquals(interval1.isActive(), interval2.isActive());
        assertEquals(interval1.getStart().getHour(), interval2.getStart().getHour());
        assertEquals(interval1.getStart().getMinute(), interval2.getStart().getMinute());
        assertEquals(interval1.getEnd().getHour(), interval2.getEnd().getHour());
        assertEquals(interval1.getEnd().getMinute(), interval2.getEnd().getMinute());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(2);
        task.setSchedulerId(0);
        task.setInstances(2);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setOnlyWifi(true);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(3);
        task.setSchedulerId(0);
        task.setInstances(3);
        task.setAddress(null);
        task.setPort(456);
        task.setAccessType(AccessType.PING);
        task.setInterval(200);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        return task;
    }

    private LogEntry getLogEntry1(long networkTaskId) {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(networkTaskId);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(789);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }

    private LogEntry getLogEntry2(long networkTaskId) {
        LogEntry insertedLogEntry2 = new LogEntry();
        insertedLogEntry2.setId(0);
        insertedLogEntry2.setNetworkTaskId(networkTaskId);
        insertedLogEntry2.setSuccess(false);
        insertedLogEntry2.setTimestamp(456);
        insertedLogEntry2.setMessage("TestMessage2");
        return insertedLogEntry2;
    }

    private LogEntry getLogEntry3(long networkTaskId) {
        LogEntry insertedLogEntry3 = new LogEntry();
        insertedLogEntry3.setId(0);
        insertedLogEntry3.setNetworkTaskId(networkTaskId);
        insertedLogEntry3.setSuccess(true);
        insertedLogEntry3.setTimestamp(123);
        insertedLogEntry3.setMessage("TestMessage3");
        return insertedLogEntry3;
    }

    private Interval getInterval1() {
        Interval interval = new Interval();
        interval.setId(0);
        interval.setActive(false);
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
        interval.setActive(true);
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
        interval.setActive(true);
        Time start = new Time();
        start.setHour(3);
        start.setMinute(3);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(4);
        interval.setEnd(end);
        return interval;
    }
}
