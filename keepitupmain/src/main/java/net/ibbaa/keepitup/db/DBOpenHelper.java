/*
 * Copyright (c) 2021. Alwin Ibba
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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;

public class DBOpenHelper extends SQLiteOpenHelper {

    private final DBSetup setup;

    private static DBOpenHelper dbOpenHelper;

    private DBOpenHelper(Context context) {
        super(context, context.getResources().getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        setup = new DBSetup(context);
    }

    public static synchronized DBOpenHelper getInstance(Context context) {
        if (dbOpenHelper == null) {
            dbOpenHelper = new DBOpenHelper(context);
        }
        return dbOpenHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(DBOpenHelper.class.getName(), "onCreate");
        setup.createTables(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onUpgrade");
        setup.recreateTables(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBOpenHelper.class.getName(), "onDowngrade");
        onUpgrade(db, oldVersion, newVersion);
    }
}
