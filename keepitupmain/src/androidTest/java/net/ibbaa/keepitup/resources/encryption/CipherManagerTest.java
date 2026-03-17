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
import net.ibbaa.keepitup.util.StringUtil;

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
        assertEquals("Key derivation failed.", result.message());
        argon2Param.put("memorycost", "65536");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation failed.", result.message());
        argon2Param.put("iterations", "3");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation failed.", result.message());
        argon2Param.put("parallelism", "2");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Key derivation failed.", result.message());
    }

    @Test
    public void testAESParameterFailure() {
        aesParam.clear();
        aesParam.put("algorithm", algorithmData.getDefaultCipher().getName());
        CipherManager.EncryptionResult result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Encryption failed.", result.message());
        aesParam.put("taglength", "128");
        result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertFalse(result.success());
        assertEquals("Encryption failed.", result.message());
    }

    @Test
    public void testEncrypt() {
        argon2Param.put("salt", createTestArgon2Salt());
        aesParam.put("iv", createTestAESIV());
        CipherManager.EncryptionResult result = cipherManager.encrypt(argon2Param, aesParam, "password", "aad", "plaintext");
        assertTrue(result.success());
        assertEquals("Encryption successful", result.message());
        assertEquals("Vgc+SaNGsEwvRGKmbjEsHWN9YKHfPMHpgEwwNt6uyyruN7gzS4s0Cy0EqdUi", result.ciphertext());
    }

    @Test
    public void testEncryptWithKey() {
        aesParam.put("iv", createTestAESIV());
        CipherManager.EncryptionResult result = cipherManager.encrypt(aesParam, createTestAESKey(), "aad", "plaintext");
        assertTrue(result.success());
        assertEquals("Encryption successful", result.message());
        assertEquals("plxXGAkj7t2x93p37hFvSetECXdo1k7LQeD18lValWBNV3cLqQWshRpPclFo", result.ciphertext());
    }

    @Test
    public void testDecrypt() {
        argon2Param.put("salt", createTestArgon2Salt());
        aesParam.put("iv", createTestAESIV());
        CipherManager.DecryptionResult result = cipherManager.decrypt(argon2Param, aesParam, "password", "aad", "Vgc+SaNGsEwvRGKmbjEsHWN9YKHfPMHpgEwwNt6uyyruN7gzS4s0Cy0EqdUi");
        assertTrue(result.success());
        assertEquals("Decryption successful", result.message());
        assertEquals("plaintext", result.plaintext());
    }

    @Test
    public void testDecryptWithKey() {
        aesParam.put("iv", createTestAESIV());
        CipherManager.DecryptionResult result = cipherManager.decrypt(aesParam, createTestAESKey(), "aad", "plxXGAkj7t2x93p37hFvSetECXdo1k7LQeD18lValWBNV3cLqQWshRpPclFo");
        assertTrue(result.success());
        assertEquals("Decryption successful", result.message());
        assertEquals("plaintext", result.plaintext());
    }

    @Test
    public void testDecryptWrongPassword() {
        argon2Param.put("salt", createTestArgon2Salt());
        aesParam.put("iv", createTestAESIV());
        CipherManager.DecryptionResult result = cipherManager.decrypt(argon2Param, aesParam, "wrong", "aad", "OeBXIM0y1TRbw4BQGNxa2W5F6dpRHNa9Hw==");
        assertFalse(result.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", result.message());
    }

    @Test
    public void testDecryptWrongKey() {
        byte[] wrongKey = createTestAESKey();
        wrongKey[1] = 1;
        aesParam.put("iv", createTestAESIV());
        CipherManager.DecryptionResult result = cipherManager.decrypt(aesParam, wrongKey, "aad", "plxXGAkj7t2x93p37hFvSetECXdo1k7LQeD18lValWBNV3cLqQWshRpPclFo");
        assertFalse(result.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", result.message());
    }

    @Test
    public void testDecryptWrongAad() {
        argon2Param.put("salt", createTestArgon2Salt());
        aesParam.put("iv", createTestAESIV());
        CipherManager.DecryptionResult result = cipherManager.decrypt(argon2Param, aesParam, "password", "wrong", "OeBXIM0y1TRbw4BQGNxa2W5F6dpRHNa9Hw==");
        assertFalse(result.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", result.message());
    }

    @Test
    public void testEncryptDecrypt() {
        String plainText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        CipherManager.EncryptionResult encryptResult = cipherManager.encrypt(argon2Param, aesParam, "test123testpassword", "xyz=1;abc=3", plainText);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        CipherManager.DecryptionResult decryptResult = cipherManager.decrypt(argon2Param, aesParam, "test123testpassword", "xyz=1;abc=3", encryptResult.ciphertext());
        assertTrue(decryptResult.success());
        assertEquals("Decryption successful", decryptResult.message());
        assertEquals(plainText, decryptResult.plaintext());
    }

    @Test
    public void testEncryptDecryptWithoutCompress() {
        String plainText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        CipherManager.EncryptionResult encryptResult = cipherManager.encrypt(argon2Param, aesParam, "test123testpassword", "xyz=1;abc=3", plainText, false);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        CipherManager.DecryptionResult decryptResult = cipherManager.decrypt(argon2Param, aesParam, "test123testpassword", "xyz=1;abc=3", encryptResult.ciphertext(), false);
        assertTrue(decryptResult.success());
        assertEquals("Decryption successful", decryptResult.message());
        assertEquals(plainText, decryptResult.plaintext());
    }

    @Test
    public void testEncryptDecryptWithKey() {
        String plainText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        CipherManager.EncryptionResult encryptResult = cipherManager.encrypt(aesParam, createTestAESKey(), "xyz=1;abc=3", plainText);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        CipherManager.DecryptionResult decryptResult = cipherManager.decrypt(aesParam, createTestAESKey(), "xyz=1;abc=3", encryptResult.ciphertext());
        assertTrue(decryptResult.success());
        assertEquals("Decryption successful", decryptResult.message());
        assertEquals(plainText, decryptResult.plaintext());
    }

    @Test
    public void testEncryptDecryptWithKeyWithoutCompress() {
        String plainText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        CipherManager.EncryptionResult encryptResult = cipherManager.encrypt(aesParam, createTestAESKey(), "xyz=1;abc=3", plainText, false);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        CipherManager.DecryptionResult decryptResult = cipherManager.decrypt(aesParam, createTestAESKey(), "xyz=1;abc=3", encryptResult.ciphertext(), false);
        assertTrue(decryptResult.success());
        assertEquals("Decryption successful", decryptResult.message());
        assertEquals(plainText, decryptResult.plaintext());
    }

    @Test
    public void testEncryptDecryptFailure() {
        String plainText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
        CipherManager.EncryptionResult encryptResult = cipherManager.encrypt(argon2Param, aesParam, "test123testpassword", "xyz=1;abc=3", plainText);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        CipherManager.DecryptionResult decryptResult = cipherManager.decrypt(argon2Param, aesParam, "xyz", "123", encryptResult.ciphertext());
        assertFalse(decryptResult.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", decryptResult.message());
    }

    private String createTestArgon2Salt() {
        byte[] salt = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        return StringUtil.byteArrayToBase64(salt);
    }

    private String createTestAESIV() {
        byte[] salt = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        return StringUtil.byteArrayToBase64(salt);
    }

    private byte[] createTestAESKey() {
        return new byte[]{
                (byte) 0x60, (byte) 0x3d, (byte) 0xeb, (byte) 0x10,
                (byte) 0x15, (byte) 0xca, (byte) 0x71, (byte) 0xbe,
                (byte) 0x2b, (byte) 0x73, (byte) 0xae, (byte) 0xf0,
                (byte) 0x85, (byte) 0x7d, (byte) 0x77, (byte) 0x81,
                (byte) 0x1f, (byte) 0x35, (byte) 0x2c, (byte) 0x07,
                (byte) 0x3b, (byte) 0x61, (byte) 0x08, (byte) 0xd7,
                (byte) 0x2d, (byte) 0x98, (byte) 0x10, (byte) 0xa3,
                (byte) 0x09, (byte) 0x14, (byte) 0xdf, (byte) 0xf4
        };
    }
}
