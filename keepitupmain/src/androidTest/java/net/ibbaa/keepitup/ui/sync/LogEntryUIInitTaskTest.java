/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskLogActivity;
import net.ibbaa.keepitup.ui.adapter.LogEntryAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogEntryUIInitTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskLogIntent(getNetworkTask()));
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testAllLogEntriesReturned() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry3 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1982, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUIInitTask initTask = new LogEntryUIInitTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        List<LogEntry> syncLogEntries = initTask.runInBackground();
        assertEquals(3, syncLogEntries.size());
        assertTrue(logEntry3.isEqual(syncLogEntries.get(0)));
        assertTrue(logEntry2.isEqual(syncLogEntries.get(1)));
        assertTrue(logEntry1.isEqual(syncLogEntries.get(2)));
    }

    @Test
    public void testAdapterUpdate() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(logEntry);
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1982, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry3 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1983, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUIInitTask initTask = new LogEntryUIInitTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        initTask.runOnUIThread(Arrays.asList(logEntry1, logEntry2, logEntry3));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(3, adapter.getItemCount());
        assertTrue(logEntry1.isEqual(adapter.getItem(0)));
        assertTrue(logEntry2.isEqual(adapter.getItem(1)));
        assertTrue(logEntry3.isEqual(adapter.getItem(2)));
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(logEntry);
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1982, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry3 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1983, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUIInitTask nullInitTask = new LogEntryUIInitTask(getActivity(activityScenario), task, null);
        nullInitTask.runOnUIThread(Arrays.asList(logEntry1, logEntry2, logEntry3));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testAdapterUpdateLimitExceeded() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        List<LogEntry> logEntries = new ArrayList<>();
        for (int ii = 0; ii < 120; ii++) {
            LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
            logEntries.add(logEntry);
        }
        LogEntryUIInitTask initTask = new LogEntryUIInitTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        initTask.runOnUIThread(logEntries);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
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
        task.setFailureCount(1);
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

    private Intent getNetworkTaskLogIntent(NetworkTask task) {
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
