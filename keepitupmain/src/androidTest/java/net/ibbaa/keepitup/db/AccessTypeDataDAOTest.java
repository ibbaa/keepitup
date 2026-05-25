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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.TestAccessTypeDataDAO;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.util.StringUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class AccessTypeDataDAOTest {

    private TestAccessTypeDataDAO accessTypeDataDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        accessTypeDataDAO = new TestAccessTypeDataDAO(TestRegistry.getContext());
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
        data1 = accessTypeDataDAO.insertAccessTypeData(data1);
        List<AccessTypeData> readDataList = accessTypeDataDAO.readAllAccessTypeData();
        assertEquals(1, readDataList.size());
        AccessTypeData readData = readDataList.get(0);
        assertTrue(readData.getId() > 0);
        assertTrue(data1.isEqual(readData));
        readData = accessTypeDataDAO.readAccessTypeDataForNetworkTask(0);
        assertTrue(data1.isEqual(readData));
        AccessTypeData data2 = getAccessTypeData2();
        AccessTypeData data3 = getAccessTypeData3();
        data2 = accessTypeDataDAO.insertAccessTypeData(data2);
        data3 = accessTypeDataDAO.insertAccessTypeData(data3);
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
    public void testInsertEncrypted() {
        AccessTypeData accessTypeData1 = getAccessTypeData1();
        accessTypeData1 = accessTypeDataDAO.insertAccessTypeData(accessTypeData1);
        AccessTypeData accessTypeData2 = getAccessTypeData2();
        accessTypeData2 = accessTypeDataDAO.insertAccessTypeData(accessTypeData2);
        Map<String, String> encryptedValues1 = accessTypeDataDAO.readEncryptedCommunityAndCommunityIV(accessTypeData1.getId());
        Map<String, String> encryptedValues2 = accessTypeDataDAO.readEncryptedCommunityAndCommunityIV(accessTypeData2.getId());
        assertNotNull(encryptedValues1.get("SNMPCOMMUNITY"));
        assertNotNull(encryptedValues1.get("SNMPCOMMUNITYIV"));
        assertNotEquals("community1", encryptedValues1.get("SNMPCOMMUNITY"));
        assertNull(encryptedValues2.get("SNMPCOMMUNITY"));
        assertNull(encryptedValues2.get("SNMPCOMMUNITYIV"));
    }

    @Test
    public void testReadEncryptedKeyInvalid() {
        AccessTypeData accessTypeData1 = getAccessTypeData1();
        accessTypeDataDAO.insertAccessTypeData(accessTypeData1);
        corruptKey();
        AccessTypeData readData = accessTypeDataDAO.readAccessTypeDataForNetworkTask(0);
        assertNull(readData.getSnmpCommunity());
        assertFalse(readData.isSnmpCommunityValid());
        readData = accessTypeDataDAO.readAllAccessTypeData().get(0);
        assertNull(readData.getSnmpCommunity());
        assertFalse(readData.isSnmpCommunityValid());
    }

    @Test
    public void testReadAllAccessTypeDataForNetworkTasks() {
        AccessTypeData accessTypeData1 = getAccessTypeData1();
        accessTypeData1 = accessTypeDataDAO.insertAccessTypeData(accessTypeData1);
        AccessTypeData accessTypeData2 = getAccessTypeData2();
        accessTypeData2 = accessTypeDataDAO.insertAccessTypeData(accessTypeData2);
        AccessTypeData accessTypeData3 = getAccessTypeData3();
        accessTypeData3 = accessTypeDataDAO.insertAccessTypeData(accessTypeData3);
        Map<Long, AccessTypeData> result = accessTypeDataDAO.readAllAccessTypeDataForNetworkTasks();
        assertEquals(3, result.size());
        assertTrue(accessTypeData1.isTechnicallyEqual(result.get(0L)));
        assertTrue(accessTypeData2.isTechnicallyEqual(result.get(1L)));
        assertTrue(accessTypeData3.isTechnicallyEqual(result.get(2L)));
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
        readData2.setIgnoreSSLError(false);
        readData2.setUseDefaultHeaders(true);
        accessTypeDataDAO.updateAccessTypeData(readData2);
        readData2 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(1);
        assertEquals(9, readData2.getPingCount());
        assertFalse(readData2.isStopOnSuccess());
        assertFalse(readData2.isIgnoreSSLError());
        assertTrue(readData2.isUseDefaultHeaders());
        readData2.setPingCount(data2.getPingCount());
        readData2.setStopOnSuccess(data2.isStopOnSuccess());
        readData2.setIgnoreSSLError(data2.isIgnoreSSLError());
        readData2.setUseDefaultHeaders(data2.isUseDefaultHeaders());
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
    public void testUpdateEncrypted() {
        AccessTypeData accessTypeData = getAccessTypeData2();
        accessTypeData = accessTypeDataDAO.insertAccessTypeData(accessTypeData);
        accessTypeData.setSnmpCommunity("mycommunity");
        accessTypeDataDAO.updateAccessTypeData(accessTypeData);
        Map<String, String> encryptedValues = accessTypeDataDAO.readEncryptedCommunityAndCommunityIV(accessTypeData.getId());
        assertNotNull(encryptedValues.get("SNMPCOMMUNITY"));
        assertNotNull(encryptedValues.get("SNMPCOMMUNITYIV"));
        assertNotEquals("mycommunity", encryptedValues.get("SNMPCOMMUNITY"));
        accessTypeData.setSnmpCommunity(null);
        accessTypeDataDAO.updateAccessTypeData(accessTypeData);
        encryptedValues = accessTypeDataDAO.readEncryptedCommunityAndCommunityIV(accessTypeData.getId());
        assertNull(encryptedValues.get("SNMPCOMMUNITY"));
        assertNull(encryptedValues.get("SNMPCOMMUNITYIV"));
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

    private void corruptKey() {
        String main_key_prefs_file = TestRegistry.getContext().getResources().getString(R.string.main_key_prefs_file);
        String mainKeyPrefsKey = TestRegistry.getContext().getResources().getString(R.string.main_key_prefs_key);
        SharedPreferences.Editor mainKeyPreferences = TestRegistry.getContext().getSharedPreferences(main_key_prefs_file, Context.MODE_PRIVATE).edit();
        mainKeyPreferences.putString(mainKeyPrefsKey, StringUtil.byteArrayToBase64(new byte[32]));
        mainKeyPreferences.commit();
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
        task.setLastSysUpTime(0);
        task.setFailureCount(1);
        task.setHighPrio(true);
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
        data.setIgnoreSSLError(true);
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V2C);
        data.setSnmpCommunity("community1");
        data.setSnmpCommunityValid(true);
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
        data.setIgnoreSSLError(true);
        data.setUseDefaultHeaders(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity(null);
        data.setSnmpCommunityValid(true);
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
        data.setIgnoreSSLError(false);
        data.setUseDefaultHeaders(true);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("community3");
        data.setSnmpCommunityValid(true);
        return data;
    }
}
