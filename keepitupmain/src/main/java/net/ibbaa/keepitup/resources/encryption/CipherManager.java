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
import net.ibbaa.keepitup.util.StringUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CipherManager {

    private final Context context;

    public CipherManager(Context context) {
        this.context = context;
    }

    public EncryptionResult encrypt(Map<String, String> kdfAlgorithmParam, Map<String, String> cipherAlgorithmParam, String password, String aad) {
        Log.d(CipherManager.class.getName(), "encrypt for KDF algorithm " + kdfAlgorithmParam.toString() + ", cipher algorithm " + cipherAlgorithmParam + ", password " + StringUtil.maskSecret(password, true) + ", aad " + aad);
        String kdfAlgorithmName = kdfAlgorithmParam.get(getResources().getString(R.string.algorithm_json_key));
        AlgorithmData.Algorithm kdfAlgorithm = AlgorithmData.Algorithm.forName(kdfAlgorithmName);
        if (!AlgorithmData.Algorithm.ARGON2ID.equals(kdfAlgorithm)) {
            String failureMessage = getResources().getString(R.string.unsupported_algorithm_message, "KDF", kdfAlgorithmName);
            return new EncryptionResult(false, failureMessage, "");
        }
        String successMessage = getResources().getString(R.string.encryption_success);
        return new EncryptionResult(true, successMessage, "");
    }

    private byte[] createKeyWithArgon2(Map<String, String> kdfAlgorithm, String password) {
        Log.d(CipherManager.class.getName(),  "createKeyWithArgon2 for KDF algorithm " + kdfAlgorithm.toString());
        String normalizedPassword = StringUtil.normalizeString(password);
        byte[] normalizedPasswordBytes = normalizedPassword.getBytes(StandardCharsets.UTF_8);
        return new byte[0];
    }

    private Resources getResources() {
        return context.getResources();
    }

    public record EncryptionResult(boolean success, String message, String ciphertext) {

    }

    public record DecryptionResult(boolean success, String message, String plaintext) {

    }
}
