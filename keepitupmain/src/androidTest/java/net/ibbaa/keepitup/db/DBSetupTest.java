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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerState;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBSetupTest {

    private NetworkTaskDAO networkTaskDAO;
    private SchedulerIdHistoryDAO schedulerIdHistoryDAO;
    private LogDAO logDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private AccessTypeDataDAO accessTypeDataDAO;
    private DBSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        setup = new DBSetup(TestRegistry.getContext());
        setup.createTables();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        schedulerIdHistoryDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        accessTypeDataDAO = new AccessTypeDataDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.deleteSchedulerState();
        accessTypeDataDAO.deleteAllAccessTypeData();
    }

    @After
    public void afterEachTestMethod() {
        setup.createTables();
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.deleteSchedulerState();
        accessTypeDataDAO.deleteAllAccessTypeData();
    }

    @Test
    public void testDropCreateTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.dropTables();
        setup.createTables();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.recreateTables();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testDropCreateNetworkTaskTable() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.dropNetworkTaskTable();
        setup.createNetworkTaskTable();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.recreateNetworkTaskTable();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
    }

    @Test
    public void testAddFailureCountColumn() {
        setup.dropNetworkTaskTable();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(TestRegistry.getContext());
        DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase().execSQL(dbConstants.getCreateTableStatementWithoutFailureCount());
        setup.addFailureCountColumnToNetworkTaskTable();
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertEquals(1, networkTaskDAO.readAllNetworkTasks().size());
    }

    @Test
    public void testAddHighPrioColumn() {
        setup.dropNetworkTaskTable();
        setup.dropAccessTypeDataTable();
        NetworkTaskDBConstants networkTaskDBConstants = new NetworkTaskDBConstants(TestRegistry.getContext());
        AccessTypeDataDBConstants accessTypeDataDBConstants = new AccessTypeDataDBConstants(TestRegistry.getContext());
        DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase().execSQL(networkTaskDBConstants.getCreateTableStatementWithoutHighPrioAndName());
        DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase().execSQL(accessTypeDataDBConstants.getCreateTableStatementWithoutIgnoreSSLError());
        setup.addHighPrioColumnToNetworkTaskTable();
        setup.addNameColumnToNetworkTaskTable();
        setup.addIgnoreSSLErrorColumnToAccessTypeDataTable();
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertEquals(1, networkTaskDAO.readAllNetworkTasks().size());
        accessTypeDataDAO.insertAccessTypeData(new AccessTypeData());
        assertEquals(1, accessTypeDataDAO.readAllAccessTypeData().size());
    }

    @Test
    public void testInitializeFailureCountColumn() {
        NetworkTask task1 = new NetworkTask();
        NetworkTask task2 = new NetworkTask();
        task1.setFailureCount(1);
        task2.setFailureCount(2);
        task1 = networkTaskDAO.insertNetworkTask(task1);
        task2 = networkTaskDAO.insertNetworkTask(task2);
        setup.initializeFailureCountColumn();
        assertEquals(0, networkTaskDAO.readNetworkTaskFailureCount(task1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskFailureCount(task2.getId()));
    }

    @Test
    public void testDropCreateLogTable() {
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.dropLogTable();
        setup.createLogTable();
        assertTrue(logDAO.readAllLogs().isEmpty());
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.recreateLogTable();
        assertTrue(logDAO.readAllLogs().isEmpty());
    }

    @Test
    public void testDropCreateSchedulerIdHistoryTable() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        setup.dropSchedulerIdHistoryTable();
        setup.createSchedulerIdHistoryTable();
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        setup.recreateSchedulerIdHistoryTable();
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
    }

    @Test
    public void testDropCreateIntervalTable() {
        intervalDAO.insertInterval(new Interval());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.dropIntervalTable();
        setup.createIntervalTable();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        intervalDAO.insertInterval(new Interval());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        setup.recreateIntervalTable();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
    }

    @Test
    public void testDropCreateSchedulerStateTable() {
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        assertNotNull(schedulerStateDAO.readSchedulerState());
        setup.dropSchedulerStateTable();
        setup.createSchedulerStateTable();
        assertNotNull(schedulerStateDAO.readSchedulerState());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        assertNotNull(schedulerStateDAO.readSchedulerState());
        setup.recreateSchedulerStateTable();
        assertNotNull(schedulerStateDAO.readSchedulerState());
    }

    @Test
    public void testDropAccessTypeDataTable() {
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.dropAccessTypeDataTable();
        setup.createAccessTypeDataTable();
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.recreateAccessTypeDataTable();
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testDeleteTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.deleteAllNetworkTasks();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.deleteAllSchedulerIds();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.deleteAllLogs();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.deleteAllIntervals();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.deleteSchedulerState();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.deleteAllAccessTypeData();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testInitializeAccessTypeDataTable() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.initializeAccessTypeDataTable();
        assertEquals(3, accessTypeDataDAO.readAllAccessTypeData().size());
        AccessTypeData data1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task1.getId());
        AccessTypeData data2 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task2.getId());
        AccessTypeData data3 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task3.getId());
        AccessTypeData data = new AccessTypeData(TestRegistry.getContext());
        data.setNetworkTaskId(task1.getId());
        assertTrue(data.isTechnicallyEqual(data1));
        data.setNetworkTaskId(task2.getId());
        assertTrue(data.isTechnicallyEqual(data2));
        data.setNetworkTaskId(task3.getId());
        assertTrue(data.isTechnicallyEqual(data3));
    }

    @Test
    public void testExportNetworkTasks() {
        networkTaskDAO.insertNetworkTask(getNetworkTask1());
        networkTaskDAO.insertNetworkTask(getNetworkTask2());
        networkTaskDAO.insertNetworkTask(getNetworkTask3());
        List<Map<String, ?>> taskList = setup.exportNetworkTasks();
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
        List<Map<String, ?>> entryList = setup.exportLogsForNetworkTask(task.getId());
        entry1 = new LogEntry(entryList.get(0));
        entry2 = new LogEntry(entryList.get(1));
        entry3 = new LogEntry(entryList.get(2));
        assertTrue(entry1.isTechnicallyEqual(getLogEntry1(task.getId())));
        assertTrue(entry2.isTechnicallyEqual(getLogEntry2(task.getId())));
        assertTrue(entry3.isTechnicallyEqual(getLogEntry3(task.getId())));
    }

    @Test
    public void testExportAccessTypeDataForNetworkTask() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        AccessTypeData data = getAccessTypeData(task.getId());
        accessTypeDataDAO.insertAccessTypeData(data);
        Map<String, ?> dataMap = setup.exportAccessTypeDataForNetworkTask(task.getId());
        assertTrue(new AccessTypeData(dataMap).isTechnicallyEqual(getAccessTypeData(task.getId())));
    }

    @Test
    public void testExportIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        List<Map<String, ?>> intervalList = setup.exportIntervals();
        Interval exportedInterval1 = new Interval(intervalList.get(0));
        Interval exportedInterval2 = new Interval(intervalList.get(1));
        Interval exportedInterval3 = new Interval(intervalList.get(2));
        assertTrue(getInterval2().isEqual(exportedInterval1));
        assertTrue(getInterval3().isEqual(exportedInterval2));
        assertTrue(getInterval1().isEqual(exportedInterval3));
    }

    @Test
    public void testImportNetworkTaskWithLogsAndAccessTypeData() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        Map<String, ?> entryMap1 = getLogEntry1(0).toMap();
        Map<String, ?> entryMap2 = getLogEntry2(0).toMap();
        Map<String, ?> entryMap3 = getLogEntry3(0).toMap();
        Map<String, ?> dataMap = getAccessTypeData(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap);
        List<NetworkTask> taskList = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, taskList.size());
        NetworkTask task = taskList.get(0);
        List<LogEntry> entryList = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(3, entryList.size());
        assertTrue(task.isTechnicallyEqual(getNetworkTask1()));
        assertFalse(task.isRunning());
        assertTrue(entryList.get(0).isTechnicallyEqual(getLogEntry1(task.getId())));
        assertTrue(entryList.get(1).isTechnicallyEqual(getLogEntry2(task.getId())));
        assertTrue(entryList.get(2).isTechnicallyEqual(getLogEntry3(task.getId())));
        AccessTypeData data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        assertTrue(data.isTechnicallyEqual(getAccessTypeData(task.getId())));
    }

    @Test
    public void testImportNetworkTaskWithLogsAndAccessTypeDataMissing() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        Map<String, ?> entryMap1 = getLogEntry1(0).toMap();
        Map<String, ?> entryMap2 = getLogEntry2(0).toMap();
        Map<String, ?> entryMap3 = getLogEntry3(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), null);
        List<NetworkTask> taskList = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, taskList.size());
        NetworkTask task = taskList.get(0);
        List<LogEntry> entryList = logDAO.readAllLogsForNetworkTask(task.getId());
        assertEquals(3, entryList.size());
        assertTrue(task.isTechnicallyEqual(getNetworkTask1()));
        assertFalse(task.isRunning());
        assertTrue(entryList.get(0).isTechnicallyEqual(getLogEntry1(task.getId())));
        assertTrue(entryList.get(1).isTechnicallyEqual(getLogEntry2(task.getId())));
        assertTrue(entryList.get(2).isTechnicallyEqual(getLogEntry3(task.getId())));
        AccessTypeData data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        AccessTypeData defaultData = new AccessTypeData(TestRegistry.getContext());
        defaultData.setNetworkTaskId(task.getId());
        assertTrue(defaultData.isTechnicallyEqual(data));
    }

    @Test
    public void testImportNetworkTaskWithLogsAndAccessTypeDataNotResetRunning() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, null, null, false);
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
        Map<String, ?> dataMap = getAccessTypeData(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        task = getNetworkTask1();
        task.setPort(80000);
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        task = getNetworkTask1();
        task.setAccessType(null);
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        task = getNetworkTask1();
        task.setInterval(-1);
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testImportAccessTypeDataInvalid() {
        NetworkTask task = getNetworkTask1();
        Map<String, ?> taskMap = task.toMap();
        AccessTypeData data = getAccessTypeData(0);
        data.setPingCount(11);
        data.setPingPackageSize(12345678);
        data.setConnectCount(11);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Collections.emptyList(), data.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        networkTaskDAO.deleteAllNetworkTasks();
        data.setPingCount(10);
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Collections.emptyList(), data.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        networkTaskDAO.deleteAllNetworkTasks();
        data.setPingPackageSize(65527);
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Collections.emptyList(), data.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        networkTaskDAO.deleteAllNetworkTasks();
        data.setConnectCount(10);
        setup.importNetworkTaskWithLogsAndAccessTypeData(taskMap, Collections.emptyList(), data.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testImportIntervals() {
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = getInterval3().toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(3, intervalList.size());
        assertTrue(getInterval2().isEqual(intervalList.get(0)));
        assertTrue(getInterval3().isEqual(intervalList.get(1)));
        assertTrue(getInterval1().isEqual(intervalList.get(2)));
    }

    @Test
    public void testImportIntervalsEdgeCaseBefore() {
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(5);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(40);
        interval2.setEnd(end);
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = interval2.toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        assertTrue(interval2.isEqual(intervalList.get(0)));
        assertTrue(getInterval1().isEqual(intervalList.get(1)));
    }

    @Test
    public void testImportIntervalsEdgeCaseAfter() {
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(11);
        start.setMinute(43);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(12);
        end.setMinute(14);
        interval2.setEnd(end);
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = interval2.toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        assertTrue(getInterval1().isEqual(intervalList.get(0)));
        assertTrue(interval2.isEqual(intervalList.get(1)));
    }

    @Test
    public void testImportIntervalsInvalid() {
        Interval interval1 = getInterval1();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(60);
        interval1.setStart(start);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(9);
        interval1.setEnd(end);
        Map<String, ?> intervalMap1 = interval1.toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = getInterval3().toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        assertTrue(getInterval2().isEqual(intervalList.get(0)));
        assertTrue(getInterval3().isEqual(intervalList.get(1)));
    }

    @Test
    public void testImportIntervalsInvalidMinDuration() {
        Interval interval1 = getInterval1();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(15);
        interval1.setStart(start);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(29);
        interval1.setEnd(end);
        Map<String, ?> intervalMap1 = interval1.toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(List.of(intervalMap1));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertTrue(intervalList.isEmpty());
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
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        assertTrue(getInterval2().isEqual(intervalList.get(0)));
        assertTrue(getInterval1().isEqual(intervalList.get(1)));
    }

    @Test
    public void testImportIntervalsOverlapWithDistance() {
        Interval interval3 = getInterval1();
        Time start = new Time();
        start.setHour(11);
        start.setMinute(20);
        interval3.setStart(start);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(59);
        interval3.setEnd(end);
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = interval3.toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(2, intervalList.size());
        assertTrue(getInterval2().isEqual(intervalList.get(0)));
        assertTrue(getInterval1().isEqual(intervalList.get(1)));
    }

    @Test
    public void testImportIntervalsOverlapWithDistanceBefore() {
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(5);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(5);
        interval2.setEnd(end);
        Map<String, ?> intervalMap1 = getInterval1().toMap();
        Map<String, ?> intervalMap2 = interval2.toMap();
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(1, intervalList.size());
        assertTrue(getInterval1().isEqual(intervalList.get(0)));
    }

    @Test
    public void testImportIntervalsOverlapDaysOverlap() {
        Interval interval1 = getInterval1();
        Time start = new Time();
        start.setHour(0);
        start.setMinute(1);
        interval1.setStart(start);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(0);
        interval1.setEnd(end);
        Map<String, ?> intervalMap1 = interval1.toMap();
        Map<String, ?> intervalMap2 = getInterval2().toMap();
        Map<String, ?> intervalMap3 = getInterval3().toMap();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        setup.importIntervals(Arrays.asList(intervalMap1, intervalMap2, intervalMap3));
        List<Interval> intervalList = intervalDAO.readAllIntervals();
        assertEquals(1, intervalList.size());
        assertTrue(interval1.isEqual(intervalList.get(0)));
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setName("name");
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        task.setFailureCount(2);
        task.setHighPrio(true);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(2);
        task.setSchedulerId(0);
        task.setName("name");
        task.setInstances(2);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setOnlyWifi(true);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        task.setFailureCount(2);
        task.setHighPrio(false);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(3);
        task.setSchedulerId(0);
        task.setName("name");
        task.setInstances(3);
        task.setAddress(null);
        task.setPort(456);
        task.setAccessType(AccessType.PING);
        task.setInterval(200);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        task.setFailureCount(1);
        task.setHighPrio(false);
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
        start.setHour(3);
        start.setMinute(3);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(4);
        interval.setEnd(end);
        return interval;
    }

    private AccessTypeData getAccessTypeData(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }
}
