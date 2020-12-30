package de.ibba.keepitup.resources;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.util.NumberUtil;
import de.ibba.keepitup.util.URLUtil;

public class PreferenceSetup {

    private final Context context;
    private final PreferenceManager preferenceManager;

    public PreferenceSetup(Context context) {
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
    }

    public Map<String, ?> exportGlobalSettings() {
        Log.d(PreferenceSetup.class.getName(), "exportGlobalSetting");
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferencePingCount", preferenceManager.getPreferencePingCount());
        globalSettings.put("preferenceConnectCount", preferenceManager.getPreferenceConnectCount());
        globalSettings.put("preferenceNotificationInactiveNetwork", preferenceManager.getPreferenceNotificationInactiveNetwork());
        globalSettings.put("preferenceDownloadExternalStorage", preferenceManager.getPreferenceDownloadExternalStorage());
        globalSettings.put("preferenceExternalStorageType", preferenceManager.getPreferenceExternalStorageType());
        globalSettings.put("preferenceDownloadFolder", preferenceManager.getPreferenceDownloadFolder());
        globalSettings.put("preferenceDownloadKeep", preferenceManager.getPreferenceDownloadKeep());
        return globalSettings;
    }

    public Map<String, ?> exportDefaults() {
        Log.d(PreferenceSetup.class.getName(), "exportDefaults");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", preferenceManager.getPreferenceAccessType() != null ? preferenceManager.getPreferenceAccessType().getCode() : -1);
        defaults.put("preferenceAddress", preferenceManager.getPreferenceAddress());
        defaults.put("preferencePort", preferenceManager.getPreferencePort());
        defaults.put("preferenceInterval", preferenceManager.getPreferenceInterval());
        defaults.put("preferenceOnlyWifi", preferenceManager.getPreferenceOnlyWifi());
        defaults.put("preferenceNotification", preferenceManager.getPreferenceNotification());
        return defaults;
    }

    public Map<String, ?> exportSystemSettings() {
        Log.d(PreferenceSetup.class.getName(), "exportSystemSettings");
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", preferenceManager.getPreferenceImportFolder());
        systemSettings.put("preferenceExportFolder", preferenceManager.getPreferenceExportFolder());
        systemSettings.put("preferenceFileLoggerEnabled", preferenceManager.getPreferenceFileLoggerEnabled());
        systemSettings.put("preferenceFileDumpEnabled", preferenceManager.getPreferenceFileDumpEnabled());
        return systemSettings;
    }

