/*
 * Copyright (c) 2026 Alwin Ibba
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
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.model.SNMPVersion;
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
        Resolve resolve1 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve2 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header1 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header2 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task1, data1, List.of(resolve1, resolve2), List.of(header1, header2));
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
        List<Resolve> readResolves = getResolveDAO().readAllResolvesForNetworkTask(task1.getId());
        resolve1.setNetworkTaskId(task1.getId());
        resolve2.setNetworkTaskId(task1.getId());
        assertEquals(2, readResolves.size());
        assertTrue(resolve1.isTechnicallyEqual(readResolves.get(0)));
        assertTrue(resolve2.isTechnicallyEqual(readResolves.get(1)));
        List<Header> headers = getHeaderDAO().readHeadersForNetworkTask(task1.getId());
        assertEquals(2, headers.size());
        assertTrue(header1.isTechnicallyEqual(headers.get(0)));
        assertTrue(header2.isTechnicallyEqual(headers.get(1)));
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertNull(adapterWrapper1.getLogEntry());
        assertTrue(task1.isEqual(adapterWrapper1.getNetworkTask()));
        assertTrue(data1.isEqual(adapterWrapper1.getAccessTypeData()));
        assertTrue(resolve1.isEqual(adapterWrapper1.getResolves().get(0)));
        assertTrue(resolve2.isEqual(adapterWrapper1.getResolves().get(1)));
        assertTrue(header1.isTechnicallyEqual(adapterWrapper1.getHeaders().get(0)));
        assertTrue(header2.isTechnicallyEqual(adapterWrapper1.getHeaders().get(1)));
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData1();
        handler.insertNetworkTask(task2, data2, null, null);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(2, tasks.size());
        assertEquals(2, getAdapter().getNextIndex());
        assertEquals(2, getAdapter().getItemCount());
    }

    @Test
    public void testInsertEmptyResolvesAndHeaders() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null, null);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(1, getAdapter().getItemCount());
        assertTrue(getResolveDAO().readAllResolvesForNetworkTask(task1.getId()).isEmpty());
        assertTrue(getHeaderDAO().readHeadersForNetworkTask(task1.getId()).isEmpty());
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getAccessTypeDataDAO().deleteAllAccessTypeData();
        getResolveDAO().deleteAllResolves();
        getHeaderDAO().deleteAllHeaders();
        getAdapter().replaceItems(Collections.emptyList());
        task1 = getNetworkTask1();
        data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null, null);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(1, getAdapter().getItemCount());
        assertTrue(getResolveDAO().readAllResolvesForNetworkTask(task1.getId()).isEmpty());
        assertTrue(getHeaderDAO().readHeadersForNetworkTask(task1.getId()).isEmpty());
    }

    @Test
    public void testUpdateNetworkTaskName() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null, null);
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
        handler.insertNetworkTask(task1, data1, null, null);
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.updateNetworkTaskName(task1, "otherName");
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    @Test
    public void testUpdateNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve2 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header1 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header2 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task1, data1, List.of(resolve1, resolve2), List.of(header1, header2));
        task1.setAddress("192.168.178.1");
        data1.setPingCount(9);
        resolve1.setTargetPort(1234);
        resolve2.setSourcePort(4321);
        header1.setValue("value2");
        header2.setName("name3");
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task1.getId());
        logEntry = getLogDAO().insertAndDeleteLog(logEntry);
        getAdapter().replaceItem(new NetworkTaskUIWrapper(task1, data1, List.of(resolve1, resolve2), List.of(header1, header2), logEntry));
        handler.updateNetworkTask(task1, data1, List.of(resolve1, resolve2), List.of(header1, header2));
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        AccessTypeData readData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(task1.getId());
        assertEquals(9, readData1.getPingCount());
        assertTrue(data1.isTechnicallyEqual(readData1));
        List<Resolve> readResolves = getResolveDAO().readAllResolvesForNetworkTask(task1.getId());
        assertEquals(2, readResolves.size());
        assertEquals(1234, readResolves.get(0).getTargetPort());
        assertEquals(4321, readResolves.get(1).getSourcePort());
        assertTrue(resolve1.isTechnicallyEqual(readResolves.get(0)));
        assertTrue(resolve2.isTechnicallyEqual(readResolves.get(1)));
        List<Header> readHeaders = getHeaderDAO().readHeadersForNetworkTask(task1.getId());
        assertEquals(2, readHeaders.size());
        assertEquals("value2", readHeaders.get(0).getValue());
        assertEquals("name3", readHeaders.get(1).getName());
        assertTrue(header1.isTechnicallyEqual(readHeaders.get(0)));
        assertTrue(header2.isTechnicallyEqual(readHeaders.get(1)));
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertTrue(logEntry.isEqual(adapterWrapper1.getLogEntry()));
        assertEquals("192.168.178.1", adapterWrapper1.getNetworkTask().getAddress());
        assertEquals(9, adapterWrapper1.getAccessTypeData().getPingCount());
        assertEquals(1234, adapterWrapper1.getResolves().get(0).getTargetPort());
        assertEquals(4321, adapterWrapper1.getResolves().get(1).getSourcePort());
        assertEquals("value2", adapterWrapper1.getHeaders().get(0).getValue());
        assertEquals("name3", adapterWrapper1.getHeaders().get(1).getName());
        assertTrue(task1.isEqual(adapterWrapper1.getNetworkTask()));
        assertTrue(data1.isEqual(adapterWrapper1.getAccessTypeData()));
        assertTrue(resolve1.isEqual(adapterWrapper1.getResolves().get(0)));
        assertTrue(resolve2.isEqual(adapterWrapper1.getResolves().get(1)));
        assertTrue(header1.isEqual(adapterWrapper1.getHeaders().get(0)));
        assertTrue(header2.isEqual(adapterWrapper1.getHeaders().get(1)));
        assertFalse(task1.isRunning());
    }

    @Test
    public void testUpdateNetworkTaskWithoutAccessTypeDataResolveAndHeaders() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve2 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header1 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header2 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task1, data1, List.of(resolve1, resolve2), List.of(header1, header2));
        task1.setAddress("192.168.178.1");
        data1.setPingCount(9);
        resolve1.setTargetPort(8080);
        resolve2.setTargetPort(8080);
        header1.setValue("value2");
        header2.setName("name3");
        getAdapter().replaceNetworkTask(task1, null, null, null, null);
        handler.updateNetworkTask(task1, null, null, null);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        AccessTypeData readData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(task1.getId());
        assertEquals(10, readData1.getPingCount());
        assertFalse(data1.isTechnicallyEqual(readData1));
        List<Resolve> readResolves = getResolveDAO().readAllResolvesForNetworkTask(task1.getId());
        assertEquals(22, readResolves.get(0).getTargetPort());
        assertEquals(22, readResolves.get(1).getTargetPort());
        assertFalse(resolve1.isTechnicallyEqual(readResolves.get(0)));
        assertFalse(resolve2.isTechnicallyEqual(readResolves.get(1)));
        List<Header> readHeaders = getHeaderDAO().readHeadersForNetworkTask(task1.getId());
        assertEquals(2, readHeaders.size());
        assertEquals("value", readHeaders.get(0).getValue());
        assertEquals("name2", readHeaders.get(1).getName());
        assertFalse(header1.isTechnicallyEqual(readHeaders.get(0)));
        assertFalse(header2.isTechnicallyEqual(readHeaders.get(1)));
    }

    @Test
    public void testUpdateEmptyResolvesAndHeaders() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve1 = getResolve1();
        Header header1 = getHeaderWithNetworkTaskId(0, "name");
        handler.insertNetworkTask(task1, data1, List.of(resolve1), List.of(header1));
        handler.updateNetworkTask(task1, data1, Collections.emptyList(), Collections.emptyList());
        assertTrue(getResolveDAO().readAllResolvesForNetworkTask(task1.getId()).isEmpty());
        assertTrue(getResolveDAO().readAllResolves().isEmpty());
        assertTrue(getHeaderDAO().readHeadersForNetworkTask(task1.getId()).isEmpty());
        assertTrue(getHeaderDAO().readAllHeaders().isEmpty());
    }

    @Test
    public void testUpdateNetworkTaskClearNetworkTaskLog() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        handler.insertNetworkTask(task1, data1, null, null);
        task1.setAddress("192.168.178.1");
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.updateNetworkTask(task1, data1, null, null);
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    @Test
    public void testDeleteNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        AccessTypeData data1 = getAccessTypeData1();
        Resolve resolve11 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve21 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header11 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header21 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task1, data1, List.of(resolve11, resolve21), List.of(header11, header21));
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData2();
        Resolve resolve12 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve22 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header12 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header22 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task2, data2, List.of(resolve12, resolve22), List.of(header12, header22));
        NetworkTask task3 = getNetworkTask3();
        AccessTypeData data3 = getAccessTypeData3();
        Resolve resolve13 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve23 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header13 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header23 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task3, data3, List.of(resolve13, resolve23), List.of(header13, header23));
        NetworkTask task4 = getNetworkTask4();
        AccessTypeData data4 = getAccessTypeData4();
        Resolve resolve14 = getResolveWithNetworkTaskId(task1.getId(), 1);
        Resolve resolve24 = getResolveWithNetworkTaskId(task1.getId(), 2);
        Header header14 = getHeaderWithNetworkTaskId(task1.getId(), "name1");
        Header header24 = getHeaderWithNetworkTaskId(task1.getId(), "name2");
        handler.insertNetworkTask(task4, data4, List.of(resolve14, resolve24), List.of(header14, header24));
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
        assertTrue(getResolveDAO().readAllResolvesForNetworkTask(taskToDelete.getId()).isEmpty());
        assertTrue(getHeaderDAO().readHeadersForNetworkTask(taskToDelete.getId()).isEmpty());
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
        handler.insertNetworkTask(task1, data1, null, null);
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData2();
        handler.insertNetworkTask(task2, data2, null, null);
        NetworkTask task3 = getNetworkTask3();
        AccessTypeData data3 = getAccessTypeData3();
        handler.insertNetworkTask(task3, data3, null, null);
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
        handler.insertNetworkTask(task1, data1, null, null);
        NetworkTask task2 = getNetworkTask2();
        AccessTypeData data2 = getAccessTypeData2();
        handler.insertNetworkTask(task2, data2, null, null);
        NetworkTask task3 = getNetworkTask3();
        AccessTypeData data3 = getAccessTypeData3();
        handler.insertNetworkTask(task3, data3, null, null);
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
        handler.insertNetworkTask(task1, data1, null, null);
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
        networkTask.setLastSysUpTime(0);
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
        networkTask.setLastSysUpTime(0);
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
        networkTask.setLastSysUpTime(0);
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
        networkTask.setLastSysUpTime(0);
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
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("public");
        data.setSnmpCommunityValid(true);
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
        data.setUseDefaultHeaders(true);
        data.setSnmpVersion(SNMPVersion.V2C);
        data.setSnmpCommunity(null);
        data.setSnmpCommunityValid(true);
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
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("community");
        data.setSnmpCommunityValid(true);
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
        data.setUseDefaultHeaders(true);
        data.setSnmpVersion(SNMPVersion.V2C);
        data.setSnmpCommunity("private");
        data.setSnmpCommunityValid(true);
        return data;
    }

    private Resolve getResolve1() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setIndex(0);
        resolve.setNetworkTaskId(0);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("192.168.178.1");
        resolve.setTargetPort(22);
        return resolve;
    }

    private Resolve getResolveWithNetworkTaskId(long networkTaskId, int index) {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setIndex(index);
        resolve.setNetworkTaskId(networkTaskId);
        resolve.setSourceAddress("192.168.178.2");
        resolve.setSourcePort(8080);
        resolve.setTargetAddress("192.168.178.1");
        resolve.setTargetPort(22);
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

    private Header getHeaderWithNetworkTaskId(long networkTaskId, String name) {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(networkTaskId);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName(name);
        header.setValue("value");
        header.setValueValid(true);
        return header;
    }

    private NetworkTaskAdapter getAdapter() {
        return (NetworkTaskAdapter) ((NetworkTaskMainActivity) getActivity(activityScenario)).getAdapter();
    }
}
