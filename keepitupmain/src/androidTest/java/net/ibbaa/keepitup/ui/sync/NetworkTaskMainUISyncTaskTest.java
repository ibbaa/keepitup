/*
 * Copyright (c) 2025 Alwin Ibba
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
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.TestRegistry;
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
        AccessTypeData data = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task.getId()));
        Resolve resolve = getResolveDAO().insertResolve(getResolve1(task.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task.getId(), new GregorianCalendar(1980, Calendar.MARCH, 18).getTime().getTime()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        NetworkTaskUIWrapper wrapper = syncTask.runInBackground();
        assertTrue(task.isEqual(wrapper.getNetworkTask()));
        assertTrue(data.isEqual(wrapper.getAccessTypeData()));
        assertTrue(resolve.isEqual(wrapper.getResolve()));
        assertTrue(logEntry2.isEqual(wrapper.getLogEntry()));
    }

    @Test
    public void testNullResolveAndLogEntry() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        AccessTypeData data = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task.getId()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        NetworkTaskUIWrapper wrapper = syncTask.runInBackground();
        assertTrue(task.isEqual(wrapper.getNetworkTask()));
        assertTrue(data.isEqual(wrapper.getAccessTypeData()));
        assertNull(wrapper.getResolve());
        assertNull(wrapper.getLogEntry());
    }

    @Test
    public void testAdapterLogUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        Resolve resolve1 = getResolveDAO().insertResolve(getResolve1(task1.getId()));
        Resolve resolve2 = getResolveDAO().insertResolve(getResolve2(task2.getId()));
        Resolve resolve3 = getResolveDAO().insertResolve(getResolve3(task3.getId()));
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, data1, resolve1, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, data2, resolve2, null);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, data3, resolve3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, getAdapter(activityScenario));
        syncTask.runOnUIThread(new NetworkTaskUIWrapper(task2, data2, resolve2, logEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(data2.isEqual(wrapper2.getAccessTypeData()));
        assertTrue(resolve2.isEqual(wrapper2.getResolve()));
        assertTrue(logEntry.isEqual(wrapper2.getLogEntry()));
    }

    @Test
    public void testAdapterTaskAccessTypeDataResolveAndLogUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        Resolve resolve1 = getResolveDAO().insertResolve(getResolve1(task1.getId()));
        Resolve resolve2 = getResolveDAO().insertResolve(getResolve2(task2.getId()));
        Resolve resolve3 = getResolveDAO().insertResolve(getResolve3(task3.getId()));
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, data1, resolve1, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, data2, resolve2, logEntry);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, data3, resolve3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        getNetworkTaskDAO().increaseNetworkTaskInstances(task2.getId());
        NetworkTask updatedTask2 = getNetworkTaskDAO().readNetworkTask(task2.getId());
        data2.setPingCount(7);
        AccessTypeData updatedData2 = getAccessTypeDataDAO().updateAccessTypeData(data2);
        resolve2.setAddress("address");
        Resolve updatedResolve2 = getResolveDAO().updateResolve(resolve2);
        LogEntry otherLogEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 18).getTime().getTime()));
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, getAdapter(activityScenario));
        syncTask.runOnUIThread(new NetworkTaskUIWrapper(updatedTask2, updatedData2, updatedResolve2, otherLogEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(updatedTask2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(updatedData2.isEqual(wrapper2.getAccessTypeData()));
        assertTrue(updatedResolve2.isEqual(wrapper2.getResolve()));
        assertTrue(otherLogEntry.isEqual(wrapper2.getLogEntry()));
    }

    @Test
    public void testAdapterTaskAccessTypeDataUpdate() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        AccessTypeData data = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task.getId()));
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        NetworkTaskUIWrapper wrapper = new NetworkTaskUIWrapper(task, data, null, null);
        adapter.addItem(wrapper);
        getNetworkTaskDAO().increaseNetworkTaskInstances(task.getId());
        NetworkTask updatedTask = getNetworkTaskDAO().readNetworkTask(task.getId());
        data.setPingCount(2);
        AccessTypeData updatedData = getAccessTypeDataDAO().updateAccessTypeData(data);
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), updatedTask, getAdapter(activityScenario));
        final NetworkTaskUIWrapper newWrapper = syncTask.runInBackground();
        assertNotNull(newWrapper);
        syncTask.runOnUIThread(newWrapper);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        adapter = getAdapter(activityScenario);
        assertEquals(1, adapter.getItemCount());
        wrapper = adapter.getItem(0);
        assertTrue(updatedTask.isEqual(wrapper.getNetworkTask()));
        assertTrue(updatedData.isEqual(wrapper.getAccessTypeData()));
        assertNull(wrapper.getResolve());
        assertNull(wrapper.getLogEntry());
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        Resolve resolve = getResolveDAO().insertResolve(getResolve1(task1.getId()));
        LogEntry logEntry = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, data1, null, null);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, data2, null, null);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, data3, null, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        NetworkTaskMainUISyncTask nullSyncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), null, null);
        nullSyncTask.runOnUIThread(new NetworkTaskUIWrapper(task2, data2, resolve, logEntry));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        wrapper2 = adapter.getItem(1);
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(data2.isEqual(wrapper2.getAccessTypeData()));
        assertNull(wrapper2.getResolve());
        assertNull(wrapper2.getLogEntry());
    }

    @Test
    public void testAdapterAccessTypeCreated() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTaskMainUISyncTask syncTask = new NetworkTaskMainUISyncTask(getActivity(activityScenario), task, getAdapter(activityScenario));
        NetworkTaskUIWrapper wrapper = syncTask.runInBackground();
        assertTrue(task.isEqual(wrapper.getNetworkTask()));
        AccessTypeData data = wrapper.getAccessTypeData();
        assertNotNull(data);
        AccessTypeData newData = new AccessTypeData(TestRegistry.getContext());
        newData.setNetworkTaskId(task.getId());
        assertTrue(data.isTechnicallyEqual(newData));
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setName("name");
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
        task.setHighPrio(true);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(2);
        task.setSchedulerId(5);
        task.setName("name");
        task.setInstances(0);
        task.setAddress("192.168.178.1");
        task.setPort(25);
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(10);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(false);
        task.setLastScheduled(1);
        task.setFailureCount(2);
        task.setHighPrio(true);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(2);
        task.setIndex(3);
        task.setSchedulerId(789);
        task.setName("name");
        task.setInstances(0);
        task.setAddress("www.host.com");
        task.setPort(456);
        task.setAccessType(AccessType.CONNECT);
        task.setInterval(20);
        task.setOnlyWifi(true);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        task.setFailureCount(3);
        task.setHighPrio(true);
        return task;
    }

    private AccessTypeData getAccessTypeData1(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(5);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }

    private AccessTypeData getAccessTypeData2(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(4);
        data.setPingPackageSize(12);
        data.setConnectCount(6);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }

    private AccessTypeData getAccessTypeData3(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(1);
        data.setPingPackageSize(678);
        data.setConnectCount(1);
        data.setStopOnSuccess(false);
        data.setIgnoreSSLError(false);
        return data;
    }

    private Resolve getResolve1(long networkTaskId) {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(networkTaskId);
        resolve.setAddress("192.168.178.1");
        resolve.setPort(22);
        return resolve;
    }

    private Resolve getResolve2(long networkTaskId) {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(networkTaskId);
        resolve.setAddress("192.168.178.1");
        resolve.setPort(443);
        return resolve;
    }

    private Resolve getResolve3(long networkTaskId) {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(networkTaskId);
        resolve.setAddress("127.0.0.1");
        resolve.setPort(-1);
        return resolve;
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
