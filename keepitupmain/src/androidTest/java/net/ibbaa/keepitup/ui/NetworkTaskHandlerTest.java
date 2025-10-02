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

package net.ibbaa.keepitup.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import net.ibbaa.keepitup.logging.NetworkTaskLog;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;
import net.ibbaa.phonelog.ILogger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class NetworkTaskHandlerTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private NetworkTaskHandler handler;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        handler = new NetworkTaskHandler((NetworkTaskMainActivity) getActivity(activityScenario));
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testStartStopNetworkTask() {
        NetworkTask task = getNetworkTask1();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        handler.startNetworkTask(task);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.isRunning());
        handler.stopNetworkTask(task);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task = tasks.get(0);
        assertFalse(task.isRunning());
    }

    @Test
    public void testStartStopNetworkTaskFailureCount() {
        NetworkTask task = getNetworkTask1();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        getNetworkTaskDAO().increaseNetworkTaskFailureCount(task.getId());
        handler.startNetworkTask(task);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task = tasks.get(0);
        assertTrue(task.isRunning());
        assertEquals(0, task.getFailureCount());
        getNetworkTaskDAO().increaseNetworkTaskFailureCount(task.getId());
        handler.stopNetworkTask(task);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task = tasks.get(0);
        assertFalse(task.isRunning());
        assertEquals(1, task.getFailureCount());
    }

    @Test
    public void testInsertNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(1, getAdapter().getItemCount());
        task1 = tasks.get(0);
        assertEquals(0, task1.getIndex());
        assertTrue(task1.getId() >= 0);
        AccessTypeData readData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(task1.getId());
        data1.setNetworkTaskId(task1.getId());
        assertTrue(data1.isTechnicallyEqual(readData1));
        Resolve readResolve1 = getResolveDAO().readResolveForNetworkTask(task1.getId());
        resolve1.setNetworkTaskId(task1.getId());
        assertTrue(resolve1.isTechnicallyEqual(readResolve1));
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertNull(adapterWrapper1.getLogEntry());
        assertTrue(task1.isEqual(adapterWrapper1.getNetworkTask()));
        assertTrue(data1.isEqual(adapterWrapper1.getAccessTypeData()));
        assertTrue(resolve1.isEqual(adapterWrapper1.getResolve()));
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData1();
        Resolve resolve2 = getResolve1();
        handler.insertNetworkTask(task2, data2, resolve2);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(2, tasks.size());
        assertEquals(2, getAdapter().getNextIndex());
        assertEquals(2, getAdapter().getItemCount());
    }

    @Test
    public void testInsertEmptyResolve() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(1, getAdapter().getItemCount());
        assertNull(getResolveDAO().readResolveForNetworkTask(task1.getId()));
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getAccessTypeDataDAO().deleteAllAccessTypeData();
        getResolveDAO().deleteAllResolve();
        getAdapter().replaceItems(Collections.emptyList());
        task1 = getNetworkTask1();
        data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, new Resolve());
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(1, getAdapter().getItemCount());
        assertNull(getResolveDAO().readResolveForNetworkTask(task1.getId()));
    }

    @Test
    public void testUpdateNetworkTaskName() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null);
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task1.getId());
        logEntry = getLogDAO().insertAndDeleteLog(logEntry);
        getAdapter().replaceItem(new NetworkTaskUIWrapper(task1, data1, null, logEntry));
        handler.updateNetworkTaskName(task1, "otherName");
        assertEquals("otherName", task1.getName());
        task1 = getNetworkTaskDAO().readNetworkTask(task1.getId());
        assertEquals("otherName", task1.getName());
    }

    @Test
    public void testUpdateNetworkTaskNameClearNetworkTaskLog() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.updateNetworkTaskName(task1, "otherName");
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    @Test
    public void testUpdateNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        task1.setAddress("192.168.178.1");
        data1.setPingCount(9);
        resolve1.setTargetPort(8080);
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task1.getId());
        logEntry = getLogDAO().insertAndDeleteLog(logEntry);
        getAdapter().replaceItem(new NetworkTaskUIWrapper(task1, data1, resolve1, logEntry));
        handler.updateNetworkTask(task1, data1, resolve1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        AccessTypeData readData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(task1.getId());
        assertEquals(9, readData1.getPingCount());
        assertTrue(data1.isTechnicallyEqual(readData1));
        Resolve readResolve1 = getResolveDAO().readResolveForNetworkTask(task1.getId());
        assertEquals(8080, readResolve1.getTargetPort());
        assertTrue(resolve1.isTechnicallyEqual(readResolve1));
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertTrue(logEntry.isEqual(adapterWrapper1.getLogEntry()));
        assertEquals("192.168.178.1", adapterWrapper1.getNetworkTask().getAddress());
        assertEquals(9, adapterWrapper1.getAccessTypeData().getPingCount());
        assertEquals(8080, adapterWrapper1.getResolve().getTargetPort());
        assertTrue(task1.isEqual(adapterWrapper1.getNetworkTask()));
        assertTrue(data1.isEqual(adapterWrapper1.getAccessTypeData()));
        assertTrue(resolve1.isEqual(adapterWrapper1.getResolve()));
        assertFalse(task1.isRunning());
    }

    @Test
    public void testUpdateNetworkTaskWithoutAccessTypeDataAndResolve() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        task1.setAddress("192.168.178.1");
        data1.setPingCount(9);
        resolve1.setTargetPort(8080);
        getAdapter().replaceNetworkTask(task1, null, null);
        handler.updateNetworkTask(task1, null, null);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        AccessTypeData readData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(task1.getId());
        assertEquals(10, readData1.getPingCount());
        assertFalse(data1.isTechnicallyEqual(readData1));
        Resolve readResolve1 = getResolveDAO().readResolveForNetworkTask(task1.getId());
        assertEquals(22, readResolve1.getTargetPort());
        assertFalse(resolve1.isTechnicallyEqual(readResolve1));
    }

    @Test
    public void testUpdateEmptyResolve() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        handler.updateNetworkTask(task1, data1, new Resolve());
        assertNull(getResolveDAO().readResolveForNetworkTask(task1.getId()));
        assertTrue(getResolveDAO().readAllResolve().isEmpty());
    }

    @Test
    public void testUpdateNetworkTaskClearNetworkTaskLog() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        task1.setAddress("192.168.178.1");
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.updateNetworkTask(task1, data1, resolve1);
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    @Test
    public void testDeleteNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData2();
        Resolve resolve2 = getResolve2();
        handler.insertNetworkTask(task2, data2, resolve2);
        NetworkTask task3 = getNetworkTask3();
        AccessTypeData data3 = getAccessTypeData3();
        Resolve resolve3 = getResolve3();
        handler.insertNetworkTask(task3, data3, resolve3);
        NetworkTask task4 = getNetworkTask4();
        AccessTypeData data4 = getAccessTypeData4();
        Resolve resolve4 = getResolve4();
        handler.insertNetworkTask(task4, data4, resolve4);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        final NetworkTask taskToDelete = tasks.get(1);
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task2.getId());
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        assertEquals(4, getAdapter().getItemCount());
        handler.startNetworkTask(taskToDelete);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> handler.deleteNetworkTask(taskToDelete));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        assertFalse(taskToDelete.isRunning());
        assertNull(getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(taskToDelete.getId()));
        assertNull(getResolveDAO().readResolveForNetworkTask(taskToDelete.getId()));
        List<LogEntry> allEntries = getLogDAO().readAllLogsForNetworkTask(taskToDelete.getId());
        assertTrue(allEntries.isEmpty());
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(3, tasks.size());
        task1 = tasks.get(0);
        task3 = tasks.get(1);
        task4 = tasks.get(2);
        assertEquals(0, task1.getIndex());
        assertEquals(1, task3.getIndex());
        assertEquals(2, task4.getIndex());
        assertEquals(3, getAdapter().getItemCount());
    }

    @Test
    public void testMoveNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null);
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData2();
        handler.insertNetworkTask(task2, data2, null);
        NetworkTask task3 = getNetworkTask3();
        AccessTypeData data3 = getAccessTypeData3();
        handler.insertNetworkTask(task3, data3, null);
        handler.moveNetworkTask(task3.getIndex(), task1.getIndex());
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        NetworkTask readTask1 = tasks.get(0);
        NetworkTask readTask2 = tasks.get(1);
        NetworkTask readTask3 = tasks.get(2);
        assertTrue(task1.isTechnicallyEqual(readTask3));
        assertTrue(task2.isTechnicallyEqual(readTask2));
        assertTrue(task3.isTechnicallyEqual(readTask1));
        assertEquals(0, readTask1.getIndex());
        assertEquals(1, readTask2.getIndex());
        assertEquals(2, readTask3.getIndex());
        assertTrue(task1.isTechnicallyEqual(getAdapter().getItem(2).getNetworkTask()));
        assertTrue(task2.isTechnicallyEqual(getAdapter().getItem(1).getNetworkTask()));
        assertTrue(task3.isTechnicallyEqual(getAdapter().getItem(0).getNetworkTask()));
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
        assertEquals(1, getAdapter().getItem(1).getNetworkTask().getIndex());
        assertEquals(2, getAdapter().getItem(2).getNetworkTask().getIndex());
        handler.moveNetworkTask(readTask2.getIndex(), readTask3.getIndex());
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        NetworkTask newReadTask1 = tasks.get(0);
        NetworkTask newReadTask2 = tasks.get(1);
        NetworkTask newReadTask3 = tasks.get(2);
        assertTrue(readTask1.isTechnicallyEqual(newReadTask1));
        assertTrue(readTask2.isTechnicallyEqual(newReadTask3));
        assertTrue(readTask3.isTechnicallyEqual(newReadTask2));
        assertTrue(readTask1.isTechnicallyEqual(getAdapter().getItem(0).getNetworkTask()));
        assertTrue(readTask2.isTechnicallyEqual(getAdapter().getItem(2).getNetworkTask()));
        assertTrue(readTask3.isTechnicallyEqual(getAdapter().getItem(1).getNetworkTask()));
    }

    @Test
    public void testMoveNetworkTaskInvalidIndex() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null);
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData2();
        handler.insertNetworkTask(task2, data2, null);
        NetworkTask task3 = getNetworkTask3();
        AccessTypeData data3 = getAccessTypeData3();
        handler.insertNetworkTask(task3, data3, null);
        handler.moveNetworkTask(3, 1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        NetworkTask readTask1 = tasks.get(0);
        NetworkTask readTask2 = tasks.get(1);
        NetworkTask readTask3 = tasks.get(2);
        assertTrue(task1.isTechnicallyEqual(readTask1));
        assertTrue(task2.isTechnicallyEqual(readTask2));
        assertTrue(task3.isTechnicallyEqual(readTask3));
        assertTrue(task1.isTechnicallyEqual(getAdapter().getItem(0).getNetworkTask()));
        assertTrue(task2.isTechnicallyEqual(getAdapter().getItem(1).getNetworkTask()));
        assertTrue(task3.isTechnicallyEqual(getAdapter().getItem(2).getNetworkTask()));
        handler.moveNetworkTask(-1, 1);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        readTask1 = tasks.get(0);
        readTask2 = tasks.get(1);
        readTask3 = tasks.get(2);
        assertTrue(task1.isTechnicallyEqual(readTask1));
        assertTrue(task2.isTechnicallyEqual(readTask2));
        assertTrue(task3.isTechnicallyEqual(readTask3));
        assertTrue(task1.isTechnicallyEqual(getAdapter().getItem(0).getNetworkTask()));
        assertTrue(task2.isTechnicallyEqual(getAdapter().getItem(1).getNetworkTask()));
        assertTrue(task3.isTechnicallyEqual(getAdapter().getItem(2).getNetworkTask()));
    }

    @Test
    public void testDeleteNetworkTaskClearNetworkTaskLog() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        handler.insertNetworkTask(task1, data1, resolve1);
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.deleteNetworkTask(task1);
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(1);
        networkTask.setSchedulerId(-1);
        networkTask.setName("name");
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        networkTask.setFailureCount(1);
        networkTask.setHighPrio(true);
        return networkTask;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(2);
        networkTask.setSchedulerId(-1);
        networkTask.setName("name");
        networkTask.setInstances(0);
        networkTask.setAddress("localhost");
        networkTask.setPort(22);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(40);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        networkTask.setFailureCount(2);
        networkTask.setHighPrio(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(3);
        networkTask.setSchedulerId(-1);
        networkTask.setName("name");
        networkTask.setInstances(0);
        networkTask.setAddress("192.168.178.100");
        networkTask.setPort(8080);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(85);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        networkTask.setFailureCount(3);
        networkTask.setHighPrio(true);
        return networkTask;
    }

    private NetworkTask getNetworkTask4() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(4);
        networkTask.setSchedulerId(-1);
        networkTask.setName("name");
        networkTask.setInstances(0);
        networkTask.setAddress("192.168.178.200");
        networkTask.setPort(3389);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(100);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        networkTask.setFailureCount(4);
        networkTask.setHighPrio(false);
        return networkTask;
    }

    private AccessTypeData getAccessTypeData1() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }

    private AccessTypeData getAccessTypeData2() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(1);
        data.setPingCount(1);
        data.setPingPackageSize(123);
        data.setConnectCount(2);
        data.setStopOnSuccess(false);
        data.setIgnoreSSLError(false);
        return data;
    }

    private AccessTypeData getAccessTypeData3() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(2);
        data.setPingCount(2);
        data.setPingPackageSize(4321);
        data.setConnectCount(5);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }

    private AccessTypeData getAccessTypeData4() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(2);
        data.setPingCount(5);
        data.setPingPackageSize(12);
        data.setConnectCount(5);
        data.setStopOnSuccess(false);
        data.setIgnoreSSLError(false);
        return data;
    }

    private Resolve getResolve1() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(0);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("192.168.178.1");
        resolve.setTargetPort(22);
        return resolve;
    }

    private Resolve getResolve2() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(1);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("192.168.178.1");
        resolve.setTargetPort(443);
        return resolve;
    }

    private Resolve getResolve3() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(2);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("127.0.0.1");
        resolve.setTargetPort(-1);
        return resolve;
    }

    private Resolve getResolve4() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(2);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("127.0.0.1");
        resolve.setTargetPort(15);
        return resolve;
    }

    private LogEntry getLogEntryWithNetworkTaskId(long networkTaskId) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(networkTaskId);
        logEntry.setSuccess(false);
        logEntry.setTimestamp(1);
        logEntry.setMessage("TestMessage");
        return logEntry;
    }

    private NetworkTaskAdapter getAdapter() {
        return (NetworkTaskAdapter) ((NetworkTaskMainActivity) getActivity(activityScenario)).getAdapter();
    }
}
