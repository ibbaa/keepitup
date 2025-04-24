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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerId;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSchedulerIdGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SchedulerIdGeneratorTest {

    private NetworkTaskDAO networkTaskDAO;
    private SchedulerIdHistoryDAO schedulerIdHistoryDAO;
    private SQLiteDatabase db;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        schedulerIdHistoryDAO = new SchedulerIdHistoryDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        db = DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase();
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        schedulerIdHistoryDAO.deleteAllSchedulerIds();
        db.close();
    }

    @Test
    public void testCreateUniqueSchedulerId() {
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(TestRegistry.getContext());
        SchedulerId schedulerId1 = idGenerator.createUniqueSchedulerId(db);
        SchedulerId schedulerId2 = idGenerator.createUniqueSchedulerId(db);
        assertTrue(schedulerId1.isValid());
        assertTrue(schedulerId2.isValid());
        assertNotEquals(schedulerId1.getSchedulerId(), schedulerId2.getSchedulerId());
        assertNotEquals(SchedulerIdGenerator.ERROR_SCHEDULER_ID, schedulerId1.getSchedulerId());
        assertNotEquals(SchedulerIdGenerator.ERROR_SCHEDULER_ID, schedulerId2.getSchedulerId());
        List<SchedulerId> databaseSchedulerIds = schedulerIdHistoryDAO.readAllSchedulerIds();
        assertEquals(2, databaseSchedulerIds.size());
        assertTrue(containsSchedulerId(databaseSchedulerIds, schedulerId1.getSchedulerId()));
        assertTrue(containsSchedulerId(databaseSchedulerIds, schedulerId2.getSchedulerId()));
    }

    @Test
    public void testCreateUniqueSchedulerIdRetryCounterExpired() {
        NetworkTask task = getNetworkTask();
        task = networkTaskDAO.insertNetworkTask(task);
        TestSchedulerIdGenerator idGenerator = new TestSchedulerIdGenerator(TestRegistry.getContext(), task.getSchedulerId());
        SchedulerId schedulerId = idGenerator.createUniqueSchedulerId(db);
        assertFalse(schedulerId.isValid());
    }

    @Test
    public void testInsertHistoryLimitExceeded() throws Exception {
        SchedulerIdGenerator idGenerator = new SchedulerIdGenerator(TestRegistry.getContext());
        for (int ii = 0; ii < 100; ii++) {
            idGenerator.createUniqueSchedulerId(db);
        }
        List<SchedulerId> databaseSchedulerIds = schedulerIdHistoryDAO.readAllSchedulerIds();
        assertEquals(100, databaseSchedulerIds.size());
        Thread.sleep(10);
        SchedulerId schedulerId = idGenerator.createUniqueSchedulerId(db);
        databaseSchedulerIds = schedulerIdHistoryDAO.readAllSchedulerIds();
        assertEquals(100, databaseSchedulerIds.size());
        assertTrue(containsSchedulerId(databaseSchedulerIds, schedulerId.getSchedulerId()));
    }

    private boolean containsSchedulerId(List<SchedulerId> schedulerIds, int schedulerId) {
        for (SchedulerId currentId : schedulerIds) {
            if (currentId.getSchedulerId() == schedulerId) {
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
        task.setHighPrio(true);
        return task;
    }
}
