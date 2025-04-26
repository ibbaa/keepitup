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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DefaultsActivityTest extends BaseUITest {

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
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
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
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_highprio_label)).check(matches(withText("High prio")));
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("no")));
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
        preferenceManager.setPreferenceStopOnSuccess(true);
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
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_highprio_label)).check(matches(withText("High prio")));
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("yes")));
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
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(click());
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size_label)).check(matches(withText("Ping package size")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("64")));
        onView(withId(R.id.textview_activity_defaults_connect_count_label)).check(matches(withText("Connect count")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_highprio_label)).check(matches(withText("High prio")));
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("yes")));
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
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(click());
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(click());
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("no")));

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
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.DOWNLOAD, preferenceManager.getPreferenceAccessType());
        assertEquals("localhost", preferenceManager.getPreferenceAddress());
        assertEquals(80, preferenceManager.getPreferencePort());
        assertEquals(50, preferenceManager.getPreferenceInterval());
        assertEquals(5, preferenceManager.getPreferencePingCount());
        assertEquals(123, preferenceManager.getPreferencePingPackageSize());
        assertEquals(9, preferenceManager.getPreferenceConnectCount());
        assertTrue(preferenceManager.getPreferenceStopOnSuccess());
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
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
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
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_label)).check(matches(withText("Stop on success")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.textview_activity_defaults_highprio_label)).check(matches(withText("High prio")));
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("no")));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertEquals(56, preferenceManager.getPreferencePingPackageSize());
        assertEquals(1, preferenceManager.getPreferenceConnectCount());
        assertFalse(preferenceManager.getPreferenceStopOnSuccess());
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
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(click());
        rotateScreen(activityScenario);
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).check(matches(withText("22")));
        onView(withId(R.id.textview_activity_defaults_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_activity_defaults_stoponsuccess)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_stoponsuccess_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_highprio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_highprio)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_highprio_on_off)).check(matches(withText("yes")));
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

    private SettingsInputDialog getDialog(ActivityScenario<?> activityScenario) {
        return (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }
}
