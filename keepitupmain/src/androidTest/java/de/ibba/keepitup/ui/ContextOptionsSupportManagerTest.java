package de.ibba.keepitup.ui;

import android.text.InputType;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.MockClipboardManager;
import de.ibba.keepitup.test.mock.TestContextOptionsSupport;
import de.ibba.keepitup.test.mock.TestContextOptionsSupportManager;
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
public class ContextOptionsSupportManagerTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockClipboardManager clipboardManager;
    private TestContextOptionsSupportManager contextOptionsSupportManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        clipboardManager = new MockClipboardManager();
        contextOptionsSupportManager = new TestContextOptionsSupportManager(getActivity(activityScenario).getSupportFragmentManager(), clipboardManager);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testShowContextOptionsDialogEmpty() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("");
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(doesNotExist());
    }

    @Test
    public void testShowContextOptionsDialogCopy() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsSupport contextOptionsSupport = (TestContextOptionsSupport) contextOptionsSupportManager.getTestContextOptionsDialog().getContextOptionsSupport();
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        contextOptionsSupport.reset();
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertFalse(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testShowContextOptionsDialogPaste() {
        clipboardManager.clearData();
        clipboardManager.putData("abc");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("");
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsSupport contextOptionsSupport = (TestContextOptionsSupport) contextOptionsSupportManager.getTestContextOptionsDialog().getContextOptionsSupport();
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        contextOptionsSupport.reset();
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertFalse(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testShowContextOptionsDialogCopyPaste() {
        clipboardManager.clearData();
        clipboardManager.putData("abc");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsSupport contextOptionsSupport = (TestContextOptionsSupport) contextOptionsSupportManager.getTestContextOptionsDialog().getContextOptionsSupport();
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        contextOptionsSupport.reset();
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertTrue(contextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        call = contextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(editText.getId(), call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        contextOptionsSupport.reset();
        contextOptionsSupportManager.showContextOptionsDialog(editText);
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
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        clipboardManager.putData("123");
        contextOptionsSupportManager.showContextOptionsDialog(editText);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testHandleContextOptionNull() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("");
        contextOptionsSupportManager.handleContextOption(editText, null);
        assertTrue(editText.getText().toString().isEmpty());
        assertFalse(clipboardManager.hasData());
    }

    @Test
    public void testHandleContextOptionCopyNoSelection() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        editText.setSelection(0, 0);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.COPY);
        assertEquals("abc", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
        editText.setText("abc");
        editText.setSelection(2, 1);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.COPY);
        assertEquals("abc", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
        editText.setText("");
        editText.setSelection(0, 0);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.COPY);
        assertEquals("", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("", clipboardManager.getData());
    }

    @Test
    public void testHandleContextOptionCopySelection() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abcabc");
        editText.setSelection(3, 6);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.COPY);
        assertEquals("abcabc", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("abc", clipboardManager.getData());
        editText.setSelection(5, 6);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.COPY);
        assertEquals("abcabc", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("c", clipboardManager.getData());
    }

    @Test
    public void testHandleContextOptionPasteNoData() {
        clipboardManager.clearData();
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        editText.setSelection(0, 0);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.PASTE);
        assertEquals("abc", editText.getText().toString());
        assertFalse(clipboardManager.hasData());
    }

    @Test
    public void testHandleContextOptionPasteNoNumericIntegerData() {
        clipboardManager.clearData();
        clipboardManager.putData("xyz");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText("abc");
        editText.setSelection(0, 0);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.PASTE);
        assertEquals("abc", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("xyz", clipboardManager.getData());
    }

    @Test
    public void testHandleContextOptionPasteNoSelection() {
        clipboardManager.clearData();
        clipboardManager.putData("xyz");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abc");
        editText.setSelection(0, 0);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.PASTE);
        assertEquals("xyz", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("xyz", clipboardManager.getData());
    }

    @Test
    public void testHandleContextOptionPasteSelection() {
        clipboardManager.clearData();
        clipboardManager.putData("xyz");
        EditText editText = new EditText(TestRegistry.getContext());
        editText.setText("abcabc");
        editText.setSelection(2, 3);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.PASTE);
        assertEquals("abxyzabc", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("xyz", clipboardManager.getData());
        editText.setText("abcabc");
        editText.selectAll();
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.PASTE);
        assertEquals("xyz", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("xyz", clipboardManager.getData());
        editText.setText("abcabc");
        editText.setSelection(1, 6);
        contextOptionsSupportManager.handleContextOption(editText, ContextOption.PASTE);
        assertEquals("axyz", editText.getText().toString());
        assertTrue(clipboardManager.hasData());
        assertEquals("xyz", clipboardManager.getData());
    }
}
