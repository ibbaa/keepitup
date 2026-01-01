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

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StandardHeaderValidatorTest {

    private StandardHeaderValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardHeaderValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateName() {
        ValidationResult result = validator.validateName(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateName("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateName("  ");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateName("Äpfel");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value contains invalid characters", result.getMessage());
        result = validator.validateName("Name\tTest");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value contains invalid characters", result.getMessage());
        result = validator.validateName("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789123456789012345678901234567890");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Maximum length: 128", result.getMessage());
        result = validator.validateName("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567891234567890123456789");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateName("   12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567891234567890123456789   ");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateName("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567891234567890123456789");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateName("abc");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }

    @Test
    public void testValidateNameExists() {
        List<String> existingNames = List.of("", "name1", "name2");
        ValidationResult result = validator.validateNameExists(existingNames, null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateNameExists(existingNames, "");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateNameExists(existingNames, "  ");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateNameExists(existingNames, " name2 ");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateNameExists(existingNames, "name1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validateNameExists(existingNames, "name3");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header name", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }

    @Test
    public void testValidateValue() {
        ValidationResult result = validator.validateValue(new String(new char[8193]));
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Maximum length: 8192", result.getMessage());
        result = validator.validateValue("Test\nMore");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Value contains invalid characters", result.getMessage());
        result = validator.validateValue("Test\u007FMore");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Value contains invalid characters", result.getMessage());
        result = validator.validateValue(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateValue("");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateValue("  ");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateValue("Äpfel");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateValue("Test\tMore");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validateValue("abc");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Header value", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }
}
