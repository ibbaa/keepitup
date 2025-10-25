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
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class HeaderDAOTest {

    private HeaderDAO headerDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        headerDAO = new HeaderDAO(TestRegistry.getContext());
        headerDAO.deleteAllHeaders();
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @After
    public void afterEachTestMethod() {
        headerDAO.deleteAllHeaders();
        networkTaskDAO.deleteAllNetworkTasks();
    }

    @Test
    public void testBasicInsertReadDelete() {
        Header header1 = getHeader1();
        header1 = headerDAO.insertHeader(header1);
        List<Header> readHeaderList = headerDAO.readAllHeaders();
        assertEquals(1, readHeaderList.size());
        Header readHeader = readHeaderList.get(0);
        assertTrue(readHeader.getId() > 0);
        assertTrue(header1.isEqual(readHeader));
        readHeaderList = headerDAO.readHeadersForNetworkTask(0);
        readHeader = readHeaderList.get(0);
        assertTrue(header1.isEqual(readHeader));
        Header header2 = getHeader2();
        headerDAO.insertHeader(header2);
        readHeaderList = headerDAO.readAllHeaders();
        assertEquals(2, readHeaderList.size());
        Header readHeader1 = readHeaderList.get(0);
        Header readHeader2 = readHeaderList.get(1);
        assertTrue(readHeader1.getId() > 0);
        assertTrue(readHeader2.getId() > 0);
        assertTrue(readHeader1.isTechnicallyEqual(header1));
        assertTrue(readHeader2.isTechnicallyEqual(header2));
        headerDAO.deleteHeadersForNetworkTask(1);
        readHeaderList = headerDAO.readHeadersForNetworkTask(1);
        assertTrue(readHeaderList.isEmpty());
        readHeaderList = headerDAO.readAllHeaders();
        assertEquals(1, readHeaderList.size());
        readHeader1 = readHeaderList.get(0);
        assertTrue(header1.isTechnicallyEqual(readHeader1));
        headerDAO.deleteAllHeaders();
        assertTrue(headerDAO.readAllHeaders().isEmpty());
    }

    @Test
    public void testDelete() {
        Header headerGlobal1 = getHeader1();
        headerGlobal1.setNetworkTaskId(-1);
        Header headerGlobal2 = getHeader1();
        headerGlobal2.setNetworkTaskId(-1);
        Header headerNetworkTask1 = getHeader2();
        headerNetworkTask1.setNetworkTaskId(1);
        Header headerNetworkTask2 = getHeader2();
        headerNetworkTask2.setNetworkTaskId(1);
        headerDAO.insertHeader(headerGlobal1);
        headerDAO.insertHeader(headerGlobal2);
        headerDAO.insertHeader(headerNetworkTask1);
        headerDAO.insertHeader(headerNetworkTask2);
        List<Header> readHeaderAllList = headerDAO.readAllHeaders();
        assertEquals(4, readHeaderAllList.size());
        assertTrue(doesHeaderListContain(readHeaderAllList, headerGlobal1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerGlobal2));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerNetworkTask1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerNetworkTask2));
        headerDAO.deleteHeader(headerGlobal1);
        readHeaderAllList = headerDAO.readAllHeaders();
        assertEquals(3, readHeaderAllList.size());
        assertFalse(doesHeaderListContain(readHeaderAllList, headerGlobal1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerGlobal2));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerNetworkTask1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerNetworkTask2));
        headerDAO.deleteHeader(headerNetworkTask1);
        readHeaderAllList = headerDAO.readAllHeaders();
        assertEquals(2, readHeaderAllList.size());
        assertFalse(doesHeaderListContain(readHeaderAllList, headerGlobal1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerGlobal2));
        assertFalse(doesHeaderListContain(readHeaderAllList, headerNetworkTask1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerNetworkTask2));
        headerDAO.deleteHeader(headerGlobal2);
        readHeaderAllList = headerDAO.readAllHeaders();
        assertEquals(1, readHeaderAllList.size());
        assertFalse(doesHeaderListContain(readHeaderAllList, headerGlobal1));
        assertFalse(doesHeaderListContain(readHeaderAllList, headerGlobal2));
        assertFalse(doesHeaderListContain(readHeaderAllList, headerNetworkTask1));
        assertTrue(doesHeaderListContain(readHeaderAllList, headerNetworkTask2));
        headerDAO.deleteHeader(headerNetworkTask2);
        readHeaderAllList = headerDAO.readAllHeaders();
        assertEquals(0, readHeaderAllList.size());
    }

    @Test
    public void testMultipleInsertReadDelete() {
        Header headerGlobal1 = getHeader1();
        headerGlobal1.setNetworkTaskId(-1);
        Header headerGlobal2 = getHeader1();
        headerGlobal2.setNetworkTaskId(-1);
        Header headerNetworkTask1 = getHeader2();
        headerNetworkTask1.setNetworkTaskId(1);
        Header headerNetworkTask2 = getHeader2();
        headerNetworkTask2.setNetworkTaskId(1);
        headerDAO.insertHeader(headerGlobal1);
        headerDAO.insertHeader(headerGlobal2);
        headerDAO.insertHeader(headerNetworkTask1);
        headerDAO.insertHeader(headerNetworkTask2);
        List<Header> readHeaderAllList = headerDAO.readAllHeaders();
        List<Header> readHeaderGlobalList = headerDAO.readGlobalHeaders();
        List<Header> readHeaderNetworkTaskList = headerDAO.readHeadersForNetworkTask(1);
        assertEquals(4, readHeaderAllList.size());
        assertEquals(2, readHeaderGlobalList.size());
        assertEquals(2, readHeaderNetworkTaskList.size());
        assertTrue(readHeaderGlobalList.get(0).isTechnicallyEqual(headerGlobal1));
        assertTrue(readHeaderGlobalList.get(1).isTechnicallyEqual(headerGlobal2));
        assertTrue(readHeaderNetworkTaskList.get(0).isTechnicallyEqual(headerNetworkTask1));
        assertTrue(readHeaderNetworkTaskList.get(1).isTechnicallyEqual(headerNetworkTask2));
        headerDAO.deleteGlobalHeaders();
        readHeaderAllList = headerDAO.readAllHeaders();
        readHeaderGlobalList = headerDAO.readGlobalHeaders();
        readHeaderNetworkTaskList = headerDAO.readHeadersForNetworkTask(1);
        assertEquals(2, readHeaderAllList.size());
        assertTrue(readHeaderGlobalList.isEmpty());
        assertEquals(2, readHeaderNetworkTaskList.size());
        headerDAO.deleteHeadersForNetworkTask(1);
        readHeaderAllList = headerDAO.readAllHeaders();
        readHeaderGlobalList = headerDAO.readGlobalHeaders();
        readHeaderNetworkTaskList = headerDAO.readHeadersForNetworkTask(1);
        assertTrue(readHeaderAllList.isEmpty());
        assertTrue(readHeaderGlobalList.isEmpty());
        assertTrue(readHeaderNetworkTaskList.isEmpty());
    }

    @Test
    public void testUpdate() {
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        headerDAO.insertHeader(header1);
        headerDAO.insertHeader(header2);
        Header readHeader1 = headerDAO.readHeadersForNetworkTask(0).get(0);
        Header readHeader2 = headerDAO.readHeadersForNetworkTask(1).get(0);
        readHeader2.setName("otherName");
        readHeader2.setValue("otherValue");
        headerDAO.updateHeader(readHeader2);
        readHeader2 = headerDAO.readHeadersForNetworkTask(1).get(0);
        assertEquals("otherName", readHeader2.getName());
        assertEquals("otherValue", readHeader2.getValue());
        readHeader2.setName(header2.getName());
        readHeader2.setValue(header2.getValue());
        assertTrue(header2.isEqual(readHeader2));
        readHeader1.setName("name");
        readHeader1.setValue("value");
        headerDAO.updateHeader(readHeader1);
        readHeader1 = headerDAO.readHeadersForNetworkTask(0).get(0);
        assertEquals("name", readHeader1.getName());
        assertEquals("value", readHeader1.getValue());
    }

    @Test
    public void testDeleteOrphan() {
        NetworkTask task = networkTaskDAO.insertNetworkTask(getNetworkTask());
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        Header header3 = getHeader1();
        header1.setNetworkTaskId(task.getId());
        header2.setNetworkTaskId(task.getId() + 1);
        header3.setNetworkTaskId(-1);
        headerDAO.insertHeader(header1);
        headerDAO.insertHeader(header2);
        headerDAO.insertHeader(header3);
        List<Header> readHeaderList = headerDAO.readAllHeaders();
        assertEquals(3, readHeaderList.size());
        headerDAO.deleteAllOrphanHeaders();
        readHeaderList = headerDAO.readAllHeaders();
        assertEquals(2, readHeaderList.size());
        assertTrue(doesHeaderListContain(readHeaderList, header1));
        assertTrue(doesHeaderListContain(readHeaderList, header3));
    }

    private boolean doesHeaderListContain(List<Header> headerList, Header header) {
        for (Header currentHeader : headerList) {
            if (currentHeader.isEqual(header)) {
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

    private Header getHeader1() {
        Header resolve = new Header();
        resolve.setId(0);
        resolve.setNetworkTaskId(0);
        resolve.setName("name1");
        resolve.setValue("value1");
        return resolve;
    }

    private Header getHeader2() {
        Header resolve = new Header();
        resolve.setId(0);
        resolve.setNetworkTaskId(1);
        resolve.setName("name2");
        resolve.setValue("value2");
        return resolve;
    }
}
