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

package net.ibbaa.keepitup.ui.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.BaseUITest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class IntervalSyncHandlerTest extends BaseUITest {

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        getIntervalDAO().deleteAllIntervals();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        getIntervalDAO().deleteAllIntervals();
    }

    @Test
    public void testSynchronizeIntervalsEmpty() {
        IntervalSyncHandler handler = new IntervalSyncHandler(TestRegistry.getContext());
        DBSyncResult syncResult = handler.synchronizeIntervals(Collections.emptyList(), Collections.emptyList());
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        assertTrue(getIntervalDAO().readAllIntervals().isEmpty());
    }

    @Test
    public void testSynchronizeIntervalsDelete() {
        getIntervalDAO().insertInterval(getInterval1());
        getIntervalDAO().insertInterval(getInterval2());
        IntervalSyncHandler handler = new IntervalSyncHandler(TestRegistry.getContext());
        DBSyncResult syncResult = handler.synchronizeIntervals(Collections.emptyList(), getIntervalDAO().readAllIntervals());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        assertTrue(getIntervalDAO().readAllIntervals().isEmpty());
    }

    @Test
    public void testSynchronizeIntervalsAdd() {
        IntervalSyncHandler handler = new IntervalSyncHandler(TestRegistry.getContext());
        List<Interval> newIntervals = List.of(getInterval1(), getInterval2(), getInterval3());
        DBSyncResult syncResult = handler.synchronizeIntervals(newIntervals, Collections.emptyList());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Interval> intervals = getIntervalDAO().readAllIntervals();
        assertEquals(2, intervals.size());
        assertTrue(intervals.get(0).isEqual(getInterval1()));
        assertTrue(intervals.get(1).isEqual(getInterval2()));
    }

    @Test
    public void testSynchronizeIntervalsNotUpdated() {
        Interval interval1 = getIntervalDAO().insertInterval(getInterval1());
        Interval interval2 = getIntervalDAO().insertInterval(getInterval2());
        IntervalSyncHandler handler = new IntervalSyncHandler(TestRegistry.getContext());
        List<Interval> newIntervals = List.of(interval1, interval2);
        DBSyncResult syncResult = handler.synchronizeIntervals(newIntervals, getIntervalDAO().readAllIntervals());
        assertTrue(syncResult.success());
        assertFalse(syncResult.dbChanged());
        List<Interval> intervals = getIntervalDAO().readAllIntervals();
        assertEquals(2, intervals.size());
        assertTrue(intervals.get(0).isEqual(getInterval1()));
        assertTrue(intervals.get(1).isEqual(getInterval2()));
    }

    @Test
    public void testSynchronizeIntervalsUpdated() {
        Interval interval1 = getIntervalDAO().insertInterval(getInterval1());
        Interval interval2 = getIntervalDAO().insertInterval(getInterval2());
        IntervalSyncHandler handler = new IntervalSyncHandler(TestRegistry.getContext());
        Time start1 = new Time();
        start1.setHour(5);
        start1.setMinute(5);
        Time end1 = new Time();
        end1.setHour(6);
        end1.setMinute(6);
        interval1.setStart(start1);
        interval1.setEnd(end1);
        Time start2 = new Time();
        start2.setHour(9);
        start2.setMinute(9);
        Time end2 = new Time();
        end2.setHour(10);
        end2.setMinute(10);
        interval2.setStart(start2);
        interval2.setEnd(end2);
        List<Interval> newIntervals = List.of(interval1, interval2);
        DBSyncResult syncResult = handler.synchronizeIntervals(newIntervals, getIntervalDAO().readAllIntervals());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Interval> intervals = getIntervalDAO().readAllIntervals();
        assertEquals(2, intervals.size());
        assertFalse(intervals.get(0).isEqual(getInterval1()));
        assertFalse(intervals.get(1).isEqual(getInterval2()));
        assertTrue(intervals.get(0).isEqual(interval1));
        assertTrue(intervals.get(1).isEqual(interval2));
    }

    @Test
    public void testSynchronizeIntervalsAddedUpdatedDeleted() {
        getIntervalDAO().insertInterval(getInterval2());
        Interval interval3 = getIntervalDAO().insertInterval(getInterval3());
        IntervalSyncHandler handler = new IntervalSyncHandler(TestRegistry.getContext());
        Time end = new Time();
        end.setHour(0);
        end.setMinute(15);
        interval3.setEnd(end);
        List<Interval> newIntervals = List.of(getInterval1(), interval3);
        DBSyncResult syncResult = handler.synchronizeIntervals(newIntervals, getIntervalDAO().readAllIntervals());
        assertTrue(syncResult.success());
        assertTrue(syncResult.dbChanged());
        List<Interval> intervals = getIntervalDAO().readAllIntervals();
        assertEquals(2, intervals.size());
        assertTrue(intervals.get(0).isEqual(getInterval1()));
        assertTrue(intervals.get(1).isEqual(interval3));
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
