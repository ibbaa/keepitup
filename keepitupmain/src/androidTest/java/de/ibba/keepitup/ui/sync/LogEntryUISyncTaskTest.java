package de.ibba.keepitup.ui.sync;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskLogActivity;
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogEntryUISyncTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskIntent(getNetworkTask()));
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
        activityScenario.close();
    }

    @Test
    public void testMostRecentLogEntryReturned() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 15).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        LogEntry syncLogEntry = syncTask.runInBackground();
        assertTrue(logEntry.isEqual(syncLogEntry));
    }

    @Test
    public void testAdapterUpdate() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        syncTask.runOnUIThread(logEntry);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
        LogEntry adapterLogEntry = adapter.getItem(0);
        assertTrue(logEntry.isEqual(adapterLogEntry));
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        syncTask.runOnUIThread(logEntry1);
        syncTask.runOnUIThread(logEntry2);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
        assertEquals(2, adapter.getItemCount());
        LogEntry logEntry3 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask nullSyncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, null);
        nullSyncTask.runOnUIThread(logEntry3);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testAdapterUpdateLimitExceeded() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        for (int ii = 0; ii < 100; ii++) {
            LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
            LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
            syncTask.runOnUIThread(logEntry1);
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
        assertEquals(100, adapter.getItemCount());
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        syncTask.runOnUIThread(logEntry2);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(100, adapter.getItemCount());
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        return task;
    }

    private LogEntry getLogEntryWithNetworkTaskId(long networkTaskId, long timestamp) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(networkTaskId);
        logEntry.setSuccess(true);
        logEntry.setTimestamp(timestamp);
        logEntry.setMessage("TestMessage");
        return logEntry;
    }

    private Intent getNetworkTaskIntent(NetworkTask task) {
        Intent intent = new Intent(TestRegistry.getContext(), NetworkTaskLogActivity.class);
        intent.putExtras(task.toBundle());
        return intent;
    }

    private LogEntryAdapter getAdapter(ActivityScenario<?> activityScenario) {
        return (LogEntryAdapter) getNetworkTaskLogActivity(activityScenario).getAdapter();
    }

    private NetworkTaskLogActivity getNetworkTaskLogActivity(ActivityScenario<?> activityScenario) {
        return (NetworkTaskLogActivity) super.getActivity(activityScenario);
    }
}
