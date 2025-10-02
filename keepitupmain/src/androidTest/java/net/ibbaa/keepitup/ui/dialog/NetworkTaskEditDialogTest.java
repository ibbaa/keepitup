/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.MockPermissionManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class NetworkTaskEditDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockPermissionManager permissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
        permissionManager = new MockPermissionManager();
        injectPermissionManager();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testGetNetworkTaskDefaultValues() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.radiogroup_dialog_network_task_edit_accesstype)).check(matches(hasChildCount(3)));
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(not(isDisplayed())));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        AccessTypeData data = dialog.getAccessTypeData();
        Resolve resolve = dialog.getResolve();
        assertNotNull(task);
        assertNotNull(data);
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals("192.168.178.1", task.getAddress());
        assertEquals(22, task.getPort());
        assertEquals(15, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isHighPrio());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isStopOnSuccess());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
    }

    @Test
    public void testSwitchYesNoText() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("no")));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_ignore_ssl_error_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testGetNetworkTaskEnteredText() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("9"));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        AccessTypeData data = dialog.getAccessTypeData();
        Resolve resolve = dialog.getResolve();
        assertNotNull(task);
        assertEquals(AccessType.CONNECT, task.getAccessType());
        assertEquals("localhost", task.getAddress());
        assertEquals(80, task.getPort());
        assertEquals(60, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isHighPrio());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(9, data.getConnectCount());
        assertFalse(data.isIgnoreSSLError());
        assertTrue(data.isStopOnSuccess());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        task = dialog.getNetworkTask();
        data = dialog.getAccessTypeData();
        resolve = dialog.getResolve();
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals("localhost", task.getAddress());
        assertEquals(60, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertFalse(task.isHighPrio());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertFalse(data.isIgnoreSSLError());
        assertTrue(data.isStopOnSuccess());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test.com"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("test.com"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText(""));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        task = dialog.getNetworkTask();
        data = dialog.getAccessTypeData();
        resolve = dialog.getResolve();
        assertNotNull(task);
        assertEquals(AccessType.DOWNLOAD, task.getAccessType());
        assertEquals("http://test.com", task.getAddress());
        assertEquals(60, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isHighPrio());
        assertEquals(3, data.getPingCount());
        assertEquals(56, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertTrue(data.isIgnoreSSLError());
        assertFalse(data.isStopOnSuccess());
        assertEquals("test.com", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
    }

    @Test
    public void testGetNetworkTaskAddressTrimmed() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("  localhost  "));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        assertEquals("localhost", task.getAddress());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("127.0.0.1  "));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        task = dialog.getNetworkTask();
        assertEquals("127.0.0.1", task.getAddress());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("  https://test.com  "));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("   test.com   "));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        task = dialog.getNetworkTask();
        Resolve resolve = dialog.getResolve();
        assertEquals("https://test.com", task.getAddress());
        assertEquals("test.com", resolve.getTargetAddress());
    }

    @Test
    public void testGetNetworkTaskEnteredTextPing() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("9"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("65000"));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        AccessTypeData data = dialog.getAccessTypeData();
        assertNotNull(task);
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals("localhost", task.getAddress());
        assertEquals(60, task.getInterval());
        assertTrue(task.isOnlyWifi());
        assertTrue(task.isNotification());
        assertTrue(task.isHighPrio());
        assertEquals(9, data.getPingCount());
        assertEquals(65000, data.getPingPackageSize());
        assertEquals(1, data.getConnectCount());
        assertTrue(data.isStopOnSuccess());
    }

    @Test
    public void testGetInitialNetworkTask() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialNetworkTask().isEqual(dialog.getNetworkTask()));
        assertTrue(dialog.getInitialAccessTypeData().isEqual(dialog.getAccessTypeData()));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("5"));
        NetworkTask initialTask = dialog.getInitialNetworkTask();
        NetworkTask task = dialog.getNetworkTask();
        AccessTypeData initialAccessTypeData = dialog.getInitialAccessTypeData();
        AccessTypeData data = dialog.getAccessTypeData();
        Resolve initialResolve = dialog.getInitialResolve();
        Resolve resolve = dialog.getResolve();
        assertEquals(initialTask.getId(), task.getId());
        assertEquals(initialTask.getIndex(), task.getIndex());
        assertEquals(initialTask.getSchedulerId(), task.getSchedulerId());
        assertEquals(initialTask.getInstances(), task.getInstances());
        assertNotEquals(initialTask.getAccessType(), task.getAccessType());
        assertNotEquals(initialTask.getAddress(), task.getAddress());
        assertEquals(initialTask.getPort(), task.getPort());
        assertEquals(initialTask.getInterval(), task.getInterval());
        assertEquals(initialTask.isOnlyWifi(), task.isOnlyWifi());
        assertEquals(initialTask.isNotification(), task.isNotification());
        assertEquals(initialTask.isHighPrio(), task.isHighPrio());
        assertEquals(initialTask.isRunning(), task.isRunning());
        assertEquals(initialAccessTypeData.getPingCount(), data.getPingCount());
        assertEquals(initialAccessTypeData.getPingPackageSize(), data.getPingPackageSize());
        assertNotEquals(initialAccessTypeData.getConnectCount(), data.getConnectCount());
        assertEquals(initialAccessTypeData.isIgnoreSSLError(), data.isIgnoreSSLError());
        assertEquals(initialAccessTypeData.isStopOnSuccess(), data.isStopOnSuccess());
        assertEquals(initialResolve.getTargetAddress(), resolve.getTargetAddress());
        assertEquals(initialResolve.getTargetPort(), resolve.getTargetPort());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        initialTask = dialog.getInitialNetworkTask();
        task = dialog.getNetworkTask();
        assertEquals(initialTask.getId(), task.getId());
        assertEquals(initialTask.getIndex(), task.getIndex());
        assertEquals(initialTask.getSchedulerId(), task.getSchedulerId());
        assertEquals(initialTask.getInstances(), task.getInstances());
        assertNotEquals(initialTask.getAccessType(), task.getAccessType());
        assertNotEquals(initialTask.getAddress(), task.getAddress());
        assertNotEquals(initialTask.getPort(), task.getPort());
        assertNotEquals(initialTask.getInterval(), task.getInterval());
        assertNotEquals(initialTask.isOnlyWifi(), task.isOnlyWifi());
        assertNotEquals(initialTask.isNotification(), task.isNotification());
        assertEquals(initialTask.isHighPrio(), task.isHighPrio());
        assertEquals(initialTask.isRunning(), task.isRunning());
        assertEquals(initialAccessTypeData.getPingCount(), data.getPingCount());
        assertEquals(initialAccessTypeData.getPingPackageSize(), data.getPingPackageSize());
        assertEquals(initialAccessTypeData.isIgnoreSSLError(), data.isIgnoreSSLError());
        assertNotEquals(initialAccessTypeData.getConnectCount(), data.getConnectCount());
        assertEquals(initialAccessTypeData.isStopOnSuccess(), data.isStopOnSuccess());
        assertEquals(initialResolve.getTargetAddress(), resolve.getTargetAddress());
        assertEquals(initialResolve.getTargetPort(), resolve.getTargetPort());
    }

    @Test
    public void testEnteredTextPreservedOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("8"));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("56")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("8"));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("not set")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("8"));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("not set")));
    }

    @Test
    public void testHighPrioPreservedOnNotificationChange() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testAccessTypePortAndAccessTypeDataFields() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ping_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ignore_ssl_error)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ignore_ssl_error)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ping_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ignore_ssl_error)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_stop_on_success)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testResolveFieldsFields() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(not(isDisplayed())));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnOkCancelClickedDialogDismissed() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_activity_main_network_task_add)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogPing() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("11"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("65528"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withText("Ping count")).check(matches(isDisplayed()));
        onView(withText("Maximum: 10")).check(matches(isDisplayed()));
        onView(withText("Package size")).check(matches(isDisplayed()));
        onView(withText("Maximum: 65527")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("25"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(doesNotExist());
        onView(withText("Maximum: 65535")).check(doesNotExist());
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withText("Ping count")).check(matches(isDisplayed()));
        onView(withText("Maximum: 10")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogConnect() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("11"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(matches(isDisplayed()));
        onView(withText("Maximum: 65535")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withText("Connect count")).check(matches(isDisplayed()));
        onView(withText("Maximum: 10")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("25"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(doesNotExist());
        onView(withText("Maximum: 65535")).check(doesNotExist());
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withText("Connect count")).check(matches(isDisplayed()));
        onView(withText("Maximum: 10")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogDownload() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http:/test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("my host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("12345678"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("URL")).check(matches(isDisplayed()));
        onView(withText("No valid URL")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withText("Connect-to host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Connect-to port")).check(matches(isDisplayed()));
        onView(withText("Maximum: 65535")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("55"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("myhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("12345"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testOnOkCancelClickedErrorDialogNoValue() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withText("Port")).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(3, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testResolveFieldsNoValueAllowed() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://address"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("not set"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testInputErrorColor() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("11"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("10"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textColor)));
    }

    @Test
    public void testInputErrorColorPingFields() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("11"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("9"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("11111"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withTextColor(R.color.textColor)));
    }

    @Test
    public void testInputErrorColorResolveFields() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("my host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("myhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("11111"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
    }

    @Test
    public void testErrorColorOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withTextColor(R.color.textColor)));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://www.xyz.com"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("ab"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("ab"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("ab"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("ab"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("ab"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withTextColor(R.color.textErrorColor)));
    }

    @Test
    public void testErrorColorOnOpenDialog() {
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withText("Download")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
    }

    @Test
    public void testNewDefaultValuesForNetworkTask() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("192.168.178.1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("15")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("2"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_ping_package_size)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("1234"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_count)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("5"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("22"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_stop_on_success)).perform(click());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_only_wifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(scrollTo());
        onView(withId(R.id.switch_activity_defaults_high_prio)).perform(click());
        onView(withText("Connect")).perform(scrollTo());
        onView(withText("Connect")).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("host.com")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("50")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("5")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("host.com")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("50")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("2")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("1234")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("host.com")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("50")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("22")));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testStateSavedOnScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("2"));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        rotateScreen(activityScenario);
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("2")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("22"));
        rotateScreen(activityScenario);
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("22")));
        onView(withId(R.id.switch_dialog_network_task_edit_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("3")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("56")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("1"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("1"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("1")));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("1")));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_stop_on_success_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testHighPrioStateSavedOnScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testSavedStateResetOnCreate() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
        onView(withText("Download")).perform(click());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        pressBack();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Ping")).check(matches(isChecked()));
    }

    @Test
    public void testAddressCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testAddressCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testAddressCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("test")));
    }

    @Test
    public void testAddressCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("test", clipboardManager.getData());
    }

    @Test
    public void testAddressCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("test", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testAddressPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("abc")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testAddressPasteOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("abc");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("abc")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testPortCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testPortCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("11");
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("25"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("25")));
        assertTrue(clipboardManager.hasData());
        assertEquals("11", clipboardManager.getData());
    }

    @Test
    public void testPortCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("11");
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("25"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("25")));
    }

    @Test
    public void testPortCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
    }

    @Test
    public void testPortCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        prepareMockClipboardManager();
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
    }

    @Test
    public void testPortPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("67");
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("67")));
        assertTrue(clipboardManager.hasData());
        assertEquals("67", clipboardManager.getData());
    }

    @Test
    public void testPortPasteOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("67");
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("67");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("67")));
        assertTrue(clipboardManager.hasData());
        assertEquals("67", clipboardManager.getData());
    }

    @Test
    public void testIntervalCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testIntervalCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("25"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("25")));
        assertTrue(clipboardManager.hasData());
        assertEquals("11", clipboardManager.getData());
    }

    @Test
    public void testIntervalCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("25"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("25")));
    }

    @Test
    public void testIntervalCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
    }

    @Test
    public void testIntervalCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        prepareMockClipboardManager();
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testIntervalPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("67");
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("67")));
        assertTrue(clipboardManager.hasData());
        assertEquals("67", clipboardManager.getData());
    }

    @Test
    public void testIntervalPasteOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("67");
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("67");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("67")));
        assertTrue(clipboardManager.hasData());
        assertEquals("67", clipboardManager.getData());
    }

    @Test
    public void testPingCountCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testPingCountCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("5")));
        assertTrue(clipboardManager.hasData());
        assertEquals("11", clipboardManager.getData());
    }

    @Test
    public void testPingCountCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("10"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("10")));
    }

    @Test
    public void testPingCountCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("9"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("9")));
        assertTrue(clipboardManager.hasData());
        assertEquals("9", clipboardManager.getData());
    }

    @Test
    public void testPingCountCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText("1"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("1")));
        assertTrue(clipboardManager.hasData());
        assertEquals("1", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testPingCountPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("1");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("1")));
        assertTrue(clipboardManager.hasData());
        assertEquals("1", clipboardManager.getData());
    }

    @Test
    public void testPingCountPasteOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("2");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("2");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(withText("2")));
        assertTrue(clipboardManager.hasData());
        assertEquals("2", clipboardManager.getData());
    }

    @Test
    public void testPingPackageSizeCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testPingPackageSizeCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("123");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("55"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("55")));
        assertTrue(clipboardManager.hasData());
        assertEquals("123", clipboardManager.getData());
    }

    @Test
    public void testPingPackageSizeCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("123");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("1234"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("1234")));
    }

    @Test
    public void testPingPackageSizeCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("568"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("568")));
        assertTrue(clipboardManager.hasData());
        assertEquals("568", clipboardManager.getData());
    }

    @Test
    public void testPingPackageSizeCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText("1"));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("1")));
        assertTrue(clipboardManager.hasData());
        assertEquals("1", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testPingPackageSizePasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("0");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("0")));
        assertTrue(clipboardManager.hasData());
        assertEquals("0", clipboardManager.getData());
    }

    @Test
    public void testPingPackageSizeOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("2");
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("2");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(withText("2")));
        assertTrue(clipboardManager.hasData());
        assertEquals("2", clipboardManager.getData());
    }

    @Test
    public void testConnectCountCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testConnectCountCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("1");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("5")));
        assertTrue(clipboardManager.hasData());
        assertEquals("1", clipboardManager.getData());
    }

    @Test
    public void testConnectCountCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("9");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("2"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("2")));
    }

    @Test
    public void testConnectCountCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("4"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("4")));
        assertTrue(clipboardManager.hasData());
        assertEquals("4", clipboardManager.getData());
    }

    @Test
    public void testConnectCountCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("1"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("1")));
        assertTrue(clipboardManager.hasData());
        assertEquals("1", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testConnectCountPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("3");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("3")));
        assertTrue(clipboardManager.hasData());
        assertEquals("3", clipboardManager.getData());
    }

    @Test
    public void testConnectCountOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("2");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("2");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("2")));
        assertTrue(clipboardManager.hasData());
        assertEquals("2", clipboardManager.getData());
    }

    @Test
    public void testConnectToHostCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("123");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        assertTrue(clipboardManager.hasData());
        assertEquals("123", clipboardManager.getData());
    }

    @Test
    public void testConnectToHostCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("9");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
    }

    @Test
    public void testConnectToHostCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        assertTrue(clipboardManager.hasData());
        assertEquals("host", clipboardManager.getData());
    }

    @Test
    public void testConnectToHostCopyNotSet() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("not set")));
        assertTrue(clipboardManager.hasData());
        assertEquals("not set", clipboardManager.getData());
    }

    @Test
    public void testConnectToHostCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText("host"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        assertTrue(clipboardManager.hasData());
        assertEquals("host", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testConnectToHostPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("host");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        assertTrue(clipboardManager.hasData());
        assertEquals("host", clipboardManager.getData());
    }

    @Test
    public void testConnectToHostOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("host");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("host");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_host)).check(matches(withText("host")));
        assertTrue(clipboardManager.hasData());
        assertEquals("host", clipboardManager.getData());
    }

    @Test
    public void testConnectToPortCopyPasteNoOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testConnectToPortCopyPasteCancel() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("1");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("5"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("5")));
        assertTrue(clipboardManager.hasData());
        assertEquals("1", clipboardManager.getData());
    }

    @Test
    public void testConnectToPortCopyPasteCancelScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("22");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("2"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("2")));
    }

    @Test
    public void testConnectToPortCopyOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("22"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("22")));
        assertTrue(clipboardManager.hasData());
        assertEquals("22", clipboardManager.getData());
    }

    @Test
    public void testConnectToPortCopyNotSet() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("not set")));
        assertTrue(clipboardManager.hasData());
        assertEquals("not set", clipboardManager.getData());
    }

    @Test
    public void testConnectToPortCopyOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("80")));
        assertTrue(clipboardManager.hasData());
        assertEquals("80", clipboardManager.getData());
        rotateScreen(activityScenario);
    }

    @Test
    public void testConnectToPortPasteOption() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("3");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("3")));
        assertTrue(clipboardManager.hasData());
        assertEquals("3", clipboardManager.getData());
    }

    @Test
    public void testConnectToPortOptionScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("21");
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
        clipboardManager.putData("21");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_to_port)).check(matches(withText("21")));
        assertTrue(clipboardManager.hasData());
        assertEquals("21", clipboardManager.getData());
    }

    @Test
    public void testNotificationWithPermission() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testNotificationWithoutPermission() {
        permissionManager.setHasPostNotificationsPermission(false);
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotEnabled()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
    }

    private MockClipboardManager prepareMockClipboardManager() {
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        dialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private void injectPermissionManager() {
        ((NetworkTaskMainActivity) getActivity(activityScenario)).injectPermissionManager(permissionManager);
    }
}
