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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.SchedulerId;

import java.util.ArrayList;
import java.util.List;

public class SchedulerIdHistoryDAO extends BaseDAO {

    public SchedulerIdHistoryDAO(Context context) {
        super(context);
    }

    public void deleteAllSchedulerIds() {
        Log.d(SchedulerIdHistoryDAO.class.getName(), "Deleting all scheduler ids");
        executeDBOperationInTransaction((NetworkTask) null, this::deleteAllSchedulerIds);
    }

    public List<SchedulerId> readAllSchedulerIds() {
        Log.d(SchedulerIdHistoryDAO.class.getName(), "Reading all scheduler ids");
        List<SchedulerId> returnedIds = executeDBOperationInTransaction((SchedulerId) null, this::readAllSchedulerIds);
        Log.d(SchedulerIdHistoryDAO.class.getName(), "Number of read scheduler ids: " + returnedIds.size());
        return returnedIds;
    }

    private int deleteAllSchedulerIds(NetworkTask networkTask, SQLiteDatabase db) {
        Log.d(SchedulerIdHistoryDAO.class.getName(), "deleteAllSchedulerIds, networkTask is " + networkTask);
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        return db.delete(dbConstants.getTableName(), null, null);
    }

    private List<SchedulerId> readAllSchedulerIds(SchedulerId schedulerId, SQLiteDatabase db) {
        Log.d(SchedulerIdHistoryDAO.class.getName(), "readAllSchedulerIds, schedulerId is " + schedulerId);
        Cursor cursor = null;
        List<SchedulerId> result = new ArrayList<>();
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        try {
            Log.d(SchedulerIdHistoryDAO.class.getName(), "Executing SQL " + dbConstants.getReadAllSchedulerIdHistoryEntriesStatement());
            cursor = db.rawQuery(dbConstants.getReadAllSchedulerIdHistoryEntriesStatement(), null);
            while (cursor.moveToNext()) {
                int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
                if (!cursor.isNull(indexIdColumn)) {
                    SchedulerId mappedSchedulerId = mapCursorToSchedulerId(cursor);
                    result.add(mappedSchedulerId);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(SchedulerIdHistoryDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        Log.d(SchedulerIdHistoryDAO.class.getName(), "readAllSchedulerIds, returning " + result);
        return result;
    }

    private SchedulerId mapCursorToSchedulerId(Cursor cursor) {
        SchedulerId schedulerId = new SchedulerId();
        SchedulerIdHistoryDBConstants dbConstants = new SchedulerIdHistoryDBConstants(getContext());
        int indexIdColumn = cursor.getColumnIndex(dbConstants.getIdColumnName());
        int indexSchedulerIdColumn = cursor.getColumnIndex(dbConstants.getSchedulerIdColumnName());
        int indexTimestampColumn = cursor.getColumnIndex(dbConstants.getTimestampColumnName());
        schedulerId.setId(cursor.getLong(indexIdColumn));
        schedulerId.setSchedulerId(cursor.getInt(indexSchedulerIdColumn));
        schedulerId.setTimestamp(cursor.getLong(indexTimestampColumn));
        return schedulerId;
    }
}
