package de.ibba.keepitup.ui.dialog;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.ibba.keepitup.BuildConfig;
import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.NetworkTaskMainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class InfoDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<NetworkTaskMainActivity> rule = new ActivityTestRule<>(NetworkTaskMainActivity.class, false, false);

    private NetworkTaskMainActivity activity;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (NetworkTaskMainActivity) launchRecyclerViewBaseActivity(rule);
    }

    @Test
    public void testBuildInfo() {
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(activity.getSupportFragmentManager(), InfoDialog.class.getName());
        onView(withId(R.id.textview_dialog_info_version)).check(matches(withText(BuildConfig.VERSION_NAME)));
        onView(withId(R.id.textview_dialog_info_build_type)).check(matches(withText(BuildConfig.BUILD_TYPE.toUpperCase())));
        onView(withId(R.id.textview_dialog_info_build_timestamp_)).check(matches(withText(containsString(getBuildYear()))));
    }

    @Test
    public void testCopyright() {
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(activity.getSupportFragmentManager(), InfoDialog.class.getName());
        onView(withId(R.id.textview_dialog_info_copyright)).check(matches(withText(containsString("Copyright"))));
        onView(withId(R.id.textview_dialog_info_copyright)).check(matches(withText(containsString("Alwin Ibba"))));
        onView(withId(R.id.textview_dialog_info_copyright)).check(matches(withText(containsString(String.valueOf(BuildConfig.RELEASE_YEAR)))));
        onView(withId(R.id.textview_dialog_info_copyright)).check(matches(withText(containsString(getBuildYear()))));
    }

    @Test
    public void testLicense() throws Exception {
        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(activity.getSupportFragmentManager(), InfoDialog.class.getName());
        onView(withId(R.id.textview_dialog_info_license)).check(matches(withText("This software is open source and released under the terms of the MIT license. Please click here to display the license text.")));
        onView(withId(R.id.textview_dialog_info_license)).perform(click());
        assertEquals(2, activity.getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.textview_dialog_raw_text_message)).check(matches(withText(containsString("MIT License"))));
        onView(withId(R.id.textview_dialog_raw_text_message)).check(matches(withText(containsString("Copyright"))));
        onView(withId(R.id.textview_dialog_raw_text_message)).check(matches(withText(containsString("Alwin Ibba"))));
        onView(withId(R.id.textview_dialog_raw_text_message)).check(matches(withText(containsString(String.valueOf(BuildConfig.RELEASE_YEAR)))));
        onView(withId(R.id.textview_dialog_raw_text_message)).check(matches(withText(containsString(getBuildYear()))));
        onView(withId(R.id.textview_dialog_raw_text_message)).check(matches(withText(containsString("Permission is hereby granted, free of charge"))));
    }

    private String getBuildYear() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(BuildConfig.TIMESTAMP));
        String test = String.valueOf(calendar.get(Calendar.YEAR));
        return test;
    }
}
