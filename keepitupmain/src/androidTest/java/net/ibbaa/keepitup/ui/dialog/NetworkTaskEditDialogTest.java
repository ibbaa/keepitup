/*
 * Copyright (c) 2023. Alwin Ibba
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
import net.ibbaa.keepitup.model.NetworkTask;
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
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
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
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
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
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
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
    public void testGetInitialNetworkTask() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialNetworkTask().isEqual(dialog.getNetworkTask()));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        NetworkTask initialTask = dialog.getInitialNetworkTask();
        NetworkTask task = dialog.getNetworkTask();
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
        assertEquals(initialTask.isRunning(), task.isRunning());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
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
        assertEquals(initialTask.isRunning(), task.isRunning());
    }

    @Test
    public void testEnteredTextPreservedOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
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
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
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
    public void testOnOkCancelClickedErrorDialogConnect() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(matches(isDisplayed()));
        onView(withText("Maximum: 65535")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Port")).check(doesNotExist());
        onView(withText("Maximum: 65535")).check(doesNotExist());
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
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
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("URL")).check(matches(isDisplayed()));
        onView(withText("No valid URL")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("55"));
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
    public void testInputErrorColor() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
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
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
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
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.textview_activity_defaults_address)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("host.com"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_port)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("80"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.textview_activity_defaults_interval)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("50"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        onView(withId(R.id.switch_activity_defaults_onlywifi)).perform(click());
        onView(withId(R.id.switch_activity_defaults_notification)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
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
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("localhost"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("80"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("60"));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).perform(click());
        rotateScreen(activityScenario);
        onView(withText("Connect")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
        onView(withText("Download")).perform(click());
        rotateScreen(activityScenario);
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.switch_dialog_network_task_edit_onlywifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_onlywifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("no")));
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
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
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
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
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
        clipboardManager = prepareMockClipboardManager();
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
        MockClipboardManager clipboardManager = prepareMockClipboardManager();
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager();
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
    public void testIntervalPasteOptionScreeRotation() {
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
