/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.model.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.model.validation.IntervalValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntervalValidatorTest {

    private IntervalValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new IntervalValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateDuration() {
        Interval interval = getInterval1();
        assertTrue(validator.validateDuration(interval));
        assertTrue(validator.validate(interval, Collections.emptyList()));
        Time start = new Time();
        start.setHour(11);
        start.setMinute(11);
        interval.setStart(start);
        assertFalse(validator.validateDuration(interval));
        assertFalse(validator.validate(interval, Collections.emptyList()));
        Time end = new Time();
        end.setHour(11);
        end.setMinute(40);
        interval.setEnd(end);
        assertFalse(validator.validateDuration(interval));
        assertFalse(validator.validate(interval, Collections.emptyList()));
        end = new Time();
        end.setHour(11);
        end.setMinute(41);
        interval.setEnd(end);
        assertTrue(validator.validateDuration(interval));
        assertTrue(validator.validate(interval, Collections.emptyList()));
    }

    @Test
    public void testValidateDurationList() {
        assertTrue(validator.validateDuration(Arrays.asList(getInterval1(), getInterval2(), getInterval3())));
        Interval interval3 = getInterval3();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(16);
        interval3.setStart(start);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(25);
        interval3.setEnd(end);
        assertFalse(validator.validateDuration(Arrays.asList(getInterval1(), getInterval2(), interval3)));
        Interval interval1 = getInterval1();
        end = new Time();
        end.setHour(10);
        end.setMinute(12);
        interval1.setEnd(end);
        assertFalse(validator.validateDuration(Arrays.asList(interval1, getInterval2(), getInterval3())));
    }

    @Test
    public void testValidateOverlap() {
        Interval interval1 = getInterval1();
        assertTrue(validator.validateOverlap(interval1, Arrays.asList(getInterval2(), getInterval3())));
        assertTrue(validator.validate(interval1, Arrays.asList(getInterval2(), getInterval3())));
        assertFalse(validator.validateOverlap(interval1, Arrays.asList(getInterval1(), getInterval2(), getInterval3())));
        assertFalse(validator.validate(interval1, Arrays.asList(getInterval1(), getInterval2(), getInterval3())));
        Interval interval3 = getInterval3();
        Time start = new Time();
        start.setHour(10);
        start.setMinute(16);
        interval3.setStart(start);
        Time end = new Time();
        end.setHour(10);
        end.setMinute(25);
        interval3.setEnd(end);
        assertFalse(validator.validateOverlap(interval3, Arrays.asList(getInterval1(), getInterval2())));
        assertFalse(validator.validate(interval3, Arrays.asList(getInterval1(), getInterval2())));
        interval3 = getInterval3();
        start = new Time();
        start.setHour(11);
        start.setMinute(20);
        interval3.setStart(start);
        end = new Time();
        end.setHour(11);
        end.setMinute(59);
        interval3.setEnd(end);
        assertFalse(validator.validateOverlap(interval3, Arrays.asList(getInterval1(), getInterval2())));
        assertFalse(validator.validate(interval3, Arrays.asList(getInterval1(), getInterval2())));
        Interval interval2 = getInterval2();
        start = new Time();
        start.setHour(9);
        start.setMinute(5);
        interval2.setStart(start);
        end = new Time();
        end.setHour(10);
        end.setMinute(5);
        interval2.setEnd(end);
        assertFalse(validator.validateOverlap(interval2, List.of(getInterval1())));
        assertFalse(validator.validate(interval2, List.of(getInterval1())));
        interval1 = getInterval1();
        start = new Time();
        start.setHour(0);
        start.setMinute(1);
        interval1.setStart(start);
        end = new Time();
        end.setHour(0);
        end.setMinute(0);
        interval1.setEnd(end);
        assertFalse(validator.validateOverlap(interval1, List.of(getInterval2())));
        assertFalse(validator.validate(interval1, List.of(getInterval2())));
        interval1 = getInterval1();
        start = new Time();
        start.setHour(23);
        start.setMinute(59);
        interval1.setStart(start);
        end = new Time();
        end.setHour(0);
        end.setMinute(30);
        interval1.setEnd(end);
        assertTrue(validator.validateOverlap(interval1, List.of(getInterval3())));
        assertTrue(validator.validate(interval1, List.of(getInterval3())));
        interval1 = getInterval1();
        start = new Time();
        start.setHour(23);
        start.setMinute(59);
        interval1.setStart(start);
        end = new Time();
        end.setHour(2);
        end.setMinute(33);
        interval1.setEnd(end);
        assertTrue(validator.validateOverlap(interval1, List.of(getInterval3())));
        assertTrue(validator.validate(interval1, List.of(getInterval3())));
        start = new Time();
        start.setHour(23);
        start.setMinute(59);
        interval1.setStart(start);
        end = new Time();
        end.setHour(2);
        end.setMinute(49);
        interval1.setEnd(end);
        assertFalse(validator.validateOverlap(interval1, List.of(getInterval3())));
        assertFalse(validator.validate(interval1, List.of(getInterval3())));
        start = new Time();
        start.setHour(4);
        start.setMinute(18);
        interval1.setStart(start);
        end = new Time();
        end.setHour(4);
        end.setMinute(45);
        interval1.setEnd(end);
        assertFalse(validator.validateOverlap(interval1, List.of(getInterval3())));
        assertFalse(validator.validate(interval1, List.of(getInterval3())));
        start = new Time();
        start.setHour(4);
        start.setMinute(34);
        interval1.setStart(start);
        end = new Time();
        end.setHour(5);
        end.setMinute(4);
        interval1.setEnd(end);
        assertTrue(validator.validateOverlap(interval1, List.of(getInterval3())));
        assertTrue(validator.validate(interval1, List.of(getInterval3())));
    }

    @Test
    public void testValidateOverlapSorted() {
        assertTrue(validator.validateOverlapSorted(Collections.emptyList()));
        assertTrue(validator.validateOverlapSorted(List.of(getInterval1())));
        assertTrue(validator.validateOverlapSorted(List.of(getInterval2())));
        assertTrue(validator.validateOverlapSorted(List.of(getInterval3())));
        assertTrue(validator.validateOverlapSorted(Arrays.asList(getInterval2(), getInterval3(), getInterval1())));
        Interval interval2 = getInterval2();
        Time end = new Time();
        end.setHour(4);
        end.setMinute(4);
        interval2.setEnd(end);
        assertFalse(validator.validateOverlapSorted(Arrays.asList(interval2, getInterval3(), getInterval1())));
        Interval interval1 = getInterval1();
        end = new Time();
        end.setHour(1);
        end.setMinute(15);
        interval1.setEnd(end);
        assertFalse(validator.validateOverlapSorted(Arrays.asList(getInterval2(), getInterval3(), interval1)));
        interval1 = getInterval1();
        end = new Time();
        end.setHour(0);
        end.setMinute(50);
        interval1.setEnd(end);
        assertFalse(validator.validateOverlapSorted(Arrays.asList(getInterval2(), getInterval3(), interval1)));
        interval2 = getInterval2();
        end = new Time();
        end.setHour(2);
        end.setMinute(50);
        interval2.setEnd(end);
        assertFalse(validator.validateOverlapSorted(Arrays.asList(interval2, getInterval3(), getInterval1())));
        interval2 = getInterval2();
        end = new Time();
        end.setHour(2);
        end.setMinute(33);
        interval2.setEnd(end);
        assertTrue(validator.validateOverlapSorted(Arrays.asList(interval2, getInterval3(), getInterval1())));
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
        start.setHour(3);
        start.setMinute(3);
        interval.setStart(start);
        Time end = new Time();
        end.setHour(4);
        end.setMinute(4);
        interval.setEnd(end);
        return interval;
    }
}
