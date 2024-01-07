/*
 * Copyright (c) 2024. Alwin Ibba
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
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.util.BundleUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SuspensionIntervalSelectDialogTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;

    @Test
    public void testMode() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_select_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_select_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testModeScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_select_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        rotateScreen(activityScenario);
        onView(withId(R.id.textview_dialog_suspension_interval_select_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeNotProvided() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        Time time = intervalSelectDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeNotProvidedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        Time time = intervalSelectDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        time = intervalSelectDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(22)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        time = intervalSelectDialog.getSelectedTime();
        assertEquals(22, time.getHour());
        assertEquals(0, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeProvided() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        Time defaultTime = new Time();
        defaultTime.setHour(12);
        defaultTime.setMinute(34);
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, defaultTime, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(34)));
        Time time = intervalSelectDialog.getSelectedTime();
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(34)));
        time = intervalSelectDialog.getSelectedTime();
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(12)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(34)));
        time = intervalSelectDialog.getSelectedTime();
        assertEquals(12, time.getHour());
        assertEquals(34, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testDefaultTimeProvidedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        Time defaultTime = new Time();
        defaultTime.setHour(23);
        defaultTime.setMinute(45);
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, defaultTime, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(45)));
    }

    @Test
    public void testTimeSelected() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(0)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(0)));
        Time time = intervalSelectDialog.getSelectedTime();
        assertEquals(0, time.getHour());
        assertEquals(0, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(23));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(59));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(59)));
        Time time = intervalSelectDialog.getSelectedTime();
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(59)));
        time = intervalSelectDialog.getSelectedTime();
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(59)));
        time = intervalSelectDialog.getSelectedTime();
        assertEquals(23, time.getHour());
        assertEquals(59, time.getMinute());
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testTimeInitialNumberPickerColorStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time time = new Time();
        time.setHour(1);
        time.setMinute(15);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, time, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(42));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorStartScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time time = new Time();
        time.setHour(2);
        time.setMinute(1);
        openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, time, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalSelectDialog intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(2));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(32));
        onView(isRoot()).perform(waitFor(500));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeInitialNumberPickerColorEndDuration() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(2);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(43);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeInitialNumberPickerColorEndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(20);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorEndDuration() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(3);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(3);
        end.setMinute(1);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(31));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(5));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorEndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(30);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(40));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedNumberPickerColorEndScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(10);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(40);
        openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalSelectDialog intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(59));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedOkStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureStart() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(15));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 30 minutes from each other. Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))). check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(0));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 30 minutes from each other. Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureStartScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.START, null, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(10));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(11));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 30 minutes from each other. Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("Start"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 30 minutes from each other. Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(10)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(11)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(3));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        updateNumberPickerStatus(intervalSelectDialog);
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedOkEnd() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(10);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(40);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(5000));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndDuration() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(30);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(10));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(30);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(59);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 30 minutes from each other."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndDurationAndOverlap() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(11);
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Intervals must not overlap and must have a distance of at least 30 minutes from each other."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(allOf(withText("End"), withGridLayoutPosition(2, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 30 minutes."), withGridLayoutPosition(2, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    @Test
    public void testTimeSelectedFailureEndScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        getTimeBasedSuspensionScheduler().restart();
        Time start = new Time();
        start.setHour(9);
        start.setMinute(0);
        Time end = new Time();
        end.setHour(9);
        end.setMinute(16);
        openSuspensionIntervalsDialog();
        SuspensionIntervalSelectDialog intervalSelectDialog = openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode.END, end, start);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(3));
        updateNumberPickerStatus(intervalSelectDialog);
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        rotateScreen(activityScenario);
        intervalSelectDialog = (SuspensionIntervalSelectDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(1);
        onView(withId(R.id.textview_dialog_validator_error_title)).check(matches(withText("Validation failed")));
        onView(allOf(withText("End"), withGridLayoutPosition(1, 0))).check(matches(isDisplayed()));
        onView(allOf(withText("Interval length minimum is 30 minutes."), withGridLayoutPosition(1, 1))).check(matches(isDisplayed()));
        onView(withId(R.id.imageview_dialog_validator_error_ok)).perform(click());
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(9)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(3)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).perform(setNumber(9));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).perform(setNumber(31));
        updateNumberPickerStatus(intervalSelectDialog);
        rotateScreen(activityScenario);
        SuspensionIntervalsDialog intervalsDialog = (SuspensionIntervalsDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
        onView(withId(R.id.textview_dialog_suspension_interval_select_time_label)).check(matches(withText("End")));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_hour)).check(matches(withValue(9)));
        onView(withId(R.id.picker_dialog_suspension_interval_select_time_minute)).check(matches(withValue(31)));
        onView(withId(R.id.imageview_dialog_suspension_interval_select_ok)).perform(click());
        assertEquals(1, getActivity(activityScenario).getSupportFragmentManager().getFragments().size());
        intervalsDialog.dismiss();
        activityScenario.close();
    }

    private SuspensionIntervalsDialog openSuspensionIntervalsDialog() {
        SuspensionIntervalsDialog intervalsDialog = new SuspensionIntervalsDialog();
        intervalsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalsDialog.class.getName());
        return intervalsDialog;
    }

    private SuspensionIntervalSelectDialog openSuspensionIntervalSelectDialog(SuspensionIntervalSelectDialog.Mode mode, Time defaultTime, Time startTime) {
        SuspensionIntervalSelectDialog intervalSelectDialog = new SuspensionIntervalSelectDialog();
        Bundle bundle = BundleUtil.stringToBundle(intervalSelectDialog.getModeKey(), mode.name());
        if (defaultTime != null) {
            BundleUtil.bundleToBundle(intervalSelectDialog.getDefaultTimeKey(), defaultTime.toBundle(), bundle);
        }
        if (startTime != null) {
            BundleUtil.bundleToBundle(intervalSelectDialog.getStartTimeKey(), startTime.toBundle(), bundle);
        }
        intervalSelectDialog.setArguments(bundle);
        intervalSelectDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalSelectDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return intervalSelectDialog;
    }

    private void updateNumberPickerStatus(SuspensionIntervalSelectDialog intervalSelectDialog) {
        intervalSelectDialog.getColorListener().onValueChange(null, 0, 0);
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
