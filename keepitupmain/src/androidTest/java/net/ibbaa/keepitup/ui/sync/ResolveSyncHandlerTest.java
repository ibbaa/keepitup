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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ResolveSyncHandlerTest extends BaseUITest {

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getResolveDAO().deleteAllResolves();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        getResolveDAO().deleteAllResolves();
    }

    @Test
    public void testSynchronizeResolvesEmpty() {
        ResolveSyncHandler handler = new ResolveSyncHandler(TestRegistry.getContext());
        DBSyncResult syncResult = handler.synchronizeResolves(0, Collections.emptyList());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertTrue(getResolveDAO().readAllResolves().isEmpty());
    }

    @Test
    public void testSynchronizeResolvesDeleteAll() {
        getResolveDAO().insertResolve(getResolve1());
        getResolveDAO().insertResolve(getResolve2());
        ResolveSyncHandler handler = new ResolveSyncHandler(TestRegistry.getContext());
        DBSyncResult syncResult = handler.synchronizeResolves(0, Collections.emptyList());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertTrue(getResolveDAO().readAllResolvesForNetworkTask(0).isEmpty());
    }

    @Test
    public void testSynchronizeResolvesAdd() {
        ResolveSyncHandler handler = new ResolveSyncHandler(TestRegistry.getContext());
        List<Resolve> newResolves = Arrays.asList(getResolve1(), getResolve2(), getResolve3());
        DBSyncResult syncResult = handler.synchronizeResolves(0, newResolves);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Resolve> resolves = getResolveDAO().readAllResolvesForNetworkTask(0);
        assertEquals(3, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertEquals(1, resolves.get(1).getIndex());
        assertEquals(2, resolves.get(2).getIndex());
        assertTrue(getResolve1().isTechnicallyEqual(resolves.get(0)));
        assertTrue(getResolve2().isTechnicallyEqual(resolves.get(1)));
        assertTrue(getResolve3().isTechnicallyEqual(resolves.get(2)));
    }

    @Test
    @SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
    public void testSynchronizeResolvesReplace() {
        getResolveDAO().insertResolve(getResolve1());
        getResolveDAO().insertResolve(getResolve2());
        ResolveSyncHandler handler = new ResolveSyncHandler(TestRegistry.getContext());
        Resolve resolve3 = getResolve3();
        DBSyncResult syncResult = handler.synchronizeResolves(0, Collections.singletonList(resolve3));
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Resolve> resolves = getResolveDAO().readAllResolvesForNetworkTask(0);
        assertEquals(1, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertTrue(getResolve3().isTechnicallyEqual(resolves.get(0)));
    }

    @Test
    public void testSynchronizeResolvesIndex() {
        ResolveSyncHandler handler = new ResolveSyncHandler(TestRegistry.getContext());
        Resolve resolve1 = getResolve1();
        Resolve resolve2 = getResolve2();
        Resolve resolve3 = getResolve3();
        resolve1.setIndex(5);
        resolve2.setIndex(3);
        resolve3.setIndex(0);
        DBSyncResult syncResult = handler.synchronizeResolves(0, Arrays.asList(resolve1, resolve2, resolve3));
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Resolve> resolves = getResolveDAO().readAllResolvesForNetworkTask(0);
        assertEquals(3, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertEquals(1, resolves.get(1).getIndex());
        assertEquals(2, resolves.get(2).getIndex());
        assertTrue(getResolve1().isTechnicallyEqual(resolves.get(0)));
        assertTrue(getResolve2().isTechnicallyEqual(resolves.get(1)));
        assertTrue(getResolve3().isTechnicallyEqual(resolves.get(2)));
    }

    @Test
    public void testSynchronizeResolvesForNetworkTask() {
        getResolveDAO().insertResolve(getResolve1());
        getResolveDAO().insertResolve(getResolve(1, 0));
        getResolveDAO().insertResolve(getResolve(1, 1));
        ResolveSyncHandler handler = new ResolveSyncHandler(TestRegistry.getContext());
        List<Resolve> newResolves = Arrays.asList(getResolve2(), getResolve3());
        DBSyncResult syncResult = handler.synchronizeResolves(0, newResolves);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Resolve> resolvesTask0 = getResolveDAO().readAllResolvesForNetworkTask(0);
        assertEquals(2, resolvesTask0.size());
        assertEquals(0, resolvesTask0.get(0).getIndex());
        assertEquals(1, resolvesTask0.get(1).getIndex());
        assertTrue(getResolve2().isTechnicallyEqual(resolvesTask0.get(0)));
        assertTrue(getResolve3().isTechnicallyEqual(resolvesTask0.get(1)));
        List<Resolve> resolvesTask1 = getResolveDAO().readAllResolvesForNetworkTask(1);
        assertEquals(2, resolvesTask1.size());
        assertEquals(0, resolvesTask1.get(0).getIndex());
        assertEquals(1, resolvesTask1.get(1).getIndex());
    }

    private Resolve getResolve1() {
        Resolve resolve = new Resolve();
        resolve.setId(-1);
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
        resolve.setId(-1);
        resolve.setIndex(1);
        resolve.setNetworkTaskId(0);
        resolve.setSourceAddress("localhost");
        resolve.setSourcePort(80);
        resolve.setTargetAddress("10.0.0.1");
        resolve.setTargetPort(8080);
        return resolve;
    }

    private Resolve getResolve3() {
        Resolve resolve = new Resolve();
        resolve.setId(-1);
        resolve.setIndex(2);
        resolve.setNetworkTaskId(0);
        resolve.setSourceAddress("192.168.1.1");
        resolve.setSourcePort(22);
        resolve.setTargetAddress("172.16.0.1");
        resolve.setTargetPort(443);
        return resolve;
    }

    @SuppressWarnings("SameParameterValue")
    private Resolve getResolve(long networkTaskId, int index) {
        Resolve resolve = new Resolve();
        resolve.setId(-1);
        resolve.setIndex(index);
        resolve.setNetworkTaskId(networkTaskId);
        resolve.setSourceAddress("127.0.0.1");
        resolve.setSourcePort(80);
        resolve.setTargetAddress("192.168.0.1");
        resolve.setTargetPort(443);
        return resolve;
    }
}
