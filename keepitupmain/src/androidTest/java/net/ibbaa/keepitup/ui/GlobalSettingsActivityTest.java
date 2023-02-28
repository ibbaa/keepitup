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

package net.ibbaa.keepitup.ui;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GlobalSettingsActivityTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testDisplayDefaultValues() {
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
        onView(withId(R.id.radiogroup_activity_global_settings_notification_type)).check(matches(hasChildCount(2)));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(withText("Failure")));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(withText("Change")));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(not(isChecked())));;
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText("None")));
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testDisplayValues() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_global_settings_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(isEnabled()));
    }

    @Test
    public void testSwitchYesNoText() {
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
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
    }

    @Test
    public void testSetPreferencesOk() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(2, preferenceManager.getPreferencePingCount());
        assertEquals(5, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceLogFile());
    }

    @Test
    public void testSetPreferencesNotifiactionType() {
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(not(isChecked())));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).perform(click());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(not(isChecked())));
    }

    @Test
    public void testSetPingCountPreferencesCancel() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }

    @Test
    public void testPingCountInput() {
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
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("5");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("6")));
        assertTrue(clipboardManager.hasData());
        assertEquals("6", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("6")));
    }

    @Test
    public void testPingCountCopyPasteOptionScreenRotation() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("5");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("5");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("6")));
        assertTrue(clipboardManager.hasData());
        assertEquals("6", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("6")));
    }

    @Test
    public void testSetConnectCountPreferencesCancel() {
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("3"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }

    @Test
    public void testConnectCountInput() {
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
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("10");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("10")));
        assertTrue(clipboardManager.hasData());
        assertEquals("10", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
    }

    @Test
    public void testConnectCountCopyPasteOptionScreenRotation() {
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("10");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("10");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("10")));
        assertTrue(clipboardManager.hasData());
        assertEquals("10", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
    }

    @Test
    public void testDownloadControls() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals(0, preferenceManager.getPreferenceExternalStorageType());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isChecked())));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
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
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(isEnabled()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
    }

    @Test
    public void testLogControls() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText("None")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(isEnabled()));
        assertTrue(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText("None")));
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).check(matches(not(isEnabled())));
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
    }

    @Test
    public void testDownloadFolderDialogOpen() {
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testLogFolderDialogOpen() {
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testDownloadFolderDialogOkCancel() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        File testFile = new File(getFileManager().getExternalRootDirectory(0), "test");
        assertFalse(testFile.exists());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(testFile.exists());
    }

    @Test
    public void testLogFolderDialogOkCancel() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("log")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        File testFile = new File(getFileManager().getExternalRootDirectory(0), "test");
        assertFalse(testFile.exists());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("log")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceLogFolder());
        assertTrue(testFile.exists());
    }

    @Test
    public void testDownloadFolderDialogErrorFileExists() throws IOException {
        PreferenceManager preferenceManager = getPreferenceManager();
        File root = getFileManager().getExternalRootDirectory(0);
        File test = new File(root, "test");
        assertTrue(test.createNewFile());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("download")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error creating download directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
    }

    @Test
    public void testLogFolderDialogErrorFileExists() throws IOException {
        PreferenceManager preferenceManager = getPreferenceManager();
        File root = getFileManager().getExternalRootDirectory(0);
        File test = new File(root, "test");
        assertTrue(test.createNewFile());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("log")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error creating log directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
    }

    @Test
    public void testDownloadControlsFileError() {
        PreferenceManager preferenceManager = getPreferenceManager();
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(mockFileManager));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).check(matches(Matchers.not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
    }

    @Test
    public void testLogControlsFileError() {
        PreferenceManager preferenceManager = getPreferenceManager();
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(mockFileManager));
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText("None")));
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).check(matches(Matchers.not(isEnabled())));
        assertFalse(preferenceManager.getPreferenceLogFile());
    }

    @Test
    public void testDownloadFolderDialogFileError() {
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(mockFileManager));
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
    }

    @Test
    public void testLogFolderDialogFileError() {
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(mockFileManager));
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error accessing external files directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_log_file_label)).check(matches(withText("Log to file")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_folder_label)).check(matches(withText("Log folder")));
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
    }

    @Test
    public void testDownloadFolderDialogOkFileError() {
        PreferenceManager preferenceManager = getPreferenceManager();
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(mockFileManager));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error creating download directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("download"))));
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
    }

    @Test
    public void testLogFolderDialogOkFileError() {
        PreferenceManager preferenceManager = getPreferenceManager();
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        MockFileManager mockFileManager = new MockFileManager();
        mockFileManager.setExternalDirectory(null, 0);
        mockFileManager.setExternalRootDirectory(null, 0);
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(mockFileManager));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Error creating log directory.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("log"))));
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
    }

    @Test
    public void testResetValues() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("4"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_global_settings_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(not(isChecked())));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_label)).check(matches(withText("Download to an external storage folder")));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_folder_label)).check(matches(withText("Download folder")));
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText("Internal storage folder")));
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_activity_global_settings_download_keep_label)).check(matches(withText("Keep downloaded files")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(not(isEnabled())));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isNotChecked()));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_download_keep)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testConfirmDialogScreenRotation() {
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
    }

    @Test
    public void testValidationErrorScreenRotation() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
    }

    @Test
    public void testValidationErrorColorScreenRotation() {
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("a")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("a")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
    }

    @Test
    public void testDownloadFolderDialogScreenRotation() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("download")));
        rotateScreen(activityScenario);
        File testFile = new File(getFileManager().getExternalRootDirectory(0), "test");
        assertFalse(testFile.exists());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.scrollview_dialog_file_choose)).perform(swipeUp());
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_download_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(testFile.exists());
        rotateScreen(activityScenario);
    }

    @Test
    public void testLogFolderDialogScreenRotation() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(scrollTo());
        onView(withId(R.id.switch_activity_global_settings_log_file)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(click());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("log")));
        rotateScreen(activityScenario);
        File testFile = new File(getFileManager().getExternalRootDirectory(0), "test");
        assertFalse(testFile.exists());
        onView(withId(R.id.edittext_dialog_file_choose_folder)).perform(replaceText("test"));
        onView(withId(R.id.scrollview_dialog_file_choose)).perform(swipeUp());
        onView(withId(R.id.imageview_dialog_file_choose_ok)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_log_folder)).perform(scrollTo());
        onView(withId(R.id.textview_activity_global_settings_log_folder)).check(matches(withText(endsWith("test"))));
        assertEquals("test", preferenceManager.getPreferenceLogFolder());
        assertTrue(testFile.exists());
        rotateScreen(activityScenario);
    }

    private SettingsInputDialog getDialog() {
        return (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }
}
