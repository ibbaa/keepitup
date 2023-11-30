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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;

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
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SuspensionIntervalAddDialogTest extends BaseUITest {

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
    public void testMode() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testModeScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeNotProvided() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(0)));
        Time time = intervalAddDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeNotProvidedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(0)));
        Time time = intervalAddDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(0)));
        time = intervalAddDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(0)));
        time = intervalAddDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeProvided() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        Time defaultTime = new Time();
        defaultTime.setHour(12);
        defaultTime.setMinute(34);
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, defaultTime, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(34)));
        Time time = intervalAddDialog.getSelectedTime();
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(34)));
        time = intervalAddDialog.getSelectedTime();
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(34)));
        time = intervalAddDialog.getSelectedTime();
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeProvidedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        Time defaultTime = new Time();
        defaultTime.setHour(23);
        defaultTime.setMinute(45);
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, defaultTime, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(45)));
    }

    @Test
    public void testTimeSelected() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(0)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(0)));
        Time time = intervalAddDialog.getSelectedTime();
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(23));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(59));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(59)));
        Time time = intervalAddDialog.getSelectedTime();
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(59)));
        time = intervalAddDialog.getSelectedTime();
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(59)));
        time = intervalAddDialog.getSelectedTime();
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTimeInitialNumberPickerColorStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time time = new Time();
        time.setHour(1);
        time.setMinute(15);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, time, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(15));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(28));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorStartScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time time = new Time();
        time.setHour(2);
        time.setMinute(1);
        openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, time, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalAddDialog intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(30));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeInitialNumberPickerColorEndDuration() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(2);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(43);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeInitialNumberPickerColorEndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(20);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorEndDuration() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(3);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(1);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(16));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(5));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorEndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(25);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(0));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(55));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorEndScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(50);
        openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalAddDialog intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(59));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedOkStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(15));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureStartScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(11));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(3));
        updateNumberPickerStatus(intervalAddDialog);
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedOkEnd() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(50);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndDuration() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(30);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(10));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(59);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndDurationAndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(11);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 15 minutes from each other"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("End"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        scheduler.restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(16);
        openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(3));
        updateNumberPickerStatus(intervalAddDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        intervalAddDialog = (SuspensionIntervalAddDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 15 minutes"), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(9)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(16));
        updateNumberPickerStatus(intervalAddDialog);
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(9)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(16)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    private SuspensionIntervalsDialog openSuspensionIntervalsDialog() {
        SuspensionIntervalsDialog intervalsDialog = new SuspensionIntervalsDialog();
        intervalsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalsDialog.class.getName());
        return intervalsDialog;
    }

    private SuspensionIntervalAddDialog openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode mode, Time defaultTime, Time startTime) {
        SuspensionIntervalAddDialog intervalAddDialog = new SuspensionIntervalAddDialog();
        Bundle bundle = BundleUtil.stringToBundle(intervalAddDialog.getModeKey(), mode.name());
        if (defaultTime != null) {
            BundleUtil.bundleToBundle(intervalAddDialog.getDefaultTimeKey(), defaultTime.toBundle(), bundle);
        }
        if (startTime != null) {
            BundleUtil.bundleToBundle(intervalAddDialog.getStartTimeKey(), startTime.toBundle(), bundle);
        }
        intervalAddDialog.setArguments(bundle);
        intervalAddDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalAddDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return intervalAddDialog;
    }

    private void updateNumberPickerStatus(SuspensionIntervalAddDialog intervalAddDialog) {
        intervalAddDialog.getColorListener().onValueChange(null, 0, 0);
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
}
