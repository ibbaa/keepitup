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

package net.ibbaa.keepitup.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SNMPVersionTest {

    @Test
    public void testValueMethods() {
        SNMPVersion version = SNMPVersion.V1;
        assertTrue(version.isV1());
        assertFalse(version.isV2C());
        version = SNMPVersion.V2C;
        assertFalse(version.isV1());
        assertTrue(version.isV2C());
    }

    @Test
    public void testForCode() {
        assertEquals(SNMPVersion.V1, SNMPVersion.forCode(SNMPVersion.V1.getCode()));
        assertEquals(SNMPVersion.V2C, SNMPVersion.forCode(SNMPVersion.V2C.getCode()));
        assertNull(SNMPVersion.forCode(SNMPVersion.V2C.getCode() + 1));
    }
}
