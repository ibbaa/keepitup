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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.ibbaa.keepitup.db.AccessTypeDataDAO;
import net.ibbaa.keepitup.db.AccessTypeDataDBConstants;
import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessTypeData;

import java.util.HashMap;
import java.util.Map;

public class TestAccessTypeDataDAO extends AccessTypeDataDAO {

    public TestAccessTypeDataDAO(Context context) {
        super(context);
    }

    public Map<String, String> readEncryptedCommunityAndCommunityIV(long id) {
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setId(id);
        return executeDBOperationInTransaction(accessTypeData, this::readEncryptedCommunityAndCommunityIV);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private Map<String, String> readEncryptedCommunityAndCommunityIV(AccessTypeData accessTypeData, SQLiteDatabase db) {
        Map<String, String> result = new HashMap<>();
        Cursor cursor = null;
        AccessTypeDataDBConstants dbConstants = new AccessTypeDataDBConstants(getContext());
        try {
            cursor = db.rawQuery(dbConstants.getReadEncryptedCommunityAndCommunityIV(), new String[]{String.valueOf(accessTypeData.getId())});
            while (cursor.moveToNext()) {
                int indexCommunityColumn = cursor.getColumnIndex(dbConstants.getSnmpCommunityColumnName());
                int indexIVColumn = cursor.getColumnIndex(dbConstants.getSnmpCommunityIVColumnName());
                result.put(dbConstants.getSnmpCommunityColumnName(), cursor.getString(indexCommunityColumn));
                result.put(dbConstants.getSnmpCommunityIVColumnName(), cursor.getString(indexIVColumn));
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
