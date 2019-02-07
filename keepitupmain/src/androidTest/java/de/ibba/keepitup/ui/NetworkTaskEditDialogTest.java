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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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
        ViewInteraction assNetworkTaskImageView = onView(allOf(withId(R.id.imageview_list_item_network_task_add), isDisplayed()));
        assNetworkTaskImageView.perform(click());
        ViewInteraction okImageView = onView(withId(R.id.imageview_dialog_edit_network_task_ok));
        Assert.assertNotNull(okImageView);
        okImageView.perform(click());
    }
}
