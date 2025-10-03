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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Syncable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class DBSyncHandlerTest {

    private DBSyncHandler<SyncTest> syncHandler;

    @Before
    public void beforeEachTestMethod() {
        syncHandler = new DBSyncHandler<>();
    }

    @Test
    public void testRetrieveSyncListEmpty() {
        assertTrue(syncHandler.retrieveSyncList(Collections.emptyList(), Collections.emptyList()).isEmpty());
        List<SyncTest> newList = new ArrayList<>();
        newList.add(new SyncTest(1, ""));
        newList.add(new SyncTest(2, ""));
        assertTrue(syncHandler.retrieveSyncList(newList, Collections.emptyList()).isEmpty());
    }

    @Test
    public void testRetrieveSyncListDelete() {
        assertTrue(syncHandler.retrieveSyncList(Collections.emptyList(), Collections.emptyList()).isEmpty());
        List<SyncTest> newList = new ArrayList<>();
        List<SyncTest> oldList = new ArrayList<>();
        oldList.add(new SyncTest(1, "data"));
        oldList.add(new SyncTest(2, "data"));
        List<DBSyncHandler.ActionWrapper<SyncTest>> result = syncHandler.retrieveSyncList(newList, oldList);
        assertEquals(2, result.size());
        assertEquals(DBSyncHandler.Action.DELETE, result.get(0).action());
        assertEquals(DBSyncHandler.Action.DELETE, result.get(1).action());
        assertEquals("data", result.get(0).object().data());
        assertEquals("data", result.get(1).object().data());
    }

    @Test
    public void testRetrieveSyncListInsert() {
        assertTrue(syncHandler.retrieveSyncList(Collections.emptyList(), Collections.emptyList()).isEmpty());
        List<SyncTest> newList = new ArrayList<>();
        List<SyncTest> oldList = new ArrayList<>();
        newList.add(new SyncTest(-1, "data"));
        newList.add(new SyncTest(-1, "data"));
        List<DBSyncHandler.ActionWrapper<SyncTest>> result = syncHandler.retrieveSyncList(newList, oldList);
        assertEquals(2, result.size());
        assertEquals(DBSyncHandler.Action.INSERT, result.get(0).action());
        assertEquals(DBSyncHandler.Action.INSERT, result.get(1).action());
        assertEquals("data", result.get(0).object().data());
        assertEquals("data", result.get(1).object().data());
    }

    @Test
    @SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
    public void testRetrieveSyncListUpdate() {
        assertTrue(syncHandler.retrieveSyncList(Collections.emptyList(), Collections.emptyList()).isEmpty());
        List<SyncTest> newList = new ArrayList<>();
        List<SyncTest> oldList = new ArrayList<>();
        oldList.add(new SyncTest(1, "data"));
        oldList.add(new SyncTest(2, "data"));
        newList.add(new SyncTest(1, "data1"));
        newList.add(new SyncTest(2, "data"));
        List<DBSyncHandler.ActionWrapper<SyncTest>> result = syncHandler.retrieveSyncList(newList, oldList);
        assertEquals(1, result.size());
        assertEquals(DBSyncHandler.Action.UPDATE, result.get(0).action());
        assertEquals(1, result.get(0).object().id());
        assertEquals("data1", result.get(0).object().data());
    }

    @Test
    public void testRetrieveSyncListInsertUpdateDelete() {
        assertTrue(syncHandler.retrieveSyncList(Collections.emptyList(), Collections.emptyList()).isEmpty());
        List<SyncTest> newList = new ArrayList<>();
        List<SyncTest> oldList = new ArrayList<>();
        oldList.add(new SyncTest(1, "data1"));
        oldList.add(new SyncTest(2, "data2"));
        oldList.add(new SyncTest(3, "data3"));
        oldList.add(new SyncTest(4, "data4"));
        newList.add(new SyncTest(1, "data1"));
        newList.add(new SyncTest(2, "newData"));
        newList.add(new SyncTest(-1, "newData"));
        List<DBSyncHandler.ActionWrapper<SyncTest>> result = syncHandler.retrieveSyncList(newList, oldList);
        assertEquals(4, result.size());
        assertEquals(DBSyncHandler.Action.UPDATE, Objects.requireNonNull(getById(2, result)).action());
        assertEquals("newData", Objects.requireNonNull(getById(2, result)).object().data());
        assertEquals(DBSyncHandler.Action.INSERT, Objects.requireNonNull(getById(-1, result)).action());
        assertEquals("newData", Objects.requireNonNull(getById(-1, result)).object().data());
        assertEquals(DBSyncHandler.Action.DELETE, Objects.requireNonNull(getById(3, result)).action());
        assertEquals("data3", Objects.requireNonNull(getById(3, result)).object().data());
        assertEquals(DBSyncHandler.Action.DELETE, Objects.requireNonNull(getById(4, result)).action());
        assertEquals("data4", Objects.requireNonNull(getById(4, result)).object().data());
    }

    private DBSyncHandler.ActionWrapper<SyncTest> getById(long id, List<DBSyncHandler.ActionWrapper<SyncTest>> objects) {
        for (DBSyncHandler.ActionWrapper<SyncTest> object : objects) {
            if (id == object.object().id()) {
                return object;
            }
        }
        return null;
    }

    private record SyncTest(long id, String data) implements Syncable<SyncTest> {

        @Override
        public boolean isEqual(SyncTest other) {
            return id == other.id && data.equals(other.data);
        }
    }
}
