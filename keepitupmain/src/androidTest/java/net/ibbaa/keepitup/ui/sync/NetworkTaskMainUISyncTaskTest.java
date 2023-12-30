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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainUISyncTaskTest extends BaseUITest {

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
    public void testMostRecentLogEntryReturned() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 18).getTime().getTime()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        NetworkTaskUIWrapper wrapper = syncTask.runInBackground();
        assertTrue(task.isEqual(wrapper.getNetworkTask()));
        assertTrue(logEntry2.isEqual(wrapper.getLogEntry()));
    }

    @Test
    public void testAdapterLogUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
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
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, logEntry);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        getNetworkTaskDAO().increaseNetworkTaskInstances(task2.getId());
        NetworkTask updatedTask2 = getNetworkTaskDAO().readNetworkTask(task2.getId());
        LogEntry otherLogEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 18).getTime().getTime()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, getAdapter(activityScenario));
        syncTask.runOnUIThread(new NetworkTaskUIWrapper(updatedTask2, otherLogEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(updatedTask2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(otherLogEntry.isEqual(wrapper2.getLogEntry()));
    }

    @Test
    public void testAdapterTaskUpdate() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        NetworkTaskUIWrapper wrapper = new NetworkTaskUIWrapper(task, null);
        adapter.addItem(wrapper);
        getNetworkTaskDAO().increaseNetworkTaskInstances(task.getId());
        NetworkTask updatedTask = getNetworkTaskDAO().readNetworkTask(task.getId());
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), updatedTask, getAdapter(activityScenario));
        final NetworkTaskUIWrapper newWrapper = syncTask.runInBackground();
        assertNotNull(newWrapper);
        syncTask.runOnUIThread(newWrapper);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        adapter = getAdapter(activityScenario);
        assertEquals(1, adapter.getItemCount());
        wrapper = adapter.getItem(0);
        assertTrue(updatedTask.isEqual(wrapper.getNetworkTask()));
        assertNull(wrapper.getLogEntry());
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
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
