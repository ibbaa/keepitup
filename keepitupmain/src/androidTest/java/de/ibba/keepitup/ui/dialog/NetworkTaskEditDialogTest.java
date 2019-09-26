package de.ibba.keepitup.ui.dialog;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
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
        onView(withId(R.id.radiogroup_dialog_network_task_edit_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
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
    public void testSwitchYesNoText() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testGetNetworkTaskEnteredText() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
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
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test.com"));
        task = dialog.getNetworkTask();
        assertNotNull(task);
        assertEquals(AccessType.DOWNLOAD, task.getAccessType());
        assertEquals("http://test.com", task.getAddress());
        assertEquals(60, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
    }

    @Test
    public void testEnteredTextPreservedOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
    }

    @Test
    public void testAccessTypePortField() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnOkCancelClickedDialogDismissed() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_list_item_network_task_add)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogConnect() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(matches(isDisplayed()));
        onView(withText("Maximum: 65535")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(doesNotExist());
        onView(withText("Maximum: 65535")).check(doesNotExist());
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogDownload() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http:/test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("URL")).check(matches(isDisplayed()));
        onView(withText("No valid URL")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("55"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogNoValue() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withText("Port")).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(3, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testInputErrorColor() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
    }

    @Test
    public void testErrorColorOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://www.xyz.com"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
    }

    @Test
    public void testErrorColorOnOpenDialog() {
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withText("Download")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
    }

    @Test
    public void testNewDefaultValuesForNetworkTask() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.textview_defaults_activity_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_defaults_activity_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_defaults_activity_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_defaults_activity_onlywifi)).perform(click());
        onView(withId(R.id.switch_defaults_activity_notification)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("host.com")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("50")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testStateSavedOnScreenRotation() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withText("Download")).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testSavedStateResetOnCreate() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withText("Download")).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        pressBack();
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
    }
}
