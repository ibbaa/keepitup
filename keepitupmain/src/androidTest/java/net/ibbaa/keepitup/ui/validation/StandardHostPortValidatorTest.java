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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StandardHostPortValidatorTest {

    private StandardHostPortValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardHostPortValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateAddress() {
        ValidationResult result = validator.validateAddress("www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateAddress("192.168.178.100");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateAddress("not valid");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("No valid host or IP address", result.getMessage());
        result = validator.validateAddress("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateAddress(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Host", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }

    @Test
    public void testValidatePort() {
        ValidationResult result = validator.validatePort("80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validatePort("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validatePort("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Minimum: 0", result.getMessage());
        result = validator.validatePort("12345678");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("Maximum: 65535", result.getMessage());
        result = validator.validatePort("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validatePort(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }

    @Test
    public void testValidateInterval() {
        ValidationResult result = validator.validateInterval("15");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateInterval("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validateInterval("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validateInterval(String.valueOf((long) Integer.MAX_VALUE + 1));
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("Maximum: " + Integer.MAX_VALUE, result.getMessage());
        result = validator.validateInterval("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateInterval(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
