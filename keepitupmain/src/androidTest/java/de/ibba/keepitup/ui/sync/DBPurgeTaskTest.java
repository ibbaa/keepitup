package de.ibba.keepitup.ui.sync;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBPurgeTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testPurge() {
        getNetworkTaskDAO().insertNetworkTask(new NetworkTask());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        DBPurgeTask task = new DBPurgeTask(getActivity(activityScenario));
        assertTrue(task.runInBackground());
        assertTrue(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertTrue(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertTrue(getLogDAO().readAllLogs().isEmpty());
    }
}
