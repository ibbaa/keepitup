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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.documentfile.provider.DocumentFile;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.TestImportTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.util.StreamUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
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
        getIntervalDAO().insertInterval(getInterval());
        AccessTypeData accessData1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData accessData2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceNotificationType(NotificationType.CHANGE);
        getPreferenceManager().setPreferenceSuspensionEnabled(false);
        getPreferenceManager().setPreferenceEnforceDefaultPingPackageSize(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceArbitraryDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
        getPreferenceManager().setPreferenceDownloadFollowsRedirects(false);
        getPreferenceManager().setPreferenceArbitraryLogFolder("folder");
        getPreferenceManager().setPreferenceAccessType(AccessType.CONNECT);
        getPreferenceManager().setPreferenceAddress("address");
        getPreferenceManager().setPreferencePort(123);
        getPreferenceManager().setPreferenceInterval(456);
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferencePingPackageSize(12);
        getPreferenceManager().setPreferenceStopOnSuccess(true);
        getPreferenceManager().setPreferenceOnlyWifi(true);
        getPreferenceManager().setPreferenceNotification(true);
        getPreferenceManager().setPreferenceImportFolder("folderImport");
        getPreferenceManager().setPreferenceExportFolder("folderExport");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("fileExport");
        getPreferenceManager().setPreferenceFileLoggerEnabled(true);
        getPreferenceManager().setPreferenceFileDumpEnabled(true);
        getPreferenceManager().setPreferenceAllowArbitraryFileLocation(true);
        getPreferenceManager().setPreferenceAskedNotificationPermission(true);
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getPreferenceManager().removeAllPreferences();
        File folder = getFileManager().getExternalRootDirectory(0);
        FileOutputStream stream = new FileOutputStream(new File(folder, "test.json"));
        StreamUtil.stringToOutputStream(result.data(), stream, StandardCharsets.UTF_8);
        stream.close();
        ImportTask task = new ImportTask(getActivity(activityScenario), folder, "test.json", false);
        task.runInBackground();
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertFalse(getIntervalDAO().readAllIntervals().isEmpty());
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
        assertTrue(task1Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task1Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task1Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask1.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry3.getNetworkTaskId());
        entries = getLogDAO().readAllLogsForNetworkTask(readTask2.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        assertTrue(task2Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task2Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task2Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask2.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry3.getNetworkTaskId());
        entries = getLogDAO().readAllLogsForNetworkTask(readTask3.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        assertTrue(task3Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task3Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task3Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask3.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry3.getNetworkTaskId());
        assertTrue(getInterval().isEqual(getIntervalDAO().readAllIntervals().get(0)));
        AccessTypeData readAccessData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(readTask1.getId());
        AccessTypeData readAccessData2 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(readTask2.getId());
        assertTrue(accessData1.isTechnicallyEqual(readAccessData1));
        assertTrue(accessData2.isTechnicallyEqual(readAccessData2));
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, getPreferenceManager().getPreferenceNotificationType());
        assertFalse(getPreferenceManager().getPreferenceSuspensionEnabled());
        assertTrue(getPreferenceManager().getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(1, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals("folder", getPreferenceManager().getPreferenceDownloadFolder());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryDownloadFolder());
        assertTrue(getPreferenceManager().getPreferenceDownloadKeep());
        assertFalse(getPreferenceManager().getPreferenceDownloadFollowsRedirects());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryLogFolder());
        assertEquals(AccessType.CONNECT, getPreferenceManager().getPreferenceAccessType());
        assertEquals("address", getPreferenceManager().getPreferenceAddress());
        assertEquals(123, getPreferenceManager().getPreferencePort());
        assertEquals(456, getPreferenceManager().getPreferenceInterval());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertEquals(12, getPreferenceManager().getPreferencePingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceStopOnSuccess());
        assertTrue(getPreferenceManager().getPreferenceOnlyWifi());
        assertTrue(getPreferenceManager().getPreferenceNotification());
        assertEquals("folderImport", getPreferenceManager().getPreferenceImportFolder());
        assertEquals("folderExport", getPreferenceManager().getPreferenceExportFolder());
        assertEquals("fileExport", getPreferenceManager().getPreferenceLastArbitraryExportFile());
        assertTrue(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertTrue(getPreferenceManager().getPreferenceFileDumpEnabled());
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(getPreferenceManager().getPreferenceAskedNotificationPermission());
    }

    @Test
    public void testImportDocumentApi() throws Exception {
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
        getIntervalDAO().insertInterval(getInterval());
        AccessTypeData accessData1 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        AccessTypeData accessData2 = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData2(task2.getId()));
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceNotificationType(NotificationType.CHANGE);
        getPreferenceManager().setPreferenceSuspensionEnabled(false);
        getPreferenceManager().setPreferenceEnforceDefaultPingPackageSize(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceArbitraryDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
        getPreferenceManager().setPreferenceDownloadFollowsRedirects(false);
        getPreferenceManager().setPreferenceArbitraryLogFolder("folder");
        getPreferenceManager().setPreferenceAccessType(AccessType.CONNECT);
        getPreferenceManager().setPreferenceAddress("address");
        getPreferenceManager().setPreferencePort(123);
        getPreferenceManager().setPreferenceInterval(456);
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferencePingPackageSize(12);
        getPreferenceManager().setPreferenceStopOnSuccess(true);
        getPreferenceManager().setPreferenceOnlyWifi(true);
        getPreferenceManager().setPreferenceNotification(true);
        getPreferenceManager().setPreferenceImportFolder("folderImport");
        getPreferenceManager().setPreferenceExportFolder("folderExport");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("fileExport");
        getPreferenceManager().setPreferenceFileLoggerEnabled(true);
        getPreferenceManager().setPreferenceFileDumpEnabled(true);
        getPreferenceManager().setPreferenceAllowArbitraryFileLocation(true);
        getPreferenceManager().setPreferenceAskedNotificationPermission(true);
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getPreferenceManager().removeAllPreferences();
        File folder = getFileManager().getExternalRootDirectory(0);
        File file = new File(folder, "test.json");
        FileOutputStream stream = new FileOutputStream(file);
        StreamUtil.stringToOutputStream(result.data(), stream, StandardCharsets.UTF_8);
        stream.close();
        TestImportTask task = new TestImportTask(getActivity(activityScenario), folder, "test.json", true);
        task.setInputStream(new FileInputStream(file));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFile(DocumentFile.fromFile(new File("test")));
        task.setDocumentManager(documentManager);
        task.runInBackground();
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertFalse(getIntervalDAO().readAllIntervals().isEmpty());
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
        assertTrue(task1Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task1Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task1Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask1.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask1.getId(), readEntry3.getNetworkTaskId());
        entries = getLogDAO().readAllLogsForNetworkTask(readTask2.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        assertTrue(task2Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task2Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task2Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask2.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask2.getId(), readEntry3.getNetworkTaskId());
        entries = getLogDAO().readAllLogsForNetworkTask(readTask3.getId());
        readEntry1 = entries.get(0);
        readEntry2 = entries.get(1);
        readEntry3 = entries.get(2);
        assertTrue(task3Entry1.isTechnicallyEqual(readEntry1));
        assertTrue(task3Entry2.isTechnicallyEqual(readEntry2));
        assertTrue(task3Entry3.isTechnicallyEqual(readEntry3));
        assertEquals(readTask3.getId(), readEntry1.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry2.getNetworkTaskId());
        assertEquals(readTask3.getId(), readEntry3.getNetworkTaskId());
        assertTrue(getInterval().isEqual(getIntervalDAO().readAllIntervals().get(0)));
        AccessTypeData readAccessData1 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(readTask1.getId());
        AccessTypeData readAccessData2 = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(readTask2.getId());
        assertTrue(accessData1.isTechnicallyEqual(readAccessData1));
        assertTrue(accessData2.isTechnicallyEqual(readAccessData2));
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, getPreferenceManager().getPreferenceNotificationType());
        assertFalse(getPreferenceManager().getPreferenceSuspensionEnabled());
        assertTrue(getPreferenceManager().getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(1, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals("folder", getPreferenceManager().getPreferenceDownloadFolder());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryDownloadFolder());
        assertTrue(getPreferenceManager().getPreferenceDownloadKeep());
        assertFalse(getPreferenceManager().getPreferenceDownloadFollowsRedirects());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryLogFolder());
        assertEquals(AccessType.CONNECT, getPreferenceManager().getPreferenceAccessType());
        assertEquals("address", getPreferenceManager().getPreferenceAddress());
        assertEquals(123, getPreferenceManager().getPreferencePort());
        assertEquals(456, getPreferenceManager().getPreferenceInterval());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertEquals(12, getPreferenceManager().getPreferencePingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceStopOnSuccess());
        assertTrue(getPreferenceManager().getPreferenceOnlyWifi());
        assertTrue(getPreferenceManager().getPreferenceNotification());
        assertEquals("folderImport", getPreferenceManager().getPreferenceImportFolder());
        assertEquals("folderExport", getPreferenceManager().getPreferenceExportFolder());
        assertEquals("fileExport", getPreferenceManager().getPreferenceLastArbitraryExportFile());
        assertTrue(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertTrue(getPreferenceManager().getPreferenceFileDumpEnabled());
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(getPreferenceManager().getPreferenceAskedNotificationPermission());
    }

    @Test
    public void testImportFailureDataNotPurged() throws Exception {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(getNetworkTask1());
        getLogDAO().insertAndDeleteLog(getLogEntry1(task1.getId()));
        getIntervalDAO().insertInterval(getInterval());
        getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData1(task1.getId()));
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceNotificationType(NotificationType.CHANGE);
        getPreferenceManager().setPreferenceSuspensionEnabled(false);
        getPreferenceManager().setPreferenceEnforceDefaultPingPackageSize(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceArbitraryDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
        getPreferenceManager().setPreferenceDownloadFollowsRedirects(false);
        getPreferenceManager().setPreferenceArbitraryLogFolder("folder");
        getPreferenceManager().setPreferenceAccessType(AccessType.CONNECT);
        getPreferenceManager().setPreferenceAddress("address");
        getPreferenceManager().setPreferencePort(123);
        getPreferenceManager().setPreferenceInterval(456);
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferencePingPackageSize(12);
        getPreferenceManager().setPreferenceStopOnSuccess(true);
        getPreferenceManager().setPreferenceOnlyWifi(true);
        getPreferenceManager().setPreferenceNotification(true);
        getPreferenceManager().setPreferenceImportFolder("folderImport");
        getPreferenceManager().setPreferenceExportFolder("folderExport");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("fileExport");
        getPreferenceManager().setPreferenceFileLoggerEnabled(true);
        getPreferenceManager().setPreferenceFileDumpEnabled(true);
        getPreferenceManager().setPreferenceAllowArbitraryFileLocation(true);
        getPreferenceManager().setPreferenceAskedNotificationPermission(true);
        File folder = getFileManager().getExternalRootDirectory(0);
        FileOutputStream stream = new FileOutputStream(new File(folder, "test.json"));
        StreamUtil.stringToOutputStream("Failure", stream, StandardCharsets.UTF_8);
        stream.close();
        ImportTask task = new ImportTask(getActivity(activityScenario), folder, "test.json", false);
        task.runInBackground();
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertFalse(getIntervalDAO().readAllIntervals().isEmpty());
        assertNotNull(getSchedulerStateDAO().readSchedulerState());
        assertFalse(getAccessTypeDataDAO().readAllAccessTypeData().isEmpty());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, getPreferenceManager().getPreferenceNotificationType());
        assertFalse(getPreferenceManager().getPreferenceSuspensionEnabled());
        assertTrue(getPreferenceManager().getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(1, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals("folder", getPreferenceManager().getPreferenceDownloadFolder());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryDownloadFolder());
        assertTrue(getPreferenceManager().getPreferenceDownloadKeep());
        assertFalse(getPreferenceManager().getPreferenceDownloadFollowsRedirects());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryLogFolder());
        assertEquals(AccessType.CONNECT, getPreferenceManager().getPreferenceAccessType());
        assertEquals("address", getPreferenceManager().getPreferenceAddress());
        assertEquals(123, getPreferenceManager().getPreferencePort());
        assertEquals(456, getPreferenceManager().getPreferenceInterval());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertEquals(12, getPreferenceManager().getPreferencePingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceStopOnSuccess());
        assertTrue(getPreferenceManager().getPreferenceOnlyWifi());
        assertTrue(getPreferenceManager().getPreferenceNotification());
        assertEquals("folderImport", getPreferenceManager().getPreferenceImportFolder());
        assertEquals("folderExport", getPreferenceManager().getPreferenceExportFolder());
        assertEquals("fileExport", getPreferenceManager().getPreferenceLastArbitraryExportFile());
        assertTrue(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertTrue(getPreferenceManager().getPreferenceFileDumpEnabled());
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(getPreferenceManager().getPreferenceAskedNotificationPermission());
    }

    private Interval getInterval() {
        Interval interval = new Interval();
        interval.setId(0);
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

    private AccessTypeData getAccessTypeData1(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        return data;
    }

    private AccessTypeData getAccessTypeData2(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(1);
        data.setPingPackageSize(55);
        data.setConnectCount(5);
        data.setStopOnSuccess(false);
        return data;
    }
}
