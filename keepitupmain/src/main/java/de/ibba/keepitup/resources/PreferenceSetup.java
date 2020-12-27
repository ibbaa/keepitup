package de.ibba.keepitup.resources;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.AccessType;

public class PreferenceSetup {

    private final PreferenceManager preferenceManager;

    public PreferenceSetup(Context context) {
        preferenceManager = new PreferenceManager(context);
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
        Log.d(PreferenceSetup.class.getName(), "importGlobalSetting");
        Object pingCount = globalSettings.get("preferencePingCount");
        if (pingCount instanceof Integer) {
            preferenceManager.setPreferencePingCount((Integer) pingCount);
        } else {
            preferenceManager.removePreferencePingCount();
        }
        Object connectCount = globalSettings.get("preferenceConnectCount");
        if (connectCount instanceof Integer) {
            preferenceManager.setPreferenceConnectCount((Integer) connectCount);
        } else {
            preferenceManager.removePreferenceConnectCount();
        }
        Object notificationInactiveNetwork = globalSettings.get("preferenceNotificationInactiveNetwork");
        if (notificationInactiveNetwork instanceof Boolean) {
            preferenceManager.setPreferenceNotificationInactiveNetwork((Boolean) notificationInactiveNetwork);
        } else {
            preferenceManager.removePreferenceNotificationInactiveNetwork();
        }
        Object downloadExternalStorage = globalSettings.get("preferenceDownloadExternalStorage");
        if (downloadExternalStorage instanceof Boolean) {
            preferenceManager.setPreferenceDownloadExternalStorage((Boolean) downloadExternalStorage);
        } else {
            preferenceManager.removePreferenceDownloadExternalStorage();
        }
        Object externalStorageType = globalSettings.get("preferenceExternalStorageType");
        if (externalStorageType instanceof Integer) {
            preferenceManager.setPreferenceExternalStorageType((Integer) externalStorageType);
        } else {
            preferenceManager.removePreferenceExternalStorageType();
        }
        Object downloadFolder = globalSettings.get("preferenceDownloadFolder");
        if (downloadFolder instanceof String) {
            preferenceManager.setPreferenceDownloadFolder((String) downloadFolder);
        } else {
            preferenceManager.removePreferenceDownloadFolder();
        }
        Object downloadKeep = globalSettings.get("preferenceDownloadKeep");
        if (downloadKeep instanceof Boolean) {
            preferenceManager.setPreferenceDownloadKeep((Boolean) downloadKeep);
        } else {
            preferenceManager.removePreferenceDownloadKeep();
        }
    }

    public void importDefaults(Map<String, ?> defaults) {
        Log.d(PreferenceSetup.class.getName(), "importDefaults");
        Object accessType = defaults.get("preferenceAccessType");
        if (accessType instanceof Integer) {
            preferenceManager.setPreferenceAccessType(AccessType.forCode((Integer) accessType));
        } else {
            preferenceManager.removePreferenceAccessType();
        }
        Object address = defaults.get("preferenceAddress");
        if (address instanceof String) {
            preferenceManager.setPreferenceAddress((String) address);
        } else {
            preferenceManager.removePreferenceAddress();
        }
        Object port = defaults.get("preferencePort");
        if (port instanceof Integer) {
            preferenceManager.setPreferencePort((Integer) port);
        } else {
            preferenceManager.removePreferencePort();
        }
        Object interval = defaults.get("preferenceInterval");
        if (interval instanceof Integer) {
            preferenceManager.setPreferenceInterval((Integer) interval);
        } else {
            preferenceManager.removePreferenceInterval();
        }
        Object onlyWifi = defaults.get("preferenceOnlyWifi");
        if (onlyWifi instanceof Boolean) {
            preferenceManager.setPreferenceOnlyWifi((Boolean) onlyWifi);
        } else {
            preferenceManager.removePreferenceOnlyWifi();
        }
        Object notification = defaults.get("preferenceNotification");
        if (notification instanceof Boolean) {
            preferenceManager.setPreferenceNotification((Boolean) notification);
        } else {
            preferenceManager.removePreferenceNotification();
        }
    }

    public void importSystemSettings(Map<String, ?> systemSettings) {
        Log.d(PreferenceSetup.class.getName(), "importSystemSettings");
        Object importFolder = systemSettings.get("preferenceImportFolder");
        if (importFolder instanceof String) {
            preferenceManager.setPreferenceImportFolder((String) importFolder);
        } else {
            preferenceManager.removePreferenceImportFolder();
        }
        Object exportFolder = systemSettings.get("preferenceExportFolder");
        if (exportFolder instanceof String) {
            preferenceManager.setPreferenceExportFolder((String) exportFolder);
        } else {
            preferenceManager.removePreferenceExportFolder();
        }
        Object fileLoggerEnabled = systemSettings.get("preferenceFileLoggerEnabled");
        if (fileLoggerEnabled instanceof Boolean) {
            preferenceManager.setPreferenceFileLoggerEnabled((Boolean) fileLoggerEnabled);
        } else {
            preferenceManager.removePreferenceFileLoggerEnabled();
        }
        Object fileDumpEnabled = systemSettings.get("preferenceFileDumpEnabled");
        if (fileDumpEnabled instanceof Boolean) {
            preferenceManager.setPreferenceFileDumpEnabled((Boolean) fileDumpEnabled);
        } else {
            preferenceManager.removePreferenceFileDumpEnabled();
        }
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
}
