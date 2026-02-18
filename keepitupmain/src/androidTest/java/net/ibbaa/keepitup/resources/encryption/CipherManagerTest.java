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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class CipherManagerTest {

    private AlgorithmData algorithmData;
    private CipherManager cipherManager;
    private Map<String, String> argon2Param;
    private Map<String, String> aesParam;

    @Before
    public void beforeEachTestMethod() {
        algorithmData = new AlgorithmData(TestRegistry.getContext());
        cipherManager = new CipherManager(TestRegistry.getContext());
        argon2Param = algorithmData.getAlgorithmDefaultParam(algorithmData.getDefaultKDF());
        aesParam = algorithmData.getAlgorithmDefaultParam(algorithmData.getDefaultCipher());
    }

    @Test
    public void testUnsupportedAlgorithm() {
        argon2Param.put("algorithm", "unsupported");
        CipherManager.EncryptionResult result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Unsupported kdf algorithm: unsupported.", result.message());
        argon2Param = algorithmData.getAlgorithmDefaultParam(algorithmData.getDefaultKDF());
        aesParam.put("algorithm", "unsupported");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Unsupported cipher algorithm: unsupported.", result.message());
    }

    @Test
    public void testArgon2ParameterFailure() {
        argon2Param.clear();
        argon2Param.put("algorithm", algorithmData.getDefaultKDF().getName());
        CipherManager.EncryptionResult result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation with Argon2id failed.", result.message());
        argon2Param.put("memorycost", "65536");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation with Argon2id failed.", result.message());
        argon2Param.put("iterations", "3");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation with Argon2id failed.", result.message());
        argon2Param.put("parallelism", "2");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation with Argon2id failed.", result.message());
    }

    @Test
    public void testAESParameterFailure() {
        aesParam.clear();
        aesParam.put("algorithm", algorithmData.getDefaultCipher().getName());
        CipherManager.EncryptionResult result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Encryption with AES256 failed.", result.message());
        aesParam.put("taglength", "128");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Encryption with AES256 failed.", result.message());
    }

    @Test
    public void testEncrypt() {
        CipherManager.EncryptionResult result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertTrue(result.success());
    }
}
