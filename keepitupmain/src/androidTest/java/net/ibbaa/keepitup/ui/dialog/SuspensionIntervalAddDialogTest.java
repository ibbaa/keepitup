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

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.Time;
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

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testMode() {
        openSuspensionIntervalDialog(SuspensionIntervalAddDialog.Mode.START, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("Start")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
        openSuspensionIntervalDialog(SuspensionIntervalAddDialog.Mode.END, null);
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.textview_dialog_suspension_interval_add_label)).check(matches(withText("Add suspension interval")));
        onView(withId(R.id.textview_dialog_suspension_interval_add_time_label)).check(matches(withText("End")));
        onView(withId(R.id.imageview_dialog_suspension_interval_add_cancel)).perform(click());
    }

    @Test
    public void testModeScreenRotation() {
        openSuspensionIntervalDialog(SuspensionIntervalAddDialog.Mode.END, null);
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
    }

    private SuspensionIntervalAddDialog openSuspensionIntervalDialog(SuspensionIntervalAddDialog.Mode mode, Time time) {
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
}
