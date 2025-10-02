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
    private String sourceAddress;
    private int sourcePort;
    private String targetAddress;
    private int targetPort;

    public Resolve() {
        this.id = -1;
        this.networktaskid = -1;
        this.sourceAddress = "";
        this.sourcePort = -1;
        this.targetAddress = "";
        this.targetPort = -1;
    }

    public Resolve(long networktaskid) {
        this.id = -1;
        this.networktaskid = networktaskid;
        this.sourceAddress = "";
        this.sourcePort = -1;
        this.targetAddress = "";
        this.targetPort = -1;
    }

    public Resolve(Resolve other) {
        this();
        this.sourceAddress = other.getSourceAddress();
        this.sourcePort = other.getSourcePort();
        this.targetAddress = other.getTargetAddress();
        this.targetPort = other.getTargetPort();
    }

    public Resolve(Context context) {
        this();
        PreferenceManager preferenceManager = new PreferenceManager(context);
        this.targetAddress = preferenceManager.getPreferenceResolveAddress();
        this.targetPort = preferenceManager.getPreferenceResolvePort();
    }

    public Resolve(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public Resolve(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        this.sourceAddress = bundle.getString("sourceAddress");
        this.sourcePort = bundle.getInt("sourcePort");
        this.targetAddress = bundle.getString("targetAddress");
        this.targetPort = bundle.getInt("targetPort");
    }

    public Resolve(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (NumberUtil.isValidLongValue(map.get("networktaskid"))) {
            this.networktaskid = NumberUtil.getLongValue(map.get("networktaskid"), -1);
        }
        if (map.get("sourceAddress") != null) {
            this.sourceAddress = Objects.requireNonNull(map.get("sourceAddress")).toString();
        }
        if (NumberUtil.isValidIntValue(map.get("sourcePort"))) {
            this.sourcePort = NumberUtil.getIntValue(map.get("sourcePort"), 0);
        }
        if (map.get("targetAddress") != null) {
            this.targetAddress = Objects.requireNonNull(map.get("targetAddress")).toString();
        }
        if (NumberUtil.isValidIntValue(map.get("targetPort"))) {
            this.targetPort = NumberUtil.getIntValue(map.get("targetPort"), 0);
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

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetaddress) {
        this.targetAddress = targetaddress;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        if (sourceAddress != null) {
            bundle.putString("sourceAddress", sourceAddress);
        }
        bundle.putInt("sourcePort", sourcePort);
        if (targetAddress != null) {
            bundle.putString("targetAddress", targetAddress);
        }
        bundle.putInt("targetPort", targetPort);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("networktaskid", networktaskid);
        if (sourceAddress != null) {
            map.put("sourceAddress", sourceAddress);
        }
        map.put("sourcePort", sourcePort);
        if (targetAddress != null) {
            map.put("targetAddress", targetAddress);
        }
        map.put("targetPort", targetPort);
        return map;
    }

    public boolean isEmpty() {
        return StringUtil.isEmpty(targetAddress) && targetPort < 0;
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
        if (!Objects.equals(sourceAddress, other.sourceAddress)) {
            return false;
        }
        if (sourcePort != other.sourcePort) {
            return false;
        }
        if (targetPort != other.targetPort) {
            return false;
        }
        return Objects.equals(targetAddress, other.targetAddress);
    }

    public boolean isTechnicallyEqual(Resolve other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (!Objects.equals(sourceAddress, other.sourceAddress)) {
            return false;
        }
        if (sourcePort != other.sourcePort) {
            return false;
        }
        if (targetPort != other.targetPort) {
            return false;
        }
        return Objects.equals(targetAddress, other.targetAddress);
    }

    @NonNull
    @Override
    public String toString() {
        return "Resolve{" +
                "id=" + id +
                ", networktaskid=" + networktaskid +
                ", sourceAddress='" + sourceAddress + '\'' +
                ", sourcePort=" + sourcePort +
                ", targetAddress='" + targetAddress + '\'' +
                ", targetPort=" + targetPort +
                '}';
    }
}
