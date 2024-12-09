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
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainUIInitTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private NetworkTaskMainUIInitTask initTask;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        initTask = new NetworkTaskMainUIInitTask(getActivity(activityScenario), getAdapter(activityScenario));
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testAllNetworkTasksReturned() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        List<NetworkTaskUIWrapper> wrapperList = initTask.runInBackground();
        assertEquals(3, wrapperList.size());
        NetworkTaskUIWrapper wrapper1 = wrapperList.get(0);
        NetworkTaskUIWrapper wrapper2 = wrapperList.get(1);
        NetworkTaskUIWrapper wrapper3 = wrapperList.get(2);
        assertTrue(task1.isEqual(wrapper1.getNetworkTask()));
        assertTrue(data1.isEqual(wrapper1.getAccessTypeData()));
        assertTrue(logEntry1.isEqual(wrapper1.getLogEntry()));
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(data2.isEqual(wrapper2.getAccessTypeData()));
        assertTrue(logEntry2.isEqual(wrapper2.getLogEntry()));
        assertTrue(task3.isEqual(wrapper3.getNetworkTask()));
        assertTrue(data3.isEqual(wrapper3.getAccessTypeData()));
        assertNull(wrapper3.getLogEntry());
    }

    @Test
    public void testAdapterUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        final NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, data1, logEntry1);
        final NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, data2, logEntry2);
        final NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, data3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(new NetworkTaskUIWrapper(task3, null, logEntry2));
        initTask.runOnUIThread(Arrays.asList(wrapper1, wrapper2, wrapper3));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        adapter.getItem(0);
        adapter.getItem(1);
        adapter.getItem(2);
        assertTrue(task1.isEqual(wrapper1.getNetworkTask()));
        assertTrue(data1.isEqual(wrapper1.getAccessTypeData()));
        assertTrue(logEntry1.isEqual(wrapper1.getLogEntry()));
        assertTrue(task2.isEqual(wrapper2.getNetworkTask()));
        assertTrue(data2.isEqual(wrapper2.getAccessTypeData()));
        assertTrue(logEntry2.isEqual(wrapper2.getLogEntry()));
        assertTrue(task3.isEqual(wrapper3.getNetworkTask()));
        assertTrue(data3.isEqual(wrapper3.getAccessTypeData()));
        assertNull(wrapper3.getLogEntry());
    }

    @Test
    public void testNullAdapterUpdate() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        final NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, data1, logEntry1);
        final NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, data2, logEntry2);
        final NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, data3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(new NetworkTaskUIWrapper(task3, null, logEntry2));
        NetworkTaskMainUIInitTask nullInitTask = new NetworkTaskMainUIInitTask(getActivity(activityScenario), null);
        nullInitTask.runOnUIThread(Arrays.asList(wrapper1, wrapper2, wrapper3));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(1, adapter.getAllItems().size());
    }

    @Test
    public void testAdapterUpdateWithEmptyList() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        AccessTypeData data1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData data2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        AccessTypeData data3 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData3(task3.getId()));
        LogEntry logEntry1 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task1.getId(), new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime()));
        LogEntry logEntry2 = getLogDAO().insertAndDeleteLog(getLogEntryWithNetworkTaskId(task2.getId(), new GregorianCalendar(1981, Calendar.MARCH, 17).getTime().getTime()));
        NetworkTaskUIWrapper wrapper1 = new NetworkTaskUIWrapper(task1, data1, logEntry1);
        NetworkTaskUIWrapper wrapper2 = new NetworkTaskUIWrapper(task2, data2, logEntry2);
        NetworkTaskUIWrapper wrapper3 = new NetworkTaskUIWrapper(task3, data3, null);
        NetworkTaskAdapter adapter = getAdapter(activityScenario);
        adapter.addItem(wrapper1);
        adapter.addItem(wrapper2);
        adapter.addItem(wrapper3);
        initTask.runOnUIThread(Collections.emptyList());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertEquals(0, adapter.getAllItems().size());
    }

    @Test
    public void testAccessTypeCreated() {
        NetworkTask task = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        List<NetworkTaskUIWrapper> wrapperList = initTask.runInBackground();
        assertEquals(1, wrapperList.size());
        NetworkTaskUIWrapper wrapper = wrapperList.get(0);
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
        task.setFailureCount(2);
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
        task.setFailureCount(3);
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
        return data;
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
