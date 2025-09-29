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

package net.ibbaa.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.JSONUtil;
import net.ibbaa.keepitup.util.NumberUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONSystemSetup {

    private final Context context;
    private final DBSetup dbSetup;
    private final JSONSystemMigrate jsonMigrate;
    private final PreferenceSetup preferenceSetup;

    public JSONSystemSetup(Context context) {
        this.context = context;
        this.dbSetup = new DBSetup(context);
        this.jsonMigrate = new JSONSystemMigrate(context);
        this.preferenceSetup = new PreferenceSetup(context);
    }

    public SystemSetupResult exportData() {
        Log.d(JSONSystemSetup.class.getName(), "exportData");
        JSONObject root = new JSONObject();
        String versionKey = getResources().getString(R.string.version_json_key);
        String dbVersionKey = getResources().getString(R.string.dbversion_json_key);
        String dbKey = getResources().getString(R.string.database_json_key);
        String settingsKey = getResources().getString(R.string.preferences_json_key);
        try {
            root.put(versionKey, BuildConfig.VERSION_CODE);
            root.put(dbVersionKey, getResources().getInteger(R.integer.db_version));
            JSONObject dbData = exportDatabase();
            root.put(dbKey, dbData);
            Log.d(JSONSystemSetup.class.getName(), "Successfully exported database: " + dbData);
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error exporting database", exc);
            return new SystemSetupResult(false, false, exc.getMessage(), root.toString());
        }
        try {
            JSONObject settings = exportSettings();
            root.put(settingsKey, settings);
            Log.d(JSONSystemSetup.class.getName(), "Successfully exported settings: " + settings);
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error exporting settings", exc);
            return new SystemSetupResult(false, false, exc.getMessage(), root.toString());
        }
        return new SystemSetupResult(true, false, "Successful export", root.toString());
    }

    public SystemSetupResult importData(String data) {
        Log.d(JSONSystemSetup.class.getName(), "importData for data " + data);
        JSONObject root;
        try {
            root = new JSONObject(data);
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error importing data", exc);
            return new SystemSetupResult(false, false, exc.getMessage(), data);
        }
        if (isFileVersionHigherThanCurrentVersion(root)) {
            Log.e(JSONSystemSetup.class.getName(), "Version mismatch on import");
            return new SystemSetupResult(false, true, "Version mismatch on import", data);
        }
        String dbKey = getResources().getString(R.string.database_json_key);
        String settingsKey = getResources().getString(R.string.preferences_json_key);
        try {
            int versionInFile = getJSONVersion(root);
            int dbVersion = getDbVersion();
            jsonMigrate.adaptBefore(root, versionInFile, dbVersion);
            importDatabase((JSONObject) root.get(dbKey));
            jsonMigrate.adaptAfterDatabase(root, versionInFile, dbVersion);
            Log.d(JSONSystemSetup.class.getName(), "Successfully imported database.");
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error importing database", exc);
            return new SystemSetupResult(false, false, exc.getMessage(), data);
        }
        try {
            int versionInFile = getJSONVersion(root);
            int dbVersion = getDbVersion();
            importSettings((JSONObject) root.get(settingsKey));
            jsonMigrate.adaptAfter(root, versionInFile, dbVersion);
            Log.d(JSONSystemSetup.class.getName(), "Successfully imported settings.");
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error importing settings", exc);
            return new SystemSetupResult(false, false, exc.getMessage(), data);
        }
        return new SystemSetupResult(true, false, "Successful import", data);
    }

    public SystemSetupResult checkImportPossible(String data) {
        Log.d(JSONSystemSetup.class.getName(), "checkImportPossible for data " + data);
        JSONObject root;
        try {
            root = new JSONObject(data);
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error on checking import data", exc);
            return new SystemSetupResult(false, false, exc.getMessage(), data);
        }
        if (isFileVersionHigherThanCurrentVersion(root)) {
            Log.e(JSONSystemSetup.class.getName(), "Version mismatch on import");
            return new SystemSetupResult(false, true, "Version mismatch on import", data);
        }
        return new SystemSetupResult(true, false, "Import possible", data);
    }

    private boolean isFileVersionHigherThanCurrentVersion(JSONObject root) {
        Log.d(JSONSystemSetup.class.getName(), "isVersionValid");
        try {
            int versionInFile = getJSONVersion(root);
            int dbVersion = getDbVersion();
            return versionInFile > dbVersion;
        } catch (Exception exc) {
            Log.e(JSONSystemSetup.class.getName(), "Error checking version on import", exc);
            return true;
        }
    }

    private void importDatabase(JSONObject dbData) throws JSONException {
        Log.d(JSONSystemSetup.class.getName(), "importDatabase for data " + dbData);
        String intervalKey = getResources().getString(R.string.interval_json_key);
        Iterator<String> keys = dbData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (intervalKey.equals(key)) {
                importIntervals(dbData, key);
            } else {
                importNetworkTask(dbData, key);
            }
        }
        dbSetup.normalizeUIIndex();
    }

    private void importNetworkTask(JSONObject dbData, String key) throws JSONException {
        String taskKey = getResources().getString(R.string.networktask_json_key);
        String logKey = getResources().getString(R.string.logentry_json_key);
        String accessTypeDataKey = getResources().getString(R.string.accesstypedata_json_key);
        String resolveKey = getResources().getString(R.string.resolve_json_key);
        JSONObject taskData = (JSONObject) dbData.get(key);
        if (taskData.has(taskKey)) {
            Map<String, ?> taskMap = JSONUtil.toMap((JSONObject) taskData.get(taskKey));
            List<?> logList = taskData.has(logKey) ? JSONUtil.toList((JSONArray) taskData.get(logKey)) : Collections.emptyList();
            Map<String, ?> dataMap = taskData.has(accessTypeDataKey) ? JSONUtil.toMap((JSONObject) taskData.get(accessTypeDataKey)) : null;
            Map<String, ?> resolveMap = taskData.has(resolveKey) ? JSONUtil.toMap((JSONObject) taskData.get(resolveKey)) : null;
            dbSetup.importNetworkTaskWithLogsAccessTypeDataAndResolve(taskMap, filterList(logList), dataMap, resolveMap);
        }
    }

    private void importIntervals(JSONObject dbData, String key) throws JSONException {
        List<?> intervalList = dbData.has(key) ? JSONUtil.toList((JSONArray) dbData.get(key)) : Collections.emptyList();
        dbSetup.importIntervals(filterList(intervalList));
    }

    private void importSettings(JSONObject settings) throws JSONException {
        Log.d(JSONSystemSetup.class.getName(), "importSettings for settings " + settings);
        String globalSettingsKey = getResources().getString(R.string.preferences_global_json_key);
        String defaultsKey = getResources().getString(R.string.preferences_defaults_json_key);
        String systemSettingsKey = getResources().getString(R.string.preferences_system_json_key);
        preferenceSetup.importGlobalSettings(settings.has(globalSettingsKey) ? JSONUtil.toMap((JSONObject) settings.get(globalSettingsKey)) : Collections.emptyMap());
        preferenceSetup.importDefaults(settings.has(defaultsKey) ? JSONUtil.toMap((JSONObject) settings.get(defaultsKey)) : Collections.emptyMap());
        preferenceSetup.importSystemSettings(settings.has(systemSettingsKey) ? JSONUtil.toMap((JSONObject) settings.get(systemSettingsKey)) : Collections.emptyMap());
    }

    @SuppressWarnings({"unchecked"})
    private List<Map<String, ?>> filterList(List<?> list) {
        Log.d(JSONSystemSetup.class.getName(), "filterLogList");
        List<Map<String, ?>> filteredList = new ArrayList<>();
        for (Object log : list) {
            if (log instanceof Map) {
                filteredList.add((Map<String, ?>) log);
            }
        }
        return filteredList;
    }

    private JSONObject exportDatabase() throws JSONException {
        Log.d(JSONSystemSetup.class.getName(), "exportDatabase");
        JSONObject dbData = new JSONObject();
        List<Map<String, ?>> networkTasks = dbSetup.exportNetworkTasks();
        for (Map<String, ?> taskMap : networkTasks) {
            long id = getId(taskMap);
            if (id >= 0) {
                List<Map<String, ?>> logs = dbSetup.exportLogsForNetworkTask(id);
                Map<String, ?> acccessTypeDataMap = dbSetup.exportAccessTypeDataForNetworkTask(id);
                Map<String, ?> resolveMap = dbSetup.exportResolveForNetworkTask(id);
                JSONObject task = getJSONObjectForNetworkTask(taskMap, logs, acccessTypeDataMap, resolveMap);
                dbData.put(String.valueOf(id), task);
            }
        }
        List<Map<String, ?>> intervals = dbSetup.exportIntervals();
        String intervalKey = getResources().getString(R.string.interval_json_key);
        dbData.put(intervalKey, new JSONArray(intervals));
        return dbData;
    }

    private JSONObject exportSettings() throws JSONException {
        Log.d(JSONSystemSetup.class.getName(), "exportSettings");
        JSONObject settings = new JSONObject();
        String globalSettingsKey = getResources().getString(R.string.preferences_global_json_key);
        String defaultsKey = getResources().getString(R.string.preferences_defaults_json_key);
        String systemSettingsKey = getResources().getString(R.string.preferences_system_json_key);
        Map<String, ?> globalSettings = preferenceSetup.exportGlobalSettings();
        Map<String, ?> defaults = preferenceSetup.exportDefaults();
        Map<String, ?> systemSettings = preferenceSetup.exportSystemSettings();
        settings.put(globalSettingsKey, new JSONObject(globalSettings));
        settings.put(defaultsKey, new JSONObject(defaults));
        settings.put(systemSettingsKey, new JSONObject(systemSettings));
        return settings;
    }

    private long getId(Map<String, ?> map) {
        if (map.containsKey("id")) {
            return NumberUtil.getLongValue(map.get("id"), -1);
        }
        return -1;
    }

    private JSONObject getJSONObjectForNetworkTask(Map<String, ?> taskMap, List<Map<String, ?>> logs, Map<String, ?> accessTypeDataMap, Map<String, ?> resolveMap) throws JSONException {
        Log.d(JSONSystemSetup.class.getName(), "getJSONObjectForNetworkTask");
        JSONObject task = new JSONObject();
        String taskKey = getResources().getString(R.string.networktask_json_key);
        String logKey = getResources().getString(R.string.logentry_json_key);
        String accessTypeDataKey = getResources().getString(R.string.accesstypedata_json_key);
        String resolveKey = getResources().getString(R.string.resolve_json_key);
        task.put(taskKey, new JSONObject(taskMap));
        task.put(logKey, new JSONArray(logs));
        if (accessTypeDataMap != null) {
            task.put(accessTypeDataKey, new JSONObject(accessTypeDataMap));
        }
        if (resolveMap != null) {
            task.put(resolveKey, new JSONObject(resolveMap));
        }
        return task;
    }

    private int getJSONVersion(JSONObject root) throws JSONException {
        String dbVersionKey = getResources().getString(R.string.dbversion_json_key);
        if (root.has(dbVersionKey)) {
            return root.getInt(dbVersionKey);
        }
        return 0;
    }

    private int getDbVersion() {
        return getResources().getInteger(R.integer.db_version);
    }


    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
