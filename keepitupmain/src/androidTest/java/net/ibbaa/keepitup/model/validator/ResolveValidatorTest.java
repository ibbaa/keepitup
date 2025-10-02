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

import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.model.validation.ResolveValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;

public class ResolveValidatorTest {

    private ResolveValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new ResolveValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateSourceAddress() {
        Resolve resolve = getResolve();
        assertTrue(validator.validateSourceAddress(resolve));
        assertTrue(validator.validate(resolve));
        resolve.setSourceAddress("http://xyz.com");
        assertFalse(validator.validateSourceAddress(resolve));
        assertFalse(validator.validate(resolve));
        resolve.setSourceAddress("123.456.788.111");
        assertFalse(validator.validateSourceAddress(resolve));
        assertFalse(validator.validate(resolve));
        resolve.setSourceAddress("127.0.0.1");
        assertTrue(validator.validateSourceAddress(resolve));
        assertTrue(validator.validate(resolve));
    }

    @Test
    public void testValidateSourcePort() {
        Resolve resolve = getResolve();
        assertTrue(validator.validateSourcePort(resolve));
        assertTrue(validator.validate(resolve));
        resolve.setSourcePort(-1);
        assertTrue(validator.validateSourcePort(resolve));
        assertTrue(validator.validate(resolve));
        resolve.setSourcePort(65536);
        assertFalse(validator.validateSourcePort(resolve));
        assertFalse(validator.validate(resolve));
        resolve.setSourcePort(65535);
        assertTrue(validator.validateSourcePort(resolve));
        assertTrue(validator.validate(resolve));
    }

    @Test
    public void testValidateTargetAddress() {
        Resolve resolve = getResolve();
        assertTrue(validator.validateTargetAddress(resolve));
        assertTrue(validator.validate(resolve));
        resolve.setTargetAddress("http://xyz.com");
        assertFalse(validator.validateTargetAddress(resolve));
        assertFalse(validator.validate(resolve));
        resolve.setTargetAddress("123.456.788.111");
        assertFalse(validator.validateTargetAddress(resolve));
        assertFalse(validator.validate(resolve));
        resolve.setTargetAddress("127.0.0.1");
        assertTrue(validator.validateTargetAddress(resolve));
        assertTrue(validator.validate(resolve));
    }

    @Test
    public void testValidateTargetPort() {
        Resolve resolve = getResolve();
        assertTrue(validator.validateTargetPort(resolve));
        assertTrue(validator.validate(resolve));
        resolve.setTargetPort(-1);
        assertTrue(validator.validateTargetPort(resolve));
        assertTrue(validator.validate(resolve));
        resolve.setTargetPort(65536);
        assertFalse(validator.validateTargetPort(resolve));
        assertFalse(validator.validate(resolve));
        resolve.setTargetPort(65535);
        assertTrue(validator.validateTargetPort(resolve));
        assertTrue(validator.validate(resolve));
    }

    private Resolve getResolve() {
        Resolve resolve = new Resolve();
        resolve.setId(0);
        resolve.setNetworkTaskId(1);
        resolve.setSourceAddress("");
        resolve.setSourcePort(-1);
        resolve.setTargetAddress("127.0.0.1");
        resolve.setTargetPort(80);
        return resolve;
    }
}
