package de.ibba.keepitup.ui;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GlobalSettingsActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    @Test
    public void testDisplayDefaultValues() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
        assertFalse(preferenceManager.getPreferenceNotificationInactiveNetwork());
        onView(withId(R.id.textview_global_settings_activity_ping_count_label)).check(matches(withText("Ping count")));
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
    }

    @Test
    public void testDisplayValues() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(scrollTo());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("10"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(scrollTo());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).check(matches(withText("10")));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_label)).check(matches(withText("Notifications when network is not active")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testSwitchYesNoText() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(scrollTo());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(scrollTo());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_global_settings_activity_notification_inactive_network_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(scrollTo());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(scrollTo());
        onView(withId(R.id.switch_global_settings_activity_notification_inactive_network)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(2, preferenceManager.getPreferencePingCount());
        assertTrue(preferenceManager.getPreferenceNotificationInactiveNetwork());
    }

    @Test
    public void testSetPreferencesCancel() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(scrollTo());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertEquals(3, preferenceManager.getPreferencePingCount());
    }

    @Test
    public void testPingCountInput() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_global_settings_activity_ping_count)).perform(scrollTo());
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
}
