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
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ResolveEditDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testEmptyFields() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(withId(R.id.textview_dialog_resolve_edit_title)).check(matches(withText("Connection configuration")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("not set")));
        Resolve resolve = dialog.getResolve();
        assertEquals("", resolve.getSourceAddress());
        assertEquals(-1, resolve.getSourcePort());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEmptyFieldsScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("not set")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("not set")));
        rotateScreen(activityScenario);
        ResolveEditDialog dialog = getDialog();
        Resolve resolve = dialog.getResolve();
        assertEquals("", resolve.getSourceAddress());
        assertEquals(-1, resolve.getSourcePort());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFieldsSet() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(getResolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("8080")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        Resolve resolve = dialog.getResolve();
        assertEquals("match.host.com", resolve.getSourceAddress());
        assertEquals(8080, resolve.getSourcePort());
        assertEquals("connect.host.com", resolve.getTargetAddress());
        assertEquals(443, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFieldsSetScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(getResolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("8080")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("8080")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        rotateScreen(activityScenario);
        ResolveEditDialog dialog = getDialog();
        Resolve resolve = dialog.getResolve();
        assertEquals("match.host.com", resolve.getSourceAddress());
        assertEquals(8080, resolve.getSourcePort());
        assertEquals("connect.host.com", resolve.getTargetAddress());
        assertEquals(443, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNotSetHandling() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("not set")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("not set")));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNotSetHandlingWithValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("8080")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGetResolveEmptyAndNotSet() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText(""));
        Resolve resolve = dialog.getResolve();
        assertEquals("", resolve.getSourceAddress());
        assertEquals(-1, resolve.getSourcePort());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("not set"));
        resolve = dialog.getResolve();
        assertEquals("", resolve.getSourceAddress());
        assertEquals(-1, resolve.getSourcePort());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGetResolveWithValuesHostTrimmed() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("   match.host.com   "));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("   connect.host.com   "));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        Resolve resolve = dialog.getResolve();
        assertEquals("match.host.com", resolve.getSourceAddress());
        assertEquals(8080, resolve.getSourcePort());
        assertEquals("connect.host.com", resolve.getTargetAddress());
        assertEquals(443, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGetResolveWithValuesHostTrimmedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("   match.host.com   "));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("   connect.host.com   "));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        rotateScreen(activityScenario);
        ResolveEditDialog dialog = getDialog();
        Resolve resolve = dialog.getResolve();
        assertEquals("match.host.com", resolve.getSourceAddress());
        assertEquals(8080, resolve.getSourcePort());
        assertEquals("connect.host.com", resolve.getTargetAddress());
        assertEquals(443, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGetResolvePreservesIdAndNetworktaskId() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        Resolve initial = getResolve();
        initial.setId(7);
        initial.setNetworkTaskId(3);
        ResolveEditDialog dialog = openResolveEditDialog(initial);
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("new.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("22"));
        Resolve resolve = dialog.getResolve();
        assertEquals(7, resolve.getId());
        assertEquals(3, resolve.getNetworkTaskId());
        assertEquals("new.host.com", resolve.getSourceAddress());
        assertEquals(22, resolve.getSourcePort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGetResolvePreservesIdAndNetworktaskIdScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        Resolve initial = getResolve();
        initial.setId(7);
        initial.setNetworkTaskId(3);
        openResolveEditDialog(initial);
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("new.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("22"));
        rotateScreen(activityScenario);
        ResolveEditDialog dialog = getDialog();
        Resolve resolve = dialog.getResolve();
        assertEquals(7, resolve.getId());
        assertEquals(3, resolve.getNetworkTaskId());
        assertEquals("new.host.com", resolve.getSourceAddress());
        assertEquals(22, resolve.getSourcePort());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInputErrorColor() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("not valid host"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("not valid host"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialog() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("not valid host"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("not valid host"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("-1"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Match host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Match port"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 65535"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Connect-to host"), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 0"), withGridLayoutPosition(4, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openResolveEditDialog(new Resolve());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("not valid host"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("99999"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("not valid host"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("-1"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Match host"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Match port"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum: 65535"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Connect-to host"), withGridLayoutPosition(3, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No valid host or IP address"), withGridLayoutPosition(3, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Connect-to port"), withGridLayoutPosition(4, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum: 0"), withGridLayoutPosition(4, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoValueAllowed() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText(""));
        Resolve resolve = dialog.getResolve();
        assertEquals("", resolve.getSourceAddress());
        assertEquals(-1, resolve.getSourcePort());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        dialog = openResolveEditDialog(new Resolve());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("not set"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("not set"));
        resolve = dialog.getResolve();
        assertEquals("", resolve.getSourceAddress());
        assertEquals(-1, resolve.getSourcePort());
        assertEquals("", resolve.getTargetAddress());
        assertEquals(-1, resolve.getTargetPort());
        onView(withId(R.id.imageview_dialog_resolve_edit_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        assertTrue(clipboardManager.hasData());
        assertEquals("match.host.com", clipboardManager.getData());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(replaceText("8080"));
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("8080")));
        assertTrue(clipboardManager.hasData());
        assertEquals("8080", clipboardManager.getData());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect.host.com")));
        assertTrue(clipboardManager.hasData());
        assertEquals("connect.host.com", clipboardManager.getData());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(replaceText("443"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("443")));
        assertTrue(clipboardManager.hasData());
        assertEquals("443", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testHostPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("match.host.com");
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("match.host.com")));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPortPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("8080");
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_port)).check(matches(withText("8080")));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(click());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_port)).check(matches(withText("8080")));
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOptionScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ResolveEditDialog dialog = openResolveEditDialog(new Resolve());
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(replaceText("match.host.com"));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_match_host)).check(matches(withText("match.host.com")));
        assertTrue(clipboardManager.hasData());
        assertEquals("match.host.com", clipboardManager.getData());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(replaceText("connect.host.com"));
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_resolve_edit_connect_to_host)).check(matches(withText("connect.host.com")));
        assertTrue(clipboardManager.hasData());
        assertEquals("connect.host.com", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_resolve_edit_cancel)).perform(click());
        activityScenario.close();
    }

    private MockClipboardManager prepareMockClipboardManager(ResolveEditDialog dialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        dialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private ResolveEditDialog openResolveEditDialog(Resolve resolve) {
        ResolveEditDialog resolveEditDialog = new ResolveEditDialog();
        Bundle bundle = BundleUtil.bundleToBundle(resolveEditDialog.getResolveKey(), resolve.toBundle());
        bundle = BundleUtil.integerToBundle(resolveEditDialog.getPositionKey(), 0, bundle);
        resolveEditDialog.setArguments(bundle);
        resolveEditDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ResolveEditDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return resolveEditDialog;
    }

    private ResolveEditDialog getDialog() {
        return (ResolveEditDialog) getDialog(activityScenario, ResolveEditDialog.class);
    }

    private Resolve getResolve() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setIndex(1);
        resolve.setNetworkTaskId(1);
        resolve.setSourceAddress("match.host.com");
        resolve.setSourcePort(8080);
        resolve.setTargetAddress("connect.host.com");
        resolve.setTargetPort(443);
        return resolve;
    }
}
