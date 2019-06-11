package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    @Test
    public void testDefaultValues() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_settings_activity_accesstype_label)).check(matches(withText("Type")));
        onView(withId(R.id.radiogroup_settings_activity_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.textview_settings_activity_address_label)).check(matches(withText("Host / URL")));
        onView(withId(R.id.textview_settings_activity_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.textview_settings_activity_port_label)).check(matches(withText("Port")));
        onView(withId(R.id.textview_settings_activity_port)).check(matches(withText("22")));
        onView(withId(R.id.textview_settings_activity_interval_label)).check(matches(withText("Interval")));
        onView(withId(R.id.textview_settings_activity_interval)).check(matches(withText("15")));
        onView(withId(R.id.textview_settings_activity_onlywifi_label)).check(matches(withText("Only on WiFi")));
        onView(withId(R.id.switch_settings_activity_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_settings_activity_notification_label)).check(matches(withText("Notifications")));
        onView(withId(R.id.switch_settings_activity_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_settings_activity_notification_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSwitchYesNoText() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
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
}
