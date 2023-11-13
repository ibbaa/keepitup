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
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.NotificationType;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.matcher.FontSizeMatcher;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestTimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;

import org.hamcrest.Matcher;
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

    private TestTimeBasedSuspensionScheduler scheduler;
    private TestNetworkTaskProcessServiceScheduler networkTaskScheduler;
    private IntervalDAO intervalDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        scheduler = new TestTimeBasedSuspensionScheduler(TestRegistry.getContext());
        networkTaskScheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.setNetworkTaskScheduler(networkTaskScheduler);
        networkTaskScheduler.setTimeBasedSuspensionScheduler(scheduler);
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
        scheduler.reset();
        scheduler.stop();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        intervalDAO.deleteAllIntervals();
        scheduler.reset();
        scheduler.stop();
    }

    @Test
    public void testDisplayDefaultValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("None")));
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Disabled")));
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("None")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Disabled")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("None")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledOneInterval() {
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Start: 10:11 End: 11:12" + System.lineSeparator())));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Disabled")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Start: 10:11 End: 11:12" + System.lineSeparator())));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledTwoIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        String intervalText = "Start: 01:01 End: 02:02" + System.lineSeparator() + "Start: 10:11 End: 11:12" + System.lineSeparator();
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText(intervalText)));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(12)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Disabled")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText(intervalText)));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(12)));
        activityScenario.close();
    }

    @Test
    public void testSuspensionDisabledThreeIntervals() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        scheduler.restart();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        String intervalText = "Start: 01:01 End: 02:02" + System.lineSeparator() + "Start: 10:11 End: 11:12" + System.lineSeparator() + "Start: 22:15 End: 23:59" + System.lineSeparator();
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText(intervalText)));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(10)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText("Disabled")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(14)));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals_label)).check(matches(withText("Defined suspension intervals")));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withText(intervalText)));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).check(matches(withFontSize(10)));
        activityScenario.close();
    }

    @Test
    public void testDownloadControls() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
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
}
