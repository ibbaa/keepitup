package de.ibba.keepitup.resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        Map<String, ?> globalSettings = new HashMap<>();
        setup.importGlobalSettings(globalSettings);
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
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
        globalSettings.put("preferenceExternalStorageType", 30);
        globalSettings.put("preferenceDownloadFolder", "folder");
        globalSettings.put("preferenceDownloadKeep", true);
        setup.importGlobalSettings(globalSettings);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(30, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
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
        defaults.put("preferenceAccessType", AccessType.CONNECT);
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
    public void testImportSystemSettingsEmpty() {
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        Map<String, ?> systemSettings = new HashMap<>();
        setup.importSystemSettings(systemSettings);
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testImportSystemSetValues() {
        Map<String, Object> systemSettings = new HashMap<>();
        systemSettings.put("preferenceFileLoggerEnabled", true);
        systemSettings.put("preferenceFileDumpEnabled", true);
        setup.importSystemSettings(systemSettings);
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testExportGlobalSettingsDefaultValues() {
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        assertEquals(globalSettings.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(globalSettings.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testExportGlobalSettingsSetValues() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(30, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(globalSettings.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
        assertEquals(globalSettings.get("preferenceDownloadFolder"), preferenceManager.getPreferenceDownloadFolder());
        assertEquals(globalSettings.get("preferenceDownloadKeep"), preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testExportDefaultsDefaultValues() {
        Map<String, ?> defaults = setup.exportDefaults();
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType());
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
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testExportSystemSettingsDefaultValues() {
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testExportSystemSettingsSetValues() {
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testImportExportGlobalSettings() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        Map<String, ?> globalSettings = setup.exportGlobalSettings();
        setup.importGlobalSettings(globalSettings);
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(30, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertEquals(globalSettings.get("preferencePingCount"), preferenceManager.getPreferencePingCount());
        assertEquals(globalSettings.get("preferenceConnectCount"), preferenceManager.getPreferenceConnectCount());
        assertEquals(globalSettings.get("preferenceNotificationInactiveNetwork"), preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(globalSettings.get("preferenceDownloadExternalStorage"), preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(globalSettings.get("preferenceExternalStorageType"), preferenceManager.getPreferenceExternalStorageType());
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
        assertEquals(defaults.get("preferenceAccessType"), preferenceManager.getPreferenceAccessType());
        assertEquals(defaults.get("preferenceAddress"), preferenceManager.getPreferenceAddress());
        assertEquals(defaults.get("preferencePort"), preferenceManager.getPreferencePort());
        assertEquals(defaults.get("preferenceInterval"), preferenceManager.getPreferenceInterval());
        assertEquals(defaults.get("preferenceOnlyWifi"), preferenceManager.getPreferenceOnlyWifi());
        assertEquals(defaults.get("preferenceNotification"), preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testImportExportSystemSettings() {
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        Map<String, ?> systemSettings = setup.exportSystemSettings();
        setup.importSystemSettings(systemSettings);
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(systemSettings.get("preferenceFileLoggerEnabled"), preferenceManager.getPreferenceFileLoggerEnabled());
        assertEquals(systemSettings.get("preferenceFileDumpEnabled"), preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testRemoveGlobalSettings() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        setup.removeGlobalSettings();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
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
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        setup.removeSystemSettings();
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
    }
}
