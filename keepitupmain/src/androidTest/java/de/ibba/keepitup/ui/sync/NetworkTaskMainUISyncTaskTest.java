package de.ibba.keepitup.ui.sync;

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
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainUISyncTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
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
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 18).getTime().getTime()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        NetworkTaskUIWrapper wrapper = syncTask.runInBackground();
        assertTrue(task.isEqual(wrapper.getNetworkTask()));
        assertTrue(logEntry2.isEqual(wrapper.getLogEntry()));
    }

    @Test
    public void testAdapterLogUpdate() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, null);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, getAdapter(activityScenario));
        syncTask.runOnUIThread(new NetworkTaskUIWrapper(task2, logEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(logEntry.isEqual(wrapper2.getLogEntry()));
    }

    @Test
    public void testAdapterTaskAndLogUpdate() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, logEntry);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        networkTaskDAO.increaseNetworkTaskInstances(task2.getId());
        NetworkTask updatedTask2 = networkTaskDAO.readNetworkTask(task2.getId());
        LogEntry otherLogEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 18).getTime().getTime()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, getAdapter(activityScenario));
        syncTask.runOnUIThread(new NetworkTaskUIWrapper(updatedTask2, otherLogEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(updatedTask2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(otherLogEntry.isEqual(wrapper2.getLogEntry()));
    }

    @Test
    public void testAdapterTaskUpdate() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        NetworkTaskUIWrapper wrapper = new NetworkTaskUIWrapper(task, null);
        adapter.addItem(wrapper);
        networkTaskDAO.increaseNetworkTaskInstances(task.getId());
        NetworkTask updatedTask = networkTaskDAO.readNetworkTask(task.getId());
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), updatedTask, getAdapter(activityScenario));
        final NetworkTaskUIWrapper newWrapper = syncTask.runInBackground();
        assertNotNull(newWrapper);
        syncTask.runOnUIThread(newWrapper);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        adapter = getAdapter(activityScenario);
        assertEquals(2, adapter.getItemCount());
        wrapper = adapter.getItem(0);
        assertTrue(updatedTask.isEqual(wrapper.getNetworkTask()));
        assertNull(wrapper.getLogEntry());
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, null);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        NetworkTaskMainUISyncTask nullSyncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, null);
        nullSyncTask.runOnUIThread(new NetworkTaskUIWrapper(task2, logEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertNull(wrapper2.getLogEntry());
    }

    private NetworkTask getNetworkTask1() {
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

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setSchedulerId(5);
        task.setInstances(0);
        task.setAddress("192.168.178.1");
        task.setPort(25);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(10);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(false);
        task.setLastScheduled(1);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(3);
        task.setSchedulerId(789);
        task.setInstances(0);
        task.setAddress("www.host.com");
        task.setPort(456);
        task.setAccessType(AccessType.CONNECT);
        task.setInterval(20);
        task.setOnlyWifi(true);
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

    private NetworkTaskAdapter getAdapter(ActivityScenario<?> activityScenario) {
        return (NetworkTaskAdapter) getNetworkTaskMainActivity(activityScenario).getAdapter();
    }

    private NetworkTaskMainActivity getNetworkTaskMainActivity(ActivityScenario<?> activityScenario) {
        return (NetworkTaskMainActivity) super.getActivity(activityScenario);
    }
}
