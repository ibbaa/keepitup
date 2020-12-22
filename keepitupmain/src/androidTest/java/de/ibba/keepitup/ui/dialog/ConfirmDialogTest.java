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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class ConfirmDialogTest extends BaseUITest {

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
    public void testConfirmMessage() {
        openConfirmDialog();
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
    }

    @Test
    public void testConfirmMessageScreenRotation() {
        openConfirmDialog();
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
    }

    @Test
    public void testConfirmMessageWithDescription() {
        openConfirmDialogWithDescription();
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("Description")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
    }

    @Test
    public void testConfirmMessageWithDescriptionScreenRotation() {
        openConfirmDialogWithDescription();
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("Description")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("Description")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_confirm_message)).check(matches(withText("Message")));
        onView(withId(R.id.textview_dialog_confirm_description)).check(matches(withText("Description")));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
    }

    @Test
    public void testProgressBar() {
        ConfirmDialog confirmDialog = openConfirmDialog();
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(not(isDisplayed())));
        getActivity(activityScenario).runOnUiThread(confirmDialog::showProgressBar);
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(isDisplayed()));
        getActivity(activityScenario).runOnUiThread(confirmDialog::hideProgressBar);
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
    }

    @Test
    public void testProgressBarScreenRotation() {
        ConfirmDialog confirmDialog = openConfirmDialog();
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(not(isDisplayed())));
        getActivity(activityScenario).runOnUiThread(confirmDialog::showProgressBar);
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        getActivity(activityScenario).runOnUiThread(() -> getDialog().showProgressBar());
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(isDisplayed()));
        getActivity(activityScenario).runOnUiThread(() -> getDialog().hideProgressBar());
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(not(isDisplayed())));
        rotateScreen(activityScenario);
        onView(withId(R.id.progressbar_dialog_confirm_progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
    }

    private ConfirmDialog openConfirmDialog() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setArguments(BundleUtil.stringToBundle(confirmDialog.getMessageKey(), "Message"));
        confirmDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ConfirmDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return confirmDialog;
    }

    private ConfirmDialog openConfirmDialogWithDescription() {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setArguments(BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getDescriptionKey()}, new String[]{"Message", "Description"}));
        confirmDialog.show(getActivity(activityScenario).getSupportFragmentManager(), ConfirmDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return confirmDialog;
    }

    private ConfirmDialog getDialog() {
        return (ConfirmDialog) getDialog(activityScenario, ConfirmDialog.class);
    }
}
