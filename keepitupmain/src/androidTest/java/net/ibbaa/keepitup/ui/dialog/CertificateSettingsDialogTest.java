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
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
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
public class CertificateSettingsDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDefaultArgumentValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.textview_dialog_certificate_settings_title)).check(matches(withText("Certificate settings")));
        onView(withId(R.id.textview_dialog_certificate_settings_allow_legacy_tls_label)).check(matches(withText("Allow legacy TLS")));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_certificate_settings_allow_legacy_tls_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_dialog_certificate_settings_ignore_ssl_error_label)).check(matches(withText("Ignore certificate errors")));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_certificate_settings_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_label)).check(matches(withText("Failure on expiry")));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_dialog_certificate_settings_expiry_days_label)).check(matches(withText("Expiry days")));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("30")));
        assertFalse(dialog.isAllowLegacyTLS());
        assertFalse(dialog.isIgnoreSSLError());
        assertFalse(dialog.isFailureOnCertificateExpiry());
        assertEquals(30, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultArgumentValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotChecked()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("30")));
        CertificateSettingsDialog dialog = getDialog();
        assertFalse(dialog.isAllowLegacyTLS());
        assertFalse(dialog.isIgnoreSSLError());
        assertFalse(dialog.isFailureOnCertificateExpiry());
        assertEquals(30, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCustomArgumentValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(true, true, true, 14, false);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_certificate_settings_allow_legacy_tls_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_certificate_settings_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isChecked()));
        onView(withId(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("14")));
        assertTrue(dialog.isAllowLegacyTLS());
        assertTrue(dialog.isIgnoreSSLError());
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(14, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCustomArgumentValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(true, true, true, 14, false);
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("14")));
        CertificateSettingsDialog dialog = getDialog();
        assertTrue(dialog.isAllowLegacyTLS());
        assertTrue(dialog.isIgnoreSSLError());
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(14, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGettersReturnToggledValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("90"));
        assertTrue(dialog.isAllowLegacyTLS());
        assertTrue(dialog.isIgnoreSSLError());
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(90, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGettersReturnToggledValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("90"));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("90")));
        CertificateSettingsDialog dialog = getDialog();
        assertTrue(dialog.isAllowLegacyTLS());
        assertTrue(dialog.isIgnoreSSLError());
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(90, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testSwitchYesNoText() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.textview_dialog_certificate_settings_allow_legacy_tls_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_dialog_certificate_settings_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.textview_dialog_certificate_settings_allow_legacy_tls_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.textview_dialog_certificate_settings_ignore_ssl_error_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        onView(withId(R.id.textview_dialog_certificate_settings_allow_legacy_tls_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.textview_dialog_certificate_settings_ignore_ssl_error_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.textview_dialog_certificate_settings_failure_on_certificate_expiry_on_off)).check(matches(withText("no")));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFieldDependenciesDisabledFlagOffNeverDisablesFields() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isEnabled()));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFieldDependenciesEnabledDisablesAndReenablesFields() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, true, 14, true);
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(14, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isEnabled()));
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(14, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testFieldDependenciesEnabledScreenRotationPreservesDisabledState() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, true, 14, true);
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("14")));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).check(matches(isEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isEnabled()));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAllowLegacyTLSToggleDoesNotAffectOtherFields() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, true, 14, true);
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        assertTrue(dialog.isAllowLegacyTLS());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        assertTrue(dialog.isFailureOnCertificateExpiry());
        assertEquals(14, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.switch_dialog_certificate_settings_allow_legacy_tls)).perform(click());
        assertFalse(dialog.isAllowLegacyTLS());
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).check(matches(isChecked()));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isNotEnabled()));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysFieldVisibilityFlagOffAlwaysVisible() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysFieldVisibilityFlagOnInitiallyHidden() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, true);
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysFieldVisibilityFlagOnInitiallyVisible() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, true, 14, true);
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysFieldVisibilityFlagOnTogglesWithSwitch() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, false, 45, true);
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        assertEquals(45, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("45")));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        assertEquals(45, dialog.getFailureOnCertificateExpiryDays());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysFieldVisibilityScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, true);
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).check(matches(isChecked()));
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("30")));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysColorValid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("1"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("3650"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysColorInvalid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("3651"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("30"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testExpiryDaysColorInvalidScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("30"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogInvalidDays() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Expiry days"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("30"));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogInvalidDaysScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Expiry days"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("30"));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogValidOkCloses() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationSkippedWhenFieldDisabled() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, true, 30, true);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.switch_dialog_certificate_settings_ignore_ssl_error)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(isNotEnabled()));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationSkippedWhenFieldHidden() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, true, 30, true);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.switch_dialog_certificate_settings_failure_on_certificate_expiry)).perform(click());
        onView(withId(R.id.linearlayout_dialog_certificate_settings_expiry_days)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationNotSkippedWhenFieldActiveDespiteFlag() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, true, 30, true);
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("0"));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("30"));
        onView(withId(R.id.imageview_dialog_certificate_settings_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, false, 30, false);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("14");
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("45"));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("45")));
        assertTrue(clipboardManager.hasData());
        assertEquals("45", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, false, 30, false);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("45");
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("45")));
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOptionScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        CertificateSettingsDialog dialog = openCertificateSettingsDialog(false, false, false, 30, false);
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("14");
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(replaceText("45"));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("14");
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_certificate_settings_expiry_days)).check(matches(withText("45")));
        assertTrue(clipboardManager.hasData());
        assertEquals("45", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCancel() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openCertificateSettingsDialog(false, false, false, 30, false);
        onView(withId(R.id.imageview_dialog_certificate_settings_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    private MockClipboardManager prepareMockClipboardManager(CertificateSettingsDialog dialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        dialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private CertificateSettingsDialog openCertificateSettingsDialog(boolean allowLegacyTLS, boolean ignoreSSLError, boolean failureOnCertificateExpiry, int failureOnCertificateExpiryDays, boolean fieldDependenciesEnabled) {
        CertificateSettingsDialog dialog = new CertificateSettingsDialog();
        Bundle arguments = new Bundle();
        BundleUtil.booleanToBundle(dialog.getAllowLegacyTLSKey(), allowLegacyTLS, arguments);
        BundleUtil.booleanToBundle(dialog.getIgnoreSSLErrorKey(), ignoreSSLError, arguments);
        BundleUtil.booleanToBundle(dialog.getFailureOnCertificateExpiryKey(), failureOnCertificateExpiry, arguments);
        BundleUtil.integerToBundle(dialog.getFailureOnCertificateExpiryDaysKey(), failureOnCertificateExpiryDays, arguments);
        BundleUtil.booleanToBundle(dialog.getFieldDependenciesEnabledKey(), fieldDependenciesEnabled, arguments);
        dialog.setArguments(arguments);
        dialog.show(getActivity(activityScenario).getSupportFragmentManager(), CertificateSettingsDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return dialog;
    }

    private CertificateSettingsDialog getDialog() {
        return (CertificateSettingsDialog) getDialog(activityScenario, CertificateSettingsDialog.class);
    }
}
