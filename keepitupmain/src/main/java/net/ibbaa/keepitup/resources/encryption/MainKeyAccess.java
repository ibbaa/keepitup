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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.integration.android.AndroidKeysetManager;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.service.TimeBasedSuspensionScheduler;
import net.ibbaa.keepitup.util.StringUtil;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class MainKeyAccess {

    private final static Object LOCK = TimeBasedSuspensionScheduler.class;

    private final static SecureRandom randomGenerator = new SecureRandom();

    private static Aead aead;

    private final Context context;

    public MainKeyAccess(Context context) {
        this.context = context;
    }

    public MainKey getMainKey() {
        Log.d(MainKeyAccess.class.getName(), "getMainKey");
        try {
            initAead(context);
        } catch (Exception exc) {
            Log.e(MainKeyAccess.class.getName(), "Aead access failed", exc);
            return new MainKey(null, false, false, exc.getMessage());
        }
        synchronized (LOCK) {
            SharedPreferences mainKeyPrefs = getMainKeyPreferences();
            String mainKeyPrefsKey = getResources().getString(R.string.main_key_prefs_key);
            byte[] mainKey;
            String encryptedMainKey = mainKeyPrefs.getString(mainKeyPrefsKey, null);
            if (encryptedMainKey != null) {
                mainKey = decryptMainKey(encryptedMainKey);
                if (mainKey != null) {
                    return new MainKey(mainKey, true, false, getResources().getString(R.string.existing_key_valid));
                }
            }
            return generateAndStoreMainKey();
        }
    }

    @SuppressLint("ApplySharedPref")
    private MainKey generateAndStoreMainKey() {
        Log.d(MainKeyAccess.class.getName(), "generateAndStoreMainKey");
        int mainKeySize = getResources().getInteger(R.integer.main_key_size_byte_default);
        String mainKeyPrefsKey = getResources().getString(R.string.main_key_prefs_key);
        byte[] mainKey = new byte[mainKeySize];
        randomGenerator.nextBytes(mainKey);
        try {
            byte[] encryptedMainKeyBytes = getAead().encrypt(mainKey, null);
            SharedPreferences.Editor mainKeyPreferences = getMainKeyPreferences().edit();
            mainKeyPreferences.putString(mainKeyPrefsKey, StringUtil.byteArrayToBase64(encryptedMainKeyBytes));
            mainKeyPreferences.commit();
            return new MainKey(mainKey, true, true, getResources().getString(R.string.created_key_valid));
        } catch (Exception exc) {
            Log.e(MainKeyAccess.class.getName(), "Encrypt failed because of unknown error", exc);
            return new MainKey(null, false, false, exc.getMessage());
        }
    }

    private byte[] decryptMainKey(String encryptedMainKey) {
        Log.d(MainKeyAccess.class.getName(), "decryptMainKey");
        try {
            byte[] encryptedMainKeyBytes = StringUtil.base64ToByteArray(encryptedMainKey);
            return getAead().decrypt(encryptedMainKeyBytes, null);
        } catch (GeneralSecurityException exc) {
            Log.e(MainKeyAccess.class.getName(), "Decrypt failed because of wrong key", exc);
            invalidateMainKey();
            return null;
        } catch (Exception exc) {
            Log.e(MainKeyAccess.class.getName(), "Decrypt failed because of unknown error", exc);
            invalidateMainKey();
            return null;
        }
    }

    @SuppressLint("ApplySharedPref")
    private void invalidateMainKey() {
        Log.d(MainKeyAccess.class.getName(), "invalidateMainKey");
        String mainKeyPrefsKey = getResources().getString(R.string.main_key_prefs_key);
        SharedPreferences.Editor mainKeyPreferences = getMainKeyPreferences().edit();
        mainKeyPreferences.remove(mainKeyPrefsKey);
        mainKeyPreferences.commit();
    }

    private SharedPreferences getMainKeyPreferences() {
        String main_key_prefs_file = getResources().getString(R.string.main_key_prefs_file);
        return context.getSharedPreferences(main_key_prefs_file, Context.MODE_PRIVATE);
    }

    public void resetMainKey() {
        synchronized (LOCK) {
            invalidateMainKey();
            aead = null;
        }
    }

    @SuppressLint("ApplySharedPref")
    public void reset() {
        String tink_keyset_prefs_key = context.getResources().getString(R.string.tink_keyset_prefs_key);
        String keystore_master_key_alias = context.getResources().getString(R.string.keystore_master_key_alias);
        synchronized (LOCK) {
            invalidateMainKey();
            SharedPreferences.Editor mainKeyPreferences = getMainKeyPreferences().edit();
            mainKeyPreferences.remove(tink_keyset_prefs_key);
            mainKeyPreferences.commit();
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                if (keyStore.containsAlias(keystore_master_key_alias)) {
                    keyStore.deleteEntry(keystore_master_key_alias);
                }
            } catch (Exception exc) {
                Log.e(MainKeyAccess.class.getName(), "Exception during keystore reset", exc);
            }
            aead = null;
        }
    }

    private static Aead getAead() {
        synchronized (LOCK) {
            return aead;
        }
    }

    private static void initAead(Context context) throws Exception {
        Log.d(MainKeyAccess.class.getName(), "initAead");
        synchronized (LOCK) {
            if (aead == null) {
                AeadConfig.register();
                String tink_keyset_prefs_key = context.getResources().getString(R.string.tink_keyset_prefs_key);
                String main_key_prefs_file = context.getResources().getString(R.string.main_key_prefs_file);
                String keystore_master_key_alias = context.getResources().getString(R.string.keystore_master_key_alias);
                KeysetHandle keysetHandle = new AndroidKeysetManager.Builder()
                        .withSharedPref(context, tink_keyset_prefs_key, main_key_prefs_file)
                        .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
                        .withMasterKeyUri("android-keystore://" + keystore_master_key_alias)
                        .build()
                        .getKeysetHandle();
                aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead.class);
            }
        }
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }

    public record MainKey(byte[] key, boolean keyValid, boolean keyNew, String message) {

    }
}
