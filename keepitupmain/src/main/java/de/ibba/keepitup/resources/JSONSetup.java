package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.DBSetup;
import de.ibba.keepitup.util.JSONUtil;
import de.ibba.keepitup.util.NumberUtil;

public class JSONSetup {

    private final Context context;
    private final DBSetup dbSetup;
    private final PreferenceSetup preferenceSetup;

    public JSONSetup(Context context) {
        this.context = context;
        this.dbSetup = new DBSetup(context);
        this.preferenceSetup = new PreferenceSetup(context);
    }

    public JSONObject export() throws JSONException {
        JSONObject root = new JSONObject();
        JSONObject dbData = exportDatabase();
        JSONObject settings = exportSettings();
        String dbKey = getResources().getString(R.string.database_json_key);
        String settingsKey = getResources().getString(R.string.preferences_json_key);
        root.put(dbKey, dbData);
        root.put(settingsKey, settings);
        return root;
    }

    private JSONObject exportDatabase() throws JSONException {
        JSONObject dbData = new JSONObject();
        List<Map<String, ?>> networkTasks = dbSetup.exportNetworkTasks(getContext());
        for (Map<String, ?> taskMap : networkTasks) {
            long id = getId(taskMap);
            if (id >= 0) {
                List<Map<String, ?>> logs = dbSetup.exportLogsForNetworkTask(getContext(), id);
                JSONObject task = getJSONObjectForNetworkTask(taskMap, logs);
                dbData.put(String.valueOf(id), task);
            }
        }
        return dbData;
    }

    private JSONObject exportSettings() throws JSONException {
        JSONObject settings = new JSONObject();
        String globalSettingsKey = getResources().getString(R.string.preferences_global_json_key);
        String defaultsKey = getResources().getString(R.string.preferences_defaults_json_key);
        String systemSettingsKey = getResources().getString(R.string.preferences_system_json_key);
        Map<String, ?> globalSettings = preferenceSetup.exportGlobalSettings();
        Map<String, ?> defaults = preferenceSetup.exportDefaults();
        Map<String, ?> systemSettings = preferenceSetup.exportSystemSettings();
        settings.put(globalSettingsKey, JSONUtil.toJSONObject(globalSettings));
        settings.put(defaultsKey, JSONUtil.toJSONObject(defaults));
        settings.put(systemSettingsKey, JSONUtil.toJSONObject(systemSettings));
        return settings;
    }

    private long getId(Map<String, ?> map) {
        if (map.containsKey("id")) {
            return NumberUtil.getLongValue(map.get("id"), -1);
        }
        return -1;
    }

    private JSONObject getJSONObjectForNetworkTask(Map<String, ?> taskMap, List<Map<String, ?>> logs) throws JSONException {
        JSONObject task = new JSONObject();
        String taskKey = getResources().getString(R.string.networktask_json_key);
        String logKey = getResources().getString(R.string.logentry_json_key);
        task.put(taskKey, JSONUtil.toJSONObject(taskMap));
        task.put(logKey, JSONUtil.toJSONArray(logs));
        return task;
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
