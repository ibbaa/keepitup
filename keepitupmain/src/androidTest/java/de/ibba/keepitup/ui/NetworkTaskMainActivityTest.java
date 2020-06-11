package de.ibba.keepitup.ui;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    @Test
    public void testInitializeActivity() {
        NetworkTask task1 = getNetworkTask1();
        task1 = getNetworkTaskDAO().insertNetworkTask(task1);
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task1.getId());
        getLogDAO().insertAndDeleteLog(logEntry);
        NetworkTask task2 = getNetworkTask2();
        getNetworkTaskDAO().insertNetworkTask(task2);
        launchRecyclerViewBaseActivity(rule);
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Instances: 0 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Type: Connect")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Host: 127.0.0.1 Port: 80")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Notification on failure: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Last execution: successful, Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Last execution message: TestMessage")));
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Instances: 0 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Host: localhost")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Interval: 40 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Notification on failure: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Last execution: not executed")));
    }

    @Test
    public void testAddDeleteNetworkTask() {
        launchRecyclerViewBaseActivity(rule);
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(3)));
        assertEquals(3, getAdapter().getItemCount());
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
        assertEquals(1, getAdapter().getItem(1).getNetworkTask().getIndex());
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(3)));
        assertEquals(3, getAdapter().getItemCount());
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
        assertEquals(1, getAdapter().getItem(1).getNetworkTask().getIndex());
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(2)));
        assertEquals(2, getAdapter().getItemCount());
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
    }

    @Test
    public void testNetworkTaskItemText() {
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Instances: 0 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Host: 192.168.178.1")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Notification on failure: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Last execution: not executed")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        setTaskInstances(activity, 1, 1);
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Instances: 1 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Type: Connect")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Host: localhost Port: 80")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Interval: 60 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Notification on failure: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Last execution: not executed")));
        setTaskExecuted(activity, 1, new GregorianCalendar(1980, Calendar.MARCH, 17), true, "Success");
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Last execution: successful, Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Last execution message: Success")));
        setTaskExecuted(activity, 1, new GregorianCalendar(2020, Calendar.DECEMBER, 1), false, "connection failed");
        setTaskInstances(activity, 1, 2);
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Instances: 2 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Last execution: failed, Dec 1, 2020 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 1))).check(matches(withText("Last execution message: connection failed")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Instances: 0 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Type: Download")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("URL: http://test")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Interval: 60 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Notification on failure: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 2))).check(matches(withText("Last execution: not executed")));
    }

    @Test
    public void testEditNetworkTask() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Host: 192.168.178.1")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Notification on failure: no")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Host: localhost")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Interval: 60 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Notification on failure: yes")));
    }

    @Test
    public void testEditNetworkTaskValueChanged() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        NetworkTask taskBefore = getNetworkTaskDAO().readAllNetworkTasks().get(0);
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        NetworkTask taskAfter = getNetworkTaskDAO().readAllNetworkTasks().get(0);
        assertEquals(taskBefore.getAccessType(), taskAfter.getAccessType());
        assertNotEquals(taskBefore.getAddress(), taskAfter.getAddress());
        assertEquals(taskBefore.getPort(), taskAfter.getPort());
        assertEquals(taskBefore.getInterval(), taskAfter.getInterval());
        assertEquals(taskBefore.isNotification(), taskAfter.isNotification());
        assertEquals(taskBefore.isOnlyWifi(), taskAfter.isOnlyWifi());
        assertNotEquals(taskBefore.getSchedulerId(), taskAfter.getSchedulerId());
    }

    @Test
    public void testEditNetworkTaskValueNotChanged() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        NetworkTask taskBefore = getNetworkTaskDAO().readAllNetworkTasks().get(0);
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        NetworkTask taskAfter = getNetworkTaskDAO().readAllNetworkTasks().get(0);
        assertTrue(taskBefore.isEqual(taskAfter));
    }

    @Test
    public void testDisplayLog() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        NetworkTask task = getAdapter().getItem(0).getNetworkTask();
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task.getId());
        getLogDAO().insertAndDeleteLog(logEntry);
        onView(allOf(withId(R.id.imageview_list_item_network_task_log), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Execution successful")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Timestamp: Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Message: TestMessage")));
    }

    @Test
    public void testStartStopNetworkTask() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        assertFalse(getAdapter().getItem(0).getNetworkTask().isRunning());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_stop_shadow)));
        assertTrue(getAdapter().getItem(0).getNetworkTask().isRunning());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        assertFalse(getAdapter().getItem(0).getNetworkTask().isRunning());
    }

    @Test
    public void testMenuOptions() {
        launchRecyclerViewBaseActivity(rule);
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withId(R.id.textview_activity_defaults_defaults_label)).check(matches(withText("Defaults")));
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.textview_dialog_info_thirdparty)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Settings")).perform(click());
        onView(withId(R.id.textview_activity_global_settings_global_label)).check(matches(withText("Global settings")));
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.textview_activity_global_settings_global_label)).check(doesNotExist());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Info")).perform(click());
        onView(withId(R.id.textview_dialog_info_title)).check(matches(withText("Keep it up")));
        onView(withId(R.id.imageview_dialog_info_ok)).perform(click());
        onView(withId(R.id.textview_dialog_info_title)).check(doesNotExist());
    }

    @Test
    public void testAddNetworkTaskScreenRotation() {
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Instances: 0 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Type: Connect")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Host: 192.168.178.1 Port: 22")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Notification on failure: yes")));
    }

    @Test
    public void testDeleteNetworkTaskScreenRotation() {
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.listview_activity_main_network_tasks)).check(matches(withListSize(1)));
    }

    @Test
    public void testEditNetworkTaskScreenRotation() {
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_instances), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Instances: 0 active")));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Host: 192.168.178.1")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Interval: 60 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).check(matches(withText("Notification on failure: no")));
    }

    private void setTaskExecuted(NetworkTaskMainActivity activity, int position, Calendar calendar, boolean success, String message) {
        NetworkTask task = getAdapter().getItem(position).getNetworkTask();
        LogEntry logEntry = new LogEntry();
        logEntry.setNetworkTaskId(task.getId());
        logEntry.setSuccess(success);
        logEntry.setTimestamp(calendar.getTime().getTime());
        logEntry.setMessage(message);
        getAdapter().replaceItem(new NetworkTaskUIWrapper(task, logEntry));
        activity.runOnUiThread(() -> getAdapter().notifyDataSetChanged());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    private void setTaskInstances(NetworkTaskMainActivity activity, int position, int instances) {
        NetworkTaskUIWrapper wrapper = getAdapter().getItem(position);
        NetworkTask task = wrapper.getNetworkTask();
        LogEntry logEntry = wrapper.getLogEntry();
        task.setInstances(instances);
        getAdapter().replaceItem(new NetworkTaskUIWrapper(task, logEntry));
        activity.runOnUiThread(() -> getAdapter().notifyDataSetChanged());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.CONNECT);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        return networkTask;
    }

    private NetworkTask getNetworkTask2() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("localhost");
        networkTask.setPort(22);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(40);
        networkTask.setOnlyWifi(true);
        networkTask.setNotification(false);
        networkTask.setRunning(false);
        return networkTask;
    }

    private LogEntry getLogEntryWithNetworkTaskId(long networkTaskId) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(networkTaskId);
        logEntry.setSuccess(true);
        logEntry.setTimestamp(new GregorianCalendar(1980, Calendar.MARCH, 17).getTime().getTime());
        logEntry.setMessage("TestMessage");
        return logEntry;
    }

    private NetworkTaskAdapter getAdapter() {
        NetworkTaskMainActivity activity = rule.getActivity();
        return (NetworkTaskAdapter) activity.getAdapter();
    }
}

