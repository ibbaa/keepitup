/*
 * Copyright (c) 2021. Alwin Ibba
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

import org.junit.Test;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalFieldValidatorTest {

    @Test
    public void testValidate() {
        IntervalFieldValidator validator = new IntervalFieldValidator("testinterval", TestRegistry.getContext());
        ValidationResult result = validator.validate("15");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validate("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validate(String.valueOf(Long.MAX_VALUE));
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("Maximum: " + Integer.MAX_VALUE, result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testinterval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
