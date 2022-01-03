/*
 * Copyright (c) 2022. Alwin Ibba
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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HostFieldValidatorTest {

    @Test
    public void testValidate() {
        HostFieldValidator validator = new HostFieldValidator("testhost", TestRegistry.getContext());
        ValidationResult result = validator.validate("www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testhost", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testhost", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("not valid");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testhost", result.getFieldName());
        assertEquals("No valid host or IP address", result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testhost", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("testhost", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
