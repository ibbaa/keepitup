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

import java.security.SecureRandom;
import java.util.Map;
import java.util.TreeMap;

public class AlgorithmData {

    private final static SecureRandom randomGenerator = new SecureRandom();

    private final Context context;

    public enum Algorithm {
        ARGON2ID("Argon2id"),
        AES256GCM("AES-256-GCM", "AES/GCM/NoPadding");

        private final String name;
        private final String cipherTransformation;

        Algorithm(String name) {
            this(name, name);
        }

        Algorithm(String name, String cipherTransformation) {
            this.name = name;
            this.cipherTransformation = cipherTransformation;
        }

        public String getName() {
            return name;
        }

        public String getCipherTransformation() {
            return cipherTransformation;
        }

        public static Algorithm forName(String name) {
            Algorithm[] algorithms = Algorithm.values();
            for (Algorithm algorithm : algorithms) {
                if (algorithm.getName().equals(name)) {
                    return algorithm;
                }
            }
            return null;
        }
    }

    public AlgorithmData(Context context) {
        this.context = context;
    }

    public Algorithm getDefaultKDF() {
        return Algorithm.ARGON2ID;
    }

    public Algorithm getDefaultCipher() {
        return Algorithm.AES256GCM;
    }

    public Map<String, String> getAlgorithmDefaultParam(Algorithm algorithm) {
        Log.d(AlgorithmData.class.getName(), "getAlgorithmDefaultParam for algorithm " + algorithm);
        Map<String, String> param = new TreeMap<>();
        if (Algorithm.ARGON2ID.equals(algorithm)) {
            param.put(getResources().getString(R.string.algorithm_json_key), Algorithm.ARGON2ID.getName());
            param.put(getResources().getString(R.string.memorycost_json_key), String.valueOf(getResources().getInteger(R.integer.argon2_memory_cost_default)));
            param.put(getResources().getString(R.string.iterations_json_key), String.valueOf(getResources().getInteger(R.integer.argon2_iterations_default)));
            param.put(getResources().getString(R.string.parallelism_json_key), String.valueOf(getResources().getInteger(R.integer.argon2_parallelism_default)));
            param.put(getResources().getString(R.string.key_size_json_key), String.valueOf(getResources().getInteger(R.integer.argon2_key_size_bit_default)));
            param.put(getResources().getString(R.string.salt_json_key), createArgon2Salt());
        } else if (Algorithm.AES256GCM.equals(algorithm)) {
            param.put(getResources().getString(R.string.algorithm_json_key), Algorithm.AES256GCM.getName());
            param.put(getResources().getString(R.string.taglength_json_key), String.valueOf(getResources().getInteger(R.integer.aes_tag_length_default)));
            param.put(getResources().getString(R.string.iv_json_key), createIV());
        }
        return param;
    }

    private String createArgon2Salt() {
        byte[] salt = new byte[16];
        randomGenerator.nextBytes(salt);
        return StringUtil.toBase64(salt);
    }

    private String createIV() {
        byte[] salt = new byte[12];
        randomGenerator.nextBytes(salt);
        return StringUtil.toBase64(salt);
    }

    private Resources getResources() {
        return context.getResources();
    }
}
