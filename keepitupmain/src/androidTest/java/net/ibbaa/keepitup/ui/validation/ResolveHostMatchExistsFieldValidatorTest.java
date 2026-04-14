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
import net.ibbaa.keepitup.util.URLUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;
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
        ResolveHostMatchExistsFieldValidator validator = new ResolveHostMatchExistsFieldValidator("testfield", resolves, null, TestRegistry.getContext());
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
        ResolveHostMatchExistsFieldValidator emptyValidator = new ResolveHostMatchExistsFieldValidator("testfield", new ArrayList<>(), null, TestRegistry.getContext());
        result = emptyValidator.validate("example.com:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        Resolve catchAllResolve = new Resolve();
        catchAllResolve.setSourceAddress("");
        catchAllResolve.setSourcePort(-1);
        ResolveHostMatchExistsFieldValidator catchAllValidator = new ResolveHostMatchExistsFieldValidator("testfield", List.of(catchAllResolve), null, TestRegistry.getContext());
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
        ResolveHostMatchExistsFieldValidator validator = new ResolveHostMatchExistsFieldValidator("testfield", resolves, null, TestRegistry.getContext());
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

    @Test
    public void testValidateWithUrl() {
        URL url = URLUtil.getURL("http://example.com:443");
        Resolve resolveExplicit = new Resolve();
        resolveExplicit.setSourceAddress("example.com");
        resolveExplicit.setSourcePort(443);
        Resolve resolveEmpty = new Resolve();
        resolveEmpty.setSourceAddress("");
        resolveEmpty.setSourcePort(-1);
        ResolveHostMatchExistsFieldValidator validator = new ResolveHostMatchExistsFieldValidator("testfield", List.of(resolveExplicit), url, TestRegistry.getContext());
        ValidationResult result = validator.validate("example.com:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("testfield", result.getFieldName());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate(":443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("example.com:");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate(":");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validator.validate("other.com:443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate(":80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validator.validate("other.com:");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        ResolveHostMatchExistsFieldValidator validatorEmpty = new ResolveHostMatchExistsFieldValidator("testfield", List.of(resolveEmpty), url, TestRegistry.getContext());
        result = validatorEmpty.validate(":");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validatorEmpty.validate(":443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validatorEmpty.validate("example.com:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validatorEmpty.validate(":80");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        ResolveHostMatchExistsFieldValidator validatorNullUrl = new ResolveHostMatchExistsFieldValidator("testfield", List.of(resolveExplicit), null, TestRegistry.getContext());
        result = validatorNullUrl.validate("example.com:443");
        assertFalse(result.isValidationSuccessful());
        assertEquals("Value already exists", result.getMessage());
        result = validatorNullUrl.validate(":443");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
        result = validatorNullUrl.validate("example.com:");
        assertTrue(result.isValidationSuccessful());
        assertEquals("Validation successful", result.getMessage());
    }
}
