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

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        globalSettings.put("preferenceNotificationInactiveNetwork", preferenceManager.getPreferenceNotificationInactiveNetwork());
        globalSettings.put("preferenceNotificationType", preferenceManager.getPreferenceNotificationType() != null ? preferenceManager.getPreferenceNotificationType().getCode() : 1);
        globalSettings.put("preferenceNotificationAfterFailures", preferenceManager.getPreferenceNotificationAfterFailures());
        globalSettings.put("preferenceSuspensionEnabled", preferenceManager.getPreferenceSuspensionEnabled());
        globalSettings.put("preferenceEnforceDefaultPingPackageSize", preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        globalSettings.put("preferenceDownloadExternalStorage", preferenceManager.getPreferenceDownloadExternalStorage());
        globalSettings.put("preferenceDownloadFolder", preferenceManager.getPreferenceDownloadFolder());
        globalSettings.put("preferenceArbitraryDownloadFolder", preferenceManager.getPreferenceArbitraryDownloadFolder());
        globalSettings.put("preferenceDownloadKeep", preferenceManager.getPreferenceDownloadKeep());
        globalSettings.put("preferenceDownloadFollowsRedirects", preferenceManager.getPreferenceDownloadFollowsRedirects());
        globalSettings.put("preferenceHTTPUserAgent", preferenceManager.getPreferenceHTTPUserAgent());
        globalSettings.put("preferenceLogFile", preferenceManager.getPreferenceLogFile());
        globalSettings.put("preferenceLogFolder", preferenceManager.getPreferenceLogFolder());
        globalSettings.put("preferenceArbitraryLogFolder", preferenceManager.getPreferenceArbitraryLogFolder());
        return globalSettings;
    }

    public Map<String, ?> exportDefaults() {
        Log.d(PreferenceSetup.class.getName(), "exportDefaults");
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", preferenceManager.getPreferenceAccessType() != null ? preferenceManager.getPreferenceAccessType().getCode() : -1);
        defaults.put("preferenceAddress", preferenceManager.getPreferenceAddress());
        defaults.put("preferencePort", preferenceManager.getPreferencePort());
        defaults.put("preferenceInterval", preferenceManager.getPreferenceInterval());
        defaults.put("preferencePingCount", preferenceManager.getPreferencePingCount());
        defaults.put("preferenceConnectCount", preferenceManager.getPreferenceConnectCount());
        defaults.put("preferenceStopOnSuccess", preferenceManager.getPreferenceStopOnSuccess());
        defaults.put("preferenceIgnoreSSLError", preferenceManager.getPreferenceIgnoreSSLError());
        defaults.put("preferenceOnlyWifi", preferenceManager.getPreferenceOnlyWifi());
        defaults.put("preferenceNotification", preferenceManager.getPreferenceNotification());
        defaults.put("preferenceHighPrio", preferenceManager.getPreferenceHighPrio());
        defaults.put("preferencePingPackageSize", preferenceManager.getPreferencePingPackageSize());
        return defaults;
    }

    public Map<String, ?> exportSystemSettings() {
        Log.d(PreferenceSetup.class.getName(), "exportSystemSettings");
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceExternalStorageType", preferenceManager.getPreferenceExternalStorageType());
        systemSettings.put("preferenceImportFolder", preferenceManager.getPreferenceImportFolder());
        systemSettings.put("preferenceExportFolder", preferenceManager.getPreferenceExportFolder());
        systemSettings.put("preferenceLastArbitraryExportFile", preferenceManager.getPreferenceLastArbitraryExportFile());
        systemSettings.put("preferenceFileLoggerEnabled", preferenceManager.getPreferenceFileLoggerEnabled());
        systemSettings.put("preferenceFileDumpEnabled", preferenceManager.getPreferenceFileDumpEnabled());
        systemSettings.put("preferenceTheme", preferenceManager.getPreferenceTheme());
        systemSettings.put("preferenceAllowArbitraryFileLocation", preferenceManager.getPreferenceAllowArbitraryFileLocation());
        systemSettings.put("preferenceAlarmOnHighPrio", preferenceManager.getPreferenceAlarmOnHighPrio());
        systemSettings.put("preferenceAskedNotificationPermission", preferenceManager.getPreferenceAskedNotificationPermission());
        systemSettings.put("preferenceAlarmInfoShown", preferenceManager.getPreferenceAlarmInfoShown());
        return systemSettings;
    }

    public void importGlobalSettings(Map<String, ?> globalSettings) {
        Log.d(PreferenceSetup.class.getName(), "importGlobalSetting, globalSettings = " + globalSettings);
        Object notificationInactiveNetwork = globalSettings.get("preferenceNotificationInactiveNetwork");
        if (isValidBoolean(notificationInactiveNetwork)) {
            preferenceManager.setPreferenceNotificationInactiveNetwork(Boolean.parseBoolean(notificationInactiveNetwork.toString()));
        } else {
            preferenceManager.removePreferenceNotificationInactiveNetwork();
        }
        Object notificationType = globalSettings.get("preferenceNotificationType");
        if (isValidNotificationType(notificationType)) {
            preferenceManager.setPreferenceNotificationType(Objects.requireNonNull(NotificationType.forCode(NumberUtil.getIntValue(notificationType, 1))));
        } else {
            preferenceManager.removePreferenceNotificationType();
        }
        Object notificationAfterFailures = globalSettings.get("preferenceNotificationAfterFailures");
        int notificationAfterFailuresMin = getResources().getInteger(R.integer.notification_after_failures_minimum);
        int notificationAfterFailuresMax = getResources().getInteger(R.integer.notification_after_failures_maximum);
        int notificationAfterFailuresDefault = getResources().getInteger(R.integer.notification_after_failures_default);
        if (isValidInteger(notificationAfterFailures, notificationAfterFailuresMin, notificationAfterFailuresMax)) {
            preferenceManager.setPreferenceNotificationAfterFailures(NumberUtil.getIntValue(notificationAfterFailures, notificationAfterFailuresDefault));
        } else {
            preferenceManager.removePreferenceNotificationAfterFailures();
        }
        Object suspensionEnabled = globalSettings.get("preferenceSuspensionEnabled");
        if (isValidBoolean(suspensionEnabled)) {
            preferenceManager.setPreferenceSuspensionEnabled(Boolean.parseBoolean(suspensionEnabled.toString()));
        } else {
            preferenceManager.removePreferenceSuspensionEnabled();
        }
        Object enforceDefaultPingPackageSize = globalSettings.get("preferenceEnforceDefaultPingPackageSize");
        if (isValidBoolean(enforceDefaultPingPackageSize)) {
            preferenceManager.setPreferenceEnforceDefaultPingPackageSize(Boolean.parseBoolean(enforceDefaultPingPackageSize.toString()));
        } else {
            preferenceManager.removePreferenceDownloadExternalStorage();
        }
        Object downloadExternalStorage = globalSettings.get("preferenceDownloadExternalStorage");
        if (isValidBoolean(downloadExternalStorage)) {
            preferenceManager.setPreferenceDownloadExternalStorage(Boolean.parseBoolean(downloadExternalStorage.toString()));
        } else {
            preferenceManager.removePreferenceEnforceDefaultPingPackageSize();
        }
        Object downloadFolder = globalSettings.get("preferenceDownloadFolder");
        if (isValidString(downloadFolder)) {
            preferenceManager.setPreferenceDownloadFolder(downloadFolder.toString());
        } else {
            preferenceManager.removePreferenceDownloadFolder();
        }
        Object arbitraryDownloadFolder = globalSettings.get("preferenceArbitraryDownloadFolder");
        if (isValidString(arbitraryDownloadFolder)) {
            preferenceManager.setPreferenceArbitraryDownloadFolder(arbitraryDownloadFolder.toString());
        } else {
            preferenceManager.removePreferenceArbitraryDownloadFolder();
        }
        Object downloadKeep = globalSettings.get("preferenceDownloadKeep");
        if (isValidBoolean(downloadKeep)) {
            preferenceManager.setPreferenceDownloadKeep(Boolean.parseBoolean(Objects.requireNonNull(downloadKeep).toString()));
        } else {
            preferenceManager.removePreferenceDownloadKeep();
        }
        Object downloadFollowsRedirects = globalSettings.get("preferenceDownloadFollowsRedirects");
        if (isValidBoolean(downloadFollowsRedirects)) {
            preferenceManager.setPreferenceDownloadFollowsRedirects(Boolean.parseBoolean(Objects.requireNonNull(downloadFollowsRedirects).toString()));
        } else {
            preferenceManager.removePreferenceDownloadFollowsRedirects();
        }
        Object httpUserAgent = globalSettings.get("preferenceHTTPUserAgent");
        if (isValidString(httpUserAgent, getResources().getInteger(R.integer.http_header_user_agent_max_length)) && !StringUtil.isEmpty(httpUserAgent.toString())) {
            preferenceManager.setPreferenceHTTPUserAgent(httpUserAgent.toString());
        } else {
            preferenceManager.removePreferenceHTTPUserAgent();
        }
        Object logFile = globalSettings.get("preferenceLogFile");
        if (isValidBoolean(logFile)) {
            preferenceManager.setPreferenceLogFile(Boolean.parseBoolean(logFile.toString()));
        } else {
            preferenceManager.removePreferenceLogFile();
        }
        Object logFolder = globalSettings.get("preferenceLogFolder");
        if (isValidString(logFolder)) {
            preferenceManager.setPreferenceLogFolder(logFolder.toString());
        } else {
            preferenceManager.removePreferenceLogFolder();
        }
        Object arbitraryLogFolder = globalSettings.get("preferenceArbitraryLogFolder");
        if (isValidString(arbitraryLogFolder)) {
            preferenceManager.setPreferenceArbitraryLogFolder(arbitraryLogFolder.toString());
        } else {
            preferenceManager.removePreferenceArbitraryLogFolder();
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
            preferenceManager.setPreferenceAddress(address.toString().trim());
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
        importPingAndConnectCount(defaults);
        Object stopOnSuccess = defaults.get("preferenceStopOnSuccess");
        if (isValidBoolean(stopOnSuccess)) {
            preferenceManager.setPreferenceStopOnSuccess(Boolean.parseBoolean(stopOnSuccess.toString()));
        } else {
            preferenceManager.removePreferenceStopOnSuccess();
        }
        Object ignoreSSLError = defaults.get("preferenceIgnoreSSLError");
        if (isValidBoolean(ignoreSSLError)) {
            preferenceManager.setPreferenceIgnoreSSLError(Boolean.parseBoolean(ignoreSSLError.toString()));
        } else {
            preferenceManager.removePreferenceIgnoreSSLError();
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
        Object highPrio = defaults.get("preferenceHighPrio");
        if (isValidBoolean(highPrio)) {
            preferenceManager.setPreferenceHighPrio(Boolean.parseBoolean(highPrio.toString()));
        } else {
            preferenceManager.removePreferenceHighPrio();
        }
        Object pingPackageSize = defaults.get("preferencePingPackageSize");
        int pingPackageSizeMin = getResources().getInteger(R.integer.ping_package_size_minimum);
        int pingPackageSizeMax = getResources().getInteger(R.integer.ping_package_size_maximum);
        int pingPackageSizeDefault = getResources().getInteger(R.integer.ping_package_size_default);
        if (isValidInteger(pingPackageSize, pingPackageSizeMin, pingPackageSizeMax)) {
            preferenceManager.setPreferencePingPackageSize(NumberUtil.getIntValue(pingPackageSize, pingPackageSizeDefault));
        } else {
            preferenceManager.removePreferencePingPackageSize();
        }
    }

    public void importPingAndConnectCount(Map<String, ?> map) {
        Log.d(PreferenceSetup.class.getName(), "importPingAndConnectCount, map = " + map);
        Object pingCount = map.get("preferencePingCount");
        int pingCountMin = getResources().getInteger(R.integer.ping_count_minimum);
        int pingCountMax = getResources().getInteger(R.integer.ping_count_maximum);
        int pingCountDefault = getResources().getInteger(R.integer.ping_count_default);
        if (isValidInteger(pingCount, pingCountMin, pingCountMax)) {
            preferenceManager.setPreferencePingCount(NumberUtil.getIntValue(pingCount, pingCountDefault));
        } else {
            preferenceManager.removePreferencePingCount();
        }
        Object connectCount = map.get("preferenceConnectCount");
        int connectCountMin = getResources().getInteger(R.integer.connect_count_minimum);
        int connectCountMax = getResources().getInteger(R.integer.connect_count_maximum);
        int connectCountDefault = getResources().getInteger(R.integer.connect_count_default);
        if (isValidInteger(connectCount, connectCountMin, connectCountMax)) {
            preferenceManager.setPreferenceConnectCount(NumberUtil.getIntValue(connectCount, connectCountDefault));
        } else {
            preferenceManager.removePreferenceConnectCount();
        }
    }

    public void importSystemSettings(Map<String, ?> systemSettings) {
        Log.d(PreferenceSetup.class.getName(), "importSystemSettings, systemSettings = " + systemSettings);
        Object externalStorageType = systemSettings.get("preferenceExternalStorageType");
        if (isValidInteger(externalStorageType, 0, 1)) {
            preferenceManager.setPreferenceExternalStorageType(NumberUtil.getIntValue(externalStorageType, 0));
        } else {
            preferenceManager.removePreferenceExternalStorageType();
        }
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
        Object lastArbitraryExportFile = systemSettings.get("preferenceLastArbitraryExportFile");
        if (isValidString(lastArbitraryExportFile)) {
            preferenceManager.setPreferenceLastArbitraryExportFile(lastArbitraryExportFile.toString());
        } else {
            preferenceManager.removePreferenceLastArbitraryExportFile();
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
        Object theme = systemSettings.get("preferenceTheme");
        if (isValidInteger(theme, -1, 2)) {
            preferenceManager.setPreferenceTheme(NumberUtil.getIntValue(theme, -1));
        } else {
            preferenceManager.removePreferenceTheme();
        }
        Object allowArbitraryFileLocation = systemSettings.get("preferenceAllowArbitraryFileLocation");
        if (isValidBoolean(allowArbitraryFileLocation)) {
            preferenceManager.setPreferenceAllowArbitraryFileLocation(Boolean.parseBoolean(allowArbitraryFileLocation.toString()));
        } else {
            preferenceManager.removePreferenceAllowArbitraryFileLocation();
        }
        Object alarmOnHighPrio = systemSettings.get("preferenceAlarmOnHighPrio");
        if (isValidBoolean(alarmOnHighPrio)) {
            preferenceManager.setPreferenceAlarmOnHighPrio(Boolean.parseBoolean(alarmOnHighPrio.toString()));
        } else {
            preferenceManager.removePreferenceAlarmOnHighPrio();
        }
        Object askedNotificationPermission = systemSettings.get("preferenceAskedNotificationPermission");
        if (isValidBoolean(askedNotificationPermission)) {
            preferenceManager.setPreferenceAskedNotificationPermission(Boolean.parseBoolean(askedNotificationPermission.toString()));
        } else {
            preferenceManager.removePreferenceAskedNotificationPermission();
        }
        Object alarmInfoShown = systemSettings.get("preferenceAlarmInfoShown");
        if (isValidBoolean(alarmInfoShown)) {
            preferenceManager.setPreferenceAlarmInfoShown(Boolean.parseBoolean(alarmInfoShown.toString()));
        } else {
            preferenceManager.removePreferenceAlarmInfoShown();
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
        return value != null && (value.toString().equals(Boolean.FALSE.toString()) || value.toString().equals(Boolean.TRUE.toString()));
    }

    private boolean isValidString(Object value) {
        return value != null;
    }

    private boolean isValidString(Object value, int maxLength) {
        return value != null && value.toString().length() <= maxLength;
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

    private boolean isValidNotificationType(Object value) {
        if (!NumberUtil.isValidIntValue(value)) {
            return false;
        }
        return NotificationType.forCode(NumberUtil.getIntValue(value, 1)) != null;
    }

    public void removeGlobalSettings() {
        Log.d(PreferenceSetup.class.getName(), "removeGlobalSettings");
        preferenceManager.removePreferenceNotificationInactiveNetwork();
        preferenceManager.removePreferenceNotificationType();
        preferenceManager.removePreferenceNotificationAfterFailures();
        preferenceManager.removePreferenceSuspensionEnabled();
        preferenceManager.removePreferenceEnforceDefaultPingPackageSize();
        preferenceManager.removePreferenceDownloadExternalStorage();
        preferenceManager.removePreferenceDownloadFolder();
        preferenceManager.removePreferenceArbitraryDownloadFolder();
        preferenceManager.removePreferenceDownloadKeep();
        preferenceManager.removePreferenceDownloadFollowsRedirects();
        preferenceManager.removePreferenceHTTPUserAgent();
        preferenceManager.removePreferenceLogFile();
        preferenceManager.removePreferenceLogFolder();
        preferenceManager.removePreferenceArbitraryLogFolder();
    }

    public void removeDefaults() {
        Log.d(PreferenceSetup.class.getName(), "removeDefaults");
        preferenceManager.removePreferenceAccessType();
        preferenceManager.removePreferenceAddress();
        preferenceManager.removePreferencePort();
        preferenceManager.removePreferenceInterval();
        preferenceManager.removePreferencePingCount();
        preferenceManager.removePreferenceConnectCount();
        preferenceManager.removePreferenceStopOnSuccess();
        preferenceManager.removePreferenceIgnoreSSLError();
        preferenceManager.removePreferencePingPackageSize();
        preferenceManager.removePreferenceOnlyWifi();
        preferenceManager.removePreferenceNotification();
        preferenceManager.removePreferenceHighPrio();
    }

    public void removeSystemSettings() {
        Log.d(PreferenceSetup.class.getName(), "removeSystemSettings");
        preferenceManager.removePreferenceExternalStorageType();
        preferenceManager.removePreferenceImportFolder();
        preferenceManager.removePreferenceExportFolder();
        preferenceManager.removePreferenceLastArbitraryExportFile();
        preferenceManager.removePreferenceFileLoggerEnabled();
        preferenceManager.removePreferenceFileDumpEnabled();
        preferenceManager.removePreferenceTheme();
        preferenceManager.removePreferenceAllowArbitraryFileLocation();
        preferenceManager.removePreferenceAlarmOnHighPrio();
        preferenceManager.removePreferenceAskedNotificationPermission();
        preferenceManager.removePreferenceAlarmInfoShown();
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
