package de.ibba.keepitup.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.test.mock.MockClipboardManager;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.dialog.SettingsInputDialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DefaultsActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<DefaultsActivity> rule = new ActivityTestRule<>(DefaultsActivity.class, false, false);

    @Test
    public void testDisplayDefaultValues() {
        launchSettingsInputActivity(rule);
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
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
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testDisplayValues() {
        launchSettingsInputActivity(rule);
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
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testMinutesAndSeconds() {
        launchSettingsInputActivity(rule);
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
    }

    @Test
    public void testSwitchYesNoText() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        launchSettingsInputActivity(rule);
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
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.DOWNLOAD, preferenceManager.getPreferenceAccessType());
        assertEquals("localhost", preferenceManager.getPreferenceAddress());
        assertEquals(80, preferenceManager.getPreferencePort());
        assertEquals(50, preferenceManager.getPreferenceInterval());
        assertTrue(preferenceManager.getPreferenceOnlyWifi());
        assertTrue(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testSetPreferencesCancel() {
        launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
    }

    @Test
    public void testAddressInput() {
        launchSettingsInputActivity(rule);
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
    }

    @Test
    public void testAddressCopyPasteOption() {
        DefaultsActivity activity = (DefaultsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        SettingsInputDialog inputDialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("data")));
    }

    @Test
    public void testAddressCopyPasteOptionScreenRotation() {
        DefaultsActivity activity = (DefaultsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        SettingsInputDialog inputDialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("data");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
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
        clipboardManager.putData("data");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("data")));
        assertTrue(clipboardManager.hasData());
        assertEquals("data", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("data")));
    }

    @Test
    public void testPortInput() {
        launchSettingsInputActivity(rule);
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
    }

    @Test
    public void testPortCopyPasteOption() {
        DefaultsActivity activity = (DefaultsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        SettingsInputDialog inputDialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("456")));
    }

    @Test
    public void testPortCopyPasteOptionScreenRotation() {
        DefaultsActivity activity = (DefaultsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("456"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("1234");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("1234");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("456")));
        assertTrue(clipboardManager.hasData());
        assertEquals("456", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("456")));
    }

    @Test
    public void testIntervalInput() {
        launchSettingsInputActivity(rule);
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
    }

    @Test
    public void testIntervalCopyPasteOption() {
        DefaultsActivity activity = (DefaultsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        SettingsInputDialog inputDialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("111");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("222"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("111")));
        assertTrue(clipboardManager.hasData());
        assertEquals("111", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("111")));
    }

    @Test
    public void testIntervalCopyPasteOptionScreenRotation() {
        DefaultsActivity activity = (DefaultsActivity) launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        SettingsInputDialog inputDialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("111");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("222"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("111");
        assertEquals(2, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity().getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("111")));
        assertTrue(clipboardManager.hasData());
        assertEquals("111", clipboardManager.getData());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("111")));
    }

    @Test
    public void testResetValues() {
        launchSettingsInputActivity(rule);
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
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
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
        onView(withId(R.id.textview_activity_defaults_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_activity_defaults_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("no")));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(AccessType.PING, preferenceManager.getPreferenceAccessType());
        assertEquals("192.168.178.1", preferenceManager.getPreferenceAddress());
        assertEquals(22, preferenceManager.getPreferencePort());
        assertEquals(15, preferenceManager.getPreferenceInterval());
        assertFalse(preferenceManager.getPreferenceOnlyWifi());
        assertFalse(preferenceManager.getPreferenceNotification());
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
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
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
        onView(withId(R.id.textview_activity_defaults_port)).check(matches(withText("80")));
        onView(withId(R.id.textview_activity_defaults_interval)).check(matches(withText("11")));
        onView(withId(R.id.textview_activity_defaults_interval_minutes)).check(matches(withText("minutes")));
        onView(withId(R.id.switch_activity_defaults_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_defaults_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_defaults_notification_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testConfirmDialogOnScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("localhost"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).check(matches(withText("localhost")));
    }

    @Test
    public void testValidationErrorScreenRotation() {
        SettingsInputActivity activity = launchSettingsInputActivity(rule);
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1a"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withText("Port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Invalid format"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
    }

    private SettingsInputDialog getDialog() {
        return (SettingsInputDialog) getActivity().getSupportFragmentManager().getFragments().get(0);
    }

    private DefaultsActivity getActivity() {
        return (DefaultsActivity) rule.getActivity();
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }
}
