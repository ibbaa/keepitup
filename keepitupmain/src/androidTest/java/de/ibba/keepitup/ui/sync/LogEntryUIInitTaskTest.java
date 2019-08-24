package de.ibba.keepitup.ui.sync;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogEntryUIInitTaskTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskLogActivity> rule = new ActivityTestRule<>(NetworkTaskLogActivity.class, false, false);

    private NetworkTaskLogActivity activity;
    private LogEntryUIInitTask initTask;
    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskLogActivity) launchRecyclerViewBaseActivity(rule, getNetworkTaskIntent(getNetworkTask()));
        initTask = new LogEntryUIInitTask(activity, (LogEntryAdapter) activity.getAdapter());
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO = new LogDAO(TestRegistry.getContext());
        logDAO.deleteAllLogs();
    }

    @After
    public void afterEachTestMethod() {
        logDAO.deleteAllLogs();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testAllLogEntriesReturned() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry3 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1982, Calendar.MARCH, 17).getTime().getTime()));
        List<LogEntry> syncLogEntries = initTask.doInBackground(task);
        assertEquals(3, syncLogEntries.size());
        assertAreEqual(logEntry3, syncLogEntries.get(0));
        assertAreEqual(logEntry2, syncLogEntries.get(1));
        assertAreEqual(logEntry1, syncLogEntries.get(2));
    }

    @Test
    public void testAdapterUpdate() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryAdapter adapter = (LogEntryAdapter) activity.getAdapter();
        adapter.addItem(logEntry);
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1982, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry3 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1983, Calendar.MARCH, 17).getTime().getTime()));
        activity.runOnUiThread(() -> initTask.onPostExecute(Arrays.asList(logEntry1, logEntry2, logEntry3)));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(3, adapter.getItemCount());
        assertAreEqual(logEntry1, adapter.getItem(0));
        assertAreEqual(logEntry2, adapter.getItem(1));
        assertAreEqual(logEntry3, adapter.getItem(2));
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryAdapter adapter = (LogEntryAdapter) activity.getAdapter();
        adapter.addItem(logEntry);
        LogEntry logEntry1 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1982, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry3 = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1983, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUIInitTask nullInitTask = new LogEntryUIInitTask(TestRegistry.getContext(), null);
        activity.runOnUiThread(() -> nullInitTask.onPostExecute(Arrays.asList(logEntry1, logEntry2, logEntry3)));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testAdapterUpdateLimitExceeded() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        List<LogEntry> logEntries = new ArrayList<>();
        for (int ii = 0; ii < 120; ii++) {
            LogEntry logEntry = logDAO.insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
            logEntries.add(logEntry);
        }
        activity.runOnUiThread(() -> initTask.onPostExecute(logEntries));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = (LogEntryAdapter) activity.getAdapter();
        assertEquals(100, adapter.getItemCount());
    }

    private void assertAreEqual(LogEntry entry1, LogEntry entry2) {
        assertEquals(entry1.getId(), entry2.getId());
        assertEquals(entry1.getNetworkTaskId(), entry2.getNetworkTaskId());
        assertEquals(entry1.isSuccess(), entry2.isSuccess());
        assertEquals(entry1.getTimestamp(), entry2.getTimestamp());
        assertEquals(entry1.getMessage(), entry2.getMessage());
    }

    private NetworkTask getNetworkTask() {
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
}