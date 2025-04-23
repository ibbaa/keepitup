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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
@SuppressWarnings({"ExtractMethodRecommender"})
@RunWith(AndroidJUnit4.class)
public class PreferenceSetupTest {

    private PreferenceSetup setup;
    private PreferenceManager preferenceManager;

    @Before
    public void beforeEachTestMethod() {
        setup = new PreferenceSetup(TestRegistry.getContext());
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
    }

    @After
    public void afterEachTestMethod() {
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testImportGlobalSettingsEmpty() {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(3);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceArbitraryDownloadFolder("/123");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceLogFolder("folder");
        preferenceManager.setPreferenceArbitraryLogFolder("/123");
        Map<String, ?> globalSettings = new HashMap<>();
        setup.importGlobalSettings(globalSettings);
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        assertEquals(1, preferenceManager.getPreferenceNotificationAfterFailures());
        assertTrue(preferenceManager.getPreferenceSuspensionEnabled());
        assertFalse(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testImportGlobalSettingsSetValues() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferenceNotificationInactiveNetwork", true);
        globalSettings.put("preferenceNotificationType", 2);
        globalSettings.put("preferenceNotificationAfterFailures", 2);
        globalSettings.put("preferenceSuspensionEnabled", false);
        globalSettings.put("preferenceEnforceDefaultPingPackageSize", true);
        globalSettings.put("preferenceDownloadExternalStorage", true);
        globalSettings.put("preferenceDownloadFolder", "folder");
        globalSettings.put("preferenceArbitraryDownloadFolder", "/123");
        globalSettings.put("preferenceDownloadKeep", true);
        globalSettings.put("preferenceDownloadFollowsRedirects", false);
        globalSettings.put("preferenceLogFile", true);
        globalSettings.put("preferenceLogFolder", "folder");
        globalSettings.put("preferenceArbitraryLogFolder", "/456");
        setup.importGlobalSettings(globalSettings);
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertEquals(2, preferenceManager.getPreferenceNotificationAfterFailures());
        assertFalse(preferenceManager.getPreferenceSuspensionEnabled());
        assertTrue(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/123", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertTrue(preferenceManager.getPreferenceLogFile());
        assertEquals("folder", preferenceManager.getPreferenceLogFolder());
        assertEquals("/456", preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testImportGlobalSettingsSetValuesDownloadKeepOnInternal() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferenceDownloadExternalStorage", false);
        globalSettings.put("preferenceDownloadKeep", true);
        setup.importGlobalSettings(globalSettings);
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testImportGlobalSettingsSetValuesAsString() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferenceNotificationInactiveNetwork", "true");
        globalSettings.put("preferenceNotificationType", "2");
        globalSettings.put("preferenceNotificationAfterFailures", "2");
        globalSettings.put("preferenceSuspensionEnabled", "false");
        globalSettings.put("preferenceEnforceDefaultPingPackageSize", "true");
        globalSettings.put("preferenceDownloadExternalStorage", "true");
        globalSettings.put("preferenceDownloadFolder", "folder");
        globalSettings.put("preferenceArbitraryDownloadFolder", "/123");
        globalSettings.put("preferenceDownloadKeep", "true");
        globalSettings.put("preferenceDownloadFollowsRedirects", "false");
        globalSettings.put("preferenceLogFile", "true");
        globalSettings.put("preferenceLogFolder", "folder");
        globalSettings.put("preferenceArbitraryLogFolder", "/456");
        setup.importGlobalSettings(globalSettings);
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertEquals(2, preferenceManager.getPreferenceNotificationAfterFailures());
        assertFalse(preferenceManager.getPreferenceSuspensionEnabled());
        assertTrue(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/123", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertTrue(preferenceManager.getPreferenceLogFile());
        assertEquals("folder", preferenceManager.getPreferenceLogFolder());
        assertEquals("/456", preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testImportGlobalSettingsInvalid() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferenceNotificationInactiveNetwork", "xyz");
        globalSettings.put("preferenceNotificationType", 25);
        globalSettings.put("preferenceNotificationAfterFailures", 11);
        globalSettings.put("preferenceSuspensionEnabled", "123");
        globalSettings.put("preferenceDownloadExternalStorage", "tru");
        globalSettings.put("preferenceDownloadFolder", null);
        globalSettings.put("preferenceArbitraryDownloadFolder", null);
        globalSettings.put("preferenceDownloadKeep", 3);
        globalSettings.put("preferenceDownloadFollowsRedirects", 3);
        globalSettings.put("preferenceLogFile", "tru");
        globalSettings.put("preferenceLogFolder", null);
        globalSettings.put("preferenceArbitraryLogFolder", null);
        setup.importGlobalSettings(globalSettings);
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        assertEquals(11, preferenceManager.getPreferenceNotificationAfterFailures());
        assertTrue(preferenceManager.getPreferenceSuspensionEnabled());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testImportDefaultsEmpty() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferencePingPackageSize(1234);
        Map<String, ?> defaults = new HashMap<>();
        setup.importDefaults(defaults);
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceHighPrio());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
    }

    @Test
    public void testImportDefaultsSetValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", AccessType.CONNECT.getCode());
        defaults.put("preferenceAddress", "address");
        defaults.put("preferencePort", 123);
        defaults.put("preferenceInterval", 456);
        defaults.put("preferencePingCount", 5);
        defaults.put("preferenceConnectCount", 10);
        defaults.put("preferenceStopOnSuccess", true);
        defaults.put("preferenceOnlyWifi", true);
        defaults.put("preferenceNotification", true);
        defaults.put("preferenceHighPrio", true);
        defaults.put("preferencePingPackageSize", 1234);
        setup.importDefaults(defaults);
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertTrue(preferenceManager.getPreferenceHighPrio());
        assertEquals(1234, preferenceManager.getPreferencePingPackageSize());
    }

    @Test
    public void testImportDefaultsSetValuesAsString() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", String.valueOf(AccessType.CONNECT.getCode()));
        defaults.put("preferenceAddress", "address");
        defaults.put("preferencePort", "123");
        defaults.put("preferenceInterval", "456");
        defaults.put("preferencePingCount", "5");
        defaults.put("preferenceConnectCount", "10");
        defaults.put("preferenceStopOnSuccess", "true");
        defaults.put("preferenceOnlyWifi", "true");
        defaults.put("preferenceNotification", "true");
        defaults.put("preferenceHighPrio", "true");
        defaults.put("preferencePingPackageSize", 1234);
        setup.importDefaults(defaults);
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertTrue(preferenceManager.getPreferenceHighPrio());
        assertEquals(1234, preferenceManager.getPreferencePingPackageSize());
    }

    @Test
    public void testImportDefaultsInvalid() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", 25);
        defaults.put("preferenceAddress", "1.1.1.1.1.1");
        defaults.put("preferencePort", 12345678);
        defaults.put("preferenceInterval", "");
        defaults.put("preferencePingCount", 11);
        defaults.put("preferenceConnectCount", 55);
        defaults.put("preferenceStopOnSuccess", 1);
        defaults.put("preferenceOnlyWifi", 1);
        defaults.put("preferenceNotification", 1);
        defaults.put("preferenceHighPrio", 1);
        defaults.put("pingPackageSize", 12345678);
        setup.importDefaults(defaults);
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceHighPrio());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
    }

    @Test
    public void testImportSystemSettingsEmpty() {
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("arbitraryFolderExport");
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        Map<String, ?> systemSettings = new HashMap<>();
        setup.importSystemSettings(systemSettings);
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals("", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
        assertFalse(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertFalse(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertFalse(preferenceManager.getPreferenceAskedNotificationPermission());
        assertFalse(preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testImportSystemSettingsSetValues() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", "folderImport");
        systemSettings.put("preferenceArbitraryImportFolder", "arbitraryFolderImport");
        systemSettings.put("preferenceExportFolder", "folderExport");
        systemSettings.put("preferenceLastArbitraryExportFile", "arbitraryFolderExport");
        systemSettings.put("preferenceExternalStorageType", 1);
        systemSettings.put("preferenceFileLoggerEnabled", true);
        systemSettings.put("preferenceFileDumpEnabled", true);
        systemSettings.put("preferenceTheme", 1);
        systemSettings.put("preferenceAllowArbitraryFileLocation", true);
        systemSettings.put("preferenceAlarmOnHighPrio", true);
        systemSettings.put("preferenceAskedNotificationPermission", true);
        systemSettings.put("preferenceAlarmInfoShown", true);
        setup.importSystemSettings(systemSettings);
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals("arbitraryFolderExport", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
        assertTrue(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertTrue(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertTrue(preferenceManager.getPreferenceAskedNotificationPermission());
        assertTrue(preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testImportSystemSettingsSetValuesAsString() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", "folderImport");
        systemSettings.put("preferenceArbitraryImportFolder", "arbitraryFolderImport");
        systemSettings.put("preferenceExportFolder", "folderExport");
        systemSettings.put("preferenceLastArbitraryExportFile", "arbitraryFolderExport");
        systemSettings.put("preferenceExternalStorageType", "1");
        systemSettings.put("preferenceFileLoggerEnabled", "true");
        systemSettings.put("preferenceFileDumpEnabled", "true");
        systemSettings.put("preferenceTheme", "1");
        systemSettings.put("preferenceAllowArbitraryFileLocation", "true");
        systemSettings.put("preferenceAlarmOnHighPrio", "true");
        systemSettings.put("preferenceAskedNotificationPermission", "true");
        systemSettings.put("preferenceAlarmInfoShown", "true");
        setup.importSystemSettings(systemSettings);
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals("arbitraryFolderExport", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
        assertTrue(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertTrue(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertTrue(preferenceManager.getPreferenceAskedNotificationPermission());
        assertTrue(preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testImportSystemSettingsInvalid() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", null);
        systemSettings.put("preferenceArbitraryImportFolder", null);
        systemSettings.put("preferenceExportFolder", null);
        systemSettings.put("preferenceLastArbitraryExportFile", null);
        systemSettings.put("preferenceExternalStorageType", "abc");
        systemSettings.put("preferenceFileLoggerEnabled", null);
        systemSettings.put("preferenceFileDumpEnabled", null);
        systemSettings.put("preferenceTheme", "abc");
        systemSettings.put("preferenceAllowArbitraryFileLocation", null);
        systemSettings.put("preferenceAlarmOnHighPrio", null);
        systemSettings.put("preferenceAskedNotificationPermission", null);
        systemSettings.put("preferenceAlarmInfoShown", null);
        setup.importSystemSettings(systemSettings);
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals("", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
        assertFalse(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertFalse(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertFalse(preferenceManager.getPreferenceAskedNotificationPermission());
        assertFalse(preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testExportGlobalSettingsDefaultValues() {
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceNotificationType"), preferenceManager.getPreferenceNotificationType().getCode());
        assertEquals(globalSettings.get("preferenceNotificationAfterFailures"), preferenceManager.getPreferenceNotificationAfterFailures());
        assertEquals(globalSettings.get("preferenceSuspensionEnabled"), preferenceManager.getPreferenceSuspensionEnabled());
        assertEquals(globalSettings.get("preferenceEnforceDefaultPingPackageSize"), preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceArbitraryDownloadFolder"), preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferenceDownloadFollowsRedirects"), preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertEquals(globalSettings.get("preferenceLogFile"), preferenceManager.getPreferenceLogFile());
        assertEquals(globalSettings.get("preferenceLogFolder"), preferenceManager.getPreferenceLogFolder());
        assertEquals(globalSettings.get("preferenceArbitraryLogFolder"), preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testExportGlobalSettingsSetValues() {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(5);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceArbitraryDownloadFolder("/123");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceLogFolder("folder");
        preferenceManager.setPreferenceArbitraryLogFolder("/456");
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertEquals(5, preferenceManager.getPreferenceNotificationAfterFailures());
        assertFalse(preferenceManager.getPreferenceSuspensionEnabled());
        assertTrue(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/123", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertTrue(preferenceManager.getPreferenceLogFile());
        assertEquals("folder", preferenceManager.getPreferenceLogFolder());
        assertEquals("/456", preferenceManager.getPreferenceArbitraryLogFolder());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceNotificationType"), preferenceManager.getPreferenceNotificationType().getCode());
        assertEquals(globalSettings.get("preferenceNotificationAfterFailures"), preferenceManager.getPreferenceNotificationAfterFailures());
        assertEquals(globalSettings.get("preferenceSuspensionEnabled"), preferenceManager.getPreferenceSuspensionEnabled());
        assertEquals(globalSettings.get("preferenceEnforceDefaultPingPackageSize"), preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceArbitraryDownloadFolder"), preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferenceDownloadFollowsRedirects"), preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertEquals(globalSettings.get("preferenceLogFile"), preferenceManager.getPreferenceLogFile());
        assertEquals(globalSettings.get("preferenceLogFolder"), preferenceManager.getPreferenceLogFolder());
        assertEquals(globalSettings.get("preferenceArbitraryLogFolder"), preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testExportDefaultsDefaultValues() {
        Map<String, ?> defaults = setup.exportDefaults();
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType().getCode());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(defaults.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(defaults.get("preferenceStopOnSuccess"), preferenceManager.getPreferenceStopOnSuccess());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
        assertEquals(defaults.get("preferenceHighPrio"), preferenceManager.getPreferenceHighPrio());
        assertEquals(defaults.get("preferencePingPackageSize"), preferenceManager.getPreferencePingPackageSize());
    }

    @Test
    public void testExportDefaultsSetValues() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        preferenceManager.setPreferencePingPackageSize(1234);
        Map<String, ?> defaults = setup.exportDefaults();
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertTrue(preferenceManager.getPreferenceHighPrio());
        assertEquals(1234, preferenceManager.getPreferencePingPackageSize());
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType().getCode());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(defaults.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(defaults.get("preferenceStopOnSuccess"), preferenceManager.getPreferenceStopOnSuccess());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
        assertEquals(defaults.get("preferenceHighPrio"), preferenceManager.getPreferenceHighPrio());
        assertEquals(defaults.get("preferencePingPackageSize"), preferenceManager.getPreferencePingPackageSize());
    }

    @Test
    public void testExportSystemSettingsDefaultValues() {
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        assertEquals(systemSettings.get("preferenceImportFolder"), preferenceManager.getPreferenceImportFolder());
        assertEquals(systemSettings.get("preferenceExportFolder"), preferenceManager.getPreferenceExportFolder());
        assertEquals(systemSettings.get("preferenceLastArbitraryExportFile"), preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(systemSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceTheme"), preferenceManager.getPreferenceTheme());
        assertEquals(systemSettings.get("preferenceAllowArbitraryFileLocation"), preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertEquals(systemSettings.get("preferenceAlarmOnHighPrio"), preferenceManager.getPreferenceAlarmOnHighPrio());
        assertEquals(systemSettings.get("preferenceAskedNotificationPermission"), preferenceManager.getPreferenceAskedNotificationPermission());
        assertEquals(systemSettings.get("preferenceAlarmInfoShown"), preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testExportSystemSettingsSetValues() {
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("arbitraryFolderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals("arbitraryFolderExport", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(30, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(5, preferenceManager.getPreferenceTheme());
        assertTrue(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertTrue(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertTrue(preferenceManager.getPreferenceAskedNotificationPermission());
        assertEquals(systemSettings.get("preferenceImportFolder"), preferenceManager.getPreferenceImportFolder());
        assertEquals(systemSettings.get("preferenceExportFolder"), preferenceManager.getPreferenceExportFolder());
        assertEquals(systemSettings.get("preferenceLastArbitraryExportFile"), preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(systemSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceTheme"), preferenceManager.getPreferenceTheme());
        assertEquals(systemSettings.get("preferenceAllowArbitraryFileLocation"), preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertEquals(systemSettings.get("preferenceAlarmOnHighPrio"), preferenceManager.getPreferenceAlarmOnHighPrio());
        assertEquals(systemSettings.get("preferenceAskedNotificationPermission"), preferenceManager.getPreferenceAskedNotificationPermission());
        assertEquals(systemSettings.get("preferenceAlarmInfoShown"), preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testImportExportGlobalSettings() {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(5);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceArbitraryDownloadFolder("/123");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceLogFolder("folder");
        preferenceManager.setPreferenceArbitraryLogFolder("/456");
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        setup.importGlobalSettings(globalSettings);
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertEquals(5, preferenceManager.getPreferenceNotificationAfterFailures());
        assertFalse(preferenceManager.getPreferenceSuspensionEnabled());
        assertTrue(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/123", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertTrue(preferenceManager.getPreferenceLogFile());
        assertEquals("folder", preferenceManager.getPreferenceLogFolder());
        assertEquals("/456", preferenceManager.getPreferenceArbitraryLogFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceNotificationType"), preferenceManager.getPreferenceNotificationType().getCode());
        assertEquals(globalSettings.get("preferenceNotificationAfterFailures"), preferenceManager.getPreferenceNotificationAfterFailures());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceSuspensionEnabled"), preferenceManager.getPreferenceSuspensionEnabled());
        assertEquals(globalSettings.get("preferenceEnforceDefaultPingPackageSize"), preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceArbitraryDownloadFolder"), preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferenceDownloadFollowsRedirects"), preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertEquals(globalSettings.get("preferenceLogFile"), preferenceManager.getPreferenceLogFile());
        assertEquals(globalSettings.get("preferenceLogFolder"), preferenceManager.getPreferenceLogFolder());
        assertEquals(globalSettings.get("preferenceArbitraryLogFolder"), preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testImportExportDefaults() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferencePingPackageSize(123);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        Map<String, ?> defaults = setup.exportDefaults();
        setup.importDefaults(defaults);
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertEquals(123, preferenceManager.getPreferencePingPackageSize());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertTrue(preferenceManager.getPreferenceHighPrio());
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType().getCode());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(defaults.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(defaults.get("preferencePingPackageSize"), preferenceManager.getPreferencePingPackageSize());
        assertEquals(defaults.get("preferenceStopOnSuccess"), preferenceManager.getPreferenceStopOnSuccess());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
        assertEquals(defaults.get("preferenceHighPrio"), preferenceManager.getPreferenceHighPrio());
    }

    @Test
    public void testImportExportSystemSettings() {
        preferenceManager.setPreferenceExternalStorageType(1);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("arbitraryFolderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(1);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        setup.importSystemSettings(systemSettings);
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals("arbitraryFolderExport", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
        assertTrue(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertTrue(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertTrue(preferenceManager.getPreferenceAskedNotificationPermission());
        assertEquals(systemSettings.get("preferenceImportFolder"), preferenceManager.getPreferenceImportFolder());
        assertEquals(systemSettings.get("preferenceExportFolder"), preferenceManager.getPreferenceExportFolder());
        assertEquals(systemSettings.get("preferenceLastArbitraryExportFile"), preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(systemSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceTheme"), preferenceManager.getPreferenceTheme());
        assertEquals(systemSettings.get("preferenceAllowArbitraryFileLocation"), preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertEquals(systemSettings.get("preferenceAlarmOnHighPrio"), preferenceManager.getPreferenceAlarmOnHighPrio());
        assertEquals(systemSettings.get("preferenceAskedNotificationPermission"), preferenceManager.getPreferenceAskedNotificationPermission());
        assertEquals(systemSettings.get("preferenceAlarmInfoShown"), preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testRemoveGlobalSettings() {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(3);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceArbitraryDownloadFolder("/123");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceLogFolder("folder");
        preferenceManager.setPreferenceArbitraryLogFolder("/456");
        setup.removeGlobalSettings();
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        assertEquals(1, preferenceManager.getPreferenceNotificationAfterFailures());
        assertTrue(preferenceManager.getPreferenceSuspensionEnabled());
        assertFalse(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryLogFolder());
    }

    @Test
    public void testRemoveDefaults() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferencePingPackageSize(12);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        setup.removeDefaults();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceHighPrio());
    }

    @Test
    public void testRemoveSystemSettings() {
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("arbitraryFolderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        setup.removeSystemSettings();
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals("", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
        assertFalse(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertFalse(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertFalse(preferenceManager.getPreferenceAskedNotificationPermission());
        assertFalse(preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testRemoveAllSettings() {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(4);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceArbitraryDownloadFolder("123");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceLogFile(true);
        preferenceManager.setPreferenceLogFolder("folder");
        preferenceManager.setPreferenceArbitraryLogFolder("/456");
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferencePingPackageSize(12);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("arbitraryFolderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        setup.removeAllSettings();
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        assertEquals(1, preferenceManager.getPreferenceNotificationAfterFailures());
        assertTrue(preferenceManager.getPreferenceSuspensionEnabled());
        assertFalse(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryDownloadFolder());
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        assertEquals("/Documents", preferenceManager.getPreferenceArbitraryLogFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceHighPrio());
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals("", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
        assertFalse(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertFalse(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertFalse(preferenceManager.getPreferenceAskedNotificationPermission());
        assertFalse(preferenceManager.getPreferenceAlarmInfoShown());
    }
}
