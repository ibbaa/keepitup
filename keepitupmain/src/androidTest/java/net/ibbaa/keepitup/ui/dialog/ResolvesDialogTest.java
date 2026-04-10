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
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class ResolvesDialogTest extends BaseUITest {

    private static final String NETWORK_TASK_URL = "https://example.com:8080/path";
    private static final long NETWORK_TASK_ID = 1L;

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDialogConfigurationEmptyList() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(withId(R.id.textview_dialog_resolves_label)).check(matches(withText("Resolve rules")));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        assertEquals(NETWORK_TASK_ID, getDialog().getNetworkTaskId());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogConfigurationEmptyListScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        rotateScreen(activityScenario);
        assertEquals(NETWORK_TASK_ID, getDialog().getNetworkTaskId());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogConfigurationWithResolves() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        assertEquals(NETWORK_TASK_ID, getDialog().getNetworkTaskId());
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(9090, items.get(0).getSourcePort());
        assertEquals("connect1.host.com", items.get(0).getTargetAddress());
        assertEquals(443, items.get(0).getTargetPort());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        assertEquals(9090, items.get(1).getSourcePort());
        assertEquals("connect2.host.com", items.get(1).getTargetAddress());
        assertEquals(443, items.get(1).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testDialogConfigurationWithResolvesScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        rotateScreen(activityScenario);
        assertEquals(NETWORK_TASK_ID, getDialog().getNetworkTaskId());
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals(1, items.get(1).getIndex());
        activityScenario.close();
    }

    @Test
    public void testMatchAndConnectToTextAllFieldsSet() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        activityScenario.close();
    }

    @Test
    public void testMatchAndConnectToTextWithURLFallbackPort() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        Resolve resolve = new Resolve(NETWORK_TASK_ID);
        resolve.setIndex(0);
        resolve.setSourceAddress("match.host.com");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("connect.host.com");
        resolve.setTargetPort(-1);
        showResolvesDialog(List.of(resolve));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match.host.com:8080")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect.host.com:8080")));
        activityScenario.close();
    }

    @Test
    public void testMatchAndConnectToTextWithURLFallbackHost() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        Resolve resolve = new Resolve(NETWORK_TASK_ID);
        resolve.setIndex(0);
        resolve.setSourceAddress("");
        resolve.setSourcePort(9090);
        resolve.setTargetAddress("");
        resolve.setTargetPort(443);
        showResolvesDialog(List.of(resolve));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: example.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: example.com:443")));
        activityScenario.close();
    }

    @Test
    public void testMatchAndConnectToTextEmptyResolve() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        Resolve resolve = new Resolve(NETWORK_TASK_ID);
        resolve.setIndex(0);
        showResolvesDialog(List.of(resolve));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: not set")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: not set")));
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveCancel() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.imageview_list_item_resolve_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveCancelScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.imageview_list_item_resolve_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveSwipeCancel() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_resolves_resolves).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveSwipeCancelScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_resolves_resolves).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveOk() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.imageview_list_item_resolve_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match2.host.com", items.get(0).getSourceAddress());
        assertEquals(9090, items.get(0).getSourcePort());
        assertEquals("connect2.host.com", items.get(0).getTargetAddress());
        assertEquals(443, items.get(0).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveOkScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.imageview_list_item_resolve_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match2.host.com", items.get(0).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveSwipeOk() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_resolves_resolves).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match2.host.com", items.get(0).getSourceAddress());
        assertEquals(9090, items.get(0).getSourcePort());
        assertEquals("connect2.host.com", items.get(0).getTargetAddress());
        assertEquals(443, items.get(0).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testDeleteResolveSwipeOkScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_resolves_resolves).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match2.host.com", items.get(0).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testDeleteLastResolveOk() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.imageview_list_item_resolve_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDeleteLastResolveSwipeOk() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1)));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(isRoot()).perform(waitFor(500));
        onView(withRecyclerView(R.id.listview_dialog_resolves_resolves).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testAddResolveCancel() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("new.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testAddResolveCancelScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("new.host.com"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("No resolve rules defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testAddResolve() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("new.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_no_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: new.host.com:7070")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target.host.com:8443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("new.host.com", items.get(0).getSourceAddress());
        assertEquals(7070, items.get(0).getSourcePort());
        assertEquals("target.host.com", items.get(0).getTargetAddress());
        assertEquals(8443, items.get(0).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testAddResolveScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("new.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: new.host.com:7070")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target.host.com:8443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("new.host.com", items.get(0).getSourceAddress());
        assertEquals(7070, items.get(0).getSourcePort());
        assertEquals("target.host.com", items.get(0).getTargetAddress());
        assertEquals(8443, items.get(0).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testAddMultipleResolves() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("first.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("1111"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target1.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("2222"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("second.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("3333"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("4444"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: first.host.com:1111")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target1.host.com:2222")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: second.host.com:3333")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: target2.host.com:4444")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("first.host.com", items.get(0).getSourceAddress());
        assertEquals(1111, items.get(0).getSourcePort());
        assertEquals("target1.host.com", items.get(0).getTargetAddress());
        assertEquals(2222, items.get(0).getTargetPort());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("second.host.com", items.get(1).getSourceAddress());
        assertEquals(3333, items.get(1).getSourcePort());
        assertEquals("target2.host.com", items.get(1).getTargetAddress());
        assertEquals(4444, items.get(1).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testAddMultipleResolvesScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("first.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("1111"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target1.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("2222"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("second.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("3333"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target2.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("4444"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: first.host.com:1111")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target1.host.com:2222")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: second.host.com:3333")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: target2.host.com:4444")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("first.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("second.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testEditExistingResolveCancel() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match1.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("9090")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect1.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("changed.host.com"));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testEditExistingResolveCancelScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("changed.host.com"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: match1.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: connect1.host.com:443")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("match1.host.com", items.get(0).getSourceAddress());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testEditExistingResolve() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match1.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("9090")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect1.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("edited.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("5050"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("edited.target.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("6060"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: edited.host.com:5050")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: edited.target.com:6060")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("edited.host.com", items.get(0).getSourceAddress());
        assertEquals(5050, items.get(0).getSourcePort());
        assertEquals("edited.target.com", items.get(0).getTargetAddress());
        assertEquals(6060, items.get(0).getTargetPort());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testEditExistingResolveScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(List.of(getFullResolve(1), getFullResolve(2)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("edited.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("5050"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("edited.target.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("6060"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: edited.host.com:5050")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: edited.target.com:6060")));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Match: match2.host.com:9090")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 1))).check(matches(withText("Connect-to: connect2.host.com:443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(2, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("edited.host.com", items.get(0).getSourceAddress());
        assertEquals(5050, items.get(0).getSourcePort());
        assertEquals("edited.target.com", items.get(0).getTargetAddress());
        assertEquals(6060, items.get(0).getTargetPort());
        assertEquals(1, items.get(1).getIndex());
        assertEquals("match2.host.com", items.get(1).getSourceAddress());
        activityScenario.close();
    }

    @Test
    public void testAddEditResolve() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("added.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: added.host.com:7070")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target.host.com:8443")));
        onView(allOf(withId(R.id.cardview_list_item_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("added.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("7070")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("target.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("8443")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("modified.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9999"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: modified.host.com:9999")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target.host.com:8443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("modified.host.com", items.get(0).getSourceAddress());
        assertEquals(9999, items.get(0).getSourcePort());
        assertEquals("target.host.com", items.get(0).getTargetAddress());
        assertEquals(8443, items.get(0).getTargetPort());
        activityScenario.close();
    }

    @Test
    public void testAddEditResolveScreenRotation() {
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class, getBypassSystemSAFBundle());
        showResolvesDialog(Collections.emptyList());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_resolves_add)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("added.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("7070"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("target.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("8443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_resolve), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("modified.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("9999"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_resolves_resolves)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_resolve_match), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Match: modified.host.com:9999")));
        onView(allOf(withId(R.id.textview_list_item_resolve_connect_to), withChildDescendantAtPosition(withId(R.id.listview_dialog_resolves_resolves), 0))).check(matches(withText("Connect-to: target.host.com:8443")));
        List<Resolve> items = getDialog().getAdapter().getAllItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getIndex());
        assertEquals("modified.host.com", items.get(0).getSourceAddress());
        assertEquals(9999, items.get(0).getSourcePort());
        assertEquals("target.host.com", items.get(0).getTargetAddress());
        assertEquals(8443, items.get(0).getTargetPort());
        activityScenario.close();
    }

    private void showResolvesDialog(List<Resolve> resolves) {
        ResolvesDialog resolvesDialog = new ResolvesDialog();
        Bundle bundle = BundleUtil.resolveListToBundle(resolvesDialog.getInitialResolvesKey(), resolves);
        BundleUtil.longToBundle(resolvesDialog.getNetworkTaskIdKey(), NETWORK_TASK_ID, bundle);
        BundleUtil.stringToBundle(resolvesDialog.getNetworkTaskURLKey(), NETWORK_TASK_URL, bundle);
        resolvesDialog.setArguments(bundle);
        resolvesDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ResolvesDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }

    private ResolvesDialog getDialog() {
        return (ResolvesDialog) getDialog(activityScenario, ResolvesDialog.class);
    }

    private Resolve getFullResolve(int number) {
        Resolve resolve = new Resolve(NETWORK_TASK_ID);
        resolve.setIndex(number - 1);
        resolve.setSourceAddress("match" + number + ".host.com");
        resolve.setSourcePort(9090);
        resolve.setTargetAddress("connect" + number + ".host.com");
        resolve.setTargetPort(443);
        return resolve;
    }
}
