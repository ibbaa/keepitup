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

import net.ibbaa.keepitup.BuildConfig;
import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.util.CollectionUtil;
import net.ibbaa.keepitup.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class JSONEncryptSetup {

    private final Context context;

    public JSONEncryptSetup(Context context) {
        this.context = context;
    }

    public EncryptionSetupResult encrypt(String password, String plaintext) {
        Log.d(JSONEncryptSetup.class.getName(), "encrypt");
        try {
            String appKey = getResources().getString(R.string.app_json_key);
            String formatKey = getResources().getString(R.string.format_json_key);
            String versionKey = getResources().getString(R.string.version_json_key);
            Map<String, String> globalParams = new TreeMap<>();
            globalParams.put(appKey, BuildConfig.APPLICATION_ID);
            globalParams.put(formatKey, getResources().getString(R.string.format_encrypted));
            globalParams.put(versionKey, String.valueOf(BuildConfig.VERSION_CODE));
            JSONObject root = new JSONObject(globalParams);
            AlgorithmData algorithmData = new AlgorithmData(getContext());
            Map<String, String> kdfParams = algorithmData.getAlgorithmDefaultParam(algorithmData.getDefaultKDF());
            Map<String, String> cipherParams = algorithmData.getAlgorithmDefaultParam(algorithmData.getDefaultCipher());
            String kdfKey = getResources().getString(R.string.kdf_json_key);
            String cipherKey = getResources().getString(R.string.cipher_json_key);
            JSONObject kdf = new JSONObject(kdfParams);
            JSONObject cipher = new JSONObject(cipherParams);
            root.put(kdfKey, kdf);
            root.put(cipherKey, cipher);
            String aad = getAad(globalParams, kdfParams, cipherParams);
            Log.d(JSONEncryptSetup.class.getName(), "aad is " + aad);
            CipherManager cipherManager = new CipherManager(getContext());
            CipherManager.EncryptionResult result = cipherManager.encrypt(kdfParams, cipherParams, password, aad, plaintext);
            if (!result.success()) {
                Log.d(JSONEncryptSetup.class.getName(), "Encryption failed");
                return new EncryptionSetupResult(false, result.message(), result.ciphertext());
            }
            String ciphertextKey = getResources().getString(R.string.ciphertext_json_key);
            root.put(ciphertextKey, result.ciphertext());
            Log.d(JSONEncryptSetup.class.getName(), "Encryption successful");
            return new EncryptionSetupResult(true, result.message(), root.toString());

        } catch (Exception exc) {
            Log.e(JSONEncryptSetup.class.getName(), "Encryption failed", exc);
            String failureMessage = getResources().getString(R.string.aes_encryption_failed);
            return new EncryptionSetupResult(false, failureMessage, "");
        }
    }

    public EncryptionSetupResult decrypt(String password, String cipherWithHeaderJson) {
        Log.d(JSONEncryptSetup.class.getName(), "decrypt");
        try {
            JSONObject root = new JSONObject(cipherWithHeaderJson);
            Map<String, String> kdfParams = extractKDFParams(root);
            Map<String, String> cipherParams = extractCipherParams(root);
            String cipherText = extractCipherText(root);
            Map<String, String> globalParams = JSONUtil.toFlatStringMap(root);
            String aad = getAad(globalParams, kdfParams, cipherParams);
            Log.d(JSONEncryptSetup.class.getName(), "aad is " + aad);
            CipherManager cipherManager = new CipherManager(getContext());
            CipherManager.DecryptionResult result = cipherManager.decrypt(kdfParams, cipherParams, password, aad, cipherText);
            if (!result.success()) {
                Log.d(JSONEncryptSetup.class.getName(), "Decryption failed");
                return new EncryptionSetupResult(false, result.message(), result.plaintext());
            }
            Log.d(JSONEncryptSetup.class.getName(), "Decryption successful");
            return new EncryptionSetupResult(true, result.message(), result.plaintext());
        } catch (Exception exc) {
            Log.e(JSONEncryptSetup.class.getName(), "Decryption failed", exc);
            String failureMessage = getResources().getString(R.string.aes_decryption_failed);
            return new EncryptionSetupResult(false, failureMessage, "");
        }
    }

    public boolean isEncryptedFormat(String data) {
        Log.d(JSONEncryptSetup.class.getName(), "isEncryptedFormat");
        try {
            JSONObject root = new JSONObject(data);
            String formatKey = getResources().getString(R.string.format_json_key);
            String encryptedFormat = getResources().getString(R.string.format_encrypted);
            Object formatValue = root.opt(formatKey);
            return formatValue != null && formatValue.toString().equals(encryptedFormat);
        } catch (Exception exc) {
            Log.e(JSONEncryptSetup.class.getName(), "Encryption check failed", exc);
        }
        return false;
    }

    private String getAad(Map<String, String> globalParams, Map<String, String> kdfParams, Map<String, String> cipherParams) {
        Map<String, String> aadMap = new HashMap<>();
        String globalPrefix = getResources().getString(R.string.global_json_key) + "_";
        String kdfPrefix = getResources().getString(R.string.kdf_json_key) + "_";
        String cipherPrefix = getResources().getString(R.string.cipher_json_key) + "_";
        CollectionUtil.copyMap(globalParams, aadMap, globalPrefix);
        CollectionUtil.copyMap(kdfParams, aadMap, kdfPrefix);
        CollectionUtil.copyMap(cipherParams, aadMap, cipherPrefix);
        return CollectionUtil.mapToStableString(aadMap);
    }

    private Map<String, String> extractKDFParams(JSONObject root) throws JSONException {
        String kdfKey = getResources().getString(R.string.kdf_json_key);
        if (!root.has(kdfKey)) {
            return Collections.emptyMap();
        }
        Object kdfData = root.get(kdfKey);
        if (!(kdfData instanceof JSONObject)) {
            return Collections.emptyMap();
        }
        root.remove(kdfKey);
        return JSONUtil.toFlatStringMap((JSONObject) kdfData);
    }

    private Map<String, String> extractCipherParams(JSONObject root) throws JSONException {
        String cipherKey = getResources().getString(R.string.cipher_json_key);
        if (!root.has(cipherKey)) {
            return Collections.emptyMap();
        }
        Object cipherData = root.get(cipherKey);
        if (!(cipherData instanceof JSONObject)) {
            return Collections.emptyMap();
        }
        root.remove(cipherKey);
        return JSONUtil.toFlatStringMap((JSONObject) cipherData);
    }

    private String extractCipherText(JSONObject root) throws JSONException {
        String cipherTextKey = getResources().getString(R.string.ciphertext_json_key);
        if (!root.has(cipherTextKey)) {
            return "";
        }
        Object cipherText = root.get(cipherTextKey);
        root.remove(cipherTextKey);
        return cipherText.toString();
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