    public void importGlobalSettings(Map<String, ?> globalSettings) {
        Log.d(PreferenceSetup.class.getName(), "importGlobalSetting, globalSettings = " + globalSettings);
        Object pingCount = globalSettings.get("preferencePingCount");
        int pingCountMin = getResources().getInteger(R.integer.ping_count_minimum);
        int pingCountMax = getResources().getInteger(R.integer.ping_count_maximum);
        int pingCountDefault = getResources().getInteger(R.integer.ping_count_default);
        if (isValidInteger(pingCount, pingCountMin, pingCountMax)) {
            preferenceManager.setPreferencePingCount(NumberUtil.getIntValue(pingCount, pingCountDefault));
        } else {
            preferenceManager.removePreferencePingCount();
        }
        Object connectCount = globalSettings.get("preferenceConnectCount");
        int connectCountMin = getResources().getInteger(R.integer.connect_count_minimum);
        int connectCountMax = getResources().getInteger(R.integer.connect_count_maximum);
        int connectCountDefault = getResources().getInteger(R.integer.connect_count_default);
        if (isValidInteger(connectCount, connectCountMin, connectCountMax)) {
            preferenceManager.setPreferenceConnectCount(NumberUtil.getIntValue(connectCount, connectCountDefault));
        } else {
            preferenceManager.removePreferenceConnectCount();
        }
        Object notificationInactiveNetwork = globalSettings.get("preferenceNotificationInactiveNetwork");
        if (isValidBoolean(notificationInactiveNetwork)) {
            preferenceManager.setPreferenceNotificationInactiveNetwork(Boolean.parseBoolean(notificationInactiveNetwork.toString()));
        } else {
            preferenceManager.removePreferenceNotificationInactiveNetwork();
        }
        Object downloadExternalStorage = globalSettings.get("preferenceDownloadExternalStorage");
        if (isValidBoolean(downloadExternalStorage)) {
            preferenceManager.setPreferenceDownloadExternalStorage(Boolean.parseBoolean(downloadExternalStorage.toString()));
        } else {
            preferenceManager.removePreferenceDownloadExternalStorage();
        }
        Object externalStorageType = globalSettings.get("preferenceExternalStorageType");
        if (isValidInteger(externalStorageType, 0, 1)) {
            preferenceManager.setPreferenceExternalStorageType(NumberUtil.getIntValue(externalStorageType, 0));
        } else {
            preferenceManager.removePreferenceExternalStorageType();
        }
        Object downloadFolder = globalSettings.get("preferenceDownloadFolder");
        if (isValidString(downloadFolder)) {
            preferenceManager.setPreferenceDownloadFolder(downloadFolder.toString());
        } else {
            preferenceManager.removePreferenceDownloadFolder();
        }
        Object downloadKeep = globalSettings.get("preferenceDownloadKeep");
        if (isValidBoolean(downloadKeep)) {
            preferenceManager.setPreferenceDownloadKeep(Boolean.parseBoolean(Objects.requireNonNull(downloadExternalStorage).toString()));
        } else {
            preferenceManager.removePreferenceDownloadKeep();
        }
    }

    public void importDefaults(Map<String, ?> defaults) {
        Log.d(PreferenceSetup.class.getName(), "importDefaults, defaults = " + defaults);
        Object accessType = defaults.get("preferenceAccessType");
        if (isValidAccessType(accessType)) {
            preferenceManager.setPreferenceAccessType(Objects.requireNonNull(AccessType.forCode(NumberUtil.getIntValue(accessType, -1))));
        } else {
            preferenceManager.removePreferenceAccessType();
        }
        Object address = defaults.get("preferenceAddress");
        if (isValidAddress(address)) {
            preferenceManager.setPreferenceAddress(address.toString());
        } else {
            preferenceManager.removePreferenceAddress();
        }
        Object port = defaults.get("preferencePort");
        int portMin = getResources().getInteger(R.integer.task_port_minimum);
        int portMax = getResources().getInteger(R.integer.task_port_maximum);
        int portDefault = getResources().getInteger(R.integer.task_port_default);
        if (isValidInteger(port, portMin, portMax)) {
            preferenceManager.setPreferencePort(NumberUtil.getIntValue(port, portDefault));
        } else {
            preferenceManager.removePreferencePort();
        }
        Object interval = defaults.get("preferenceInterval");
        int intervalMin = getResources().getInteger(R.integer.task_interval_minimum);
        int intervalMax = getResources().getInteger(R.integer.task_interval_maximum);
        int intervalDefault = getResources().getInteger(R.integer.task_interval_default);
        if (isValidInteger(interval, intervalMin, intervalMax)) {
            preferenceManager.setPreferenceInterval(NumberUtil.getIntValue(interval, intervalDefault));
        } else {
            preferenceManager.removePreferenceInterval();
        }
        Object onlyWifi = defaults.get("preferenceOnlyWifi");
        if (isValidBoolean(onlyWifi)) {
            preferenceManager.setPreferenceOnlyWifi(Boolean.parseBoolean(onlyWifi.toString()));
        } else {
            preferenceManager.removePreferenceOnlyWifi();
        }
        Object notification = defaults.get("preferenceNotification");
        if (isValidBoolean(notification)) {
            preferenceManager.setPreferenceNotification(Boolean.parseBoolean(notification.toString()));
        } else {
            preferenceManager.removePreferenceNotification();
        }
    }

