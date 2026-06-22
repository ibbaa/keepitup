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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class SNMPItemDAOTest {

    private SNMPItemDAO snmpItemDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        snmpItemDAO = new SNMPItemDAO(TestRegistry.getContext());
        snmpItemDAO.deleteAllSNMPItems();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @After
    public void afterEachTestMethod() {
        snmpItemDAO.deleteAllSNMPItems();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testInsertReadDelete() {
        SNMPItem item1 = getSNMPItem1();
        item1 = snmpItemDAO.insertSNMPItem(item1);
        List<SNMPItem> readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(1, readItemList.size());
        SNMPItem readItem = readItemList.get(0);
        assertTrue(readItem.getId() > 0);
        assertTrue(item1.isEqual(readItem));
        readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(0);
        assertTrue(item1.isEqual(readItemList.get(0)));
        SNMPItem item2 = getSNMPItem2();
        snmpItemDAO.insertSNMPItem(item2);
        readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(2, readItemList.size());
        SNMPItem readItem1 = readItemList.get(0);
        SNMPItem readItem2 = readItemList.get(1);
        assertTrue(readItem1.getId() > 0);
        assertTrue(readItem2.getId() > 0);
        assertTrue(item1.isEqual(readItem1));
        assertTrue(item2.isEqual(readItem2));
        snmpItemDAO.deleteAllSNMPItemsForNetworkTask(1);
        readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(1);
        assertTrue(readItemList.isEmpty());
        readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(1, readItemList.size());
        readItem1 = readItemList.get(0);
        assertTrue(item1.isTechnicallyEqual(readItem1));
        readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(0);
        assertEquals(1, readItemList.size());
        readItem1 = readItemList.get(0);
        assertTrue(item1.isTechnicallyEqual(readItem1));
        snmpItemDAO.deleteAllSNMPItems();
        assertTrue(snmpItemDAO.readAllSNMPItems().isEmpty());
    }

    @Test
    public void testInsertSNMPItems() {
        int count = snmpItemDAO.insertSNMPItems(List.of());
        assertEquals(0, count);
        assertTrue(snmpItemDAO.readAllSNMPItems().isEmpty());
        SNMPItem item1 = getSNMPItem1();
        SNMPItem item2 = getSNMPItem2();
        SNMPItem item3 = getSNMPItem3();
        item1.setNetworkTaskId(0);
        item2.setNetworkTaskId(0);
        item3.setNetworkTaskId(0);
        count = snmpItemDAO.insertSNMPItems(Arrays.asList(item1, item2, item3));
        assertEquals(3, count);
        List<SNMPItem> readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(0);
        assertEquals(3, readItemList.size());
        assertTrue(readItemList.get(0).getId() > 0);
        assertTrue(readItemList.get(1).getId() > 0);
        assertTrue(readItemList.get(2).getId() > 0);
        assertTrue(item1.isTechnicallyEqual(readItemList.get(0)));
        assertTrue(item2.isTechnicallyEqual(readItemList.get(1)));
        assertTrue(item3.isTechnicallyEqual(readItemList.get(2)));
    }

    @Test
    public void testUpdate() {
        SNMPItem item1 = getSNMPItem1();
        SNMPItem item2 = getSNMPItem2();
        snmpItemDAO.insertSNMPItem(item1);
        snmpItemDAO.insertSNMPItem(item2);
        SNMPItem readItem1 = snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0);
        SNMPItem readItem2 = snmpItemDAO.readAllSNMPItemsForNetworkTask(1).get(0);
        readItem2.setOid("1.3.6.1.99");
        readItem2.setMonitored(true);
        snmpItemDAO.updateSNMPItem(readItem2);
        readItem2 = snmpItemDAO.readAllSNMPItemsForNetworkTask(1).get(0);
        assertEquals("1.3.6.1.99", readItem2.getOid());
        assertTrue(readItem2.isMonitored());
        readItem2.setOid(item2.getOid());
        readItem2.setMonitored(item2.isMonitored());
        assertTrue(item2.isEqual(readItem2));
        readItem1.setName("UpdatedName");
        readItem1.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        snmpItemDAO.updateSNMPItem(readItem1);
        readItem1 = snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0);
        assertEquals("UpdatedName", readItem1.getName());
        assertEquals(SNMPItemType.INTERFACETYPE, readItem1.getSnmpItemType());
    }

    @Test
    public void testUpdateMonitored() {
        SNMPItem item1 = getSNMPItem1();
        item1 = snmpItemDAO.insertSNMPItem(item1);
        assertTrue(snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0).isMonitored());
        snmpItemDAO.updateSNMPItemMonitored(item1.getId(), false);
        assertFalse(snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0).isMonitored());
        snmpItemDAO.updateSNMPItemMonitored(item1.getId(), true);
        assertTrue(snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0).isMonitored());
    }

    @Test
    public void testUpdateOid() {
        SNMPItem item1 = getSNMPItem1();
        item1 = snmpItemDAO.insertSNMPItem(item1);
        assertEquals("1.3.6.1.1", snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0).getOid());
        snmpItemDAO.updateSNMPItemOid(item1.getId(), "1.3.6.1.99");
        assertEquals("1.3.6.1.99", snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0).getOid());
        snmpItemDAO.updateSNMPItemOid(item1.getId(), null);
        assertNull(snmpItemDAO.readAllSNMPItemsForNetworkTask(0).get(0).getOid());
    }

    @Test
    public void testSortByName() {
        SNMPItem charlie = new SNMPItem();
        charlie.setNetworkTaskId(0);
        charlie.setName("Charlie");
        charlie.setOid("1.3.6.1.3");
        charlie.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        charlie.setMonitored(true);
        SNMPItem alpha = new SNMPItem();
        alpha.setNetworkTaskId(0);
        alpha.setName("Alpha");
        alpha.setOid("1.3.6.1.1");
        alpha.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        alpha.setMonitored(false);
        SNMPItem beta = new SNMPItem();
        beta.setNetworkTaskId(0);
        beta.setName("Beta");
        beta.setOid("1.3.6.1.2");
        beta.setSnmpItemType(SNMPItemType.INTERFACEALIAS);
        beta.setMonitored(true);
        snmpItemDAO.insertSNMPItem(charlie);
        snmpItemDAO.insertSNMPItem(alpha);
        snmpItemDAO.insertSNMPItem(beta);
        List<SNMPItem> readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(0);
        assertEquals(3, readItemList.size());
        assertEquals("Alpha", readItemList.get(0).getName());
        assertEquals("Beta", readItemList.get(1).getName());
        assertEquals("Charlie", readItemList.get(2).getName());
        readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(3, readItemList.size());
        assertEquals("Alpha", readItemList.get(0).getName());
        assertEquals("Beta", readItemList.get(1).getName());
        assertEquals("Charlie", readItemList.get(2).getName());
    }

    @Test
    public void testSortByNameThenId() {
        SNMPItem item1 = new SNMPItem();
        item1.setNetworkTaskId(0);
        item1.setName("Interface");
        item1.setOid("1.3.6.1.1");
        item1.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        item1.setMonitored(true);
        SNMPItem item2 = new SNMPItem();
        item2.setNetworkTaskId(0);
        item2.setName("Interface");
        item2.setOid("1.3.6.1.2");
        item2.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        item2.setMonitored(false);
        SNMPItem item3 = new SNMPItem();
        item3.setNetworkTaskId(0);
        item3.setName("Interface");
        item3.setOid("1.3.6.1.3");
        item3.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        item3.setMonitored(true);
        item1 = snmpItemDAO.insertSNMPItem(item1);
        item2 = snmpItemDAO.insertSNMPItem(item2);
        item3 = snmpItemDAO.insertSNMPItem(item3);
        List<SNMPItem> readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(0);
        assertEquals(3, readItemList.size());
        assertEquals(item1.getId(), readItemList.get(0).getId());
        assertEquals(item2.getId(), readItemList.get(1).getId());
        assertEquals(item3.getId(), readItemList.get(2).getId());
        readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(3, readItemList.size());
        assertEquals(item1.getId(), readItemList.get(0).getId());
        assertEquals(item2.getId(), readItemList.get(1).getId());
        assertEquals(item3.getId(), readItemList.get(2).getId());
        SNMPItem item4 = new SNMPItem();
        item4.setNetworkTaskId(0);
        item4.setName("Alpha");
        item4.setOid("1.3.6.1.4");
        item4.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item4.setMonitored(false);
        item4 = snmpItemDAO.insertSNMPItem(item4);
        readItemList = snmpItemDAO.readAllSNMPItemsForNetworkTask(0);
        assertEquals(4, readItemList.size());
        assertEquals("Alpha", readItemList.get(0).getName());
        assertEquals(item4.getId(), readItemList.get(0).getId());
        assertEquals(item1.getId(), readItemList.get(1).getId());
        assertEquals(item2.getId(), readItemList.get(2).getId());
        assertEquals(item3.getId(), readItemList.get(3).getId());
    }

    @Test
    public void testNullSNMPItemType() {
        SNMPItem item3 = getSNMPItem3();
        item3 = snmpItemDAO.insertSNMPItem(item3);
        SNMPItem readItem = snmpItemDAO.readAllSNMPItemsForNetworkTask(2).get(0);
        assertNull(readItem.getSnmpItemType());
        assertTrue(item3.isEqual(readItem));
    }

    @Test
    public void testReadAllSNMPItemsForNetworkTasks() {
        SNMPItem item1 = getSNMPItem1();
        item1 = snmpItemDAO.insertSNMPItem(item1);
        SNMPItem item2 = getSNMPItem2();
        item2 = snmpItemDAO.insertSNMPItem(item2);
        SNMPItem item3 = getSNMPItem3();
        item3 = snmpItemDAO.insertSNMPItem(item3);
        SNMPItem item4 = getSNMPItem2();
        item4.setName("Extra");
        item4 = snmpItemDAO.insertSNMPItem(item4);
        Map<Long, List<SNMPItem>> result = snmpItemDAO.readAllSNMPItemsForNetworkTasks();
        assertEquals(3, result.size());
        List<SNMPItem> itemList0 = Objects.requireNonNull(result.get(0L));
        List<SNMPItem> itemList1 = Objects.requireNonNull(result.get(1L));
        List<SNMPItem> itemList2 = Objects.requireNonNull(result.get(2L));
        assertEquals(1, itemList0.size());
        assertEquals(2, itemList1.size());
        assertEquals(1, itemList2.size());
        assertTrue(item1.isTechnicallyEqual(itemList0.get(0)));
        assertTrue(item2.isTechnicallyEqual(itemList1.get(0)));
        assertTrue(item4.isTechnicallyEqual(itemList1.get(1)));
        assertTrue(item3.isTechnicallyEqual(itemList2.get(0)));
    }

    @Test
    public void testDeleteSNMPItem() {
        SNMPItem item1 = getSNMPItem1();
        SNMPItem item2 = getSNMPItem2();
        item1 = snmpItemDAO.insertSNMPItem(item1);
        item2 = snmpItemDAO.insertSNMPItem(item2);
        assertEquals(2, snmpItemDAO.readAllSNMPItems().size());
        snmpItemDAO.deleteSNMPItem(item1);
        List<SNMPItem> readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(1, readItemList.size());
        assertTrue(item2.isTechnicallyEqual(readItemList.get(0)));
        snmpItemDAO.deleteSNMPItem(item2);
        assertTrue(snmpItemDAO.readAllSNMPItems().isEmpty());
    }

    @Test
    public void testDeleteOrphan() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        SNMPItem item1 = getSNMPItem1();
        SNMPItem item2 = getSNMPItem2();
        item1.setNetworkTaskId(task.getId());
        item2.setNetworkTaskId(task.getId() + 1);
        snmpItemDAO.insertSNMPItem(item1);
        snmpItemDAO.insertSNMPItem(item2);
        List<SNMPItem> readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(2, readItemList.size());
        snmpItemDAO.deleteAllOrphanSNMPItems();
        readItemList = snmpItemDAO.readAllSNMPItems();
        assertEquals(1, readItemList.size());
        assertTrue(item1.isTechnicallyEqual(readItemList.get(0)));
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

    private SNMPItem getSNMPItem1() {
        SNMPItem item = new SNMPItem();
        item.setId(0);
        item.setNetworkTaskId(0);
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setName("Alpha");
        item.setOid("1.3.6.1.1");
        item.setMonitored(true);
        return item;
    }

    private SNMPItem getSNMPItem2() {
        SNMPItem item = new SNMPItem();
        item.setId(0);
        item.setNetworkTaskId(1);
        item.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        item.setName("Beta");
        item.setOid("1.3.6.1.2");
        item.setMonitored(false);
        return item;
    }

    private SNMPItem getSNMPItem3() {
        SNMPItem item = new SNMPItem();
        item.setId(0);
        item.setNetworkTaskId(2);
        item.setSnmpItemType(null);
        item.setName("Gamma");
        item.setOid("1.3.6.1.3");
        item.setMonitored(true);
        return item;
    }
}
