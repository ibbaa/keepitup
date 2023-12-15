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

package net.ibbaa.keepitup.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.db.IntervalDAO;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestNetworkTaskProcessServiceScheduler;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.test.mock.TestTimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class IntervalHandlerTest extends BaseUITest {

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
    public void testSynchronizeIntervalsEmpty() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        onView(isRoot()).perform(waitFor(500));
        IntervalHandler handler = new IntervalHandler(getGlobalSettingsActivity(), intervalsDialog);
        handler.synchronizeIntervals();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
        assertTrue(intervalsDialog.getAdapter().getAllItems().isEmpty());
    }

    @Test
    public void testSynchronizeDelete() {
        intervalDAO.insertInterval(getInterval1());
        intervalDAO.insertInterval(getInterval2());
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        onView(isRoot()).perform(waitFor(500));
        IntervalHandler handler = new IntervalHandler(getGlobalSettingsActivity(), intervalsDialog);
        intervalsDialog.getAdapter().removeItems();
        handler.synchronizeIntervals();
        assertTrue(intervalDAO.readAllIntervals().isEmpty());
    }

    @Test
    public void testSynchronizeIntervalsAdded() {
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        injectTimeBasedSuspensionScheduler();
        SuspensionIntervalsDialog intervalsDialog = openSuspensionIntervalsDialog();
        onView(isRoot()).perform(waitFor(500));
        IntervalHandler handler = new IntervalHandler(getGlobalSettingsActivity(), intervalsDialog);
        intervalsDialog.getAdapter().addItem(getInterval1());
        intervalsDialog.getAdapter().addItem(getInterval2());
        intervalsDialog.getAdapter().addItem(getInterval3());
        handler.synchronizeIntervals();
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(2, intervals.size());
        assertTrue(intervals.get(0).isEqual(getInterval1()));
        assertTrue(intervals.get(1).isEqual(getInterval2()));
    }

    private void injectTimeBasedSuspensionScheduler() {
        (getGlobalSettingsActivity()).injectTimeBasedSuspensionScheduler(scheduler);
    }

    private GlobalSettingsActivity getGlobalSettingsActivity() {
        return (GlobalSettingsActivity) getActivity(activityScenario);
    }

    private SuspensionIntervalsDialog openSuspensionIntervalsDialog() {
        SuspensionIntervalsDialog intervalsDialog = new SuspensionIntervalsDialog();
        intervalsDialog.show(getActivity(activityScenario).getSupportFragmentManager(), SuspensionIntervalsDialog.class.getName());
        return intervalsDialog;
    }

    private Interval getInterval1() {
        Interval interval = new Interval();
        interval.setId(-1);
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

    private Interval getInterval2() {
        Interval interval = new Interval();
        interval.setId(-1);
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
