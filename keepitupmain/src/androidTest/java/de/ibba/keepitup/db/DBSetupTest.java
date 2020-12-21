package de.ibba.keepitup.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;

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
}
