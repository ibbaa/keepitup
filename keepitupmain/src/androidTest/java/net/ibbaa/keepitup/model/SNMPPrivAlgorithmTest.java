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
public class SNMPPrivAlgorithmTest {

    @Test
    public void testValueMethods() {
        SNMPPrivAlgorithm algorithm = SNMPPrivAlgorithm.NONE;
        assertTrue(algorithm.isNone());
        assertFalse(algorithm.isDES());
        assertFalse(algorithm.isAES128());
        assertFalse(algorithm.isAES192());
        assertFalse(algorithm.isAES256());
        assertFalse(algorithm.isAES256C());
        algorithm = SNMPPrivAlgorithm.DES;
        assertFalse(algorithm.isNone());
        assertTrue(algorithm.isDES());
        assertFalse(algorithm.isAES128());
        assertFalse(algorithm.isAES192());
        assertFalse(algorithm.isAES256());
        assertFalse(algorithm.isAES256C());
        algorithm = SNMPPrivAlgorithm.AES128;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isDES());
        assertTrue(algorithm.isAES128());
        assertFalse(algorithm.isAES192());
        assertFalse(algorithm.isAES256());
        assertFalse(algorithm.isAES256C());
        algorithm = SNMPPrivAlgorithm.AES192;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isDES());
        assertFalse(algorithm.isAES128());
        assertTrue(algorithm.isAES192());
        assertFalse(algorithm.isAES256());
        assertFalse(algorithm.isAES256C());
        algorithm = SNMPPrivAlgorithm.AES256;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isDES());
        assertFalse(algorithm.isAES128());
        assertFalse(algorithm.isAES192());
        assertTrue(algorithm.isAES256());
        assertFalse(algorithm.isAES256C());
        algorithm = SNMPPrivAlgorithm.AES256C;
        assertFalse(algorithm.isNone());
        assertFalse(algorithm.isDES());
        assertFalse(algorithm.isAES128());
        assertFalse(algorithm.isAES192());
        assertFalse(algorithm.isAES256());
        assertTrue(algorithm.isAES256C());
    }

    @Test
    public void testForCode() {
        assertEquals(SNMPPrivAlgorithm.NONE, SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.NONE.getCode()));
        assertEquals(SNMPPrivAlgorithm.DES, SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.DES.getCode()));
        assertEquals(SNMPPrivAlgorithm.AES128, SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.AES128.getCode()));
        assertEquals(SNMPPrivAlgorithm.AES192, SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.AES192.getCode()));
        assertEquals(SNMPPrivAlgorithm.AES256, SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.AES256.getCode()));
        assertEquals(SNMPPrivAlgorithm.AES256C, SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.AES256C.getCode()));
        assertNull(SNMPPrivAlgorithm.forCode(SNMPPrivAlgorithm.AES256C.getCode() + 1));
    }
}
