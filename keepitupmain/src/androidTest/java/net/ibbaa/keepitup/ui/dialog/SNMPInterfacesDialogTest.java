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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.test.mock.TestSNMPInterfacesDialog;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.ui.sync.SNMPScanResult;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class SNMPInterfacesDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDialogEmptyListBeforeScan() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showDialog(Collections.emptyList());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("Use Scan to load the interfaces")));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(not(isDisplayed())));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogEmptyListBeforeScanScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showDialog(Collections.emptyList());
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("Use Scan to load the interfaces")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("Use Scan to load the interfaces")));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(not(isDisplayed())));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogEmptyListAfterScan() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(Collections.emptyList(), Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("The device returned no interfaces")));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(not(isDisplayed())));
        assertEquals(0, getTestDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogEmptyListAfterScanScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(Collections.emptyList(), Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("The device returned no interfaces")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_no_item), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("The device returned no interfaces")));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(not(isDisplayed())));
        assertEquals(0, getTestDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogItemsNameOnlyNoInfos() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showDialog(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")));
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("eth0")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).check(matches(withText("wlan0")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("")));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(not(isDisplayed())));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogItemsNameOnlyNoInfosScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showDialog(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")));
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("eth0")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("eth0")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).check(matches(withText("wlan0")));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogItemWithAlias() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        showTestDialog(Collections.emptyList(), successResult(List.of(item), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, "LAN"))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("LAN")));
        activityScenario.close();
    }

    @Test
    public void testDialogItemWithAliasScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        showTestDialog(Collections.emptyList(), successResult(List.of(item), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, "LAN"))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("LAN")));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("LAN")));
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogItemWithEmptyAlias() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        showTestDialog(Collections.emptyList(), successResult(List.of(item), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_alias), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(not(isDisplayed())));
        activityScenario.close();
    }

    @Test
    public void testDialogItemStatusUpGreen() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        showTestDialog(Collections.emptyList(), successResult(List.of(item), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("Up")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withTextColor(R.color.textOkColor)));
        activityScenario.close();
    }

    @Test
    public void testDialogItemStatusDownRed() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        showTestDialog(Collections.emptyList(), successResult(List.of(item), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 2, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("Down")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        activityScenario.close();
    }

    @Test
    public void testDialogItemStatusTestingRed() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        showTestDialog(Collections.emptyList(), successResult(List.of(item), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 3, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withText("Testing")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        activityScenario.close();
    }

    @Test
    public void testDialogItemStatusColorsScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item1 = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem item2 = getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        showTestDialog(Collections.emptyList(), successResult(List.of(item1, item2), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(6, 2, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withTextColor(R.color.textOkColor)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(withTextColor(R.color.textOkColor)));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_status), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogItemLongNameEllipsized() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        String longName = "very-long-interface-name-that-exceeds-any-reasonable-display-width-and-must-be-truncated";
        showDialog(List.of(getSNMPDescrItem(longName, "1.3.6.1.2.1.2.2.1.2.1")));
        onView(allOf(withId(R.id.textview_list_item_snmp_interface_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).check(matches(isEllipsized()));
        activityScenario.close();
    }

    @Test
    public void testDialogLargeList() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        List<SNMPItem> items = new ArrayList<>();
        for (int ii = 1; ii <= 15; ii++) {
            items.add(getSNMPDescrItem("interface" + ii, "1.3.6.1.2.1.2.2.1.2." + ii));
        }
        showTestDialog(Collections.emptyList(), successResult(items, Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertEquals(15, getTestDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogLargeListScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        List<SNMPItem> items = new ArrayList<>();
        for (int ii = 1; ii <= 15; ii++) {
            items.add(getSNMPDescrItem("interface" + ii, "1.3.6.1.2.1.2.2.1.2." + ii));
        }
        showTestDialog(Collections.emptyList(), successResult(items, Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertEquals(15, getTestDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        assertEquals(15, getTestDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        assertEquals(15, getTestDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogShowAllHiddenWhenNoFilterEffect() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(6, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(not(isDisplayed())));
        activityScenario.close();
    }

    @Test
    public void testDialogShowAllVisibleWhenFilterEffect() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(isDisplayed()));
        activityScenario.close();
    }

    @Test
    public void testDialogShowAllVisibleScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.cardview_dialog_snmp_interfaces_show_all)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogShowAllToggleShowsAllItems() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        activityScenario.close();
    }

    @Test
    public void testDialogShowAllToggleStateScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        assertTrue(getTestDialog().getAdapter().isShowAll());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogScanError() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), errorResult(List.of("Connection failed")));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Scan failed")));
        activityScenario.close();
    }

    @Test
    public void testDialogScanErrorScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), errorResult(List.of("Connection failed")));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Scan failed")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Scan failed")));
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogScanMultipleErrors() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), errorResult(List.of("Error 1", "Error 2", "Error 3")));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Scan failed")));
        activityScenario.close();
    }

    @Test
    public void testDialogScanErrorClearsExistingItems() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item1 = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem item2 = getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        showTestDialog(List.of(item1, item2), errorResult(List.of("Connection failed")));
        assertEquals(2, getTestDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Scan failed")));
        assertEquals(0, getTestDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogScanErrorClearsExistingItemsScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item1 = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem item2 = getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        showTestDialog(List.of(item1, item2), errorResult(List.of("Connection failed")));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Scan failed")));
        assertEquals(0, getTestDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        assertEquals(0, getTestDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogScanRemovedMonitoredItems() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem monitoredItem = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        monitoredItem.setMonitored(true);
        showTestDialog(List.of(monitoredItem), successResult(List.of(getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")), Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Interfaces not found")));
        activityScenario.close();
    }

    @Test
    public void testDialogScanRemovedMonitoredItemsScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem monitoredItem = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        monitoredItem.setMonitored(true);
        showTestDialog(List.of(monitoredItem), successResult(List.of(getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2")), Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Interfaces not found")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Interfaces not found")));
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogAdapterGetAllItemsAndInterfaceInfos() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item1 = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem item2 = getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        SNMPInterfaceInfo info1 = getInterfaceInfo(6, 1, "LAN");
        SNMPInterfaceInfo info2 = getInterfaceInfo(6, 2, null);
        showTestDialog(Collections.emptyList(), successResult(List.of(item1, item2), Map.of("1.3.6.1.2.1.2.2.1.2.1", info1, "1.3.6.1.2.1.2.2.1.2.2", info2)));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        List<SNMPItem> allItems = getTestDialog().getAdapter().getAllItems();
        assertEquals(2, allItems.size());
        assertEquals("eth0", allItems.get(0).getName());
        assertEquals("wlan0", allItems.get(1).getName());
        Map<String, SNMPInterfaceInfo> adapterInfos = getTestDialog().getAdapter().getInterfaceInfos();
        assertEquals(2, adapterInfos.size());
        assertNotNull(adapterInfos.get("1.3.6.1.2.1.2.2.1.2.1"));
        assertEquals("LAN", Objects.requireNonNull(adapterInfos.get("1.3.6.1.2.1.2.2.1.2.1")).getAlias());
        assertEquals(1, Objects.requireNonNull(adapterInfos.get("1.3.6.1.2.1.2.2.1.2.1")).getStatus());
        assertEquals(2, Objects.requireNonNull(adapterInfos.get("1.3.6.1.2.1.2.2.1.2.2")).getStatus());
        activityScenario.close();
    }

    @Test
    public void testDialogAdapterGetAllItemsAndInterfaceInfosScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        SNMPItem item1 = getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1");
        SNMPItem item2 = getSNMPDescrItem("wlan0", "1.3.6.1.2.1.2.2.1.2.2");
        showTestDialog(Collections.emptyList(), successResult(List.of(item1, item2), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, "LAN"), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(6, 2, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        rotateScreen(activityScenario);
        List<SNMPItem> allItems = getTestDialog().getAdapter().getAllItems();
        assertEquals(2, allItems.size());
        assertEquals("eth0", allItems.get(0).getName());
        assertEquals("LAN", Objects.requireNonNull(getTestDialog().getAdapter().getInterfaceInfos().get("1.3.6.1.2.1.2.2.1.2.1")).getAlias());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogMonitoredCheckbox() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1")), Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertFalse(getTestDialog().getAdapter().getAllItems().get(0).isMonitored());
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).perform(click());
        assertTrue(getTestDialog().getAdapter().getAllItems().get(0).isMonitored());
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).perform(click());
        assertFalse(getTestDialog().getAdapter().getAllItems().get(0).isMonitored());
        activityScenario.close();
    }

    @Test
    public void testDialogMonitoredFilteredItemAppearsInFilteredList() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).perform(click());
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        activityScenario.close();
    }

    @Test
    public void testDialogMonitoredFilteredItemAppearsScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).perform(click());
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        assertTrue(getTestDialog().getAdapter().getAllItems().get(1).isMonitored());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    @Test
    public void testDialogUnmonitoredFilteredItemDisappearsFromFilteredList() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1"), getSNMPDescrItem("lo", "1.3.6.1.2.1.2.2.1.2.2")), Map.of("1.3.6.1.2.1.2.2.1.2.1", getInterfaceInfo(6, 1, null), "1.3.6.1.2.1.2.2.1.2.2", getInterfaceInfo(24, 1, null))));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).perform(click());
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(2)));
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 1))).perform(click());
        onView(withId(R.id.checkbox_dialog_snmp_interfaces_show_all)).perform(click());
        onView(withId(R.id.listview_dialog_snmp_interfaces_items)).check(matches(withListSize(1)));
        activityScenario.close();
    }

    @Test
    public void testDialogMonitoredCheckboxScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showTestDialog(Collections.emptyList(), successResult(List.of(getSNMPDescrItem("eth0", "1.3.6.1.2.1.2.2.1.2.1")), Collections.emptyMap()));
        onView(withId(R.id.imageview_dialog_snmp_interfaces_scan)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.checkbox_list_item_snmp_interface_monitored), withChildDescendantAtPosition(withId(R.id.listview_dialog_snmp_interfaces_items), 0))).perform(click());
        assertTrue(getTestDialog().getAdapter().getAllItems().get(0).isMonitored());
        rotateScreen(activityScenario);
        assertTrue(getTestDialog().getAdapter().getAllItems().get(0).isMonitored());
        rotateScreen(activityScenario);
        activityScenario.close();
    }

    private void showDialog(List<SNMPItem> initialItems) {
        SNMPInterfacesDialog dialog = new SNMPInterfacesDialog();
        dialog.setArguments(buildDialogBundle(dialog, initialItems));
        dialog.show(getActivity(activityScenario).getSupportFragmentManager(), SNMPInterfacesDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }

    private void showTestDialog(List<SNMPItem> initialItems, SNMPScanResult mockResult) {
        TestSNMPInterfacesDialog dialog = new TestSNMPInterfacesDialog();
        dialog.setMockResult(mockResult);
        dialog.setArguments(buildDialogBundle(dialog, initialItems));
        dialog.show(getActivity(activityScenario).getSupportFragmentManager(), SNMPInterfacesDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }

    private Bundle buildDialogBundle(SNMPInterfacesDialog dialog, List<SNMPItem> initialItems) {
        Bundle bundle = BundleUtil.snmpItemListToBundle(dialog.getInitialSNMPItemsKey(), initialItems);
        BundleUtil.stringToBundle(dialog.getAddressKey(), "test.host", bundle);
        BundleUtil.integerToBundle(dialog.getPortKey(), 161, bundle);
        BundleUtil.stringToBundle(dialog.getSNMPVersionKey(), SNMPVersion.V2C.name(), bundle);
        BundleUtil.stringToBundle(dialog.getCommunityKey(), "public", bundle);
        return bundle;
    }

    private SNMPScanResult successResult(List<SNMPItem> items, Map<String, SNMPInterfaceInfo> infos) {
        return new SNMPScanResult(true, items, infos, Collections.emptyList(), null);
    }

    private SNMPScanResult errorResult(List<String> errors) {
        return new SNMPScanResult(false, Collections.emptyList(), Collections.emptyMap(), errors, null);
    }

    private SNMPItem getSNMPDescrItem(String name, String oid) {
        SNMPItem item = new SNMPItem();
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setName(name);
        item.setOid(oid);
        item.setMonitored(false);
        return item;
    }

    private SNMPInterfaceInfo getInterfaceInfo(int type, int status, String alias) {
        SNMPInterfaceInfo info = new SNMPInterfaceInfo();
        info.setType(type);
        info.setStatus(status);
        info.setAlias(alias);
        return info;
    }

    private SNMPInterfacesDialog getDialog() {
        return (SNMPInterfacesDialog) getDialog(activityScenario, SNMPInterfacesDialog.class);
    }

    private TestSNMPInterfacesDialog getTestDialog() {
        return (TestSNMPInterfacesDialog) getDialog(activityScenario, SNMPInterfacesDialog.class);
    }
}
