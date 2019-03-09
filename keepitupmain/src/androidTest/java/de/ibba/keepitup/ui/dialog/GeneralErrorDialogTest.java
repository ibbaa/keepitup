package de.ibba.keepitup.ui.dialog;

import android.support.test.annotation.UiThreadTest;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GeneralErrorDialogTest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class);

    private NetworkTaskMainActivity activity;

    @Before
    @UiThreadTest
    public void beforeEachTestMethod() {
        activity = rule.getActivity();
    }

    @Test
    public void testErrorMessage() {
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.messageToBundle(GeneralErrorDialog.class.getSimpleName(), "Message"));
        errorDialog.show(activity.getSupportFragmentManager(), GeneralErrorDialog.class.getName());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Message")));
    }
}