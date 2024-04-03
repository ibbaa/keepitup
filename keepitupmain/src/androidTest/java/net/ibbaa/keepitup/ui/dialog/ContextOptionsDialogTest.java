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

package net.ibbaa.keepitup.ui.dialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestContextOptionsDialog;
import net.ibbaa.keepitup.test.mock.TestContextOptionsSupport;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ContextOptionsDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchRecyclerViewBaseActivity(NetworkTaskMainActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testDisplayOptions() {
        openContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        openContextOptionDialog(Collections.singletonList(ContextOption.COPY.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        openContextOptionDialog(Collections.emptyList());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(0)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testDisplayOptionsTwoOptionsScreenRotation() {
        openContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testDisplayOptionsOneOptionScreenRotation() {
        openContextOptionDialog(Collections.singletonList(ContextOption.COPY.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testCancel() {
        TestContextOptionsDialog contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertFalse(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testOptionClicked() {
        TestContextOptionsDialog contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.sourceResourceId());
        assertEquals(ContextOption.COPY, call.option());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        testContextOptionsSupport.reset();
        contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.sourceResourceId());
        assertEquals(ContextOption.PASTE, call.option());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testOptionClickedTwoOptionsScreenRotation() {
        openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsDialog contextOptionsDialog = getDialog();
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.sourceResourceId());
        assertEquals(ContextOption.COPY, call.option());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testOptionClickedOneOptionScreenRotation() {
        openTestContextOptionDialog(Collections.singletonList(ContextOption.PASTE.name()));
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsDialog contextOptionsDialog = getDialog();
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.sourceResourceId());
        assertEquals(ContextOption.PASTE, call.option());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @SuppressWarnings({"UnusedReturnValue"})
    private ContextOptionsDialog openContextOptionDialog(List<String> contextOptions) {
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog();
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), contextOptions);
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return contextOptionsDialog;
    }

    private TestContextOptionsDialog openTestContextOptionDialog(List<String> contextOptions) {
        TestContextOptionsDialog contextOptionsDialog = new TestContextOptionsDialog();
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), contextOptions);
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return contextOptionsDialog;
    }

    private TestContextOptionsDialog getDialog() {
        return (TestContextOptionsDialog) getDialog(activityScenario, TestContextOptionsDialog.class);
    }
}
