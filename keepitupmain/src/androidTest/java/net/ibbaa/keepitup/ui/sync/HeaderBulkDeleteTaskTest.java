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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.util.ThreadUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Future;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class HeaderBulkDeleteTaskTest {

    private HeaderDAO headerDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        headerDAO = new HeaderDAO(TestRegistry.getContext());
        headerDAO.deleteAllHeaders();
    }

    @After
    public void afterEachTestMethod() {
        headerDAO.deleteAllHeaders();
    }

    @Test
    public void testDelete() throws Exception {
        Header header1 = getHeader(-1, 1);
        Header header2 = getHeader(-1, 2);
        Header header3 = getHeader(1, 3);
        Header header4 = getHeader(1, 4);
        headerDAO.insertHeaders(List.of(header1, header2, header3, header4));
        HeaderBulkDeleteTask task = new HeaderBulkDeleteTask(TestRegistry.getContext(), List.of(header1, header2, header3));
        Future<Header> future = ThreadUtil.execute(task);
        future.get();
        List<Header> readHeaders = headerDAO.readAllHeaders();
        assertEquals(1, readHeaders.size());
        assertEquals("name4", readHeaders.get(0).getName());
    }

    private Header getHeader(int networkTaskId, int number) {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(networkTaskId);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("name" + number);
        header.setValue("value" + number);
        header.setValueValid(true);
        return header;
    }
}
