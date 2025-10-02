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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
        readResolve = resolveDAO.readResolveForNetworkTask(0);
        assertTrue(resolve1.isEqual(readResolve));
        Resolve resolve2 = getResolve2();
        resolveDAO.insertResolve(resolve2);
        readResolveList = resolveDAO.readAllResolve();
        assertEquals(2, readResolveList.size());
        Resolve readResolve1 = readResolveList.get(0);
        Resolve readResolve2 = readResolveList.get(1);
        assertTrue(readResolve1.getId() > 0);
        assertTrue(readResolve2.getId() > 0);
        assertTrue(doesAccessTypeDataListContain(readResolveList, resolve1));
        assertTrue(doesAccessTypeDataListContain(readResolveList, resolve2));
        resolveDAO.deleteResolveForNetworkTask(1);
        readResolve = resolveDAO.readResolveForNetworkTask(1);
        assertNull(readResolve);
        readResolveList = resolveDAO.readAllResolve();
        assertEquals(1, readResolveList.size());
        readResolve1 = readResolveList.get(0);
        assertTrue(resolve1.isTechnicallyEqual(readResolve1));
        resolveDAO.deleteAllResolve();
        assertTrue(resolveDAO.readAllResolve().isEmpty());
    }

    @Test
    public void testUpdate() {
        Resolve resolve1 = getResolve1();
        Resolve resolve2 = getResolve2();
        resolveDAO.insertResolve(resolve1);
        resolveDAO.insertResolve(resolve2);
        Resolve readResolve1 = resolveDAO.readResolveForNetworkTask(0);
        Resolve readResolve2 = resolveDAO.readResolveForNetworkTask(1);
        readResolve2.setTargetAddress("192.168.178.25");
        readResolve2.setTargetPort(443);
        resolveDAO.updateResolve(readResolve2);
        readResolve2 = resolveDAO.readResolveForNetworkTask(1);
        assertEquals("192.168.178.25", readResolve2.getTargetAddress());
        assertEquals(443, readResolve2.getTargetPort());
        readResolve2.setTargetAddress(resolve2.getTargetAddress());
        readResolve2.setTargetPort(resolve2.getTargetPort());
        assertTrue(resolve2.isEqual(readResolve2));
        readResolve1.setSourceAddress("192.168.188.25");
        readResolve1.setSourcePort(12);
        readResolve1.setTargetAddress("192.168.188.25");
        readResolve1.setTargetPort(12);
        resolveDAO.updateResolve(readResolve1);
        readResolve1 = resolveDAO.readResolveForNetworkTask(0);
        assertEquals("192.168.188.25", readResolve1.getSourceAddress());
        assertEquals(12, readResolve1.getSourcePort());
        assertEquals("192.168.188.25", readResolve1.getTargetAddress());
        assertEquals(12, readResolve1.getTargetPort());
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

    private boolean doesAccessTypeDataListContain(List<Resolve> resolveList, Resolve resolve) {
        for (Resolve currentResolve : resolveList) {
            if (currentResolve.isTechnicallyEqual(resolve)) {
                return true;
            }
        }
        return false;
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
        resolve.setNetworkTaskId(1);
        resolve.setSourceAddress("localhost");
        resolve.setSourcePort(443);
        resolve.setTargetAddress("127.0.0.1");
        resolve.setTargetPort(80);
        return resolve;
    }
}
