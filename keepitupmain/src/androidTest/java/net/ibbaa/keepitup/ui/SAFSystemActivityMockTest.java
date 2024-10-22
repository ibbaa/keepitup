/*
 * Copyright (c) 2024. Alwin Ibba
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

package net.ibbaa.keepitup.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.documentfile.provider.DocumentFile;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.common.base.Charsets;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.JSONSystemSetup;
import net.ibbaa.keepitup.resources.SystemSetupResult;
import net.ibbaa.keepitup.test.mock.DelegatingTestPermissionLauncher;
import net.ibbaa.keepitup.test.mock.MockDocumentManager;
import net.ibbaa.keepitup.test.mock.MockExportTask;
import net.ibbaa.keepitup.test.mock.MockImportTask;
import net.ibbaa.keepitup.test.mock.MockStoragePermissionManager;
import net.ibbaa.keepitup.test.mock.TestExportTask;
import net.ibbaa.keepitup.test.mock.TestImportTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.sync.ExportTask;
import net.ibbaa.keepitup.ui.sync.ImportTask;
import net.ibbaa.keepitup.util.StreamUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

@MediumTest
@SuppressWarnings({"SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class SAFSystemActivityMockTest extends BaseUITest {

    private MockStoragePermissionManager storagePermissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        storagePermissionManager = getMockStoragePermissionManager();
    }

    @Test
    public void testAllowArbitraryFileLocation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertFalse(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testAllowArbitraryFileLocationPermissionPresent() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        storagePermissionManager.setGrantedFolder("/Movies");
        storagePermissionManager.requestPersistentFolderPermission(null, "/Movies");
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertFalse(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        assertTrue(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Movies"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Test"));
        assertFalse(storagePermissionManager.hasPersistentPermission(getActivity(activityScenario), "/Documents"));
        activityScenario.close();
    }

    @Test
    public void testAllowArbitraryFileLocationImportExportFolderText() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        onView(withId(R.id.textview_activity_system_config_import_folder)).check(matches(withText(endsWith("config"))));
        onView(withId(R.id.textview_activity_system_config_export_folder)).check(matches(withText(endsWith("config"))));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        assertTrue(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        onView(withId(R.id.textview_activity_system_config_import_folder)).check(matches(withText("Choose file")));
        onView(withId(R.id.textview_activity_system_config_export_folder)).check(matches(withText("Choose file")));
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("no")));
        assertFalse(getPreferenceManager().getPreferenceAllowArbitraryFileLocation());
        onView(withId(R.id.textview_activity_system_config_import_folder)).check(matches(withText(endsWith("config"))));
        onView(withId(R.id.textview_activity_system_config_export_folder)).check(matches(withText(endsWith("config"))));
    }

    @Test
    public void testAllowArbitraryFileLocationExportPermission() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectExportTask(activityScenario, getMockExportTask(activityScenario, true));
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectExportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedCreateFile("/Test/test.json");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_system_config_export_folder)).perform(click());
        assertTrue(storagePermissionManager.getCreateFilePermissions().contains("/Test/test.json"));
        assertEquals("/Test/test.json", getPreferenceManager().getPreferenceLastArbitraryExportFile());
    }

    @Test
    public void testAllowArbitraryFileLocationImportPermission() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectImportTask(activityScenario, getMockImportTask(activityScenario, true));
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectImportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedOpenFile("/Test/test.json");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("/Test/test.json");
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_system_config_import_folder)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("The import will overwrite all existing network tasks, log entries and the configuration. This cannot be undone.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(storagePermissionManager.getOpenFilePermissions().contains("/Test/test.json"));
        assertTrue(storagePermissionManager.getFolderPermissions().contains("/Test"));
    }

    @Test
    public void testAllowArbitraryFileLocationImportPermissionResetLogFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectImportTask(activityScenario, getMockImportTask(activityScenario, true));
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectImportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedOpenFile("/Test/test.json");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("/Test/test.json");
        getPreferenceManager().setPreferenceLogFile(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        getPreferenceManager().setPreferenceArbitraryLogFolder("/abc");
        onView(withId(R.id.textview_activity_system_config_import_folder)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("The import will overwrite all existing network tasks, log entries and the configuration. This cannot be undone.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(storagePermissionManager.getOpenFilePermissions().contains("/Test/test.json"));
        assertTrue(storagePermissionManager.getFolderPermissions().isEmpty());
    }

    @Test
    public void testAllowArbitraryFileLocationImportPermissionResetDownloadFolder() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectImportTask(activityScenario, getMockImportTask(activityScenario, true));
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectImportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedOpenFile("/Test/test.json");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("/Test/test.json");
        getPreferenceManager().setPreferenceLogFile(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        getPreferenceManager().setPreferenceArbitraryDownloadFolder("/xyz");
        onView(withId(R.id.textview_activity_system_config_import_folder)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("The import will overwrite all existing network tasks, log entries and the configuration. This cannot be undone.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(storagePermissionManager.getOpenFilePermissions().contains("/Test/test.json"));
        assertTrue(storagePermissionManager.getFolderPermissions().isEmpty());
    }

    @Test
    public void testAllowArbitraryFileLocationResetRevokePermission() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectImportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedOpenFile("/Test/test.json");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("/Test/test.json");
        getPreferenceManager().setPreferenceLogFile(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_system_config_reset_label)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This will delete all network tasks and log entries, reset the configuration to default settings and cannot be undone.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(storagePermissionManager.getFolderPermissions().isEmpty());
    }

    @Test
    public void testAllowArbitraryFileLocationExport() throws Exception {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        File folder = getFileManager().getExternalRootDirectory(0);
        File file = new File(folder, "test.json");
        TestExportTask task = new TestExportTask(getActivity(activityScenario), folder, "test.json", true);
        task.setOutputStream(new FileOutputStream(file));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFile(DocumentFile.fromFile(new File("test")));
        task.setDocumentManager(documentManager);
        injectExportTask(activityScenario, task);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectExportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedCreateFile("/Test/test.json");
        NetworkTask networkTask = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry networkTaskEntry = getLogDAO().insertAndDeleteLog(getLogEntry(networkTask.getId()));
        AccessTypeData accessTypeData = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData(networkTask.getId()));
        getIntervalDAO().insertInterval(getInterval());
        getTimeBasedSuspensionScheduler().reset();
        getTimeBasedSuspensionScheduler().getIntervals();
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertFalse(getAccessTypeDataDAO().readAllAccessTypeData().isEmpty());
        assertFalse(getIntervalDAO().readAllIntervals().isEmpty());
        assertFalse(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
        getPreferenceManager().setPreferenceTheme(-1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
        getPreferenceManager().setPreferenceLogFolder("folder");
        getPreferenceManager().setPreferenceAccessType(AccessType.CONNECT);
        getPreferenceManager().setPreferenceAddress("address");
        getPreferenceManager().setPreferencePort(123);
        getPreferenceManager().setPreferenceInterval(456);
        getPreferenceManager().setPreferenceOnlyWifi(true);
        getPreferenceManager().setPreferenceNotification(true);
        getPreferenceManager().setPreferenceImportFolder("folderImport");
        getPreferenceManager().setPreferenceExportFolder("folderExport");
        getPreferenceManager().setPreferenceLastArbitraryExportFile("arbitraryFolderExport");
        getPreferenceManager().setPreferenceFileLoggerEnabled(true);
        getPreferenceManager().setPreferenceFileDumpEnabled(true);
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_system_config_export_folder)).perform(click());
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getAccessTypeDataDAO().deleteAllAccessTypeData();
        getIntervalDAO().deleteAllIntervals();
        FileInputStream inputStream = new FileInputStream(file);
        String jsonData = StreamUtil.inputStreamToString(inputStream, Charsets.UTF_8);
        inputStream.close();
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.importData(jsonData);
        assertTrue(result.success());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertFalse(getAccessTypeDataDAO().readAllAccessTypeData().isEmpty());
        assertFalse(getIntervalDAO().readAllIntervals().isEmpty());
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        NetworkTask readTask = tasks.get(0);
        assertTrue(networkTask.isTechnicallyEqual(readTask));
        assertFalse(readTask.isRunning());
        List<LogEntry> entries = getLogDAO().readAllLogsForNetworkTask(readTask.getId());
        LogEntry readEntry = entries.get(0);
        assertTrue(networkTaskEntry.isTechnicallyEqual(readEntry));
        assertEquals(readTask.getId(), readEntry.getNetworkTaskId());
        AccessTypeData readAccessData = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(readTask.getId());
        assertTrue(accessTypeData.isTechnicallyEqual(readAccessData));
        List<Interval> intervals = getIntervalDAO().readAllIntervals();
        assertEquals(1, intervals.size());
        assertTrue(getInterval().isEqual(intervals.get(0)));
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals(-1, getPreferenceManager().getPreferenceTheme());
        assertEquals("folder", getPreferenceManager().getPreferenceDownloadFolder());
        assertEquals("/Test", getPreferenceManager().getPreferenceArbitraryDownloadFolder());
        assertTrue(getPreferenceManager().getPreferenceDownloadKeep());
        assertEquals("folder", getPreferenceManager().getPreferenceLogFolder());
        assertEquals("/Test", getPreferenceManager().getPreferenceArbitraryLogFolder());
        assertEquals(AccessType.CONNECT, getPreferenceManager().getPreferenceAccessType());
        assertEquals("address", getPreferenceManager().getPreferenceAddress());
        assertEquals(123, getPreferenceManager().getPreferencePort());
        assertEquals(456, getPreferenceManager().getPreferenceInterval());
        assertTrue(getPreferenceManager().getPreferenceOnlyWifi());
        assertTrue(getPreferenceManager().getPreferenceNotification());
        assertEquals("folderImport", getPreferenceManager().getPreferenceImportFolder());
        assertEquals("folderExport", getPreferenceManager().getPreferenceExportFolder());
        assertEquals("/Test/test.json", getPreferenceManager().getPreferenceLastArbitraryExportFile());
        assertTrue(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertTrue(getPreferenceManager().getPreferenceFileDumpEnabled());
    }

    @Test
    public void testAllowArbitraryFileLocationImport() throws Exception {
        NetworkTask networkTask = getNetworkTaskDAO().insertNetworkTask(getNetworkTask());
        LogEntry taskEntry = getLogDAO().insertAndDeleteLog(getLogEntry(networkTask.getId()));
        getIntervalDAO().insertInterval(getInterval());
        AccessTypeData accessData = getAccessTypeDataDAO().insertAccessTypeData(getAccessTypeData(networkTask.getId()));
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceNotificationType(NotificationType.CHANGE);
        getPreferenceManager().setPreferenceSuspensionEnabled(false);
        getPreferenceManager().setPreferenceEnforceDefaultPingPackageSize(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(1);
        getPreferenceManager().setPreferenceDownloadFolder("folder");
        getPreferenceManager().setPreferenceArbitraryDownloadFolder("folder");
        getPreferenceManager().setPreferenceDownloadKeep(true);
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
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.exportData();
        assertTrue(result.success());
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getPreferenceManager().removeAllPreferences();
        File folder = getFileManager().getExternalRootDirectory(0);
        File file = new File(folder, "test.json");
        FileOutputStream stream = new FileOutputStream(file);
        StreamUtil.stringToOutputStream(result.data(), stream, Charsets.UTF_8);
        stream.close();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(SystemActivity.class, getBypassSystemSAFBundle());
        injectMocks(activityScenario);
        injectArbitraryFolderLauncher(activityScenario, "/Test");
        storagePermissionManager.setGrantedFolder("/Test");
        injectImportFileLauncher(activityScenario, "/Test/test.json");
        storagePermissionManager.setGrantedOpenFile("/Test/test.json");
        TestImportTask task = new TestImportTask(getActivity(activityScenario), folder, "test.json", true);
        task.setInputStream(new FileInputStream(file));
        MockDocumentManager documentManager = new MockDocumentManager();
        documentManager.setFile(DocumentFile.fromFile(new File("test")));
        task.setDocumentManager(documentManager);
        injectImportTask(activityScenario, task);
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_allow_arbitrary_file_location)).perform(click());
        onView(withId(R.id.textview_activity_system_allow_arbitrary_file_location_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_system_config_import_folder)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertFalse(getIntervalDAO().readAllIntervals().isEmpty());
        List<NetworkTask> tasks = getNetworkTaskDAO().readAllNetworkTasks();
        NetworkTask readTask = tasks.get(0);
        assertTrue(networkTask.isTechnicallyEqual(readTask));
        assertFalse(readTask.isRunning());
        List<LogEntry> entries = getLogDAO().readAllLogsForNetworkTask(readTask.getId());
        LogEntry readEntry = entries.get(0);
        assertTrue(taskEntry.isTechnicallyEqual(readEntry));
        assertTrue(getInterval().isEqual(getIntervalDAO().readAllIntervals().get(0)));
        AccessTypeData readAccessData = getAccessTypeDataDAO().readAccessTypeDataForNetworkTask(readTask.getId());
        assertTrue(accessData.isTechnicallyEqual(readAccessData));
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, getPreferenceManager().getPreferenceNotificationType());
        assertFalse(getPreferenceManager().getPreferenceSuspensionEnabled());
        assertTrue(getPreferenceManager().getPreferenceEnforceDefaultPingPackageSize());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(1, getPreferenceManager().getPreferenceExternalStorageType());
        assertEquals("folder", getPreferenceManager().getPreferenceDownloadFolder());
        assertEquals("folder", getPreferenceManager().getPreferenceArbitraryDownloadFolder());
        assertTrue(getPreferenceManager().getPreferenceDownloadKeep());
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
    }

    private NetworkTask getNetworkTask() {
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

    private LogEntry getLogEntry(long networkTaskId) {
        LogEntry insertedLogEntry1 = new LogEntry();
        insertedLogEntry1.setId(0);
        insertedLogEntry1.setNetworkTaskId(networkTaskId);
        insertedLogEntry1.setSuccess(true);
        insertedLogEntry1.setTimestamp(789);
        insertedLogEntry1.setMessage("TestMessage1");
        return insertedLogEntry1;
    }

    private AccessTypeData getAccessTypeData(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(networkTaskId);
        data.setPingCount(10);
        data.setPingPackageSize(1234);
        data.setConnectCount(3);
        data.setStopOnSuccess(true);
        return data;
    }

    private Interval getInterval() {
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

    private void injectMocks(ActivityScenario<?> activityScenario) {
        ((SystemActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        ((SystemActivity) getActivity(activityScenario)).injectStoragePermissionManager(storagePermissionManager);
        ((SystemActivity) getActivity(activityScenario)).injectArbitraryFolderLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantArbitraryFolderPermissions));
        ((SystemActivity) getActivity(activityScenario)).injectExportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationExportFilePermission));
        ((SystemActivity) getActivity(activityScenario)).injectImportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationImportFilePermission));
    }

    private void injectArbitraryFolderLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectArbitraryFolderLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantArbitraryFolderPermissions, uri));
    }

    private void injectExportFileLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectExportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationExportFilePermission, uri));
    }

    private void injectImportFileLauncher(ActivityScenario<?> activityScenario, String uri) {
        ((SystemActivity) getActivity(activityScenario)).injectImportFileLauncher(new DelegatingTestPermissionLauncher(((SystemActivity) getActivity(activityScenario))::grantConfigurationImportFilePermission, uri));
    }

    private MockStoragePermissionManager getMockStoragePermissionManager() {
        return new MockStoragePermissionManager();
    }

    private MockExportTask getMockExportTask(ActivityScenario<?> activityScenario, boolean success) {
        return new MockExportTask(getActivity(activityScenario), success);
    }

    private MockImportTask getMockImportTask(ActivityScenario<?> activityScenario, boolean success) {
        return new MockImportTask(getActivity(activityScenario), new SystemSetupResult(success, false, "", ""));
    }

    private void injectImportTask(ActivityScenario<?> activityScenario, ImportTask importTask) {
        SystemActivity activity = (SystemActivity) getActivity(activityScenario);
        activity.injectImportTask(importTask);
    }

    private void injectExportTask(ActivityScenario<?> activityScenario, ExportTask exportTask) {
        SystemActivity activity = (SystemActivity) getActivity(activityScenario);
        activity.injectExportTask(exportTask);
    }
}
