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
import android.content.SharedPreferences;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NotificationType;

import java.util.HashSet;
import java.util.Set;

public class PreferenceManager {

    private final Context context;

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public void removeAllPreferences() {
        Log.d(PreferenceManager.class.getName(), "removeAllPreferences");
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.clear();
        editor.commit();
    }

    public void removePreferenceValue(String key) {
        Log.d(PreferenceManager.class.getName(), "removePreferenceValue, key is " + key);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.remove(key);
        editor.commit();
    }

    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceBoolean, key is " + key + ", default is " + defaultValue);
        boolean value = getDefaultSharedPreferences().getBoolean(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceBoolean(String key, boolean value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceBoolean, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public int getPreferenceInt(String key, int defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceInt, key is " + key + ", default is " + defaultValue);
        int value = getDefaultSharedPreferences().getInt(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceInt(String key, int value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceInt, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    public long getPreferenceLong(String key, long defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceLong, key is " + key + ", default is " + defaultValue);
        long value = getDefaultSharedPreferences().getLong(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceLong(String key, long value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLong, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getPreferenceString(String key, String defaultValue) {
        Log.d(PreferenceManager.class.getName(), "getPreferenceString, key is " + key + ", default is " + defaultValue);
        String value = getDefaultSharedPreferences().getString(key, defaultValue);
        Log.d(PreferenceManager.class.getName(), "resolved value is " + value);
        return value;
    }

    public void setPreferenceString(String key, String value) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLong, key is " + key + ", value is " + value);
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor();
        editor.putString(key, value);
        editor.commit();
    }

    public AccessType getPreferenceAccessType() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceAccessType");
        return AccessType.forCode(getPreferenceInt(getResources().getString(R.string.task_accesstype_key), getResources().getInteger(R.integer.task_accesstype_default)));
    }

    public void setPreferenceAccessType(AccessType accessType) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceAccessType, type is " + accessType);
        setPreferenceInt(getResources().getString(R.string.task_accesstype_key), accessType.getCode());
    }

    public void removePreferenceAccessType() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceAccessType");
        removePreferenceValue(getResources().getString(R.string.task_accesstype_key));
    }

    public String getPreferenceAddress() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceAddress");
        return getPreferenceString(getResources().getString(R.string.task_address_key), getResources().getString(R.string.task_address_default));
    }

    public void setPreferenceAddress(String address) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceAddress, address is " + address);
        setPreferenceString(getResources().getString(R.string.task_address_key), address);
    }

    public void removePreferenceAddress() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceAddress");
        removePreferenceValue(getResources().getString(R.string.task_address_key));
    }

    public int getPreferencePort() {
        Log.d(PreferenceManager.class.getName(), "getPreferencePort");
        return getPreferenceInt(getResources().getString(R.string.task_port_key), getResources().getInteger(R.integer.task_port_default));
    }

    public void setPreferencePort(int port) {
        Log.d(PreferenceManager.class.getName(), "setPreferencePort, port is " + port);
        setPreferenceInt(getResources().getString(R.string.task_port_key), port);
    }

    public void removePreferencePort() {
        Log.d(PreferenceManager.class.getName(), "removePreferencePort");
        removePreferenceValue(getResources().getString(R.string.task_port_key));
    }

    public int getPreferenceInterval() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceInterval");
        return getPreferenceInt(getResources().getString(R.string.task_interval_key), getResources().getInteger(R.integer.task_interval_default));
    }

