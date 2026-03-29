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
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.validation.CredentialInfo;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

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

    @Test
    public void testNoEncryption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoEncryptionScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoEncryptionWithCredentials() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CredentialInfo credentialInfo1 = new CredentialInfo("name1", "value1");
        CredentialInfo credentialInfo2 = new CredentialInfo("name2", "value2");
        openExportEncryptDialog(List.of(credentialInfo1, credentialInfo2));
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        onView(withId(R.id.textview_dialog_credential_info_title)).check(matches(withText("Credentials not exported")));
        onView(withId(R.id.textview_dialog_credential_info_message)).check(matches(withText(startsWith("Confidential data"))));
        onView(allOf(withText("name1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("value1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("name2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("value2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_credential_info_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoEncryptionWithCredentialsScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CredentialInfo credentialInfo1 = new CredentialInfo("name1", "value1");
        CredentialInfo credentialInfo2 = new CredentialInfo("name2", "value2");
        openExportEncryptDialog(List.of(credentialInfo1, credentialInfo2));
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_credential_info_title)).check(matches(withText("Credentials not exported")));
        onView(withId(R.id.textview_dialog_credential_info_message)).check(matches(withText(startsWith("Confidential data"))));
        onView(allOf(withText("name1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("value1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("name2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("value2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_credential_info_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(isDisplayed()));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testToggleEncryption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withText("12345678")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("12345678")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testToggleEncryptionScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withText("12345678")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("12345678")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testDefaultValuesOk() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testDefaultValuesOkScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.textview_dialog_export_encrypt_title)).check(matches(withText("Encryption")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(withText("Encrypt file (recommended)")));
        onView(withId(R.id.checkbox_dialog_export_encrypt_encrypt)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(not(isDisplayed())));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertTrue(getDialog().isEncrypt());
        assertEquals("", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordMinLength() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Minimum length: 8")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum length: 8"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("123", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordMinLengthScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Minimum length: 8")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum length: 8"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        rotateScreen(activityScenario);
        assertTrue(getDialog().isEncrypt());
        assertEquals("123", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordMinLengthMatch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Minimum length: 8")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum length: 8"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("123", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordMinLengthMatchScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Minimum length: 8")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum length: 8"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertTrue(getDialog().isEncrypt());
        assertEquals("123", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordMinLengthNoMatch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Minimum length: 8")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("456"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum length: 8"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Confirm password"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Values do not match"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("123", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordMinLengthNoMatchScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Minimum length: 8")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("456"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Minimum length: 8"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Confirm password"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Values do not match"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        rotateScreen(activityScenario);
        assertTrue(getDialog().isEncrypt());
        assertEquals("123", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordMaxLength() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Maximum length: 128")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordMaxLengthScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Maximum length: 128")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(not(isDisplayed())));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        rotateScreen(activityScenario);
        assertTrue(getDialog().isEncrypt());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordMaxLengthMatch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Maximum length: 128")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordMaxLengthMatchScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Maximum length: 128")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertTrue(getDialog().isEncrypt());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordMaxLengthNoMatch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Maximum length: 128")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Confirm password"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Values do not match"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordMaxLengthNoMatchScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Maximum length: 128")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("456"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 128"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Confirm password"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Values do not match"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        rotateScreen(activityScenario);
        assertTrue(getDialog().isEncrypt());
        assertEquals("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordOkMatch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertTrue(dialog.isEncrypt());
        assertEquals("12345678", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordOkMatchScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        assertTrue(getDialog().isEncrypt());
        assertEquals("12345678", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testPasswordNoMatch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123456789"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Confirm password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Values do not match"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        assertTrue(dialog.isEncrypt());
        assertEquals("12345678", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testPasswordNoMatchScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123456789"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Confirm password"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Values do not match"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertTrue(getDialog().isEncrypt());
        assertEquals("12345678", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testConfirmPasswordFirst() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertTrue(dialog.isEncrypt());
        assertEquals("12345678", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testConfirmPasswordFirstScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        rotateScreen(activityScenario);
        onView(isRoot()).perform(waitFor(500));
        assertTrue(getDialog().isEncrypt());
        assertEquals("12345678", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testConfirmPasswordFirstEdit() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        ExportEncryptDialog dialog = openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123456789"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123456789"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        assertTrue(dialog.isEncrypt());
        assertEquals("123456789", dialog.getPassword());
        activityScenario.close();
    }

    @Test
    public void testConfirmPasswordFirstEditScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("12345678"));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withText("Password OK")));
        onView(withId(R.id.textview_dialog_export_encrypt_password)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password_confirm)).perform(replaceText("123456789"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does not match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(replaceText("123456789"));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withText("Password does match")));
        onView(withId(R.id.textview_dialog_export_encrypt_password_confirm)).check(matches(withTextColor(R.color.textOkColor)));
        onView(isRoot()).perform(waitFor(500));
        assertTrue(getDialog().isEncrypt());
        assertEquals("123456789", getDialog().getPassword());
        onView(withId(R.id.imageview_dialog_export_encrypt_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testPasswordToggle() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasswordToggleScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openExportEncryptDialog();
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_export_encrypt_password)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_export_encrypt_cancel)).perform(click());
        activityScenario.close();
    }

    private ExportEncryptDialog openExportEncryptDialog() {
        return openExportEncryptDialog(Collections.emptyList());
    }

    private ExportEncryptDialog openExportEncryptDialog(List<CredentialInfo> credentials) {
        ExportEncryptDialog exportEncryptDialog = new ExportEncryptDialog();
        if (!credentials.isEmpty()) {
            Bundle bundle = BundleUtil.credentialInfoListToBundle(exportEncryptDialog.getCredentialInfoBaseKey(), credentials);
            exportEncryptDialog.setArguments(bundle);
        }
        exportEncryptDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ExportEncryptDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return exportEncryptDialog;
    }

    private ExportEncryptDialog getDialog() {
        return (ExportEncryptDialog) getDialog(activityScenario, ExportEncryptDialog.class);
    }
}
