package de.ibba.keepitup.ui.dialog;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
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
public class GeneralErrorDialogTest extends BaseUITest {

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
    public void testErrorMessage() {
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.stringToBundle(GeneralErrorDialog.class.getSimpleName(), "Message"));
        errorDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GeneralErrorDialog.class.getName());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
    }

    @Test
    public void testScreenRotation() {
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.stringToBundle(GeneralErrorDialog.class.getSimpleName(), "Message"));
        errorDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GeneralErrorDialog.class.getName());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Message")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Message")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Message")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
    }
}
