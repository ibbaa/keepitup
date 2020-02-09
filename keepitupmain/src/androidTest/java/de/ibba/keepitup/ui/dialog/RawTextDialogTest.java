package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

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
import static org.hamcrest.CoreMatchers.containsString;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class RawTextDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
    }

    @Test
    public void testContent() {
        RawTextDialog rawTextDialog = new RawTextDialog();
        Bundle bundle = BundleUtil.stringToBundle("COPYRIGHT", "TESTTESTTEST");
        bundle.putInt(rawTextDialog.getResourceIdKey(), R.raw.license);
        rawTextDialog.setArguments(bundle);
        rawTextDialog.show(activity.getSupportFragmentManager(), RawTextDialog.class.getName());
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("MIT License"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("TESTTESTTEST"))));
        onView(withId(R.id.textview_dialog_raw_text_content)).check(matches(withText(containsString("Permission is hereby granted, free of charge"))));
    }
}
