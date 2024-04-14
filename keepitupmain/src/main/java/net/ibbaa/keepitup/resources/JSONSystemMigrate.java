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

package net.ibbaa.keepitup.resources;

import android.content.Context;

import net.ibbaa.keepitup.logging.Log;

import org.json.JSONObject;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class JSONSystemMigrate {

    private final Context context;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private final SortedMap<Integer, JSONSystemMigrate.Migration> versionAdaptBefore;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private final SortedMap<Integer, JSONSystemMigrate.Migration> versionAdaptAfterDatabase;
    private final SortedMap<Integer, JSONSystemMigrate.Migration> versionAdaptAfter;

    public JSONSystemMigrate(Context context) {
        this.context = context;
        this.versionAdaptBefore = new TreeMap<>();
        this.versionAdaptAfterDatabase = new TreeMap<>();
        this.versionAdaptAfter = new TreeMap<>();
        versionAdaptAfter.put(3, this::version3AdaptAfterFrom0);
    }

    public void adaptBefore(JSONObject root, int oldVersion, int newVersion) {
        Log.d(JSONSystemMigrate.class.getName(), "adaptBefore with oldVersion = " + oldVersion + " and newVersion = " + newVersion);
        int version = oldVersion + 1;
        while (version <= newVersion) {
            if (versionAdaptBefore.containsKey(version)) {
                JSONSystemMigrate.Migration upgrade = versionAdaptBefore.get(version);
                Objects.requireNonNull(upgrade).migrate(root);
            }
            version++;
        }
    }

    public void adaptAfterDatabase(JSONObject root, int oldVersion, int newVersion) {
        Log.d(JSONSystemMigrate.class.getName(), "adaptAfterDatabase with oldVersion = " + oldVersion + " and newVersion = " + newVersion);
        int version = oldVersion + 1;
        while (version <= newVersion) {
            if (versionAdaptAfterDatabase.containsKey(version)) {
                JSONSystemMigrate.Migration upgrade = versionAdaptAfterDatabase.get(version);
                Objects.requireNonNull(upgrade).migrate(root);
            }
            version++;
        }
    }

    public void adaptAfter(JSONObject root, int oldVersion, int newVersion) {
        Log.d(JSONSystemMigrate.class.getName(), "adaptAfter with oldVersion = " + oldVersion + " and newVersion = " + newVersion);
        int version = oldVersion + 1;
        while (version <= newVersion) {
            if (versionAdaptAfter.containsKey(version)) {
                JSONSystemMigrate.Migration upgrade = versionAdaptAfter.get(version);
                Objects.requireNonNull(upgrade).migrate(root);
            }
            version++;
        }
    }

    private void version3AdaptAfterFrom0(JSONObject root) {
        Log.d(JSONSystemMigrate.class.getName(), "version3UpgradeFrom0");
    }

    @FunctionalInterface
    private interface Migration {
        @SuppressWarnings({"unused"})
        void migrate(JSONObject root);
    }
}
