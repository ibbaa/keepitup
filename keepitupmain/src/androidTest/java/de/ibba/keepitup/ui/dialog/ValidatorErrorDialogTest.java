package de.ibba.keepitup.ui.dialog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.widget.GridLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.SettingsActivity;
import de.ibba.keepitup.ui.validation.ValidationResult;
import de.ibba.keepitup.util.BundleUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class ValidatorErrorDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<SettingsActivity> rule = new ActivityTestRule<>(SettingsActivity.class, false, false);

    private SettingsActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = rule.launchActivity(null);
        activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
    }

    @Test
    public void testErrorMessage() {
        ValidatorErrorDialog errorDialog = new ValidatorErrorDialog();
        Bundle bundle = new Bundle();
        BundleUtil.addValidationResultToIndexedBundle(bundle, new ValidationResult(false, "field1", "message1"));
        BundleUtil.addValidationResultToIndexedBundle(bundle, new ValidationResult(false, "field2", "message2"));
        errorDialog.setArguments(bundle);
        errorDialog.show(activity.getSupportFragmentManager(), ValidatorErrorDialog.class.getName());
        onView(allOf(withText("field1"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field2"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("message2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).check(matches(withGridLayoutPositionAndSpan(3, 1, GridLayout.CENTER, 0, 2, GridLayout.CENTER)));
    }
}
