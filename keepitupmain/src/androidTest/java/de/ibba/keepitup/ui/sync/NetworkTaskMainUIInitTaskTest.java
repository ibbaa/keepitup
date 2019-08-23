package de.ibba.keepitup.ui.sync;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainUIInitTaskTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;
    private NetworkTaskMainUIInitTask initTask;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        initTask = new NetworkTaskMainUIInitTask(activity, (NetworkTaskAdapter) activity.getAdapter());
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
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
        assertAreEqual(task1, wrapper1.getNetworkTask());
        assertAreEqual(logEntry1, wrapper1.getLogEntry());
        assertAreEqual(task2, wrapper2.getNetworkTask());
        assertAreEqual(logEntry2, wrapper2.getLogEntry());
        assertAreEqual(task3, wrapper3.getNetworkTask());
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
        NetworkTaskAdapter adapter = (NetworkTaskAdapter) activity.getAdapter();
        adapter.addItem(new NetworkTaskUIWrapper(task3, logEntry2));
        activity.runOnUiThread(() -> initTask.onPostExecute(Arrays.asList(wrapper1, wrapper2, wrapper3)));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        NetworkTaskUIWrapper adapterWrapper1 = adapter.getItem(0);
        NetworkTaskUIWrapper adapterWrapper2 = adapter.getItem(1);
        NetworkTaskUIWrapper adapterWrapper3 = adapter.getItem(2);
        assertAreEqual(task1, adapterWrapper1.getNetworkTask());
        assertAreEqual(logEntry1, adapterWrapper1.getLogEntry());
        assertAreEqual(task2, adapterWrapper2.getNetworkTask());
        assertAreEqual(logEntry2, adapterWrapper2.getLogEntry());
        assertAreEqual(task3, adapterWrapper3.getNetworkTask());
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
        NetworkTaskAdapter adapter = (NetworkTaskAdapter) activity.getAdapter();
        adapter.addItem(new NetworkTaskUIWrapper(task3, logEntry2));
        NetworkTaskMainUIInitTask nullInitTask = new NetworkTaskMainUIInitTask(activity, null);
        activity.runOnUiThread(() -> nullInitTask.onPostExecute(Arrays.asList(wrapper1, wrapper2, wrapper3)));
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
        NetworkTaskAdapter adapter = (NetworkTaskAdapter) activity.getAdapter();
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        activity.runOnUiThread(() -> initTask.onPostExecute(Collections.emptyList()));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(0, adapter.getAllItems().size());
    }

    private void assertAreEqual(NetworkTask task1, NetworkTask task2) {
        assertEquals(task1.getId(), task2.getId());
        assertEquals(task1.getIndex(), task2.getIndex());
        assertEquals(task1.getSchedulerId(), task2.getSchedulerId());
        assertEquals(task1.getAccessType(), task2.getAccessType());
        assertEquals(task1.getAddress(), task2.getAddress());
        assertEquals(task1.getPort(), task2.getPort());
        assertEquals(task1.getInterval(), task2.getInterval());
        assertEquals(task1.isOnlyWifi(), task2.isOnlyWifi());
        assertEquals(task1.isNotification(), task2.isNotification());
        assertEquals(task1.isRunning(), task2.isRunning());
    }

    private void assertAreEqual(LogEntry entry1, LogEntry entry2) {
        assertEquals(entry1.getId(), entry2.getId());
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
}
