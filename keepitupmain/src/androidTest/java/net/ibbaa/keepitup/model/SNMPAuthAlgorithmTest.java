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
public class SNMPAuthAlgorithmTest {

    @Test
    public void testValueMethods() {
        SNMPAuthAlgorithm algorithm = SNMPAuthAlgorithm.NONE;
        assertTrue(algorithm.isNone());
        assertFalse(algorithm.isMD5());
        assertFalse(algorithm.isSHA1());
        assertFalse(algorithm.isSHA224());
        assertFalse(algorithm.isSHA256());
        assertFalse(algorithm.isSHA384());
        assertFalse(algorithm.isSHA512());
        algorithm = SNMPAuthAlgorithm.MD5;
        assertFalse(algorithm.isNone());
        assertTrue(algorithm.isMD5());
        assertFalse(algorithm.isSHA1());
        assertFalse(algorithm.isSHA224());
        assertFalse(algorithm.isSHA256());
        assertFalse(algorithm.isSHA384());
        assertFalse(algorithm.isSHA512());
        algorithm = SNMPAuthAlgorithm.SHA1;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isMD5());
        assertTrue(algorithm.isSHA1());
        assertFalse(algorithm.isSHA224());
        assertFalse(algorithm.isSHA256());
        assertFalse(algorithm.isSHA384());
        assertFalse(algorithm.isSHA512());
        algorithm = SNMPAuthAlgorithm.SHA224;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isMD5());
        assertFalse(algorithm.isSHA1());
        assertTrue(algorithm.isSHA224());
        assertFalse(algorithm.isSHA256());
        assertFalse(algorithm.isSHA384());
        assertFalse(algorithm.isSHA512());
        algorithm = SNMPAuthAlgorithm.SHA256;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isMD5());
        assertFalse(algorithm.isSHA1());
        assertFalse(algorithm.isSHA224());
        assertTrue(algorithm.isSHA256());
        assertFalse(algorithm.isSHA384());
        assertFalse(algorithm.isSHA512());
        algorithm = SNMPAuthAlgorithm.SHA384;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isMD5());
        assertFalse(algorithm.isSHA1());
        assertFalse(algorithm.isSHA224());
        assertFalse(algorithm.isSHA256());
        assertTrue(algorithm.isSHA384());
        assertFalse(algorithm.isSHA512());
        algorithm = SNMPAuthAlgorithm.SHA512;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isMD5());
        assertFalse(algorithm.isSHA1());
        assertFalse(algorithm.isSHA224());
        assertFalse(algorithm.isSHA256());
        assertFalse(algorithm.isSHA384());
        assertTrue(algorithm.isSHA512());
    }

    @Test
    public void testForCode() {
        assertEquals(SNMPAuthAlgorithm.NONE, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.NONE.getCode()));
        assertEquals(SNMPAuthAlgorithm.MD5, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.MD5.getCode()));
        assertEquals(SNMPAuthAlgorithm.SHA1, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.SHA1.getCode()));
        assertEquals(SNMPAuthAlgorithm.SHA224, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.SHA224.getCode()));
        assertEquals(SNMPAuthAlgorithm.SHA256, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.SHA256.getCode()));
        assertEquals(SNMPAuthAlgorithm.SHA384, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.SHA384.getCode()));
        assertEquals(SNMPAuthAlgorithm.SHA512, SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.SHA512.getCode()));
        assertNull(SNMPAuthAlgorithm.forCode(SNMPAuthAlgorithm.SHA512.getCode() + 1));
    }
}
