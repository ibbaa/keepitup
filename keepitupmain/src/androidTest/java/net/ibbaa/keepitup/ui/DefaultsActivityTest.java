/*
 * Copyright (c) 2026 Alwin Ibba
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
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;
import net.ibbaa.keepitup.ui.sync.HeaderSyncHandler;
import net.ibbaa.keepitup.util.StringUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class DefaultsActivityTest extends BaseUITest {

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        DBSetup dbSetup = new DBSetup(TestRegistry.getContext());
        dbSetup.initializeHeaderTable();
        resetGlobalHeaderHandler();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        resetGlobalHeaderHandler();
    }

    @Test
    public void testDisplayDefaultValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals("", preferenceManager.getPreferenceResolveMatchAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolveMatchPort());
        assertEquals("", preferenceManager.getPreferenceResolveAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolvePort());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
        assertFalse(preferenceManager.getPreferenceIgnoreSSLError());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceHighPrio());
        onView(withId(R.id.textview_activity_defaults_accesstype_label)).check(matches(withText("Type")));
        onView(withId(R.id.radiogroup_activity_defaults_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_activity_defaults_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("15")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size_label)).check(matches(withText("Ping package size")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.textview_activity_defaults_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_defaults_match_host_label)).check(matches(withText("Match host")));
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_port_label)).check(matches(withText("Match port")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host_label)).check(matches(withText("Connect-to host")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port_label)).check(matches(withText("Connect-to port")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_label)).check(matches(withText("Ignore SSL errors")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_only_wifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_high_prio_label)).check(matches(withText("High priority")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("no")));
        activityScenario.close();
    }

    @Test
    public void testDisplayDefaultValuesChanged() {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceAccessType(AccessType.CONNECT);
        preferenceManager.setPreferenceAddress("127.0.0.1");
        preferenceManager.setPreferencePort(1024);
        preferenceManager.setPreferenceInterval(1);
        preferenceManager.setPreferencePingCount(8);
        preferenceManager.setPreferencePingPackageSize(1234);
        preferenceManager.setPreferenceConnectCount(9);
        preferenceManager.setPreferenceResolveMatchAddress("matchaddress");
        preferenceManager.setPreferenceResolveMatchPort(80);
        preferenceManager.setPreferenceResolveAddress("address");
        preferenceManager.setPreferenceResolvePort(443);
        preferenceManager.setPreferenceStopOnSuccess(true);
        preferenceManager.setPreferenceIgnoreSSLError(true);
        preferenceManager.setPreferenceOnlyWifi(false);
        preferenceManager.setPreferenceNotification(true);
        preferenceManager.setPreferenceHighPrio(true);
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_accesstype_label)).check(matches(withText("Type")));
        onView(withId(R.id.radiogroup_activity_defaults_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("127.0.0.1")));
        onView(withId(R.id.textview_activity_defaults_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("1024")));
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minute")));
        onView(withId(R.id.textview_activity_defaults_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("8")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size_label)).check(matches(withText("Ping package size")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("1234")));
        onView(withId(R.id.textview_activity_defaults_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("9")));
        onView(withId(R.id.textview_activity_defaults_match_host_label)).check(matches(withText("Match host")));
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("matchaddress")));
        onView(withId(R.id.textview_activity_defaults_match_port_label)).check(matches(withText("Match port")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host_label)).check(matches(withText("Connect-to host")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("address")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port_label)).check(matches(withText("Connect-to port")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("443")));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_label)).check(matches(withText("Ignore SSL errors")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_only_wifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_high_prio_label)).check(matches(withText("High priority")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("yes")));
        activityScenario.close();
    }

    @Test
    public void testDisplayValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withText("Download")).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("64"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("matchaddress"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("address"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size_label)).check(matches(withText("Ping package size")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("64")));
        onView(withId(R.id.textview_activity_defaults_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_defaults_match_host_label)).check(matches(withText("Match host")));
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("matchaddress")));
        onView(withId(R.id.textview_activity_defaults_match_port_label)).check(matches(withText("Match port")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host_label)).check(matches(withText("Connect-to host")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("address")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port_label)).check(matches(withText("Connect-to port")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("443")));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_label)).check(matches(withText("Ignore SSL errors")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_only_wifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_high_prio_label)).check(matches(withText("High priority")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("yes")));
        activityScenario.close();
    }

    @Test
    public void testMinutes() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minute")));
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        activityScenario.close();
    }

    @Test
    public void testSwitchYesNoText() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("no")));
        activityScenario.close();
    }

    @Test
    public void testSetPreferencesOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withText("Download")).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("9"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("matchaddress"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("address"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("22"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.DOWNLOAD, preferenceManager.getPreferenceAccessType());
        assertEquals("localhost", preferenceManager.getPreferenceAddress());
        assertEquals(80, preferenceManager.getPreferencePort());
        assertEquals(50, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(123, preferenceManager.getPreferencePingPackageSize());
        assertEquals(9, preferenceManager.getPreferenceConnectCount());
        assertEquals("matchaddress", preferenceManager.getPreferenceResolveMatchAddress());
        assertEquals(80, preferenceManager.getPreferenceResolveMatchPort());
        assertEquals("address", preferenceManager.getPreferenceResolveAddress());
        assertEquals(22, preferenceManager.getPreferenceResolvePort());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
        assertTrue(preferenceManager.getPreferenceIgnoreSSLError());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertTrue(preferenceManager.getPreferenceHighPrio());
        activityScenario.close();
    }

    @Test
    public void testSetPreferencesCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("matchaddress"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("address"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("22"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals("", preferenceManager.getPreferenceResolveMatchAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolveMatchPort());
        assertEquals("", preferenceManager.getPreferenceResolveAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolvePort());
        activityScenario.close();
    }

    @Test
    public void testAddressInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 2.33"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid URL"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(2, 0))).check(doesNotExist());
        onView(allOf(withText("No value specified"), withGridLayoutPosition(2, 1))).check(doesNotExist());
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.2.100")));
        activityScenario.close();
    }

    @Test
    public void testAddressTrimmed() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("  host.com  "));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100  "));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.2.100")));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNone() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNoneScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeader() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("User-Agent: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderTwoHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(2, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderTwoHeadersScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(2, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderFiveHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(5, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderSixHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("1 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(6, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("Name5", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Value5", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(5).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(5).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(5).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(5).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderTenHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        getHeaderDAO().insertHeader(getHeader(6));
        getHeaderDAO().insertHeader(getHeader(7));
        getHeaderDAO().insertHeader(getHeader(8));
        getHeaderDAO().insertHeader(getHeader(9));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("5 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(10, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("Name5", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Value5", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        assertEquals("Name6", handler.getGlobalHeaders().get(5).getName());
        assertEquals("Value6", handler.getGlobalHeaders().get(5).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(5).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(5).isValueValid());
        assertEquals("Name7", handler.getGlobalHeaders().get(6).getName());
        assertEquals("Value7", handler.getGlobalHeaders().get(6).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(6).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(6).isValueValid());
        assertEquals("Name8", handler.getGlobalHeaders().get(7).getName());
        assertEquals("Value8", handler.getGlobalHeaders().get(7).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(7).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(7).isValueValid());
        assertEquals("Name9", handler.getGlobalHeaders().get(8).getName());
        assertEquals("Value9", handler.getGlobalHeaders().get(8).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(8).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(8).isValueValid());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(9).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(9).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(9).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(9).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderToggleValue() {
        getHeaderDAO().deleteAllHeaders();
        Header header = getHeader(1);
        header.setValue("122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890");
        getHeaderDAO().insertHeader(header);
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isEllipsized()));
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).perform(click());
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(not(isEllipsized())));
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).perform(scrollTo());
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).perform(click());
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isEllipsized()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderToggleMoreLessHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        getHeaderDAO().insertHeader(getHeader(6));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("2 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("2 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).perform(click());
        onView(allOf(withText("less…"), withFontSize(10), withGridLayoutRowColumnPosition(8, 0))).perform(scrollTo());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name6: "), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value6"), withFontSize(10), withGridLayoutRowColumnPosition(6, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(10), withGridLayoutRowColumnPosition(7, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(10), withGridLayoutRowColumnPosition(7, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("less…"), withFontSize(10), withGridLayoutRowColumnPosition(8, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("less…"), withFontSize(10), withGridLayoutRowColumnPosition(8, 0))).perform(click());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("2 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderToggleMoreLessHeadersScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        getHeaderDAO().insertHeader(getHeader(6));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("2 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("2 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withText("less…"), withFontSize(10), withGridLayoutRowColumnPosition(8, 0))).perform(scrollTo());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name6: "), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value6"), withFontSize(10), withGridLayoutRowColumnPosition(6, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("User-Agent: "), withFontSize(10), withGridLayoutRowColumnPosition(7, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(10), withGridLayoutRowColumnPosition(7, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("less…"), withFontSize(10), withGridLayoutRowColumnPosition(8, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("less…"), withFontSize(10), withGridLayoutRowColumnPosition(8, 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("2 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNoneAddOneCancel() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNoneAddOneCancelScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNoneAddOneOk() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Name", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNoneAddOneOkScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Name", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderAddBasicAuthOk() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("abc:123", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderAddBasicAuthOkScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("abc:123", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderAddAuthorizationOk() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderAddAuthorizationOkScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersAddOneCancel() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(3, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersAddOneCancelScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(3, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersAddOneOk() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("AName: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("AValue"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(4, handler.getGlobalHeaders().size());
        assertEquals("AName", handler.getGlobalHeaders().get(0).getName());
        assertEquals("AValue", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name1", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersAddOneOkScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("AName: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("AValue"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(4, handler.getGlobalHeaders().size());
        assertEquals("AName", handler.getGlobalHeaders().get(0).getName());
        assertEquals("AValue", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name1", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersAddThree() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name5"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value5"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value2"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name6"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value6"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("1 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).perform(scrollTo());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("1 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(6, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("Name5", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Value5", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        assertEquals("Name6", handler.getGlobalHeaders().get(5).getName());
        assertEquals("Value6", handler.getGlobalHeaders().get(5).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(5).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(5).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersAddThreeRotateScreen() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name5"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value5"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value2"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name6"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value6"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("1 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).perform(scrollTo());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("1 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(6, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("Name5", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Value5", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        assertEquals("Name6", handler.getGlobalHeaders().get(5).getName());
        assertEquals("Value6", handler.getGlobalHeaders().get(5).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(5).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(5).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderAddEllipsized() {
        getHeaderDAO().deleteAllHeaders();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890"));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withFontSize(14), withGridLayoutRowColumnPosition(1, 1), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isEllipsized()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Name", handler.getGlobalHeaders().get(0).getName());
        assertEquals("122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890", handler.getGlobalHeaders().get(0).getValue());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderDeleteCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("User-Agent: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderDeleteCancelScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("User-Agent: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderDeleteOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertTrue(handler.getGlobalHeaders().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderDeleteOkScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertTrue(handler.getGlobalHeaders().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersDeleteOne() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(2, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersDeleteOneScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(2, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNineHeadersDeleteAllCancel() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        getHeaderDAO().insertHeader(getHeader(6));
        getHeaderDAO().insertHeader(getHeader(7));
        getHeaderDAO().insertHeader(getHeader(8));
        getHeaderDAO().insertHeader(getHeader(9));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(2)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("4 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).perform(scrollTo());
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name2: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value2"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name5: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value5"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("4 more…"), withFontSize(10), withGridLayoutRowColumnPosition(6, 0))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(9, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name2", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value2", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("Name5", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Value5", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        assertEquals("Name6", handler.getGlobalHeaders().get(5).getName());
        assertEquals("Value6", handler.getGlobalHeaders().get(5).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(5).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(5).isValueValid());
        assertEquals("Name7", handler.getGlobalHeaders().get(6).getName());
        assertEquals("Value7", handler.getGlobalHeaders().get(6).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(6).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(6).isValueValid());
        assertEquals("Name8", handler.getGlobalHeaders().get(7).getName());
        assertEquals("Value8", handler.getGlobalHeaders().get(7).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(7).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(7).isValueValid());
        assertEquals("Name9", handler.getGlobalHeaders().get(8).getName());
        assertEquals("Value9", handler.getGlobalHeaders().get(8).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(8).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(8).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderNineHeadersDeleteAllOk() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        getHeaderDAO().insertHeader(getHeader(6));
        getHeaderDAO().insertHeader(getHeader(7));
        getHeaderDAO().insertHeader(getHeader(8));
        getHeaderDAO().insertHeader(getHeader(9));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(2)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("None"), withFontSize(14), withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertTrue(handler.getGlobalHeaders().isEmpty());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderEditCancel() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("User-Agent: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderEditCancelScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("User-Agent: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderEditOk() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderOneHeaderEditOkScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersEditOne() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name4"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value4"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(3, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderThreeHeadersEditOneScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name4"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value4"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(3, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderAddEditDelete() {
        getHeaderDAO().deleteAllHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        getHeaderDAO().insertHeader(getHeader(6));
        getHeaderDAO().insertHeader(getHeader(7));
        getHeaderDAO().insertHeader(getHeader(8));
        getHeaderDAO().insertHeader(getHeader(9));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(8)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 7))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(6)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 5))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(4)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name1"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value1"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name8"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value8"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name9"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value9"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("Name1: "), withFontSize(10), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(10), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name3: "), withFontSize(10), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value3"), withFontSize(10), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name4: "), withFontSize(10), withGridLayoutRowColumnPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value4"), withFontSize(10), withGridLayoutRowColumnPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name8: "), withFontSize(10), withGridLayoutRowColumnPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value8"), withFontSize(10), withGridLayoutRowColumnPosition(4, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name9: "), withFontSize(10), withGridLayoutRowColumnPosition(5, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value9"), withFontSize(10), withGridLayoutRowColumnPosition(5, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(5, handler.getGlobalHeaders().size());
        assertEquals("Name1", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        assertEquals("Name3", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value3", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(1).isValueValid());
        assertEquals("Name4", handler.getGlobalHeaders().get(2).getName());
        assertEquals("Value4", handler.getGlobalHeaders().get(2).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(2).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(2).isValueValid());
        assertEquals("Name8", handler.getGlobalHeaders().get(3).getName());
        assertEquals("Value8", handler.getGlobalHeaders().get(3).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(3).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(3).isValueValid());
        assertEquals("Name9", handler.getGlobalHeaders().get(4).getName());
        assertEquals("Value9", handler.getGlobalHeaders().get(4).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(4).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(4).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderInvalidDecryptionKeyCancel() {
        getHeaderDAO().deleteAllHeaders();
        Header header = getHeader(1);
        header.setHeaderType(HeaderType.BASICAUTH);
        header.setName("Authorization");
        header.setValue("xyz:abc");
        getHeaderDAO().insertHeader(header);
        corruptKey();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderInvalidDecryptionKeyCancelScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        Header header = getHeader(1);
        header.setHeaderType(HeaderType.BASICAUTH);
        header.setName("Authorization");
        header.setValue("xyz:abc");
        getHeaderDAO().insertHeader(header);
        corruptKey();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderInvalidDecryptionKey() {
        getHeaderDAO().deleteAllHeaders();
        Header header = getHeader(1);
        header.setHeaderType(HeaderType.GENERICAUTH);
        header.setName("Authorization");
        header.setValue("xyz");
        getHeaderDAO().insertHeader(header);
        corruptKey();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("xyz"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textColor)));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("xyz", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testHTTPHeaderInvalidDecryptionKeyScreenRotation() {
        getHeaderDAO().deleteAllHeaders();
        Header header = getHeader(1);
        header.setHeaderType(HeaderType.GENERICAUTH);
        header.setName("Authorization");
        header.setValue("xyz");
        getHeaderDAO().insertHeader(header);
        corruptKey();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("xyz"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textColor)));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("xyz", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testTwoHeadersInvalidDecryptionKey() {
        getHeaderDAO().deleteAllHeaders();
        Header header1 = getHeader(1);
        Header header2 = getHeader(1);
        header2.setHeaderType(HeaderType.BASICAUTH);
        header2.setName("Authorization");
        header2.setValue("xyz:123");
        getHeaderDAO().insertHeaders(List.of(header1, header2));
        corruptKey();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Authorization: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("xyz"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(allOf(withText("Authorization: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(12), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withText("************"), withFontSize(12), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Name1: "), withFontSize(12), withGridLayoutRowColumnPosition(2, 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withText("Value1"), withFontSize(12), withGridLayoutRowColumnPosition(2, 1))).check(matches(withTextColor(R.color.textColor)));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(2, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("xyz:123", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        assertEquals("Name1", handler.getGlobalHeaders().get(1).getName());
        assertEquals("Value1", handler.getGlobalHeaders().get(1).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(1).getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testBasicAuthValidation() {
        getHeaderDAO().deleteAllHeaders();
        Header header = getHeader(1);
        header.setHeaderType(HeaderType.BASICAUTH);
        header.setName("Authorization");
        header.setValue("xyz:123");
        getHeaderDAO().insertHeader(header);
        corruptKey();
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withText("Basic auth password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Authorization: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withText("************"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(withTextColor(R.color.textErrorColor)));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("Authorization", handler.getGlobalHeaders().get(0).getName());
        assertEquals("", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, handler.getGlobalHeaders().get(0).getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testResetValuesGlobalHeaders() {
        getHeaderDAO().deleteGlobalHeaders();
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        resetGlobalHeaderHandler();
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        ((DefaultsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(getTimeBasedSuspensionScheduler());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_global_headers_label)).check(matches(withText("HTTP Headers")));
        onView(allOf(withText("User-Agent: "), withFontSize(14), withGridLayoutRowColumnPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-"), withFontSize(14), withGridLayoutRowColumnPosition(1, 1))).check(matches(isDisplayed()));
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        assertEquals(1, handler.getGlobalHeaders().size());
        assertEquals("User-Agent", handler.getGlobalHeaders().get(0).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", handler.getGlobalHeaders().get(0).getValue());
        assertEquals(HeaderType.GENERIC, handler.getGlobalHeaders().get(0).getHeaderType());
        assertTrue(handler.getGlobalHeaders().get(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testAddressCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("data")));
        activityScenario.close();
    }

    @Test
    public void testAddressCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("data");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("data")));
        activityScenario.close();
    }

    @Test
    public void testPortInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("70000"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 65535"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        activityScenario.close();
    }

    @Test
    public void testPortCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("456")));
        activityScenario.close();
    }

    @Test
    public void testPortCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("1234");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("456")));
        activityScenario.close();
    }

    @Test
    public void testIntervalInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("xyz"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("15")));
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("20"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("20")));
        activityScenario.close();
    }

    @Test
    public void testIntervalCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("111");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("222"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("111")));
        assertTrue(clipboardManager.hasData());
        assertEquals("111", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("111")));
        activityScenario.close();
    }

    @Test
    public void testIntervalCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("111");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("222"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("111");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("111")));
        assertTrue(clipboardManager.hasData());
        assertEquals("111", clipboardManager.getData());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("111")));
        activityScenario.close();
    }

    @Test
    public void testPingCountInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 10"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("5")));
        activityScenario.close();
    }

    @Test
    public void testPingCountCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("6")));
        activityScenario.close();
    }

    @Test
    public void testPingCountCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("6")));
        activityScenario.close();
    }

    @Test
    public void testPingPackageSizeInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("-1"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 0"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("65528"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 65527"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("5")));
        activityScenario.close();
    }

    @Test
    public void testPingPackageSizeCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("6")));
        activityScenario.close();
    }

    @Test
    public void testPingPackageSizeCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        SettingsInputDialog inputDialog = (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("55");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("66"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("55");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("66")));
        assertTrue(clipboardManager.hasData());
        assertEquals("66", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("66")));
        activityScenario.close();
    }

    @Test
    public void testConnectCountInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 10"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("5")));
        activityScenario.close();
    }

    @Test
    public void testConnectCountCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("10")));
        activityScenario.close();
    }

    @Test
    public void testConnectCountCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("10")));
        activityScenario.close();
    }

    @Test
    public void testMatchHostInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 2.33"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Match host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("192.168.2.100")));
        activityScenario.close();
    }

    @Test
    public void testMatchHostTrimmed() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("  host.com  "));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100  "));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("192.168.2.100")));
        activityScenario.close();
    }

    @Test
    public void testMatchHostCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("data")));
        activityScenario.close();
    }

    @Test
    public void testMatchHostCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("data");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("data")));
        activityScenario.close();
    }

    @Test
    public void testMatchPortInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Match port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("70000"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Match port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 65535"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("80")));
        activityScenario.close();
    }

    @Test
    public void testMatchPortCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("456")));
        activityScenario.close();
    }

    @Test
    public void testMatchPortCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("1234");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("456")));
        activityScenario.close();
    }

    @Test
    public void testConnectToHostInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 2.33"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect-to host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("192.168.2.100")));
        activityScenario.close();
    }

    @Test
    public void testConnectToHostTrimmed() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("  host.com  "));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100  "));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("192.168.2.100")));
        activityScenario.close();
    }

    @Test
    public void testConnectToHostCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("data")));
        activityScenario.close();
    }

    @Test
    public void testConnectToHostCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("data");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("data")));
        activityScenario.close();
    }

    @Test
    public void testConnectToPortInput() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("70000"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 65535"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("80")));
        activityScenario.close();
    }

    @Test
    public void testConnectToPortCopyPasteOption() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        SettingsInputDialog inputDialog = getDialog(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("456")));
        activityScenario.close();
    }

    @Test
    public void testConnectToPortCopyPasteOptionScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog(activityScenario));
        clipboardManager.putData("1234");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("456")));
        activityScenario.close();
    }

    @Test
    public void testMatchHostAndPortPreferenceRemoved() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceResolveMatchAddress("matchaddress");
        preferenceManager.setPreferenceResolveMatchPort(80);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("not set")));
        assertEquals("", preferenceManager.getPreferenceResolveMatchAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolveMatchPort());
        activityScenario.close();
    }

    @Test
    public void testConnectToHostAndPortPreferenceRemoved() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceResolveAddress("address");
        preferenceManager.setPreferenceResolvePort(443);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("not set")));
        assertEquals("", preferenceManager.getPreferenceResolveAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolvePort());
        activityScenario.close();
    }

    @Test
    public void testResetValues() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withText("Download")).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("33"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("4"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("matchaddress"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("address"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("4"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_activity_defaults_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("15")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size_label)).check(matches(withText("Ping package size")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.textview_activity_defaults_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_defaults_match_host_label)).check(matches(withText("Match host")));
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_port_label)).check(matches(withText("Match port")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host_label)).check(matches(withText("Connect-to host")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port_label)).check(matches(withText("Connect-to port")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_label)).check(matches(withText("Ignore SSL errors")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_only_wifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_high_prio_label)).check(matches(withText("High priority")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("no")));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertEquals("", preferenceManager.getPreferenceResolveMatchAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolveMatchPort());
        assertEquals("", preferenceManager.getPreferenceResolveAddress());
        assertEquals(-1, preferenceManager.getPreferenceResolvePort());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
        assertFalse(preferenceManager.getPreferenceIgnoreSSLError());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertFalse(preferenceManager.getPreferenceHighPrio());
        activityScenario.close();
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("22"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("matchaddress"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("address"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("4"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        rotateScreen(activityScenario);
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("matchaddress")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("address")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("4")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("matchaddress")));
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("address")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("4")));
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_high_prio_on_off)).check(matches(withText("yes")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationAddress() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("55"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("55"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("55")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationPingCount() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("5")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationPingPackageSize() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("33"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("33"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("33")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationConnectCount() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("1")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("5")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationMatchHost() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_host)).check(matches(withText("localhost")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationMatchPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("55"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("55"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_match_port)).check(matches(withText("55")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationConnectToHost() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).check(matches(withText("localhost")));
        activityScenario.close();
    }

    @Test
    public void testConfirmDialogOnScreenRotationConnectToPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("55"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("not set")));
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("55"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).check(matches(withText("55")));
        activityScenario.close();
    }

    @Test
    public void testValidationErrorScreenRotationAddress() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1. 34.2"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Host / URL"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationAddress() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1. 34.2"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("1. 34.2")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("1. 34.2")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorScreenRotationPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
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
    public void testValidationErrorScreenRotationInterval() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("999999999999999"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: " + Integer.MAX_VALUE), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: " + Integer.MAX_VALUE), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: " + Integer.MAX_VALUE), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationInterval() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("999999999999999"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("999999999999999")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("999999999999999")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorScreenRotationPingCount() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
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
    public void testValidationErrorColorScreenRotationPingCount() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
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
    public void testValidationErrorScreenRotationPingPackageSize() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Ping package size"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationPingPackageSize() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
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
    public void testValidationErrorScreenRotationConnectCount() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Connect count"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationConnectCount() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
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
    public void testValidationErrorScreenRotationMatchHost() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1. 34.2"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Match host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Match host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Match host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationMatchHost() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1. 34.2"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("1. 34.2")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("1. 34.2")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorScreenRotationMatchPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Match port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Match port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Match port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationMatchPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_match_port)).perform(click());
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
    public void testValidationErrorScreenRotationConnectToHost() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1. 34.2"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect-to host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Connect-to host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Connect-to host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationConnectToHost() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1. 34.2"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("1. 34.2")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("1. 34.2")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorScreenRotationConnectToPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorColorScreenRotationConnectToPort() {
        ActivityScenario<?> activityScenario = launchSettingsInputActivity(DefaultsActivity.class);
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
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

    private void resetGlobalHeaderHandler() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
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

    private void corruptKey() {
        String main_key_prefs_file = TestRegistry.getContext().getResources().getString(R.string.main_key_prefs_file);
        String mainKeyPrefsKey = TestRegistry.getContext().getResources().getString(R.string.main_key_prefs_key);
        SharedPreferences.Editor mainKeyPreferences = TestRegistry.getContext().getSharedPreferences(main_key_prefs_file, Context.MODE_PRIVATE).edit();
        mainKeyPreferences.putString(mainKeyPrefsKey, StringUtil.byteArrayToBase64(new byte[32]));
        mainKeyPreferences.commit();
    }

    private Header getHeader(int number) {
        Header header = new Header();
        header.setNetworkTaskId(-1);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("Name" + number);
        header.setValue("Value" + number);
        header.setValueValid(true);
        return header;
    }
}
