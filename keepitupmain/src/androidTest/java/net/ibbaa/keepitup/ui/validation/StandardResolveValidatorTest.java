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

package net.ibbaa.keepitup.ui.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StandardResolveValidatorTest {

    private StandardResolveValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardResolveValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateValueSet() {
        Resolve resolve = new Resolve();
        ValidationResult result = validator.validateValueSet(resolve);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value", result.getFieldName());
        assertEquals("At least one value must be specified", result.getMessage());
        resolve.setSourceAddress("127.0.0.1");
        result = validator.validateValueSet(resolve);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        resolve = new Resolve();
        resolve.setSourcePort(80);
        result = validator.validateValueSet(resolve);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        resolve = new Resolve();
        resolve.setTargetAddress("127.0.0.1");
        result = validator.validateValueSet(resolve);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        resolve = new Resolve();
        resolve.setTargetPort(80);
        result = validator.validateValueSet(resolve);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }

    @Test
    public void testValidateSourceAddress() {
        ValidationResult result = validator.validateSourceAddress("www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourceAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourceAddress("192.168.178.100");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourceAddress("not valid");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("No valid host or IP address", result.getMessage());
        result = validator.validateSourceAddress("");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourceAddress(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourceAddress("not set");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }

    @Test
    public void testValidateSourcePort() {
        ValidationResult result = validator.validateSourcePort("80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourcePort("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validateSourcePort("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Minimum: 0", result.getMessage());
        result = validator.validateSourcePort("12345678");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Maximum: 65535", result.getMessage());
        result = validator.validateSourcePort("");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourcePort(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourcePort("not set");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }

    @Test
    public void testValidateSourceExists() {
        Resolve resolve1 = new Resolve();
        resolve1.setSourceAddress("example.com");
        resolve1.setSourcePort(443);
        Resolve resolve2 = new Resolve();
        resolve2.setSourceAddress("");
        resolve2.setSourcePort(-1);
        ValidationResult result = validator.validateSourceExists(List.of(resolve1), "example.com:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Match host/port", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateSourceExists(List.of(resolve1), "example.com:80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Match host/port", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateSourceExists(List.of(resolve2), null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateSourceExists(List.of(), "example.com:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
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
