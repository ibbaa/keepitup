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

import net.ibbaa.keepitup.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SNMPItem {

    private long id;
    private long networktaskid;
    private SNMPItemType snmpItemType;
    private String name;
    private String oid;
    private boolean monitored;

    public SNMPItem() {
        this.id = -1;
        this.networktaskid = -1;
        this.snmpItemType = null;
        this.name = null;
        this.oid = null;
        this.monitored = false;
    }

    public SNMPItem(SNMPItem other) {
        this();
        this.snmpItemType = other.getSnmpItemType();
        this.name = other.getName();
        this.oid = other.getOid();
        this.monitored = other.isMonitored();
    }

    public SNMPItem(Bundle bundle) {
        this();
        this.id = bundle.getLong("id");
        this.networktaskid = bundle.getLong("networktaskid");
        if (bundle.containsKey("snmpItemType")) {
            snmpItemType = SNMPItemType.forCode(bundle.getInt("snmpItemType"));
        }
        this.name = bundle.getString("name");
        this.oid = bundle.getString("oid");
        this.monitored = bundle.getInt("monitored") >= 1;
    }

    public SNMPItem(Map<String, ?> map) {
        this();
        if (NumberUtil.isValidLongValue(map.get("id"))) {
            this.id = NumberUtil.getLongValue(map.get("id"), -1);
        }
        if (NumberUtil.isValidLongValue(map.get("networktaskid"))) {
            this.networktaskid = NumberUtil.getLongValue(map.get("networktaskid"), -1);
        }
        if (NumberUtil.isValidIntValue(map.get("snmpItemType"))) {
            this.snmpItemType = SNMPItemType.forCode(NumberUtil.getIntValue(map.get("snmpItemType"), -1));
        }
        if (map.get("name") != null) {
            this.name = Objects.requireNonNull(map.get("name")).toString();
        }
        if (map.get("oid") != null) {
            this.oid = Objects.requireNonNull(map.get("oid")).toString();
        }
        if (map.get("monitored") != null) {
            this.monitored = !"false".equalsIgnoreCase(Objects.requireNonNull(map.get("monitored")).toString());
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

    public SNMPItemType getSnmpItemType() {
        return snmpItemType;
    }

    public void setSnmpItemType(SNMPItemType snmpItemType) {
        this.snmpItemType = snmpItemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean monitored) {
        this.monitored = monitored;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putLong("id", id);
        bundle.putLong("networktaskid", networktaskid);
        if (snmpItemType != null) {
            bundle.putInt("snmpItemType", snmpItemType.getCode());
        }
        if (name != null) {
            bundle.putString("name", name);
        }
        if (oid != null) {
            bundle.putString("oid", oid);
        }
        bundle.putInt("monitored", monitored ? 1 : 0);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public Map<String, ?> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("networktaskid", networktaskid);
        if (snmpItemType != null) {
            map.put("snmpItemType", snmpItemType.getCode());
        }
        if (name != null) {
            map.put("name", name);
        }
        if (oid != null) {
            map.put("oid", oid);
        }
        map.put("monitored", monitored);
        return map;
    }

    public boolean isEqual(SNMPItem other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (!Objects.equals(snmpItemType, other.snmpItemType)) {
            return false;
        }
        if (!Objects.equals(name, other.name)) {
            return false;
        }
        if (!Objects.equals(oid, other.oid)) {
            return false;
        }
        return monitored == other.monitored;
    }

    public boolean isTechnicallyEqual(SNMPItem other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (networktaskid != other.networktaskid) {
            return false;
        }
        if (!Objects.equals(snmpItemType, other.snmpItemType)) {
            return false;
        }
        if (!Objects.equals(name, other.name)) {
            return false;
        }
        if (!Objects.equals(oid, other.oid)) {
            return false;
        }
        return monitored == other.monitored;
    }

    @NonNull
    @Override
    public String toString() {
        return "SNMPItem{" +
                "id=" + id +
                ", networktaskid=" + networktaskid +
                ", snmpItemType=" + snmpItemType +
                ", name='" + name + '\'' +
                ", oid='" + oid + '\'' +
                ", monitored=" + monitored +
                '}';
    }
}
