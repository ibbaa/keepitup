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

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.model.SchedulerState;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.ConstantPreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceManager;
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
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class DBSetupTest {

    private NetworkTaskDAO networkTaskDAO;
    private SchedulerIdHistoryDAO schedulerIdHistoryDAO;
    private SchedulerIdGenerator schedulerIdGenerator;
    private LogDAO logDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private AccessTypeDataDAO accessTypeDataDAO;
    private ResolveDAO resolveDAO;
    private HeaderDAO headerDAO;
    private DBSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        setup = new DBSetup(TestRegistry.getContext());
        setup.createTables();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        schedulerIdHistoryDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        schedulerIdGenerator = new SchedulerIdGenerator(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        accessTypeDataDAO = new AccessTypeDataDAO(TestRegistry.getContext());
        resolveDAO = new ResolveDAO(TestRegistry.getContext());
        headerDAO = new HeaderDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        schedulerStateDAO.deleteSchedulerState();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        headerDAO.deleteAllHeaders();
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
        resolveDAO.deleteAllResolve();
        headerDAO.deleteAllHeaders();
    }

    @Test
    public void testDropCreateTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        schedulerIdGenerator.enlistToSchedulerIdHistory(DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase(), 22);
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        resolveDAO.insertResolve(getResolve(0));
        headerDAO.insertHeader(getHeader(0));
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.dropTables();
        setup.createTables();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        assertEquals(1, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        schedulerIdGenerator.enlistToSchedulerIdHistory(DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase(), 22);
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        resolveDAO.insertResolve(getResolve(0));
        headerDAO.insertHeader(getHeader(0));
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertEquals(2, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        setup.recreateTables();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        assertEquals(1, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        resolveDAO.insertResolve(getResolve(0));
        headerDAO.insertHeader(getHeader(0));
        setup.tryDropTables();
        setup.createTables();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        assertEquals(1, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
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
        schedulerIdGenerator.enlistToSchedulerIdHistory(DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase(), 22);
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        setup.dropSchedulerIdHistoryTable();
        setup.createSchedulerIdHistoryTable();
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        schedulerIdGenerator.enlistToSchedulerIdHistory(DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase(), 22);
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
    public void testDropCreateAccessTypeDataTable() {
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
    public void testDropCreateResolveTable() {
        resolveDAO.insertResolve(getResolve(0));
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        setup.dropResolveTable();
        setup.createResolveTable();
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        resolveDAO.insertResolve(getResolve(0));
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        setup.recreateResolveTable();
        assertTrue(resolveDAO.readAllResolve().isEmpty());
    }

    @Test
    public void testDropCreateHeaderTable() {
        assertTrue(headerDAO.readAllHeaders().isEmpty());
        headerDAO.insertHeader(getHeader(0));
        assertEquals(1, headerDAO.readAllHeaders().size());
        assertEquals(0, headerDAO.readGlobalHeaders().size());
        headerDAO.insertHeader(getHeader(-1));
        assertEquals(2, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        setup.dropHeaderTable();
        setup.createHeaderTable();
        assertEquals(1, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
        resolveDAO.insertResolve(getResolve(0));
        headerDAO.insertHeader(getHeader(-1));
        assertEquals(2, headerDAO.readAllHeaders().size());
        assertEquals(2, headerDAO.readGlobalHeaders().size());
        setup.recreateHeaderTable();
        assertEquals(1, headerDAO.readAllHeaders().size());
        assertEquals(1, headerDAO.readGlobalHeaders().size());
    }

    @Test
    public void testInitializeHeaderTable() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        ConstantPreferenceManager constantPreferenceManager = new ConstantPreferenceManager(TestRegistry.getContext());
        assertTrue(headerDAO.readAllHeaders().isEmpty());
        setup.dropHeaderTable();
        setup.createHeaderTable();
        Header header = headerDAO.readGlobalHeaders().get(0);
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("User-Agent", header.getName());
        assertEquals(constantPreferenceManager.getPreferenceHTTPUserAgent(), header.getValue());
        preferenceManager.setPreferenceString(TestRegistry.getContext().getResources().getString(R.string.http_user_agent_key), "Test");
        setup.recreateHeaderTable();
        header = headerDAO.readGlobalHeaders().get(0);
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("User-Agent", header.getName());
        assertEquals("Test", header.getValue());
        assertEquals("Mozilla/5.0", constantPreferenceManager.getPreferenceHTTPUserAgent());
    }

    @Test
    public void testDeleteTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        schedulerIdGenerator.enlistToSchedulerIdHistory(DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase(), 22);
        logDAO.insertAndDeleteLog(new LogEntry());
        intervalDAO.insertInterval(new Interval());
        schedulerStateDAO.insertSchedulerState(new SchedulerState(0, false, 1));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData(0));
        resolveDAO.insertResolve(getResolve(0));
        headerDAO.insertHeader(getHeader(0));
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllNetworkTasks();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllSchedulerIds();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllLogs();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllIntervals();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteSchedulerState();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNull(schedulerStateDAO.readSchedulerState());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllAccessTypeData();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllResolve();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        assertFalse(headerDAO.readAllHeaders().isEmpty());
        setup.deleteAllHeaders();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertNull(schedulerStateDAO.readSchedulerState());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        assertTrue(headerDAO.readAllHeaders().isEmpty());
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
    public void testNormalizeUIIndex() {
        NetworkTask insertedTask1 = getNetworkTask1();
        NetworkTask insertedTask2 = getNetworkTask2();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        insertedTask2 = networkTaskDAO.insertNetworkTask(insertedTask2);
        setup.normalizeUIIndex();
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        assertEquals(0, readTasks.get(0).getIndex());
        assertEquals(1, readTasks.get(1).getIndex());
        assertTrue(insertedTask1.isTechnicallyEqual(readTasks.get(0)));
        assertTrue(insertedTask2.isTechnicallyEqual(readTasks.get(1)));
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
    public void testExportResolveForNetworkTask() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        Resolve resolve = getResolve(task.getId());
        resolveDAO.insertResolve(resolve);
        Map<String, ?> dataMap = setup.exportResolveForNetworkTask(task.getId());
        assertTrue(new Resolve(dataMap).isTechnicallyEqual(getResolve(task.getId())));
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
    public void testExportHeaders() {
        Header header1 = getHeader(-1);
        header1.setName("bName");
        Header header2 = getHeader(-1);
        header2.setName("aName");
        headerDAO.insertHeader(header1);
        headerDAO.insertHeader(getHeader(0));
        headerDAO.insertHeader(header2);
        List<Map<String, ?>> headerList = setup.exportGlobalHeaders();
        assertEquals(2, headerList.size());
        Header exportedHeader1 = new Header(headerList.get(0));
        Header exportedHeader2 = new Header(headerList.get(1));
        assertTrue(exportedHeader2.isEqual(header1));
        assertTrue(exportedHeader1.isEqual(header2));
    }

    @Test
    public void testImportNetworkTaskWithLogsAccessTypeDataAndResolve() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        Map<String, ?> entryMap1 = getLogEntry1(0).toMap();
        Map<String, ?> entryMap2 = getLogEntry2(0).toMap();
        Map<String, ?> entryMap3 = getLogEntry3(0).toMap();
        Map<String, ?> dataMap = getAccessTypeData(0).toMap();
        Map<String, ?> resolveMap = getResolve(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap, resolveMap);
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
        Resolve resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertTrue(resolve.isTechnicallyEqual(getResolve(task.getId())));
    }

    @Test
    public void testImportNetworkTaskAddressTrimmed() {
        NetworkTask task = getNetworkTask1();
        task.setAddress("   127.0.0.1");
        Map<String, ?> taskMap = task.toMap();
        Map<String, ?> dataMap = getAccessTypeData(0).toMap();
        Resolve resolve = getResolve(0);
        resolve.setTargetAddress("   127.0.0.1");
        Map<String, ?> resolveMap = resolve.toMap();
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), dataMap, resolveMap);
        List<NetworkTask> taskList = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, taskList.size());
        task = taskList.get(0);
        assertTrue(task.isTechnicallyEqual(getNetworkTask1()));
        assertEquals("127.0.0.1", task.getAddress());
        resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertEquals("127.0.0.1", resolve.getTargetAddress());
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        task = getNetworkTask1();
        task.setAccessType(AccessType.DOWNLOAD);
        task.setAddress("   https://test.org   ");
        taskMap = task.toMap();
        resolve = getResolve(0);
        resolve.setTargetAddress("   192.168.178.1  ");
        resolveMap = resolve.toMap();
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), dataMap, resolveMap);
        taskList = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, taskList.size());
        task = taskList.get(0);
        assertEquals("https://test.org", task.getAddress());
        resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertEquals("192.168.178.1", resolve.getTargetAddress());
    }

    @Test
    public void testImportNetworkTaskWithLogsAccessTypeDataAndResolveMissing() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        Map<String, ?> entryMap1 = getLogEntry1(0).toMap();
        Map<String, ?> entryMap2 = getLogEntry2(0).toMap();
        Map<String, ?> entryMap3 = getLogEntry3(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), null, null);
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
        assertTrue(resolveDAO.readAllResolve().isEmpty());
    }

    @Test
    public void testImportNetworkTaskWithLogsAndAccessTypeDataNotResetRunning() {
        Map<String, ?> taskMap = getNetworkTask1().toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, null, null, null, false);
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
        Map<String, ?> resolveMap = getResolve(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap, resolveMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        task = getNetworkTask1();
        task.setPort(80000);
        taskMap = task.toMap();
        dataMap = getAccessTypeData(0).toMap();
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap, resolveMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        task = getNetworkTask1();
        task.setAccessType(null);
        taskMap = task.toMap();
        dataMap = getAccessTypeData(0).toMap();
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap, resolveMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        task = getNetworkTask1();
        task.setInterval(-1);
        taskMap = task.toMap();
        dataMap = getAccessTypeData(0).toMap();
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap, resolveMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        task = getNetworkTask1();
        task.setName("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
        taskMap = task.toMap();
        dataMap = getAccessTypeData(0).toMap();
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Arrays.asList(entryMap1, entryMap2, entryMap3), dataMap, resolveMap);
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
    }

    @Test
    public void testImportAccessTypeDataInvalid() {
        NetworkTask task = getNetworkTask1();
        Map<String, ?> taskMap = task.toMap();
        AccessTypeData data = getAccessTypeData(0);
        data.setPingCount(11);
        Map<String, ?> resolveMap = getResolve(0).toMap();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), data.toMap(), resolveMap);
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        AccessTypeData defaultData = new AccessTypeData(TestRegistry.getContext());
        defaultData.setNetworkTaskId(task.getId());
        assertTrue(defaultData.isTechnicallyEqual(data));
        Resolve resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertTrue(getResolve(1).isTechnicallyEqual(resolve));
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        data = getAccessTypeData(0);
        data.setPingPackageSize(12345678);
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), data.toMap(), resolveMap);
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        defaultData = new AccessTypeData(TestRegistry.getContext());
        defaultData.setNetworkTaskId(task.getId());
        assertTrue(defaultData.isTechnicallyEqual(data));
        resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertTrue(getResolve(task.getId()).isTechnicallyEqual(resolve));
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        data = getAccessTypeData(0);
        data.setConnectCount(11);
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), data.toMap(), resolveMap);
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        defaultData = new AccessTypeData(TestRegistry.getContext());
        defaultData.setNetworkTaskId(task.getId());
        assertTrue(defaultData.isTechnicallyEqual(data));
        resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertTrue(getResolve(task.getId()).isTechnicallyEqual(resolve));
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        data = getAccessTypeData(0);
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), data.toMap(), resolveMap);
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        defaultData = new AccessTypeData(TestRegistry.getContext());
        defaultData.setNetworkTaskId(task.getId());
        assertFalse(defaultData.isTechnicallyEqual(data));
        resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertTrue(getResolve(task.getId()).isTechnicallyEqual(resolve));
    }

    @Test
    public void testImportResolveInvalid() {
        NetworkTask task = getNetworkTask1();
        Map<String, ?> taskMap = task.toMap();
        Map<String, ?> dataMap = getAccessTypeData(0).toMap();
        Resolve resolve = getResolve(0);
        resolve.setTargetAddress("1.1.1.1.1");
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), dataMap, resolve.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        AccessTypeData data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        assertTrue(getAccessTypeData(task.getId()).isTechnicallyEqual(data));
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        resolve = getResolve(0);
        resolve.setTargetPort(Integer.MAX_VALUE);
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), data.toMap(), resolve.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        assertTrue(getAccessTypeData(task.getId()).isTechnicallyEqual(data));
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        resolveDAO.deleteAllResolve();
        resolve = getResolve(0);
        setup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, Collections.emptyList(), data.toMap(), resolve.toMap());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertFalse(resolveDAO.readAllResolve().isEmpty());
        task = networkTaskDAO.readAllNetworkTasks().get(0);
        data = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task.getId());
        resolve = resolveDAO.readResolveForNetworkTask(task.getId());
        assertTrue(getAccessTypeData(task.getId()).isTechnicallyEqual(data));
        assertTrue(getResolve(task.getId()).isTechnicallyEqual(resolve));
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

    @Test
    public void testImportGlobalHeaders() {
        Map<String, ?> headerMap1 = getHeader(-1).toMap();
        Map<String, ?> headerMap2 = getHeader(0).toMap();
        Map<String, ?> headerMap3 = getHeader(-2).toMap();
        assertTrue(headerDAO.readAllHeaders().isEmpty());
        setup.importGlobalHeaders(Arrays.asList(headerMap1, headerMap2, headerMap3));
        List<Header> headerList = headerDAO.readAllHeaders();
        List<Header> globalHeaderList = headerDAO.readGlobalHeaders();
        assertEquals(2, headerList.size());
        assertEquals(2, globalHeaderList.size());
        assertTrue(getHeader(-1).isTechnicallyEqual(headerList.get(0)));
        assertTrue(getHeader(-1).isTechnicallyEqual(headerList.get(1)));
        assertTrue(getHeader(-1).isTechnicallyEqual(globalHeaderList.get(0)));
        assertTrue(getHeader(-1).isTechnicallyEqual(globalHeaderList.get(1)));
    }

    @Test
    public void testImportGlobalHeadersInvalid() {
        Header header1 = getHeader(-1);
        Header header2 = getHeader(-1);
        Header header3 = getHeader(-1);
        Header header4 = getHeader(-1);
        Header header5 = getHeader(-1);
        Header header6 = getHeader(-1);
        header1.setName("  ");
        header2.setName("Name\nTest");
        header3.setName(new String(new char[129]));
        header4.setValue("Test\u0001More");
        header5.setValue("Name\nTest");
        header6.setValue(new String(new char[8193]));
        Map<String, ?> headerMap1 = header1.toMap();
        Map<String, ?> headerMap2 = header2.toMap();
        Map<String, ?> headerMap3 = header3.toMap();
        Map<String, ?> headerMap4 = header4.toMap();
        Map<String, ?> headerMap5 = header5.toMap();
        Map<String, ?> headerMap6 = header6.toMap();
        assertTrue(headerDAO.readAllHeaders().isEmpty());
        setup.importGlobalHeaders(Arrays.asList(headerMap1, headerMap2, headerMap3, headerMap4, headerMap5, headerMap6));
        assertTrue(headerDAO.readAllHeaders().isEmpty());
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

    private Resolve getResolve(long networkTaskId) {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(networkTaskId);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("host.com");
        resolve.setTargetPort(1234);
        return resolve;
    }

    private Header getHeader(long networkTaskId) {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(networkTaskId);
        header.setName("name");
        header.setValue("value");
        return header;
    }
}
