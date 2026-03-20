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

package net.ibbaa.keepitup.test.mock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.db.HeaderDBConstants;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Header;

import java.util.HashMap;
import java.util.Map;

public class TestHeaderDAO extends HeaderDAO {

    public TestHeaderDAO(Context context) {
        super(context);
    }

    public Header insertHeaderUnencrypted(Header header) {
        return executeDBOperationInTransaction(header, this::insertHeaderUnencrypted);
    }

    public Map<String, String> readEncryptedValueAndValueIV(long id) {
        Header header = new Header();
        header.setId(id);
        return executeDBOperationInTransaction(header, this::readEncryptedValueAndValueIV);
    }

    private Header insertHeaderUnencrypted(Header header, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        values.put(dbConstants.getNetworkTaskIdColumnName(), header.getNetworkTaskId() < 0 ? null : header.getNetworkTaskId());
        values.put(dbConstants.getHeaderTypeColumnName(), header.getHeaderType() == null ? null : header.getHeaderType().getCode());
        values.put(dbConstants.getNameColumnName(), header.getName());
        values.putNull(dbConstants.getValueIVColumnName());
        values.put(dbConstants.getValueColumnName(), header.getValue());
        header.setValueValid(true);
        long rowid = db.insert(dbConstants.getTableName(), null, values);
        header.setId(rowid);
        return header;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private Map<String, String> readEncryptedValueAndValueIV(Header header, SQLiteDatabase db) {
        Map<String, String> result = new HashMap<>();
        Cursor cursor = null;
        HeaderDBConstants dbConstants = new HeaderDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getReadEncryptedValueAndValueIV(), new String[]{String.valueOf(header.getId())});
            while (cursor.moveToNext()) {
                int indexValueColumn = cursor.getColumnIndex(dbConstants.getValueColumnName());
                int indexIVColumn = cursor.getColumnIndex(dbConstants.getValueIVColumnName());
                result.put(dbConstants.getValueColumnName(), cursor.getString(indexValueColumn));
                result.put(dbConstants.getValueIVColumnName(), cursor.getString(indexIVColumn));
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable exc) {
                    Log.e(HeaderDAO.class.getName(), "Error closing result cursor", exc);
                }
            }
        }
        return result;
    }
}
