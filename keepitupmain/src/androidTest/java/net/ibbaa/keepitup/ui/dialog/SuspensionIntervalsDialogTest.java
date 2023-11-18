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
import net.ibbaa.keepitup.db.IntervalDAO;
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
    private IntervalDAO intervalDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        scheduler = new TestTimeBasedSuspensionScheduler(TestRegistry.getContext());
        networkTaskScheduler = new TestNetworkTaskProcessServiceScheduler(TestRegistry.getContext());
        scheduler.setNetworkTaskScheduler(networkTaskScheduler);
        networkTaskScheduler.setTimeBasedSuspensionScheduler(scheduler);
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
        scheduler.reset();
        scheduler.stop();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        intervalDAO.deleteAllIntervals();
        scheduler.reset();
        scheduler.stop();
    }

    @Test
    public void testOpenCloseDialog() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.imageview_dialog_suspension_intervals_cancel)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).perform(click());
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
        assertEquals(0, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        activityScenario.close();
    }

    @Test
    public void testNoIntervals() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        intervalDAO.insertInterval(getInterval3());
        scheduler.restart();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        onView(withId(R.id.textview_activity_global_settings_suspension_enabled_label)).check(matches(withText("Suspension intervals enabled")));
        onView(withId(R.id.switch_activity_global_settings_suspension_enabled)).check(matches(isChecked()));
        onView(withId(R.id.textview_activity_global_settings_suspension_intervals)).perform(click());
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
