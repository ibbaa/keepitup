package de.ibba.keepitup.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.google.common.base.Charsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.JSONSystemSetup;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.resources.SystemSetupResult;
import de.ibba.keepitup.service.NetworkTaskProcessServiceScheduler;
import de.ibba.keepitup.test.mock.MockAlarmManager;
import de.ibba.keepitup.test.mock.MockDBPurgeTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.sync.DBPurgeTask;
import de.ibba.keepitup.util.StreamUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SystemActivityTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockAlarmManager alarmManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(SystemActivity.class);
        ((SystemActivity) getActivity(activityScenario)).injectScheduler(getScheduler());
        alarmManager = (MockAlarmManager) getScheduler().getAlarmManager();
        alarmManager.reset();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testResetConfigurationCancel() {
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(30);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_reset)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(30, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testResetConfigurationCancelScreenRotation() {
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(30);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_reset)).perform(click());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        ((SystemActivity) getActivity(activityScenario)).injectScheduler(getScheduler());
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(30, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testResetConfiguration() {
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(30);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_reset)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(alarmManager.wasCancelAlarmCalled());
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
        assertFalse(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertFalse(getPreferenceManager().getPreferenceFileDumpEnabled());
    }

    @Test
    public void testResetConfigurationScreenRotation() {
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(30);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_reset)).perform(click());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        ((SystemActivity) getActivity(activityScenario)).injectScheduler(getScheduler());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(alarmManager.wasCancelAlarmCalled());
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
        assertFalse(getPreferenceManager().getPreferenceFileLoggerEnabled());
        assertFalse(getPreferenceManager().getPreferenceFileDumpEnabled());
    }

    @Test
    public void testResetConfigurationError() {
        injectPurgeTask(getMockDBPurgeTask(false));
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(30);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_reset)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(alarmManager.wasCancelAlarmCalled());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(30, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testResetConfigurationErrorScreenRotation() {
        injectPurgeTask(getMockDBPurgeTask(false));
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(30);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_reset)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertTrue(alarmManager.wasCancelAlarmCalled());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(30, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testExportConfigurationCancel() {
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_export)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        File folder = getFileManager().getExternalDirectory("folderExport", 0);
        assertFalse(getFileManager().doesFileExist(folder, "keepitup_config.json"));
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testExportConfigurationCancelScreenRotation() {
        insertAndScheduleNetworkTask();
        getLogDAO().insertAndDeleteLog(new LogEntry());
        getLogDAO().insertAndDeleteLog(new LogEntry());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_export)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        File folder = getFileManager().getExternalDirectory("folderExport", 0);
        assertFalse(getFileManager().doesFileExist(folder, "keepitup_config.json"));
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testExportConfiguration() throws Exception {
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
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_export)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        File folder = getFileManager().getExternalDirectory("folderExport", 0);
        assertTrue(getFileManager().doesFileExist(folder, "keepitup_config.json"));
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getPreferenceManager().removeAllPreferences();
        String jsonData = StreamUtil.inputStreamToString(new FileInputStream(new File(folder, "keepitup_config.json")), Charsets.UTF_8);
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.importData(jsonData);
        assertTrue(result.isSuccess());
        assertTrue(getFileManager().doesFileExist(folder, "keepitup_config.json"));
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
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testExportConfigurationScreenRotation() throws Exception {
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
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_export)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        File folder = getFileManager().getExternalDirectory("folderExport", 0);
        assertTrue(getFileManager().doesFileExist(folder, "keepitup_config.json"));
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
        getNetworkTaskDAO().deleteAllNetworkTasks();
        getLogDAO().deleteAllLogs();
        getPreferenceManager().removeAllPreferences();
        String jsonData = StreamUtil.inputStreamToString(new FileInputStream(new File(folder, "keepitup_config.json")), Charsets.UTF_8);
        JSONSystemSetup setup = new JSONSystemSetup(TestRegistry.getContext());
        SystemSetupResult result = setup.importData(jsonData);
        assertTrue(result.isSuccess());
        assertTrue(getFileManager().doesFileExist(folder, "keepitup_config.json"));
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
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testExportConfigurationFileExistsCancel() throws Exception {
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
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_export)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        File folder = getFileManager().getExternalDirectory("folderExport", 0);
        assertTrue(new File(folder, "keepitup_config.json").createNewFile());
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("The specified file exists and will be overwritten.")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        String fileContent = StreamUtil.inputStreamToString(new FileInputStream(new File(folder, "keepitup_config.json")), Charsets.UTF_8);
        assertTrue(fileContent.isEmpty());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testExportConfigurationFileExistsCancelScreenRotation() throws Exception {
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
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        getPreferenceManager().setPreferencePingCount(5);
        getPreferenceManager().setPreferenceConnectCount(10);
        getPreferenceManager().setPreferenceNotificationInactiveNetwork(true);
        getPreferenceManager().setPreferenceDownloadExternalStorage(true);
        getPreferenceManager().setPreferenceExternalStorageType(0);
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
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_system_config_export)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        File folder = getFileManager().getExternalDirectory("folderExport", 0);
        assertTrue(new File(folder, "keepitup_config.json").createNewFile());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folderExport")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("keepitup_config.json")));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("The specified file exists and will be overwritten.")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        String fileContent = StreamUtil.inputStreamToString(new FileInputStream(new File(folder, "keepitup_config.json")), Charsets.UTF_8);
        assertTrue(fileContent.isEmpty());
        assertFalse(alarmManager.wasCancelAlarmCalled());
        assertFalse(getNetworkTaskDAO().readAllNetworkTasks().isEmpty());
        assertFalse(getSchedulerIdHistoryDAO().readAllSchedulerIds().isEmpty());
        assertFalse(getLogDAO().readAllLogs().isEmpty());
        assertEquals(5, getPreferenceManager().getPreferencePingCount());
        assertEquals(10, getPreferenceManager().getPreferenceConnectCount());
        assertTrue(getPreferenceManager().getPreferenceNotificationInactiveNetwork());
        assertTrue(getPreferenceManager().getPreferenceDownloadExternalStorage());
        assertEquals(0, getPreferenceManager().getPreferenceExternalStorageType());
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
    public void testDisplayDefaultValues() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testDisplayValues() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testSwitchYesNoText() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testFileLoggerInitialized() {
        assertNull(Log.getLogger());
        assertNull(Dump.getDump());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        assertNotNull(Log.getLogger());
        assertNotNull(Dump.getDump());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        assertNull(Log.getLogger());
        assertNull(Dump.getDump());
    }

    @Test
    public void testResetValues() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("yes")));
    }

    private void insertAndScheduleNetworkTask() {
        NetworkTask task1 = getNetworkTaskDAO().insertNetworkTask(new NetworkTask());
        NetworkTask task2 = getNetworkTaskDAO().insertNetworkTask(new NetworkTask());
        getNetworkTaskDAO().updateNetworkTaskRunning(task1.getId(), true);
        getNetworkTaskDAO().updateNetworkTaskRunning(task2.getId(), true);
        getScheduler().reschedule(task1, NetworkTaskProcessServiceScheduler.Delay.INTERVAL);
        getScheduler().reschedule(task2, NetworkTaskProcessServiceScheduler.Delay.INTERVAL);
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

    private MockDBPurgeTask getMockDBPurgeTask(boolean success) {
        return new MockDBPurgeTask(getActivity(activityScenario), success);
    }

    private void injectPurgeTask(DBPurgeTask purgeTask) {
        SystemActivity activity = (SystemActivity) getActivity(activityScenario);
        activity.injectPurgeTask(purgeTask);
    }
}
