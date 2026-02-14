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

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.util.StringUtil;

import java.util.Objects;

public class EncryptionInfo {

    private boolean encrypt;
    private String password;

    public EncryptionInfo() {
        this.encrypt = false;
        this.password = null;
    }

    public EncryptionInfo(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public EncryptionInfo(Bundle bundle) {
        this();
        this.encrypt = bundle.getInt("encrypt") >= 1;
        this.password = bundle.getString("password");
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("encrypt", encrypt ? 1 : 0);
        bundle.putString("password", password);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public boolean isEqual(EncryptionInfo other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (encrypt != other.encrypt) {
            return false;
        }
        return Objects.equals(password, other.password);
    }

    @NonNull
    @Override
    public String toString() {
        return "EncryptionInfo{" +
                "encrypt=" + encrypt +
                ", password='" + StringUtil.maskSecret(password, true) + '\'' +
                '}';
    }
}
