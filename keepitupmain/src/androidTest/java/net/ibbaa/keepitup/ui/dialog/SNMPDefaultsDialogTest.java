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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.model.SNMPTransport;
import net.ibbaa.keepitup.model.SNMPVersion;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SNMPDefaultsDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDefaultValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.textview_dialog_snmp_defaults_title)).check(matches(withText("SNMP settings")));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isNotChecked()));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("161")));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isNotChecked()));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("MD5")));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-128")));
        assertEquals(SNMPVersion.V2C, dialog.getSNMPVersion());
        assertEquals(161, dialog.getSNMPPort());
        assertEquals(SNMPTransport.UDP, dialog.getSNMPTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES128, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isNotChecked()));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("161")));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isNotChecked()));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("MD5")));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-128")));
        rotateScreen(activityScenario);
        SNMPDefaultsDialog dialog = getDialog();
        assertEquals(SNMPVersion.V2C, dialog.getSNMPVersion());
        assertEquals(161, dialog.getSNMPPort());
        assertEquals(SNMPTransport.UDP, dialog.getSNMPTransport());
        assertEquals(SNMPAuthAlgorithm.MD5, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES128, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCustomPreferenceValues() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceSNMPVersion(SNMPVersion.V3);
        preferenceManager.setPreferenceSNMPPort(162);
        preferenceManager.setPreferenceSNMPTransport(SNMPTransport.TCP);
        preferenceManager.setPreferenceSNMPAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        preferenceManager.setPreferenceSNMPPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("162")));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-256")));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-256")));
        assertEquals(SNMPVersion.V3, dialog.getSNMPVersion());
        assertEquals(162, dialog.getSNMPPort());
        assertEquals(SNMPTransport.TCP, dialog.getSNMPTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES256, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCustomPreferenceValuesScreenRotation() {
        PreferenceManager preferenceManager = new PreferenceManager(TestRegistry.getContext());
        preferenceManager.setPreferenceSNMPVersion(SNMPVersion.V3);
        preferenceManager.setPreferenceSNMPPort(162);
        preferenceManager.setPreferenceSNMPTransport(SNMPTransport.TCP);
        preferenceManager.setPreferenceSNMPAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        preferenceManager.setPreferenceSNMPPrivAlgorithm(SNMPPrivAlgorithm.AES256);
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("162")));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-256")));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-256")));
        SNMPDefaultsDialog dialog = getDialog();
        assertEquals(SNMPVersion.V3, dialog.getSNMPVersion());
        assertEquals(162, dialog.getSNMPPort());
        assertEquals(SNMPTransport.TCP, dialog.getSNMPTransport());
        assertEquals(SNMPAuthAlgorithm.SHA256, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES256, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGettersReturnSelectedValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("8162"));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-512"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-256C"))).inRoot(isPlatformPopup()).perform(click());
        assertEquals(SNMPVersion.V1, dialog.getSNMPVersion());
        assertEquals(8162, dialog.getSNMPPort());
        assertEquals(SNMPTransport.TCP, dialog.getSNMPTransport());
        assertEquals(SNMPAuthAlgorithm.SHA512, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES256C, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGettersReturnSelectedValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("8162"));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-512"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-256C"))).inRoot(isPlatformPopup()).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isChecked()));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("8162")));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isChecked()));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-512")));
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-256C")));
        rotateScreen(activityScenario);
        SNMPDefaultsDialog dialog = getDialog();
        assertEquals(SNMPVersion.V3, dialog.getSNMPVersion());
        assertEquals(8162, dialog.getSNMPPort());
        assertEquals(SNMPTransport.TCP, dialog.getSNMPTransport());
        assertEquals(SNMPAuthAlgorithm.SHA512, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES256C, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testSNMPVersionOptions() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isNotChecked()));
        assertEquals(SNMPVersion.V1, dialog.getSNMPVersion());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isNotChecked()));
        assertEquals(SNMPVersion.V2C, dialog.getSNMPVersion());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v1)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v2c)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_version_v3)).check(matches(isChecked()));
        assertEquals(SNMPVersion.V3, dialog.getSNMPVersion());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAllSNMPTransportOptions() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp)).check(matches(isNotChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isChecked()));
        assertEquals(SNMPTransport.TCP, dialog.getSNMPTransport());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp)).perform(click());
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_udp)).check(matches(isChecked()));
        onView(withId(R.id.radiobutton_dialog_snmp_defaults_snmp_transport_tcp)).check(matches(isNotChecked()));
        assertEquals(SNMPTransport.UDP, dialog.getSNMPTransport());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testSNMPAuthAlgorithmOptions() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("None")));
        assertEquals(SNMPAuthAlgorithm.NONE, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-1"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-1")));
        assertEquals(SNMPAuthAlgorithm.SHA1, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-384"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-384")));
        assertEquals(SNMPAuthAlgorithm.SHA384, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testSNMPPrivAlgorithmOptions() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("None")));
        assertEquals(SNMPPrivAlgorithm.NONE, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("DES"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("DES")));
        assertEquals(SNMPPrivAlgorithm.DES, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_defaults_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-192")));
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPortInputColorValid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("65535"));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("0"));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPortInputColorInvalid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("161"));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPortInputColorInvalidScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("161"));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogEmptyPort() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_snmp_defaults_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("SNMP port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("161"));
        onView(withId(R.id.imageview_dialog_snmp_defaults_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogEmptyPortScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText(""));
        onView(withId(R.id.imageview_dialog_snmp_defaults_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("SNMP port"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("No value specified"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("161"));
        onView(withId(R.id.imageview_dialog_snmp_defaults_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorDialogValidOkCloses() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPDefaultsDialog();
        onView(withId(R.id.imageview_dialog_snmp_defaults_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("123");
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("8162"));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("8162")));
        assertTrue(clipboardManager.hasData());
        assertEquals("8162", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPasteOption() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("8162");
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("8162")));
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOptionScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPDefaultsDialog dialog = openSNMPDefaultsDialog();
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("123");
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(replaceText("8162"));
        rotateScreen(activityScenario);
        clipboardManager = prepareMockClipboardManager(getDialog());
        clipboardManager.putData("123");
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_defaults_snmp_port)).check(matches(withText("8162")));
        assertTrue(clipboardManager.hasData());
        assertEquals("8162", clipboardManager.getData());
        onView(withId(R.id.imageview_dialog_snmp_defaults_cancel)).perform(click());
        activityScenario.close();
    }

    private MockClipboardManager prepareMockClipboardManager(SNMPDefaultsDialog dialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        dialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private SNMPDefaultsDialog openSNMPDefaultsDialog() {
        SNMPDefaultsDialog dialog = new SNMPDefaultsDialog();
        dialog.show(getActivity(activityScenario).getSupportFragmentManager(), SNMPDefaultsDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return dialog;
    }

    private SNMPDefaultsDialog getDialog() {
        return (SNMPDefaultsDialog) getDialog(activityScenario, SNMPDefaultsDialog.class);
    }
}
