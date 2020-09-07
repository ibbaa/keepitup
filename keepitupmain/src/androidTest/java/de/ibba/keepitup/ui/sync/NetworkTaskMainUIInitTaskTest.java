package de.ibba.keepitup.ui.sync;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainUIInitTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private NetworkTaskMainUIInitTask initTask;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        initTask = new NetworkTaskMainUIInitTask(getActivity(activityScenario), getAdapter(activityScenario));
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
    public void testAllNetworkTasksReturned() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        List<NetworkTaskUIWrapper> wrapperList = initTask.doInBackground();
        assertEquals(3, wrapperList.size());
        NetworkTaskUIWrapper wrapper1 = wrapperList.get(0);
        NetworkTaskUIWrapper wrapper2 = wrapperList.get(1);
        NetworkTaskUIWrapper wrapper3 = wrapperList.get(2);
        assertTrue(task1.isEqual(wrapper1.getNetworkTask()));
        assertTrue(logEntry1.isEqual(wrapper1.getLogEntry()));
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(logEntry2.isEqual(wrapper2.getLogEntry()));
        assertTrue(task3.isEqual(wrapper3.getNetworkTask()));
        assertNull(wrapper3.getLogEntry());
    }

    @Test
    public void testAdapterUpdate() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        final NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, logEntry1);
        final NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, logEntry2);
        final NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(new NetworkTaskUIWrapper(task3, logEntry2));
        getActivity(activityScenario).runOnUiThread(() -> initTask.onPostExecute(Arrays.asList(wrapper1, wrapper2, wrapper3)));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        adapter.getItem(0);
        adapter.getItem(1);
        adapter.getItem(2);
        assertTrue(task1.isEqual(wrapper1.getNetworkTask()));
        assertTrue(logEntry1.isEqual(wrapper1.getLogEntry()));
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(logEntry2.isEqual(wrapper2.getLogEntry()));
        assertTrue(task3.isEqual(wrapper3.getNetworkTask()));
        assertNull(wrapper3.getLogEntry());
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        final NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, logEntry1);
        final NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, logEntry2);
        final NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(new NetworkTaskUIWrapper(task3, logEntry2));
        NetworkTaskMainUIInitTask nullInitTask = new NetworkTaskMainUIInitTask(getActivity(activityScenario), null);
        getActivity(activityScenario).runOnUiThread(() -> nullInitTask.onPostExecute(Arrays.asList(wrapper1, wrapper2, wrapper3)));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(1, adapter.getAllItems().size());
    }

    @Test
    public void testAdapterUpdateWithEmptyList() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, logEntry1);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, logEntry2);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        getActivity(activityScenario).runOnUiThread(() -> initTask.onPostExecute(Collections.emptyList()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(0, adapter.getAllItems().size());
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
