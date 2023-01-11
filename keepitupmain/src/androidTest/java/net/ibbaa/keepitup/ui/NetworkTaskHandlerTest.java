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

package net.ibbaa.keepitup.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;

import net.ibbaa.keepitup.logging.ILogger;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.service.log.NetworkTaskLog;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskAdapter;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        getNetworkTaskDAO().insertNetworkTask(task);
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
    public void testInsertNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(1, tasks.size());
        assertEquals(1, getAdapter().getNextIndex());
        assertEquals(2, getAdapter().getItemCount());
        task1 = tasks.get(0);
        assertEquals(0, task1.getIndex());
        assertTrue(task1.getId() >= 0);
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertNull(adapterWrapper1.getLogEntry());
        assertTrue(task1.isEqual(adapterWrapper1.getNetworkTask()));
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(2, tasks.size());
        assertEquals(2, getAdapter().getNextIndex());
        assertEquals(3, getAdapter().getItemCount());
        task2 = tasks.get(1);
        NetworkTaskUIWrapper adapterWrapper2 = getAdapter().getItem(1);
        assertNull(adapterWrapper2.getLogEntry());
        assertTrue(task2.isEqual(adapterWrapper2.getNetworkTask()));
    }

    @Test
    public void testUpdateNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        task1.setAddress("192.168.178.1");
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task1.getId());
        logEntry = getLogDAO().insertAndDeleteLog(logEntry);
        getAdapter().replaceItem(new NetworkTaskUIWrapper(task1, logEntry));
        handler.updateNetworkTask(task1);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task1 = tasks.get(0);
        assertEquals("192.168.178.1", task1.getAddress());
        NetworkTaskUIWrapper adapterWrapper1 = getAdapter().getItem(0);
        assertTrue(logEntry.isEqual(adapterWrapper1.getLogEntry()));
        assertEquals("192.168.178.1", adapterWrapper1.getNetworkTask().getAddress());
        assertTrue(task1.isEqual(adapterWrapper1.getNetworkTask()));
        assertFalse(task1.isRunning());
        task2 = tasks.get(1);
        handler.startNetworkTask(task2);
        handler.updateNetworkTask(task2);
        assertTrue(task2.isRunning());
    }

    @Test
    public void testUpdateNetworkTaskClearNetworkTaskLog() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        task1.setAddress("192.168.178.1");
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.updateNetworkTask(task1);
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    @Test
    public void testDeleteNetworkTask() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        NetworkTask task2 = getNetworkTask2();
        handler.insertNetworkTask(task2);
        NetworkTask task3 = getNetworkTask3();
        handler.insertNetworkTask(task3);
        NetworkTask task4 = getNetworkTask4();
        handler.insertNetworkTask(task4);
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        task2 = tasks.get(1);
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task2.getId());
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        getLogDAO().insertAndDeleteLog(logEntry);
        assertEquals(5, getAdapter().getItemCount());
        handler.startNetworkTask(task2);
        handler.deleteNetworkTask(task2);
        assertFalse(task2.isRunning());
        List<LogEntry> allEntries = getLogDAO().readAllLogsForNetworkTask(task2.getId());
        assertTrue(allEntries.isEmpty());
        tasks = getNetworkTaskDAO().readAllNetworkTasks();
        assertEquals(3, tasks.size());
        task1 = tasks.get(0);
        task3 = tasks.get(1);
        task4 = tasks.get(2);
        assertEquals(0, task1.getIndex());
        assertEquals(1, task3.getIndex());
        assertEquals(2, task4.getIndex());
        assertEquals(4, getAdapter().getItemCount());
    }

    @Test
    public void testDeleteNetworkTaskClearNetworkTaskLog() {
        NetworkTask task1 = getNetworkTask1();
        handler.insertNetworkTask(task1);
        ILogger logger = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        handler.deleteNetworkTask(task1);
        ILogger logger2 = NetworkTaskLog.getLogger(TestRegistry.getContext(), task1);
        assertNotSame(logger, logger2);
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("localhost");
        networkTask.setPort(22);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(40);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("192.168.178.100");
        networkTask.setPort(8080);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(85);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask4() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("192.168.178.200");
        networkTask.setPort(3389);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(100);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        return networkTask;
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
