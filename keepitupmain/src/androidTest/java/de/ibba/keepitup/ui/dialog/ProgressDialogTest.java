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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ProgressDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = ActivityScenario.launch(NetworkTaskMainActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testProgressBarDisplayed() {
        openProgressDialog();
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
    }

    @Test
    public void testProgressBarScreenRotation() {
        openProgressDialog();
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.progressbar_dialog_progress)).check(matches(isDisplayed()));
    }

    private ProgressDialog openProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ConfirmDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return progressDialog;
    }
}
