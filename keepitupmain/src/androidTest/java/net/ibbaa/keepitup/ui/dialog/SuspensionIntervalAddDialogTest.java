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
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;
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
    private IntervalDAO intervalDAO;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        intervalDAO.deleteAllIntervals();
    }

    @Test
    public void testMode() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        activityScenario.close();
    }

    @Test
    public void testModeScreenRotation() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, null);
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
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.END, null);
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
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null);
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
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, defaultTime);
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
        openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, defaultTime);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withValue(23)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withValue(45)));
    }

    @Test
    public void testTimeSelected() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null);
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
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null);
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
    public void testTimeSelectedNumberPickerColorStart() {
        /*activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        SuspensionIntervalAddDialog intervalAddDialog = openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode.START, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).perform(setNumber(11));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).perform(setNumber(15));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_hour)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.picker_dialog_suspension_interval_add_time_minute)).check(matches(withNumberPickerColor(R.color.textErrorColor)));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        intervalsDialog.dismiss();
        activityScenario.close();*/
    }

    private SuspensionIntervalsDialog openSuspensionIntervalsDialog() {
        SuspensionIntervalsDialog intervalsDialog = new SuspensionIntervalsDialog();
        intervalsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalsDialog.class.getName());
        return intervalsDialog;
    }

    private SuspensionIntervalAddDialog openSuspensionIntervalAddDialog(SuspensionIntervalAddDialog.Mode mode, Time time) {
        SuspensionIntervalAddDialog intervalAddDialog = new SuspensionIntervalAddDialog();
        Bundle bundle = BundleUtil.stringToBundle(intervalAddDialog.getModeKey(), mode.name());
        if (time != null) {
            BundleUtil.bundleToBundle(intervalAddDialog.getDefaultTimeKey(), time.toBundle(), bundle);
        }
        intervalAddDialog.setArguments(bundle);
        intervalAddDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalAddDialog.class.getName());
        onView(isRoot()).perform(waitFor(500));
        return intervalAddDialog;
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
