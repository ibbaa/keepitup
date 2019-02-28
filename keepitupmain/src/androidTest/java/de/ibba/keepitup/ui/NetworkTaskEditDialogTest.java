package de.ibba.keepitup.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskEditDialogTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class);

    private NetworkTaskMainActivity activity;

    @Before
    @UiThreadTest
    public void beforeEachTestMethod() {
        activity = rule.getActivity();
    }

    @Test
    public void testGetNetworkTaskDefaultValues() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withId(R.id.radiogroup_dialog_edit_network_task_accesstype)).check(matches(hasChildCount(2)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withText("22")));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).check(matches(isNotChecked()));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) activity.getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        assertNotNull(task);
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals("192.168.178.1", task.getAddress());
        assertEquals(22, task.getPort());
        assertEquals(15, task.getInterval());
        assertFalse(task.isNotification());
    }

    @Test
    public void testGetNetworkTaskEnteredText() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) activity.getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        assertNotNull(task);
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals("localhost", task.getAddress());
        assertEquals(80, task.getPort());
        assertEquals(60, task.getInterval());
        assertTrue(task.isNotification());
        onView(withText("Connect")).perform(click());
        task = dialog.getNetworkTask();
        assertEquals(AccessType.CONNECT, task.getAccessType());
    }

    @Test
    public void testAccessTypePortField() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).check(matches(isDisplayed()));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnOkCancelClickedDialogDismissed() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_edit_network_task_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
        addNetworkTaskImageView.perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialog() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(matches(isDisplayed()));
        onView(withText("Maximum: 65535")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_edit_network_task_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(doesNotExist());
        onView(withText("Maximum: 65535")).check(doesNotExist());
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_edit_network_task_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_edit_network_task_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedInputErrorColor() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withTextColor(R.color.textColor)));
    }

    public static Matcher<View> withTextColor(final int expectedId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            protected boolean matchesSafely(TextView textView) {
                int colorId = ContextCompat.getColor(textView.getContext(), expectedId);
                return textView.getCurrentTextColor() == colorId;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                description.appendValue(expectedId);
            }
        };
    }
}
