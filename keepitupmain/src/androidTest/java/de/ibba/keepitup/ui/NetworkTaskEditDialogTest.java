package de.ibba.keepitup.ui;

import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.NetworkTask;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskEditDialogTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class);

    private NetworkTaskMainActivity activity;

    @Before
    @UiThreadTest
    public void beforeEachTestMethod() {
        activity = rule.getActivity();
    }

    @Test
    public void testAddNetworkTask() {
        ViewInteraction addNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        addNetworkTaskImageView.perform(click());
        onView(withId(R.id.radiogroup_dialog_edit_network_task_accesstype)).check(matches(hasChildCount(1)));
        NetworkTaskEditDialog dialog = (NetworkTaskEditDialog) activity.getSupportFragmentManager().getFragments().get(0);
        NetworkTask task = dialog.getNetworkTask();
        Assert.assertNotNull(task);
        ViewInteraction okImageView = onView(withId(R.id.imageview_dialog_edit_network_task_ok));
        Assert.assertNotNull(okImageView);
        okImageView.perform(click());
    }
}
