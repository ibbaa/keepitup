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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.HeaderType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.DefaultsActivity;
import net.ibbaa.keepitup.ui.sync.HeaderSyncHandler;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class HeadersDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDialogConfiguration() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(Collections.emptyList(), 1, "Custom title", false);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Custom title")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogConfigurationScreenRotation() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(Collections.emptyList(), 1, "Custom title", false);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Custom title")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        rotateScreen(activityScenario);
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Custom title")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        rotateScreen(activityScenario);
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogConfigurationWithRestoreAndHeaders() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 2, "Title", true);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Title")));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        assertEquals(2, dialog.getNetworkTaskId());
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testDialogConfigurationWithRestoreAndHeadersScreenRotation() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 2, "Title", true);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Title")));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        assertEquals(2, dialog.getNetworkTaskId());
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        assertEquals(2, dialog.getNetworkTaskId());
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testRestoreDefaultHeadersCancel() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 1, "Custom title", true);
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_headers_restore)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Restore headers?")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This overwrites the current headers with the default headers.")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testRestoreDefaultHeadersCancelScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 1, "Custom title", true);
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_headers_restore)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Restore headers?")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This overwrites the current headers with the default headers.")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testRestoreDefaultHeaders() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 1, "Custom title", true);
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_headers_restore)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Restore headers?")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This overwrites the current headers with the default headers.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }

    @Test
    public void testRestoreDefaultHeadersScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 1, "Custom title", true);
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_headers_restore)).check(matches(withText("Restore default headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_headers_restore)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Restore headers?")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("This overwrites the current headers with the default headers.")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        assertEquals(1, dialog.getNetworkTaskId());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        activityScenario.close();
    }


    @Test
    public void testBasicAuthHeaderAddAndOpenWithoutPasswordChange() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        HeadersDialog dialog = showHeadersDialog(List.of(getHeader(1), getHeader(2)), 1, "Custom title", true);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        Header header = dialog.getAdapter().getItem(0);
        assertEquals("Authorization", header.getName());
        assertEquals("abc:123", header.getValue());
        assertEquals(HeaderType.BASICAUTH, header.getHeaderType());
        activityScenario.close();
    }

    @Test
    public void testHeaderColor() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        Header header1 = getHeader(1);
        Header header2 = getHeader(2);
        header2.setValueValid(false);
        Header header3 = getHeader(3);
        header3.setValueValid(false);
        showHeadersDialog(List.of(header1, header2, header3), 1, "Custom title", true);
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name4"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value4"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name5"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value5"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value5")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name6"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value6"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value5")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name6")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value6")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertTrue(getDialog().getAdapter().getItem(0).isValueValid());
        assertTrue(getDialog().getAdapter().getItem(1).isValueValid());
        assertTrue(getDialog().getAdapter().getItem(2).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHeaderColorAuthorizationHeader() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        Header header1 = getHeader(1);
        header1.setHeaderType(HeaderType.GENERICAUTH);
        header1.setName("Authorization");
        header1.setValue("Value");
        header1.setValueValid(false);
        showHeadersDialog(List.of(header1), 1, "Custom title", true);
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertTrue(getDialog().getAdapter().getItem(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testHeaderColorAuthorizationHeaderScreenRotation() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        Header header1 = getHeader(1);
        header1.setHeaderType(HeaderType.GENERICAUTH);
        header1.setName("Authorization");
        header1.setValue("Value");
        header1.setValueValid(false);
        showHeadersDialog(List.of(header1), 1, "Custom title", true);
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertTrue(getDialog().getAdapter().getItem(0).isValueValid());
        activityScenario.close();
    }

    @Test
    public void testOpenCloseDialog() {
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_headers_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoHeaders() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Global headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNoHeadersScreenRotation() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Global headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Global headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Global headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeader() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.textview_dialog_headers_label)).check(matches(withText("Global headers")));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFiveHeaders() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Value5")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFiveHeadersScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        getHeaderDAO().insertHeader(getHeader(4));
        getHeaderDAO().insertHeader(getHeader(5));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Value5")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Value5")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Name5")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withText("Value5")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 4))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneHeaderEllipsized() {
        Header header = getHeader(1);
        header.setValue("122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890122345689012234568901223456890");
        getHeaderDAO().insertHeader(header);
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(allOf(withGridLayoutRowColumnPosition(1, 0), isDescendantOfA(withId(R.id.gridlayout_activity_defaults_global_headers_value)))).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isEllipsized()));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderDeleteCancel() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderSwipeDeleteCancel() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderDeleteCancelScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderSwipeDeleteCancelScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderDelete() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderSwipeDelete() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderDeleteScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderSwipeDeleteScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Really delete?")));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("No headers defined")));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersDelete() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        header1 = getDialog().getAdapter().getItem(0);
        header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name3", header2.getName());
        assertEquals("Value3", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersSwipeDelete() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        header1 = getDialog().getAdapter().getItem(0);
        header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name3", header2.getName());
        assertEquals("Value3", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersDeleteScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        header1 = getDialog().getAdapter().getItem(0);
        header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name3", header2.getName());
        assertEquals("Value3", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersSwipeDeleteRotateScreen() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(1)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        header1 = getDialog().getAdapter().getItem(0);
        header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name3", header2.getName());
        assertEquals("Value3", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(0)).perform(ViewActions.swipeRight());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderAddCancel() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderAddCancelScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderAddOk() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("AName", header1.getName());
        assertEquals("AValue", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("User-Agent", header2.getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderAddOkScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("AName", header1.getName());
        assertEquals("AValue", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("User-Agent", header2.getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddCancel() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddCancelScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddOk() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(4));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name3"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value3"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        Header header4 = getDialog().getAdapter().getItem(3);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        assertEquals("Name4", header4.getName());
        assertEquals("Value4", header4.getValue());
        assertEquals(HeaderType.GENERIC, header4.getHeaderType());
        assertTrue(header4.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddOkScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(4));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name3"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value3"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Name4")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Value4")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        Header header4 = getDialog().getAdapter().getItem(3);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        assertEquals("Name4", header4.getName());
        assertEquals("Value4", header4.getValue());
        assertEquals(HeaderType.GENERIC, header4.getHeaderType());
        assertTrue(header4.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAddBasicAuth() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("User-Agent", header2.getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAddBasicAuthScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("User-Agent", header2.getName());
        assertEquals("Mozilla/5.0 (Linux; Android) KeepItUp/-", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddAndDelete() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(2)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name3"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value3"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddAndDeleteScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(2)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name3"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value3"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddAndDeleteWithBasicAuth() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(2)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name1", header2.getName());
        assertEquals("Value1", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name2", header3.getName());
        assertEquals("Value2", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddAndDeleteWithBasicAuthScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withRecyclerView(R.id.listview_dialog_headers_headers).atPosition(2)).perform(ViewActions.swipeRight());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name1", header2.getName());
        assertEquals("Value1", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name2", header3.getName());
        assertEquals("Value2", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersAddBasicAuthAlreadyExists() {
        Header header = getHeader(1);
        header.setName("Authorization");
        header.setValue("ABC");
        header.setHeaderType(HeaderType.GENERICAUTH);
        header.setValueValid(true);
        getHeaderDAO().insertHeader(header);
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value already exists"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        header = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header.getName());
        assertEquals("ABC", header.getValue());
        assertEquals(HeaderType.GENERICAUTH, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNoHeadersAddValidationFailed() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name\nName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Name\nName"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Header value"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("success"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("success"));
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("success")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("success")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header = getDialog().getAdapter().getItem(0);
        assertEquals("success", header.getName());
        assertEquals("success", header.getValue());
        assertEquals(HeaderType.GENERIC, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNoHeadersAddValidationFailedScreenRotation() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name\nName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Name\nName"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Header value"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("success"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("success"));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("success")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("success")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header = getDialog().getAdapter().getItem(0);
        assertEquals("success", header.getName());
        assertEquals("success", header.getValue());
        assertEquals(HeaderType.GENERIC, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNoHeadersAddValidationFailedHeaderExists() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value2"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value already exists"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Success"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Success"));
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 3))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        Header header4 = getDialog().getAdapter().getItem(3);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name2", header2.getName());
        assertEquals("Value2", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        assertEquals("Success", header4.getName());
        assertEquals("Success", header4.getValue());
        assertEquals(HeaderType.GENERIC, header4.getHeaderType());
        assertTrue(header4.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderOpenCancel() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderOpenCancelScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("User-Agent")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Mozilla/5.0 (Linux; Android) KeepItUp/-")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderOpenOk() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header = getDialog().getAdapter().getItem(0);
        assertEquals("Name", header.getName());
        assertEquals("Value", header.getValue());
        assertEquals(HeaderType.GENERIC, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderOpenOkScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header = getDialog().getAdapter().getItem(0);
        assertEquals("Name", header.getName());
        assertEquals("Value", header.getValue());
        assertEquals(HeaderType.GENERIC, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderOpenBasicAuth() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header.getName());
        assertEquals("abc:123", header.getValue());
        assertEquals(HeaderType.BASICAUTH, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultHeaderOpenBasicAuthScreenRotation() {
        addDefaultHeader();
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header.getName());
        assertEquals("abc:123", header.getValue());
        assertEquals(HeaderType.BASICAUTH, header.getHeaderType());
        assertTrue(header.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenCancel() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenCancelScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name2")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value2")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenOk() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("AName", header1.getName());
        assertEquals("AValue", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name1", header2.getName());
        assertEquals("Value1", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenOkScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("AName"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("AValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AName")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("AValue")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        Header header3 = getDialog().getAdapter().getItem(2);
        assertEquals("AName", header1.getName());
        assertEquals("AValue", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name1", header2.getName());
        assertEquals("Value1", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        assertEquals("Name3", header3.getName());
        assertEquals("Value3", header3.getValue());
        assertEquals(HeaderType.GENERIC, header3.getHeaderType());
        assertTrue(header3.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTwoHeadersOpenValidationFailed() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789123456789012345678901234567890"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value\nValue"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Header value"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Success"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Success"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Success", header2.getName());
        assertEquals("Success", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTwoHeadersOpenValidationFailedScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789123456789012345678901234567890"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value\nValue"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Header value"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Success"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Success"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Success", header2.getName());
        assertEquals("Success", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTwoHeadersOpenValidationFailedHeaderExists() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name1"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value1"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(allOf(withText("Header name"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value already exists"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Success"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Success"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Success")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Success", header2.getName());
        assertEquals("Success", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAddAuthorizationHeader() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAddAuthorizationHeaderScreenRotation() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOpenAuthorizationHeader() {
        getHeaderDAO().insertHeader(getHeader(1));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOpenAuthorizationHeaderScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenAndDelete() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name3"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value3"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name3", header2.getName());
        assertEquals("Value3", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenAndDeleteScreenRotation() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Name3"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value3"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name3")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value3")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Name1", header1.getName());
        assertEquals("Value1", header1.getValue());
        assertEquals(HeaderType.GENERIC, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name3", header2.getName());
        assertEquals("Value3", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeHeadersOpenAndDeleteWithBasicAuth() {
        getHeaderDAO().insertHeader(getHeader(1));
        getHeaderDAO().insertHeader(getHeader(2));
        getHeaderDAO().insertHeader(getHeader(3));
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.imageview_list_item_header_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 2))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_headers_headers)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_header_no_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("Authorization")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withText("************")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Name1")));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withText("Value1")));
        onView(allOf(withId(R.id.textview_list_item_header_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        onView(allOf(withId(R.id.textview_list_item_header_value), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 1))).check(matches(withTextColor(R.color.textColor)));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        Header header2 = getDialog().getAdapter().getItem(1);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        assertEquals("Name1", header2.getName());
        assertEquals("Value1", header2.getValue());
        assertEquals(HeaderType.GENERIC, header2.getHeaderType());
        assertTrue(header2.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testShowConfirmAuthorizationHeaderNotice() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(3, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value2"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value2", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testShowConfirmAuthorizationHeaderNoticeScreenRotation() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_name)).perform(replaceText("Authorization"));
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(3, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value2"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value2", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testShowConfirmAuthorizationHeaderNoticeBasicAuth() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(3, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testShowConfirmAuthorizationHeaderNoticeBasicAuthScreenRotation() {
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(withId(R.id.imageview_dialog_headers_add)).perform(click());
        onView(withId(R.id.checkbox_dialog_header_edit_basic_auth)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(3, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Confirm security notice")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText(containsString("Authorization headers often include credentials"))));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("abc:123", header1.getValue());
        assertEquals(HeaderType.BASICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testShowConfirmAuthorizationHeaderNoticeNotShown() {
        Header header = getHeader(1);
        header.setName("Authorization");
        header.setValue("Value");
        header.setHeaderType(HeaderType.GENERICAUTH);
        getHeaderDAO().insertHeader(header);
        resetGlobalHeaderHandler();
        activityScenario = launchSettingsInputActivity(DefaultsActivity.class, getBypassSystemSAFBundle());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(scrollTo());
        onView(withId(R.id.cardview_activity_defaults_global_headers)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_header), withChildDescendantAtPosition(withId(R.id.listview_dialog_headers_headers), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_header_edit_value)).perform(replaceText("Value2"));
        onView(withId(R.id.imageview_dialog_header_edit_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        Header header1 = getDialog().getAdapter().getItem(0);
        assertEquals("Authorization", header1.getName());
        assertEquals("Value2", header1.getValue());
        assertEquals(HeaderType.GENERICAUTH, header1.getHeaderType());
        assertTrue(header1.isValueValid());
        onView(withId(R.id.imageview_dialog_headers_cancel)).perform(click());
        activityScenario.close();
    }

    private HeadersDialog showHeadersDialog(List<Header> headers, long networkTaskId, String title, boolean supportsRestoreD) {
        HeadersDialog headersDialog = new HeadersDialog();
        Bundle bundle = BundleUtil.headerListToBundle(headersDialog.getInitialHeadersKey(), headers);
        BundleUtil.longToBundle(headersDialog.getNetworkTaskIdKey(), networkTaskId, bundle);
        BundleUtil.stringToBundle(headersDialog.getHeadersTitleKey(), title, bundle);
        BundleUtil.booleanToBundle(headersDialog.getSupportsRestoreDefaultHeadersKey(), supportsRestoreD, bundle);
        headersDialog.setArguments(bundle);
        headersDialog.show(getActivity(activityScenario).getSupportFragmentManager(), HeadersDialog.class.getName());
        return headersDialog;
    }

    private HeadersDialog getDialog() {
        return (HeadersDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private void resetGlobalHeaderHandler() {
        HeaderSyncHandler handler = new HeaderSyncHandler(TestRegistry.getContext());
        handler.reset();
    }

    private void addDefaultHeader() {
        DBSetup dbSetup = new DBSetup(TestRegistry.getContext());
        dbSetup.initializeHeaderTable();
    }

    private Header getHeader(int number) {
        Header header = new Header();
        header.setNetworkTaskId(-1);
        header.setHeaderType(HeaderType.GENERIC);
        header.setName("Name" + number);
        header.setValue("Value" + number);
        header.setValueValid(true);
        return header;
    }
}
