package de.ibba.keepitup.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.service.NetworkTaskProcessServiceScheduler;
import de.ibba.keepitup.test.mock.MockAlarmManager;
import de.ibba.keepitup.test.mock.MockDBPurgeTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.sync.DBPurgeTask;

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

    private MockDBPurgeTask getMockDBPurgeTask(boolean success) {
        return new MockDBPurgeTask(getActivity(activityScenario), success);
    }

    private void injectPurgeTask(DBPurgeTask purgeTask) {
        SystemActivity activity = (SystemActivity) getActivity(activityScenario);
        activity.injectPurgeTask(purgeTask);
    }
}
