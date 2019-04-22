package de.ibba.keepitup.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.AccessType;
import de.ibba.keepitup.model.LogEntry;
import de.ibba.keepitup.model.NetworkTask;
import de.ibba.keepitup.ui.adapter.NetworkTaskAdapter;
import de.ibba.keepitup.ui.adapter.NetworkTaskUIWrapper;
import de.ibba.keepitup.ui.sync.UISyncController;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Host: 127.0.0.1 Port: 80")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Notification on failure: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Last execution: successful, Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Last execution message: TestMessage")));
        onView(allOf(withId(R.id.textview_list_item_network_task_title), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Network task")));
        onView(allOf(withId(R.id.textview_list_item_network_task_status), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Status: Stopped")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Host: localhost Port: 22")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Interval: 40 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Notification on failure: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution: not executed")));
    }

    @Test
    public void testUISyncStart() throws Throwable {
        assertFalse(UISyncController.isRunning());
        launchRecyclerViewBaseActivity(rule);
        assertTrue(UISyncController.isRunning());
    }

    @Test
    public void testAddDeleteNetworkTask() {
        launchRecyclerViewBaseActivity(rule);
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
        assertEquals(4, getAdapter().getItemCount());
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
        assertEquals(1, getAdapter().getItem(1).getNetworkTask().getIndex());
        assertEquals(2, getAdapter().getItem(2).getNetworkTask().getIndex());
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_general_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(4)));
        assertEquals(4, getAdapter().getItemCount());
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
        assertEquals(1, getAdapter().getItem(1).getNetworkTask().getIndex());
        assertEquals(2, getAdapter().getItem(2).getNetworkTask().getIndex());
        onView(allOf(withId(R.id.imageview_list_item_network_task_delete), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_general_confirm_ok)).perform(click());
        onView(withId(R.id.listview_main_activity_network_tasks)).check(matches(withListSize(3)));
        assertEquals(3, getAdapter().getItemCount());
        assertEquals(0, getAdapter().getItem(0).getNetworkTask().getIndex());
        assertEquals(1, getAdapter().getItem(1).getNetworkTask().getIndex());
    }

    @Test
    public void testNetworkTaskItemText() {
        NetworkTaskMainActivity activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
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
        setTaskExecuted(activity, 1, new GregorianCalendar(1980, Calendar.MARCH, 17), true, "Success");
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution: successful, Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution message: Success")));
        setTaskExecuted(activity, 1, new GregorianCalendar(2020, Calendar.DECEMBER, 1), false, "connection failed");
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_timestamp), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution: failed, Dec 1, 2020 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_network_task_last_exec_message), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 1))).check(matches(withText("Last execution message: connection failed")));
    }

    @Test
    public void testEditNetworkTask() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("456"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_cancel)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Host: 192.168.178.1 Port: 22")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Interval: 15 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Only on WiFi: no")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Notification on failure: no")));
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_edit_network_task_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_edit_network_task_port)).perform(replaceText("456"));
        onView(withId(R.id.edittext_dialog_edit_network_task_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_edit_network_task_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_edit_network_task_notification)).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_network_task_accesstype), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Type: Ping")));
        onView(allOf(withId(R.id.textview_list_item_network_task_address), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Host: localhost Port: 456")));
        onView(allOf(withId(R.id.textview_list_item_network_task_interval), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Interval: 60 minutes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_onlywifi), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Only on WiFi: yes")));
        onView(allOf(withId(R.id.textview_list_item_network_task_notification), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withText("Notification on failure: yes")));
    }

    @Test
    public void testDisplayLog() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        NetworkTask task = getAdapter().getItem(0).getNetworkTask();
        LogEntry logEntry = getLogEntryWithNetworkTaskId(task.getId());
        getLogDAO().insertAndDeleteLog(logEntry);
        onView(allOf(withId(R.id.imageview_list_item_network_task_log), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).perform(click());
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Execution successful")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Timestamp: Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Message: TestMessage")));
    }

    @Test
    public void testStartStopNetworkTask() {
        launchRecyclerViewBaseActivity(rule);
        onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.imageview_dialog_edit_network_task_ok)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        assertFalse(getAdapter().getItem(0).getNetworkTask().isRunning());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_stop_shadow)));
        assertTrue(getAdapter().getItem(0).getNetworkTask().isRunning());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_network_task_start_stop), withChildDescendantAtPosition(withId(R.id.listview_main_activity_network_tasks), 0))).check(matches(withDrawable(R.drawable.icon_start_shadow)));
        assertFalse(getAdapter().getItem(0).getNetworkTask().isRunning());
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

    private NetworkTask getNetworkTask1() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(-1);
        networkTask.setSchedulerId(-1);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
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

