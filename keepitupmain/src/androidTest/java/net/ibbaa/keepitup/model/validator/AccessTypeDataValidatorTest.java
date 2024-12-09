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

import net.ibbaa.keepitup.model.AccessTypeData;
import net.ibbaa.keepitup.model.validation.AccessTypeDataValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;

public class AccessTypeDataValidatorTest {

    private AccessTypeDataValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new AccessTypeDataValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidatePingCount() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validatePingCount(data));
        assertTrue(validator.validate(data));
        data.setPingCount(11);
        assertFalse(validator.validatePingCount(data));
        assertFalse(validator.validate(data));
        data.setPingCount(0);
        assertFalse(validator.validatePingCount(data));
        assertFalse(validator.validate(data));
        data.setPingCount(10);
        assertTrue(validator.validatePingCount(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidatePingPackageSize() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validatePingPackageSize(data));
        assertTrue(validator.validate(data));
        data.setPingPackageSize(65528);
        assertFalse(validator.validatePingPackageSize(data));
        assertFalse(validator.validate(data));
        data.setPingPackageSize(-1);
        assertFalse(validator.validatePingPackageSize(data));
        assertFalse(validator.validate(data));
        data.setPingPackageSize(65527);
        assertTrue(validator.validatePingPackageSize(data));
        assertTrue(validator.validate(data));
        data.setPingPackageSize(0);
        assertTrue(validator.validatePingPackageSize(data));
        assertTrue(validator.validate(data));
    }

    @Test
    public void testValidateConnectCount() {
        AccessTypeData data = getAccessTypeData();
        assertTrue(validator.validateConnectCount(data));
        assertTrue(validator.validate(data));
        data.setConnectCount(11);
        assertFalse(validator.validateConnectCount(data));
        assertFalse(validator.validate(data));
        data.setConnectCount(0);
        assertFalse(validator.validateConnectCount(data));
        assertFalse(validator.validate(data));
        data.setConnectCount(10);
        assertTrue(validator.validateConnectCount(data));
        assertTrue(validator.validate(data));
    }

    private AccessTypeData getAccessTypeData() {
        AccessTypeData data = new AccessTypeData();
        data.setId(0);
        data.setNetworkTaskId(0);
        data.setPingCount(3);
        data.setPingPackageSize(56);
        data.setConnectCount(1);
        data.setStopOnSuccess(true);
        return data;
    }
}
