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

package net.ibbaa.keepitup.model.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;

public class NetworkTaskValidatorTest {

    private NetworkTaskValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new NetworkTaskValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateAccessType() {
        NetworkTask task = getNetworkTask();
        assertTrue(validator.validateAccessType(task));
        assertTrue(validator.validate(task));
        task.setAccessType(null);
        assertFalse(validator.validateAccessType(task));
        assertFalse(validator.validate(task));
    }

    @Test
    public void testValidateAddress() {
        NetworkTask task = getNetworkTask();
        assertTrue(validator.validateAddress(task));
        assertTrue(validator.validate(task));
        task.setAddress("http:// xyz abc");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("123.456.788.111");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("127.0.0.1");
        assertTrue(validator.validateAddress(task));
        assertTrue(validator.validate(task));
    }

    @Test
    public void testValidatePort() {
        NetworkTask task = getNetworkTask();
        assertTrue(validator.validatePort(task));
        assertTrue(validator.validate(task));
        task.setPort(-1);
        assertFalse(validator.validatePort(task));
        assertFalse(validator.validate(task));
        task.setPort(65536);
        assertFalse(validator.validatePort(task));
        assertFalse(validator.validate(task));
        task.setPort(65535);
        assertTrue(validator.validatePort(task));
        assertTrue(validator.validate(task));
    }

    @Test
    public void testValidateInterval() {
        NetworkTask task = getNetworkTask();
        assertTrue(validator.validateInterval(task));
        assertTrue(validator.validate(task));
        task.setInterval(-1);
        assertFalse(validator.validateInterval(task));
        assertFalse(validator.validate(task));
        task.setInterval(Integer.MAX_VALUE);
        assertTrue(validator.validateInterval(task));
        assertTrue(validator.validate(task));
    }

    private NetworkTask getNetworkTask() {
        NetworkTask task = new NetworkTask();
        task.setId(0);
        task.setIndex(1);
        task.setSchedulerId(0);
        task.setInstances(1);
        task.setAddress("127.0.0.1");
        task.setPort(80);
        task.setAccessType(AccessType.PING);
        task.setInterval(15);
        task.setOnlyWifi(false);
        task.setNotification(true);
        task.setRunning(true);
        task.setLastScheduled(0);
        task.setFailureCount(1);
        return task;
    }
}
