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

package net.ibbaa.keepitup.model.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.SNMPInterfaceInfo;
import net.ibbaa.keepitup.model.validation.SNMPInterfaceInfoValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPInterfaceInfoValidatorTest {

    private SNMPInterfaceInfoValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new SNMPInterfaceInfoValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateDescr() {
        SNMPInterfaceInfo info = getSNMPInterfaceInfo(null, null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo("", null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(new String(new char[256]).replace('\0', 'A'), null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(new String(new char[255]).replace('\0', 'A'), null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0\twith\ttabs", null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0\nwith\nnewline", null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0\rwith\rcarriage", null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0" + (char) 0x00, null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0" + (char) 0x1F, null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0" + (char) 0x7F, null, 1, 1);
        assertFalse(validator.validateDescr(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0", null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0 with spaces", null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo("eth0ä", null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo("名前", null, 1, 1);
        assertTrue(validator.validateDescr(info));
        assertTrue(validator.validate(info));
    }

    @Test
    public void testValidateAlias() {
        SNMPInterfaceInfo info = getSNMPInterfaceInfo(null, null, 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "", 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, new String(new char[256]).replace('\0', 'A'), 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, new String(new char[255]).replace('\0', 'A'), 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias\twith\ttabs", 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias\nwith\nnewline", 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias\rwith\rcarriage", 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias" + (char) 0x00, 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias" + (char) 0x1F, 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias" + (char) 0x7F, 1, 1);
        assertFalse(validator.validateAlias(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "Loopback", 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "alias with spaces", 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "aliasä", 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, "別名", 1, 1);
        assertTrue(validator.validateAlias(info));
        assertTrue(validator.validate(info));
    }

    @Test
    public void testValidateType() {
        SNMPInterfaceInfo info = getSNMPInterfaceInfo(null, null, 0, 1);
        assertFalse(validator.validateType(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, -1, 1);
        assertFalse(validator.validateType(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 1, 1);
        assertTrue(validator.validateType(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 6, 1);
        assertTrue(validator.validateType(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 100, 1);
        assertTrue(validator.validateType(info));
        assertTrue(validator.validate(info));
    }

    @Test
    public void testValidateStatus() {
        SNMPInterfaceInfo info = getSNMPInterfaceInfo(null, null, 1, 0);
        assertFalse(validator.validateStatus(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 1, -1);
        assertFalse(validator.validateStatus(info));
        assertFalse(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 1, 1);
        assertTrue(validator.validateStatus(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 1, 2);
        assertTrue(validator.validateStatus(info));
        assertTrue(validator.validate(info));
        info = getSNMPInterfaceInfo(null, null, 1, 100);
        assertTrue(validator.validateStatus(info));
        assertTrue(validator.validate(info));
    }

    private SNMPInterfaceInfo getSNMPInterfaceInfo(String descr, String alias, int type, int status) {
        SNMPInterfaceInfo info = new SNMPInterfaceInfo();
        info.setDescr(descr);
        info.setAlias(alias);
        info.setType(type);
        info.setStatus(status);
        return info;
    }
}
