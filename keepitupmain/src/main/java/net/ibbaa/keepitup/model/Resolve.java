/*
 * Copyright (c) 2025 Alwin Ibba
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

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Resolve {

    private long id;
    private long networktaskid;
    private String address;
    private int port;

    public Resolve() {
        this.id = -1;
        this.networktaskid = -1;
        this.address = null;
        this.port = -1;
    }

    public Resolve(long networktaskid) {
        this.id = -1;
        this.networktaskid = networktaskid;
        this.address = null;
        this.port = -1;
    }

    public Resolve(Resolve other) {
        this();
        this.address = other.getAddress();
        this.port = other.getPort();
    }

    public Resolve(Context context) {
        this();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        this.address = preferenceManager.getPreferenceResolveAddress();
        this.port = preferenceManager.getPreferenceResolvePort();
    }

    public Resolve(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public Resolve(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        this.address = bundle.getString("address");
        this.port = bundle.getInt("port");
    }

    public Resolve(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (NumberUtil.isValidLongValue(map.get("networktaskid"))) {
            this.networktaskid = NumberUtil.getLongValue(map.get("networktaskid"), -1);
        }
        if (map.get("address") != null) {
            this.address = Objects.requireNonNull(map.get("address")).toString();
        }
        if (NumberUtil.isValidIntValue(map.get("port"))) {
            this.port = NumberUtil.getIntValue(map.get("port"), 0);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNetworkTaskId() {
        return networktaskid;
    }

    public void setNetworkTaskId(long networktaskid) {
        this.networktaskid = networktaskid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        if (address != null) {
            bundle.putString("address", address);
        }
        bundle.putInt("port", port);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("networktaskid", networktaskid);
        if (address != null) {
            map.put("address", address);
        }
        map.put("port", port);
        return map;
    }

    public boolean isEmpty() {
        return StringUtil.isEmpty(address) && port < 0;
    }

    public boolean isEqual(Resolve other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return Objects.equals(address, other.address);
    }

    public boolean isTechnicallyEqual(Resolve other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return Objects.equals(address, other.address);
    }

    @NonNull
    @Override
    public String toString() {
        return "Resolve{" +
                "id=" + id +
                ", networktaskid=" + networktaskid +
                ", address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
