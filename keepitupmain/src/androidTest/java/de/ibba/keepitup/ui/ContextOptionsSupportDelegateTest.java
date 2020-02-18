package de.ibba.keepitup.ui;

import android.text.InputType;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.MockClipboardManager;
import de.ibba.keepitup.test.mock.TestContextOptionsSupport;
import de.ibba.keepitup.test.mock.TestRegistry;
import de.ibba.keepitup.ui.dialog.ContextOption;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ContextOptionsSupportDelegateTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;
    private TestContextOptionsSupport contextOptionsSupport;
    private MockClipboardManager clipboardManager;
    private ContextOptionsSupportDelegate contextOptionsSupportDelegate;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        contextOptionsSupport = new TestContextOptionsSupport();
        clipboardManager = new MockClipboardManager();
        contextOptionsSupportDelegate = new ContextOptionsSupportDelegate(activity.getSupportFragmentManager(), contextOptionsSupport, clipboardManager);
    }

    @Test
    public void testShowContextOptionsDialogEmpty() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("");
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(doesNotExist());
    }

    @Test
    public void testShowContextOptionsDialogCopy() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).perform(click());
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        contextOptionsSupport.reset();
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertFalse(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testShowContextOptionsDialogPaste() {
        clipboardManager.clearData();
        clipboardManager.putData("abc");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("");
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).perform(click());
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        contextOptionsSupport.reset();
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertFalse(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testShowContextOptionsDialogCopyPaste() {
        clipboardManager.clearData();
        clipboardManager.putData("abc");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).perform(click());
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        contextOptionsSupport.reset();
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 1))).perform(click());
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        contextOptionsSupport.reset();
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertFalse(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testShowContextOptionsDialogNumericIntegerData() {
        clipboardManager.clearData();
        clipboardManager.putData("abc");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setText("abc");
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        clipboardManager.putData("123");
        contextOptionsSupportDelegate.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }
}
