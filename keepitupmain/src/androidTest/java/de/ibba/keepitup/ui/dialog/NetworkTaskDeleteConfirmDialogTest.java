package de.ibba.keepitup.ui.dialog;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskDeleteConfirmDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
    }

    @Test
    public void testConfirmMessage() {
        NetworkTaskConfirmDialog confirmDialog = new NetworkTaskConfirmDialog();
        confirmDialog.setArguments(BundleUtil.messageToBundle(NetworkTaskConfirmDialog.class.getSimpleName(), "Message"));
        confirmDialog.show(activity.getSupportFragmentManager(), NetworkTaskConfirmDialog.class.getName());
        onView(withId(R.id.textview_dialog_network_task_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_network_task_confirm_cancel)).perform(click());
    }
}
