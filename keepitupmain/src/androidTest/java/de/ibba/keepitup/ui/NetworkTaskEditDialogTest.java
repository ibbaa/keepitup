package de.ibba.keepitup.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
    public void testAddNetworkTaskDefaultValues() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withId(R.id.radiogroup_dialog_edit_network_task_accesstype)).check(matches(hasChildCount(1)));
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
    public void testAddNetworkTaskEnteredText() {
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
    }
}
