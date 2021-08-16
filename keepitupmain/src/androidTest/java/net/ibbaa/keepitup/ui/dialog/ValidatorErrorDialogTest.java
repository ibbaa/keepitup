package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;
import android.widget.GridLayout;

import androidx.test.core.app.ActivityScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class ValidatorErrorDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testErrorMessage() {
        openValidatorErrorDialog();
        onView(allOf(withText("field1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).check(matches(withGridLayoutPositionAndSpan(3, 1, GridLayout.CENTER, 0, 2, GridLayout.CENTER)));
    }

    @Test
    public void testErrorMessageScreenRotation() {
        openValidatorErrorDialog();
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
