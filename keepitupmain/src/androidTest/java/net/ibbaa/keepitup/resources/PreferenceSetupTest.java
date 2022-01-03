/*
 * Copyright (c) 2022. Alwin Ibba
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
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@SmallTest
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
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        Map<String, ?> globalSettings = new HashMap<>();
        setup.importGlobalSettings(globalSettings);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testImportGlobalSettingsSetValues() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferencePingCount", 5);
        globalSettings.put("preferenceConnectCount", 10);
        globalSettings.put("preferenceNotificationInactiveNetwork", true);
        globalSettings.put("preferenceDownloadExternalStorage", true);
        globalSettings.put("preferenceDownloadFolder", "folder");
        globalSettings.put("preferenceDownloadKeep", true);
        setup.importGlobalSettings(globalSettings);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testImportGlobalSettingsSetValuesAsString() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferencePingCount", "5");
        globalSettings.put("preferenceConnectCount", "10");
        globalSettings.put("preferenceNotificationInactiveNetwork", "true");
        globalSettings.put("preferenceDownloadExternalStorage", "true");
        globalSettings.put("preferenceDownloadFolder", "folder");
        globalSettings.put("preferenceDownloadKeep", "true");
        setup.importGlobalSettings(globalSettings);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testImportGlobalSettingsInvalid() {
        Map<String, Object> globalSettings = new HashMap<>();
        globalSettings.put("preferencePingCount", 11);
        globalSettings.put("preferenceConnectCount", 55);
        globalSettings.put("preferenceNotificationInactiveNetwork", "xyz");
        globalSettings.put("preferenceDownloadExternalStorage", "tru");
        globalSettings.put("preferenceDownloadFolder", null);
        globalSettings.put("preferenceDownloadKeep", 3);
        setup.importGlobalSettings(globalSettings);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testImportDefaultsEmpty() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        Map<String, ?> defaults = new HashMap<>();
        setup.importDefaults(defaults);
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testImportDefaultsSetValues() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", AccessType.CONNECT.getCode());
        defaults.put("preferenceAddress", "address");
        defaults.put("preferencePort", 123);
        defaults.put("preferenceInterval", 456);
        defaults.put("preferenceOnlyWifi", true);
        defaults.put("preferenceNotification", true);
        setup.importDefaults(defaults);
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testImportDefaultsSetValuesAsString() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", String.valueOf(AccessType.CONNECT.getCode()));
        defaults.put("preferenceAddress", "address");
        defaults.put("preferencePort", "123");
        defaults.put("preferenceInterval", "456");
        defaults.put("preferenceOnlyWifi", "true");
        defaults.put("preferenceNotification", "true");
        setup.importDefaults(defaults);
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testImportDefaultsInvalid() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("preferenceAccessType", 25);
        defaults.put("preferenceAddress", "1.1.1.1.1.1");
        defaults.put("preferencePort", 12345678);
        defaults.put("preferenceInterval", "");
        defaults.put("preferenceOnlyWifi", 1);
        defaults.put("preferenceNotification", 1);
        setup.importDefaults(defaults);
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testImportSystemSettingsEmpty() {
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        Map<String, ?> systemSettings = new HashMap<>();
        setup.importSystemSettings(systemSettings);
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportSystemSetValues() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", "folderImport");
        systemSettings.put("preferenceExportFolder", "folderExport");
        systemSettings.put("preferenceExternalStorageType", 1);
        systemSettings.put("preferenceFileLoggerEnabled", true);
        systemSettings.put("preferenceFileDumpEnabled", true);
        systemSettings.put("preferenceTheme", 1);
        setup.importSystemSettings(systemSettings);
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportSystemSetValuesAsString() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", "folderImport");
        systemSettings.put("preferenceExportFolder", "folderExport");
        systemSettings.put("preferenceExternalStorageType", "1");
        systemSettings.put("preferenceFileLoggerEnabled", "true");
        systemSettings.put("preferenceFileDumpEnabled", "true");
        systemSettings.put("preferenceTheme", "1");
        setup.importSystemSettings(systemSettings);
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportSystemSetInvalid() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceImportFolder", null);
        systemSettings.put("preferenceExportFolder", null);
        systemSettings.put("preferenceExternalStorageType", "abc");
        systemSettings.put("preferenceFileLoggerEnabled", null);
        systemSettings.put("preferenceFileDumpEnabled", null);
        systemSettings.put("preferenceTheme", "abc");
        setup.importSystemSettings(systemSettings);
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testExportGlobalSettingsDefaultValues() {
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        assertEquals(globalSettings.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(globalSettings.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testExportGlobalSettingsSetValues() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(globalSettings.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testExportDefaultsDefaultValues() {
        Map<String, ?> defaults = setup.exportDefaults();
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType().getCode());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testExportDefaultsSetValues() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        Map<String, ?> defaults = setup.exportDefaults();
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType().getCode());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testExportSystemSettingsDefaultValues() {
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        assertEquals(systemSettings.get("preferenceImportFolder"), preferenceManager.getPreferenceImportFolder());
        assertEquals(systemSettings.get("preferenceExportFolder"), preferenceManager.getPreferenceExportFolder());
        assertEquals(systemSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceTheme"), preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testExportSystemSettingsSetValues() {
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals(30, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(5, preferenceManager.getPreferenceTheme());
        assertEquals(systemSettings.get("preferenceImportFolder"), preferenceManager.getPreferenceImportFolder());
        assertEquals(systemSettings.get("preferenceExportFolder"), preferenceManager.getPreferenceExportFolder());
        assertEquals(systemSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceTheme"), preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportExportGlobalSettings() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        setup.importGlobalSettings(globalSettings);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(globalSettings.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testImportExportDefaults() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        Map<String, ?> defaults = setup.exportDefaults();
        setup.importDefaults(defaults);
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType().getCode());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testImportExportSystemSettings() {
        preferenceManager.setPreferenceExternalStorageType(1);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(1);
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        setup.importSystemSettings(systemSettings);
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
        assertEquals(systemSettings.get("preferenceImportFolder"), preferenceManager.getPreferenceImportFolder());
        assertEquals(systemSettings.get("preferenceExportFolder"), preferenceManager.getPreferenceExportFolder());
        assertEquals(systemSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceTheme"), preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testRemoveGlobalSettings() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        setup.removeGlobalSettings();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testRemoveDefaults() {
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        setup.removeDefaults();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testRemoveSystemSettings() {
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        setup.removeSystemSettings();
        assertEquals("config", preferenceManager.getPreferenceImportFolder());
        assertEquals("config", preferenceManager.getPreferenceExportFolder());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testAllSettings() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        setup.removeAllSettings();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
    }
}
