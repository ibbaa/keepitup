/*
 * Copyright (c) 2023. Alwin Ibba
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

import java.util.Calendar;
import java.util.GregorianCalendar;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class LogEntryUISyncTaskTest extends BaseUITest {

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
    public void testMostRecentLogEntryReturned() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 15).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        LogEntry syncLogEntry = syncTask.runInBackground();
        assertTrue(logEntry.isEqual(syncLogEntry));
    }

    @Test
    public void testAdapterUpdate() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        syncTask.runOnUIThread(logEntry);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
        LogEntry adapterLogEntry = adapter.getItem(0);
        assertTrue(logEntry.isEqual(adapterLogEntry));
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        syncTask.runOnUIThread(logEntry1);
        syncTask.runOnUIThread(logEntry2);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
        assertEquals(2, adapter.getItemCount());
        LogEntry logEntry3 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntryUISyncTask nullSyncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, null);
        nullSyncTask.runOnUIThread(logEntry3);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testAdapterUpdateLimitExceeded() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        for (int ii = 0; ii < 100; ii++) {
            LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
            LogEntryUISyncTask syncTask = new LogEntryUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
            syncTask.runOnUIThread(logEntry1);
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        LogEntryAdapter adapter = getAdapter(activityScenario);
        assertEquals(100, adapter.getItemCount());
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
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
