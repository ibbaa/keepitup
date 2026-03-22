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

import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class HeaderSyncHandlerTest extends BaseUITest {

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getHeaderDAO().deleteAllHeaders();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        getHeaderDAO().deleteAllHeaders();
    }

    @Test
    public void testContainsInvalidHeaders() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertFalse(handler.containsInvalidHeaders(null));
        assertFalse(handler.containsInvalidHeaders(Collections.emptyList()));
        Header header1 = getHeader1(-1);
        Header header2 = getHeader2(-1);
        Header header3 = getHeader3(-1);
        header2.setValueValid(false);
        assertTrue(handler.containsInvalidHeaders(List.of(header1, header2, header3)));
        assertTrue(handler.containsInvalidHeaders(List.of(header1, header2)));
        assertFalse(handler.containsInvalidHeaders(List.of(header1, header3)));
    }

    @Test
    public void testRemoveInvalidHeaders() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertTrue(handler.removeInvalidHeaders(Collections.emptyList()).isEmpty());
        Header header1 = getHeader1(-1);
        Header header2 = getHeader2(-1);
        Header header3 = getHeader3(-1);
        header2.setValueValid(false);
        List<Header> originalList = new ArrayList<>(List.of(header1, header2, header3));
        List<Header> headers = handler.removeInvalidHeaders(originalList);
        assertEquals(1, headers.size());
        assertTrue(headers.get(0).isTechnicallyEqual(header2));
        assertEquals(2, originalList.size());
        assertTrue(header1.isTechnicallyEqual(originalList.get(0)));
        assertTrue(header3.isTechnicallyEqual(originalList.get(1)));
    }

    @Test
    public void testGetInvalidHeaders() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertTrue(handler.getInvalidHeaders(Collections.emptyList()).isEmpty());
        Header header1 = getHeader1(-1);
        Header header2 = getHeader2(-1);
        Header header3 = getHeader3(-1);
        header2.setValueValid(false);
        List<Header> headers = handler.getInvalidHeaders(List.of(header1, header2, header3));
        assertEquals(1, headers.size());
        assertTrue(headers.get(0).isTechnicallyEqual(header2));
    }

    @Test
    public void testGetGlobalHeadersCopyForNetworkTask() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        assertEquals(0, handler.getGlobalHeadersCopyForNetworkTask(1).size());
        getHeaderDAO().insertHeader(getHeader1(-1));
        getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader3(-1));
        handler.reset();
        List<Header> copyHeaders = handler.getGlobalHeadersCopyForNetworkTask(1);
        assertEquals(3, copyHeaders.size());
        assertTrue(getHeader1(1).isTechnicallyEqual(copyHeaders.get(0)));
        assertTrue(getHeader2(1).isTechnicallyEqual(copyHeaders.get(1)));
        assertTrue(getHeader3(1).isTechnicallyEqual(copyHeaders.get(2)));
    }

    @Test
    public void testGetGlobalHeaders() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        assertEquals(0, handler.getGlobalHeaders().size());
        getHeaderDAO().insertHeader(getHeader1(-1));
        getHeaderDAO().insertHeader(getHeader1(1));
        assertEquals(0, handler.getGlobalHeaders().size());
        handler.reset();
        assertEquals(1, handler.getGlobalHeaders().size());
        getHeaderDAO().insertHeader(getHeader2(-1));
        handler.reset();
        assertEquals(2, handler.getGlobalHeaders().size());
        assertTrue(getHeader1(-1).isTechnicallyEqual(handler.getGlobalHeaders().get(0)));
        assertTrue(getHeader2(-1).isTechnicallyEqual(handler.getGlobalHeaders().get(1)));
        handler.reset();
    }

    @Test
    public void testGetHeaders() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        assertEquals(0, getHeaderDAO().readAllHeaders().size());
        getHeaderDAO().insertHeader(getHeader3(3));
        getHeaderDAO().insertHeader(getHeader1(1));
        assertEquals(1, handler.getHeaders(1).size());
        assertEquals(0, handler.getHeaders(2).size());
        assertEquals(1, handler.getHeaders(3).size());
        getHeaderDAO().insertHeader(getHeader2(1));
        assertEquals(2, handler.getHeaders(1).size());
        assertEquals(0, handler.getHeaders(2).size());
        assertEquals(1, handler.getHeaders(3).size());
        assertTrue(getHeader1(1).isTechnicallyEqual(handler.getHeaders(1).get(0)));
        assertTrue(getHeader2(1).isTechnicallyEqual(handler.getHeaders(1).get(1)));
        assertTrue(getHeader3(3).isTechnicallyEqual(handler.getHeaders(3).get(0)));
    }

    @Test
    public void testSynchronizeGlobalHeadersEmpty() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        DBSyncResult syncResult = handler.synchronizeHeaders(-1, Collections.emptyList());
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        assertTrue(getHeaderDAO().readAllHeaders().isEmpty());
    }

    @Test
    public void testSynchronizeHeadersEmpty() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        DBSyncResult syncResult = handler.synchronizeHeaders(1, Collections.emptyList());
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        assertTrue(getHeaderDAO().readAllHeaders().isEmpty());
    }

    @Test
    public void testSynchronizeGlobalHeadersDelete() {
        getHeaderDAO().insertHeader(getHeader1(-1));
        getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader2(1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        DBSyncResult syncResult = handler.synchronizeHeaders(-1, Collections.emptyList());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertTrue(getHeaderDAO().readGlobalHeaders().isEmpty());
        assertEquals(1, getHeaderDAO().readAllHeaders().size());
        assertTrue(getHeader2(1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(0)));
    }

    @Test
    public void testSynchronizeHeadersDelete() {
        getHeaderDAO().insertHeader(getHeader1(1));
        getHeaderDAO().insertHeader(getHeader2(1));
        getHeaderDAO().insertHeader(getHeader3(3));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        DBSyncResult syncResult = handler.synchronizeHeaders(1, Collections.emptyList());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertEquals(1, getHeaderDAO().readAllHeaders().size());
        assertTrue(getHeader3(3).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(0)));
    }

    @Test
    public void testSynchronizeGlobalHeadersAdd() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        List<Header> newHeaders = List.of(getHeader1(-1), getHeader2(-1), getHeader2(1));
        DBSyncResult syncResult = handler.synchronizeHeaders(-1, newHeaders);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertEquals(2, getHeaderDAO().readAllHeaders().size());
        assertTrue(getHeader1(-1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(0)));
        assertTrue(getHeader2(-1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(1)));
    }

    @Test
    public void testSynchronizeHeadersAdd() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        List<Header> newHeaders = List.of(getHeader1(1), getHeader2(1), getHeader2(3));
        DBSyncResult syncResult = handler.synchronizeHeaders(1, newHeaders);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertEquals(2, getHeaderDAO().readAllHeaders().size());
        assertTrue(getHeader1(1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(0)));
        assertTrue(getHeader2(1).isTechnicallyEqual(getHeaderDAO().readAllHeaders().get(1)));
    }

    @Test
    public void testSynchronizeGlobalHeadersNotUpdated() {
        Header header1 = getHeaderDAO().insertHeader(getHeader1(-1));
        Header header2 = getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader2(1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        List<Header> newHeaders = List.of(header1, header2);
        DBSyncResult syncResult = handler.synchronizeHeaders(-1, newHeaders);
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        List<Header> headers = getHeaderDAO().readAllHeaders();
        assertEquals(3, headers.size());
    }

    @Test
    public void testSynchronizeHeadersNotUpdated() {
        Header header1 = getHeaderDAO().insertHeader(getHeader1(1));
        Header header2 = getHeaderDAO().insertHeader(getHeader2(1));
        getHeaderDAO().insertHeader(getHeader2(-1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        List<Header> newHeaders = List.of(header1, header2);
        DBSyncResult syncResult = handler.synchronizeHeaders(1, newHeaders);
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        List<Header> headers = getHeaderDAO().readAllHeaders();
        assertEquals(3, headers.size());
    }

    @Test
    public void testSynchronizeGlobalHeadersUpdated() {
        Header header1 = getHeaderDAO().insertHeader(getHeader1(-1));
        Header header2 = getHeaderDAO().insertHeader(getHeader2(-1));
        getHeaderDAO().insertHeader(getHeader2(1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        header2.setName("name3");
        header2.setValue("value3");
        List<Header> newHeaders = List.of(header1, header2);
        DBSyncResult syncResult = handler.synchronizeHeaders(-1, newHeaders);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertEquals(3, getHeaderDAO().readAllHeaders().size());
        List<Header> headers = getHeaderDAO().readGlobalHeaders();
        assertTrue(headers.get(0).isTechnicallyEqual(getHeader1(-1)));
        assertTrue(headers.get(0).isTechnicallyEqual(header1));
        assertFalse(headers.get(1).isTechnicallyEqual(getHeader2(-1)));
        assertTrue(headers.get(1).isTechnicallyEqual(header2));
    }

    @Test
    public void testSynchronizeHeadersUpdated() {
        Header header1 = getHeaderDAO().insertHeader(getHeader1(1));
        Header header2 = getHeaderDAO().insertHeader(getHeader2(1));
        getHeaderDAO().insertHeader(getHeader2(-1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        header2.setName("name3");
        header2.setValue("value3");
        List<Header> newHeaders = List.of(header1, header2);
        DBSyncResult syncResult = handler.synchronizeHeaders(1, newHeaders);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertEquals(3, getHeaderDAO().readAllHeaders().size());
        List<Header> headers = getHeaderDAO().readHeadersForNetworkTask(1);
        assertTrue(headers.get(0).isTechnicallyEqual(getHeader1(1)));
        assertTrue(headers.get(0).isTechnicallyEqual(header1));
        assertFalse(headers.get(1).isTechnicallyEqual(getHeader2(1)));
        assertTrue(headers.get(1).isTechnicallyEqual(header2));
    }

    @Test
    public void testSynchronizeGlobalHeadersAddedUpdatedDeleted() {
        getHeaderDAO().insertHeader(getHeader2(-1));
        Header header3 = getHeaderDAO().insertHeader(getHeader3(-1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        header3.setName("anotherName");
        List<Header> newHeaders = List.of(getHeader1(-1), header3);
        DBSyncResult syncResult = handler.synchronizeHeaders(-1, newHeaders);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Header> headers = getHeaderDAO().readAllHeaders();
        assertEquals(2, headers.size());
        assertTrue(headers.get(0).isTechnicallyEqual(header3));
        assertTrue(headers.get(1).isTechnicallyEqual(getHeader1(-1)));
    }

    @Test
    public void testSynchronizeHeadersAddedUpdatedDeleted() {
        getHeaderDAO().insertHeader(getHeader2(1));
        Header header3 = getHeaderDAO().insertHeader(getHeader3(1));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
        header3.setName("anotherName");
        List<Header> newHeaders = List.of(getHeader1(1), header3);
        DBSyncResult syncResult = handler.synchronizeHeaders(1, newHeaders);
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Header> headers = getHeaderDAO().readAllHeaders();
        assertEquals(2, headers.size());
        assertTrue(headers.get(0).isTechnicallyEqual(header3));
        assertTrue(headers.get(1).isTechnicallyEqual(getHeader1(1)));
    }

    private Header getHeader1(long networktaskid) {
        Header header = new Header();
        header.setId(-1);
        header.setNetworkTaskId(networktaskid);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("name1");
        header.setValue("value1");
        header.setValueValid(true);
        return header;
    }

    private Header getHeader2(long networktaskid) {
        Header header = new Header();
        header.setId(-1);
        header.setNetworkTaskId(networktaskid);
        header.setHeaderType(HeaderType.BASICAUTH);
        header.setName("name2");
        header.setValue("value2");
        header.setValueValid(true);
        return header;
    }

    @SuppressWarnings("SameParameterValue")
    private Header getHeader3(long networktaskid) {
        Header header = new Header();
        header.setId(-1);
        header.setNetworkTaskId(networktaskid);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("name3");
        header.setValue("value3");
        header.setValueValid(true);
        return header;
    }
}
