package net.ibbaa.keepitup.test.viewaction;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public class WaitForViewAction implements ViewAction {

    private final long time;

    public WaitForViewAction(long time) {
        this.time = time;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isRoot();
    }

    @Override
    public String getDescription() {
        return "Wait for " + time + " msec";
    }

    @Override
    public void perform(UiController uiController, View view) {
        uiController.loopMainThreadForAtLeast(time);
    }
}
