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
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class ExportEncryptDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDefaultValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testDefaultValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        assertTrue(getDialog().isEncrypt());
        assertEquals("", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    private ExportEncryptDialog openExportEncryptDialog() {
        ExportEncryptDialog exportEncryptDialog = new ExportEncryptDialog();
        exportEncryptDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ExportEncryptDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return exportEncryptDialog;
    }

    private ExportEncryptDialog getDialog() {
        return (ExportEncryptDialog) getDialog(activityScenario, ExportEncryptDialog.class);
    }
}
