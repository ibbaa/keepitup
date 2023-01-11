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

import android.widget.GridLayout;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.TestValidator1;
import net.ibbaa.keepitup.test.mock.TestValidator2;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SettingsInputDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testGetValue() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Collections.emptyList());
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals("abc", inputDialog.getValue());
    }

    @Test
    public void testValidation() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator2.class.getName(), TestValidator2.class.getName()));
        openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("field"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("testfailed1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("testfailed2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).check(matches(withGridLayoutPositionAndSpan(3, 1, GridLayout.CENTER, 0, 2, GridLayout.CENTER)));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("success"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testTextColor() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("failure"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("success"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
    }

    @Test
    public void testCancel() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testCopyPasteNoOption() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        prepareMockClipboardManager(inputDialog);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testCopyPasteNoOptionNumericIntegerData() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.CONNECTCOUNT, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testCopyPasteCancel() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testCopyPasteCancelScreenRotation() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
    }

    @Test
    public void testCopyOption() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("test", clipboardManager.getData());
    }

    @Test
    public void testCopyOptionScreenRotation() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        prepareMockClipboardManager(inputDialog);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog());
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("test", clipboardManager.getData());
    }

    @Test
    public void testPasteOption() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testPasteOptionScreenRotation() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        rotateScreen(activityScenario);
        MockClipboardManager clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("abc");
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testCopyPasteOption() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
        clipboardManager.putData("22");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("22")));
        assertTrue(clipboardManager.hasData());
        assertEquals("22", clipboardManager.getData());
    }

    @Test
    public void testCopyPasteOptionScreenRotation() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("11");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("22");
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("22")));
        assertTrue(clipboardManager.hasData());
        assertEquals("22", clipboardManager.getData());
    }

    @Test
    public void testStateSavedOnScreenRotation() {
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Collections.emptyList());
        SettingsInputDialog inputDialog = openSettingsInputDialog(input);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        inputDialog = (SettingsInputDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals("test", inputDialog.getValue());
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private SettingsInputDialog openSettingsInputDialog(SettingsInput input) {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SettingsInputDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return inputDialog;
    }

    private SettingsInputDialog getDialog() {
        return (SettingsInputDialog) getDialog(activityScenario, SettingsInputDialog.class);
    }
}
