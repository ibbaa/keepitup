package de.ibba.keepitup.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Dump;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.resources.PreferenceManager;
import de.ibba.keepitup.test.mock.TestRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SystemActivityTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = ActivityScenario.launch(SystemActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testDisplayDefaultValues() {
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testDisplayValues() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(withText(endsWith("log"))));
        onView(withId(R.id.textview_activity_system_log_folder)).check(matches(not(isEnabled())));
    }

    @Test
    public void testSwitchYesNoText() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("no")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("no")));
    }

    @Test
    public void testSetPreferencesOk() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        PreferenceManager preferenceManager = getPreferenceManager();
        assertTrue(preferenceManager.getPreferenceFileLoggerEnabled());
        assertTrue(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testFileLoggerInitialized() {
        assertNull(Log.getLogger());
        assertNull(Dump.getDump());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        assertNotNull(Log.getLogger());
        assertNotNull(Dump.getDump());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        assertNull(Log.getLogger());
        assertNull(Dump.getDump());
    }

    @Test
    public void testResetValues() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        openActionBarOverflowOrOptionsMenu(TestRegistry.getContext());
        onView(withText("Reset")).perform(click());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isNotChecked()));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isNotChecked()));
        PreferenceManager preferenceManager = getPreferenceManager();
        assertFalse(preferenceManager.getPreferenceFileLoggerEnabled());
        assertFalse(preferenceManager.getPreferenceFileDumpEnabled());
    }

    @Test
    public void testPreserveValuesOnScreenRotation() {
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).perform(click());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(scrollTo());
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("yes")));
        rotateScreen(activityScenario);
        onView(withId(R.id.switch_activity_system_file_logger_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_logger_enabled_on_off)).check(matches(withText("yes")));
        onView(withId(R.id.switch_activity_system_file_dump_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_system_file_dump_enabled_on_off)).check(matches(withText("yes")));
    }
}
