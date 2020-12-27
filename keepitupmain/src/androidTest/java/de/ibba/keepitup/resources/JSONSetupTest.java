package de.ibba.keepitup.resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.db.LogDAO;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.util.JSONUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class JSONSetupTest {

    private NetworkTaskDAO networkTaskDAO;
    private LogDAO logDAO;
    private PreferenceManager preferenceManager;
    private JSONSetup setup;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        networkTaskDAO = new NetworkTaskDAO(TestRegistry.getContext());
        logDAO = new LogDAO(TestRegistry.getContext());
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.removeAllPreferences();
        setup = new JSONSetup(TestRegistry.getContext());
    }

    @After
    public void afterEachTestMethod() {
        networkTaskDAO.deleteAllNetworkTasks();
        logDAO.deleteAllLogs();
        preferenceManager.removeAllPreferences();
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
        JSONObject jsonData = setup.export();
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
    public void testExportSettings() throws Exception {
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
        JSONObject jsonData = setup.export();
        JSONObject settingsData = (JSONObject) jsonData.get("preferences");
        JSONObject globalSettingsData = (JSONObject) settingsData.get("global");
        JSONObject defaultsData = (JSONObject) settingsData.get("defaults");
        JSONObject systemSettingsData = (JSONObject) settingsData.get("system");
        assertEquals(5, globalSettingsData.getInt("preferencePingCount"));
        assertEquals(10, globalSettingsData.getInt("preferenceConnectCount"));
        assertTrue(globalSettingsData.getBoolean("preferenceNotificationInactiveNetwork"));
        assertTrue(globalSettingsData.getBoolean("preferenceDownloadExternalStorage"));
        assertEquals(30, globalSettingsData.getInt("preferenceExternalStorageType"));
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
        assertTrue(systemSettingsData.getBoolean("preferenceFileLoggerEnabled"));
        assertTrue(systemSettingsData.getBoolean("preferenceFileDumpEnabled"));
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
        task.setAccessType(null);
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
        task.setAddress(null);
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
