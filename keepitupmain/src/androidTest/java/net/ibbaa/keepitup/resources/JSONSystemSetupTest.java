/*
 * Copyright (c) 2023. Alwin Ibba
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
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class JSONSystemSetupTest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private IntervalDAO intervalDAO;
    private PreferenceManager preferenceManager;
    private JSONSystemSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        setup = new JSONSystemSetup(TestRegistry.getContext());
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testVersion() throws Exception {
        SystemSetupResult result = setup.exportData();
        assertTrue(result.isSuccess());
        JSONObject jsonData = new JSONObject(result.getData());
        assertEquals(BuildConfig.VERSION_CODE, jsonData.getInt("version"));
        assertEquals(TestRegistry.getContext().getResources().getInteger(R.integer.db_version), jsonData.getInt("dbversion"));
    }

    @Test
    public void testExportDatabase() throws Exception {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry task1Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        LogEntry task1Entry2 = logDAO.insertAndDeleteLog(getLogEntry2(task1.getId()));
        LogEntry task1Entry3 = logDAO.insertAndDeleteLog(getLogEntry3(task1.getId()));
        LogEntry task2Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task2.getId()));
        LogEntry task2Entry2 = logDAO.insertAndDeleteLog(getLogEntry2(task2.getId()));
        LogEntry task2Entry3 = logDAO.insertAndDeleteLog(getLogEntry3(task2.getId()));
        LogEntry task3Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task3.getId()));
        LogEntry task3Entry2 = logDAO.insertAndDeleteLog(getLogEntry2(task3.getId()));
        LogEntry task3Entry3 = logDAO.insertAndDeleteLog(getLogEntry3(task3.getId()));
        SystemSetupResult result = setup.exportData();
        assertTrue(result.isSuccess());
        JSONObject jsonData = new JSONObject(result.getData());
        JSONObject databaseData = (JSONObject) jsonData.get("database");
        JSONObject task1Data = (JSONObject) databaseData.get(String.valueOf(task1.getId()));
        JSONObject task2Data = (JSONObject) databaseData.get(String.valueOf(task2.getId()));
        JSONObject task3Data = (JSONObject) databaseData.get(String.valueOf(task3.getId()));
        JSONObject task1NetworkTaskData = (JSONObject) task1Data.get("networktask");
        JSONObject task2NetworkTaskData = (JSONObject) task2Data.get("networktask");
        JSONObject task3NetworkTaskData = (JSONObject) task3Data.get("networktask");
        NetworkTask task1NetworkTask = new NetworkTask(JSONUtil.toMap(task1NetworkTaskData));
        NetworkTask task2NetworkTask = new NetworkTask(JSONUtil.toMap(task2NetworkTaskData));
        NetworkTask task3NetworkTask = new NetworkTask(JSONUtil.toMap(task3NetworkTaskData));
        assertTrue(task1.isEqual(task1NetworkTask));
        assertTrue(task2.isEqual(task2NetworkTask));
        assertTrue(task3.isEqual(task3NetworkTask));
        JSONArray task1LogData = (JSONArray) task1Data.get("logentry");
        JSONArray task2LogData = (JSONArray) task2Data.get("logentry");
        JSONArray task3LogData = (JSONArray) task3Data.get("logentry");
        LogEntry task1LogEntry1 = new LogEntry(JSONUtil.toMap((JSONObject) task1LogData.get(0)));
        LogEntry task1LogEntry2 = new LogEntry(JSONUtil.toMap((JSONObject) task1LogData.get(1)));
        LogEntry task1LogEntry3 = new LogEntry(JSONUtil.toMap((JSONObject) task1LogData.get(2)));
        LogEntry task2LogEntry1 = new LogEntry(JSONUtil.toMap((JSONObject) task2LogData.get(0)));
        LogEntry task2LogEntry2 = new LogEntry(JSONUtil.toMap((JSONObject) task2LogData.get(1)));
        LogEntry task2LogEntry3 = new LogEntry(JSONUtil.toMap((JSONObject) task2LogData.get(2)));
        LogEntry task3LogEntry1 = new LogEntry(JSONUtil.toMap((JSONObject) task3LogData.get(0)));
        LogEntry task3LogEntry2 = new LogEntry(JSONUtil.toMap((JSONObject) task3LogData.get(1)));
        LogEntry task3LogEntry3 = new LogEntry(JSONUtil.toMap((JSONObject) task3LogData.get(2)));
        assertTrue(task1Entry1.isEqual(task1LogEntry1));
        assertTrue(task1Entry2.isEqual(task1LogEntry2));
        assertTrue(task1Entry3.isEqual(task1LogEntry3));
        assertTrue(task2Entry1.isEqual(task2LogEntry1));
        assertTrue(task2Entry2.isEqual(task2LogEntry2));
        assertTrue(task2Entry3.isEqual(task2LogEntry3));
        assertTrue(task3Entry1.isEqual(task3LogEntry1));
        assertTrue(task3Entry2.isEqual(task3LogEntry2));
        assertTrue(task3Entry3.isEqual(task3LogEntry3));
    }

    @Test
    public void testExportDatabaseInvalidTask() throws Exception {
        NetworkTask task1 = getNetworkTask1();
        task1.setAddress("127.12..1.1.1.1");
        task1 = networkTaskDAO.insertNetworkTask(task1);
        SystemSetupResult result = setup.exportData();
        assertTrue(result.isSuccess());
        JSONObject jsonData = new JSONObject(result.getData());
        JSONObject databaseData = (JSONObject) jsonData.get("database");
        JSONObject task1Data = (JSONObject) databaseData.get(String.valueOf(task1.getId()));
        JSONObject task1NetworkTaskData = (JSONObject) task1Data.get("networktask");
        NetworkTask task1NetworkTask = new NetworkTask(JSONUtil.toMap(task1NetworkTaskData));
        assertTrue(task1.isEqual(task1NetworkTask));
    }

    @Test
    public void testExportSettings() throws Exception {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
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
        SystemSetupResult result = setup.exportData();
        JSONObject jsonData = new JSONObject(result.getData());
        JSONObject settingsData = (JSONObject) jsonData.get("preferences");
        JSONObject globalSettingsData = (JSONObject) settingsData.get("global");
        JSONObject defaultsData = (JSONObject) settingsData.get("defaults");
        JSONObject systemSettingsData = (JSONObject) settingsData.get("system");
        assertEquals(5, globalSettingsData.getInt("preferencePingCount"));
        assertEquals(10, globalSettingsData.getInt("preferenceConnectCount"));
        assertTrue(globalSettingsData.getBoolean("preferenceNotificationInactiveNetwork"));
        assertEquals(NotificationType.CHANGE, NotificationType.forCode(globalSettingsData.getInt("preferenceNotificationType")));
        assertTrue(globalSettingsData.getBoolean("preferenceDownloadExternalStorage"));
        assertEquals("folder", globalSettingsData.getString("preferenceDownloadFolder"));
        assertTrue(globalSettingsData.getBoolean("preferenceDownloadKeep"));
        assertEquals(AccessType.CONNECT, AccessType.forCode(defaultsData.getInt("preferenceAccessType")));
        assertEquals("address", defaultsData.getString("preferenceAddress"));
        assertEquals(123, defaultsData.getInt("preferencePort"));
        assertEquals(456, defaultsData.getInt("preferenceInterval"));
        assertTrue(defaultsData.getBoolean("preferenceOnlyWifi"));
        assertTrue(defaultsData.getBoolean("preferenceNotification"));
        assertEquals("folderImport", systemSettingsData.getString("preferenceImportFolder"));
        assertEquals("folderExport", systemSettingsData.getString("preferenceExportFolder"));
        assertEquals(30, systemSettingsData.getInt("preferenceExternalStorageType"));
        assertTrue(systemSettingsData.getBoolean("preferenceFileLoggerEnabled"));
        assertTrue(systemSettingsData.getBoolean("preferenceFileDumpEnabled"));
        assertEquals(5, systemSettingsData.getInt("preferenceTheme"));
    }

    @Test
    public void testExportSettingsInvalid() throws Exception {
        preferenceManager.setPreferencePingCount(25);
        preferenceManager.setPreferenceConnectCount(25);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferencePort(100000);
        preferenceManager.setPreferenceInterval(-5);
        preferenceManager.setPreferenceTheme(5);
        SystemSetupResult result = setup.exportData();
        JSONObject jsonData = new JSONObject(result.getData());
        JSONObject settingsData = (JSONObject) jsonData.get("preferences");
        JSONObject globalSettingsData = (JSONObject) settingsData.get("global");
        JSONObject defaultsData = (JSONObject) settingsData.get("defaults");
        JSONObject systemSettingsData = (JSONObject) settingsData.get("system");
        assertEquals(25, globalSettingsData.getInt("preferencePingCount"));
        assertEquals(25, globalSettingsData.getInt("preferenceConnectCount"));
        assertEquals(30, systemSettingsData.getInt("preferenceExternalStorageType"));
        assertEquals(5, systemSettingsData.getInt("preferenceTheme"));
        assertEquals(100000, defaultsData.getInt("preferencePort"));
        assertEquals(-5, defaultsData.getInt("preferenceInterval"));
    }

    @Test
    public void testImportDatabase() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = networkTaskDAO.insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = networkTaskDAO.insertNetworkTask(getNetworkTask3());
        LogEntry task1Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        LogEntry task1Entry2 = logDAO.insertAndDeleteLog(getLogEntry2(task1.getId()));
        LogEntry task1Entry3 = logDAO.insertAndDeleteLog(getLogEntry3(task1.getId()));
        LogEntry task2Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task2.getId()));
        LogEntry task2Entry2 = logDAO.insertAndDeleteLog(getLogEntry2(task2.getId()));
        LogEntry task2Entry3 = logDAO.insertAndDeleteLog(getLogEntry3(task2.getId()));
        LogEntry task3Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task3.getId()));
        LogEntry task3Entry2 = logDAO.insertAndDeleteLog(getLogEntry2(task3.getId()));
        LogEntry task3Entry3 = logDAO.insertAndDeleteLog(getLogEntry3(task3.getId()));
        SystemSetupResult exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        SystemSetupResult importResult = setup.importData(exportResult.getData());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        List<NetworkTask> tasks = networkTaskDAO.readAllNetworkTasks();
        NetworkTask readTask1 = tasks.get(0);
        NetworkTask readTask2 = tasks.get(1);
        NetworkTask readTask3 = tasks.get(2);
        assertTrue(task1.isTechnicallyEqual(readTask1));
        assertTrue(task2.isTechnicallyEqual(readTask2));
        assertTrue(task3.isTechnicallyEqual(readTask3));
        assertFalse(readTask1.isRunning());
        assertFalse(readTask2.isRunning());
        assertFalse(readTask3.isRunning());
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(readTask1.getId());
        LogEntry readEntry1 = entries.get(0);
        LogEntry readEntry2 = entries.get(1);
        LogEntry readEntry3 = entries.get(2);
        logEntryEquals(task1Entry1, readEntry1);
        logEntryEquals(task1Entry2, readEntry2);
        logEntryEquals(task1Entry3, readEntry3);
        assertEquals(readTask1.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry3.getNetworkTaskId());
        entries = logDAO.readAllLogsForNetworkTask(readTask2.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        logEntryEquals(task2Entry1, readEntry1);
        logEntryEquals(task2Entry2, readEntry2);
        logEntryEquals(task2Entry3, readEntry3);
        assertEquals(readTask2.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry3.getNetworkTaskId());
        entries = logDAO.readAllLogsForNetworkTask(readTask3.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        logEntryEquals(task3Entry1, readEntry1);
        logEntryEquals(task3Entry2, readEntry2);
        logEntryEquals(task3Entry3, readEntry3);
        assertEquals(readTask3.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry3.getNetworkTaskId());
    }

    @Test
    public void testImportDatabaseInvalidTask() {
        NetworkTask task1 = getNetworkTask1();
        task1.setAddress("127.12..1.1.1.1");
        networkTaskDAO.insertNetworkTask(task1);
        SystemSetupResult exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        SystemSetupResult importResult = setup.importData(exportResult.getData());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        task1 = getNetworkTask1();
        task1.setAccessType(null);
        networkTaskDAO.insertNetworkTask(task1);
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        importResult = setup.importData(exportResult.getData());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        task1 = getNetworkTask1();
        task1.setPort(100000);
        networkTaskDAO.insertNetworkTask(task1);
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        importResult = setup.importData(exportResult.getData());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        task1 = getNetworkTask1();
        task1.setInterval(-5);
        networkTaskDAO.insertNetworkTask(task1);
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        importResult = setup.importData(exportResult.getData());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
    }

    @Test
    public void testImportSettings() {
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(1);
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
        preferenceManager.setPreferenceTheme(1);
        SystemSetupResult exportResult = setup.exportData();
        preferenceManager.removeAllPreferences();
        SystemSetupResult importResult = setup.importData(exportResult.getData());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportSettingsInvalid() {
        preferenceManager.setPreferencePingCount(20);
        preferenceManager.setPreferenceConnectCount(20);
        preferenceManager.setPreferenceExternalStorageType(2);
        preferenceManager.setPreferencePort(100000);
        preferenceManager.setPreferenceInterval(-5);
        preferenceManager.setPreferenceTheme(5);
        SystemSetupResult exportResult = setup.exportData();
        preferenceManager.removeAllPreferences();
        SystemSetupResult importResult = setup.importData(exportResult.getData());
        assertTrue(importResult.isSuccess());
        assertEquals(exportResult.getData(), importResult.getData());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportFailure() throws Exception {
        SystemSetupResult result = setup.importData("failure");
        assertFalse(result.isSuccess());
        assertEquals("failure", result.getData());
        networkTaskDAO.insertNetworkTask(getNetworkTask1());
        result = setup.exportData();
        JSONObject jsonResult = new JSONObject(result.getData());
        jsonResult.put("database", "failure");
        result = setup.importData(jsonResult.toString());
        assertFalse(result.isSuccess());
        assertEquals(jsonResult.toString(), result.getData());
        result = setup.exportData();
        jsonResult = new JSONObject(result.getData());
        JSONObject database = (JSONObject) jsonResult.get("database");
        database.put("25", "failure");
        jsonResult.put("database", database);
        result = setup.importData(jsonResult.toString());
        assertFalse(result.isSuccess());
        assertEquals(jsonResult.toString(), result.getData());
        result = setup.exportData();
        jsonResult = new JSONObject(result.getData());
        jsonResult.put("preferences", "failure");
        result = setup.importData(jsonResult.toString());
        assertFalse(result.isSuccess());
        assertEquals(jsonResult.toString(), result.getData());
        result = setup.exportData();
        jsonResult = new JSONObject(result.getData());
        JSONObject settings = (JSONObject) jsonResult.get("preferences");
        settings.put("system", "failure");
        jsonResult.put("preferences", settings);
        result = setup.importData(jsonResult.toString());
        assertFalse(result.isSuccess());
        assertEquals(jsonResult.toString(), result.getData());
    }

    private void logEntryEquals(LogEntry entry1, LogEntry entry2) {
        assertEquals(entry1.isSuccess(), entry2.isSuccess());
        assertEquals(entry1.getTimestamp(), entry2.getTimestamp());
        assertEquals(entry1.getMessage(), entry2.getMessage());
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        return task;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(2);
        task.setSchedulerId(0);
        task.setInstances(2);
        task.setAddress("host.com");
        task.setPort(21);
        task.setAccessType(AccessType.CONNECT);
        task.setInterval(1);
        task.setOnlyWifi(true);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        return task;
    }

    private NetworkTask getNetworkTask3() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(3);
        task.setSchedulerId(0);
        task.setInstances(3);
        task.setAddress("test.com");
        task.setPort(456);
        task.setAccessType(AccessType.PING);
        task.setInterval(200);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        task.setLastScheduled(0);
        return task;
    }

    private LogEntry getLogEntry1(long networkTaskId) {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(networkTaskId);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(789);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }

    private LogEntry getLogEntry2(long networkTaskId) {
        LogEntry insertedLogEntry2 = new LogEntry();
        insertedLogEntry2.setId(0);
        insertedLogEntry2.setNetworkTaskId(networkTaskId);
        insertedLogEntry2.setSuccess(false);
        insertedLogEntry2.setTimestamp(456);
        insertedLogEntry2.setMessage("TestMessage2");
        return insertedLogEntry2;
    }

    private LogEntry getLogEntry3(long networkTaskId) {
        LogEntry insertedLogEntry3 = new LogEntry();
        insertedLogEntry3.setId(0);
        insertedLogEntry3.setNetworkTaskId(networkTaskId);
        insertedLogEntry3.setSuccess(true);
        insertedLogEntry3.setTimestamp(123);
        insertedLogEntry3.setMessage("TestMessage3");
        return insertedLogEntry3;
    }
}
