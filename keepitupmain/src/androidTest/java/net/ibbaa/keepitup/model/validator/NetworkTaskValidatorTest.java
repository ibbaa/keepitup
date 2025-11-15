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

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.NetworkTask;
import net.ibbaa.keepitup.model.validation.NetworkTaskValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NetworkTaskValidatorTest {

    private NetworkTaskValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new NetworkTaskValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateName() {
        NetworkTask task = getNetworkTask();
        assertTrue(validator.validateName(task));
        assertTrue(validator.validate(task));
        task.setName(null);
        assertTrue(validator.validateName(task));
        assertTrue(validator.validate(task));
        task.setName("");
        assertTrue(validator.validateName(task));
        assertTrue(validator.validate(task));
        task.setName("1");
        assertTrue(validator.validateName(task));
        assertTrue(validator.validate(task));
        task.setName("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        assertTrue(validator.validateName(task));
        assertTrue(validator.validate(task));
        task.setName("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
        assertFalse(validator.validateName(task));
        assertFalse(validator.validate(task));
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
    public void testValidateAddressPing() {
        NetworkTask task = getNetworkTask();
        assertTrue(validator.validateAddress(task));
        assertTrue(validator.validate(task));
        task.setAddress("http://xyz.com");
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
    public void testValidateAddressConnect() {
        NetworkTask task = getNetworkTask();
        task.setAccessType(AccessType.CONNECT);
        assertTrue(validator.validateAddress(task));
        assertTrue(validator.validate(task));
        task.setAddress("http://xyz.com");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("123.456.788.111");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("127.0.0.1.5");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
    }

    @Test
    public void testValidateAddressDownload() {
        NetworkTask task = getNetworkTask();
        task.setAccessType(AccessType.DOWNLOAD);
        task.setAddress("http://xyz.com");
        assertTrue(validator.validateAddress(task));
        assertTrue(validator.validate(task));
        task.setAddress("http:// xyz com");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("123.456.788.111");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("127.0.0.1");
        assertFalse(validator.validateAddress(task));
        assertFalse(validator.validate(task));
        task.setAddress("http://127.0.0.1");
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
        task.setName("name");
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
        task.setHighPrio(true);
        return task;
    }
}
