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

package net.ibbaa.keepitup.ui.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.test.mock.TestRegistry;
import net.ibbaa.keepitup.ui.dialog.ContextOption;
import net.ibbaa.keepitup.ui.validation.NullAccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.NullNetworkTaskValidator;
import net.ibbaa.keepitup.ui.validation.StandardAccessTypeDataValidator;
import net.ibbaa.keepitup.ui.validation.StandardHostPortValidator;
import net.ibbaa.keepitup.ui.validation.URLValidator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class EnumMappingTest {

    private EnumMapping enumMapping;

    @Before
    public void beforeEachTestMethod() {
        enumMapping = new EnumMapping(TestRegistry.getContext());
    }

    @Test
    public void testGetAccessTypeText() {
        assertEquals("Ping", enumMapping.getAccessTypeText(AccessType.PING));
        assertEquals("Connect", enumMapping.getAccessTypeText(AccessType.CONNECT));
        assertEquals("Host: %s", enumMapping.getAccessTypeAddressText(AccessType.PING));
        assertEquals("Host: %s Port: %d", enumMapping.getAccessTypeAddressText(AccessType.CONNECT));
        assertEquals("Host:", enumMapping.getAccessTypeAddressLabel(AccessType.PING));
        assertEquals("Port:", enumMapping.getAccessTypePortLabel(AccessType.PING));
        assertEquals("Host:", enumMapping.getAccessTypeAddressLabel(AccessType.CONNECT));
        assertEquals("Port:", enumMapping.getAccessTypePortLabel(AccessType.CONNECT));
        assertEquals("No type", enumMapping.getAccessTypeText(null));
        assertEquals("Host: not applicable", enumMapping.getAccessTypeAddressText(null));
    }

    @Test
    public void testGetNetworkTaskValidator() {
        assertTrue(enumMapping.getNetworkTaskValidator(null) instanceof NullNetworkTaskValidator);
        assertTrue(enumMapping.getNetworkTaskValidator(AccessType.PING) instanceof StandardHostPortValidator);
        assertTrue(enumMapping.getNetworkTaskValidator(AccessType.CONNECT) instanceof StandardHostPortValidator);
        assertTrue(enumMapping.getNetworkTaskValidator(AccessType.DOWNLOAD) instanceof URLValidator);
    }

    @Test
    public void testGetAccessTypeDataValidator() {
        assertTrue(enumMapping.getAccessTypeDataValidator(null) instanceof NullAccessTypeDataValidator);
        assertTrue(enumMapping.getAccessTypeDataValidator(AccessType.PING) instanceof StandardAccessTypeDataValidator);
        assertTrue(enumMapping.getAccessTypeDataValidator(AccessType.CONNECT) instanceof StandardAccessTypeDataValidator);
        assertTrue(enumMapping.getAccessTypeDataValidator(AccessType.DOWNLOAD) instanceof StandardAccessTypeDataValidator);
    }

    @Test
    public void testGetContextOptionName() {
        assertEquals("Copy", enumMapping.getContextOptionName(ContextOption.COPY));
        assertEquals("Paste", enumMapping.getContextOptionName(ContextOption.PASTE));
    }
}
