package de.ibba.keepitup.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.test.mock.MockFileManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GlobalSettingsActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<GlobalSettingsActivity> rule = new ActivityTestRule<>(GlobalSettingsActivity.class, false, false);

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getFileManager().deleteDirectory(getFileManager().getExternalDirectory("test"));
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        getFileManager().deleteDirectory(getFileManager().getExternalDirectory("test"));
    }

    @Test
    public void testDisplayDefaultValues() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.textview_global_settings_activity_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(not(isEnabled())));
    }

    @Test
    public void testDisplayValues() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isEnabled()));
    }

    @Test
    public void testSwitchYesNoText() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(2, preferenceManager.getPreferencePingCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testSetPingCountPreferencesCancel() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }

    @Test
    public void testPingCountInput() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 10"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("5")));
    }

    @Test
    public void testDownloadControls() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isEnabled()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(not(isChecked())));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(not(isEnabled())));
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testDownloadFolderDialogOpen() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_folder_choose_edit_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testDownloadFolderDialogOkCancel() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_folder_choose_edit_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_folder_choose_edit_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_folder_choose_edit_cancel)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_folder_choose_edit_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_folder_choose_edit_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_folder_choose_edit_ok)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceDownloadFolder());
    }

    @Test
    public void testDownloadControlsFileError() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        MockFileManager mockFileManager = new MockFileManager();
        activity.injectFileManager(mockFileManager);
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(Matchers.not(isEnabled())));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
    }

    @Test
    public void testDownloadFolderDialogFileError() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        activity.injectFileManager(mockFileManager);
        onView(withId(R.id.textview_global_settings_activity_download_folder)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("download"))));
    }

    @Test
    public void testDownloadFolderDialogOkFileError() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        activity.injectFileManager(mockFileManager);
        onView(withId(R.id.edittext_dialog_folder_choose_edit_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_folder_choose_edit_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error creating download directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
    }

    @Test
    public void testResetValues() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_global_settings_activity_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_global_settings_activity_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(not(isEnabled())));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_download_keep)).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_on_off)).check(matches(withText("yes")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_download_keep_on_off)).check(matches(withText("yes")));
    }
}
