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
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GeneralMessageDialogTest extends BaseUITest {

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
    public void testMessage() {
        GeneralMessageDialog errorDialog = openGeneralMessageDialog();
        onView(isRoot()).perform(waitFor(500));
        assertEquals("ExtraData", errorDialog.getExtraData());
        onView(withId(R.id.textview_dialog_general_message_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_general_message_ok)).perform(click());
    }

    @Test
    public void testScreenRotation() {
        openGeneralMessageDialog();
        onView(withId(R.id.textview_dialog_general_message_message)).check(matches(withText("Message")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_general_message_message)).check(matches(withText("Message")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_general_message_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_general_message_ok)).perform(click());
    }

    private GeneralMessageDialog openGeneralMessageDialog() {
        GeneralMessageDialog errorDialog = new GeneralMessageDialog();
        Bundle bundle = BundleUtil.stringToBundle(errorDialog.getMessageKey(), "Message");
        BundleUtil.stringToBundle(errorDialog.getExtraDataKey(), "ExtraData", bundle);
        errorDialog.setArguments(bundle);
        errorDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GeneralMessageDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return errorDialog;
    }
}
