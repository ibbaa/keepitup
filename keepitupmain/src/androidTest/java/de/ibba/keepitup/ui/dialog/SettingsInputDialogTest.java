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
import de.ibba.keepitup.test.mock.MockClipboardManager;
import de.ibba.keepitup.test.mock.TestValidator1;
import de.ibba.keepitup.test.mock.TestValidator2;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.GlobalSettingsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testCopyPasteNoOption() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        prepareMockClipboardManager(inputDialog);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testCopyPasteNoOptionNumericIntegerData() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.CONNECTCOUNT, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
    }

    @Test
    public void testCopyPasteCancel() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testCopyOption() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        assertTrue(clipboardManager.hasData());
        assertEquals("test", clipboardManager.getData());
    }

    @Test
    public void testPasteOption() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("abc");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText(""));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
    }

    @Test
    public void testCopyPasteOption() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, "success", "field", Arrays.asList(TestValidator1.class.getName(), TestValidator1.class.getName()));
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        MockClipboardManager clipboardManager = prepareMockClipboardManager(inputDialog);
        clipboardManager.putData("11");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("33"));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("33")));
        assertTrue(clipboardManager.hasData());
        assertEquals("33", clipboardManager.getData());
        clipboardManager.putData("22");
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(longClick());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertEquals(1, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("22")));
        assertTrue(clipboardManager.hasData());
        assertEquals("22", clipboardManager.getData());
    }

    @Test
    public void testStateSavedOnScreenRotation() {
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, "abc", "field", Collections.emptyList());
        inputDialog.setArguments(input.toBundle());
        inputDialog.show(activity.getSupportFragmentManager(), SettingsInputDialog.class.getName());
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("abc")));
        onView(withId(R.id.edittext_dialog_settings_input_value)).perform(replaceText("test"));
        rotateScreen(activity);
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.edittext_dialog_settings_input_value)).check(matches(withText("test")));
        inputDialog = (SettingsInputDialog) rule.getActivity().getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.imageview_dialog_settings_input_ok)).perform(click());
        assertEquals("test", inputDialog.getValue());
    }

    private MockClipboardManager prepareMockClipboardManager(SettingsInputDialog inputDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        inputDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }
}
