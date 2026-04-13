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

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class ResolveHostMatchExistsFieldValidatorTest {

    @Test
    public void testValidate() {
        Resolve resolve1 = new Resolve();
        resolve1.setSourceAddress("example.com");
        resolve1.setSourcePort(443);
        Resolve resolve2 = new Resolve();
        resolve2.setSourceAddress("other.com");
        resolve2.setSourcePort(80);
        Resolve resolve3 = new Resolve();
        resolve3.setSourceAddress("127.0.0.1");
        resolve3.setSourcePort(-1);
        Resolve resolve4 = new Resolve();
        resolve4.setSourceAddress("");
        resolve4.setSourcePort(8080);
        List<Resolve> resolves = List.of(resolve1, resolve2, resolve3, resolve4);
        ResolveHostMatchExistsFieldValidator validator = new ResolveHostMatchExistsFieldValidator("testfield", resolves, TestRegistry.getContext());
        ValidationResult result = validator.validate(null);
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("example.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("example.com:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("EXAMPLE.COM:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("example.com:80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("third.com:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("other.com:80");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("127.0.0.1:");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("127.0.0.1:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("127.0.0.1:80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate(":8080");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate(":443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("  example.com : 443 ");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("[example.com]:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("[EXAMPLE.COM]:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("[example.com]:80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("[127.0.0.1]:");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        ResolveHostMatchExistsFieldValidator emptyValidator = new ResolveHostMatchExistsFieldValidator("testfield", new ArrayList<>(), TestRegistry.getContext());
        result = emptyValidator.validate("example.com:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        // resolve with both empty address and negative port matches null/empty input (both sides empty+negative → equal)
        Resolve catchAllResolve = new Resolve();
        catchAllResolve.setSourceAddress("");
        catchAllResolve.setSourcePort(-1);
        ResolveHostMatchExistsFieldValidator catchAllValidator = new ResolveHostMatchExistsFieldValidator("testfield", List.of(catchAllResolve), TestRegistry.getContext());
        result = catchAllValidator.validate(null);
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = catchAllValidator.validate(":");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
    }

    @Test
    public void testValidateIPv6() {
        Resolve resolve1 = new Resolve();
        resolve1.setSourceAddress("::1");
        resolve1.setSourcePort(443);
        Resolve resolve2 = new Resolve();
        resolve2.setSourceAddress("::1");
        resolve2.setSourcePort(-1);
        List<Resolve> resolves = List.of(resolve1, resolve2);
        ResolveHostMatchExistsFieldValidator validator = new ResolveHostMatchExistsFieldValidator("testfield", resolves, TestRegistry.getContext());
        ValidationResult result = validator.validate("::1:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("[::1]:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("0:0:0:0:0:0:0:1:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("[0:0:0:0:0:0:0:1]:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("::1:80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("::1:");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("::2:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
    }
}
