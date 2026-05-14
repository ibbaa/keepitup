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

import java.util.Objects;

public class SNMPInterfaceInfo {

    private String descr;
    private int type;
    private int status;
    private String alias;

    public SNMPInterfaceInfo() {
        this.descr = null;
        this.type = -1;
        this.status = -1;
        this.alias = null;
    }

    public SNMPInterfaceInfo(SNMPInterfaceInfo other) {
        this.descr = other.getDescr();
        this.type = other.getType();
        this.status = other.getStatus();
        this.alias = other.getAlias();
    }

    public SNMPInterfaceInfo(Bundle bundle) {
        this();
        this.descr = bundle.getString("descr");
        this.type = bundle.getInt("type");
        this.status = bundle.getInt("status");
        this.alias = bundle.getString("alias");
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        if (descr != null) {
            bundle.putString("descr", descr);
        }
        bundle.putInt("type", type);
        bundle.putInt("status", status);
        if (alias != null) {
            bundle.putString("alias", alias);
        }
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public boolean isEqual(SNMPInterfaceInfo other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!Objects.equals(descr, other.descr)) {
            return false;
        }
        if (!Objects.equals(alias, other.alias)) {
            return false;
        }
        if (status != other.status) {
            return false;
        }
        return type == other.type;
    }
}
