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

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class CipherManager {

    private final Context context;

    public CipherManager(Context context) {
        this.context = context;
    }

    public EncryptionResult encrypt(Map<String, String> kdfAlgorithmParam, Map<String, String> cipherAlgorithmParam, String password, String aad, String plainText) {
        Log.d(CipherManager.class.getName(), "encrypt for kdf algorithm " + kdfAlgorithmParam.toString() + ", cipher algorithm " + cipherAlgorithmParam + ", password " + StringUtil.maskSecret(password, true) + ", aad " + aad);
        CryptoContext cryptoContext = prepareCryptoContext(kdfAlgorithmParam, cipherAlgorithmParam, password);
        if (!cryptoContext.success()) {
            String failureMessage = cryptoContext.message();
            return new EncryptionResult(false, failureMessage, "");
        }
        byte[] key = cryptoContext.key();
        try {
            String cipherText = encryptWithAES256(cipherAlgorithmParam, key, aad, plainText);
            if (cipherText == null) {
                String failureMessage = getResources().getString(R.string.aes_encryption_failed);
                return new EncryptionResult(false, failureMessage, "");
            }
            String successMessage = getResources().getString(R.string.encryption_success);
            return new EncryptionResult(true, successMessage, cipherText);
        } catch (Exception exc) {
            Log.e(CipherManager.class.getName(), "Exception during aes encryption", exc);
            String failureMessage = getResources().getString(R.string.aes_encryption_failed);
            return new EncryptionResult(false, failureMessage, "");
        }
    }

    public DecryptionResult decrypt(Map<String, String> kdfAlgorithmParam, Map<String, String> cipherAlgorithmParam, String password, String aad, String cipherText) {
        Log.d(CipherManager.class.getName(), "decrypt for kdf algorithm " + kdfAlgorithmParam.toString() + ", cipher algorithm " + cipherAlgorithmParam + ", password " + StringUtil.maskSecret(password, true) + ", aad " + aad);
        CryptoContext cryptoContext = prepareCryptoContext(kdfAlgorithmParam, cipherAlgorithmParam, password);
        if (!cryptoContext.success()) {
            String failureMessage = cryptoContext.message();
            return new DecryptionResult(false, failureMessage, "");
        }
        byte[] key = cryptoContext.key();
        try {
            String plainText = decryptWithAES256(cipherAlgorithmParam, key, aad, cipherText);
            if (plainText == null) {
                String failureMessage = getResources().getString(R.string.aes_decryption_failed);
                return new DecryptionResult(false, failureMessage, "");
            }
            String successMessage = getResources().getString(R.string.decryption_success);
            return new DecryptionResult(true, successMessage, plainText);
        } catch (InvalidCipherTextException exc) {
            Log.e(CipherManager.class.getName(), "InvalidCipherTextException during aes encryption", exc);
            String failureMessage = getResources().getString(R.string.aes_decryption_wrong_password_or_file_corrupt);
            return new DecryptionResult(false, failureMessage, "");
        } catch (Exception exc) {
            Log.e(CipherManager.class.getName(), "Exception during aes encryption", exc);
            String failureMessage = getResources().getString(R.string.aes_decryption_failed);
            return new DecryptionResult(false, failureMessage, "");
        }
    }

    private CryptoContext prepareCryptoContext(Map<String, String> kdfAlgorithmParam, Map<String, String> cipherAlgorithmParam, String password) {
        String kdfAlgorithmName = kdfAlgorithmParam.get(getResources().getString(R.string.algorithm_json_key));
        AlgorithmData.Algorithm kdfAlgorithm = AlgorithmData.Algorithm.forName(kdfAlgorithmName);
        if (!AlgorithmData.Algorithm.ARGON2ID.equals(kdfAlgorithm)) {
            Log.e(CipherManager.class.getName(), "Unsupported kdf algorithm: " + kdfAlgorithmName);
            String failureMessage = getResources().getString(R.string.unsupported_algorithm_message, "kdf", kdfAlgorithmName);
            return new CryptoContext(false, failureMessage, null, null);
        }
        byte[] key = createKeyWithArgon2(kdfAlgorithmParam, password);
        if (key == null) {
            String failureMessage = getResources().getString(R.string.argon2_key_derivation_failed);
            return new CryptoContext(false, failureMessage, null, null);
        }
        String cipherAlgorithmName = cipherAlgorithmParam.get(getResources().getString(R.string.algorithm_json_key));
        AlgorithmData.Algorithm cipherAlgorithm = AlgorithmData.Algorithm.forName(cipherAlgorithmName);
        if (!AlgorithmData.Algorithm.AES256GCM.equals(cipherAlgorithm)) {
            Log.e(CipherManager.class.getName(), "Unsupported cipher algorithm: " + cipherAlgorithmName);
            String failureMessage = getResources().getString(R.string.unsupported_algorithm_message, "cipher", cipherAlgorithmName);
            return new CryptoContext(false, failureMessage, null, null);
        }
        return new CryptoContext(true, "Success", key, cipherAlgorithm);
    }

    private byte[] createKeyWithArgon2(Map<String, String> kdfAlgorithm, String password) {
        Log.d(CipherManager.class.getName(), "createKeyWithArgon2 for kdf algorithm " + kdfAlgorithm.toString());
        String normalizedPassword = StringUtil.normalizeString(password);
        byte[] normalizedPasswordBytes = normalizedPassword.getBytes(StandardCharsets.UTF_8);
        int memoryCost = NumberUtil.getIntValue(kdfAlgorithm.get(getResources().getString(R.string.memorycost_json_key)), -1);
        int iterations = NumberUtil.getIntValue(kdfAlgorithm.get(getResources().getString(R.string.iterations_json_key)), -1);
        int parallelism = NumberUtil.getIntValue(kdfAlgorithm.get(getResources().getString(R.string.parallelism_json_key)), -1);
        int keySize = NumberUtil.getIntValue(kdfAlgorithm.get(getResources().getString(R.string.key_size_json_key)), -1);
        String salt = kdfAlgorithm.get(getResources().getString(R.string.salt_json_key));
        Log.d(CipherManager.class.getName(), "memoryCost: " + memoryCost);
        Log.d(CipherManager.class.getName(), "iterations: " + iterations);
        Log.d(CipherManager.class.getName(), "parallelism: " + parallelism);
        Log.d(CipherManager.class.getName(), "keySize: " + keySize);
        Log.d(CipherManager.class.getName(), "salt: " + salt);
        if (memoryCost < 0 || iterations < 0 || parallelism < 0 || keySize < 0 || StringUtil.isEmpty(salt)) {
            Log.e(CipherManager.class.getName(), "createKeyWithArgon2 invalid argon2id parameter. Cannot proceed.");
            return null;
        }
        byte[] saltBytes = StringUtil.base64ToByteArray(salt);
        Log.d(CipherManager.class.getName(), "saltBytes length: " + Objects.requireNonNull(saltBytes).length);
        Argon2Parameters argon2Params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id).withSalt(saltBytes).withParallelism(parallelism).withMemoryAsKB(memoryCost).withIterations(iterations).build();
        Argon2BytesGenerator argon2Generator = new Argon2BytesGenerator();
        argon2Generator.init(argon2Params);
        byte[] key = new byte[keySize / 8];
        argon2Generator.generateBytes(normalizedPasswordBytes, key);
        return key;
    }

    @SuppressWarnings("deprecation")
    private String encryptWithAES256(Map<String, String> cipherAlgorithm, byte[] key, String aad, String plainText) throws Exception {
        Log.d(CipherManager.class.getName(), "encryptWithAES256 for cipher algorithm " + cipherAlgorithm.toString() + ", aad is " + aad);
        Log.d(CipherManager.class.getName(), "key length: " + Objects.requireNonNull(key).length);
        int tagLength = NumberUtil.getIntValue(cipherAlgorithm.get(getResources().getString(R.string.taglength_json_key)), -1);
        String iv = cipherAlgorithm.get(getResources().getString(R.string.iv_json_key));
        if (tagLength < 0 || StringUtil.isEmpty(iv)) {
            Log.e(CipherManager.class.getName(), "encryptWithAES256 invalid aes256 parameter. Cannot proceed.");
            return null;
        }
        byte[] ivBytes = StringUtil.base64ToByteArray(iv);
        byte[] aadBytes = StringUtil.notNull(aad).getBytes(StandardCharsets.UTF_8);
        byte[] plainTextBytes = StringUtil.notNull(plainText).getBytes(StandardCharsets.UTF_8);
        Log.d(CipherManager.class.getName(), "ivBytes length: " + Objects.requireNonNull(ivBytes).length);
        Log.d(CipherManager.class.getName(), "aadBytes length: " + Objects.requireNonNull(aadBytes).length);
        Log.d(CipherManager.class.getName(), "plainTextBytes length: " + Objects.requireNonNull(plainTextBytes).length);
        GCMBlockCipher gcmCipher = new GCMBlockCipher(new AESEngine());
        AEADParameters aeadParams = new AEADParameters(new KeyParameter(key), tagLength, ivBytes, aadBytes);
        gcmCipher.init(true, aeadParams);
        byte[] ciphertextBytes = new byte[gcmCipher.getOutputSize(plainTextBytes.length)];
        Log.d(CipherManager.class.getName(), "ciphertextBytes length: " + Objects.requireNonNull(ciphertextBytes).length);
        int offset = gcmCipher.processBytes(plainTextBytes, 0, plainTextBytes.length, ciphertextBytes, 0);
        gcmCipher.doFinal(ciphertextBytes, offset);
        return StringUtil.byteArrayToBase64(ciphertextBytes);
    }

    @SuppressWarnings("deprecation")
    private String decryptWithAES256(Map<String, String> cipherAlgorithm, byte[] key, String aad, String cipherText) throws Exception {
        Log.d(CipherManager.class.getName(), "decryptWithAES256 for cipher algorithm " + cipherAlgorithm.toString() + ", aad is " + aad);
        Log.d(CipherManager.class.getName(), "key length: " + Objects.requireNonNull(key).length);
        int tagLength = NumberUtil.getIntValue(cipherAlgorithm.get(getResources().getString(R.string.taglength_json_key)), -1);
        String iv = cipherAlgorithm.get(getResources().getString(R.string.iv_json_key));
        if (tagLength < 0 || StringUtil.isEmpty(iv)) {
            Log.e(CipherManager.class.getName(), "decryptWithAES256 invalid aes256 parameter. Cannot proceed.");
            return null;
        }
        byte[] ivBytes = StringUtil.base64ToByteArray(iv);
        byte[] aadBytes = StringUtil.notNull(aad).getBytes(StandardCharsets.UTF_8);
        byte[] cipherTextBytes = StringUtil.base64ToByteArray(cipherText);
        Log.d(CipherManager.class.getName(), "ivBytes length: " + Objects.requireNonNull(ivBytes).length);
        Log.d(CipherManager.class.getName(), "aadBytes length: " + Objects.requireNonNull(aadBytes).length);
        Log.d(CipherManager.class.getName(), "cipherTextBytes length: " + Objects.requireNonNull(cipherTextBytes).length);
        GCMBlockCipher gcmCipher = new GCMBlockCipher(new AESEngine());
        AEADParameters aeadParams = new AEADParameters(new KeyParameter(key), tagLength, ivBytes, aadBytes);
        gcmCipher.init(false, aeadParams);
        byte[] plainTextBytes = new byte[gcmCipher.getOutputSize(Objects.requireNonNull(cipherTextBytes).length)];
        Log.d(CipherManager.class.getName(), "plainTextBytes length: " + Objects.requireNonNull(plainTextBytes).length);
        int offset = gcmCipher.processBytes(cipherTextBytes, 0, cipherTextBytes.length, plainTextBytes, 0);
        gcmCipher.doFinal(plainTextBytes, offset);
        return new String(plainTextBytes, StandardCharsets.UTF_8);
    }

    private Resources getResources() {
        return context.getResources();
    }

    public record EncryptionResult(boolean success, String message, String ciphertext) {

    }

    public record DecryptionResult(boolean success, String message, String plaintext) {

    }

    private record CryptoContext(boolean success, String message, byte[] key, AlgorithmData.Algorithm cipherAlgorithm) {

    }
}
