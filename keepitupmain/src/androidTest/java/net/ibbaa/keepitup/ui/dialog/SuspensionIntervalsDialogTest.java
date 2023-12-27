/*
 * Copyright (c) 2023. Alwin Ibba
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
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestTimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SuspensionIntervalsDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private TestTimeBasedSuspensionScheduler scheduler;
    private TestNetworkTaskProcessServiceScheduler networkTaskScheduler;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        scheduler = new TestTimeBasedSuspensionScheduler(TestRegistry.getContext());
        networkTaskScheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.setNetworkTaskScheduler(networkTaskScheduler);
        networkTaskScheduler.setTimeBasedSuspensionScheduler(scheduler);
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.stop();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        networkTaskScheduler.reset();
        scheduler.reset();
        scheduler.stop();
    }

    @Test
    public void testOpenCloseDialog() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoIntervals() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testNoIntervalsScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneInterval() {
        getIntervalDAO().insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneIntervalScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervals() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneIntervalDeleteCancel() {
        getIntervalDAO().insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneIntervalDeleteCancelScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        rotateScreen(activityScenario);
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_confirm_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneIntervalDelete() {
        getIntervalDAO().insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testOneIntervalDeleteScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        rotateScreen(activityScenario);
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        assertEquals(0, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsDelete() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).perform(click());
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsDeleteScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        rotateScreen(activityScenario);
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalDefaultValues() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(7)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(8)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(13)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(16)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 05:00 End: 07:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 08:00 End: 10:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 11:00 End: 13:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 14:00 End: 16:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 4))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 4))).check(matches(withText("Start: 22:00 End: 04:00")));
        assertEquals(5, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesLargestGap() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 12:12 End: 14:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesLargestGapScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 12:12 End: 14:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesThresholdGap() {
        Interval interval1 = getInterval1();
        Time start1 = new Time();
        start1.setHour(2);
        start1.setMinute(47);
        Time end1 = new Time();
        end1.setHour(22);
        end1.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval3 = getInterval3();
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(30);
        interval3.setEnd(end3);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(2)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(17)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(2)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(32)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 02:17 End: 02:32")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 02:47 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 00:30")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesThresholdGapScreenRotation() {
        Interval interval1 = getInterval1();
        Time start1 = new Time();
        start1.setHour(2);
        start1.setMinute(47);
        Time end1 = new Time();
        end1.setHour(22);
        end1.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval3 = getInterval3();
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(30);
        interval3.setEnd(end3);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(2)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(17)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(2)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(32)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 02:17 End: 02:32")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 02:47 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 00:30")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesMinGap() {
        Interval interval1 = getInterval1();
        Time start1 = new Time();
        start1.setHour(2);
        start1.setMinute(30);
        Time end1 = new Time();
        end1.setHour(22);
        end1.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval3 = getInterval3();
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(30);
        interval3.setEnd(end3);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 02:30 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 00:30")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesMinGapScreenRotation() {
        Interval interval1 = getInterval1();
        Time start1 = new Time();
        start1.setHour(2);
        start1.setMinute(30);
        Time end1 = new Time();
        end1.setHour(22);
        end1.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval3 = getInterval3();
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(30);
        interval3.setEnd(end3);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 02:30 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 00:30")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesLargestGapAdjustStart() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(12));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(50));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(50)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 12:50 End: 14:50")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesLargestGapAdjustStartScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(12));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(50));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(50)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 12:50 End: 14:50")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesThresholdGapAdjustStart() {
        Interval interval1 = getInterval1();
        Time start1 = new Time();
        start1.setHour(3);
        start1.setMinute(30);
        Time end1 = new Time();
        end1.setHour(22);
        end1.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval3 = getInterval3();
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(30);
        interval3.setEnd(end3);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(2)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(17)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 03:00 End: 03:15")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 03:30 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 00:30")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesThresholdGapAdjustStartScreenRotation() {
        Interval interval1 = getInterval1();
        Time start1 = new Time();
        start1.setHour(3);
        start1.setMinute(30);
        Time end1 = new Time();
        end1.setHour(22);
        end1.setMinute(0);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Interval interval3 = getInterval3();
        Time end3 = new Time();
        end3.setHour(0);
        end3.setMinute(30);
        interval3.setEnd(end3);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(2)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(17)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 03:00 End: 03:15")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 03:30 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 00:30")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
    }

    @Test
    public void testIntervalsDefaultValuesOpenLargeGapAdjustStart() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(12));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 12:00 End: 14:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalsDefaultValuesOpenLargeGapAdjustStartScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        rotateScreen(activityScenario);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(12));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(14)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 12:00 End: 14:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalsDefaultValuesOpenThresholdGapAdjustStart() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(19));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(16));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 19:16 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalsDefaultValuesOpenThresholdGapAdjustStartScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(19));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(16));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 19:16 End: 22:00")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalsDefaultValuesOpenMinGapAdjustStart() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalsDefaultValuesOpenMinGapAdjustStartScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalAddCancel() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalAddCancelScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        rotateScreen(activityScenario);
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalAddOk() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testIntervalAddOkScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("No intervals defined")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:00 End: 04:00")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsAdd() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 03:03 End: 05:03")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(3);
        start.setMinute(3);
        Time end = new Time();
        end.setHour(5);
        end.setMinute(3);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsAddScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        rotateScreen(activityScenario);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 03:03 End: 05:03")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(3);
        start.setMinute(3);
        Time end = new Time();
        end.setHour(5);
        end.setMinute(3);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsAddError() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(59));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsAddErrorScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(59));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(4));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsAddOverlapDays() {
        getIntervalDAO().insertInterval(getInterval1());
        Interval interval3 = getInterval3();
        Time end = new Time();
        end.setHour(0);
        end.setMinute(15);
        interval3.setEnd(end);
        getIntervalDAO().insertInterval(interval3);
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(1));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:15 End: 03:15")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 00:15")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(1);
        start.setMinute(15);
        end = new Time();
        end.setHour(3);
        end.setMinute(15);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(interval3.isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsAddAndDelete() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_interval_add)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 03:03 End: 05:03")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 3))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(4, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(2, getDialog().getAdapter().getAllItems().size());
        onView(allOf(withId(R.id.imageview_list_item_suspension_interval_delete), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.imageview_dialog_confirm_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(1, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenSameInterval() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenSameIntervalScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenSorted() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(30));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 00:15 End: 00:30")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(0);
        start.setMinute(15);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(30);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenSortedScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(30));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 00:15 End: 00:30")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(0);
        start.setMinute(15);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(30);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenStartAfterEnd() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(1)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(1)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(20));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 02:05 End: 02:20")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(2);
        start.setMinute(5);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(20);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenStartAfterEndScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        rotateScreen(activityScenario);
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(1)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(1)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(4)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(5)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(20));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 02:05 End: 02:20")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(2);
        start.setMinute(5);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(20);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(getInterval3().isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenError() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(1));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(59)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(16));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(1));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 00:01")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(22);
        start.setMinute(15);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(1);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsOpenErrorScreenRotation() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(15)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(1));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(59)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(22));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(16));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(1));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 00:01")));
        assertEquals(3, getDialog().getAdapter().getAllItems().size());
        Interval interval = new Interval();
        Time start = new Time();
        start.setHour(22);
        start.setMinute(15);
        Time end = new Time();
        end.setHour(0);
        end.setMinute(1);
        interval.setStart(start);
        interval.setEnd(end);
        assertTrue(getInterval2().isEqual(getDialog().getAdapter().getAllItems().get(0)));
        assertTrue(getInterval1().isEqual(getDialog().getAdapter().getAllItems().get(1)));
        assertTrue(interval.isEqual(getDialog().getAdapter().getAllItems().get(2)));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsFinalErrorDuration() {
        Interval interval1 = getInterval1();
        Time end = new Time();
        end.setHour(10);
        end.setMinute(12);
        interval1.setEnd(end);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 10:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 10:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsFinalErrorDurationScreenRotation() {
        Interval interval1 = getInterval1();
        Time end = new Time();
        end.setHour(10);
        end.setMinute(12);
        interval1.setEnd(end);
        getIntervalDAO().insertInterval(interval1);
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 10:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 10:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 23:59")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsFinalErrorOverlap() {
        Interval interval3 = getInterval3();
        Time end = new Time();
        end.setHour(1);
        end.setMinute(15);
        interval3.setEnd(end);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 01:15")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 01:15")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testThreeIntervalsFinalErrorOverlapScreenRotation() {
        Interval interval3 = getInterval3();
        Time end = new Time();
        end.setHour(1);
        end.setMinute(15);
        interval3.setEnd(end);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getIntervalDAO().insertInterval(interval3);
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.cardview_activity_global_settings_suspension_intervals)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 01:15")));
        rotateScreen(activityScenario);
        onView(withId(R.id.imageview_dialog_suspension_intervals_ok)).perform(click());
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        onView(allOf(withText("Interval"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_suspension_intervals_intervals)).check(matches(withListSize(3)));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval_no_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 0))).check(matches(withText("Start: 01:01 End: 02:02")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 1))).check(matches(withText("Start: 10:11 End: 11:12")));
        onView(allOf(withId(R.id.cardview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.textview_list_item_suspension_interval), withChildDescendantAtPosition(withId(R.id.listview_dialog_suspension_intervals_intervals), 2))).check(matches(withText("Start: 22:15 End: 01:15")));
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        activityScenario.close();
    }

    private SuspensionIntervalsDialog getDialog() {
        return (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private void injectTimeBasedSuspensionScheduler() {
        ((GlobalSettingsActivity) getActivity(activityScenario)).injectTimeBasedSuspensionScheduler(scheduler);
    }

    private Interval getInterval1() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(10);
        start.setMinute(11);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(11);
        end.setMinute(12);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval2() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(1);
        start.setMinute(1);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(2);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval3() {
        Interval interval = new Interval();
        interval.setId(0);
        Time start = new Time();
        start.setHour(22);
        start.setMinute(15);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(23);
        end.setMinute(59);
        interval.setEnd(end);
        return interval;
    }
}