    public void setPreferenceInterval(int interval) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceInterval, interval is " + interval);
        setPreferenceInt(getResources().getString(R.string.task_interval_key), interval);
    }

    public void removePreferenceInterval() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceInterval");
        removePreferenceValue(getResources().getString(R.string.task_interval_key));
    }

    public boolean getPreferenceOnlyWifi() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceOnlyWifi");
        return getPreferenceBoolean(getResources().getString(R.string.task_onlywifi_key), getResources().getBoolean(R.bool.task_onlywifi_default));
    }

    public void setPreferenceOnlyWifi(boolean onlyWifi) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceOnlyWifi, onlyWifi is " + onlyWifi);
        setPreferenceBoolean(getResources().getString(R.string.task_onlywifi_key), onlyWifi);
    }

    public void removePreferenceOnlyWifi() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceOnlyWifi");
        removePreferenceValue(getResources().getString(R.string.task_onlywifi_key));
    }

    public boolean getPreferenceNotification() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceNotification");
        return getPreferenceBoolean(getResources().getString(R.string.task_notification_key), getResources().getBoolean(R.bool.task_notification_default));
    }

    public void setPreferenceNotification(boolean notification) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceNotification, notification is " + notification);
        setPreferenceBoolean(getResources().getString(R.string.task_notification_key), notification);
    }

    public void removePreferenceNotification() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceNotification");
        removePreferenceValue(getResources().getString(R.string.task_notification_key));
    }

    public int getPreferencePingCount() {
        Log.d(PreferenceManager.class.getName(), "getPreferencePingCount");
        return getPreferenceInt(getResources().getString(R.string.ping_count_key), getResources().getInteger(R.integer.ping_count_default));
    }

    public void setPreferencePingCount(int count) {
        Log.d(PreferenceManager.class.getName(), "setPreferencePingCount, count is " + count);
        setPreferenceInt(getResources().getString(R.string.ping_count_key), count);
    }

    public void removePreferencePingCount() {
        Log.d(PreferenceManager.class.getName(), "removePreferencePingCount");
        removePreferenceValue(getResources().getString(R.string.ping_count_key));
    }

    public int getPreferenceConnectCount() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceConnectCount");
        return getPreferenceInt(getResources().getString(R.string.connect_count_key), getResources().getInteger(R.integer.connect_count_default));
    }

    public void setPreferenceConnectCount(int count) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceConnectCount, count is " + count);
        setPreferenceInt(getResources().getString(R.string.connect_count_key), count);
    }

    public void removePreferenceConnectCount() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceConnectCount");
        removePreferenceValue(getResources().getString(R.string.connect_count_key));
    }

    public int getPreferencePingPackageSize() {
        Log.d(PreferenceManager.class.getName(), "getPreferencePingPackageSize");
        return getPreferenceInt(getResources().getString(R.string.ping_package_size_key), getResources().getInteger(R.integer.ping_package_size_default));
    }

    public void setPreferencePingPackageSize(int pingPackageSize) {
        Log.d(PreferenceManager.class.getName(), "setPreferencePingPackageSize, pingPackageSize is " + pingPackageSize);
        setPreferenceInt(getResources().getString(R.string.ping_package_size_key), pingPackageSize);
    }

    public void removePreferencePingPackageSize() {
        Log.d(PreferenceManager.class.getName(), "removePreferencePingPackageSize");
        removePreferenceValue(getResources().getString(R.string.ping_package_size_key));
    }

    public boolean getPreferenceStopOnSuccess() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceStopOnSuccess");
        return getPreferenceBoolean(getResources().getString(R.string.stop_on_success_key), getResources().getBoolean(R.bool.stop_on_success_default));
    }

    public void setPreferenceStopOnSuccess(boolean stopOnSuccess) {
        Log.d(PreferenceManager.class.getName(), "stopOnSuccess, stopOnSuccess is " + stopOnSuccess);
        setPreferenceBoolean(getResources().getString(R.string.stop_on_success_key), stopOnSuccess);
    }

    public void removePreferenceStopOnSuccess() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceStopOnSuccess");
        removePreferenceValue(getResources().getString(R.string.stop_on_success_key));
    }

    public NotificationType getPreferenceNotificationType() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceNotificationType");
        return NotificationType.forCode(getPreferenceInt(getResources().getString(R.string.notification_type_key), getResources().getInteger(R.integer.notification_type_default)));
    }

    public void setPreferenceNotificationType(NotificationType notificationType) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceNotificationType, type is " + notificationType);
        setPreferenceInt(getResources().getString(R.string.notification_type_key), notificationType.getCode());
    }

    public void removePreferenceNotificationType() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceNotificationType");
        removePreferenceValue(getResources().getString(R.string.notification_type_key));
    }

    public int getPreferenceNotificationAfterFailures() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceNotificationAfterFailures");
        return getPreferenceInt(getResources().getString(R.string.notification_after_failures_key), getResources().getInteger(R.integer.notification_after_failures_default));
    }

    public void setPreferenceNotificationAfterFailures(int notificationAfterFailures) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceNNotificationAfterFailures, notificationAfterFailures is " + notificationAfterFailures);
        setPreferenceInt(getResources().getString(R.string.notification_after_failures_key), notificationAfterFailures);
    }

    public void removePreferenceNotificationAfterFailures() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceNotificationAfterFailures");
        removePreferenceValue(getResources().getString(R.string.notification_after_failures_key));
    }

    public boolean getPreferenceSuspensionEnabled() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceSuspensionEnabled");
        return getPreferenceBoolean(getResources().getString(R.string.suspension_enabled_key), getResources().getBoolean(R.bool.suspension_enabled_default));
    }

    public void setPreferenceSuspensionEnabled(boolean suspensionEnabled) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceSuspensionEnabled, suspensionEnabled is " + suspensionEnabled);
        setPreferenceBoolean(getResources().getString(R.string.suspension_enabled_key), suspensionEnabled);
    }

    public void removePreferenceSuspensionEnabled() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceSuspensionEnabled");
        removePreferenceValue(getResources().getString(R.string.suspension_enabled_key));
    }

    public boolean getPreferenceNotificationInactiveNetwork() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceNotificationInactiveNetwork");
        return getPreferenceBoolean(getResources().getString(R.string.notification_inactive_network_key), getResources().getBoolean(R.bool.notification_inactive_network_default));
    }

    public void setPreferenceNotificationInactiveNetwork(boolean notification) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceNotificationInactiveNetwork, notification is " + notification);
        setPreferenceBoolean(getResources().getString(R.string.notification_inactive_network_key), notification);
    }

    public void removePreferenceNotificationInactiveNetwork() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceNotificationInactiveNetwork");
        removePreferenceValue(getResources().getString(R.string.notification_inactive_network_key));
    }

    public boolean getPreferenceEnforceDefaultPingPackageSize() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceEnforceDefaultPingPackageSize");
        return getPreferenceBoolean(getResources().getString(R.string.enforce_default_ping_package_size_key), getResources().getBoolean(R.bool.enforce_default_ping_package_size_default));
    }

    public void setPreferenceEnforceDefaultPingPackageSize(boolean enforceDefaultPingPackageSize) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceEnforceDefaultPingPackageSize, enforceDefaultPingPackageSize is " + enforceDefaultPingPackageSize);
        setPreferenceBoolean(getResources().getString(R.string.enforce_default_ping_package_size_key), enforceDefaultPingPackageSize);
    }

    public void removePreferenceEnforceDefaultPingPackageSize() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceEnforceDefaultPingPackageSize");
        removePreferenceValue(getResources().getString(R.string.enforce_default_ping_package_size_key));
    }

    public boolean getPreferenceDownloadExternalStorage() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceDownloadExternalStorage");
        return getPreferenceBoolean(getResources().getString(R.string.download_external_storage_key), getResources().getBoolean(R.bool.download_external_storage_default));
    }

    public void setPreferenceDownloadExternalStorage(boolean downloadExternalStorage) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceDownloadExternalStorage, downloadExternalStorage is " + downloadExternalStorage);
        setPreferenceBoolean(getResources().getString(R.string.download_external_storage_key), downloadExternalStorage);
    }

    public void removePreferenceDownloadExternalStorage() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceDownloadExternalStorage");
        removePreferenceValue(getResources().getString(R.string.download_external_storage_key));
    }

    public int getPreferenceExternalStorageType() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceExternalStorageType");
        return getPreferenceInt(getResources().getString(R.string.external_storage_type_key), getResources().getInteger(R.integer.external_storage_type_default));
    }

    public void setPreferenceExternalStorageType(int externalStorageType) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceExternalStorageType, externalStorageType is " + externalStorageType);
        setPreferenceInt(getResources().getString(R.string.external_storage_type_key), externalStorageType);
    }

    public void removePreferenceExternalStorageType() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceExternalStorageType");
        removePreferenceValue(getResources().getString(R.string.external_storage_type_key));
    }

    public String getPreferenceDownloadFolder() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceDownloadFolder");
        return getPreferenceString(getResources().getString(R.string.download_folder_key), getResources().getString(R.string.download_folder_default));
    }

    public void setPreferenceDownloadFolder(String downloadFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceDownloadFolder, downloadFolder is " + downloadFolder);
        setPreferenceString(getResources().getString(R.string.download_folder_key), downloadFolder);
    }

    public void removePreferenceDownloadFolder() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceDownloadFolder");
        removePreferenceValue(getResources().getString(R.string.download_folder_key));
    }

    public String getPreferenceArbitraryDownloadFolder() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceArbitraryDownloadFolder");
        return getPreferenceString(getResources().getString(R.string.arbitrary_download_folder_key), getResources().getString(R.string.arbitrary_download_folder_default));
    }

    public void setPreferenceArbitraryDownloadFolder(String downloadFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceArbitraryDownloadFolder, downloadFolder is " + downloadFolder);
        setPreferenceString(getResources().getString(R.string.arbitrary_download_folder_key), downloadFolder);
    }

    public void removePreferenceArbitraryDownloadFolder() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceArbitraryDownloadFolder");
        removePreferenceValue(getResources().getString(R.string.arbitrary_download_folder_key));
    }

    public boolean getPreferenceLogFile() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceLogFile");
        return getPreferenceBoolean(getResources().getString(R.string.log_file_key), getResources().getBoolean(R.bool.log_file_default));
    }

    public void setPreferenceLogFile(boolean logFile) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLogFile, logFile is " + logFile);
        setPreferenceBoolean(getResources().getString(R.string.log_file_key), logFile);
    }

    public void removePreferenceLogFile() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceLogFile");
        removePreferenceValue(getResources().getString(R.string.log_file_key));
    }

    public String getPreferenceLogFolder() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceLogFolder");
        return getPreferenceString(getResources().getString(R.string.log_folder_key), getResources().getString(R.string.log_folder_default));
    }

    public void setPreferenceLogFolder(String logFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLogFolder, logFolder is " + logFolder);
        setPreferenceString(getResources().getString(R.string.log_folder_key), logFolder);
    }

    public void removePreferenceLogFolder() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceLogFolder");
        removePreferenceValue(getResources().getString(R.string.log_folder_key));
    }

    public String getPreferenceArbitraryLogFolder() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceArbitraryLogFolder");
        return getPreferenceString(getResources().getString(R.string.arbitrary_log_folder_key), getResources().getString(R.string.arbitrary_log_folder_default));
    }

    public void setPreferenceArbitraryLogFolder(String logFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceArbitraryLogFolder, logFolder is " + logFolder);
        setPreferenceString(getResources().getString(R.string.arbitrary_log_folder_key), logFolder);
    }

    public void removePreferenceArbitraryLogFolder() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceArbitraryLogFolder");
        removePreferenceValue(getResources().getString(R.string.arbitrary_log_folder_key));
    }

    public boolean getPreferenceDownloadKeep() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceDownloadKeep");
        return getPreferenceBoolean(getResources().getString(R.string.download_keep_key), getResources().getBoolean(R.bool.download_keep_default));
    }

    public void setPreferenceDownloadKeep(boolean downloadKeep) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceDownloadKeep, downloadKeep is " + downloadKeep);
        setPreferenceBoolean(getResources().getString(R.string.download_keep_key), downloadKeep);
    }

    public void removePreferenceDownloadKeep() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceDownloadKeep");
        removePreferenceValue(getResources().getString(R.string.download_keep_key));
    }

    public boolean getPreferenceDownloadFollowsRedirects() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceDownloadFollowsRedirects");
        return getPreferenceBoolean(getResources().getString(R.string.download_follows_redirects_key), getResources().getBoolean(R.bool.download_follows_redirects_default));
    }

    public void setPreferenceDownloadFollowsRedirects(boolean downloadFollowsRedirects) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceDownloadFollowsRedirects, downloadFollowsRedirects is " + downloadFollowsRedirects);
        setPreferenceBoolean(getResources().getString(R.string.download_follows_redirects_key), downloadFollowsRedirects);
    }

    public void removePreferenceDownloadFollowsRedirects() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceDownloadFollowsRedirects");
        removePreferenceValue(getResources().getString(R.string.download_follows_redirects_key));
    }

    public String getPreferenceImportFolder() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceImportFolder");
        return getPreferenceString(getResources().getString(R.string.import_folder_key), getResources().getString(R.string.import_folder_default));
    }

    public void setPreferenceImportFolder(String importFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceImportFolder, importFolder is " + importFolder);
        setPreferenceString(getResources().getString(R.string.import_folder_key), importFolder);
    }

    public void removePreferenceImportFolder() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceImportFolder");
        removePreferenceValue(getResources().getString(R.string.import_folder_key));
    }

    public String getPreferenceExportFolder() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceExportFolder");
        return getPreferenceString(getResources().getString(R.string.export_folder_key), getResources().getString(R.string.export_folder_default));
    }

    public void setPreferenceExportFolder(String exportFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceExportFolder, importFolder is " + exportFolder);
        setPreferenceString(getResources().getString(R.string.export_folder_key), exportFolder);
    }

    public void removePreferenceExportFolder() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceExportFolder");
        removePreferenceValue(getResources().getString(R.string.export_folder_key));
    }

    public String getPreferenceLastArbitraryExportFile() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceLastArbitraryExportFile");
        return getPreferenceString(getResources().getString(R.string.last_arbitrary_export_file_key), getResources().getString(R.string.last_arbitrary_export_file_default));
    }

    public void setPreferenceLastArbitraryExportFile(String exportFolder) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceLastArbitraryExportFile, exportFolder is " + exportFolder);
        setPreferenceString(getResources().getString(R.string.last_arbitrary_export_file_key), exportFolder);
    }

    public void removePreferenceLastArbitraryExportFile() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceLastArbitraryExportFile");
        removePreferenceValue(getResources().getString(R.string.last_arbitrary_export_file_key));
    }

    public int getPreferenceTheme() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceTheme");
        return getPreferenceInt(getResources().getString(R.string.theme_key), getResources().getInteger(R.integer.theme_default));
    }

    public void setPreferenceTheme(int theme) {
        Log.d(PreferenceManager.class.getName(), "setTheme, theme is " + theme);
        setPreferenceInt(getResources().getString(R.string.theme_key), theme);
    }

    public void removePreferenceTheme() {
        Log.d(PreferenceManager.class.getName(), "removeTheme");
        removePreferenceValue(getResources().getString(R.string.theme_key));
    }

    public boolean getPreferenceAllowArbitraryFileLocation() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceAllowArbitraryFileLocation");
        return getPreferenceBoolean(getResources().getString(R.string.allow_arbitrary_file_location_key), getResources().getBoolean(R.bool.allow_arbitrary_file_location_default));
    }

    public void setPreferenceAllowArbitraryFileLocation(boolean allowArbitraryFileLocation) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceAllowArbitraryFileLocation, allowArbitraryFileLocation is " + allowArbitraryFileLocation);
        setPreferenceBoolean(getResources().getString(R.string.allow_arbitrary_file_location_key), allowArbitraryFileLocation);
    }

    public void removePreferenceAllowArbitraryFileLocation() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceAllowArbitraryFileLocation");
        removePreferenceValue(getResources().getString(R.string.allow_arbitrary_file_location_key));
    }

    public boolean getPreferenceFileLoggerEnabled() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceFileLoggerEnabled");
        return getPreferenceBoolean(getResources().getString(R.string.file_logger_enabled_key), getResources().getBoolean(R.bool.file_logger_enabled_default));
    }

    public void setPreferenceFileLoggerEnabled(boolean fileLoggerEnabled) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceFileLoggerEnabled, fileLoggerEnabled is " + fileLoggerEnabled);
        setPreferenceBoolean(getResources().getString(R.string.file_logger_enabled_key), fileLoggerEnabled);
    }

    public void removePreferenceFileLoggerEnabled() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceFileLoggerEnabled");
        removePreferenceValue(getResources().getString(R.string.file_logger_enabled_key));
    }

    public boolean getPreferenceFileDumpEnabled() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceFileDumpEnabled");
        return getPreferenceBoolean(getResources().getString(R.string.file_dump_enabled_key), getResources().getBoolean(R.bool.file_dump_enabled_default));
    }

    public void setPreferenceFileDumpEnabled(boolean fileDumpEnabled) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceFileDumpEnabled, fileDumpEnabled is " + fileDumpEnabled);
        setPreferenceBoolean(getResources().getString(R.string.file_dump_enabled_key), fileDumpEnabled);
    }

    public void removePreferenceFileDumpEnabled() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceFileDumpEnabled");
        removePreferenceValue(getResources().getString(R.string.file_dump_enabled_key));
    }

    public boolean getPreferenceAskedNotificationPermission() {
        Log.d(PreferenceManager.class.getName(), "getPreferenceAskedNotificationPermission");
        return getPreferenceBoolean(getResources().getString(R.string.asked_notification_permission), getResources().getBoolean(R.bool.asked_notification_permission_default));
    }

    public void setPreferenceAskedNotificationPermission(boolean askedNotificationPermission) {
        Log.d(PreferenceManager.class.getName(), "setPreferenceAskedNotificationPermission, askedNotificationPermission is " + askedNotificationPermission);
        setPreferenceBoolean(getResources().getString(R.string.asked_notification_permission), askedNotificationPermission);
    }

    public void removePreferenceAskedNotificationPermission() {
        Log.d(PreferenceManager.class.getName(), "removePreferenceAskedNotificationPermission");
        removePreferenceValue(getResources().getString(R.string.asked_notification_permission));
    }

    public Set<String> getArbitraryFolders() {
        Set<String> folders = new HashSet<>(2);
        folders.add(getPreferenceArbitraryLogFolder());
        folders.add(getPreferenceArbitraryDownloadFolder());
        return folders;
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    private SharedPreferences.Editor getDefaultSharedPreferencesEditor() {
        return getDefaultSharedPreferences().edit();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
