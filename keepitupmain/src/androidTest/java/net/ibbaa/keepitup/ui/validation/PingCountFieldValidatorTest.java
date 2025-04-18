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

package net.ibbaa.keepitup.ui.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;

public class PingCountFieldValidatorTest {

    @Test
    public void testValidate() {
        PingCountFieldValidator validator = new PingCountFieldValidator("testpingcount", TestRegistry.getContext());
        ValidationResult result = validator.validate("10");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testpingcount", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testpingcount", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validate("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testpingcount", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validate(String.valueOf(Long.MAX_VALUE));
        assertFalse(result.isValidationSuccessful());
        assertEquals("testpingcount", result.getFieldName());
        assertEquals("Maximum: 10", result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testpingcount", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testpingcount", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
