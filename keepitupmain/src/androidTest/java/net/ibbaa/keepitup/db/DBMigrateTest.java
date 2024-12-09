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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.database.sqlite.SQLiteException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBMigrateTest {

    private DBSetup setup;
    private DBMigrate migrate;
    private NetworkTaskDAO networkTaskDAO;
    private IntervalDAO intervalDAO;
    private SchedulerStateDAO schedulerStateDAO;
    private AccessTypeDataDAO accessTypeDataDAO;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        setup = new DBSetup(TestRegistry.getContext());
        migrate = new DBMigrate(setup);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        schedulerStateDAO = new SchedulerStateDAO(TestRegistry.getContext());
        accessTypeDataDAO = new AccessTypeDataDAO(TestRegistry.getContext());
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        setup.dropTables();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
        setup.dropTables();
    }

    @Test
    public void testUpgradeFrom1To2() {
        setup.createTables();
        setup.dropIntervalTable();
        migrate.doUpgrade(TestRegistry.getContext(), 1, 2);
        intervalDAO.insertInterval(new Interval());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        assertNotNull(schedulerStateDAO.readSchedulerState());
    }

    @Test(expected = SQLiteException.class)
    public void testDowngradeFrom2To1() {
        setup.createTables();
        migrate.doDowngrade(TestRegistry.getContext(), 2, 1);
        intervalDAO.readAllIntervals();
    }

    @Test(expected = SQLiteException.class)
    public void testDowngradeFrom2To1SchedulerState() {
        setup.createTables();
        migrate.doDowngrade(TestRegistry.getContext(), 2, 1);
        schedulerStateDAO.readSchedulerState();
    }

    @Test
    public void testUpgradeFrom2To3() {
        setup.createTables();
        setup.dropAccessTypeDataTable();
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        preferenceManager.setPreferencePingCount(1);
        preferenceManager.setPreferenceConnectCount(3);
        migrate.doUpgrade(TestRegistry.getContext(), 2, 3);
        assertEquals(3, accessTypeDataDAO.readAllAccessTypeData().size());
        AccessTypeData data1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task1.getId());
        AccessTypeData data2 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task2.getId());
        AccessTypeData data3 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task3.getId());
        AccessTypeData data = new AccessTypeData(TestRegistry.getContext());
        data.setNetworkTaskId(task1.getId());
        assertTrue(data.isTechnicallyEqual(data1));
        data.setNetworkTaskId(task2.getId());
        assertTrue(data.isTechnicallyEqual(data2));
        data.setNetworkTaskId(task3.getId());
        assertTrue(data.isTechnicallyEqual(data3));
    }

    @Test
    public void testUpgradeFrom2To3FailureCountColumn() {
        setup.createTables();
        setup.dropNetworkTaskTable();
        NetworkTaskDBConstants dbConstants = new NetworkTaskDBConstants(TestRegistry.getContext());
        DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase().execSQL(dbConstants.getCreateTableStatementWithoutFailureCount());
        migrate.doUpgrade(TestRegistry.getContext(), 2, 3);
        networkTaskDAO.insertNetworkTask(new NetworkTask());
        assertEquals(1, networkTaskDAO.readAllNetworkTasks().size());
    }

    @Test(expected = SQLiteException.class)
    public void testDowngradeFrom3To2() {
        setup.createTables();
        migrate.doDowngrade(TestRegistry.getContext(), 3, 2);
        accessTypeDataDAO.readAllAccessTypeData();
    }

    @Test
    public void testUpgradeFrom0To3() {
        setup.createTables();
        setup.dropIntervalTable();
        setup.dropAccessTypeDataTable();
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        migrate.doUpgrade(TestRegistry.getContext(), 0, 3);
        intervalDAO.insertInterval(new Interval());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        AccessTypeData data1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task1.getId());
        AccessTypeData data = new AccessTypeData(TestRegistry.getContext());
        data.setNetworkTaskId(task1.getId());
        assertTrue(data.isTechnicallyEqual(data1));
    }

    @Test
    public void testUpgradeFrom3To4StopOnSuccessColumn() {
        setup.createTables();
        setup.dropAccessTypeDataTable();
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(TestRegistry.getContext());
        DBOpenHelper.getInstance(TestRegistry.getContext()).getWritableDatabase().execSQL(dbConstants.getCreateTableStatementWithoutStopOnSuccess());
        migrate.doUpgrade(TestRegistry.getContext(), 3, 4);
        accessTypeDataDAO.insertAccessTypeData(new AccessTypeData());
        assertEquals(1, accessTypeDataDAO.readAllAccessTypeData().size());
    }

    @Test
    public void testUpgradeFrom0To4() {
        setup.createTables();
        setup.dropIntervalTable();
        setup.dropAccessTypeDataTable();
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        migrate.doUpgrade(TestRegistry.getContext(), 0, 4);
        intervalDAO.insertInterval(new Interval());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        assertNotNull(schedulerStateDAO.readSchedulerState());
        AccessTypeData data1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(task1.getId());
        AccessTypeData data = new AccessTypeData(TestRegistry.getContext());
        data.setNetworkTaskId(task1.getId());
        assertTrue(data.isTechnicallyEqual(data1));
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
        task.setFailureCount(2);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(2);
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
        task.setFailureCount(1);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(3);
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
        task.setFailureCount(0);
        return task;
    }
}
