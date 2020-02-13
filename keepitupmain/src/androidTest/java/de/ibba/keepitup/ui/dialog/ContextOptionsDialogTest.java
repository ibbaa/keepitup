package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

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
import de.ibba.keepitup.test.mock.TestContextOptionsSupport;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ContextOptionsDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;
    private TestContextOptionsSupport testContextOptionsSupport;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
        testContextOptionsSupport = new TestContextOptionsSupport();
    }

    @Test
    public void testDisplayOptions() {
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog(testContextOptionsSupport);
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(activity.getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Copy")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 1))).check(matches(withText("Paste")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), Collections.singletonList(ContextOption.COPY.name()));
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(activity.getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(1)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).check(matches(withText("Copy")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), Collections.emptyList());
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(activity.getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(0)));
        onView(withId(R.id.textview_dialog_context_options_title)).check(matches(withText("Text options")));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }

    @Test
    public void testCancel() {
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog(testContextOptionsSupport);
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(activity.getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        assertFalse(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
    }

    @Test
    public void testOptionClicked() {
        ContextOptionsDialog contextOptionsDialog = new ContextOptionsDialog(testContextOptionsSupport);
        Bundle bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 1);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(activity.getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 0))).perform(click());
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        TestContextOptionsSupport.OnContextOptionsDialogEntryClickedCall call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(1, call.getSourceResourceId());
        assertEquals(ContextOption.COPY, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
        testContextOptionsSupport.reset();
        bundle = BundleUtil.stringListToBundle(ContextOption.class.getSimpleName(), Arrays.asList(ContextOption.COPY.name(), ContextOption.PASTE.name()));
        bundle.putInt(contextOptionsDialog.getSourceResourceIdKey(), 2);
        contextOptionsDialog.setArguments(bundle);
        contextOptionsDialog.show(activity.getSupportFragmentManager(), ContextOptionsDialog.class.getName());
        onView(withId(R.id.listview_dialog_context_options_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_context_option_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_context_options_entries), 1))).perform(click());
        assertTrue(testContextOptionsSupport.wasOnContextOptionsDialogEntryClickedCalled());
        call = testContextOptionsSupport.getOnContextOptionsDialogEntryClickedCalls().get(0);
        assertEquals(2, call.getSourceResourceId());
        assertEquals(ContextOption.PASTE, call.getOption());
        onView(withId(R.id.imageview_dialog_context_options_cancel)).perform(click());
    }
}
