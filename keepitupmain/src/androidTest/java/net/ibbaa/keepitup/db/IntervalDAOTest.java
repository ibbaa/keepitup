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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class IntervalDAOTest {

    private IntervalDAO intervalDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        intervalDAO.deleteAllIntervals();
    }

    @After
    public void afterEachTestMethod() {
        intervalDAO.deleteAllIntervals();
    }

    @Test
    public void testInsertReadDelete() {
        Interval insertedInterval1 = getInterval1();
        intervalDAO.insertInterval(insertedInterval1);
        List<Interval> readIntervals = intervalDAO.readAllIntervals();
        assertEquals(1, readIntervals.size());
        Interval readInterval = readIntervals.get(0);
        assertTrue(readInterval.getId() > 0);
        assertTrue(insertedInterval1.isEqual(readInterval));
        readInterval = intervalDAO.readInterval(readInterval.getId());
        assertTrue(insertedInterval1.isEqual(readInterval));
        Interval insertedInterval2 = getInterval2();
        Interval insertedInterval3 = getInterval3();
        Interval insertedInterval4 = getInterval4();
        Interval insertedInterval5 = getInterval5();
        intervalDAO.insertInterval(insertedInterval2);
        intervalDAO.insertInterval(insertedInterval3);
        intervalDAO.insertInterval(insertedInterval4);
        intervalDAO.insertInterval(insertedInterval5);
        readIntervals = intervalDAO.readAllIntervals();
        assertEquals(5, readIntervals.size());
        Interval readInterval1 = readIntervals.get(0);
        Interval readInterval2 = readIntervals.get(1);
        Interval readInterval3 = readIntervals.get(2);
        Interval readInterval4 = readIntervals.get(3);
        Interval readInterval5 = readIntervals.get(4);
        assertTrue(readInterval1.getId() > 0);
        assertTrue(readInterval2.getId() > 0);
        assertTrue(readInterval3.getId() > 0);
        assertTrue(readInterval4.getId() > 0);
        assertTrue(readInterval5.getId() > 0);
        assertTrue(insertedInterval1.isEqual(readInterval3));
        assertTrue(insertedInterval2.isEqual(readInterval1));
        assertTrue(insertedInterval3.isEqual(readInterval5));
        assertTrue(insertedInterval4.isEqual(readInterval2));
        assertTrue(insertedInterval5.isEqual(readInterval4));
        readInterval1 = intervalDAO.readInterval(readInterval1.getId());
        readInterval2 = intervalDAO.readInterval(readInterval2.getId());
        readInterval3 = intervalDAO.readInterval(readInterval3.getId());
        readInterval4 = intervalDAO.readInterval(readInterval4.getId());
        readInterval5 = intervalDAO.readInterval(readInterval5.getId());
        assertTrue(insertedInterval1.isEqual(readInterval3));
        assertTrue(insertedInterval2.isEqual(readInterval1));
        assertTrue(insertedInterval3.isEqual(readInterval5));
        assertTrue(insertedInterval4.isEqual(readInterval2));
        assertTrue(insertedInterval5.isEqual(readInterval4));
        intervalDAO.deleteInterval(readInterval2);
        readInterval2 = intervalDAO.readInterval(readInterval2.getId());
        assertNull(readInterval2);
    }

    @Test
    public void testReadValues() {
        Interval interval1 = getInterval1();
        interval1 = intervalDAO.insertInterval(interval1);
        assertTrue(interval1.getId() > 0);
        interval1 = intervalDAO.readInterval(interval1.getId());
        assertFalse(interval1.isActive());
        assertEquals(10, interval1.getStart().getHour());
        assertEquals(11, interval1.getStart().getMinute());
        assertEquals(11, interval1.getEnd().getHour());
        assertEquals(12, interval1.getEnd().getMinute());
        Interval interval2 = getInterval2();
        interval2 = intervalDAO.insertInterval(interval2);
        assertTrue(interval2.getId() > 0);
        interval2 = intervalDAO.readInterval(interval2.getId());
        assertTrue(interval2.isActive());
        assertEquals(1, interval2.getStart().getHour());
        assertEquals(1, interval2.getStart().getMinute());
        assertEquals(2, interval2.getEnd().getHour());
        assertEquals(2, interval2.getEnd().getMinute());
    }

    @Test
    public void testUpdate() {
        Interval insertedInterval1 = getInterval1();
        intervalDAO.insertInterval(insertedInterval1);
        List<Interval> readIntervals = intervalDAO.readAllIntervals();
        Interval readInterval1 = readIntervals.get(0);
        Interval interval2 = getInterval2();
        interval2.setId(readInterval1.getId());
        intervalDAO.updateInterval(interval2);
        assertTrue(insertedInterval1.isEqual(readInterval1));
        readInterval1 = intervalDAO.readInterval(readInterval1.getId());
        assertEquals(interval2.isActive(), readInterval1.isActive());
        assertEquals(interval2.getStart().getHour(), readInterval1.getStart().getHour());
        assertEquals(interval2.getStart().getMinute(), readInterval1.getStart().getMinute());
        assertEquals(interval2.getEnd().getHour(), readInterval1.getEnd().getHour());
        assertEquals(interval2.getEnd().getMinute(), readInterval1.getEnd().getMinute());
    }

    private Interval getInterval1() {
        Interval interval = new Interval();
        interval.setId(0);
        interval.setActive(false);
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
        interval.setActive(true);
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
        interval.setActive(true);
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

    private Interval getInterval4() {
        Interval interval = new Interval();
        interval.setId(0);
        interval.setActive(true);
        Time start = new Time();
        start.setHour(2);
        start.setMinute(4);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(2);
        end.setMinute(5);
        interval.setEnd(end);
        return interval;
    }

    private Interval getInterval5() {
        Interval interval = new Interval();
        interval.setId(0);
        interval.setActive(true);
        Time start = new Time();
        start.setHour(15);
        start.setMinute(1);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(16);
        end.setMinute(16);
        interval.setEnd(end);
        return interval;
    }
}
