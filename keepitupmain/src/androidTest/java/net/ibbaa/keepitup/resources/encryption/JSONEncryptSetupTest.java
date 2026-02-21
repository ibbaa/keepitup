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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class JSONEncryptSetupTest {

    private JSONEncryptSetup encryptSetup;

    @Before
    public void beforeEachTestMethod() {
        encryptSetup = new JSONEncryptSetup(TestRegistry.getContext());
    }

    @Test
    public void testEncryptDecryptSuccessful() {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, encryptResult.data());
        assertTrue(decryptResult.success());
        assertEquals("Decryption successful", decryptResult.message());
        assertEquals(plaintext, decryptResult.data());
    }

    @Test
    public void testEncryptDecryptWrongPassword() {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password + "Wrong", encryptResult.data());
        assertFalse(decryptResult.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", decryptResult.message());
    }

    @Test
    public void testEncryptDecryptJSONMalformed() throws Exception {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, encryptResult.data().replaceFirst("\\{", "}"));
        assertFalse(decryptResult.success());
        assertNotEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", decryptResult.message());
    }

    @Test
    public void testEncryptDecryptCiphertextManipulated() throws Exception {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        JSONObject jsonObject = new JSONObject(encryptResult.data());
        jsonObject.put("ciphertext", "abc");
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, jsonObject.toString());
        assertFalse(decryptResult.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", decryptResult.message());
    }

    @Test
    public void testEncryptDecryptHeaderManipulated() throws Exception {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        JSONObject jsonObject = new JSONObject(encryptResult.data());
        JSONObject kdfObject = (JSONObject) jsonObject.get("kdf");
        kdfObject.put("iterations", "4");
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, jsonObject.toString());
        assertFalse(decryptResult.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", decryptResult.message());
    }

    @Test
    public void testEncryptDecryptKDFHeaderRemoved() throws Exception {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        JSONObject jsonObject = new JSONObject(encryptResult.data());
        JSONObject kdfObject = (JSONObject) jsonObject.get("kdf");
        kdfObject.remove("memorycost");
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, jsonObject.toString());
        assertFalse(decryptResult.success());
        assertEquals("Key derivation with Argon2id failed.", decryptResult.message());
    }

    @Test
    public void testEncryptDecryptCipherHeaderRemoved() throws Exception {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        JSONObject jsonObject = new JSONObject(encryptResult.data());
        JSONObject cipherObject = (JSONObject) jsonObject.get("cipher");
        cipherObject.remove("taglength");
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, jsonObject.toString());
        assertFalse(decryptResult.success());
        assertEquals("Decryption with AES256 failed.", decryptResult.message());
    }

    @Test
    public void testEncryptDecryptHeaderAdded() throws Exception {
        String plaintext = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.";
        String password = "pass123topsecret";
        EncryptionSetupResult encryptResult = encryptSetup.encrypt(password, plaintext);
        assertTrue(encryptResult.success());
        assertEquals("Encryption successful", encryptResult.message());
        JSONObject jsonObject = new JSONObject(encryptResult.data());
        jsonObject.put("another", "header");
        EncryptionSetupResult decryptResult = encryptSetup.decrypt(password, jsonObject.toString());
        assertFalse(decryptResult.success());
        assertEquals("Decryption failed. The password is incorrect or the file has been modified or damaged.", decryptResult.message());
    }
}
