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

package net.ibbaa.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
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
        versionAdaptAfter.put(6, this::version6AdaptAfterFrom3);
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
        Log.d(JSONSystemMigrate.class.getName(), "version3AdaptAfterFrom0");
        String settingsKey = getResources().getString(R.string.preferences_json_key);
        try {
            JSONObject globalSettings = getGlobalSettings(root, settingsKey);
            if (globalSettings == null) {
                Log.e(JSONSystemMigrate.class.getName(), "version3UpgradeFrom0, globalSettings is null, migration not possible.");
                return;
            }
            PreferenceSetup setup = new PreferenceSetup(getContext());
            Log.d(JSONSystemMigrate.class.getName(), "version3UpgradeFrom0, importing ping and connect count from global settings");
            setup.importPingAndConnectCount(JSONUtil.toMap(globalSettings));
        } catch (JSONException exc) {
            Log.e(JSONSystemMigrate.class.getName(), "Error on migrating version3UpgradeFrom0", exc);
        }
    }

    private void version6AdaptAfterFrom3(JSONObject root) {
        Log.d(JSONSystemMigrate.class.getName(), "version6AdaptAfterFrom3");
        String settingsKey = getResources().getString(R.string.preferences_json_key);
        try {
            JSONObject globalSettings = getGlobalSettings(root, settingsKey);
            if (globalSettings == null) {
                Log.e(JSONSystemMigrate.class.getName(), "version3UpgradeFrom0, globalSettings is null, migration not possible.");
            } else {
                Map<String, ?> globalSettingsMap = JSONUtil.toMap(globalSettings);
                Object userAgent = globalSettingsMap.get("preferenceHTTPUserAgent");
                if (userAgent != null && !userAgent.toString().isEmpty()) {
                    PreferenceManager preferenceManager = new PreferenceManager(getContext());
                    preferenceManager.setPreferenceString(getResources().getString(R.string.http_user_agent_key), userAgent.toString());
                }
            }
            DBSetup setup = new DBSetup(getContext());
            setup.deleteAllHeaders();
            setup.initializeHeaderTable();
        } catch (Exception exc) {
            Log.e(JSONSystemMigrate.class.getName(), "Error on migrating version3UpgradeFrom0", exc);
        }
    }

    private JSONObject getGlobalSettings(JSONObject root, String settingsKey) throws JSONException {
        if (!root.has(settingsKey)) {
            Log.e(JSONSystemMigrate.class.getName(), "getGlobalSettings, no key " + settingsKey + ", returning null");
            return null;
        }
        JSONObject settings = (JSONObject) root.get(settingsKey);
        String globalSettingsKey = getResources().getString(R.string.preferences_global_json_key);
        if (!settings.has(globalSettingsKey)) {
            Log.e(JSONSystemMigrate.class.getName(), "getGlobalSettings, no key " + globalSettingsKey + ", returning null");
            return null;
        }
        return (JSONObject) settings.get(globalSettingsKey);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    @FunctionalInterface
    private interface Migration {
        @SuppressWarnings({"unused"})
        void migrate(JSONObject root);
    }
}
