package de.ibba.keepitup.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.test.mock.MockClipboardManager;
import de.ibba.keepitup.test.mock.MockFileManager;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GlobalSettingsActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<GlobalSettingsActivity> rule = new ActivityTestRule<>(GlobalSettingsActivity.class, false, false);

    @Test
    public void testDisplayDefaultValues() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.textview_activity_global_settings_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_global_settings_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_external_storage_type_label)).check(matches(withText("External storage type")));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isEnabled())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testDisplayValues() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_global_settings_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isEnabled()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testSwitchYesNoText() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_logger_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_logger_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_dump_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_dump_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_dump_enabled_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(2, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testSetPingCountPreferencesCancel() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }

    @Test
    public void testPingCountInput() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
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
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 10"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("5")));
    }

    @Test
    public void testPingCountCopyPasteOption() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) activity.getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("5");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("6")));
        assertTrue(clipboardManager.hasData());
        assertEquals("6", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("6")));
    }

    @Test
    public void testPingCountCopyPasteOptionScreenRotation() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) activity.getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("5");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        assertEquals(2, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("5");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("6")));
        assertTrue(clipboardManager.hasData());
        assertEquals("6", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("6")));
    }

    @Test
    public void testSetConnectCountPreferencesCancel() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("3"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }

    @Test
    public void testConnectCountInput() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 10"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("5")));
    }

    @Test
    public void testConnectCountCopyPasteOption() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) activity.getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("10");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("10")));
        assertTrue(clipboardManager.hasData());
        assertEquals("10", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
    }

    @Test
    public void testConnectCountCopyPasteOptionScreenRotation() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("10");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("10");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("10")));
        assertTrue(clipboardManager.hasData());
        assertEquals("10", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
    }

    @Test
    public void testDownloadControls() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_external_storage_type_label)).check(matches(withText("External storage type")));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isEnabled())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_external_storage_type_label)).check(matches(withText("External storage type")));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isEnabled()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isChecked())));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isEnabled())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isEnabled()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testExternalStorageType() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isEnabled()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isEnabled()));
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        String downloadPrimary = getText(withId(R.id.textview_activity_global_settings_download_folder));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        String downloadPrimaryDialog = getText(withId(R.id.textview_dialog_folder_choose_absolute));
        assertEquals(downloadPrimary, downloadPrimaryDialog);
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).perform(click());
        assertEquals(1, preferenceManager.getPreferenceExternalStorageType());
        String downloadSDCard = getText(withId(R.id.textview_activity_global_settings_download_folder));
        assertNotEquals(downloadPrimary, downloadSDCard);
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        String downloadSDCardDialog = getText(withId(R.id.textview_dialog_folder_choose_absolute));
        assertEquals(downloadSDCard, downloadSDCardDialog);
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).perform(click());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        String downloadPrimary2 = getText(withId(R.id.textview_activity_global_settings_download_folder));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        String downloadPrimaryDialog2 = getText(withId(R.id.textview_dialog_folder_choose_absolute));
        assertEquals(downloadPrimary2, downloadPrimaryDialog2);
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDownloadFolderDialogOpen() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testDownloadFolderDialogOkCancel() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        File testFile = new File(getFileManager().getExternalRootDirectory(0), "test");
        assertFalse(testFile.exists());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_folder_choose_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(testFile.exists());
    }

    @Test
    public void testDownloadControlsFileError() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activity.injectFileManager(mockFileManager);
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(Matchers.not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
    }

    @Test
    public void testDownloadFolderDialogFileError() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activity.injectFileManager(mockFileManager);
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
    }

    @Test
    public void testDownloadFolderDialogOkFileError() {
        GlobalSettingsActivity activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activity.injectFileManager(mockFileManager);
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_folder_choose_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error creating download directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
    }

    @Test
    public void testFileLoggerInitialized() {
        launchSettingsInputActivity(rule);
        assertNull(Log.getLogger());
        assertNull(Dump.getDump());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        assertNotNull(Log.getLogger());
        assertNotNull(Dump.getDump());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        assertNull(Log.getLogger());
        assertNull(Dump.getDump());
    }

    @Test
    public void testResetValues() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("4"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_global_settings_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.radiogroup_activity_global_settings_external_storage_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(withText("Primary")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(withText("SD card")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isEnabled())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isNotChecked()));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isEnabled()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isEnabled()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_dump_enabled_on_off)).check(matches(withText("yes")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_primary)).check(matches(isEnabled()));
        onView(withId(R.id.radiobutton_activity_global_settings_external_storage_type_sdcard)).check(matches(isEnabled()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_file_dump_enabled_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testConfirmDialogScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
    }

    @Test
    public void testValidationErrorScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
    }

    @Test
    public void testValidationErrorColorScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("a")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("a")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
    }

    @Test
    public void testDownloadFolderDialogScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("download")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        File testFile = new File(getFileManager().getExternalRootDirectory(0), "test");
        assertFalse(testFile.exists());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.scrollview_dialog_folder_choose)).perform(swipeUp());
        onView(withId(R.id.imageview_dialog_folder_choose_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(testFile.exists());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
    }

    private SettingsInputDialog getDialog() {
        return (SettingsInputDialog) getActivity().getSupportFragmentManager().getFragments().get(0);
    }

    private GlobalSettingsActivity getActivity() {
        return (GlobalSettingsActivity) rule.getActivity();
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }
}
