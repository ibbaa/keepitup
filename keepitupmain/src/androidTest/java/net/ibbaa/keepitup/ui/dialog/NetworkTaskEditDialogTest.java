/*
 * Copyright (c) 2026 Alwin Ibba
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPAuthInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.MockPermissionManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestSNMPInterfacesDialog;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.sync.HeaderSyncHandler;
import net.ibbaa.keepitup.ui.sync.SNMPScanResult;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class NetworkTaskEditDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockPermissionManager permissionManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
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
        onView(withId(R.id.radiogroup_dialog_network_task_edit_accesstype)).check(matches(hasChildCount(4)));
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
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
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
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_use_default_headers_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_use_default_headers_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_use_default_headers_on_off)).check(matches(withText("yes")));
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
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        task = dialog.getNetworkTask();
        data = dialog.getAccessTypeData();
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
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("http://test.com"));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).perform(click());
        task = dialog.getNetworkTask();
        data = dialog.getAccessTypeData();
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
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v1)).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(replaceText("testcommunity"));
        task = dialog.getNetworkTask();
        data = dialog.getAccessTypeData();
        assertNotNull(task);
        assertEquals(AccessType.SNMP, task.getAccessType());
        assertEquals("http://test.com", task.getAddress());
        assertEquals(60, task.getInterval());
        assertFalse(task.isOnlyWifi());
        assertFalse(task.isNotification());
        assertFalse(task.isHighPrio());
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals("testcommunity", data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertFalse(data.isStopOnSuccess());
        onView(withText("Ping")).perform(click());
        task = dialog.getNetworkTask();
        data = dialog.getAccessTypeData();
        assertEquals(AccessType.PING, task.getAccessType());
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
    }

    @Test
    public void testHiddenNetworkTaskPortNotReturnedAfterChange() {
        activityScenario.close();
        NetworkTask task = new NetworkTask();
        task.setIndex(0);
        task.setAddress("localhost");
        task.setAccessType(AccessType.CONNECT);
        task.setPort(80);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setNetworkTaskId(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(accessTypeData);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("443"));
        onView(withText("Download")).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask resultTask = dialog.getNetworkTask();
        assertEquals(AccessType.DOWNLOAD, resultTask.getAccessType());
        assertEquals(80, resultTask.getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testHiddenAccessTypeDataFieldsNotReturnedAfterChange() {
        activityScenario.close();
        NetworkTask task = new NetworkTask();
        task.setIndex(0);
        task.setAddress("localhost");
        task.setAccessType(AccessType.CONNECT);
        task.setPort(80);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setNetworkTaskId(task.getId());
        accessTypeData.setConnectCount(5);
        accessTypeData.setStopOnSuccess(true);
        getAccessTypeDataDAO().insertAccessTypeData(accessTypeData);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(withText("5")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("9"));
        onView(withText("Download")).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask resultTask = dialog.getNetworkTask();
        AccessTypeData resultData = dialog.getAccessTypeData();
        assertEquals(AccessType.DOWNLOAD, resultTask.getAccessType());
        assertEquals(80, resultTask.getPort());
        assertEquals(5, resultData.getConnectCount());
        assertTrue(resultData.isStopOnSuccess());
        assertFalse(resultData.isIgnoreSSLError());
        assertNull(resultData.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, resultData.getSnmpTransport());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testHiddenSNMPFieldsNotReturnedAfterChange() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(typeText("newcommunity"), closeSoftKeyboard());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v2c)).perform(click());
        onView(withText("Ping")).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        NetworkTask resultTask = dialog.getNetworkTask();
        AccessTypeData resultData = dialog.getAccessTypeData();
        assertEquals(AccessType.PING, resultTask.getAccessType());
        assertEquals(161, resultTask.getPort());
        assertEquals(SNMPVersion.V1, resultData.getSnmpVersion());
        assertEquals("community", resultData.getSnmpCommunity());
        assertEquals(SNMPTransport.TCP, resultData.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, resultData.getSnmpAuthAlgorithm());
        assertEquals("user", resultData.getSnmpUserName());
        assertEquals("authpass", resultData.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256, resultData.getSnmpPrivAlgorithm());
        assertEquals("privpass", resultData.getSnmpPrivPassphrase());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
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
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        task = dialog.getNetworkTask();
        assertEquals("https://test.com", task.getAddress());
    }

    @Test
    public void testGetNetworkTaskCommunityTrimmed() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(replaceText("  public  "));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals("public", data.getSnmpCommunity());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(replaceText("public  "));
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        data = dialog.getAccessTypeData();
        assertEquals("public", data.getSnmpCommunity());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
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
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        assertNull(data.getSnmpCommunity());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
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
        List<Resolve> initialResolves = dialog.getInitialResolves();
        List<Resolve> resolves = dialog.getResolves();
        List<Header> initialHeaders = dialog.getInitialHeaders();
        List<Header> headers = dialog.getInitialHeaders();
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
        assertEquals(initialAccessTypeData.getSnmpVersion(), data.getSnmpVersion());
        assertEquals(initialAccessTypeData.getSnmpCommunity(), data.getSnmpCommunity());
        assertEquals(initialAccessTypeData.getSnmpTransport(), data.getSnmpTransport());
        assertEquals(initialAccessTypeData.getSnmpAuthAlgorithm(), data.getSnmpAuthAlgorithm());
        assertEquals(initialAccessTypeData.getSnmpUserName(), data.getSnmpUserName());
        assertEquals(initialAccessTypeData.getSnmpAuthPassphrase(), data.getSnmpAuthPassphrase());
        assertEquals(initialAccessTypeData.getSnmpPrivAlgorithm(), data.getSnmpPrivAlgorithm());
        assertEquals(initialAccessTypeData.getSnmpPrivPassphrase(), data.getSnmpPrivPassphrase());
        assertNull(initialHeaders);
        assertNull(headers);
        assertTrue(initialResolves.isEmpty());
        assertTrue(resolves.isEmpty());
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
        assertEquals(initialAccessTypeData.getSnmpVersion(), data.getSnmpVersion());
        assertEquals(initialAccessTypeData.getSnmpCommunity(), data.getSnmpCommunity());
        assertEquals(initialAccessTypeData.getSnmpTransport(), data.getSnmpTransport());
        assertEquals(initialAccessTypeData.getSnmpAuthAlgorithm(), data.getSnmpAuthAlgorithm());
        assertEquals(initialAccessTypeData.getSnmpUserName(), data.getSnmpUserName());
        assertEquals(initialAccessTypeData.getSnmpAuthPassphrase(), data.getSnmpAuthPassphrase());
        assertEquals(initialAccessTypeData.getSnmpPrivAlgorithm(), data.getSnmpPrivAlgorithm());
        assertEquals(initialAccessTypeData.getSnmpPrivPassphrase(), data.getSnmpPrivPassphrase());
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
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("80")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).perform(replaceText("8"));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
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
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(not(isDisplayed())));
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
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(not(isDisplayed())));
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
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_stop_on_success)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ping_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_ping_package_size)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_connect_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.radiogroup_dialog_network_task_edit_snmp_version)).check(matches(isDisplayed()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v2c)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).check(matches(isNotChecked()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(isDisplayed()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isNotChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_auth_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_stop_on_success)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_stop_on_success)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testResolveFieldsVisibility() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_resolve_rules)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(not(isDisplayed())));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_resolve_rules)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_resolve_rules)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(withText("Resolve rules:")));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (0 rules)")));
    }

    @Test
    public void testResolveFieldsVisibilityScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_resolve_rules)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_resolve_rules)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.linearlayout_dialog_network_task_edit_resolve_rules)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_label)).check(matches(withText("Resolve rules:")));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (0 rules)")));
    }

    @Test
    public void testResolveRulesInitialState() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (0 rules)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> initialResolves = dialog.getInitialResolves();
        List<Resolve> resolves = dialog.getResolves();
        assertNotNull(initialResolves);
        assertTrue(initialResolves.isEmpty());
        assertNotNull(resolves);
        assertTrue(resolves.isEmpty());
    }

    @Test
    public void testResolveRulesInitialStateScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (0 rules)")));
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialResolves().isEmpty());
        assertTrue(dialog.getResolves().isEmpty());
    }

    @Test
    public void testResolveRulesOpenDialogValidURL() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.textview_dialog_resolves_label)).check(matches(withText("Resolve rules")));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect.host.com:443")));
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (1 rule)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialResolves().isEmpty());
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(1, resolves.size());
        assertEquals("match.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
    }

    @Test
    public void testResolveRulesOpenDialogValidURLScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect.host.com:443")));
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (1 rule)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(1, resolves.size());
        assertEquals("match.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
    }

    @Test
    public void testResolveRulesOpenDialogInvalidURL() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("invalid-url"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.textview_dialog_resolves_label)).check(matches(withText("Resolve rules")));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect.host.com:443")));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: undefined")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:8443")));
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (2 rules)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(2, resolves.size());
        assertEquals("match.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
        assertEquals("", resolves.get(1).getSourceAddress());
        assertEquals(-1, resolves.get(1).getSourcePort());
        assertEquals("connect2.host.com", resolves.get(1).getTargetAddress());
        assertEquals(8443, resolves.get(1).getTargetPort());
    }

    @Test
    public void testResolveRulesChangeCancelZeroResolves() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_cancel)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (0 rules)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialResolves().isEmpty());
        assertTrue(dialog.getResolves().isEmpty());
    }

    @Test
    public void testResolveRulesChangeCancelZeroResolvesScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (0 rules)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialResolves().isEmpty());
        assertTrue(dialog.getResolves().isEmpty());
    }

    @Test
    public void testResolveRulesChangeOkOneResolve() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (1 rule)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialResolves().isEmpty());
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(1, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertEquals("match.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
    }

    @Test
    public void testResolveRulesChangeOkOneResolveScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (1 rule)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(1, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertEquals("match.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
    }

    @Test
    public void testResolveRulesChangeOkMultipleResolves() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match1.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect1.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:7070")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:8443")));
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (2 rules)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialResolves().isEmpty());
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(2, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertEquals("match1.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect1.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
        assertEquals(1, resolves.get(1).getIndex());
        assertEquals("match2.host.com", resolves.get(1).getSourceAddress());
        assertEquals(7070, resolves.get(1).getSourcePort());
        assertEquals("connect2.host.com", resolves.get(1).getTargetAddress());
        assertEquals(8443, resolves.get(1).getTargetPort());
    }

    @Test
    public void testResolveRulesChangeOkMultipleResolvesScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("https://example.com:8080/path"));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match1.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9090"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect1.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_resolves_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (2 rules)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(2, resolves.size());
        assertEquals(0, resolves.get(0).getIndex());
        assertEquals("match1.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect1.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
        assertEquals(1, resolves.get(1).getIndex());
        assertEquals("match2.host.com", resolves.get(1).getSourceAddress());
        assertEquals(7070, resolves.get(1).getSourcePort());
        assertEquals("connect2.host.com", resolves.get(1).getTargetAddress());
        assertEquals(8443, resolves.get(1).getTargetPort());
    }

    @Test
    public void testResolveRulesInitialOneResolve() {
        activityScenario.close();
        NetworkTask task = getNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        Resolve resolve = getResolve(task.getId(), 0, "match.host.com", 9090, "connect.host.com", 443);
        getResolveDAO().insertResolve(resolve);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (1 rule)")));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.textview_dialog_resolves_label)).check(matches(withText("Resolve rules")));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect.host.com:443")));
        onView(withId(R.id.imageview_dialog_resolves_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> initialResolves = dialog.getInitialResolves();
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(1, initialResolves.size());
        assertEquals("match.host.com", initialResolves.get(0).getSourceAddress());
        assertEquals(9090, initialResolves.get(0).getSourcePort());
        assertEquals("connect.host.com", initialResolves.get(0).getTargetAddress());
        assertEquals(443, initialResolves.get(0).getTargetPort());
        assertEquals(1, resolves.size());
        assertEquals("match.host.com", resolves.get(0).getSourceAddress());
        assertEquals(9090, resolves.get(0).getSourcePort());
        assertEquals("connect.host.com", resolves.get(0).getTargetAddress());
        assertEquals(443, resolves.get(0).getTargetPort());
    }

    @Test
    public void testResolveRulesInitialMultipleResolves() {
        activityScenario.close();
        NetworkTask task = getNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        Resolve resolve1 = getResolve(task.getId(), 0, "match1.host.com", 9090, "connect1.host.com", 443);
        Resolve resolve2 = getResolve(task.getId(), 1, "match2.host.com", 7070, "connect2.host.com", 8443);
        getResolveDAO().insertResolve(resolve1);
        getResolveDAO().insertResolve(resolve2);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).check(matches(withText("Click here (2 rules)")));
        onView(withId(R.id.textview_dialog_network_task_edit_resolve_rules_value)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:7070")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:8443")));
        onView(withId(R.id.imageview_dialog_resolves_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<Resolve> initialResolves = dialog.getInitialResolves();
        List<Resolve> resolves = dialog.getResolves();
        assertEquals(2, initialResolves.size());
        assertEquals("match1.host.com", initialResolves.get(0).getSourceAddress());
        assertEquals(9090, initialResolves.get(0).getSourcePort());
        assertEquals("match2.host.com", initialResolves.get(1).getSourceAddress());
        assertEquals(7070, initialResolves.get(1).getSourcePort());
        assertEquals(2, resolves.size());
        assertEquals("match1.host.com", resolves.get(0).getSourceAddress());
        assertEquals("match2.host.com", resolves.get(1).getSourceAddress());
    }

    @Test
    public void testHeadersFields() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testHeadersFieldsScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(not(isDisplayed())));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        rotateScreen(activityScenario);
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testHeadersChangeCancel() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(1, headers.size());
        assertEquals("User-Agent", headers.get(0).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
    }

    @Test
    public void testHeadersChangeCancelScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(1, headers.size());
        assertEquals("User-Agent", headers.get(0).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
    }

    @Test
    public void testHeadersChangeOk() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("AName", headers.get(0).getName());
        assertEquals("AValue", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("User-Agent", headers.get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testHeadersChangeOkScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("AName", headers.get(0).getName());
        assertEquals("AValue", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("User-Agent", headers.get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testHeadersChangeBasicAuthOk() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("Authorization", headers.get(0).getName());
        assertEquals("abc:123", headers.get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("User-Agent", headers.get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testHeadersChangeAuthorizationOk() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("authvalue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Headers network task")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("Authorization", headers.get(0).getName());
        assertEquals("authvalue", headers.get(0).getValue());
        assertEquals(HeaderType.GENERICAUTH, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("User-Agent", headers.get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testHeadersRestore() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_restore)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Restore headers?")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This overwrites the current headers with the default headers.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(1, headers.size());
        assertEquals("User-Agent", headers.get(0).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
    }

    @Test
    public void testHeadersRestoreScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_headers_restore)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Restore headers?")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This overwrites the current headers with the default headers.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (1 header)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(1, headers.size());
        assertEquals("User-Agent", headers.get(0).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
    }

    @Test
    public void testHeadersAddedUseDefaultHeaders() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        assertNull(dialog.getHeaders());
    }

    @Test
    public void testHeadersAddedUseDefaultHeadersScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        assertNull(dialog.getHeaders());
    }

    @Test
    public void testNoHeaders() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (0 headers)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        assertTrue(dialog.getHeaders().isEmpty());
    }

    @Test
    public void testNoHeadersScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (0 headers)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        assertTrue(dialog.getHeaders().isEmpty());
    }

    @Test
    public void testHeadersAddedUpdated() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("BName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("BValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("BName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("BValue")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("AName", headers.get(0).getName());
        assertEquals("AValue", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("BName", headers.get(1).getName());
        assertEquals("BValue", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testHeadersAddedUpdatedScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("BName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("BValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("BName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("BValue")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("AName", headers.get(0).getName());
        assertEquals("AValue", headers.get(0).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("BName", headers.get(1).getName());
        assertEquals("BValue", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testHeadersAddedAuthorization() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("authvalue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("Authorization", headers.get(0).getName());
        assertEquals("authvalue", headers.get(0).getValue());
        assertEquals(HeaderType.GENERICAUTH, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("User-Agent", headers.get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
    }

    @Test
    public void testShowConfirmAuthorizationHeaderNoticeOnlyOnce() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.switch_dialog_network_task_edit_use_default_headers)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("authvalue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(4, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_headers_label)).check(matches(withText("HTTP Headers:")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).check(matches(withText("Click here (2 headers)")));
        onView(withId(R.id.textview_dialog_network_task_edit_headers_value)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertNull(dialog.getInitialHeaders());
        List<Header> headers = dialog.getHeaders();
        assertEquals(2, headers.size());
        assertEquals("Authorization", headers.get(0).getName());
        assertEquals("abc:123", headers.get(0).getValue());
        assertEquals(HeaderType.BASICAUTH, headers.get(0).getHeaderType());
        assertTrue(headers.get(0).isValueValid());
        assertEquals("User-Agent", headers.get(1).getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", headers.get(1).getValue());
        assertEquals(HeaderType.GENERIC, headers.get(1).getHeaderType());
        assertTrue(headers.get(1).isValueValid());
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
    public void testOnOkCancelClickedErrorDialogSNMP() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(replaceText("invalid community"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(matches(isDisplayed()));
        onView(withText("Minimum: 1")).check(matches(isDisplayed()));
        onView(withText("Community")).check(matches(isDisplayed()));
        onView(withText("Value contains invalid characters")).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("15"));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(replaceText("public"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("No valid host or IP address")).check(matches(isDisplayed()));
        onView(withText("Interval")).check(doesNotExist());
        onView(withText("Minimum: 1")).check(doesNotExist());
        onView(withText("Community")).check(doesNotExist());
        onView(withText("Value contains invalid characters")).check(doesNotExist());
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
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
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("192.168.1.1"));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("161"));
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("15"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withTextColor(R.color.textColor)));
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
        onView(withId(R.id.cardview_activity_defaults_certificate_settings)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_certificate_settings)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
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
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_network_task_edit_only_wifi)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_only_wifi_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_notification)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_notification_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_network_task_edit_high_prio)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_network_task_edit_high_prio_on_off)).check(matches(withText("yes")));
    }

    @Test
    public void testNewDefaultValuesForNetworkTaskSNMP() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v2c)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Defaults")).perform(click());
        onView(withId(R.id.cardview_activity_defaults_snmp_settings)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_snmp_settings)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.imageview_dialog_snmp_defaults_ok)).perform(click());
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v1)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v2c)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isNotChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
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
        rotateScreen(activityScenario);
        onView(withText("Download")).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).check(matches(withText("localhost")));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).check(matches(withText("60")));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(withText("Click here")));
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
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

    @Test
    public void testSNMPCommunityToggle() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPCommunityToggleScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPCommunityToggleDisabledWithInitialCommunity() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(typeText("newcommunity"), closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText("newcommunity")));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPCommunityToggleDisabledWithInitialCommunityScreenRotation() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(typeText("newcommunity"), closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText("newcommunity")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPCommunityTogglePreservedOnAccessTypeChange() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withText("Download")).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(true)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText("")));
        onView(withText("Ping")).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPTransportFieldVisibility() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_transport_label)).check(matches(not(isDisplayed())));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_transport_label)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_transport_label)).check(matches(not(isDisplayed())));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_transport_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_transport_label)).check(matches(withText("Transport:")));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isDisplayed()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_auth_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_transport_label)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testSNMPVersionV3CommunityAuthVisibility() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_auth_label)).check(matches(not(isDisplayed())));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_auth_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_auth_label)).check(matches(withText("Authentication:")));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_auth_value)).check(matches(withText("Click here")));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v1)).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v2c)).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPVersionV3ScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(not(isDisplayed())));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPTransportTCPScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isChecked()));
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isNotChecked()));
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isNotChecked()));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(SNMPTransport.TCP, dialog.getAccessTypeData().getSnmpTransport());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPTransportDefault() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isNotChecked()));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(SNMPTransport.UDP, dialog.getAccessTypeData().getSnmpTransport());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPTransportReturnedByGetAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPVersion.V2C, data.getSnmpVersion());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPTransportNotReturnedWhenSNMPHidden() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).perform(click());
        onView(withText("Ping")).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
    }

    @Test
    public void testSNMPTransportInitialValueFromExistingTask() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_udp)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v1)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(isDisplayed()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPVersionAndTransportPreservedOnAccessTypeChange() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_transport)).check(matches(not(isDisplayed())));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPAuthDialogOpenedOnClick() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(doesNotExist());
    }

    @Test
    public void testSNMPAuthDialogOkUpdatesAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V3, data.getSnmpVersion());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertEquals("testuser", data.getSnmpUserName());
        assertEquals("", data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("", data.getSnmpPrivPassphrase());
        assertNull(data.getSnmpCommunity());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPAuthDialogCancelPreservesAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V3, data.getSnmpVersion());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        assertNull(data.getSnmpCommunity());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPAuthDataSurvivesRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V3, data.getSnmpVersion());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertEquals("testuser", data.getSnmpUserName());
        assertEquals("", data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("", data.getSnmpPrivPassphrase());
        assertNull(data.getSnmpCommunity());
        rotateScreen(activityScenario);
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V3, data.getSnmpVersion());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertEquals("testuser", data.getSnmpUserName());
        assertEquals("", data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("", data.getSnmpPrivPassphrase());
        assertNull(data.getSnmpCommunity());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPV3AuthDataReturnedByGetAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V3, data.getSnmpVersion());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertEquals("testuser", data.getSnmpUserName());
        assertEquals("", data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertEquals("", data.getSnmpPrivPassphrase());
        assertNull(data.getSnmpCommunity());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPAuthDialogWithPassphrase() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-256"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(replaceText("authpass123"), closeSoftKeyboard());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-256"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(replaceText("privpass123"), closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V3, data.getSnmpVersion());
        assertEquals(SNMPTransport.TCP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, data.getSnmpAuthAlgorithm());
        assertEquals("testuser", data.getSnmpUserName());
        assertEquals("authpass123", data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256, data.getSnmpPrivAlgorithm());
        assertEquals("privpass123", data.getSnmpPrivPassphrase());
        assertNull(data.getSnmpCommunity());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPV3AuthDataNotReturnedWhenV1Selected() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_auth)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("testuser"), closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v1)).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertEquals(SNMPVersion.V1, data.getSnmpVersion());
        assertEquals(SNMPTransport.UDP, data.getSnmpTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, data.getSnmpAuthAlgorithm());
        assertNull(data.getSnmpUserName());
        assertNull(data.getSnmpAuthPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES128, data.getSnmpPrivAlgorithm());
        assertNull(data.getSnmpPrivPassphrase());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPCommunityValidationSkippedForV3() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_snmp_community)).perform(replaceText("invalid community"), closeSoftKeyboard());
        onView(withId(R.id.radiobutton_dialog_network_task_edit_snmp_version_v3)).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_address)).perform(replaceText("123.456"));
        onView(withId(R.id.edittext_dialog_network_task_edit_interval)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_network_task_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withText("Host")).check(matches(isDisplayed()));
        onView(withText("Community")).check(doesNotExist());
        onView(withText("Value contains invalid characters")).check(doesNotExist());
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testPortSwitchToSNMPShowsDefaultSNMPPort() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("161")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(161, dialog.getNetworkTask().getPort());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        assertEquals(22, dialog.getNetworkTask().getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testPortSwitchFromSNMPTaskToConnect() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = new AccessTypeData();
        data.setNetworkTaskId(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("161")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(161, dialog.getNetworkTask().getPort());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        assertEquals(22, dialog.getNetworkTask().getPort());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("161")));
        assertEquals(161, dialog.getNetworkTask().getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testPortPreservedAfterSwitchToSNMPAndBack() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("443"));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("161")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("443")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(443, dialog.getNetworkTask().getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPPortPreservedAfterSwitchToConnectAndBack() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = new AccessTypeData();
        data.setNetworkTaskId(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("163"));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("163")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(163, dialog.getNetworkTask().getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testPortSwitchPreservedOnScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("443"));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("161")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("161")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(161, dialog.getNetworkTask().getPort());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("443")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("443")));
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(443, dialog.getNetworkTask().getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPPortPreservedOnScreenRotation() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = new AccessTypeData();
        data.setNetworkTaskId(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).perform(replaceText("163"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("163")));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("22")));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.edittext_dialog_network_task_edit_port)).check(matches(withText("163")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(163, dialog.getNetworkTask().getPort());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testSNMPInterfacesFieldVisibility() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_interfaces)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_label)).check(matches(not(isDisplayed())));
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_interfaces)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_label)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_interfaces)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_label)).check(matches(not(isDisplayed())));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_snmp_interfaces)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_label)).check(matches(withText("Interfaces:")));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (0 interfaces)")));
    }

    @Test
    public void testSNMPInterfacesInitialState() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (0 interfaces)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<SNMPItem> initialSnmpItems = dialog.getInitialSnmpItems();
        List<SNMPItem> snmpItems = dialog.getSnmpItems();
        assertNotNull(initialSnmpItems);
        assertTrue(initialSnmpItems.isEmpty());
        assertNotNull(snmpItems);
        assertTrue(snmpItems.isEmpty());
    }

    @Test
    public void testSNMPInterfacesInitialStateScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("SNMP")).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (0 interfaces)")));
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertTrue(dialog.getInitialSnmpItems().isEmpty());
        assertTrue(dialog.getSnmpItems().isEmpty());
    }

    @Test
    public void testSNMPInterfacesInitialItems() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        SNMPItem snmpItem1 = getSNMPItem(task.getId(), "eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem snmpItem2 = getSNMPItem(task.getId(), "wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        getSnmpItemDAO().insertSNMPItem(snmpItem1);
        getSnmpItemDAO().insertSNMPItem(snmpItem2);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (2 interfaces)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        List<SNMPItem> initialSnmpItems = dialog.getInitialSnmpItems();
        List<SNMPItem> snmpItems = dialog.getSnmpItems();
        assertEquals(2, initialSnmpItems.size());
        assertEquals("eth0", initialSnmpItems.get(0).getName());
        assertEquals("1.3.6.1.2.1.2.2.1.2.1", initialSnmpItems.get(0).getOid());
        assertEquals("wlan0", initialSnmpItems.get(1).getName());
        assertEquals("1.3.6.1.2.1.2.2.1.2.2", initialSnmpItems.get(1).getOid());
        assertEquals(2, snmpItems.size());
        assertEquals("eth0", snmpItems.get(0).getName());
        assertEquals("wlan0", snmpItems.get(1).getName());
    }

    @Test
    public void testSNMPInterfacesInitialItemsScreenRotation() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        SNMPItem snmpItem1 = getSNMPItem(task.getId(), "eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem snmpItem2 = getSNMPItem(task.getId(), "wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        getSnmpItemDAO().insertSNMPItem(snmpItem1);
        getSnmpItemDAO().insertSNMPItem(snmpItem2);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (2 interfaces)")));
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(2, dialog.getInitialSnmpItems().size());
        assertEquals("eth0", dialog.getInitialSnmpItems().get(0).getName());
        assertEquals("wlan0", dialog.getInitialSnmpItems().get(1).getName());
        assertEquals(2, dialog.getSnmpItems().size());
        assertEquals("eth0", dialog.getSnmpItems().get(0).getName());
        assertEquals("wlan0", dialog.getSnmpItems().get(1).getName());
    }

    @Test
    public void testSNMPInterfacesMerge() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        getSnmpItemDAO().insertSNMPItem(getSNMPItem(task.getId(), "eth0", "1.3.6.1.2.1.2.2.1.2.1"));
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (1 interface)")));
        SNMPScanResult mockResult = new SNMPScanResult(true, List.of(getSNMPItem(-1, "eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")), Collections.emptyMap(), Collections.emptyList(), null);
        showTestSNMPInterfacesDialog(mockResult);
        onView(withId(R.id.imageview_dialog_snmp_interfaces_ok)).perform(click());
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (2 interfaces)")));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(1, dialog.getInitialSnmpItems().size());
        assertEquals("eth0", dialog.getInitialSnmpItems().get(0).getName());
        assertEquals(2, dialog.getSnmpItems().size());
        assertEquals("eth0", dialog.getSnmpItems().get(0).getName());
        assertEquals("wlan0", dialog.getSnmpItems().get(1).getName());
    }

    @Test
    public void testSNMPInterfacesMergeScreenRotation() {
        activityScenario.close();
        NetworkTask task = getSNMPNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData data = getAccessTypeData(task.getId());
        getAccessTypeDataDAO().insertAccessTypeData(data);
        getSnmpItemDAO().insertSNMPItem(getSNMPItem(task.getId(), "eth0", "1.3.6.1.2.1.2.2.1.2.1"));
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        SNMPScanResult mockResult = new SNMPScanResult(true, List.of(getSNMPItem(-1, "eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")), Collections.emptyMap(), Collections.emptyList(), null);
        showTestSNMPInterfacesDialog(mockResult);
        onView(withId(R.id.imageview_dialog_snmp_interfaces_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_network_task_edit_snmp_interfaces_value)).check(matches(withText("Click here (2 interfaces)")));
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        assertEquals(1, dialog.getInitialSnmpItems().size());
        assertEquals("eth0", dialog.getInitialSnmpItems().get(0).getName());
        assertEquals(2, dialog.getSnmpItems().size());
        assertEquals("eth0", dialog.getSnmpItems().get(0).getName());
        assertEquals("wlan0", dialog.getSnmpItems().get(1).getName());
    }

    @Test
    public void testCertificateSettingsFieldVisibility() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_label)).check(matches(not(isDisplayed())));
        onView(withText("Ping")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_label)).check(matches(not(isDisplayed())));
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_label)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_label)).check(matches(withText("Certificate settings:")));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(withText("Click here")));
        onView(withText("SNMP")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_label)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testCertificateSettingsFieldVisibilityScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Connect")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withText("Download")).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_label)).check(matches(withText("Certificate settings:")));
        onView(withId(R.id.textview_dialog_network_task_edit_certificate_settings_value)).check(matches(withText("Click here")));
    }

    @Test
    public void testCertificateSettingsInitialState() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotChecked()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("30")));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsDialogOpenedOnClick() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(doesNotExist());
    }

    @Test
    public void testCertificateSettingsDialogOkUpdatesAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertTrue(data.isAllowLegacyTLS());
        assertTrue(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsDialogCancelPreservesAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertFalse(data.isAllowLegacyTLS());
        assertFalse(data.isIgnoreSSLError());
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsDataScreenRotation() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("60"));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        rotateScreen(activityScenario);
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertTrue(data.isAllowLegacyTLS());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(60, data.getFailureOnCertificateExpiryDays());
        rotateScreen(activityScenario);
        dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        data = dialog.getAccessTypeData();
        assertTrue(data.isAllowLegacyTLS());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(60, data.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsNotReturnedWhenAccessTypeSwitchedAway() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withText("Ping")).perform(click());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertFalse(data.isIgnoreSSLError());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsChangesDoNotAffectInitialAccessTypeData() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData initialData = dialog.getInitialAccessTypeData();
        AccessTypeData data = dialog.getAccessTypeData();
        assertFalse(initialData.isIgnoreSSLError());
        assertTrue(data.isIgnoreSSLError());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsInitializedFromExistingAccessTypeData() {
        activityScenario.close();
        NetworkTask task = getNetworkTask();
        task = getNetworkTaskDAO().insertNetworkTask(task);
        AccessTypeData accessTypeData = new AccessTypeData();
        accessTypeData.setNetworkTaskId(task.getId());
        accessTypeData.setAllowLegacyTLS(true);
        accessTypeData.setIgnoreSSLError(true);
        accessTypeData.setFailureOnCertificateExpiry(true);
        accessTypeData.setFailureOnCertificateExpiryDays(90);
        getAccessTypeDataDAO().insertAccessTypeData(accessTypeData);
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        injectPermissionManager();
        onView(allOf(withId(R.id.imageview_list_item_network_task_edit), withChildDescendantAtPosition(withId(R.id.listview_activity_main_network_tasks), 0))).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("90")));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsExpiryDaysTakenOverWhenDisabledButValid() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("45"));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(45, data.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsExpiryDaysNotTakenOverWhenDisabledAndInvalid() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("0"));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertTrue(data.isIgnoreSSLError());
        assertTrue(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    @Test
    public void testCertificateSettingsExpiryDaysNotTakenOverWhenHidden() {
        onView(allOf(withId(R.id.imageview_activity_main_network_task_add), isDisplayed())).perform(click());
        onView(withText("Download")).perform(click());
        onView(withId(R.id.linearlayout_dialog_network_task_edit_certificate_settings)).perform(performClickIgnoringVisibility());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("0"));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        AccessTypeData data = dialog.getAccessTypeData();
        assertFalse(data.isFailureOnCertificateExpiry());
        assertEquals(30, data.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_network_task_edit_cancel)).perform(click());
    }

    private MockClipboardManager prepareMockClipboardManager() {
        onView(isRoot()).perform(waitFor(500));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        dialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private void showTestSNMPInterfacesDialog(SNMPScanResult mockResult) {
        TestSNMPInterfacesDialog dialog = new TestSNMPInterfacesDialog();
        dialog.setMockResult(mockResult);
        SNMPInterfacesDialog snmpInterfacesDialog = new SNMPInterfacesDialog();
        Bundle bundle = BundleUtil.snmpItemListToBundle(snmpInterfacesDialog.getInitialSNMPItemsKey(), Collections.emptyList());
        BundleUtil.stringToBundle(snmpInterfacesDialog.getAddressKey(), "192.168.1.1", bundle);
        BundleUtil.integerToBundle(snmpInterfacesDialog.getPortKey(), 161, bundle);
        BundleUtil.stringToBundle(snmpInterfacesDialog.getSNMPVersionKey(), SNMPVersion.V2C.name(), bundle);
        BundleUtil.stringToBundle(snmpInterfacesDialog.getSNMPTransportKey(), SNMPTransport.UDP.name(), bundle);
        SNMPAuthInfo authInfo = new SNMPAuthInfo();
        authInfo.setCommunity("public");
        BundleUtil.bundleToBundle(snmpInterfacesDialog.getAuthInfoKey(), authInfo.toBundle(), bundle);
        dialog.setArguments(bundle);
        dialog.show(getActivity(activityScenario).getSupportFragmentManager(), SNMPInterfacesDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
    }

    private void injectPermissionManager() {
        ((NetworkTaskMainActivity) getActivity(activityScenario)).injectPermissionManager(permissionManager);
    }

    private void resetGlobalHeaderHandler() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
    }

    private void addDefaultHeader() {
        DBSetup dbSetup = new DBSetup(TestRegistry.getContext());
        dbSetup.initializeHeaderTable();
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setIndex(0);
        task.setAddress("https://example.com:8080/path");
        task.setAccessType(AccessType.DOWNLOAD);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }

    private NetworkTask getSNMPNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setIndex(0);
        task.setAddress("192.168.1.1");
        task.setAccessType(AccessType.SNMP);
        task.setPort(161);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(false);
        task.setRunning(false);
        return task;
    }

    private AccessTypeData getAccessTypeData(long networkTaskId) {
        AccessTypeData data = new AccessTypeData();
        data.setNetworkTaskId(networkTaskId);
        data.setUseDefaultHeaders(false);
        data.setIgnoreSSLError(false);
        data.setAllowLegacyTLS(false);
        data.setStopOnSuccess(false);
        data.setSnmpVersion(SNMPVersion.V1);
        data.setSnmpCommunity("community");
        data.setSnmpCommunityValid(true);
        data.setSnmpTransport(SNMPTransport.TCP);
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpUserName("user");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthPassphraseValid(true);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivPassphraseValid(true);
        data.setFailureOnCertificateExpiry(true);
        data.setFailureOnCertificateExpiryDays(14);
        return data;
    }

    private Resolve getResolve(long networkTaskId, int index, String sourceAddress, int sourcePort, String targetAddress, int targetPort) {
        Resolve resolve = new Resolve(networkTaskId);
        resolve.setIndex(index);
        resolve.setSourceAddress(sourceAddress);
        resolve.setSourcePort(sourcePort);
        resolve.setTargetAddress(targetAddress);
        resolve.setTargetPort(targetPort);
        return resolve;
    }

    private SNMPItem getSNMPItem(long networkTaskId, String name, String oid) {
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(-1);
        snmpItem.setNetworkTaskId(networkTaskId);
        snmpItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        snmpItem.setName(name);
        snmpItem.setOid(oid);
        snmpItem.setMonitored(true);
        return snmpItem;
    }

    @SuppressWarnings("SameParameterValue")
    private SNMPItem getSNMPDescrItem(String name, String oid) {
        SNMPItem snmpItem = new SNMPItem();
        snmpItem.setId(-1);
        snmpItem.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        snmpItem.setName(name);
        snmpItem.setOid(oid);
        snmpItem.setMonitored(false);
        return snmpItem;
    }
}
