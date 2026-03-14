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

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class BasicAuthPasswordFieldValidatorTest {

    @Test
    public void testValidate() {
        BasicAuthPasswordFieldValidator validator = new BasicAuthPasswordFieldValidator("Password", TestRegistry.getContext());
        ValidationResult result = validator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Password", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate("");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Password", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validate(new String(new char[4096]));
        assertFalse(result.isValidationSuccessful());
        assertEquals("Password", result.getFieldName());
        assertEquals("Maximum length: 4095", result.getMessage());
        result = validator.validate(" ");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Password", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("a");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Password", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate(new String(new char[4095]));
        assertTrue(result.isValidationSuccessful());
        assertEquals("Password", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
    }
}
