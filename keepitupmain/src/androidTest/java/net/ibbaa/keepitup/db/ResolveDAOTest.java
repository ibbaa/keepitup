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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class ResolveDAOTest {

    private ResolveDAO resolveDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        resolveDAO = new ResolveDAO(TestRegistry.getContext());
        resolveDAO.deleteAllResolve();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @After
    public void afterEachTestMethod() {
        resolveDAO.deleteAllResolve();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testInsertReadDelete() {
        Resolve resolve1 = getResolve1();
        resolve1 = resolveDAO.insertResolve(resolve1);
        List<Resolve> readResolveList = resolveDAO.readAllResolve();
        assertEquals(1, readResolveList.size());
        Resolve readResolve = readResolveList.get(0);
        assertTrue(readResolve.getId() > 0);
        assertTrue(resolve1.isEqual(readResolve));
        readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertTrue(resolve1.isEqual(readResolveList.get(0)));
        Resolve resolve2 = getResolve2();
        resolveDAO.insertResolve(resolve2);
        readResolveList = resolveDAO.readAllResolve();
        assertEquals(2, readResolveList.size());
        Resolve readResolve1 = readResolveList.get(0);
        Resolve readResolve2 = readResolveList.get(1);
        assertTrue(readResolve1.getId() > 0);
        assertTrue(readResolve2.getId() > 0);
        assertTrue(resolve1.isEqual(readResolve1));
        assertTrue(resolve2.isEqual(readResolve2));
        resolveDAO.deleteAllResolveForNetworkTask(1);
        readResolveList = resolveDAO.readAllResolveForNetworkTask(1);
        assertTrue(readResolveList.isEmpty());
        readResolveList = resolveDAO.readAllResolve();
        assertEquals(1, readResolveList.size());
        readResolve1 = readResolveList.get(0);
        assertTrue(resolve1.isTechnicallyEqual(readResolve1));
        readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertEquals(1, readResolveList.size());
        readResolve1 = readResolveList.get(0);
        assertTrue(resolve1.isTechnicallyEqual(readResolve1));
        resolveDAO.deleteAllResolve();
        assertTrue(resolveDAO.readAllResolve().isEmpty());
    }

    @Test
    public void testDeleteIndexUpdate() {
        Resolve resolve1 = getResolve1();
        Resolve resolve2 = getResolve2();
        Resolve resolve3 = getResolve3();
        resolve1.setNetworkTaskId(1);
        resolve2.setNetworkTaskId(1);
        resolve3.setNetworkTaskId(1);
        resolve1 = resolveDAO.insertResolve(resolve1);
        resolve2 = resolveDAO.insertResolve(resolve2);
        resolve3 = resolveDAO.insertResolve(resolve3);
        List<Resolve> readResolveList = resolveDAO.readAllResolveForNetworkTask(1);
        assertTrue(resolve1.isTechnicallyEqual(readResolveList.get(0)));
        assertTrue(resolve2.isTechnicallyEqual(readResolveList.get(1)));
        assertTrue(resolve3.isTechnicallyEqual(readResolveList.get(2)));
        readResolveList = resolveDAO.readAllResolve();
        assertTrue(resolve1.isTechnicallyEqual(readResolveList.get(0)));
        assertTrue(resolve2.isTechnicallyEqual(readResolveList.get(1)));
        assertTrue(resolve3.isTechnicallyEqual(readResolveList.get(2)));
        resolveDAO.deleteResolve(resolve2);
        readResolveList = resolveDAO.readAllResolveForNetworkTask(1);
        assertEquals(2, readResolveList.size());
        assertTrue(resolve1.isTechnicallyEqual(readResolveList.get(0)));
        assertTrue(resolve2.isTechnicallyEqual(readResolveList.get(1)));
        assertEquals(0, readResolveList.get(0).getIndex());
        assertEquals(1, readResolveList.get(1).getIndex());
        readResolveList = resolveDAO.readAllResolve();
        assertEquals(2, readResolveList.size());
        assertTrue(resolve1.isTechnicallyEqual(readResolveList.get(0)));
        assertTrue(resolve2.isTechnicallyEqual(readResolveList.get(1)));
        assertEquals(0, readResolveList.get(0).getIndex());
        assertEquals(1, readResolveList.get(1).getIndex());
    }

    @Test
    public void testReadAllResolveForNetworkTasks() {
        Resolve resolve1 = getResolve1();
        resolve1 = resolveDAO.insertResolve(resolve1);
        Resolve resolve2 = getResolve2();
        resolve2 = resolveDAO.insertResolve(resolve2);
        Resolve resolve3 = getResolve1();
        resolve3.setNetworkTaskId(2);
        resolve3 = resolveDAO.insertResolve(resolve3);
        Resolve resolve4 = getResolve2();
        resolve4.setNetworkTaskId(3);
        resolve4 = resolveDAO.insertResolve(resolve4);
        Map<Long, Resolve> result = resolveDAO.readAllResolveForNetworkTasks();
        assertEquals(4, result.size());
        assertTrue(resolve1.isTechnicallyEqual(result.get(0L)));
        assertTrue(resolve2.isTechnicallyEqual(result.get(1L)));
        assertTrue(resolve3.isTechnicallyEqual(result.get(2L)));
        assertTrue(resolve4.isTechnicallyEqual(result.get(3L)));
    }

    @Test
    public void testUpdate() {
        Resolve resolve1 = getResolve1();
        Resolve resolve2 = getResolve2();
        resolveDAO.insertResolve(resolve1);
        resolveDAO.insertResolve(resolve2);
        Resolve readResolve1 = resolveDAO.readAllResolveForNetworkTask(0).get(0);
        Resolve readResolve2 = resolveDAO.readAllResolveForNetworkTask(1).get(0);
        readResolve2.setTargetAddress("192.168.178.25");
        readResolve2.setTargetPort(443);
        resolveDAO.updateResolve(readResolve2);
        readResolve2 = resolveDAO.readAllResolveForNetworkTask(1).get(0);
        assertEquals("192.168.178.25", readResolve2.getTargetAddress());
        assertEquals(443, readResolve2.getTargetPort());
        readResolve2.setTargetAddress(resolve2.getTargetAddress());
        readResolve2.setTargetPort(resolve2.getTargetPort());
        assertTrue(resolve2.isEqual(readResolve2));
        readResolve1.setSourceAddress("192.168.188.25");
        readResolve1.setSourcePort(12);
        resolveDAO.updateResolve(readResolve1);
        readResolve1 = resolveDAO.readAllResolveForNetworkTask(0).get(0);
        assertEquals("192.168.188.25", readResolve1.getSourceAddress());
        assertEquals(12, readResolve1.getSourcePort());
    }

    @Test
    public void testNormalizeUIIndex() {
        assertFalse(resolveDAO.normalizeUIIndex());
        assertTrue(resolveDAO.readAllResolve().isEmpty());
        Resolve insertedResolve1 = getResolve1();
        insertedResolve1.setNetworkTaskId(0);
        insertedResolve1.setIndex(0);
        insertedResolve1 = resolveDAO.insertResolve(insertedResolve1);
        assertFalse(resolveDAO.normalizeUIIndex());
        List<Resolve> readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertEquals(0, readResolveList.get(0).getIndex());
        assertTrue(insertedResolve1.isEqual(readResolveList.get(0)));
        resolveDAO.deleteAllResolveForNetworkTask(0);
        insertedResolve1 = getResolve1();
        Resolve insertedResolve2 = getResolve2();
        Resolve insertedResolve3 = getResolve3();
        insertedResolve1.setNetworkTaskId(0);
        insertedResolve2.setNetworkTaskId(0);
        insertedResolve3.setNetworkTaskId(0);
        insertedResolve1.setIndex(0);
        insertedResolve2.setIndex(1);
        insertedResolve3.setIndex(2);
        insertedResolve1 = resolveDAO.insertResolve(insertedResolve1);
        insertedResolve2 = resolveDAO.insertResolve(insertedResolve2);
        insertedResolve3 = resolveDAO.insertResolve(insertedResolve3);
        assertFalse(resolveDAO.normalizeUIIndex());
        readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertEquals(0, readResolveList.get(0).getIndex());
        assertEquals(1, readResolveList.get(1).getIndex());
        assertEquals(2, readResolveList.get(2).getIndex());
        assertTrue(insertedResolve1.isEqual(readResolveList.get(0)));
        assertTrue(insertedResolve2.isEqual(readResolveList.get(1)));
        assertTrue(insertedResolve3.isEqual(readResolveList.get(2)));
        resolveDAO.deleteAllResolveForNetworkTask(0);
        insertedResolve1 = getResolve1();
        insertedResolve1.setNetworkTaskId(0);
        insertedResolve1.setIndex(1);
        insertedResolve1 = resolveDAO.insertResolve(insertedResolve1);
        assertTrue(resolveDAO.normalizeUIIndex());
        readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertEquals(0, readResolveList.get(0).getIndex());
        assertTrue(insertedResolve1.isTechnicallyEqual(readResolveList.get(0)));
        resolveDAO.deleteAllResolve();
        insertedResolve1 = getResolve1();
        insertedResolve2 = getResolve2();
        insertedResolve3 = getResolve3();
        insertedResolve1.setNetworkTaskId(0);
        insertedResolve2.setNetworkTaskId(0);
        insertedResolve3.setNetworkTaskId(0);
        insertedResolve1.setIndex(0);
        insertedResolve2.setIndex(8);
        insertedResolve3.setIndex(5);
        insertedResolve1 = resolveDAO.insertResolve(insertedResolve1);
        insertedResolve2 = resolveDAO.insertResolve(insertedResolve2);
        insertedResolve3 = resolveDAO.insertResolve(insertedResolve3);
        assertTrue(resolveDAO.normalizeUIIndex());
        readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertEquals(0, readResolveList.get(0).getIndex());
        assertEquals(1, readResolveList.get(1).getIndex());
        assertEquals(2, readResolveList.get(2).getIndex());
        assertTrue(insertedResolve1.isTechnicallyEqual(readResolveList.get(0)));
        assertTrue(insertedResolve3.isTechnicallyEqual(readResolveList.get(1)));
        assertTrue(insertedResolve2.isTechnicallyEqual(readResolveList.get(2)));
        resolveDAO.deleteAllResolve();
        insertedResolve1 = getResolve1();
        insertedResolve2 = getResolve2();
        insertedResolve3 = getResolve3();
        insertedResolve1.setNetworkTaskId(0);
        insertedResolve2.setNetworkTaskId(0);
        insertedResolve3.setNetworkTaskId(0);
        resolveDAO.insertResolve(insertedResolve1);
        resolveDAO.insertResolve(insertedResolve2);
        resolveDAO.insertResolve(insertedResolve3);
        resolveDAO.insertResolve(insertedResolve1);
        resolveDAO.insertResolve(insertedResolve2);
        resolveDAO.insertResolve(insertedResolve3);
        assertTrue(resolveDAO.normalizeUIIndex());
        readResolveList = resolveDAO.readAllResolveForNetworkTask(0);
        assertEquals(0, readResolveList.get(0).getIndex());
        assertEquals(1, readResolveList.get(1).getIndex());
        assertEquals(2, readResolveList.get(2).getIndex());
        assertEquals(3, readResolveList.get(3).getIndex());
        assertEquals(4, readResolveList.get(4).getIndex());
        assertEquals(5, readResolveList.get(5).getIndex());
    }

    @Test
    public void testNormalizeUIIndexMultipleNetworkTasks() {
        Resolve insertedResolve1Task0 = getResolve1();
        Resolve insertedResolve2Task0 = getResolve2();
        Resolve insertedResolve3Task0 = getResolve3();
        Resolve insertedResolve1Task1 = getResolve1();
        Resolve insertedResolve2Task1 = getResolve2();
        Resolve insertedResolve1Task2 = getResolve1();
        insertedResolve1Task0.setNetworkTaskId(0);
        insertedResolve2Task0.setNetworkTaskId(0);
        insertedResolve3Task0.setNetworkTaskId(0);
        insertedResolve1Task1.setNetworkTaskId(1);
        insertedResolve2Task1.setNetworkTaskId(1);
        insertedResolve1Task2.setNetworkTaskId(2);
        insertedResolve1Task0.setIndex(3);
        insertedResolve2Task0.setIndex(4);
        insertedResolve3Task0.setIndex(100);
        insertedResolve1Task1.setIndex(3);
        insertedResolve2Task1.setIndex(5);
        insertedResolve1Task2.setIndex(1);
        resolveDAO.insertResolve(insertedResolve1Task0);
        resolveDAO.insertResolve(insertedResolve2Task0);
        resolveDAO.insertResolve(insertedResolve3Task0);
        resolveDAO.insertResolve(insertedResolve1Task1);
        resolveDAO.insertResolve(insertedResolve2Task1);
        resolveDAO.insertResolve(insertedResolve1Task2);
        assertTrue(resolveDAO.normalizeUIIndex());
        List<Resolve> readResolveList = resolveDAO.readAllResolve();
        List<Resolve> readResolveListTask0 = resolveDAO.readAllResolveForNetworkTask(0);
        List<Resolve> readResolveListTask1 = resolveDAO.readAllResolveForNetworkTask(1);
        List<Resolve> readResolveListTask2 = resolveDAO.readAllResolveForNetworkTask(2);
        assertEquals(6, readResolveList.size());
        assertEquals(3, readResolveListTask0.size());
        assertEquals(2, readResolveListTask1.size());
        assertEquals(1, readResolveListTask2.size());
        assertEquals(0, readResolveListTask0.get(0).getIndex());
        assertEquals(1, readResolveListTask0.get(1).getIndex());
        assertEquals(2, readResolveListTask0.get(2).getIndex());
        assertEquals(0, readResolveListTask1.get(0).getIndex());
        assertEquals(1, readResolveListTask1.get(1).getIndex());
        assertEquals(0, readResolveListTask2.get(0).getIndex());
    }

    @Test
    public void testDeleteOrphan() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        Resolve resolve1 = getResolve1();
        Resolve resolve2 = getResolve2();
        resolve1.setNetworkTaskId(task.getId());
        resolve2.setNetworkTaskId(task.getId() + 1);
        resolveDAO.insertResolve(resolve1);
        resolveDAO.insertResolve(resolve2);
        List<Resolve> readResolveList = resolveDAO.readAllResolve();
        assertEquals(2, readResolveList.size());
        resolveDAO.deleteAllOrphanResolve();
        readResolveList = resolveDAO.readAllResolve();
        assertEquals(1, readResolveList.size());
        assertTrue(resolve1.isTechnicallyEqual(readResolveList.get(0)));
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setName("name");
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        task.setFailureCount(1);
        task.setHighPrio(true);
        return task;
    }

    private Resolve getResolve1() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setIndex(0);
        resolve.setNetworkTaskId(0);
        resolve.setSourceAddress("127.0.0.1");
        resolve.setSourcePort(443);
        resolve.setTargetAddress("192.168.178.1");
        resolve.setTargetPort(22);
        return resolve;
    }

    private Resolve getResolve2() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setIndex(1);
        resolve.setNetworkTaskId(1);
        resolve.setSourceAddress("localhost");
        resolve.setSourcePort(443);
        resolve.setTargetAddress("127.0.0.1");
        resolve.setTargetPort(80);
        return resolve;
    }

    private Resolve getResolve3() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setIndex(2);
        resolve.setNetworkTaskId(2);
        resolve.setSourceAddress("localhost");
        resolve.setSourcePort(443);
        resolve.setTargetAddress("127.0.0.1");
        resolve.setTargetPort(80);
        return resolve;
    }
}
