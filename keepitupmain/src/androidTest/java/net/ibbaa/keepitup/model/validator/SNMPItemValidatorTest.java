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

import net.ibbaa.keepitup.model.SNMPItem;
import net.ibbaa.keepitup.model.SNMPItemType;
import net.ibbaa.keepitup.model.validation.SNMPItemValidator;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPItemValidatorTest {

    private SNMPItemValidator validator;

    @Before
    public void beforeEachTestMethod() {
        validator = new SNMPItemValidator(TestRegistry.getContext());
    }

    @Test
    public void testValidateNameDescr() {
        SNMPItem item = getSNMPItem(null, "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("", "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem(new String(new char[256]), "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name\twith\ttabs", "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name\nwith\nnewline", "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name\rwith\rcarriage", "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name" + (char) 0x00, "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name" + (char) 0x1F, "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name" + (char) 0x7F, "1.3.6.1.2.1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("Alpha", "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("name with spaces", "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("nameä", "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("名前", "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem(new String(new char[255]).replace('\0', 'A'), "1.3.6.1.2.1");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
    }

    @Test
    public void testValidateNameType() {
        SNMPItem item = getSNMPItemType(null);
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItemType("");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItemType("abc");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItemType("0");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItemType("-1");
        assertFalse(validator.validateName(item));
        assertFalse(validator.validate(item));
        item = getSNMPItemType("5");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
        item = getSNMPItemType("100");
        assertTrue(validator.validateName(item));
        assertTrue(validator.validate(item));
    }

    @Test
    public void testValidateOID() {
        SNMPItem item = getSNMPItem("name", null);
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", new String(new char[257]).replace('\0', '1'));
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "1");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "1.");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", ".1.2");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "1.2.");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "1.a.2");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "abc");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "1 2 3");
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
        item = getSNMPItem("name", "1.2");
        assertTrue(validator.validateOID(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("name", "0.0");
        assertTrue(validator.validateOID(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("name", "1.3.6.1.2.1");
        assertTrue(validator.validateOID(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("name", "1.3.6.1.2.1.1.1.0");
        assertTrue(validator.validateOID(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("name", "100.200.300");
        assertTrue(validator.validateOID(item));
        assertTrue(validator.validate(item));
        item = getSNMPItem("name", new String(new char[256]).replace('\0', '1'));
        assertFalse(validator.validateOID(item));
        assertFalse(validator.validate(item));
    }

    @Test
    public void testValidateSNMPItemType() {
        SNMPItem item = getSNMPItem("name", "1.3.6.1.2.1");
        item.setSnmpItemType(null);
        assertFalse(validator.validateSNMPItemType(item));
        assertFalse(validator.validate(item));
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        assertTrue(validator.validateSNMPItemType(item));
        assertTrue(validator.validate(item));
        item.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        assertTrue(validator.validateSNMPItemType(item));
        assertFalse(validator.validate(item));
        item.setSnmpItemType(SNMPItemType.INTERFACEALIAS);
        assertTrue(validator.validateSNMPItemType(item));
        assertTrue(validator.validate(item));
    }

    private SNMPItem getSNMPItem(String name, String oid) {
        SNMPItem item = new SNMPItem();
        item.setId(0);
        item.setNetworkTaskId(0);
        item.setSnmpItemType(SNMPItemType.INTERFACEDESCR);
        item.setName(name);
        item.setOid(oid);
        return item;
    }

    private SNMPItem getSNMPItemType(String name) {
        SNMPItem item = new SNMPItem();
        item.setId(0);
        item.setNetworkTaskId(0);
        item.setSnmpItemType(SNMPItemType.INTERFACETYPE);
        item.setName(name);
        item.setOid("1.3.6.1.2.1");
        return item;
    }
}
