/*
 * Copyright (c) 2024. Alwin Ibba
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
public class StandardAccessTypeDataValidatorTest {

    private StandardAccessTypeDataValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardAccessTypeDataValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidatePingCount() {
        ValidationResult result = validator.validatePingCount("10");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Ping count", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validatePingCount("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Ping count", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validatePingCount("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Ping count", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validatePingCount("11");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Ping count", result.getFieldName());
        assertEquals("Maximum: 10", result.getMessage());
        result = validator.validatePingCount("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Ping count", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validatePingCount(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Ping count", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }

    @Test
    public void testValidatePingPackageSize() {
        ValidationResult result = validator.validatePingPackageSize("65527");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Package size", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validatePingPackageSize("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Package size", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validatePingPackageSize("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Package size", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validatePingPackageSize("65528");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Package size", result.getFieldName());
        assertEquals("Maximum: 65527", result.getMessage());
        result = validator.validatePingPackageSize("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Package size", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validatePingPackageSize(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Package size", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }

    @Test
    public void testValidateConnectCount() {
        ValidationResult result = validator.validateConnectCount("10");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Connect count", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateConnectCount("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect count", result.getFieldName());
        assertEquals("Invalid format", result.getMessage());
        result = validator.validateConnectCount("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect count", result.getFieldName());
        assertEquals("Minimum: 1", result.getMessage());
        result = validator.validateConnectCount("11");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect count", result.getFieldName());
        assertEquals("Maximum: 10", result.getMessage());
        result = validator.validateConnectCount("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect count", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateConnectCount(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Connect count", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
