package de.ibba.keepitup.ui;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    @Test
    public void testDisplayDefaultValues() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
        assertEquals(15, preferenceManager.getPreferenceConnectionTimeout());
        assertEquals(10, preferenceManager.getPreferenceReadTimeout());
        onView(withId(R.id.textview_settings_activity_accesstype_label)).check(matches(withText("Type")));
        onView(withId(R.id.radiogroup_settings_activity_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_settings_activity_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_settings_activity_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_settings_activity_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_settings_activity_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("15")));
        onView(withId(R.id.textview_settings_activity_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_settings_activity_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_settings_activity_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_settings_activity_connection_timeout_label)).check(matches(withText("Connection timeout")));
        onView(withId(R.id.textview_settings_activity_connection_timeout)).check(matches(withText("15")));
        onView(withId(R.id.textview_settings_activity_connection_timeout_seconds)).check(matches(withText("seconds")));
        onView(withId(R.id.textview_settings_activity_read_timeout_label)).check(matches(withText("Read timeout")));
        onView(withId(R.id.textview_settings_activity_read_timeout)).check(matches(withText("10")));
        onView(withId(R.id.textview_settings_activity_read_timeout_seconds)).check(matches(withText("seconds")));
    }


    @Test
    public void testDisplayValues() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.textview_settings_activity_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).perform(click());
        onView(withId(R.id.switch_settings_activity_notification)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("12"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("25"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_settings_activity_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_settings_activity_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_settings_activity_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_settings_activity_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_settings_activity_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_settings_activity_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_settings_activity_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_settings_activity_connection_timeout)).check(matches(withText("12")));
        onView(withId(R.id.textview_settings_activity_connection_timeout_seconds)).check(matches(withText("seconds")));
        onView(withId(R.id.textview_settings_activity_read_timeout)).check(matches(withText("25")));
        onView(withId(R.id.textview_settings_activity_read_timeout_seconds)).check(matches(withText("seconds")));
    }

    @Test
    public void testMinutesAndSeconds() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("1")));
        onView(withId(R.id.textview_settings_activity_interval_minutes)).check(matches(withText("minute")));
        onView(withId(R.id.textview_settings_activity_connection_timeout)).check(matches(withText("1")));
        onView(withId(R.id.textview_settings_activity_connection_timeout_seconds)).check(matches(withText("second")));
        onView(withId(R.id.textview_settings_activity_read_timeout)).check(matches(withText("1")));
        onView(withId(R.id.textview_settings_activity_read_timeout_seconds)).check(matches(withText("second")));
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_settings_activity_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_settings_activity_connection_timeout)).check(matches(withText("11")));
        onView(withId(R.id.textview_settings_activity_connection_timeout_seconds)).check(matches(withText("seconds")));
        onView(withId(R.id.textview_settings_activity_read_timeout)).check(matches(withText("11")));
        onView(withId(R.id.textview_settings_activity_read_timeout_seconds)).check(matches(withText("seconds")));
    }

    @Test
    public void testSwitchYesNoText() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_settings_activity_onlywifi)).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_settings_activity_notification)).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_settings_activity_onlywifi)).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_settings_activity_notification)).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.textview_settings_activity_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_settings_activity_onlywifi)).perform(click());
        onView(withId(R.id.switch_settings_activity_notification)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.DOWNLOAD, preferenceManager.getPreferenceAccessType());
        assertEquals("localhost", preferenceManager.getPreferenceAddress());
        assertEquals(80, preferenceManager.getPreferencePort());
        assertEquals(50, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
        assertEquals(2, preferenceManager.getPreferenceConnectionTimeout());
        assertEquals(2, preferenceManager.getPreferenceReadTimeout());
    }

    @Test
    public void testSetPreferencesCancel() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_settings_activity_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertEquals(15, preferenceManager.getPreferenceConnectionTimeout());
        assertEquals(10, preferenceManager.getPreferenceReadTimeout());
    }

    @Test
    public void testAddressInput() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_address)).perform(click());
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
        onView(withId(R.id.textview_settings_activity_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_settings_activity_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_address)).check(matches(withText("host.com")));
        onView(withId(R.id.textview_settings_activity_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("192.168.2.100"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_address)).check(matches(withText("192.168.2.100")));
    }

    @Test
    public void testPortInput() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_port)).perform(click());
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
        onView(withId(R.id.textview_settings_activity_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_settings_activity_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_port)).check(matches(withText("80")));
    }

    @Test
    public void testIntervalInput() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
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
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("15")));
        onView(withId(R.id.textview_settings_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("20"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("20")));
    }

    @Test
    public void testConnectionTimeoutInput() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("aa"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connection timeout"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connection timeout"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Connection timeout"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).check(matches(withText("15")));
        onView(withId(R.id.textview_settings_activity_connection_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("21"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_connection_timeout)).check(matches(withText("21")));
    }

    @Test
    public void testReadTimeoutInput() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(scrollTo());
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1 0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Read timeout"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Read timeout"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Read timeout"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).check(matches(withText("10")));
        onView(withId(R.id.textview_settings_activity_read_timeout)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_settings_activity_read_timeout)).check(matches(withText("333")));
    }
}
