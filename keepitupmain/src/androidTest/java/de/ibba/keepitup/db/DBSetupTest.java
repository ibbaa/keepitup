package de.ibba.keepitup.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBSetupTest {

    private NetworkTaskDAO networkTaskDAO;
    private SchedulerIdHistoryDAO schedulerIdHistoryDAO;
    private LogDAO logDAO;
    private DBSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        setup = new DBSetup(TestRegistry.getContext());
        setup.createTables(TestRegistry.getContext());
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        schedulerIdHistoryDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        setup.createTables(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        logDAO.deleteAllLogs();
    }

    @Test
    public void testDropCreateTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.dropTables(TestRegistry.getContext());
        setup.createTables(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.recreateTables(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
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
    public void testDeleteTables() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.deleteAllNetworkTasks(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.deleteAllSchedulerIds(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        setup.deleteAllLogs(TestRegistry.getContext());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdHistoryDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
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

    private void logEntryEquals(LogEntry entry1, LogEntry entry2) {
        assertEquals(entry1.getNetworkTaskId(), entry2.getNetworkTaskId());
        assertEquals(entry1.isSuccess(), entry2.isSuccess());
        assertEquals(entry1.getTimestamp(), entry2.getTimestamp());
        assertEquals(entry1.getMessage(), entry2.getMessage());
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
}
