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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class GeneralErrorDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
    }

    @Test
    public void testErrorMessage() {
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.stringToBundle(GeneralErrorDialog.class.getSimpleName(), "Message"));
        errorDialog.show(activity.getSupportFragmentManager(), GeneralErrorDialog.class.getName());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Message")));
    }
}
