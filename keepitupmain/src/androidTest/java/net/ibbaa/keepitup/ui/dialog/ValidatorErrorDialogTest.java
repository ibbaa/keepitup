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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.os.Bundle;
import android.widget.GridLayout;

import androidx.test.core.app.ActivityScenario;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ValidatorErrorDialogTest extends BaseUITest {

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
        openValidatorErrorDialog();
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("field1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).check(matches(withGridLayoutPositionAndSpan(3, 1, GridLayout.CENTER, 0, 2, GridLayout.CENTER)));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
    }

    @Test
    public void testErrorMessageScreenRotation() {
        openValidatorErrorDialog();
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("field1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("field1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("field1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
    }

    private void openValidatorErrorDialog() {
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        ValidationResult result1 = new ValidationResult(false, "field1", "message1");
        ValidationResult result2 = new ValidationResult(false, "field2", "message2");
        Bundle bundle = BundleUtil.validationResultListToBundle(errorDialog.getValidationResultBaseKey(), Arrays.asList(result1, result2));
        errorDialog.setArguments(bundle);
        errorDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ValidatorErrorDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }
}
