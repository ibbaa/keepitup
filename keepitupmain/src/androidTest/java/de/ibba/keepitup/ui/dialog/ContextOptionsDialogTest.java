package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.TestContextOptionsDialog;
import de.ibba.keepitup.test.mock.TestContextOptionsSupport;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ContextOptionsDialogTest extends BaseUITest {

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
    public void testDisplayOptions() {
        openContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        openContextOptionDialog(Collections.singletonList(ContextOption.COPY.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        openContextOptionDialog(Collections.emptyList());
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(0)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testDisplayOptionsTwoOptionsScreenRotation() {
        openContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testDisplayOptionsOneOptionScreenRotation() {
        openContextOptionDialog(Collections.singletonList(ContextOption.COPY.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).check(matches(withText("Copy")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testCancel() {
        TestContextOptionsDialog contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertFalse(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testOptionClicked() {
        TestContextOptionsDialog contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        testContextOptionsSupport.reset();
        contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        onView(withId(R.id.listview_dialog_context_options)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 1))).perform(click());
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testOptionClickedTwoOptionsScreenRotation() {
        TestContextOptionsDialog contextOptionsDialog = openTestContextOptionDialog(Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        contextOptionsDialog = getDialog();
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testOptionClickedOneOptionScreenRotation() {
        TestContextOptionsDialog contextOptionsDialog = openTestContextOptionDialog(Collections.singletonList(ContextOption.PASTE.name()));
        rotateScreen(activityScenario);
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.textview_list_item_context_option_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options), 0))).perform(click());
        contextOptionsDialog = getDialog();
        TestContextOptionsSupport testContextOptionsSupport = (TestContextOptionsSupport) contextOptionsDialog.getContextOptionsSupport();
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    private ContextOptionsDialog openContextOptionDialog(List<String> contextOptions) {
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog();
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), contextOptions);
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return contextOptionsDialog;
    }

    private TestContextOptionsDialog openTestContextOptionDialog(List<String> contextOptions) {
        TestContextOptionsDialog contextOptionsDialog = new TestContextOptionsDialog();
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), contextOptions);
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return contextOptionsDialog;
    }

    private TestContextOptionsDialog getDialog() {
        return (TestContextOptionsDialog) getDialog(activityScenario, TestContextOptionsDialog.class);
    }
}
