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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.startsWith;

import android.os.Bundle;
import android.widget.GridLayout;

import androidx.test.core.app.ActivityScenario;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.validation.DecryptionResult;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class DecryptionErrorDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testErrorMessage() {
        openDecryptionErrorDialog();
        onView(withId(R.id.textview_dialog_decryption_error_title)).check(matches(withText("Reenter credentials")));
        onView(withId(R.id.textview_dialog_decryption_error_message)).check(matches(withText(startsWith("The following"))));
        onView(allOf(withText("task1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("task2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_decryption_error_ok)).check(matches(withGridLayoutPositionAndSpan(3, 1, GridLayout.CENTER, 0, 2, GridLayout.CENTER)));
        onView(withId(R.id.imageview_dialog_decryption_error_ok)).perform(click());
    }

    @Test
    public void testErrorMessageScreenRotation() {
        openDecryptionErrorDialog();
        onView(withId(R.id.textview_dialog_decryption_error_title)).check(matches(withText("Reenter credentials")));
        onView(withId(R.id.textview_dialog_decryption_error_message)).check(matches(withText(startsWith("The following"))));
        onView(allOf(withText("task1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("task2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("task1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("task2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("task1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("task2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_decryption_error_ok)).perform(click());
    }

    private void openDecryptionErrorDialog() {
        DecryptionErrorDialog errorDialog = new DecryptionErrorDialog();
        DecryptionResult result1 = new DecryptionResult("task1", "message1");
        DecryptionResult result2 = new DecryptionResult("task2", "message2");
        Bundle bundle = BundleUtil.decryptionResultListToBundle(errorDialog.getDecryptionResultBaseKey(), Arrays.asList(result1, result2));
        errorDialog.setArguments(bundle);
        errorDialog.show(getActivity(activityScenario).getSupportFragmentManager(), DecryptionErrorDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }
}
