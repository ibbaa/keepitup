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
public class StandardResolveValidatorTest {

    private StandardResolveValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardResolveValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateTargetAddress() {
        ValidationResult result = validator.validateTargetAddress("www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetAddress("192.168.178.100");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetAddress("not valid");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("No valid host or IP address", result.getMessage());
        result = validator.validateTargetAddress("");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetAddress(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetAddress("not set");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }

    @Test
    public void testValidateTargetPort() {
        ValidationResult result = validator.validateTargetPort("80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetPort("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validateTargetPort("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Minimum: 0", result.getMessage());
        result = validator.validateTargetPort("12345678");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Maximum: 65535", result.getMessage());
        result = validator.validateTargetPort("");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetPort(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateTargetPort("not set");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect-to port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }
}
