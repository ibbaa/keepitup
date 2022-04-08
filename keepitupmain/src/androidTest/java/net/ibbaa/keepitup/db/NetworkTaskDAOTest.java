/*
 * Copyright (c) 2022. Alwin Ibba
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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskDAOTest {

    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testInsertReadDelete() {
        NetworkTask insertedTask1 = getNetworkTask1();
        networkTaskDAO.insertNetworkTask(insertedTask1);
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        assertEquals(1, readTasks.size());
        NetworkTask readTask = readTasks.get(0);
        assertTrue(readTask.getId() > 0);
        assertTrue(insertedTask1.isEqual(readTask));
        readTask = networkTaskDAO.readNetworkTask(readTask.getId());
        assertTrue(insertedTask1.isEqual(readTask));
        NetworkTask insertedTask2 = getNetworkTask2();
        NetworkTask insertedTask3 = getNetworkTask3();
        networkTaskDAO.insertNetworkTask(insertedTask2);
        networkTaskDAO.insertNetworkTask(insertedTask3);
        readTasks = networkTaskDAO.readAllNetworkTasks();
        assertEquals(3, readTasks.size());
        NetworkTask readTask1 = readTasks.get(0);
        NetworkTask readTask3 = readTasks.get(1);
        NetworkTask readTask2 = readTasks.get(2);
        assertTrue(readTask1.getId() > 0);
        assertTrue(readTask2.getId() > 0);
        assertTrue(readTask3.getId() > 0);
        assertTrue(insertedTask1.isEqual(readTask1));
        assertTrue(insertedTask2.isEqual(readTask2));
        assertTrue(insertedTask3.isEqual(readTask3));
        assertEquals(1, readTask1.getIndex());
        assertEquals(10, readTask2.getIndex());
        assertEquals(5, readTask3.getIndex());
        readTask1 = networkTaskDAO.readNetworkTask(readTask1.getId());
        readTask2 = networkTaskDAO.readNetworkTask(readTask2.getId());
        readTask3 = networkTaskDAO.readNetworkTask(readTask3.getId());
        assertTrue(insertedTask1.isEqual(readTask1));
        assertTrue(insertedTask2.isEqual(readTask2));
        assertTrue(insertedTask3.isEqual(readTask3));
        networkTaskDAO.deleteNetworkTask(readTask2);
        readTask2 = networkTaskDAO.readNetworkTask(readTask2.getId());
        assertNull(readTask2);
    }

    @Test
    public void testInsertUniqueSchedulerId() {
        NetworkTask insertedTask1 = getNetworkTask1();
        NetworkTask insertedTask2 = getNetworkTask2();
        NetworkTask insertedTask3 = getNetworkTask3();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        insertedTask2 = networkTaskDAO.insertNetworkTask(insertedTask2);
        insertedTask3 = networkTaskDAO.insertNetworkTask(insertedTask3);
        assertNotEquals(insertedTask1.getSchedulerId(), insertedTask2.getSchedulerId());
        assertNotEquals(insertedTask1.getSchedulerId(), insertedTask3.getSchedulerId());
        assertNotEquals(insertedTask2.getSchedulerId(), insertedTask3.getSchedulerId());
    }

    @Test
    public void testInsertResetInstances() {
        NetworkTask insertedTask1 = getNetworkTask1();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        assertEquals(0, insertedTask1.getInstances());
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
    }

    @Test
    public void testInsertResetLastScheduled() {
        NetworkTask insertedTask1 = getNetworkTask1();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        assertEquals(-1, insertedTask1.getLastScheduled());
    }

    @Test
    public void testDeleteIndexCleanup() {
        NetworkTask insertedTask1 = new NetworkTask();
        NetworkTask insertedTask2 = new NetworkTask();
        NetworkTask insertedTask3 = new NetworkTask();
        NetworkTask insertedTask4 = new NetworkTask();
        NetworkTask insertedTask5 = new NetworkTask();
        NetworkTask insertedTask6 = new NetworkTask();
        insertedTask1.setIndex(1);
        insertedTask2.setIndex(2);
        insertedTask3.setIndex(3);
        insertedTask4.setIndex(4);
        insertedTask5.setIndex(5);
        insertedTask6.setIndex(6);
        networkTaskDAO.insertNetworkTask(insertedTask1);
        insertedTask2 = networkTaskDAO.insertNetworkTask(insertedTask2);
        networkTaskDAO.insertNetworkTask(insertedTask3);
        networkTaskDAO.insertNetworkTask(insertedTask4);
        networkTaskDAO.insertNetworkTask(insertedTask5);
        networkTaskDAO.insertNetworkTask(insertedTask6);
        networkTaskDAO.deleteNetworkTask(insertedTask2);
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        assertEquals(5, readTasks.size());
        NetworkTask readTask1 = readTasks.get(0);
        NetworkTask readTask2 = readTasks.get(1);
        NetworkTask readTask3 = readTasks.get(2);
        NetworkTask readTask4 = readTasks.get(3);
        NetworkTask readTask5 = readTasks.get(4);
        assertEquals(1, readTask1.getIndex());
        assertEquals(2, readTask2.getIndex());
        assertEquals(3, readTask3.getIndex());
        assertEquals(4, readTask4.getIndex());
        assertEquals(5, readTask5.getIndex());
    }

    @Test
    public void testUpdateRunning() {
        NetworkTask insertedTask1 = getNetworkTask1();
        networkTaskDAO.insertNetworkTask(insertedTask1);
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        NetworkTask readTask1 = readTasks.get(0);
        networkTaskDAO.updateNetworkTaskRunning(readTask1.getId(), false);
        readTask1 = networkTaskDAO.readNetworkTask(readTask1.getId());
        assertEquals(insertedTask1.getIndex(), readTask1.getIndex());
        assertEquals(insertedTask1.getAccessType(), readTask1.getAccessType());
        assertEquals(insertedTask1.getAddress(), readTask1.getAddress());
        assertEquals(insertedTask1.getPort(), readTask1.getPort());
        assertEquals(insertedTask1.getInterval(), readTask1.getInterval());
        assertEquals(insertedTask1.isOnlyWifi(), readTask1.isOnlyWifi());
        assertEquals(insertedTask1.isNotification(), readTask1.isNotification());
        assertEquals(insertedTask1.getSchedulerId(), readTask1.getSchedulerId());
        assertEquals(insertedTask1.getInstances(), readTask1.getInstances());
        assertFalse(readTask1.isRunning());
        assertEquals(-1, readTask1.getLastScheduled());
    }

    @Test
    public void testReadNumberNetworkTasksRunning() {
        NetworkTask insertedTask1 = getNetworkTask1();
        NetworkTask insertedTask2 = getNetworkTask2();
        NetworkTask insertedTask3 = getNetworkTask3();
        networkTaskDAO.insertNetworkTask(insertedTask1);
        networkTaskDAO.insertNetworkTask(insertedTask2);
        insertedTask3 = networkTaskDAO.insertNetworkTask(insertedTask3);
        assertEquals(1, networkTaskDAO.readNetworkTasksRunning());
        networkTaskDAO.updateNetworkTaskRunning(insertedTask3.getId(), true);
        assertEquals(2, networkTaskDAO.readNetworkTasksRunning());
    }

    @Test
    public void testUpdate() {
        NetworkTask insertedTask1 = getNetworkTask1();
        networkTaskDAO.insertNetworkTask(insertedTask1);
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        NetworkTask readTask1 = readTasks.get(0);
        NetworkTask task2 = getNetworkTask2();
        task2.setId(readTask1.getId());
        networkTaskDAO.updateNetworkTask(task2);
        assertTrue(insertedTask1.isEqual(readTask1));
        readTask1 = networkTaskDAO.readNetworkTask(readTask1.getId());
        assertEquals(task2.getAccessType(), readTask1.getAccessType());
        assertEquals(task2.getAddress(), readTask1.getAddress());
        assertEquals(task2.getPort(), readTask1.getPort());
        assertEquals(task2.getInterval(), readTask1.getInterval());
        assertEquals(task2.isOnlyWifi(), readTask1.isOnlyWifi());
        assertEquals(task2.isNotification(), readTask1.isNotification());
        assertEquals(insertedTask1.getIndex(), readTask1.getIndex());
        assertEquals(insertedTask1.isRunning(), readTask1.isRunning());
        assertEquals(-1, readTask1.getLastScheduled());
    }

    @Test
    public void testUpdateSchedulerIdChanged() {
        NetworkTask insertedTask1 = getNetworkTask1();
        networkTaskDAO.insertNetworkTask(insertedTask1);
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        NetworkTask readTask1 = readTasks.get(0);
        int schedulerId = readTask1.getSchedulerId();
        readTask1.setAddress("abc.com");
        networkTaskDAO.updateNetworkTask(readTask1);
        readTasks = networkTaskDAO.readAllNetworkTasks();
        readTask1 = readTasks.get(0);
        assertNotEquals(schedulerId, readTask1.getSchedulerId());
    }

    @Test
    public void testUpdateResetInstances() {
        NetworkTask insertedTask1 = getNetworkTask1();
        networkTaskDAO.insertNetworkTask(insertedTask1);
        List<NetworkTask> readTasks = networkTaskDAO.readAllNetworkTasks();
        NetworkTask readTask1 = readTasks.get(0);
        readTask1.setAddress("abc.com");
        readTask1.setInstances(5);
        readTask1 = networkTaskDAO.updateNetworkTask(readTask1);
        assertEquals(0, readTask1.getInstances());
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(readTask1.getId()));
    }

    @Test
    public void testIncreaseResetInstances() {
        NetworkTask insertedTask1 = getNetworkTask1();
        NetworkTask insertedTask2 = getNetworkTask2();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        insertedTask2 = networkTaskDAO.insertNetworkTask(insertedTask2);
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        assertEquals(2, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask2.getId());
        assertEquals(2, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.resetNetworkTaskInstances(insertedTask1.getId());
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(1, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.resetAllNetworkTaskInstances();
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
    }

    @Test
    public void testDecreaseInstances() {
        NetworkTask insertedTask1 = getNetworkTask1();
        NetworkTask insertedTask2 = getNetworkTask2();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        insertedTask2 = networkTaskDAO.insertNetworkTask(insertedTask2);
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.increaseNetworkTaskInstances(insertedTask1.getId());
        assertEquals(5, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask2.getId());
        assertEquals(4, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask2.getId());
        assertEquals(3, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        networkTaskDAO.decreaseNetworkTaskInstances(insertedTask1.getId());
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask1.getId()));
        assertEquals(0, networkTaskDAO.readNetworkTaskInstances(insertedTask2.getId()));
    }

    @Test
    public void testUpdateNetworkTaskLastScheduled() {
        NetworkTask insertedTask1 = getNetworkTask1();
        insertedTask1 = networkTaskDAO.insertNetworkTask(insertedTask1);
        networkTaskDAO.updateNetworkTaskLastScheduled(insertedTask1.getId(), 125);
        NetworkTask readTask1 = networkTaskDAO.readNetworkTask(insertedTask1.getId());
        assertEquals(125, readTask1.getLastScheduled());
        networkTaskDAO.resetNetworkTaskLastScheduled(readTask1.getId());
        readTask1 = networkTaskDAO.readNetworkTask(insertedTask1.getId());
        assertEquals(-1, readTask1.getLastScheduled());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(10);
        task.setSchedulerId(0);
        task.setInstances(2);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setOnlyWifi(true);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(5);
        task.setSchedulerId(0);
        task.setInstances(3);
        task.setAddress(null);
        task.setPort(456);
        task.setAccessType(AccessType.PING);
        task.setInterval(200);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        return task;
    }
}
