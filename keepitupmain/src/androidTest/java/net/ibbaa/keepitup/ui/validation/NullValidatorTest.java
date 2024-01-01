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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NullValidatorTest {

    private NullValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new NullValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidate() {
        ValidationResult result = validator.validateAddress("www.host.com");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Address", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validatePort("23");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Port", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
        result = validator.validateInterval("1");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Interval", result.getFieldName());
        assertEquals("No value specified", result.getMessage());
    }
}
