package de.ibba.keepitup.ui;

import android.content.Intent;
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
import de.ibba.keepitup.ui.adapter.LogEntryAdapter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskLogActivityTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskLogActivity> rule = new ActivityTestRule<>(NetworkTaskLogActivity.class, false, false);

    @Test
    public void testInitializeActivityNoData() {
        NetworkTask task = insertNetworkTask();
        rule.launchActivity(getNetworkTaskIntent(task));
        onView(withId(R.id.listview_log_activity_log_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("No logs present for network task 1")));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(not(isDisplayed())));
    }

    @Test
    public void testInitializeActivity() {
        NetworkTask task = insertNetworkTask();
        LogEntry entry1 = getLogEntry(task, new GregorianCalendar(1980, Calendar.MARCH, 17), false, "Message1");
        LogEntry entry2 = getLogEntry(task, new GregorianCalendar(1985, Calendar.DECEMBER, 24), true, "Message2");
        getLogDAO().insertAndDeleteLog(entry1);
        getLogDAO().insertAndDeleteLog(entry2);
        rule.launchActivity(getNetworkTaskIntent(task));
        onView(withId(R.id.listview_log_activity_log_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Execution successful")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Timestamp: Dec 24, 1985 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Message: Message2")));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 1))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 1))).check(matches(withText("Execution failed")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 1))).check(matches(withText("Timestamp: Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 1))).check(matches(withText("Message: Message1")));
    }

    @Test
    public void testRefresh() {
        NetworkTask task = insertNetworkTask();
        LogEntry entry1 = getLogEntry(task, new GregorianCalendar(1980, Calendar.MARCH, 17), false, "Message1");
        LogEntry entry2 = getLogEntry(task, new GregorianCalendar(1985, Calendar.DECEMBER, 24), true, "Message2");
        getLogDAO().insertAndDeleteLog(entry1);
        getLogDAO().insertAndDeleteLog(entry2);
        rule.launchActivity(getNetworkTaskIntent(task));
        onView(withId(R.id.listview_log_activity_log_entries)).check(matches(withListSize(2)));
        LogEntry entry3 = getLogEntry(task, new GregorianCalendar(2016, Calendar.JULY, 1), true, "Message3");
        getLogDAO().insertAndDeleteLog(entry3);
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Refresh")).perform(click());
        onView(withId(R.id.listview_log_activity_log_entries)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Execution successful")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Timestamp: Jul 1, 2016 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_log_activity_log_entries), 0))).check(matches(withText("Message: Message3")));
    }

    private LogEntryAdapter getAdapter() {
        NetworkTaskLogActivity activity = rule.getActivity();
        return (LogEntryAdapter) activity.getAdapter();
    }

    private NetworkTask insertNetworkTask() {
        NetworkTask task = getNetworkTask();
        return getNetworkTaskDAO().insertNetworkTask(task);
    }

    private Intent getNetworkTaskIntent(NetworkTask task) {
        Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), NetworkTaskLogActivity.class);
        intent.putExtras(task.toBundle());
        return intent;
    }

    private NetworkTask getNetworkTask() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(0);
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

    private LogEntry getLogEntry(NetworkTask task, Calendar calendar, boolean success, String message) {
        LogEntry logEntry = new LogEntry();
        logEntry.setId(0);
        logEntry.setNetworkTaskId(task.getId());
        logEntry.setSuccess(success);
        logEntry.setTimestamp(calendar.getTime().getTime());
        logEntry.setMessage(message);
        return logEntry;
    }
}

