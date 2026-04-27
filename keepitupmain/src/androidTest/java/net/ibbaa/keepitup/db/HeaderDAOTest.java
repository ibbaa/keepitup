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
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.encryption.MainKeyAccess;
import net.ibbaa.keepitup.test.mock.TestHeaderDAO;
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
public class HeaderDAOTest {

    private TestHeaderDAO headerDAO;
    private NetworkTaskDAO networkTaskDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        MainKeyAccess mainKeyAccess = new MainKeyAccess(TestRegistry.getContext());
        mainKeyAccess.resetMainKey();
        headerDAO = new TestHeaderDAO(TestRegistry.getContext());
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
    public void testInsertReadDelete() {
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
    public void testBulkInsert() {
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        Header header3 = getHeader3();
        int count = headerDAO.insertHeaders(List.of(header1, header2, header3));
        assertEquals(3, count);
        List<Header> readHeaderList = headerDAO.readAllHeaders();
        assertEquals(3, readHeaderList.size());
        assertTrue(header1.isTechnicallyEqual(readHeaderList.get(0)));
        assertTrue(header2.isTechnicallyEqual(readHeaderList.get(1)));
        assertTrue(header3.isTechnicallyEqual(readHeaderList.get(2)));
    }

    @Test
    public void testInsertReadEncrypted() {
        Header header = getHeader4();
        header = headerDAO.insertHeader(header);
        assertTrue(header.getId() > 0);
        assertTrue(header.isTechnicallyEqual(getHeader4()));
        Map<String, String> encryptedValues = headerDAO.readEncryptedValueAndValueIV(header.getId());
        assertNotNull(encryptedValues.get("VALUE"));
        assertNotNull(encryptedValues.get("VALUEIV"));
        assertNotEquals("value4", encryptedValues.get("VALUE"));
        Header readHeader = headerDAO.readAllHeaders().get(0);
        assertTrue(header.isTechnicallyEqual(readHeader));
    }

    @Test
    public void testInsertReadUnencryptedAuthorization() {
        Header header1 = getHeader2();
        Header header2 = getHeader4();
        header1 = headerDAO.insertHeader(header1);
        header2 = headerDAO.insertHeaderUnencrypted(header2);
        Header readHeader1 = headerDAO.readAllHeaders().get(0);
        Header readHeader2 = headerDAO.readAllHeaders().get(1);
        assertTrue(header1.isTechnicallyEqual(readHeader1));
        assertTrue(header2.isTechnicallyEqual(readHeader2));
        Map<String, String> encryptedValues1 = headerDAO.readEncryptedValueAndValueIV(header1.getId());
        Map<String, String> encryptedValues2 = headerDAO.readEncryptedValueAndValueIV(header2.getId());
        assertNotNull(encryptedValues1.get("VALUE"));
        assertNotNull(encryptedValues1.get("VALUEIV"));
        assertEquals("value4", encryptedValues2.get("VALUE"));
        assertNull(encryptedValues2.get("VALUEIV"));
    }

    @Test
    public void testBulkInsertReadEncrypted() {
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        Header header3 = getHeader3();
        Header header4 = getHeader4();
        int count = headerDAO.insertHeaders(List.of(header1, header2, header3, header4));
        assertEquals(4, count);
        List<Header> readHeaderList = headerDAO.readAllHeaders();
        assertEquals(4, readHeaderList.size());
        assertTrue(header1.isTechnicallyEqual(readHeaderList.get(0)));
        assertTrue(header2.isTechnicallyEqual(readHeaderList.get(1)));
        assertTrue(header3.isTechnicallyEqual(readHeaderList.get(2)));
        assertTrue(header4.isTechnicallyEqual(readHeaderList.get(3)));
        Map<String, String> encryptedValues1 = headerDAO.readEncryptedValueAndValueIV(header1.getId());
        Map<String, String> encryptedValues2 = headerDAO.readEncryptedValueAndValueIV(header2.getId());
        Map<String, String> encryptedValues3 = headerDAO.readEncryptedValueAndValueIV(header3.getId());
        Map<String, String> encryptedValues4 = headerDAO.readEncryptedValueAndValueIV(header4.getId());
        assertEquals("value1", encryptedValues1.get("VALUE"));
        assertNull(encryptedValues1.get("VALUEIV"));
        assertNotEquals("value2", encryptedValues2.get("VALUE"));
        assertNotNull(encryptedValues2.get("VALUE"));
        assertNotNull(encryptedValues2.get("VALUEIV"));
        assertEquals("value3", encryptedValues3.get("VALUE"));
        assertNull(encryptedValues3.get("VALUEIV"));
        assertNotEquals("value4", encryptedValues4.get("VALUE"));
        assertNotNull(encryptedValues4.get("VALUE"));
        assertNotNull(encryptedValues4.get("VALUEIV"));
    }

    @Test
    public void testBulkInsertReadEncryptedKeyInvalid() {
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        Header header3 = getHeader3();
        Header header4 = getHeader4();
        int count = headerDAO.insertHeaders(List.of(header1, header2, header3, header4));
        assertEquals(4, count);
        corruptKey();
        List<Header> readHeaderList = headerDAO.readAllHeaders();
        assertEquals(4, readHeaderList.size());
        Header readHeader2 = readHeaderList.get(1);
        Header readHeader4 = readHeaderList.get(3);
        assertFalse(readHeader2.isValueValid());
        assertFalse(readHeader4.isValueValid());
        assertEquals("", readHeader2.getValue());
        assertEquals("", readHeader4.getValue());
        assertEquals("name2", readHeader2.getName());
        assertEquals("name4", readHeader4.getName());
        assertTrue(header1.isTechnicallyEqual(readHeaderList.get(0)));
        assertTrue(header3.isTechnicallyEqual(readHeaderList.get(2)));
    }

    @Test
    public void testUpdateEncrypted() {
        Header header = getHeader1();
        header = headerDAO.insertHeader(header);
        assertTrue(header.isTechnicallyEqual(getHeader1()));
        header.setHeaderType(HeaderType.GENERICAUTH);
        header.setValue("secret");
        headerDAO.updateHeader(header);
        header = headerDAO.readAllHeaders().get(0);
        assertEquals("name1", header.getName());
        assertEquals("secret", header.getValue());
        assertEquals(HeaderType.GENERICAUTH, header.getHeaderType());
        Map<String, String> encryptedValues = headerDAO.readEncryptedValueAndValueIV(header.getId());
        assertNotNull(encryptedValues.get("VALUE"));
        assertNotNull(encryptedValues.get("VALUEIV"));
        assertNotEquals("secret", encryptedValues.get("VALUE"));
    }

    @Test
    public void testReadAllHeadersForNetworkTasks() {
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        Header header3 = getHeader1();
        Header header4 = getHeader2();
        Header header5 = getHeader1();
        Header header6 = getHeader2();
        Header header7 = getHeader2();
        Header header8 = getHeader1();
        header6.setNetworkTaskId(3);
        header7.setNetworkTaskId(-1);
        header8.setNetworkTaskId(-1);
        header1 = headerDAO.insertHeader(header1);
        header2 = headerDAO.insertHeader(header2);
        headerDAO.insertHeader(header3);
        headerDAO.insertHeader(header4);
        headerDAO.insertHeader(header5);
        header6 = headerDAO.insertHeader(header6);
        headerDAO.insertHeader(header7);
        headerDAO.insertHeader(header8);
        Map<Long, List<Header>> result = headerDAO.readAllHeadersForNetworkTasks();
        assertEquals(3, result.size());
        List<Header> headerList1 = result.get(0L);
        List<Header> headerList2 = result.get(1L);
        List<Header> headerList3 = result.get(3L);
        assertNotNull(headerList1);
        assertNotNull(headerList2);
        assertNotNull(headerList3);
        assertEquals(3, headerList1.size());
        assertEquals(2, headerList2.size());
        assertEquals(1, headerList3.size());
        assertTrue(header1.isTechnicallyEqual(headerList1.get(0)));
        assertTrue(header1.isTechnicallyEqual(headerList1.get(1)));
        assertTrue(header1.isTechnicallyEqual(headerList1.get(2)));
        assertTrue(header2.isTechnicallyEqual(headerList2.get(0)));
        assertTrue(header2.isTechnicallyEqual(headerList2.get(1)));
        assertTrue(header6.isTechnicallyEqual(headerList3.get(0)));
    }

    @Test
    public void testReadAllHeadersForNetworkTasksEncryptedKeyInvalid() {
        Header header1 = getHeader1();
        Header header2 = getHeader2();
        Header header3 = getHeader1();
        Header header4 = getHeader2();
        header1.setNetworkTaskId(1);
        header2.setNetworkTaskId(1);
        header3.setNetworkTaskId(3);
        header4.setNetworkTaskId(3);
        headerDAO.insertHeader(header1);
        headerDAO.insertHeader(header2);
        headerDAO.insertHeader(header3);
        headerDAO.insertHeader(header4);
        corruptKey();
        Map<Long, List<Header>> result = headerDAO.readAllHeadersForNetworkTasks();
        assertEquals(2, result.size());
        List<Header> headerList1 = result.get(1L);
        List<Header> headerList2 = result.get(3L);
        assertNotNull(headerList1);
        assertNotNull(headerList2);
        assertTrue(headerList1.get(0).isValueValid());
        assertFalse(headerList1.get(1).isValueValid());
        assertTrue(headerList2.get(0).isValueValid());
        assertFalse(headerList2.get(1).isValueValid());
        assertEquals("", headerList1.get(1).getValue());
        assertEquals("", headerList2.get(1).getValue());
        List<Header> readHeaders = headerDAO.readHeadersForNetworkTask(3);
        assertTrue(readHeaders.get(0).isTechnicallyEqual(headerList2.get(0)));
        assertTrue(readHeaders.get(1).isTechnicallyEqual(headerList2.get(1)));
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
    public void testBulkDelete() {
        Header headerGlobal1 = getHeader1();
        headerGlobal1.setNetworkTaskId(-1);
        Header headerGlobal2 = getHeader1();
        headerGlobal2.setNetworkTaskId(-1);
        Header headerNetworkTask1 = getHeader2();
        headerNetworkTask1.setNetworkTaskId(1);
        Header headerNetworkTask2 = getHeader2();
        headerNetworkTask2.setNetworkTaskId(1);
        headerDAO.insertHeaders(List.of(headerGlobal1, headerGlobal2, headerNetworkTask1, headerNetworkTask2));
        headerDAO.deleteHeaders(List.of(headerGlobal1, headerGlobal2, headerNetworkTask1));
        List<Header> readHeaders = headerDAO.readAllHeaders();
        assertEquals(1, readHeaders.size());
        assertTrue(doesHeaderListContain(readHeaders, headerNetworkTask2));
        headerDAO.deleteHeaders(List.of(headerNetworkTask2));
        readHeaders = headerDAO.readAllHeaders();
        assertEquals(0, readHeaders.size());
    }

    @Test
    public void testDeleteNotExisting() {
        Header header1 = getHeader1();
        headerDAO.insertHeader(header1);
        Header header2 = getHeader2();
        header2.setId(header1.getId() + 1);
        Header header3 = getHeader3();
        header3.setId(header1.getId() + 2);
        headerDAO.deleteHeaders(List.of(header1, header2, header3));
        List<Header> readHeaders = headerDAO.readAllHeaders();
        assertEquals(0, readHeaders.size());
        header1 = getHeader1();
        header1.setNetworkTaskId(1);
        header2 = getHeader2();
        header2.setNetworkTaskId(1);
        headerDAO.insertHeader(header1);
        headerDAO.insertHeader(header2);
        header3 = getHeader3();
        header3.setNetworkTaskId(1);
        header3.setId(header2.getId() + 10);
        headerDAO.deleteHeadersForNetworkTask(1);
        readHeaders = headerDAO.readAllHeaders();
        assertEquals(0, readHeaders.size());
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
    public void testReadGlobalEncrypted() {
        Header headerGlobal1 = getHeader1();
        headerGlobal1.setNetworkTaskId(-1);
        Header headerGlobal2 = getHeader2();
        headerGlobal2.setNetworkTaskId(-1);
        Header header3 = getHeader3();
        Header header4 = getHeader4();
        headerDAO.insertHeaders(List.of(headerGlobal1, headerGlobal2, header3, header4));
        List<Header> globalHeaders = headerDAO.readGlobalHeaders();
        List<Header> allHeaders = headerDAO.readAllHeaders();
        assertEquals(2, globalHeaders.size());
        assertEquals(4, allHeaders.size());
        assertTrue(headerGlobal1.isTechnicallyEqual(globalHeaders.get(0)));
        assertTrue(headerGlobal2.isTechnicallyEqual(globalHeaders.get(1)));
        assertTrue(headerGlobal1.isTechnicallyEqual(allHeaders.get(0)));
        assertTrue(headerGlobal2.isTechnicallyEqual(allHeaders.get(1)));
        assertTrue(header3.isTechnicallyEqual(allHeaders.get(2)));
        assertTrue(header4.isTechnicallyEqual(allHeaders.get(3)));
        Map<String, String> encryptedValues1 = headerDAO.readEncryptedValueAndValueIV(headerGlobal1.getId());
        Map<String, String> encryptedValues2 = headerDAO.readEncryptedValueAndValueIV(headerGlobal2.getId());
        Map<String, String> encryptedValues3 = headerDAO.readEncryptedValueAndValueIV(header3.getId());
        Map<String, String> encryptedValues4 = headerDAO.readEncryptedValueAndValueIV(header4.getId());
        assertEquals("value1", encryptedValues1.get("VALUE"));
        assertNull(encryptedValues1.get("VALUEIV"));
        assertNotEquals("value2", encryptedValues2.get("VALUE"));
        assertNotNull(encryptedValues2.get("VALUE"));
        assertNotNull(encryptedValues2.get("VALUEIV"));
        assertEquals("value3", encryptedValues3.get("VALUE"));
        assertNull(encryptedValues3.get("VALUEIV"));
        assertNotEquals("value4", encryptedValues4.get("VALUE"));
        assertNotNull(encryptedValues4.get("VALUE"));
        assertNotNull(encryptedValues4.get("VALUEIV"));
    }

    @Test
    public void testReadGlobalEncryptedKeyInvalid() {
        Header headerGlobal1 = getHeader1();
        headerGlobal1.setNetworkTaskId(-1);
        Header headerGlobal2 = getHeader2();
        headerGlobal2.setNetworkTaskId(-1);
        headerDAO.insertHeaders(List.of(headerGlobal1, headerGlobal2));
        corruptKey();
        List<Header> globalHeaders = headerDAO.readGlobalHeaders();
        assertEquals(2, globalHeaders.size());
        Header readHeader1 = globalHeaders.get(0);
        Header readHeader2 = globalHeaders.get(1);
        assertFalse(readHeader2.isValueValid());
        assertEquals("", readHeader2.getValue());
        assertEquals("name2", readHeader2.getName());
        assertTrue(headerGlobal1.isTechnicallyEqual(readHeader1));
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

    private Header getHeader1() {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(0);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("name1");
        header.setValue("value1");
        header.setValueValid(true);
        return header;
    }

    private Header getHeader2() {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(1);
        header.setHeaderType(HeaderType.BASICAUTH);
        header.setName("name2");
        header.setValue("value2");
        header.setValueValid(true);
        return header;
    }

    private Header getHeader3() {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(1);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("name3");
        header.setValue("value3");
        header.setValueValid(true);
        return header;
    }

    private Header getHeader4() {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(1);
        header.setHeaderType(HeaderType.GENERICAUTH);
        header.setName("name4");
        header.setValue("value4");
        header.setValueValid(true);
        return header;
    }
}
