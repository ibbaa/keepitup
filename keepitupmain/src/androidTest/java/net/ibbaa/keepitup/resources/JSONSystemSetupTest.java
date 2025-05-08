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
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.AccessTypeDataDAO;
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.db.LogDAO;
import net.ibbaa.keepitup.db.NetworkTaskDAO;
import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.model.Time;
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
    private AccessTypeDataDAO accessTypeDataDAO;
    private PreferenceManager preferenceManager;
    private JSONSystemSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        accessTypeDataDAO = new AccessTypeDataDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        accessTypeDataDAO.deleteAllAccessTypeData();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        setup = new JSONSystemSetup(TestRegistry.getContext());
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        accessTypeDataDAO.deleteAllAccessTypeData();
        preferenceManager.removeAllPreferences();
    }

    @Test
    public void testVersion() throws Exception {
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        JSONObject jsonData = new JSONObject(result.data());
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
        AccessTypeData accessData1 = accessTypeDataDAO.insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData accessData2 = accessTypeDataDAO.insertAccessTypeData(getAccessTypeData2(task2.getId()));
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        JSONObject jsonData = new JSONObject(result.data());
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
        JSONObject task1AccessDataJSON = (JSONObject) task1Data.get("accesstypedata");
        JSONObject task2AccessDataJSON = (JSONObject) task2Data.get("accesstypedata");
        AccessTypeData task1AccessData = new AccessTypeData(JSONUtil.toMap((task1AccessDataJSON)));
        AccessTypeData task2AccessData = new AccessTypeData(JSONUtil.toMap((task2AccessDataJSON)));
        assertTrue(task1AccessData.isEqual(accessData1));
        assertTrue(task2AccessData.isEqual(accessData2));
    }

    @Test
    public void testExportDatabaseWithIntervals() throws Exception {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        LogEntry task1Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        Interval interval1 = intervalDAO.insertInterval(getInterval1());
        Interval interval2 = intervalDAO.insertInterval(getInterval2());
        Interval interval3 = intervalDAO.insertInterval(getInterval3());
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        JSONObject jsonData = new JSONObject(result.data());
        JSONObject databaseData = (JSONObject) jsonData.get("database");
        JSONObject task1Data = (JSONObject) databaseData.get(String.valueOf(task1.getId()));
        JSONObject task1NetworkTaskData = (JSONObject) task1Data.get("networktask");
        NetworkTask task1NetworkTask = new NetworkTask(JSONUtil.toMap(task1NetworkTaskData));
        assertTrue(task1.isEqual(task1NetworkTask));
        JSONArray task1LogData = (JSONArray) task1Data.get("logentry");
        LogEntry task1LogEntry1 = new LogEntry(JSONUtil.toMap((JSONObject) task1LogData.get(0)));
        assertTrue(task1Entry1.isEqual(task1LogEntry1));
        JSONArray intervalData = (JSONArray) databaseData.get("interval");
        Interval readInterval1 = new Interval(JSONUtil.toMap((JSONObject) intervalData.get(0)));
        Interval readInterval2 = new Interval(JSONUtil.toMap((JSONObject) intervalData.get(1)));
        Interval readinterval3 = new Interval(JSONUtil.toMap((JSONObject) intervalData.get(2)));
        assertTrue(interval1.isEqual(readInterval2));
        assertTrue(interval2.isEqual(readInterval1));
        assertTrue(interval3.isEqual(readinterval3));
    }

    @Test
    public void testExportDatabaseInvalidTask() throws Exception {
        NetworkTask task1 = getNetworkTask1();
        task1.setAddress("127.12..1.1.1.1");
        task1 = networkTaskDAO.insertNetworkTask(task1);
        AccessTypeData data1 = getAccessTypeData1(task1.getId());
        data1.setPingCount(25);
        AccessTypeData accessData1 = accessTypeDataDAO.insertAccessTypeData(data1);
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        JSONObject jsonData = new JSONObject(result.data());
        JSONObject databaseData = (JSONObject) jsonData.get("database");
        JSONObject task1Data = (JSONObject) databaseData.get(String.valueOf(task1.getId()));
        JSONObject task1NetworkTaskData = (JSONObject) task1Data.get("networktask");
        NetworkTask task1NetworkTask = new NetworkTask(JSONUtil.toMap(task1NetworkTaskData));
        assertTrue(task1.isEqual(task1NetworkTask));
        JSONObject task1AccessDataJSON = (JSONObject) task1Data.get("accesstypedata");
        AccessTypeData task1AccessData = new AccessTypeData(JSONUtil.toMap((task1AccessDataJSON)));
        assertTrue(task1AccessData.isEqual(accessData1));
    }

    @Test
    public void testExportDatabaseInvalidInterval() throws Exception {
        Interval interval = intervalDAO.insertInterval(new Interval());
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        JSONObject jsonData = new JSONObject(result.data());
        JSONObject databaseData = (JSONObject) jsonData.get("database");
        JSONArray intervalData = (JSONArray) databaseData.get("interval");
        Interval intervalRead = new Interval(JSONUtil.toMap((JSONObject) intervalData.get(0)));
        assertTrue(interval.isEqual(intervalRead));
    }

    @Test
    public void testExportSettings() throws Exception {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(2);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceArbitraryDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceArbitraryLogFolder("folder");
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceIgnoreSSLError(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        preferenceManager.setPreferencePingPackageSize(15);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("fileExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(5);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        SystemSetupResult result = setup.exportData();
        JSONObject jsonData = new JSONObject(result.data());
        JSONObject settingsData = (JSONObject) jsonData.get("preferences");
        JSONObject globalSettingsData = (JSONObject) settingsData.get("global");
        JSONObject defaultsData = (JSONObject) settingsData.get("defaults");
        JSONObject systemSettingsData = (JSONObject) settingsData.get("system");
        assertTrue(globalSettingsData.getBoolean("preferenceNotificationInactiveNetwork"));
        assertEquals(NotificationType.CHANGE, NotificationType.forCode(globalSettingsData.getInt("preferenceNotificationType")));
        assertEquals(2, globalSettingsData.getInt("preferenceNotificationType"));
        assertFalse(globalSettingsData.getBoolean("preferenceSuspensionEnabled"));
        assertTrue(globalSettingsData.getBoolean("preferenceEnforceDefaultPingPackageSize"));
        assertTrue(globalSettingsData.getBoolean("preferenceDownloadExternalStorage"));
        assertEquals("folder", globalSettingsData.getString("preferenceDownloadFolder"));
        assertEquals("folder", globalSettingsData.getString("preferenceArbitraryDownloadFolder"));
        assertTrue(globalSettingsData.getBoolean("preferenceDownloadKeep"));
        assertFalse(globalSettingsData.getBoolean("preferenceDownloadFollowsRedirects"));
        assertEquals("folder", globalSettingsData.getString("preferenceArbitraryLogFolder"));
        assertEquals(AccessType.CONNECT, AccessType.forCode(defaultsData.getInt("preferenceAccessType")));
        assertEquals("address", defaultsData.getString("preferenceAddress"));
        assertEquals(123, defaultsData.getInt("preferencePort"));
        assertEquals(456, defaultsData.getInt("preferenceInterval"));
        assertEquals(5, defaultsData.getInt("preferencePingCount"));
        assertEquals(10, defaultsData.getInt("preferenceConnectCount"));
        assertTrue(defaultsData.getBoolean("preferenceStopOnSuccess"));
        assertTrue(defaultsData.getBoolean("preferenceIgnoreSSLError"));
        assertTrue(defaultsData.getBoolean("preferenceOnlyWifi"));
        assertTrue(defaultsData.getBoolean("preferenceNotification"));
        assertTrue(defaultsData.getBoolean("preferenceHighPrio"));
        assertEquals(15, defaultsData.getInt("preferencePingPackageSize"));
        assertEquals("folderImport", systemSettingsData.getString("preferenceImportFolder"));
        assertEquals("folderExport", systemSettingsData.getString("preferenceExportFolder"));
        assertEquals("fileExport", systemSettingsData.getString("preferenceLastArbitraryExportFile"));
        assertEquals(30, systemSettingsData.getInt("preferenceExternalStorageType"));
        assertTrue(systemSettingsData.getBoolean("preferenceFileLoggerEnabled"));
        assertTrue(systemSettingsData.getBoolean("preferenceFileDumpEnabled"));
        assertEquals(5, systemSettingsData.getInt("preferenceTheme"));
        assertTrue(systemSettingsData.getBoolean("preferenceAllowArbitraryFileLocation"));
        assertTrue(systemSettingsData.getBoolean("preferenceAlarmOnHighPrio"));
        assertTrue(systemSettingsData.getBoolean("preferenceAskedNotificationPermission"));
        assertTrue(systemSettingsData.getBoolean("preferenceAlarmInfoShown"));
    }

    @Test
    public void testExportSettingsInvalid() throws Exception {
        preferenceManager.setPreferenceNotificationAfterFailures(21);
        preferenceManager.setPreferencePingPackageSize(12345678);
        preferenceManager.setPreferenceExternalStorageType(30);
        preferenceManager.setPreferencePort(100000);
        preferenceManager.setPreferenceInterval(-5);
        preferenceManager.setPreferencePingCount(25);
        preferenceManager.setPreferenceConnectCount(25);
        preferenceManager.setPreferenceTheme(5);
        SystemSetupResult result = setup.exportData();
        JSONObject jsonData = new JSONObject(result.data());
        JSONObject settingsData = (JSONObject) jsonData.get("preferences");
        JSONObject globalSettingsData = (JSONObject) settingsData.get("global");
        JSONObject defaultsData = (JSONObject) settingsData.get("defaults");
        JSONObject systemSettingsData = (JSONObject) settingsData.get("system");
        assertEquals(21, globalSettingsData.getInt("preferenceNotificationAfterFailures"));
        assertEquals(30, systemSettingsData.getInt("preferenceExternalStorageType"));
        assertEquals(5, systemSettingsData.getInt("preferenceTheme"));
        assertEquals(100000, defaultsData.getInt("preferencePort"));
        assertEquals(-5, defaultsData.getInt("preferenceInterval"));
        assertEquals(25, defaultsData.getInt("preferencePingCount"));
        assertEquals(25, defaultsData.getInt("preferenceConnectCount"));
        assertEquals(12345678, defaultsData.getInt("preferencePingPackageSize"));
    }

    @Test
    public void testImportVersionMismatch() throws Exception {
        networkTaskDAO.insertNetworkTask(getNetworkTask1());
        SystemSetupResult exportResult = setup.exportData();
        JSONObject jsonData = new JSONObject(exportResult.data());
        int version = jsonData.getInt("dbversion");
        jsonData.put("dbversion", version + 1);
        SystemSetupResult importResult = setup.importData(jsonData.toString());
        assertFalse(importResult.success());
        assertTrue(importResult.versionMismatch());
    }

    @Test
    public void testCheckImportPossible() throws Exception {
        networkTaskDAO.insertNetworkTask(getNetworkTask1());
        SystemSetupResult exportResult = setup.exportData();
        JSONObject jsonData = new JSONObject(exportResult.data());
        int version = jsonData.getInt("dbversion");
        jsonData.put("dbversion", version + 1);
        SystemSetupResult importResult = setup.checkImportPossible(jsonData.toString());
        assertFalse(importResult.success());
        assertTrue(importResult.versionMismatch());
        importResult = setup.checkImportPossible("wrong");
        assertFalse(importResult.success());
        assertFalse(importResult.versionMismatch());
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
        AccessTypeData accessData1 = accessTypeDataDAO.insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData accessData2 = accessTypeDataDAO.insertAccessTypeData(getAccessTypeData2(task2.getId()));
        SystemSetupResult exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        accessTypeDataDAO.deleteAllAccessTypeData();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
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
        assertTrue(task1Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task1Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task1Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask1.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry3.getNetworkTaskId());
        entries = logDAO.readAllLogsForNetworkTask(readTask2.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        assertTrue(task2Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task2Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task2Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask2.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry3.getNetworkTaskId());
        entries = logDAO.readAllLogsForNetworkTask(readTask3.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        assertTrue(task3Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task3Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task3Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask3.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry3.getNetworkTaskId());
        AccessTypeData readAccessData1 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(readTask1.getId());
        AccessTypeData readAccessData2 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(readTask2.getId());
        AccessTypeData readAccessData3 = accessTypeDataDAO.readAccessTypeDataForNetworkTask(readTask3.getId());
        assertTrue(accessData1.isTechnicallyEqual(readAccessData1));
        assertTrue(accessData2.isTechnicallyEqual(readAccessData2));
        AccessTypeData accessData3 = new AccessTypeData(TestRegistry.getContext());
        accessData3.setNetworkTaskId(readAccessData3.getId());
        assertTrue(accessData3.isTechnicallyEqual(readAccessData3));
    }

    @Test
    public void testImportDatabaseWithIntervals() {
        NetworkTask task1 = networkTaskDAO.insertNetworkTask(getNetworkTask1());
        LogEntry task1Entry1 = logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        Interval interval1 = intervalDAO.insertInterval(getInterval1());
        Interval interval2 = intervalDAO.insertInterval(getInterval2());
        Interval interval3 = intervalDAO.insertInterval(getInterval3());
        SystemSetupResult exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        intervalDAO.deleteAllIntervals();
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertFalse(logDAO.readAllLogs().isEmpty());
        assertFalse(intervalDAO.readAllIntervals().isEmpty());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<NetworkTask> tasks = networkTaskDAO.readAllNetworkTasks();
        NetworkTask readTask1 = tasks.get(0);
        assertTrue(task1.isTechnicallyEqual(readTask1));
        List<LogEntry> entries = logDAO.readAllLogsForNetworkTask(readTask1.getId());
        LogEntry readEntry1 = entries.get(0);
        assertTrue(task1Entry1.isTechnicallyEqual(readEntry1));
        List<Interval> intervals = intervalDAO.readAllIntervals();
        Interval readInterval1 = intervals.get(0);
        Interval readInterval2 = intervals.get(1);
        Interval readInterval3 = intervals.get(2);
        assertTrue(interval1.isEqual(readInterval2));
        assertTrue(interval2.isEqual(readInterval1));
        assertTrue(interval3.isEqual(readInterval3));
    }

    @Test
    public void testImportDatabaseWithIntervalsOverlapDays() {
        Interval interval1 = getInterval1();
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(23);
        start.setMinute(59);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(29);
        interval2.setEnd(end);
        intervalDAO.insertInterval(interval1);
        intervalDAO.insertInterval(interval2);
        SystemSetupResult exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(2, intervals.size());
        Interval readInterval1 = intervals.get(0);
        Interval readInterval2 = intervals.get(1);
        assertTrue(interval1.isEqual(readInterval1));
        assertTrue(interval2.isEqual(readInterval2));
    }

    @Test
    public void testImportDatabaseInvalidTask() {
        NetworkTask task1 = getNetworkTask1();
        task1.setAddress("127.12..1.1.1.1");
        task1 = networkTaskDAO.insertNetworkTask(task1);
        logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData1(task1.getId()));
        SystemSetupResult exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        accessTypeDataDAO.deleteAllAccessTypeData();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        task1 = getNetworkTask1();
        task1.setAccessType(null);
        task1 = networkTaskDAO.insertNetworkTask(task1);
        logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData1(task1.getId()));
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        accessTypeDataDAO.deleteAllAccessTypeData();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        task1 = getNetworkTask1();
        task1.setPort(100000);
        task1 = networkTaskDAO.insertNetworkTask(task1);
        logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData1(task1.getId()));
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        accessTypeDataDAO.deleteAllAccessTypeData();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        task1 = getNetworkTask1();
        task1.setInterval(-5);
        task1 = networkTaskDAO.insertNetworkTask(task1);
        logDAO.insertAndDeleteLog(getLogEntry1(task1.getId()));
        accessTypeDataDAO.insertAccessTypeData(getAccessTypeData1(task1.getId()));
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        accessTypeDataDAO.deleteAllAccessTypeData();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(logDAO.readAllLogs().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testImportDatabaseInvalidAccessTypeData() {
        NetworkTask task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        AccessTypeData data1 = getAccessTypeData1(task1.getId());
        data1.setPingCount(11);
        accessTypeDataDAO.insertAccessTypeData(data1);
        SystemSetupResult exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        data1 = getAccessTypeData1(task1.getId());
        data1.setPingPackageSize(12345678);
        accessTypeDataDAO.insertAccessTypeData(data1);
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        task1 = getNetworkTask1();
        task1 = networkTaskDAO.insertNetworkTask(task1);
        data1 = getAccessTypeData1(task1.getId());
        data1.setConnectCount(0);
        accessTypeDataDAO.insertAccessTypeData(data1);
        exportResult = setup.exportData();
        networkTaskDAO.deleteAllNetworkTasks();
        accessTypeDataDAO.deleteAllAccessTypeData();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertFalse(networkTaskDAO.readAllNetworkTasks().isEmpty());
        assertTrue(accessTypeDataDAO.readAllAccessTypeData().isEmpty());
    }

    @Test
    public void testImportDatabaseInvalidInterval() {
        intervalDAO.insertInterval(new Interval());
        SystemSetupResult exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        Interval interval1 = getInterval1();
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(12);
        start.setMinute(0);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(12);
        end.setMinute(61);
        interval2.setEnd(end);
        intervalDAO.insertInterval(interval1);
        intervalDAO.insertInterval(interval2);
        exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        Interval interval = intervals.get(0);
        assertTrue(interval1.isEqual(interval));
    }

    @Test
    public void testImportDatabaseInvalidIntervalOverlap() {
        intervalDAO.insertInterval(new Interval());
        SystemSetupResult exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        Interval interval1 = getInterval1();
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(11);
        start.setMinute(1);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(20);
        interval2.setEnd(end);
        intervalDAO.insertInterval(interval1);
        intervalDAO.insertInterval(interval2);
        exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        Interval interval = intervals.get(0);
        assertTrue(interval1.isEqual(interval));
    }

    @Test
    public void testImportDatabaseInvalidIntervalMinDuration() {
        intervalDAO.insertInterval(new Interval());
        SystemSetupResult exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        Interval interval1 = getInterval1();
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(12);
        start.setMinute(13);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(12);
        end.setMinute(27);
        interval2.setEnd(end);
        intervalDAO.insertInterval(interval1);
        intervalDAO.insertInterval(interval2);
        exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        Interval interval = intervals.get(0);
        assertTrue(interval1.isEqual(interval));
    }

    @Test
    public void testImportDatabaseInvalidIntervalOverlapDistance() {
        Interval interval1 = getInterval1();
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(1);
        start.setMinute(1);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(5);
        interval2.setEnd(end);
        intervalDAO.insertInterval(interval1);
        intervalDAO.insertInterval(interval2);
        SystemSetupResult exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        Interval readInterval1 = intervals.get(0);
        assertTrue(interval2.isEqual(readInterval1));
    }

    @Test
    public void testImportDatabaseInvalidIntervalOverlapDistanceAfter() {
        Interval interval1 = getInterval1();
        Interval interval2 = getInterval2();
        Time start = new Time();
        start.setHour(11);
        start.setMinute(20);
        interval2.setStart(start);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(25);
        interval2.setEnd(end);
        intervalDAO.insertInterval(interval1);
        intervalDAO.insertInterval(interval2);
        SystemSetupResult exportResult = setup.exportData();
        intervalDAO.deleteAllIntervals();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
        Interval readInterval1 = intervals.get(0);
        assertTrue(interval1.isEqual(readInterval1));
    }

    @Test
    public void testImportSettings() {
        preferenceManager.setPreferenceNotificationInactiveNetwork(true);
        preferenceManager.setPreferenceNotificationType(NotificationType.CHANGE);
        preferenceManager.setPreferenceNotificationAfterFailures(8);
        preferenceManager.setPreferenceSuspensionEnabled(false);
        preferenceManager.setPreferenceEnforceDefaultPingPackageSize(true);
        preferenceManager.setPreferenceDownloadExternalStorage(true);
        preferenceManager.setPreferenceExternalStorageType(1);
        preferenceManager.setPreferenceDownloadFolder("folder");
        preferenceManager.setPreferenceDownloadKeep(true);
        preferenceManager.setPreferenceDownloadFollowsRedirects(false);
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("address");
        preferenceManager.setPreferencePort(123);
        preferenceManager.setPreferenceInterval(456);
        preferenceManager.setPreferencePingCount(5);
        preferenceManager.setPreferenceConnectCount(10);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceIgnoreSSLError(true);
        preferenceManager.setPreferenceOnlyWifi(true);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        preferenceManager.setPreferencePingPackageSize(1234);
        preferenceManager.setPreferenceImportFolder("folderImport");
        preferenceManager.setPreferenceExportFolder("folderExport");
        preferenceManager.setPreferenceLastArbitraryExportFile("fileExport");
        preferenceManager.setPreferenceFileLoggerEnabled(true);
        preferenceManager.setPreferenceFileDumpEnabled(true);
        preferenceManager.setPreferenceTheme(1);
        preferenceManager.setPreferenceAllowArbitraryFileLocation(true);
        preferenceManager.setPreferenceAlarmOnHighPrio(true);
        preferenceManager.setPreferenceAskedNotificationPermission(true);
        preferenceManager.setPreferenceAlarmInfoShown(true);
        SystemSetupResult exportResult = setup.exportData();
        preferenceManager.removeAllPreferences();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertEquals(8, preferenceManager.getPreferenceNotificationAfterFailures());
        assertFalse(preferenceManager.getPreferenceSuspensionEnabled());
        assertTrue(preferenceManager.getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("folder", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceDownloadFollowsRedirects());
        assertEquals(AccessType.CONNECT, preferenceManager.getPreferenceAccessType());
        assertEquals("address", preferenceManager.getPreferenceAddress());
        assertEquals(123, preferenceManager.getPreferencePort());
        assertEquals(456, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(10, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
        assertTrue(preferenceManager.getPreferenceIgnoreSSLError());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertTrue(preferenceManager.getPreferenceHighPrio());
        assertEquals(1234, preferenceManager.getPreferencePingPackageSize());
        assertEquals("folderImport", preferenceManager.getPreferenceImportFolder());
        assertEquals("folderExport", preferenceManager.getPreferenceExportFolder());
        assertEquals("fileExport", preferenceManager.getPreferenceLastArbitraryExportFile());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
        assertEquals(1, preferenceManager.getPreferenceTheme());
        assertTrue(preferenceManager.getPreferenceAllowArbitraryFileLocation());
        assertTrue(preferenceManager.getPreferenceAlarmOnHighPrio());
        assertTrue(preferenceManager.getPreferenceAskedNotificationPermission());
        assertTrue(preferenceManager.getPreferenceAlarmInfoShown());
    }

    @Test
    public void testImportSettingsInvalid() {
        preferenceManager.setPreferenceNotificationAfterFailures(21);
        preferenceManager.setPreferencePingPackageSize(12345678);
        preferenceManager.setPreferenceExternalStorageType(2);
        preferenceManager.setPreferencePort(100000);
        preferenceManager.setPreferenceInterval(-5);
        preferenceManager.setPreferencePingCount(20);
        preferenceManager.setPreferenceConnectCount(20);
        preferenceManager.setPreferenceTheme(5);
        SystemSetupResult exportResult = setup.exportData();
        preferenceManager.removeAllPreferences();
        SystemSetupResult importResult = setup.importData(exportResult.data());
        assertTrue(importResult.success());
        assertEquals(exportResult.data(), importResult.data());
        assertEquals(1, preferenceManager.getPreferenceNotificationAfterFailures());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals(-1, preferenceManager.getPreferenceTheme());
    }

    @Test
    public void testImportMigration0To3() throws Exception {
        SystemSetupResult exportResult = setup.exportData();
        JSONObject root = new JSONObject(exportResult.data());
        JSONObject settings = root.getJSONObject("preferences");
        JSONObject globalSettings = settings.getJSONObject("global");
        JSONObject defaultSettings = settings.getJSONObject("defaults");
        root.remove("dbversion");
        defaultSettings.remove("preferencePingCount");
        defaultSettings.remove("preferenceConnectCount");
        globalSettings.put("preferencePingCount", 5);
        globalSettings.put("preferenceConnectCount", 5);
        SystemSetupResult importResult = setup.importData(root.toString());
        assertTrue(importResult.success());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testImportMigration2To3() throws Exception {
        SystemSetupResult exportResult = setup.exportData();
        JSONObject root = new JSONObject(exportResult.data());
        JSONObject settings = root.getJSONObject("preferences");
        JSONObject globalSettings = settings.getJSONObject("global");
        JSONObject defaultSettings = settings.getJSONObject("defaults");
        root.put("dbversion", 2);
        defaultSettings.remove("preferencePingCount");
        defaultSettings.remove("preferenceConnectCount");
        globalSettings.put("preferencePingCount", 5);
        globalSettings.put("preferenceConnectCount", 5);
        SystemSetupResult importResult = setup.importData(root.toString());
        assertTrue(importResult.success());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
    }

    @Test
    public void testImportFailure() throws Exception {
        SystemSetupResult result = setup.importData("failure");
        assertFalse(result.success());
        assertEquals("failure", result.data());
        networkTaskDAO.insertNetworkTask(getNetworkTask1());
        result = setup.exportData();
        JSONObject jsonResult = new JSONObject(result.data());
        jsonResult.put("database", "failure");
        result = setup.importData(jsonResult.toString());
        assertFalse(result.success());
        assertEquals(jsonResult.toString(), result.data());
        result = setup.exportData();
        jsonResult = new JSONObject(result.data());
        JSONObject database = (JSONObject) jsonResult.get("database");
        database.put("25", "failure");
        jsonResult.put("database", database);
        result = setup.importData(jsonResult.toString());
        assertFalse(result.success());
        assertEquals(jsonResult.toString(), result.data());
        result = setup.exportData();
        jsonResult = new JSONObject(result.data());
        jsonResult.put("preferences", "failure");
        result = setup.importData(jsonResult.toString());
        assertFalse(result.success());
        assertEquals(jsonResult.toString(), result.data());
        result = setup.exportData();
        jsonResult = new JSONObject(result.data());
        JSONObject settings = (JSONObject) jsonResult.get("preferences");
        settings.put("system", "failure");
        jsonResult.put("preferences", settings);
        result = setup.importData(jsonResult.toString());
        assertFalse(result.success());
        assertEquals(jsonResult.toString(), result.data());
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
        task.setFailureCount(1);
        task.setHighPrio(true);
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
        task.setFailureCount(2);
        task.setHighPrio(false);
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
        task.setFailureCount(3);
        task.setHighPrio(false);
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

    private Interval getInterval1() {
        Interval interval = new Interval();
        interval.setId(1);
        Time start = new Time();
        start.setHour(10);
        start.setMinute(11);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(12);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval2() {
        Interval interval = new Interval();
        interval.setId(2);
        Time start = new Time();
        start.setHour(1);
        start.setMinute(1);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(2);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval3() {
        Interval interval = new Interval();
        interval.setId(3);
        Time start = new Time();
        start.setHour(22);
        start.setMinute(15);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(23);
        end.setMinute(59);
        interval.setEnd(end);
        return interval;
    }

    private AccessTypeData getAccessTypeData1(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }

    private AccessTypeData getAccessTypeData2(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(1);
        data.setPingPackageSize(55);
        data.setConnectCount(5);
        data.setStopOnSuccess(true);
        data.setIgnoreSSLError(true);
        return data;
    }
}
