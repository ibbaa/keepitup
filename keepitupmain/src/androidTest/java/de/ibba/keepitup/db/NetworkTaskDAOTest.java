package de.ibba.keepitup.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskDAOTest {

    private NetworkTaskDAO dao;

    @Before
    public void beforeEachTestMethod() {
        dao = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteAllNetworkTasks();
    }

    @AfterClass
    public static void afterAllTestMethods() {
        NetworkTaskDAO dao = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteAllNetworkTasks();
    }

    @Test
    public void testInsertReadDelete() {
        assertEquals(0, dao.readMaximumIndex());
        NetworkTask insertedTask1 = getNetworkTask1();
        dao.insertNetworkTask(insertedTask1);
        List<NetworkTask> readTasks = dao.readAllNetworkTasks();
        assertEquals(1, readTasks.size());
        NetworkTask readTask = readTasks.get(0);
        assertAreEqual(insertedTask1, readTask);
        assertEquals(1, dao.readMaximumIndex());
        readTask = dao.readNetworkTask(readTask.getId());
        assertAreEqual(insertedTask1, readTask);
        assertEquals(1, readTask.getIndex());
        NetworkTask insertedTask2 = getNetworkTask2();
        NetworkTask insertedTask3 = getNetworkTask3();
        dao.insertNetworkTask(insertedTask2);
        dao.insertNetworkTask(insertedTask3);
        readTasks = dao.readAllNetworkTasks();
        assertEquals(3, readTasks.size());
        NetworkTask readTask1 = readTasks.get(0);
        NetworkTask readTask2 = readTasks.get(1);
        NetworkTask readTask3 = readTasks.get(2);
        assertAreEqual(insertedTask1, readTask1);
        assertAreEqual(insertedTask2, readTask2);
        assertAreEqual(insertedTask3, readTask3);
        assertEquals(1, readTask1.getIndex());
        assertEquals(2, readTask2.getIndex());
        assertEquals(3, readTask3.getIndex());
        assertEquals(3, dao.readMaximumIndex());
        readTask1 = dao.readNetworkTask(readTask1.getId());
        readTask2 = dao.readNetworkTask(readTask2.getId());
        readTask3 = dao.readNetworkTask(readTask3.getId());
        assertAreEqual(insertedTask1, readTask1);
        assertAreEqual(insertedTask2, readTask2);
        assertAreEqual(insertedTask3, readTask3);
        dao.deleteNetworkTask(readTask2.getId());
        readTask2 = dao.readNetworkTask(readTask2.getId());
        assertNull(readTask2);
    }

    @Test
    public void testUpdate() {
        NetworkTask insertedTask1 = getNetworkTask1();
        NetworkTask insertedTask2 = getNetworkTask2();
        NetworkTask insertedTask3 = getNetworkTask3();
        dao.insertNetworkTask(insertedTask1);
        dao.insertNetworkTask(insertedTask2);
        dao.insertNetworkTask(insertedTask3);
        List<NetworkTask> readTasks = dao.readAllNetworkTasks();
        NetworkTask readTask1 = readTasks.get(0);
        NetworkTask readTask2 = readTasks.get(1);
        NetworkTask readTask3 = readTasks.get(2);
        dao.updateNetworkTaskSuccess(readTask2.getId(), true, 987, "TestMessage2");
        dao.updateNetworkTaskNotification(readTask3.getId(), true);
        readTask2 = dao.readNetworkTask(readTask2.getId());
        readTask3 = dao.readNetworkTask(readTask3.getId());
        assertAreEqual(insertedTask1, readTask1);
        assertEquals(insertedTask2.getAddress(), readTask2.getAddress());
        assertEquals(insertedTask2.getPort(), readTask2.getPort());
        assertEquals(insertedTask2.getInterval(), readTask2.getInterval());
        assertEquals(insertedTask2.isNotification(), readTask2.isNotification());
        assertTrue(readTask2.isSuccess());
        assertEquals(987, readTask2.getTimestamp());
        assertEquals("TestMessage2", readTask2.getMessage());
        assertEquals(insertedTask3.getAddress(), readTask3.getAddress());
        assertEquals(insertedTask3.getPort(), readTask3.getPort());
        assertEquals(insertedTask3.getInterval(), readTask3.getInterval());
        assertEquals(insertedTask3.isSuccess(), readTask3.isSuccess());
        assertEquals(insertedTask3.getMessage(), readTask3.getMessage());
        assertTrue(readTask3.isNotification());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask insertedTask1 = new NetworkTask();
        insertedTask1.setId(0);
        insertedTask1.setAddress("127.0.0.1");
        insertedTask1.setPort(80);
        insertedTask1.setAccessType(AccessType.PING);
        insertedTask1.setInterval(15);
        insertedTask1.setSuccess(true);
        insertedTask1.setTimestamp(789);
        insertedTask1.setMessage("TestMessage1");
        insertedTask1.setNotification(true);
        return insertedTask1;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask insertedTask2 = new NetworkTask();
        insertedTask2.setId(0);
        insertedTask2.setAddress("host.com");
        insertedTask2.setPort(21);
        insertedTask2.setAccessType(null);
        insertedTask2.setInterval(1);
        insertedTask2.setSuccess(false);
        insertedTask2.setTimestamp(456);
        insertedTask2.setMessage(null);
        insertedTask2.setNotification(false);
        return insertedTask2;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask insertedTask3 = new NetworkTask();
        insertedTask3.setId(0);
        insertedTask3.setAddress(null);
        insertedTask3.setPort(456);
        insertedTask3.setAccessType(AccessType.PING);
        insertedTask3.setInterval(200);
        insertedTask3.setSuccess(true);
        insertedTask3.setTimestamp(123);
        insertedTask3.setMessage("TestMessage3");
        insertedTask3.setNotification(false);
        return insertedTask3;
    }

    private void assertAreEqual(NetworkTask task1, NetworkTask task2) {
        assertEquals(task1.getAddress(), task2.getAddress());
        assertEquals(task1.getPort(), task2.getPort());
        assertEquals(task1.getInterval(), task2.getInterval());
        assertEquals(task1.isSuccess(), task2.isSuccess());
        assertEquals(task1.getTimestamp(), task2.getTimestamp());
        assertEquals(task1.getMessage(), task2.getMessage());
        assertEquals(task1.isNotification(), task2.isNotification());
    }
}
