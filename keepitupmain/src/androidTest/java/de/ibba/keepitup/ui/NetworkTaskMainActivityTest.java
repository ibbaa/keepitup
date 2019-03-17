package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.R;
import de.ibba.keepitup.db.NetworkTaskDAO;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.service.NetworkKeepAliveServiceScheduler;
import de.ibba.keepitup.test.matcher.ChildDescendantAtPositionMatcher;
import de.ibba.keepitup.test.matcher.DrawableMatcher;
import de.ibba.keepitup.test.matcher.ListSizeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskMainActivityTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;
    private NetworkTaskDAO dao;
    private NetworkKeepAliveServiceScheduler scheduler;

    @Before
    public void beforeEachTestMethod() {
        dao = new NetworkTaskDAO(InstrumentationRegistry.getTargetContext());
        dao.deleteAllNetworkTasks();
        scheduler = new NetworkKeepAliveServiceScheduler(InstrumentationRegistry.getTargetContext());
        scheduler.stopAll();
        rule.launchActivity(null);
        activity = rule.getActivity();
    }

    @After
    public void afterEachTestMethod() {
        dao.deleteAllNetworkTasks();
        scheduler.stopAll();
    }

    @Test
    public void testAddDeleteNetworkTask() {
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(4)));
        assertEquals(4, activity.getAdapter().getItemCount());
        assertEquals(0, activity.getAdapter().getItem(0).getIndex());
        assertEquals(1, activity.getAdapter().getItem(1).getIndex());
        assertEquals(2, activity.getAdapter().getItem(2).getIndex());
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_general_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(4)));
        assertEquals(4, activity.getAdapter().getItemCount());
        assertEquals(0, activity.getAdapter().getItem(0).getIndex());
        assertEquals(1, activity.getAdapter().getItem(1).getIndex());
        assertEquals(2, activity.getAdapter().getItem(2).getIndex());
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_general_confirm_ok)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(3)));
        assertEquals(3, activity.getAdapter().getItemCount());
        assertEquals(0, activity.getAdapter().getItem(0).getIndex());
        assertEquals(1, activity.getAdapter().getItem(1).getIndex());
    }

    @Test
    public void testNetworkTaskItemText() {
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Host: 192.168.178.1 Port: 22")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Notification on failure: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Last execution: not executed")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Type: Connect")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Host: localhost")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Interval: 60 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Notification on failure: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution: not executed")));
        setTaskExecuted(1, new GregorianCalendar(1980, Calendar.MARCH, 17), true, "Success");
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution: successful, Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution message: Success")));
        setTaskExecuted(1, new GregorianCalendar(2020, Calendar.DECEMBER, 1), false, "connection failed");
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution: failed, Dec 1, 2020 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution message: connection failed")));
    }

    private void setTaskExecuted(int position, Calendar calendar, boolean success, String message) {
        NetworkTask task = activity.getAdapter().getItem(position);
        task.setSuccess(success);
        task.setTimestamp(calendar.getTime().getTime());
        task.setMessage(message);
        activity.runOnUiThread(() -> activity.getAdapter().notifyDataSetChanged());
    }

    public static Matcher<View> withListSize(final int size) {
        return new ListSizeMatcher(size);
    }

    public static Matcher<View> withChildDescendantAtPosition(final Matcher<View> parentMatcher, final int childPosition) {
        return new ChildDescendantAtPositionMatcher(parentMatcher, childPosition);
    }

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }
}

