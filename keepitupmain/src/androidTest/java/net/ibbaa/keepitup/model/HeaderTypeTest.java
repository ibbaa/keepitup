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
public class HeaderTypeTest {

    @Test
    public void testValueMethods() {
        HeaderType type = HeaderType.GENERIC;
        assertFalse(type.isSecret());
        assertTrue(type.isGeneric());
        assertFalse(type.isBasicAuth());
        assertFalse(type.isGenericAuth());
        type = HeaderType.BASICAUTH;
        assertTrue(type.isSecret());
        assertFalse(type.isGeneric());
        assertTrue(type.isBasicAuth());
        assertFalse(type.isGenericAuth());
        type = HeaderType.GENERICAUTH;
        assertTrue(type.isSecret());
        assertFalse(type.isGeneric());
        assertFalse(type.isBasicAuth());
        assertTrue(type.isGenericAuth());
    }

    @Test
    public void testForCode() {
        assertEquals(HeaderType.GENERIC, HeaderType.forCode(HeaderType.GENERIC.getCode()));
        assertEquals(HeaderType.BASICAUTH, HeaderType.forCode(HeaderType.BASICAUTH.getCode()));
        assertEquals(HeaderType.GENERICAUTH, HeaderType.forCode(HeaderType.GENERICAUTH.getCode()));
        assertNull(HeaderType.forCode(HeaderType.GENERICAUTH.getCode() + 1));
    }
}
