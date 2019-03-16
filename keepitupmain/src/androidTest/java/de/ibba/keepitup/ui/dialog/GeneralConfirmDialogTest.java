package de.ibba.keepitup.ui.dialog;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;
import de.ibba.keepitup.util.BundleUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GeneralConfirmDialogTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;

    @Before
    public void beforeEachTestMethod() {
        rule.launchActivity(null);
        activity = rule.getActivity();
    }

    @Test
    public void testConfirmMessage() {
        GeneralConfirmDialog confirmDialog = new GeneralConfirmDialog();
        confirmDialog.setArguments(BundleUtil.messageToBundle(GeneralConfirmDialog.class.getSimpleName(), "Message"));
        confirmDialog.show(activity.getSupportFragmentManager(), GeneralConfirmDialog.class.getName());
        onView(withId(R.id.textview_dialog_general_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_general_confirm_cancel)).perform(click());
    }
}
