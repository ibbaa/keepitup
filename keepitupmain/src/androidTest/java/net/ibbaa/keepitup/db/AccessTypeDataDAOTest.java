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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class AccessTypeDataDAOTest {

    private AccessTypeDataDAO accessTypeDataDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        accessTypeDataDAO = new AccessTypeDataDAO(TestRegistry.getContext());
        accessTypeDataDAO.deleteAllAccessTypeData();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @After
    public void afterEachTestMethod() {
        accessTypeDataDAO.deleteAllAccessTypeData();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testInsertReadDelete() {
        AccessTypeData data1 = getAccessTypeData1();
        accessTypeDataDAO.insertAccessTypeData(data1);
        List<AccessTypeData> readDataList = accessTypeDataDAO.readAllAccessTypeData();
        assertEquals(1, readDataList.size());
        AccessTypeData readData = readDataList.get(0);
        assertTrue(readData.getId() > 0);
        assertTrue(data1.isEqual(readData));
        readData = accessTypeDataDAO.readAccessTypeDataForNetworkTask(0);
        assertTrue(data1.isEqual(readData));
        AccessTypeData data2 = getAccessTypeData2();
        AccessTypeData data3 = getAccessTypeData3();
        accessTypeDataDAO.insertAccessTypeData(data2);
        accessTypeDataDAO.insertAccessTypeData(data3);
        readDataList = accessTypeDataDAO.readAllAccessTypeData();
        assertEquals(3, readDataList.size());
        AccessTypeData readData1 = readDataList.get(0);
        AccessTypeData readData2 = readDataList.get(1);
        AccessTypeData readData3 = readDataList.get(2);
        assertTrue(readData1.getId() > 0);
        assertTrue(readData2.getId() > 0);
        assertTrue(readData3.getId() > 0);
        assertTrue(doesAccessTypeDataListContain(readDataList, data1));
        assertTrue(doesAccessTypeDataListContain(readDataList, data2));
        assertTrue(doesAccessTypeDataListContain(readDataList, data3));
        accessTypeDataDAO.deleteAccessTypeDataForNetworkTask(1);
        readData = accessTypeDataDAO.readAccessTypeDataForNetworkTask(1);
        assertNull(readData);
        readDataList = accessTypeDataDAO.readAllAccessTypeData();
        assertEquals(2, readDataList.size());
        assertTrue(doesAccessTypeDataListContain(readDataList, data1));
        assertTrue(doesAccessTypeDataListContain(readDataList, data3));
        accessTypeDataDAO.deleteAllAccessTypeData();
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testUpdate() {
        AccessTypeData data1 = getAccessTypeData1();
        AccessTypeData data2 = getAccessTypeData2();
        accessTypeDataDAO.insertAccessTypeData(data1);
        accessTypeDataDAO.insertAccessTypeData(data2);
        AccessTypeData readData1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(0);
        AccessTypeData readData2 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(1);
        readData2.setPingCount(9);
        readData2.setStopOnSuccess(false);
        accessTypeDataDAO.updateAccessTypeData(readData2);
        readData2 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(1);
        assertEquals(9, readData2.getPingCount());
        assertFalse(readData2.isStopOnSuccess());
        readData2.setPingCount(data2.getPingCount());
        readData2.setStopOnSuccess(data2.isStopOnSuccess());
        assertTrue(data2.isEqual(readData2));
        readData1.setPingPackageSize(12);
        readData1.setConnectCount(1);
        accessTypeDataDAO.updateAccessTypeData(readData1);
        readData1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(0);
        assertEquals(12, readData1.getPingPackageSize());
        assertEquals(1, readData1.getConnectCount());
        readData1.setPingPackageSize(data1.getPingPackageSize());
        readData1.setConnectCount(data1.getConnectCount());
        assertTrue(data1.isTechnicallyEqual(readData1));
    }

    @Test
    public void testDeleteOrphan() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        AccessTypeData data1 = getAccessTypeData1();
        AccessTypeData data2 = getAccessTypeData2();
        AccessTypeData data3 = getAccessTypeData3();
        data1.setNetworkTaskId(task.getId());
        data2.setNetworkTaskId(task.getId() + 1);
        data3.setNetworkTaskId(task.getId() + 1);
        accessTypeDataDAO.insertAccessTypeData(data1);
        accessTypeDataDAO.insertAccessTypeData(data2);
        accessTypeDataDAO.insertAccessTypeData(data3);
        List<AccessTypeData> readDataList = accessTypeDataDAO.readAllAccessTypeData();
        assertEquals(3, readDataList.size());
        accessTypeDataDAO.deleteAllOrphanAccessTypeData();
        readDataList = accessTypeDataDAO.readAllAccessTypeData();
        assertEquals(1, readDataList.size());
        assertTrue(data1.isTechnicallyEqual(readDataList.get(0)));
    }

    private boolean doesAccessTypeDataListContain(List<AccessTypeData> dataList, AccessTypeData data) {
        for (AccessTypeData currentData : dataList) {
            if (currentData.isTechnicallyEqual(data)) {
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
        return task;
    }

    private AccessTypeData getAccessTypeData1() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        return data;
    }

    private AccessTypeData getAccessTypeData2() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(1);
        data.setPingCount(1);
        data.setPingPackageSize(123);
        data.setConnectCount(2);
        data.setStopOnSuccess(true);
        return data;
    }

    private AccessTypeData getAccessTypeData3() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(2);
        data.setPingCount(2);
        data.setPingPackageSize(4321);
        data.setConnectCount(5);
        data.setStopOnSuccess(false);
        return data;
    }
}
