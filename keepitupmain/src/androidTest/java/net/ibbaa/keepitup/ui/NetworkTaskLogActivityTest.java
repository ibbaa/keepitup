/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.LogEntry;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskLogActivityTest extends BaseUITest {

    @Test
    public void testInitializeActivityNoData() {
        NetworkTask task = insertNetworkTask();
        ActivityScenario<?> activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskLogIntent(task));
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("No logs present for network task 1")));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(not(isDisplayed())));
        activityScenario.close();
    }

    @Test
    public void testInitializeActivity() {
        NetworkTask task = insertNetworkTask();
        LogEntry entry1 = getLogEntry(task, new GregorianCalendar(1980, Calendar.MARCH, 17), false, "Message1");
        LogEntry entry2 = getLogEntry(task, new GregorianCalendar(1985, Calendar.DECEMBER, 24), true, "Message2");
        getLogDAO().insertAndDeleteLog(entry1);
        getLogDAO().insertAndDeleteLog(entry2);
        ActivityScenario<?> activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskLogIntent(task));
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Execution successful")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Timestamp: Dec 24, 1985 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Message: Message2")));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Execution failed")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Timestamp: Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Message: Message1")));
        activityScenario.close();
    }

    @Test
    public void testDeleteLogs() {
        NetworkTask task = insertNetworkTask();
        LogEntry entry1 = getLogEntry(task, new GregorianCalendar(1980, Calendar.MARCH, 17), false, "Message1");
        LogEntry entry2 = getLogEntry(task, new GregorianCalendar(1985, Calendar.DECEMBER, 24), true, "Message2");
        getLogDAO().insertAndDeleteLog(entry1);
        getLogDAO().insertAndDeleteLog(entry2);
        ActivityScenario<?> activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskLogIntent(task));
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(2)));
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Delete logs")).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(2)));
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Delete logs")).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(1)));
        onView(withOverflowButton()).check(doesNotExist());
        activityScenario.close();
    }

    @Test
    public void testScreenRotation() {
        NetworkTask task = insertNetworkTask();
        LogEntry entry1 = getLogEntry(task, new GregorianCalendar(1980, Calendar.MARCH, 17), false, "Message1");
        LogEntry entry2 = getLogEntry(task, new GregorianCalendar(1985, Calendar.DECEMBER, 24), true, "Message2");
        getLogDAO().insertAndDeleteLog(entry1);
        getLogDAO().insertAndDeleteLog(entry2);
        ActivityScenario<?> activityScenario = launchRecyclerViewBaseActivity(getNetworkTaskLogIntent(task));
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(2)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_log_entry_no_log), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Execution successful")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Timestamp: Dec 24, 1985 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 0))).check(matches(withText("Message: Message2")));
        onView(allOf(withId(R.id.cardview_list_item_log_entry), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_log_entry_title), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Log entry for network task 1")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_success), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Execution failed")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_timestamp), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Timestamp: Mar 17, 1980 12:00:00 AM")));
        onView(allOf(withId(R.id.textview_list_item_log_entry_message), withChildDescendantAtPosition(withId(R.id.listview_activity_log_log_entries), 1))).check(matches(withText("Message: Message1")));
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Delete logs")).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_activity_log_log_entries)).check(matches(withListSize(1)));
        onView(withOverflowButton()).check(doesNotExist());
        activityScenario.close();
    }

    private NetworkTask insertNetworkTask() {
        NetworkTask task = getNetworkTask();
        return getNetworkTaskDAO().insertNetworkTask(task);
    }

    private Intent getNetworkTaskLogIntent(NetworkTask task) {
        Intent intent = new Intent(TestRegistry.getContext(), NetworkTaskLogActivity.class);
        intent.putExtras(task.toBundle());
        return intent;
    }

    private NetworkTask getNetworkTask() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.setId(-1);
        networkTask.setIndex(0);
        networkTask.setSchedulerId(-1);
        networkTask.setInstances(0);
        networkTask.setAddress("127.0.0.1");
        networkTask.setPort(80);
        networkTask.setAccessType(AccessType.PING);
        networkTask.setInterval(15);
        networkTask.setOnlyWifi(false);
        networkTask.setNotification(true);
        networkTask.setRunning(false);
        networkTask.setLastScheduled(1);
        networkTask.setFailureCount(2);
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

