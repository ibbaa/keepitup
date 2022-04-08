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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.util.StreamUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ImportTaskTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testImport() throws Exception {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        LogEntry task1Entry1 = getLogDAO().insertAndDeleteLog(getLogEntry1(task1.getId()));
        LogEntry task1Entry2 = getLogDAO().insertAndDeleteLog(getLogEntry2(task1.getId()));
        LogEntry task1Entry3 = getLogDAO().insertAndDeleteLog(getLogEntry3(task1.getId()));
        LogEntry task2Entry1 = getLogDAO().insertAndDeleteLog(getLogEntry1(task2.getId()));
        LogEntry task2Entry2 = getLogDAO().insertAndDeleteLog(getLogEntry2(task2.getId()));
        LogEntry task2Entry3 = getLogDAO().insertAndDeleteLog(getLogEntry3(task2.getId()));
        LogEntry task3Entry1 = getLogDAO().insertAndDeleteLog(getLogEntry1(task3.getId()));
        LogEntry task3Entry2 = getLogDAO().insertAndDeleteLog(getLogEntry2(task3.getId()));
        LogEntry task3Entry3 = getLogDAO().insertAndDeleteLog(getLogEntry3(task3.getId()));
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
        getPreferenceManager().setPreferenceAccessType(AccessType.CONNECT);
        getPreferenceManager().setPreferenceAddress("address");
        getPreferenceManager().setPreferencePort(123);
        getPreferenceManager().setPreferenceInterval(456);
        getPreferenceManager().setPreferenceOnlyWifi(true);
        getPreferenceManager().setPreferenceNotification(true);
        getPreferenceManager().setPreferenceImportFolder("folderImport");
        getPreferenceManager().setPreferenceExportFolder("folderExport");
        getPreferenceManager().setPreferenceFileLoggerEnabled(true);
        getPreferenceManager().setPreferenceFileDumpEnabled(true);
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.exportData();
        assertTrue(result.isSuccess());
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getPreferenceManager().removeAllPreferences();
        File folder = getFileManager().getExternalRootDirectory(0);
        StreamUtil.stringToOutputStream(result.getData(), new FileOutputStream(new File(folder, "test.json")), Charsets.UTF_8);
        ImportTask task = new ImportTask(getActivity(activityScenario), folder, "test.json");
        task.runInBackground();
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        NetworkTask readTask1 = tasks.get(0);
        NetworkTask readTask2 = tasks.get(1);
        NetworkTask readTask3 = tasks.get(2);
        assertTrue(task1.isTechnicallyEqual(readTask1));
        assertTrue(task2.isTechnicallyEqual(readTask2));
        assertTrue(task3.isTechnicallyEqual(readTask3));
        assertFalse(readTask1.isRunning());
        assertFalse(readTask2.isRunning());
        assertFalse(readTask3.isRunning());
        List<LogEntry> entries = getLogDAO().readAllLogsForNetworkTask(readTask1.getId());
        LogEntry readEntry1 = entries.get(0);
        LogEntry readEntry2 = entries.get(1);
        LogEntry readEntry3 = entries.get(2);
        logEntryEquals(task1Entry1, readEntry1);
        logEntryEquals(task1Entry2, readEntry2);
        logEntryEquals(task1Entry3, readEntry3);
        assertEquals(readTask1.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry3.getNetworkTaskId());
        entries = getLogDAO().readAllLogsForNetworkTask(readTask2.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        logEntryEquals(task2Entry1, readEntry1);
        logEntryEquals(task2Entry2, readEntry2);
        logEntryEquals(task2Entry3, readEntry3);
        assertEquals(readTask2.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry3.getNetworkTaskId());
        entries = getLogDAO().readAllLogsForNetworkTask(readTask3.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        logEntryEquals(task3Entry1, readEntry1);
        logEntryEquals(task3Entry2, readEntry2);
        logEntryEquals(task3Entry3, readEntry3);
        assertEquals(readTask3.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry3.getNetworkTaskId());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(1, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals("folder", getPreferenceManager().getPreferenceDownloadFolder());
        assertTrue(getPreferenceManager().getPreferenceDownloadKeep());
        assertEquals(AccessType.CONNECT, getPreferenceManager().getPreferenceAccessType());
        assertEquals("address", getPreferenceManager().getPreferenceAddress());
        assertEquals(123, getPreferenceManager().getPreferencePort());
        assertEquals(456, getPreferenceManager().getPreferenceInterval());
        assertTrue(getPreferenceManager().getPreferenceOnlyWifi());
        assertTrue(getPreferenceManager().getPreferenceNotification());
        assertEquals("folderImport", getPreferenceManager().getPreferenceImportFolder());
        assertEquals("folderExport", getPreferenceManager().getPreferenceExportFolder());
        assertTrue(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertTrue(getPreferenceManager().getPreferenceFileDumpEnabled());
    }

    @Test
    public void testImportFailureDataPurged() throws Exception {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask2());
        NetworkTask task3 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask3());
        getLogDAO().insertAndDeleteLog(getLogEntry1(task1.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry2(task1.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry3(task1.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry1(task2.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry2(task2.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry3(task2.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry1(task3.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry2(task3.getId()));
        getLogDAO().insertAndDeleteLog(getLogEntry3(task3.getId()));
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
        getPreferenceManager().setPreferenceAccessType(AccessType.CONNECT);
        getPreferenceManager().setPreferenceAddress("address");
        getPreferenceManager().setPreferencePort(123);
        getPreferenceManager().setPreferenceInterval(456);
        getPreferenceManager().setPreferenceOnlyWifi(true);
        getPreferenceManager().setPreferenceNotification(true);
        getPreferenceManager().setPreferenceImportFolder("folderImport");
        getPreferenceManager().setPreferenceExportFolder("folderExport");
        getPreferenceManager().setPreferenceFileLoggerEnabled(true);
        getPreferenceManager().setPreferenceFileDumpEnabled(true);
        File folder = getFileManager().getExternalRootDirectory(0);
        StreamUtil.stringToOutputStream("Failure", new FileOutputStream(new File(folder, "test.json")), Charsets.UTF_8);
        ImportTask task = new ImportTask(getActivity(activityScenario), folder, "test.json");
        task.runInBackground();
        assertTrue(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertTrue(getLogDAO().readAllLogs().isEmpty());
        assertEquals(3, getPreferenceManager().getPreferencePingCount());
        assertEquals(1, getPreferenceManager().getPreferenceConnectCount());
        assertFalse(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertFalse(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals("download", getPreferenceManager().getPreferenceDownloadFolder());
        assertFalse(getPreferenceManager().getPreferenceDownloadKeep());
        assertEquals(AccessType.PING, getPreferenceManager().getPreferenceAccessType());
        assertEquals("192.168.178.1", getPreferenceManager().getPreferenceAddress());
        assertEquals(22, getPreferenceManager().getPreferencePort());
        assertEquals(15, getPreferenceManager().getPreferenceInterval());
        assertFalse(getPreferenceManager().getPreferenceOnlyWifi());
        assertFalse(getPreferenceManager().getPreferenceNotification());
        assertEquals("config", getPreferenceManager().getPreferenceImportFolder());
        assertEquals("config", getPreferenceManager().getPreferenceExportFolder());
        assertFalse(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertFalse(getPreferenceManager().getPreferenceFileDumpEnabled());
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
