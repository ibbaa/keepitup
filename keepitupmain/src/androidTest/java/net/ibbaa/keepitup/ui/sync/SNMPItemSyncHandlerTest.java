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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SNMPItemSyncHandlerTest extends BaseUITest {

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getSnmpItemDAO().deleteAllSNMPItems();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        getSnmpItemDAO().deleteAllSNMPItems();
    }

    @Test
    public void testSynchronizeSNMPItemsEmpty() {
        SNMPItemSyncHandler handler = new SNMPItemSyncHandler(TestRegistry.getContext());
        DBSyncResult syncResult = handler.synchronizeSNMPItems(Collections.emptyList(), Collections.emptyList());
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        assertTrue(getSnmpItemDAO().readAllSNMPItems().isEmpty());
    }

    @Test
    public void testSynchronizeSNMPItemsDelete() {
        getSnmpItemDAO().insertSNMPItem(getSNMPItem1());
        getSnmpItemDAO().insertSNMPItem(getSNMPItem2());
        SNMPItemSyncHandler handler = new SNMPItemSyncHandler(TestRegistry.getContext());
        DBSyncResult syncResult = handler.synchronizeSNMPItems(Collections.emptyList(), getSnmpItemDAO().readAllSNMPItems());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertTrue(getSnmpItemDAO().readAllSNMPItems().isEmpty());
    }

    @Test
    public void testSynchronizeSNMPItemsAdd() {
        SNMPItemSyncHandler handler = new SNMPItemSyncHandler(TestRegistry.getContext());
        List<SNMPItem> newSNMPItems = List.of(getSNMPItem1(), getSNMPItem2(), getSNMPItem3());
        DBSyncResult syncResult = handler.synchronizeSNMPItems(newSNMPItems, Collections.emptyList());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<SNMPItem> snmpItems = getSnmpItemDAO().readAllSNMPItems();
        assertEquals(2, snmpItems.size());
        assertTrue(snmpItems.get(0).isTechnicallyEqual(getSNMPItem1()));
        assertTrue(snmpItems.get(1).isTechnicallyEqual(getSNMPItem2()));
    }

    @Test
    public void testSynchronizeSNMPItemsNotUpdated() {
        SNMPItem snmpItem1 = getSnmpItemDAO().insertSNMPItem(getSNMPItem1());
        SNMPItem snmpItem2 = getSnmpItemDAO().insertSNMPItem(getSNMPItem2());
        SNMPItemSyncHandler handler = new SNMPItemSyncHandler(TestRegistry.getContext());
        List<SNMPItem> newSNMPItems = List.of(snmpItem1, snmpItem2);
        DBSyncResult syncResult = handler.synchronizeSNMPItems(newSNMPItems, getSnmpItemDAO().readAllSNMPItems());
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        List<SNMPItem> snmpItems = getSnmpItemDAO().readAllSNMPItems();
        assertEquals(2, snmpItems.size());
        assertTrue(snmpItems.get(0).isEqual(snmpItem1));
        assertTrue(snmpItems.get(1).isEqual(snmpItem2));
    }

    @Test
    public void testSynchronizeSNMPItemsUpdated() {
        SNMPItem snmpItem1 = getSnmpItemDAO().insertSNMPItem(getSNMPItem1());
        SNMPItem snmpItem2 = getSnmpItemDAO().insertSNMPItem(getSNMPItem2());
        SNMPItemSyncHandler handler = new SNMPItemSyncHandler(TestRegistry.getContext());
        snmpItem1.setOid(".1.3.6.1.2.1.2.2.1.2.99");
        snmpItem1.setMonitored(false);
        snmpItem2.setOid(".1.3.6.1.2.1.2.2.1.3.99");
        snmpItem2.setMonitored(true);
        List<SNMPItem> newSNMPItems = List.of(snmpItem1, snmpItem2);
        DBSyncResult syncResult = handler.synchronizeSNMPItems(newSNMPItems, getSnmpItemDAO().readAllSNMPItems());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<SNMPItem> snmpItems = getSnmpItemDAO().readAllSNMPItems();
        assertEquals(2, snmpItems.size());
        assertFalse(snmpItems.get(0).isTechnicallyEqual(getSNMPItem1()));
        assertFalse(snmpItems.get(1).isTechnicallyEqual(getSNMPItem2()));
        assertTrue(snmpItems.get(0).isEqual(snmpItem1));
        assertTrue(snmpItems.get(1).isEqual(snmpItem2));
    }

    @Test
    public void testSynchronizeSNMPItemsAddedUpdatedDeleted() {
        getSnmpItemDAO().insertSNMPItem(getSNMPItem2());
        SNMPItem snmpItem3 = getSnmpItemDAO().insertSNMPItem(getSNMPItem3());
        SNMPItemSyncHandler handler = new SNMPItemSyncHandler(TestRegistry.getContext());
        snmpItem3.setOid(".1.3.6.1.2.1.31.1.1.1.1.99");
        List<SNMPItem> newSNMPItems = List.of(getSNMPItem1(), snmpItem3);
        DBSyncResult syncResult = handler.synchronizeSNMPItems(newSNMPItems, getSnmpItemDAO().readAllSNMPItems());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<SNMPItem> snmpItems = getSnmpItemDAO().readAllSNMPItems();
        assertEquals(2, snmpItems.size());
        assertTrue(snmpItems.get(0).isTechnicallyEqual(getSNMPItem1()));
        assertTrue(snmpItems.get(1).isEqual(snmpItem3));
    }

    private SNMPItem getSNMPItem1() {
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(-1);
        snmpItem.setNetworkTaskId(1);
        snmpItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        snmpItem.setName("eth0");
        snmpItem.setOid(".1.3.6.1.2.1.2.2.1.2.1");
        snmpItem.setMonitored(true);
        return snmpItem;
    }

    private SNMPItem getSNMPItem2() {
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(-1);
        snmpItem.setNetworkTaskId(1);
        snmpItem.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        snmpItem.setName("eth1");
        snmpItem.setOid(".1.3.6.1.2.1.2.2.1.3.1");
        snmpItem.setMonitored(false);
        return snmpItem;
    }

    private SNMPItem getSNMPItem3() {
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(0);
        snmpItem.setNetworkTaskId(1);
        snmpItem.setSnmpItemType(SNMPItemType.INTERFACEALIAS);
        snmpItem.setName("eth2");
        snmpItem.setOid(".1.3.6.1.2.1.31.1.1.1.1.1");
        snmpItem.setMonitored(true);
        return snmpItem;
    }
}
