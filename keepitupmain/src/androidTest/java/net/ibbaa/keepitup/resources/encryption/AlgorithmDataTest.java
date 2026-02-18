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

package net.ibbaa.keepitup.resources.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class AlgorithmDataTest {

    @Test
    public void testForName() {
        assertSame(AlgorithmData.Algorithm.ARGON2ID, AlgorithmData.Algorithm.forName("Argon2id"));
        assertSame(AlgorithmData.Algorithm.AES256GCM, AlgorithmData.Algorithm.forName("AES-256-GCM"));
    }

    @Test
    public void testGetDefaultAlgorithm() {
        AlgorithmData data = new AlgorithmData(TestRegistry.getContext());
        assertSame(AlgorithmData.Algorithm.ARGON2ID, data.getDefaultKDF());
        assertSame(AlgorithmData.Algorithm.AES256GCM, data.getDefaultCipher());
    }

    @Test
    public void testGetAlgorithmDefaultParam() {
        AlgorithmData data = new AlgorithmData(TestRegistry.getContext());
        Map<String, String> kdfParam = data.getAlgorithmDefaultParam(AlgorithmData.Algorithm.ARGON2ID);
        assertEquals("Argon2id", kdfParam.get("algorithm"));
        assertEquals("65536", kdfParam.get("memorycost"));
        assertEquals("3", kdfParam.get("iterations"));
        assertEquals("2", kdfParam.get("parallelism"));
        assertEquals("256", kdfParam.get("keysize"));
        assertTrue(kdfParam.containsKey("salt"));
        Map<String, String> cipherParam = data.getAlgorithmDefaultParam(AlgorithmData.Algorithm.AES256GCM);
        assertEquals("AES-256-GCM", cipherParam.get("algorithm"));
        assertEquals("128", cipherParam.get("taglength"));
        assertTrue(cipherParam.containsKey("iv"));
    }
}
