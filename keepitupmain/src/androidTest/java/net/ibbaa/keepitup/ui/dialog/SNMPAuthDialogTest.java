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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.SNMPAuthAlgorithm;
import net.ibbaa.keepitup.model.SNMPPrivAlgorithm;
import net.ibbaa.keepitup.test.mock.MockClipboardManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SNMPAuthDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testDefaultValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPAuthDialog dialog = openSNMPAuthDialog();
        onView(withId(R.id.textview_dialog_snmp_auth_title)).check(matches(withText("SNMP authentication")));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_user_name_label)).check(matches(withText("Auth username")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label)).check(matches(withText("Auth passphrase")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText("")));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_algorithm_label)).check(matches(withText("Auth algorithm")));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("None")));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label)).check(matches(withText("Privacy passphrase")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_algorithm_label)).check(matches(withText("Privacy algorithm")));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(not(isEnabled())));
        assertEquals("", dialog.getSNMPUserName());
        assertEquals("", dialog.getSNMPAuthPassphrase());
        assertEquals(SNMPAuthAlgorithm.NONE, dialog.getSNMPAuthAlgorithm());
        assertEquals("", dialog.getSNMPPrivPassphrase());
        assertEquals(SNMPPrivAlgorithm.NONE, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_snmp_auth_title)).check(matches(withText("SNMP authentication")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText("")));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("None")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(not(isEnabled())));
        rotateScreen(activityScenario);
        SNMPAuthDialog dialog = getDialog();
        assertEquals("", dialog.getSNMPUserName());
        assertEquals("", dialog.getSNMPAuthPassphrase());
        assertEquals(SNMPAuthAlgorithm.NONE, dialog.getSNMPAuthAlgorithm());
        assertEquals("", dialog.getSNMPPrivPassphrase());
        assertEquals(SNMPPrivAlgorithm.NONE, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpUserName("testuser");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("testuser")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-256")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-192")));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(isEnabled()));
        assertEquals("testuser", dialog.getSNMPUserName());
        assertEquals("authpass", dialog.getSNMPAuthPassphrase());
        assertEquals(SNMPAuthAlgorithm.SHA256, dialog.getSNMPAuthAlgorithm());
        assertEquals("privpass", dialog.getSNMPPrivPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpUserName("testuser");
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        openSNMPAuthDialog(data);
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("testuser")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-256")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-192")));
        rotateScreen(activityScenario);
        SNMPAuthDialog dialog = getDialog();
        assertEquals("testuser", dialog.getSNMPUserName());
        assertEquals("authpass", dialog.getSNMPAuthPassphrase());
        assertEquals(SNMPAuthAlgorithm.SHA256, dialog.getSNMPAuthAlgorithm());
        assertEquals("privpass", dialog.getSNMPPrivPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGettersReturnSelectedValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPAuthDialog dialog = openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("myuser"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(replaceText("myauthpass"));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-512"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-256C"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(replaceText("myprivpass"));
        assertEquals("myuser", dialog.getSNMPUserName());
        assertEquals("myauthpass", dialog.getSNMPAuthPassphrase());
        assertEquals(SNMPAuthAlgorithm.SHA512, dialog.getSNMPAuthAlgorithm());
        assertEquals("myprivpass", dialog.getSNMPPrivPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256C, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testGettersReturnSelectedValuesScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("myuser"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(replaceText("myauthpass"));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-512"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-256C"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(replaceText("myprivpass"));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("myuser")));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-512")));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-256C")));
        rotateScreen(activityScenario);
        SNMPAuthDialog dialog = getDialog();
        assertEquals("myuser", dialog.getSNMPUserName());
        assertEquals("myauthpass", dialog.getSNMPAuthPassphrase());
        assertEquals(SNMPAuthAlgorithm.SHA512, dialog.getSNMPAuthAlgorithm());
        assertEquals("myprivpass", dialog.getSNMPPrivPassphrase());
        assertEquals(SNMPPrivAlgorithm.AES256C, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testSNMPAuthAlgorithmOptions() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPAuthDialog dialog = openSNMPAuthDialog();
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-1"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-1")));
        assertEquals(SNMPAuthAlgorithm.SHA1, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-384"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-384")));
        assertEquals(SNMPAuthAlgorithm.SHA384, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("None")));
        assertEquals(SNMPAuthAlgorithm.NONE, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testSNMPPrivAlgorithmOptions() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("DES"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("DES")));
        assertEquals(SNMPPrivAlgorithm.DES, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-192")));
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-256C"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-256C")));
        assertEquals(SNMPPrivAlgorithm.AES256C, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivFieldsDisabledWhenAuthNone() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPAuthDialog dialog = openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-256"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        assertEquals(SNMPAuthAlgorithm.SHA256, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(not(isEnabled())));
        assertEquals(SNMPAuthAlgorithm.NONE, dialog.getSNMPAuthAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("MD5"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(isEnabled()));
        assertEquals(SNMPAuthAlgorithm.MD5, dialog.getSNMPAuthAlgorithm());
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivFieldsDisabledWhenAuthNoneScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-256"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        rotateScreen(activityScenario);
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("SHA-256")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("None")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("MD5"))).inRoot(isPlatformPopup()).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).check(matches(withSpinnerText("MD5")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(isEnabled()));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseDisabledWhenPrivNone() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        assertEquals(SNMPPrivAlgorithm.NONE, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        assertEquals(SNMPPrivAlgorithm.AES192, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        assertEquals(SNMPPrivAlgorithm.NONE, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("DES"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        assertEquals(SNMPPrivAlgorithm.DES, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseDisabledWhenPrivNoneScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("AES-192")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).check(matches(withSpinnerText("None")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        SNMPAuthDialog dialog = getDialog();
        assertEquals(SNMPPrivAlgorithm.NONE, dialog.getSNMPPrivAlgorithm());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAuthPassphraseRetainedWhenAuthSetToNone() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPAuthDialog dialog = openSNMPAuthDialog();
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-256"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(replaceText("myauthpass"));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(not(isEnabled())));
        assertEquals("myauthpass", dialog.getSNMPAuthPassphrase());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("SHA-256"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText("myauthpass")));
        assertEquals("myauthpass", dialog.getSNMPAuthPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testEditedInitialAuthPassphraseRetainedWhenAuthSetToNone() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(typeText("newauth"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_auth_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(not(isEnabled())));
        assertEquals("newauth", dialog.getSNMPAuthPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseRetainedWhenPrivSetToNone() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(replaceText("myprivpass"));
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        assertEquals("myprivpass", dialog.getSNMPPrivPassphrase());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("AES-192"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(isEnabled()));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText("myprivpass")));
        assertEquals("myprivpass", dialog.getSNMPPrivPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testEditedInitialPrivPassphraseRetainedWhenPrivSetToNone() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(typeText("newpriv"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.spinner_dialog_snmp_auth_snmp_priv_algorithm)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("None"))).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(not(isEnabled())));
        assertEquals("newpriv", dialog.getSNMPPrivPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testUserNameTextColorValid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("validuser"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testUserNameTextColorInvalid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textColor)));
        String tooLong = new String(new char[256]).replace('\0', 'a');
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText(tooLong));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("validuser"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testUserNameTextColorInvalidScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        String tooLong = new String(new char[256]).replace('\0', 'a');
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText(tooLong));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("validuser"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withTextColor(R.color.textColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAuthPassphraseLabelColorValid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAuthPassphraseLabelColorInvalid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphraseValid(false);
        openSNMPAuthDialog(data);
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAuthPassphraseLabelColorInvalidScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphraseValid(false);
        openSNMPAuthDialog(data);
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseLabelColorValid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseLabelColorInvalid() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpPrivPassphraseValid(false);
        openSNMPAuthDialog(data);
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_auth_passphrase_label)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseLabelColorInvalidScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpPrivPassphraseValid(false);
        openSNMPAuthDialog(data);
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_snmp_auth_snmp_priv_passphrase_label)).check(matches(withTextColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAuthPassphraseToggle() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testAuthPassphraseToggleScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseToggle() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testPrivPassphraseToggleScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialPassphrasesShowPlaceholder() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialPassphrasesShowPlaceholderScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        openSNMPAuthDialog(data);
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        rotateScreen(activityScenario);
        SNMPAuthDialog dialog = getDialog();
        assertEquals("authpass", dialog.getSNMPAuthPassphrase());
        assertEquals("privpass", dialog.getSNMPPrivPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialAuthPassphraseClearOnFocus() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        assertEquals("authpass", dialog.getSNMPAuthPassphrase());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(typeText("newauth"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(false)));
        assertEquals("newauth", dialog.getSNMPAuthPassphrase());
        assertEquals("privpass", dialog.getSNMPPrivPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialAuthPassphraseClearOnFocusScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        openSNMPAuthDialog(data);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(typeText("newauth"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withPasswordVisibility(true)));
        SNMPAuthDialog dialog = getDialog();
        assertEquals("newauth", dialog.getSNMPAuthPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialPrivPassphraseClearOnFocus() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        SNMPAuthDialog dialog = openSNMPAuthDialog(data);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText(StringUtil.getSecretPlaceholder())));
        assertEquals("privpass", dialog.getSNMPPrivPassphrase());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(typeText("newpriv"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(false)));
        assertEquals("newpriv", dialog.getSNMPPrivPassphrase());
        assertEquals("authpass", dialog.getSNMPAuthPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testInitialPrivPassphraseClearOnFocusScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        data.setSnmpPrivAlgorithm(SNMPPrivAlgorithm.AES192);
        openSNMPAuthDialog(data);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(typeText("newpriv"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(false)));
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(closeSoftKeyboard());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(false)));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(togglePassword());
        rotateScreen(activityScenario);
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).check(matches(withPasswordVisibility(true)));
        SNMPAuthDialog dialog = getDialog();
        assertEquals("newpriv", dialog.getSNMPPrivPassphrase());
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorUserNameTooLong() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        String tooLong = new String(new char[256]).replace('\0', 'a');
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText(tooLong));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Auth username"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 255"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("validuser"));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorUserNameTooLongScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        String tooLong = new String(new char[256]).replace('\0', 'a');
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText(tooLong));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Auth username"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 255"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("validuser"));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorAuthPassphraseTooLong() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        String tooLong = new String(new char[256]).replace('\0', 'x');
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(replaceText(tooLong));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Auth passphrase"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 255"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).perform(replaceText("valid"));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationErrorPrivPassphraseTooLong() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        openSNMPAuthDialog(data);
        String tooLong = new String(new char[256]).replace('\0', 'x');
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(replaceText(tooLong));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(2, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("Privacy passphrase"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Maximum length: 255"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_priv_passphrase)).perform(replaceText("valid"));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationOkWithEmptyPassphrase() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_auth_passphrase)).check(matches(withText("")));
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testValidationOkWithInitialPassphraseNotClicked() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        AccessTypeData data = new AccessTypeData();
        data.setSnmpAuthPassphrase("authpass");
        data.setSnmpAuthAlgorithm(SNMPAuthAlgorithm.SHA256);
        data.setSnmpPrivPassphrase("privpass");
        openSNMPAuthDialog(data);
        onView(withId(R.id.imageview_dialog_snmp_auth_ok)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOptionUserName() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        SNMPAuthDialog dialog = openSNMPAuthDialog();
        onView(isRoot()).perform(waitFor(500));
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("myuser"));
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("myuser")));
        assertTrue(clipboardManager.hasData());
        assertEquals("myuser", clipboardManager.getData());
        clipboardManager.putData("pasteduser");
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("pasteduser")));
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testCopyPasteOptionUserNameScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        openSNMPAuthDialog();
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(replaceText("myuser"));
        rotateScreen(activityScenario);
        SNMPAuthDialog dialog = getDialog();
        MockClipboardManager clipboardManager = prepareMockClipboardManager(dialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("myuser")));
        assertTrue(clipboardManager.hasData());
        assertEquals("myuser", clipboardManager.getData());
        clipboardManager.putData("pasteduser");
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).perform(longClick());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_snmp_auth_snmp_user_name)).check(matches(withText("pasteduser")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_snmp_auth_cancel)).perform(click());
        activityScenario.close();
    }

    private MockClipboardManager prepareMockClipboardManager(SNMPAuthDialog dialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        dialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }

    private SNMPAuthDialog openSNMPAuthDialog() {
        return openSNMPAuthDialog(new AccessTypeData());
    }

    private SNMPAuthDialog openSNMPAuthDialog(AccessTypeData data) {
        SNMPAuthDialog dialog = new SNMPAuthDialog();
        Bundle args = BundleUtil.bundleToBundle(dialog.getAccessTypeDataKey(), data.toBundle(), new Bundle());
        dialog.setArguments(args);
        dialog.show(getActivity(activityScenario).getSupportFragmentManager(), SNMPAuthDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return dialog;
    }

    private SNMPAuthDialog getDialog() {
        return (SNMPAuthDialog) getDialog(activityScenario, SNMPAuthDialog.class);
    }
}