    public void importSystemSettings(Map<String, ?> systemSettings) {
        Log.d(PreferenceSetup.class.getName(), "importSystemSettings, systemSettings = " + systemSettings);
        Object importFolder = systemSettings.get("preferenceImportFolder");
        if (isValidString(importFolder)) {
            preferenceManager.setPreferenceImportFolder(importFolder.toString());
        } else {
            preferenceManager.removePreferenceImportFolder();
        }
        Object exportFolder = systemSettings.get("preferenceExportFolder");
        if (isValidString(exportFolder)) {
            preferenceManager.setPreferenceExportFolder(exportFolder.toString());
        } else {
            preferenceManager.removePreferenceExportFolder();
        }
        Object fileLoggerEnabled = systemSettings.get("preferenceFileLoggerEnabled");
        if (isValidBoolean(fileLoggerEnabled)) {
            preferenceManager.setPreferenceFileLoggerEnabled(Boolean.parseBoolean(fileLoggerEnabled.toString()));
        } else {
            preferenceManager.removePreferenceFileLoggerEnabled();
        }
        Object fileDumpEnabled = systemSettings.get("preferenceFileDumpEnabled");
        if (isValidBoolean(fileDumpEnabled)) {
            preferenceManager.setPreferenceFileDumpEnabled(Boolean.parseBoolean(fileDumpEnabled.toString()));
        } else {
            preferenceManager.removePreferenceFileDumpEnabled();
        }
    }

    private boolean isValidInteger(Object value, int min, int max) {
        if (!NumberUtil.isValidIntValue(value)) {
            return false;
        }
        int intValue = NumberUtil.getIntValue(value, -1);
        return intValue >= min && intValue <= max;
    }

    private boolean isValidBoolean(Object value) {
        return value != null;
    }

    private boolean isValidString(Object value) {
        return value != null;
    }

    private boolean isValidAddress(Object value) {
        if (value == null || value.toString().isEmpty()) {
            return false;
        }
        return URLUtil.isValidIPAddress(value.toString()) || URLUtil.isValidHostName(value.toString()) || URLUtil.isValidURL(value.toString());
    }

    private boolean isValidAccessType(Object value) {
        if (!NumberUtil.isValidIntValue(value)) {
            return false;
        }
        return AccessType.forCode(NumberUtil.getIntValue(value, -1)) != null;
    }

    public void removeGlobalSettings() {
        Log.d(PreferenceSetup.class.getName(), "removeGlobalSettings");
        preferenceManager.removePreferencePingCount();
        preferenceManager.removePreferenceConnectCount();
        preferenceManager.removePreferenceNotificationInactiveNetwork();
        preferenceManager.removePreferenceDownloadExternalStorage();
        preferenceManager.removePreferenceExternalStorageType();
        preferenceManager.removePreferenceDownloadFolder();
        preferenceManager.removePreferenceDownloadKeep();
    }

    public void removeDefaults() {
        Log.d(PreferenceSetup.class.getName(), "removeDefaults");
        preferenceManager.removePreferenceAccessType();
        preferenceManager.removePreferenceAddress();
        preferenceManager.removePreferencePort();
        preferenceManager.removePreferenceInterval();
        preferenceManager.removePreferenceOnlyWifi();
        preferenceManager.removePreferenceNotification();
    }

    public void removeSystemSettings() {
        Log.d(PreferenceSetup.class.getName(), "removeSystemSettings");
        preferenceManager.removePreferenceImportFolder();
        preferenceManager.removePreferenceExportFolder();
        preferenceManager.removePreferenceFileLoggerEnabled();
        preferenceManager.removePreferenceFileDumpEnabled();
    }

    public void removeAllSettings() {
        Log.d(PreferenceSetup.class.getName(), "removeAllSettings");
        preferenceManager.removeAllPreferences();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
