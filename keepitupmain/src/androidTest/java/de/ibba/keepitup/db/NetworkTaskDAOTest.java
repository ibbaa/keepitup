package de.ibba.keepitup.db;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskDAOTest {

    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
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
        assertAreEqual(insertedTask1, readTask);
        readTask = networkTaskDAO.readNetworkTask(readTask.getId());
        assertAreEqual(insertedTask1, readTask);
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
        assertAreEqual(insertedTask1, readTask1);
        assertAreEqual(insertedTask2, readTask2);
        assertAreEqual(insertedTask3, readTask3);
        assertEquals(1, readTask1.getIndex());
        assertEquals(10, readTask2.getIndex());
        assertEquals(5, readTask3.getIndex());
        readTask1 = networkTaskDAO.readNetworkTask(readTask1.getId());
        readTask2 = networkTaskDAO.readNetworkTask(readTask2.getId());
        readTask3 = networkTaskDAO.readNetworkTask(readTask3.getId());
        assertAreEqual(insertedTask1, readTask1);
        assertAreEqual(insertedTask2, readTask2);
        assertAreEqual(insertedTask3, readTask3);
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
        assertFalse(readTask1.isRunning());
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
        assertAreEqual(insertedTask1, readTask1);
        readTask1 = networkTaskDAO.readNetworkTask(readTask1.getId());
        assertEquals(task2.getAccessType(), readTask1.getAccessType());
        assertEquals(task2.getAddress(), readTask1.getAddress());
        assertEquals(task2.getPort(), readTask1.getPort());
        assertEquals(task2.getInterval(), readTask1.getInterval());
        assertEquals(task2.isOnlyWifi(), readTask1.isOnlyWifi());
        assertEquals(task2.isNotification(), readTask1.isNotification());
        assertEquals(insertedTask1.getIndex(), readTask1.getIndex());
        assertEquals(insertedTask1.isRunning(), readTask1.isRunning());
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

    private NetworkTask getNetworkTask1() {
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

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(10);
        task.setSchedulerId(0);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(null);
        task.setInterval(1);
        task.setOnlyWifi(true);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(5);
        task.setSchedulerId(0);
        task.setAddress(null);
        task.setPort(456);
        task.setAccessType(AccessType.PING);
        task.setInterval(200);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }

    private void assertAreEqual(NetworkTask task1, NetworkTask task2) {
        assertEquals(task1.getId(), task2.getId());
        assertEquals(task1.getIndex(), task2.getIndex());
        assertEquals(task1.getSchedulerId(), task2.getSchedulerId());
        assertEquals(task1.getAccessType(), task2.getAccessType());
        assertEquals(task1.getAddress(), task2.getAddress());
        assertEquals(task1.getPort(), task2.getPort());
        assertEquals(task1.getInterval(), task2.getInterval());
        assertEquals(task1.isOnlyWifi(), task2.isOnlyWifi());
        assertEquals(task1.isNotification(), task2.isNotification());
        assertEquals(task1.isRunning(), task2.isRunning());
    }
}
