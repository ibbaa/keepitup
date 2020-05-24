package de.ibba.keepitup.ui.dialog;

import android.content.res.Configuration;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.MockPowerManager;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.GlobalSettingsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.startsWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class BatteryOptimizationDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<GlobalSettingsActivity> rule = new ActivityTestRule<>(GlobalSettingsActivity.class, false, false);

    private GlobalSettingsActivity activity;
    private MockPowerManager powerManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = rule.launchActivity(null);
        activity.setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
        powerManager = new MockPowerManager();
        activity.injectPowerManager(powerManager);
    }

    @Test
    public void testBatteryOptimizationActive() {
        powerManager.setBatteryOptimized(true);
        BatteryOptimizationDialog batteryOptimizationDialog = new BatteryOptimizationDialog(activity);
        batteryOptimizationDialog.show(activity.getSupportFragmentManager(), BatteryOptimizationDialog.class.getName());
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        onView(withId(R.id.imageview_dialog_battery_optimization_ok)).perform(click());
    }

    @Test
    public void testBatteryOptimizationNotActive() {
        powerManager.setBatteryOptimized(false);
        BatteryOptimizationDialog batteryOptimizationDialog = new BatteryOptimizationDialog(activity);
        batteryOptimizationDialog.show(activity.getSupportFragmentManager(), BatteryOptimizationDialog.class.getName());
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is not active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        onView(withId(R.id.imageview_dialog_battery_optimization_ok)).perform(click());
    }
}
