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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@SuppressWarnings({"SequencedCollectionMethodCanBeUsed"})
@RunWith(AndroidJUnit4.class)
public class GlobalHeadersEditDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testEmptyNameAndValue() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(new Header());
        onView(withId(R.id.textview_dialog_global_header_edit_title)).check(matches(withText("HTTP Header")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("")));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("", header.getName());
        assertEquals("", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEmptyNameAndValueScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(new Header());
        onView(withId(R.id.textview_dialog_global_header_edit_title)).check(matches(withText("HTTP Header")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("")));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("", header.getName());
        assertEquals("", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("", header.getName());
        assertEquals("", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.textview_dialog_global_header_edit_title)).check(matches(withText("HTTP Header")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("", header.getName());
        assertEquals("", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEditNameAndValue() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(new Header());
        onView(withId(R.id.textview_dialog_global_header_edit_title)).check(matches(withText("HTTP Header")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("name"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("value"));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEditNameAndValueScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(new Header());
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("name"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("value"));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("name")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("value")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("name")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("value")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("name", header.getName());
        assertEquals("value", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNameAndValueSet() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(getHeader(1));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name1", header.getName());
        assertEquals("Value1", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNameAndValueSetScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(getHeader(1));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name1", header.getName());
        assertEquals("Value1", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name1", header.getName());
        assertEquals("Value1", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name1", header.getName());
        assertEquals("Value1", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEditNameAndValueSet() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(getHeader(1));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("Name2"));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name2", header.getName());
        assertEquals("Name2", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEditNameAndValueSetScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(getHeader(1));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("Value2"));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name2", header.getName());
        assertEquals("Value2", header.getValue());
        rotateScreen(activityScenario);
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name2")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value2")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name2", header.getName());
        assertEquals("Value2", header.getValue());
        dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name2")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value2")));
        header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("Name2", header.getName());
        assertEquals("Value2", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEditNameAndValueErrorColor() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        GlobalHeaderEditDialog dialog = openGlobalHeaderEditDialog(getHeader(1));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789123456789012345678901234567890"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("Name\tTest"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("Test\u007FMore"));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("NameTest"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("TestMore"));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("NameTest", header.getName());
        assertEquals("TestMore", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEditNameAndValueErrorColorScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openGlobalHeaderEditDialog(getHeader(1));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withText("Name1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withText("Value1")));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789123456789012345678901234567890"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("Name2"));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("Name\tTest"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("Test\u007FMore"));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).perform(replaceText("NameTest"));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).perform(replaceText("TestMore"));
        onView(withId(R.id.edittext_dialog_global_header_edit_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_global_header_edit_value)).check(matches(withTextColor(R.color.textColor)));
        GlobalHeaderEditDialog dialog = (GlobalHeaderEditDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        Header header = dialog.getHeader();
        assertEquals(-1, header.getId());
        assertEquals(-1, header.getNetworkTaskId());
        assertEquals("NameTest", header.getName());
        assertEquals("TestMore", header.getValue());
        onView(withId(R.id.imageview_dialog_global_header_edit_cancel)).perform(click());
        activityScenario.close();
    }

    private GlobalHeaderEditDialog openGlobalHeaderEditDialog(Header header) {
        GlobalHeaderEditDialog globalHeaderEditDialog = new GlobalHeaderEditDialog();
        Bundle bundle = BundleUtil.bundleToBundle(globalHeaderEditDialog.getHeaderKey(), header.toBundle());
        bundle = BundleUtil.integerToBundle(globalHeaderEditDialog.getPositionKey(), -1, bundle);
        globalHeaderEditDialog.setArguments(bundle);
        globalHeaderEditDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GlobalHeaderEditDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return globalHeaderEditDialog;
    }

    private Header getHeader(int number) {
        Header header = new Header();
        header.setNetworkTaskId(-1);
        header.setName("Name" + number);
        header.setValue("Value" + number);
        return header;
    }
}
