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
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class PasswordInputDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDefaultValue() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        assertEquals("", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testDefaultValueScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        rotateScreen(activityScenario);
        assertEquals("", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterPassword() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(replaceText("12345678"));
        onView(withId(R.id.imageview_dialog_password_input_ok)).perform(click());
        assertEquals("12345678", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterPasswordScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(replaceText("12345678"));
        rotateScreen(activityScenario);
        assertEquals("12345678", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_password_input_ok)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testEnterPasswordNoValue() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_password_input_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        assertEquals("", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterPasswordNoValueScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(replaceText(""));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_password_input_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        assertEquals("", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterPasswordMaxLength() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.imageview_dialog_password_input_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testEnterPasswordMaxLengthScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        PasswordInputDialog dialog = openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.textview_dialog_password_input_title)).check(matches(withText("Enter password")));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.imageview_dialog_password_input_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordToggle() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordToggleScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openPasswordInputDialog();
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_password_input_password)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_password_input_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_password_input_cancel)).perform(click());
        activityScenario.close();
    }

    private PasswordInputDialog openPasswordInputDialog() {
        PasswordInputDialog passwordInputDialog = new PasswordInputDialog();
        passwordInputDialog.show(getActivity(activityScenario).getSupportFragmentManager(), PasswordInputDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return passwordInputDialog;
    }

    private PasswordInputDialog getDialog() {
        return (PasswordInputDialog) getDialog(activityScenario, PasswordInputDialog.class);
    }
}
