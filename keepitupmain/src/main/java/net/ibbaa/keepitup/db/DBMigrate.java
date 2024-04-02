/*
 * Copyright (c) 2024. Alwin Ibba
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

import net.ibbaa.keepitup.logging.Log;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class DBMigrate {

    private final DBSetup setup;
    private final SortedMap<Integer, Migration> versionUpgrades;
    private final SortedMap<Integer, Migration> versionDowngrades;

    public DBMigrate(DBSetup setup) {
        this.setup = setup;
        this.versionUpgrades = new TreeMap<>();
        this.versionDowngrades = new TreeMap<>();
        versionUpgrades.put(2, this::version2UpgradeFrom1);
        versionDowngrades.put(2, this::version2DowngradeTo1);
    }

    public void doUpgrade(Context context, int oldVersion, int newVersion) {
        doUpgrade(DBOpenHelper.getInstance(context).getWritableDatabase(), oldVersion, newVersion);
    }

    public void doDowngrade(Context context, int oldVersion, int newVersion) {
        doDowngrade(DBOpenHelper.getInstance(context).getWritableDatabase(), oldVersion, newVersion);
    }

    public void doUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBMigrate.class.getName(), "doUpgrade from version " + oldVersion + " to " + newVersion);
        int version = oldVersion + 1;
        while (versionUpgrades.containsKey(version) && version <= newVersion) {
            Migration upgrade = versionUpgrades.get(version);
            Objects.requireNonNull(upgrade).migrate(db);
            version++;
        }
    }

    public void doDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(DBMigrate.class.getName(), "doDowngrade from version " + oldVersion + " to " + newVersion);
        int version = oldVersion;
        while (versionUpgrades.containsKey(version) && version > newVersion) {
            Migration downgrade = versionDowngrades.get(version);
            Objects.requireNonNull(downgrade).migrate(db);
            version--;
        }
    }

    private void version2UpgradeFrom1(SQLiteDatabase db) {
        Log.d(DBMigrate.class.getName(), "version2UpgradeFrom1");
        setup.recreateIntervalTable(db);
        setup.recreateSchedulerStateTable(db);
    }

    private void version2DowngradeTo1(SQLiteDatabase db) {
        Log.d(DBMigrate.class.getName(), "version2DowngradeTo1");
        setup.dropIntervalTable(db);
        setup.dropSchedulerStateTable(db);
    }

    @FunctionalInterface
    private interface Migration {
        void migrate(SQLiteDatabase db);
    }
}
