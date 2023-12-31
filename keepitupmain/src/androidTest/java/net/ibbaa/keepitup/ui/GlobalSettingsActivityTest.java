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

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.matcher.FontSizeMatcher;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GlobalSettingsActivityTest extends BaseUITest {

    @Test
    public void testDisplayDefaultValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
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
        activityScenario.close();
    }

    @Test
    public void testDisplayValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
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
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(allOf(withText("Disabled"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
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
        activityScenario.close();
    }

    @Test
    public void testSwitchYesNoText() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_on_off)).check(matches(withText("yes")));
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
        activityScenario.close();
    }

    @Test
    public void testSetPreferencesOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
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
        assertFalse(preferenceManager.getPreferenceSuspensionEnabled());
        assertTrue(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertTrue(preferenceManager.getPreferenceDownloadKeep());
        assertTrue(preferenceManager.getPreferenceLogFile());
        activityScenario.close();
    }

    @Test
    public void testSetPreferencesNotificationType() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(NotificationType.CHANGE, preferenceManager.getPreferenceNotificationType());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).perform(click());
        assertEquals(NotificationType.FAILURE, preferenceManager.getPreferenceNotificationType());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_failure)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isNotChecked()));
        activityScenario.close();
    }

    @Test
    public void testSetPingCountPreferencesCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        activityScenario.close();
    }

    @Test
    public void testPingCountInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testPingCountCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testPingCountCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("5");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("6")));
        assertTrue(clipboardManager.hasData());
        assertEquals("6", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).check(matches(withText("6")));
        activityScenario.close();
    }

    @Test
    public void testSetConnectCountPreferencesCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("3"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        activityScenario.close();
    }

    @Test
    public void testConnectCountInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testConnectCountCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testConnectCountCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("10");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("10");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("10")));
        assertTrue(clipboardManager.hasData());
        assertEquals("10", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("10")));
        activityScenario.close();
    }

    @Test
    public void testSuspensionNoneDisabled() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Disabled"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledOneInterval() {
        getIntervalDAO().insertInterval(getInterval1());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Disabled"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledTwoIntervals() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Disabled"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledThreeIntervals() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(10), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(10), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(10), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Disabled"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(10), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(10), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(10), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledFiveIntervals() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        getIntervalDAO().insertInterval(getInterval4());
        getIntervalDAO().insertInterval(getInterval5());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(10), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 03:03 End: 04:04"), withFontSize(10), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 05:05 End: 06:06"), withFontSize(10), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(10), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(10), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Disabled"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(10), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 03:03 End: 04:04"), withFontSize(10), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 05:05 End: 06:06"), withFontSize(10), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(10), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(10), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testSuspensionChangeEnabledDisabledSchedulerRestarted() {
        NetworkTask task = getNetworkTask1();
        getNetworkTaskDAO().insertNetworkTask(task);
        getIntervalDAO().insertInterval(getInterval1());
        setTestTime(getTestTimestamp(24, 10, 15));
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        assertTrue(getTimeBasedSuspensionScheduler().isSuspended());
        assertTrue(getTimeBasedSuspensionScheduler().isRunning());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        assertFalse(getTimeBasedSuspensionScheduler().isSuspended());
        assertFalse(getTimeBasedSuspensionScheduler().isRunning());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        assertTrue(getTimeBasedSuspensionScheduler().isSuspended());
        assertTrue(getTimeBasedSuspensionScheduler().isRunning());
    }

    @Test
    public void testSuspensionSevenIntervals() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        getIntervalDAO().insertInterval(getInterval4());
        getIntervalDAO().insertInterval(getInterval5());
        getIntervalDAO().insertInterval(getInterval6());
        getIntervalDAO().insertInterval(getInterval7());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(10), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 03:03 End: 04:04"), withFontSize(10), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 05:05 End: 06:06"), withFontSize(10), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 07:07 End: 08:08"), withFontSize(10), withGridLayoutPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 09:09 End: 09:39"), withFontSize(10), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(10), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(10), withGridLayoutPosition(3, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddOneCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertTrue(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddOneCancelScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertTrue(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddOneOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 22:00 End: 04:00"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertEquals(1, getTimeBasedSuspensionScheduler().getIntervals().size());
        Interval interval = getTimeBasedSuspensionScheduler().getIntervals().get(0);
        Interval newInterval = new Interval();
        Time start = new Time();
        start.setHour(22);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(0);
        newInterval.setStart(start);
        newInterval.setEnd(end);
        assertTrue(interval.isEqual(newInterval));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddOneOkScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 22:00 End: 04:00"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertEquals(1, getTimeBasedSuspensionScheduler().getIntervals().size());
        Interval interval = getTimeBasedSuspensionScheduler().getIntervals().get(0);
        Interval newInterval = new Interval();
        Time start = new Time();
        start.setHour(22);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(0);
        newInterval.setStart(start);
        newInterval.setEnd(end);
        assertTrue(interval.isEqual(newInterval));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddTwoCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 05:00 End: 07:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertTrue(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddTwoCancelScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 05:00 End: 07:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertTrue(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddTwoOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 05:00 End: 07:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 05:00 End: 07:00"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:00 End: 04:00"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        Interval interval1 = getTimeBasedSuspensionScheduler().getIntervals().get(0);
        Interval interval2 = getTimeBasedSuspensionScheduler().getIntervals().get(1);
        Interval newInterval1 = new Interval();
        Time start1 = new Time();
        start1.setHour(5);
        start1.setMinute(0);
        Time end1 = new Time();
        end1.setHour(7);
        end1.setMinute(0);
        newInterval1.setStart(start1);
        newInterval1.setEnd(end1);
        Interval newInterval2 = new Interval();
        Time start2 = new Time();
        start2.setHour(22);
        start2.setMinute(0);
        Time end2 = new Time();
        end2.setHour(4);
        end2.setMinute(0);
        newInterval2.setStart(start2);
        newInterval2.setEnd(end2);
        assertTrue(interval1.isEqual(newInterval1));
        assertTrue(interval2.isEqual(newInterval2));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddTwoOkScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 05:00 End: 07:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 05:00 End: 07:00"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:00 End: 04:00"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        Interval interval1 = getTimeBasedSuspensionScheduler().getIntervals().get(0);
        Interval interval2 = getTimeBasedSuspensionScheduler().getIntervals().get(1);
        Interval newInterval1 = new Interval();
        Time start1 = new Time();
        start1.setHour(5);
        start1.setMinute(0);
        Time end1 = new Time();
        end1.setHour(7);
        end1.setMinute(0);
        newInterval1.setStart(start1);
        newInterval1.setEnd(end1);
        Interval newInterval2 = new Interval();
        Time start2 = new Time();
        start2.setHour(22);
        start2.setMinute(0);
        Time end2 = new Time();
        end2.setHour(4);
        end2.setMinute(0);
        newInterval2.setStart(start2);
        newInterval2.setEnd(end2);
        assertTrue(interval1.isEqual(newInterval1));
        assertTrue(interval2.isEqual(newInterval2));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddSevenOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(1));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(1));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(2));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(4));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(5));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(6));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(6));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(7));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(7));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(8));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(8));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(9));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(39));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(11));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(12));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(23));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(59));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(10), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 03:03 End: 04:04"), withFontSize(10), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 05:05 End: 06:06"), withFontSize(10), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 07:07 End: 08:08"), withFontSize(10), withGridLayoutPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 09:09 End: 09:39"), withFontSize(10), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(10), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(10), withGridLayoutPosition(3, 1))).check(matches(isDisplayed()));
        assertEquals(7, getTimeBasedSuspensionScheduler().getIntervals().size());
        List<Interval> intervals = getTimeBasedSuspensionScheduler().getIntervals();
        assertTrue(getInterval2().isEqual(intervals.get(0)));
        assertTrue(getInterval4().isEqual(intervals.get(1)));
        assertTrue(getInterval5().isEqual(intervals.get(2)));
        assertTrue(getInterval6().isEqual(intervals.get(3)));
        assertTrue(getInterval7().isEqual(intervals.get(4)));
        assertTrue(getInterval1().isEqual(intervals.get(5)));
        assertTrue(getInterval3().isEqual(intervals.get(6)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalDeleteOneOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertTrue(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalDeleteOneOkScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        assertTrue(getTimeBasedSuspensionScheduler().getIntervals().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalDeleteFiveOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(1));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(1));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(2));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(4));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(5));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(6));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(6));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(7));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(7));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(8));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(8));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(9));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(39));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(11));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(12));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(23));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(59));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 22:15 End: 23:59"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        List<Interval> intervals = getTimeBasedSuspensionScheduler().getIntervals();
        assertTrue(getInterval1().isEqual(intervals.get(0)));
        assertTrue(getInterval3().isEqual(intervals.get(1)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalChangeOneCancel() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(4));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 03:03 End: 04:04")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        List<Interval> intervals = getTimeBasedSuspensionScheduler().getIntervals();
        assertTrue(getInterval2().isEqual(intervals.get(0)));
        assertTrue(getInterval1().isEqual(intervals.get(1)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalChangeOneOk() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(4));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 03:03 End: 04:04")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 03:03 End: 04:04"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        List<Interval> intervals = getTimeBasedSuspensionScheduler().getIntervals();
        assertTrue(getInterval4().isEqual(intervals.get(0)));
        assertTrue(getInterval1().isEqual(intervals.get(1)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalChangeOneOkScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(4));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 03:03 End: 04:04")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 03:03 End: 04:04"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        List<Interval> intervals = getTimeBasedSuspensionScheduler().getIntervals();
        assertTrue(getInterval4().isEqual(intervals.get(0)));
        assertTrue(getInterval1().isEqual(intervals.get(1)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddChangeDelete() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 01:01 End: 02:02"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 10:11 End: 11:12"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(4));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 03:03 End: 04:04")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(14));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(14));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(15));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 14:14 End: 15:15")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(11));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:15")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 14:14 End: 15:15")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:15")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 14:14 End: 15:15")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(allOf(withText("Start: 10:11 End: 11:15"), withFontSize(12), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Start: 14:14 End: 15:15"), withFontSize(12), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        assertEquals(2, getTimeBasedSuspensionScheduler().getIntervals().size());
        Interval interval1 = getTimeBasedSuspensionScheduler().getIntervals().get(0);
        Interval interval2 = getTimeBasedSuspensionScheduler().getIntervals().get(1);
        Interval newInterval1 = new Interval();
        Time start1 = new Time();
        start1.setHour(10);
        start1.setMinute(11);
        Time end1 = new Time();
        end1.setHour(11);
        end1.setMinute(15);
        newInterval1.setStart(start1);
        newInterval1.setEnd(end1);
        Interval newInterval2 = new Interval();
        Time start2 = new Time();
        start2.setHour(14);
        start2.setMinute(14);
        Time end2 = new Time();
        end2.setHour(15);
        end2.setMinute(15);
        newInterval2.setStart(start2);
        newInterval2.setEnd(end2);
        assertTrue(interval1.isEqual(newInterval1));
        assertTrue(interval2.isEqual(newInterval2));
        activityScenario.close();
    }

    @Test
    public void testSuspensionIntervalAddChangeDeleteSchedulerRestarted() {
        NetworkTask task = getNetworkTask1();
        getNetworkTaskDAO().insertNetworkTask(task);
        setTestTime(getTestTimestamp(24, 10, 15));
        getTimeBasedSuspensionScheduler().restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        assertFalse(getTimeBasedSuspensionScheduler().isSuspended());
        assertFalse(getTimeBasedSuspensionScheduler().isRunning());
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(11));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(12));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        assertTrue(getTimeBasedSuspensionScheduler().isSuspended());
        assertTrue(getTimeBasedSuspensionScheduler().isRunning());
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        assertFalse(getTimeBasedSuspensionScheduler().isSuspended());
        assertFalse(getTimeBasedSuspensionScheduler().isRunning());
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(11));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(12));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        assertTrue(getTimeBasedSuspensionScheduler().isSuspended());
        assertTrue(getTimeBasedSuspensionScheduler().isRunning());
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(12));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(14));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        assertFalse(getTimeBasedSuspensionScheduler().isSuspended());
        assertTrue(getTimeBasedSuspensionScheduler().isRunning());
    }

    @Test
    public void testDownloadControls() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isNotChecked()));
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
        activityScenario.close();
    }

    @Test
    public void testLogControls() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadFolderDialogOpen() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_download_folder)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testLogFolderDialogOpen() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadFolderDialogOkCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testLogFolderDialogOkCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadFolderDialogErrorFileExists() throws IOException {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testLogFolderDialogErrorFileExists() throws IOException {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadControlsFileError() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testLogControlsFileError() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadFolderDialogFileError() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testLogFolderDialogFileError() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadFolderDialogOkFileError() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testLogFolderDialogOkFileError() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testResetValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("4"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
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
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
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
        assertTrue(preferenceManager.getPreferenceSuspensionEnabled());
        assertFalse(preferenceManager.getPreferenceDownloadExternalStorage());
        assertEquals("download", preferenceManager.getPreferenceDownloadFolder());
        assertFalse(preferenceManager.getPreferenceDownloadKeep());
        assertFalse(preferenceManager.getPreferenceLogFile());
        assertEquals("log", preferenceManager.getPreferenceLogFolder());
        activityScenario.close();
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_notification_inactive_network)).perform(click());
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).perform(click());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
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
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_on_off)).check(matches(withText("no")));
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
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.radiobutton_activity_global_settings_notification_type_change)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_global_settings_download_external_storage)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_external_storage_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_download_keep)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_download_keep_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_global_settings_log_file)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_log_file_on_off)).check(matches(withText("yes")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("6"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_connect_count)).check(matches(withText("2")));
        activityScenario.close();
    }

    @Test
    public void testValidationErrorScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testDownloadFolderDialogScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    @Test
    public void testLogFolderDialogScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
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
        activityScenario.close();
    }

    private void setTestTime(long time) {
        getTimeService().setTimestamp(time);
        getTimeService().setTimestamp2(time);
    }

    private long getTestTimestamp(int day, int hour, int minute) {
        Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, day, hour, minute, 1);
        return calendar.getTimeInMillis();
    }

    public static Matcher<View> withFontSize(float expectedSize) {
        return new FontSizeMatcher(expectedSize);
    }

    private SettingsInputDialog getDialog(ActivityScenario<?> activityScenario) {
        return (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask task = new NetworkTask();
        task.setId(1);
        task.setIndex(1);
        task.setSchedulerId(1);
        task.setInstances(0);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(20);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(1);
        return task;
    }

    private Interval getInterval1() {
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

    private Interval getInterval2() {
        Interval interval = new Interval();
        interval.setId(0);
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
        interval.setId(0);
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

    private Interval getInterval4() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(3);
        start.setMinute(3);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(4);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval5() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(5);
        start.setMinute(5);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(6);
        end.setMinute(6);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval6() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(7);
        start.setMinute(7);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(8);
        end.setMinute(8);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval7() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(9);
        start.setMinute(9);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(39);
        interval.setEnd(end);
        return interval;
    }
}
