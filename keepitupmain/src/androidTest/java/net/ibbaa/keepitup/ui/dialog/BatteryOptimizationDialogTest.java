/*
 * Copyright (c) 2026 Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.ui.dialog;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.MockPowerManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class BatteryOptimizationDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockPowerManager powerManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class, getBypassSystemSAFBundle());
        powerManager = new MockPowerManager();
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectPowerManager(powerManager);
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
    public void testOptimizationListHint() {
        openBatteryOptimizationDialog();
        onView(withId(R.id.textview_dialog_battery_optimization_restricted)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText("Battery optimization is active for this app.")));
        onView(withId(R.id.textview_dialog_battery_optimization_description)).check(matches(withText("Active battery optimization may cause the network tasks to be triggered less often as configured by the interval setting if the device is unplugged.")));
        onView(withId(R.id.textview_dialog_battery_optimization_settings_hint)).check(matches(withText("To turn off battery optimization, show all apps in the Android settings, scroll to this app, tap it and choose Not optimized.")));
        onView(withId(R.id.imageview_dialog_battery_optimization_ok)).perform(click());
    }

    @Test
    public void testAppBatteryUsageHint() {
        powerManager.setUsesAppBatteryUsageSettings(true);
        openBatteryOptimizationDialog();
        onView(withId(R.id.textview_dialog_battery_optimization_restricted)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(withText("Battery optimization is active for this app.")));
        onView(withId(R.id.textview_dialog_battery_optimization_description)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_battery_optimization_settings_hint)).check(matches(withText("To turn off battery optimization, select App battery usage in the Android settings, tap the text Allow background usage and choose Unrestricted. Do not turn off the switch: network tasks would no longer run in the background. Menu names may differ depending on the device.")));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        onView(withId(R.id.imageview_dialog_battery_optimization_ok)).perform(click());
    }

    @Test
    public void testBackgroundRestricted() {
        powerManager.setBackgroundRestricted(true);
        powerManager.setUsesAppBatteryUsageSettings(true);
        openBatteryOptimizationDialog();
        onView(withId(R.id.textview_dialog_battery_optimization_restricted)).check(matches(withText("Background usage is restricted for this app. Network tasks will not run while the app is in the background.")));
        onView(withId(R.id.textview_dialog_battery_optimization_restricted)).check(matches(isDisplayed()));
        onView(withId(R.id.textview_dialog_battery_optimization_info)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_battery_optimization_description)).check(matches(not(isDisplayed())));
        onView(withId(R.id.textview_dialog_battery_optimization_settings_hint)).check(matches(withText("To remove the restriction, select App battery usage in the Android settings and allow background usage. Menu names may differ depending on the device.")));
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
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_battery_optimization_title)).check(matches(withText("Battery Optimization")));
        onView(withId(R.id.textview_dialog_battery_optimization_link)).check(matches(withText("Click here to change battery optimization settings")));
    }

    private void openBatteryOptimizationDialog() {
        BatteryOptimizationDialog batteryOptimizationDialog = new BatteryOptimizationDialog();
        batteryOptimizationDialog.show(getActivity(activityScenario).getSupportFragmentManager(), BatteryOptimizationDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
    }
}
