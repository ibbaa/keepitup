package net.ibbaa.keepitup.ui.dialog;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.MockPowerManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.startsWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class BatteryOptimizationDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockPowerManager powerManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        powerManager = new MockPowerManager();
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectPowerManager(powerManager));
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testBatteryOptimizationActive() {
        powerManager.setBatteryOptimized(true);
        openBatteryOptimizationDialog();
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        onView(withId(R.id.imageview_dialog_battery_optimization_ok)).perform(click());
    }

    @Test
    public void testBatteryOptimizationNotActive() {
        powerManager.setBatteryOptimized(false);
        openBatteryOptimizationDialog();
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is not active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        onView(withId(R.id.imageview_dialog_battery_optimization_ok)).perform(click());
    }

    @Test
    public void testBatteryOptimizationScreenRotation() {
        openBatteryOptimizationDialog();
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText(startsWith("Battery optimization is active for this app."))));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
    }

    private void openBatteryOptimizationDialog() {
        BatteryOptimizationDialog batteryOptimizationDialog = new BatteryOptimizationDialog();
        batteryOptimizationDialog.show(getActivity(activityScenario).getSupportFragmentManager(), BatteryOptimizationDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }
}
