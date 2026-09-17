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

public class SNMPAuthInfo {

    private String community;
    private SNMPAuthAlgorithm authAlgorithm;
    private String userName;
    private String authPassphrase;
    private SNMPPrivAlgorithm privAlgorithm;
    private String privPassphrase;

    public SNMPAuthInfo() {
        this.community = null;
        this.authAlgorithm = null;
        this.userName = null;
        this.authPassphrase = null;
        this.privAlgorithm = null;
        this.privPassphrase = null;
    }

    @SuppressWarnings("unused")
    public SNMPAuthInfo(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public SNMPAuthInfo(Bundle bundle) {
        this();
        this.community = bundle.getString("community");
        if (bundle.containsKey("authAlgorithm")) {
            this.authAlgorithm = SNMPAuthAlgorithm.forCode(bundle.getInt("authAlgorithm"));
        }
        this.userName = bundle.getString("userName");
        this.authPassphrase = bundle.getString("authPassphrase");
        if (bundle.containsKey("privAlgorithm")) {
            this.privAlgorithm = SNMPPrivAlgorithm.forCode(bundle.getInt("privAlgorithm"));
        }
        this.privPassphrase = bundle.getString("privPassphrase");
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public SNMPAuthAlgorithm getAuthAlgorithm() {
        return authAlgorithm;
    }

    public void setAuthAlgorithm(SNMPAuthAlgorithm authAlgorithm) {
        this.authAlgorithm = authAlgorithm;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthPassphrase() {
        return authPassphrase;
    }

    public void setAuthPassphrase(String authPassphrase) {
        this.authPassphrase = authPassphrase;
    }

    public SNMPPrivAlgorithm getPrivAlgorithm() {
        return privAlgorithm;
    }

    public void setPrivAlgorithm(SNMPPrivAlgorithm privAlgorithm) {
        this.privAlgorithm = privAlgorithm;
    }

    public String getPrivPassphrase() {
        return privPassphrase;
    }

    public void setPrivPassphrase(String privPassphrase) {
        this.privPassphrase = privPassphrase;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        if (community != null) {
            bundle.putString("community", community);
        }
        if (authAlgorithm != null) {
            bundle.putInt("authAlgorithm", authAlgorithm.getCode());
        }
        if (userName != null) {
            bundle.putString("userName", userName);
        }
        if (authPassphrase != null) {
            bundle.putString("authPassphrase", authPassphrase);
        }
        if (privAlgorithm != null) {
            bundle.putInt("privAlgorithm", privAlgorithm.getCode());
        }
        if (privPassphrase != null) {
            bundle.putString("privPassphrase", privPassphrase);
        }
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public boolean isEqual(SNMPAuthInfo other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!Objects.equals(community, other.community)) {
            return false;
        }
        if (!Objects.equals(authAlgorithm, other.authAlgorithm)) {
            return false;
        }
        if (!Objects.equals(userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(authPassphrase, other.authPassphrase)) {
            return false;
        }
        if (!Objects.equals(privAlgorithm, other.privAlgorithm)) {
            return false;
        }
        return Objects.equals(privPassphrase, other.privPassphrase);
    }

    @NonNull
    @Override
    public String toString() {
        return "SNMPAuthInfo{" +
                "community='" + StringUtil.maskSecret(community, true) + '\'' +
                ", authAlgorithm=" + authAlgorithm +
                ", userName='" + userName + '\'' +
                ", authPassphrase='" + StringUtil.maskSecret(authPassphrase, true) + '\'' +
                ", privAlgorithm=" + privAlgorithm +
                ", privPassphrase='" + StringUtil.maskSecret(privPassphrase, true) + '\'' +
                '}';
    }
}
