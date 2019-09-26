package de.ibba.keepitup.ui.dialog;

import android.content.res.Configuration;
import android.widget.GridLayout;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.TestValidator1;
import de.ibba.keepitup.test.mock.TestValidator2;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.GlobalSettingsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SettingsInputDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<GlobalSettingsActivity> rule = new ActivityTestRule<>(GlobalSettingsActivity.class, false, false);

    private GlobalSettingsActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = rule.launchActivity(null);
        activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
    }

    @Test
    public void testGetValue() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Collections.emptyList());
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals("abc", inputDialog.getValue());
    }

    @Test
    public void testValidation() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator2.class.getName(), TestValidator2.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(allOf(withText("field"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("testfailed1"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("field"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("testfailed2"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).check(matches(withGridLayoutPositionAndSpan(3, 1, GridLayout.CENTER, 0, 2, GridLayout.CENTER)));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("success"));
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testTextColor() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("failure"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textErrorColor)));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("success"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withTextColor(R.color.textColor)));
    }

    @Test
    public void testCancel() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        onView(withId(R.id.imageview_dialog_settings_input_cancel)).perform(click());
        assertEquals(0, activity.getSupportFragmentManager().getFragments().size());
    }
}
