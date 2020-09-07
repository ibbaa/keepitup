package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class RawTextDialogTest extends BaseUITest {

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
    public void testContent() {
        openRawTextDialog();
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("MIT License"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("TESTTESTTEST"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("Permission is hereby granted, free of charge"))));
    }

    @Test
    public void testContentScreenRotation() {
        openRawTextDialog();
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("MIT License"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("TESTTESTTEST"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("Permission is hereby granted, free of charge"))));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("MIT License"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("TESTTESTTEST"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("Permission is hereby granted, free of charge"))));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("MIT License"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("TESTTESTTEST"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("Permission is hereby granted, free of charge"))));
    }


    private void openRawTextDialog() {
        RawTextDialog rawTextDialog = new RawTextDialog();
        Bundle bundle = BundleUtil.stringToBundle("COPYRIGHT", "TESTTESTTEST");
        bundle.putInt(rawTextDialog.getResourceIdKey(), R.raw.license);
        rawTextDialog.setArguments(bundle);
        rawTextDialog.show(getActivity(activityScenario).getSupportFragmentManager(), RawTextDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }
}
