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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
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
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class BasicAuthDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDefaultValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        BasicAuthDialog dialog = openBasicAuthDialog();
        onView(withId(R.id.textview_dialog_basic_auth_title)).check(matches(withText("Basic authentication")));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        assertEquals("", dialog.getUsername());
        assertEquals("", dialog.getPassword());
        assertEquals(":", dialog.getUsernameAndPassword());
        activityScenario.close();
    }

    @Test
    public void testDefaultValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        BasicAuthDialog dialog = openBasicAuthDialog();
        onView(withId(R.id.textview_dialog_basic_auth_title)).check(matches(withText("Basic authentication")));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_basic_auth_title)).check(matches(withText("Basic authentication")));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        assertEquals("", dialog.getUsername());
        assertEquals("", dialog.getPassword());
        assertEquals(":", dialog.getUsernameAndPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterValuesOk() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        BasicAuthDialog dialog = openBasicAuthDialog();
        onView(withId(R.id.textview_dialog_basic_auth_title)).check(matches(withText("Basic authentication")));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals("abc", dialog.getUsername());
        assertEquals("123", dialog.getPassword());
        assertEquals("abc:123", dialog.getUsernameAndPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterValuesOkScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.textview_dialog_basic_auth_title)).check(matches(withText("Basic authentication")));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("123"));
        rotateScreen(activityScenario);
        BasicAuthDialog dialog = getDialog();
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals("abc", dialog.getUsername());
        assertEquals("123", dialog.getPassword());
        assertEquals("abc:123", dialog.getUsernameAndPassword());
        activityScenario.close();
    }

    @Test
    public void testUsernameTextColor() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc:abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testUsernameTextColorScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc:abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc"));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withTextColor(R.color.textColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValuesInvalid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        BasicAuthDialog dialog = openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc:abc"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Username"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Password"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("abc"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals("", dialog.getUsername());
        assertEquals("abc", dialog.getPassword());
        assertEquals(":abc", dialog.getUsernameAndPassword());
        activityScenario.close();
    }

    @Test
    public void testValuesInvalidScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("abc:abc"));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Username"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Value contains invalid characters"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Password"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(replaceText("abc"));
        BasicAuthDialog dialog = getDialog();
        onView(withId(R.id.imageview_dialog_basic_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertEquals("", dialog.getUsername());
        assertEquals("abc", dialog.getPassword());
        assertEquals(":abc", dialog.getUsernameAndPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordToggle() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordToggleScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(true)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialUsernameAndPassword() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog("abc:123");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("abc")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("123")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        openBasicAuthDialog("");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        openBasicAuthDialog(":abc");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("abc")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        openBasicAuthDialog("abc:");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("abc")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialUsernameAndPasswordScreeRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog("abc:123");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("abc")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("123")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        rotateScreen(activityScenario);
        openBasicAuthDialog("");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        openBasicAuthDialog(":abc");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("abc")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        rotateScreen(activityScenario);
        openBasicAuthDialog("abc:");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("abc")));
        onView(withId(R.id.edittext_dialog_basic_auth_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        BasicAuthDialog dialog = openBasicAuthDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("333"));
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("333")));
        assertTrue(clipboardManager.hasData());
        assertEquals("333", clipboardManager.getData());
        clipboardManager.putData("aaa");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("aaa")));
        assertTrue(clipboardManager.hasData());
        assertEquals("aaa", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOptionScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openBasicAuthDialog();
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(replaceText("333"));
        rotateScreen(activityScenario);
        BasicAuthDialog dialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("333")));
        assertTrue(clipboardManager.hasData());
        assertEquals("333", clipboardManager.getData());
        clipboardManager.putData("aaa");
        onView(withId(R.id.edittext_dialog_basic_auth_username)).perform(longClick());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_basic_auth_username)).check(matches(withText("aaa")));
        assertTrue(clipboardManager.hasData());
        assertEquals("aaa", clipboardManager.getData());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_basic_auth_cancel)).perform(click());
        activityScenario.close();
    }

    private BasicAuthDialog openBasicAuthDialog() {
        return openBasicAuthDialog(null);
    }

    private BasicAuthDialog openBasicAuthDialog(String inititalUsernameAndPassword) {
        BasicAuthDialog basicAuthDialog = new BasicAuthDialog();
        if (inititalUsernameAndPassword != null) {
            Bundle bundle = BundleUtil.stringToBundle(basicAuthDialog.getInitialUsernameAndPasswordKey(), inititalUsernameAndPassword);
            basicAuthDialog.setArguments(bundle);
        }
        basicAuthDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ExportEncryptDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return basicAuthDialog;
    }

    private MockClipboardManager prepareMockClipboardManager(BasicAuthDialog basicAuthDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        basicAuthDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private BasicAuthDialog getDialog() {
        return (BasicAuthDialog) getDialog(activityScenario, BasicAuthDialog.class);
    }
}
