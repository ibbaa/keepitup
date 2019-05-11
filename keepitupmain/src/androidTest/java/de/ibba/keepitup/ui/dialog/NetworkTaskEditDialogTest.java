package de.ibba.keepitup.ui.dialog;

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
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;

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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskEditDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
    }

    @Test
    public void testGetNetworkTaskDefaultValues() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.radiogroup_dialog_edit_network_task_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).check(matches(isNotChecked()));
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
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) activity.getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        assertNotNull(task);
        assertEquals(AccessType.CONNECT, task.getAccessType());
        assertEquals("localhost", task.getAddress());
        assertEquals(80, task.getPort());
        assertEquals(60, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        onView(withText("Ping")).perform(click());
        task = dialog.getNetworkTask();
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals("localhost", task.getAddress());
        assertEquals(60, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("http://test.com"));
        task = dialog.getNetworkTask();
        assertNotNull(task);
        assertEquals(AccessType.DOWNLOAD, task.getAccessType());
        assertEquals("http://test.com", task.getAddress());
        assertEquals(60, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
    }

    @Test
    public void testGetNetworkTaskEnteredTextPreservedOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withText("60")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withText("60")));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(withText("60")));
    }

    @Test
    public void testAccessTypePortField() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).check(matches(isDisplayed()));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).check(matches(isDisplayed()));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnOkCancelClickedDialogDismissed() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_edit_network_task_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_list_item_network_task_add)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogConnect() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
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
        onView(withId(R.id.imageview_dialog_network_task_validator_error_ok)).perform(click());
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
        onView(withId(R.id.imageview_dialog_network_task_validator_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_edit_network_task_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogDownload() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("http:/test"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("URL")).check(matches(isDisplayed()));
        onView(withText("No valid URL")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_network_task_validator_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("http://test"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("55"));
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedInputErrorColor() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
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

    @Test
    public void testOnOkCancelClickedInputErrorColorOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("https://www.xyz.com"));
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).check(matches(withTextColor(R.color.textErrorColor)));
    }
}
