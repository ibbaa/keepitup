package de.ibba.keepitup.ui.sync;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.db.SchedulerIdHistoryDAO;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBPurgeTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private SchedulerIdHistoryDAO schedulerIdDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
        schedulerIdDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        schedulerIdDAO.deleteAllSchedulerIds();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
        activityScenario.close();
    }

    @Test
    public void testPurge() {
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        logDAO.insertAndDeleteLog(new LogEntry());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(schedulerIdDAO.readAllSchedulerIds().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        DBPurgeTask task = new DBPurgeTask(getActivity(activityScenario));
        assertTrue(task.runInBackground());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(schedulerIdDAO.readAllSchedulerIds().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
    }
}
