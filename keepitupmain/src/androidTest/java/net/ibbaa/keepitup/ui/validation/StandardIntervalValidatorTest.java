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

package net.ibbaa.keepitup.ui.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.model.Time;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StandardIntervalValidatorTest {

    private StandardIntervalValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardIntervalValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateDuration() {
        Interval interval = getInterval1();
        ValidationResult result = validator.validateDuration(interval);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        Time end = new Time();
        end.setHour(10);
        end.setMinute(15);
        interval.setEnd(end);
        result = validator.validateDuration(interval);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Intervals must last at least 15 minutes", result.getMessage());
    }

    @Test
    public void testValidateOverlap() {
        Interval interval1 = getInterval1();
        ValidationResult result = validator.validateOverlap(interval1, Arrays.asList(getInterval2(), getInterval3()));
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateOverlap(interval1, Arrays.asList(getInterval1(), getInterval2(), getInterval3()));
        assertFalse(result.isValidationSuccessful());
        assertEquals("Intervals must not overlap and must have a distance of at least 15 minutes from each other", result.getMessage());
    }

    @Test
    public void testValidateIsInInterval() {
        Time time = new Time();
        time.setHour(3);
        time.setMinute(30);
        ValidationResult result = validator.validateInInterval(time, Arrays.asList(getInterval1(), getInterval2()));
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateInInterval(time, Arrays.asList(getInterval1(), getInterval2(), getInterval3()));
        assertFalse(result.isValidationSuccessful());
        assertEquals("Intervals must not overlap and must have a distance of at least 15 minutes from each other", result.getMessage());
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
