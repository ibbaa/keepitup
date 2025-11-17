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

package net.ibbaa.keepitup.model.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.model.validation.HeaderValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class HeaderValidatorTest {

    private HeaderValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new HeaderValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateName() {
        Header header = getHeader(null, "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("   ", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader(new String(new char[129]), "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content Type", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content:Type", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Äpfel", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Name\nTest", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Name\rTest", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Name\tTest", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Name,Test", "Test");
        assertFalse(validator.validateName(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", "Test");
        assertTrue(validator.validateName(header));
        assertTrue(validator.validate(header));
        header = getHeader("  Content-Type ", "Test");
        assertTrue(validator.validateName(header));
        assertTrue(validator.validate(header));
    }

    @Test
    public void testValidateValue() {
        Header header = getHeader("Content-Type", null);
        assertFalse(validator.validateValue(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", new String(new char[8193]));
        assertFalse(validator.validateValue(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", "Test\nMore");
        assertFalse(validator.validateValue(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", "Test\rMore");
        assertFalse(validator.validateValue(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", "Test\u0001More");
        assertFalse(validator.validateValue(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", "Test\u007FMore");
        assertFalse(validator.validateValue(header));
        assertFalse(validator.validate(header));
        header = getHeader("Content-Type", "Test\tMore");
        assertTrue(validator.validateValue(header));
        assertTrue(validator.validate(header));
        header = getHeader("Content-Type", "Äpfel mit Ümläutén");
        assertTrue(validator.validateValue(header));
        assertTrue(validator.validate(header));
        header = getHeader("Content-Type", "  ");
        assertTrue(validator.validateValue(header));
        assertTrue(validator.validate(header));
        header = getHeader("Content-Type", "");
        assertTrue(validator.validateValue(header));
        assertTrue(validator.validate(header));
        header = getHeader("Content-Type", "Test");
        assertTrue(validator.validateValue(header));
        assertTrue(validator.validate(header));
        header = getHeader("Content-Type", "  Test ");
        assertTrue(validator.validateValue(header));
        assertTrue(validator.validate(header));
    }

    private Header getHeader(String name, String value) {
        Header header = new Header();
        header.setId(0);
        header.setNetworkTaskId(0);
        header.setName(name);
        header.setValue(value);
        return header;
    }
}
